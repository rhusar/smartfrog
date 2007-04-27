/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

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
class InterruptHandlerImpl implements SignalHandler,InterruptHandler {

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
     * @param log log to log messages to
     */
    public void bind(String name, LogSF log) {
        this.log=log;
        try {
            oldHandler=Signal.handle(new Signal(name), this);
        } catch (IllegalArgumentException e) {
            //this happens when binding fails. In this situation, warn, but keep going
            this.log.err("Failed to set control-C handler -is JVM running with -Xrs set?",e);
        }
    }
}
