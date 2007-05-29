package org.smartfrog.services.xunit.timeouts;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * Thi
 */
public class TimeoutCompoundImpl extends CompoundImpl implements TimeoutCompound{

    private boolean failOnTimeout;

    //message to get logged at info level
    private String timeoutMessage ;
    //timeout in seconds. If <=0 the timeout is disabled
    private int timeout;

    private WatchDogThread watchdog;


    public TimeoutCompoundImpl() throws RemoteException {
    }


    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure deploying compound or
     *                                  sub-component
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failed to start compound
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        timeoutMessage=sfResolve(ATTR_TIMEOUT_MESSAGE,timeoutMessage,true);
        timeout=sfResolve(ATTR_TIMEOUT,timeout, true);
        failOnTimeout = sfResolve(ATTR_FAIL_ON_TIMEOUT, failOnTimeout, true);
        sfLog().debug("Starting new TimeoutCompound with timeout="+timeout
            +" and message="+timeoutMessage);
        watchdog=new WatchDogThread(timeout);
        watchdog.start();
    }

    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if(watchdog!=null) {
            watchdog.stopWatching();
            watchdog=null;
        }
        super.sfTerminateWith(status);
    }


    /**
     * callback from the watchdog timer to tell me we are taking too long.
     */
    synchronized void initiateTimeoutProcess() {
        //forget about the (self-terminating) watchdog
        //and catch any cleanup states
        synchronized(this) {
            if(watchdog==null) {
                return;
            }
            watchdog = null;
        }

        TerminationRecord terminationRecord = TerminationRecord.abnormal(
                timeoutMessage,
                null);

        int terminated=0;
        for (Enumeration e = sfChildren();
             e.hasMoreElements();) {
            try {
                Prim child = (Prim) e.nextElement();
                if(!child.sfIsTerminated() && !child.sfIsTerminating()) {
                    terminated++;
                    child.sfDetachAndTerminate(terminationRecord);
                }
            } catch (RemoteException rex) {
                sfLog().ignore("When terminating a child",rex);
            }
        }
        //if we were to fail on a timeout, then terminate
        if(terminated>0 && failOnTimeout) {
            sfTerminate(terminationRecord);
        }
    }

    /**
     * Watchdog thread
     */
    private class WatchDogThread extends Thread {

        //timeout in seconds. If <=0 the timeout is disabled
        private int timeout;


        /**
         * {@inheritDoc}
         *
         */
        public WatchDogThread(int timeout) {
            this.timeout = timeout;
        }

        public void stopWatching() {
            this.interrupt();
        }


        /**
         * {@inheritDoc}
         *
         */
        public void run() {
            if(timeout<=0) {
                return;
            }
            int sleepTime = timeout * 1000;

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                return;
            }

            //if we get here, we slept for the timeout time.
            initiateTimeoutProcess();
        }


    }
}


