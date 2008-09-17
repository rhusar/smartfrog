package org.smartfrog.services.xunit.timeouts;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;

/**
 * This component is a compound that kills its children after a defined time in
 * seconds if they have not already terminated of their own accord.
 *
 * It lets us add timeouts around a test suite or other component, without
 * adding lots of timeout logic to the component itself.
 *
 * Warning: Java does not really like threads being killed. The safest way to
 * work with this is to run the child components in their own processes.
 <pre>
 TimeoutCompoundSchema extends Schema {
    failOnTimeout extends Boolean;
     //message to get logged at info level
     timeoutMessage extends String;
     //timeout in seconds. If <=0 the timeout is disabled
     timeout extends Integer;
    }
 </pre>
 */

public class TimeoutCompoundImpl extends CompoundImpl implements TimeoutCompound {

    private boolean failOnTimeout;

    //message to get logged at info level
    private String timeoutMessage ;
    //timeout in milliseconds. If <=0 the timeout is disabled
    private int timeout;

    private WatchDogThread watchdog;


    public TimeoutCompoundImpl() throws RemoteException {
    }


    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/network error
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
        for (Prim child:sfChildList()) {
            try {
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
    private class WatchDogThread extends SmartFrogThread {

        //timeout in seconds. If <=0 the timeout is disabled
        private int timeoutMillis;


        /**
         * {@inheritDoc}
         *
         */
        public WatchDogThread(int timeout) {
            timeoutMillis = timeout;
        }

        /**
         * Stop the thread watching by interrupting its sleep.
         */
        public void stopWatching() {
            interrupt();
        }


        /**
         * {@inheritDoc}
         *
         */
        public void execute() throws Throwable {
            if(timeoutMillis <=0) {
                return;
            }
            int sleepTime = timeoutMillis;

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                //we were asked to stop watching; skip the timeout
                return;
            }

            //if we get here, we slept for the timeout time.
            initiateTimeoutProcess();
        }


    }
}


