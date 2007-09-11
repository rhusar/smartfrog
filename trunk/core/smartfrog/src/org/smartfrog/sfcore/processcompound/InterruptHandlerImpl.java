/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.sfcore.processcompound;

import sun.misc.SignalHandler;
import sun.misc.Signal;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.logging.LogSF;

import java.rmi.RemoteException;

/**
 * signal handler for control-C events
 * This is not portable to Non-Sun runtimes (like gcj), because it extends
 * a sun.misc method.
 */
public class InterruptHandlerImpl implements SignalHandler,InterruptHandler {

    /**
     * The old handler. Nothing is done with this this
     */
    private SignalHandler oldHandler;
    protected LogSF log;

    public InterruptHandlerImpl() {
    }

    /**
     * Handle a signal from the runtime.
     * <ol>
     * <li>if we are not already terminated, we begin a clean shutdown of the program.
     * <li>
     * <li>If we receive another signal, do an exit without terminating components, returning
     *  {@link ExitCodes#EXIT_ERROR_CODE_CTRL_ALT_DEL} as the error code.
     * @param signal received signal.
     */
    public void handle(Signal signal) {
        if (!SFProcess.markProcessCompoundTerminated()) {
            if (SFProcess.processCompound != null) {
                try {
                    log.out("Terminating sfDaemon gracefully!!");
                    SFProcess.processCompound.sfTerminate(new TerminationRecord(TerminationRecord.NORMAL,
                            "sfDaemon forced to terminate ",
                            SFProcess.processCompound.sfCompleteName()));
                } catch (RemoteException re) {
                    //log and ignore
                    if (log.isIgnoreEnabled()) {
                        log.ignore(re);
                    }

                } catch (Throwable thr) {
                    if (log.isIgnoreEnabled()) {
                        log.ignore(thr);
                    }
                }
            }
        } else {
            log.out("sfDaemon killed!");
            //http://www.tldp.org/LDP/abs/html/exitcodes.html
            // 130 - Control-C is fatal error signal 2, (130 = 128 + 2)
            ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_CTRL_ALT_DEL);
        }
    }

    /**
     * bind to a signal. On HP-UX+cruise control this fails with an error,
     * one we dont see on the command line.
     * This handler catches the exception and logs it, so that smartfrog
     * keeps running even if graceful shutdown is broken.
     * @param name name of interrupt to bind to.
     * @param logger log to log messages to
     */
    public void bind(String name, LogSF logger) {
        this.log=logger;
        try {
            oldHandler=Signal.handle(new Signal(name), this);
        } catch (IllegalArgumentException e) {
            //this happens when binding fails. In this situation, warn, but keep going
            this.log.err("Failed to set control-C handler -is JVM running with -Xrs set?",e);
        }
    }
}
