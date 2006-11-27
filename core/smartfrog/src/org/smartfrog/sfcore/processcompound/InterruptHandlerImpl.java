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

    private SignalHandler oldHandler;
    protected LogSF log;

    public InterruptHandlerImpl() {
    }

    public void handle(Signal sig) {
        if (!SFProcess.markProcessCompoundTerminated()) {
            if (SFProcess.processCompound != null) {
                try {
                    //Logger.log("Terminating sfDaemon gracefully!!");
                    log.out("Terminating sfDaemon gracefully!!");
                    SFProcess.processCompound.sfTerminate(new TerminationRecord(TerminationRecord.NORMAL,
                            "sfDaemon forced to terminate ",
                            SFProcess.processCompound.sfCompleteName()));
                } catch (RemoteException re) {
                    //Logger.log(re);
                    //log and ignore
                    if (log.isIgnoreEnabled()) {
                        log.ignore(re);
                    }

                } catch (Throwable thr) {
                    //Logger.log(thr);
                    if (log.isIgnoreEnabled()) {
                        log.ignore(thr);
                    }
                }
            }
        } else {
            //Logger.log("sfDaemon killed!");
            log.out("sfDaemon killed!");
            //http://www.tldp.org/LDP/abs/html/exitcodes.html
            // 130 - Control-C is fatal error signal 2, (130 = 128 + 2)
            ExitCodes.exitWithError(ExitCodes.EXIT_ERROR_CODE_CRTL_ALT_DEL);
        }
    }

    /**
     * bind to a signal. On HP-UX+cruise control this fails with an error,
     * one we dont see on the command line.
     * This handler catches the exception and logs it, so that smartfrog
     * keeps running even if graceful shutdown is broken.
     * @param name name of interrupt to bind to.
     * @param log
     */
    public void bind(String name, LogSF log) {
        this.log=log;
        try {
            oldHandler=Signal.handle(new Signal(name), this);
        } catch (IllegalArgumentException e) {
            //this happens when binding fails. In this situation, warn, but keep going
            this.log.err("Failed to set control-C handler -is JVM running with -Xrs set?",e);
//                Logger.log("Failed to set control-C handler -is JVM running with -Xrs set?");
//                Logger.log(e);
        }
    }
}
