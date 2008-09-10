/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.www;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;

/**

 */
public class WaitForPageImpl extends LivenessPageComponent
        implements WaitForPage, Runnable {

    private int timeout = 0;

    /**
     * thread to do the work
     */
    private SmartFrogThread worker;

    public static final String ERROR_WAIT_FOR_TIMEOUT = "Timeout waiting for the web page: \n";

    public WaitForPageImpl() throws RemoteException {
    }

    public SmartFrogThread getWorker() {
        return worker;
    }

    public void setWorker(SmartFrogThread worker) {
        this.worker = worker;
    }


    /**
     * override point --should we check for workflow termination after startup
     *
     * @return true if the workflow attributes should be checked during startup
     */
    protected boolean terminateAfterStartup() {
        return false;
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException
     *                                  failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        timeout = sfResolve(ATTR_TIMEOUT, timeout, true);
        worker = new SmartFrogThread(this);
        worker.setName(sfCompleteName().toString());
        worker.start();
    }


    /**
     * terminate the worker thread if needed
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread thread = getWorker();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }


    /**
     * poll the liveness page
     *
     * @return any exception that got raised, or null for no error
     */
    private SmartFrogLivenessException poll() {
        try {
            getLivenessPage().onPing();
            //success!
            return null;
            //exit the loop
        } catch (SmartFrogLivenessException e) {
            //remember the last exception
            return e;
        }
    }

    /**
     * this thread does the sleep and poll
     *
     * @see Thread#run()
     */
    public void run() {
        long now = System.currentTimeMillis();
        long endTime = now + timeout;
        long sleepTime = getCheckFrequency();
        boolean timedOut;
        boolean success = false;
        boolean interrupted = false;
        SmartFrogLivenessException lastException = null;
        getLog().info("Starting to wait for " + timeout + "ms on " + getLivenessPage().toString());
        do {
            if (poll() == null) {
                success = true;
                break;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                //ooh, interrupted
                interrupted = true;
            }
            now = System.currentTimeMillis();
            timedOut = now > endTime;
        } while (!timedOut && !interrupted);

        //exit the loop, post process our current state
        if (interrupted) {
            //interrupted? Bail out immediately as
            //we were probably terminated
            return;
        }

        //now look at the result
        if (!success) {
            //on a failure, grab the full text
            getLivenessPage().setFetchErrorText(true);
            //poll the site
            lastException = poll();
            //and check that the success flag didnt change
            success = lastException == null;
        }
        TerminationRecord record;
        Reference name = getHelper().completeNameOrNull();
        //create an exception appropriate for the fault.
        if (success) {
            //successful exit
            record = TerminationRecord.normal(name);
            //now do a terminate with the relevant message
            getHelper().sfSelfDetachAndOrTerminate(record);
        } else {
            //failure. Abnormal termination
            String errorText = ERROR_WAIT_FOR_TIMEOUT + getLivenessPage().getTargetURL();
            if (getLivenessPage().getErrorMessage() != null) {
                errorText = errorText +
                        '\n' + getLivenessPage().getErrorMessage();
            }
            record = TerminationRecord.abnormal(
                    errorText,
                    name,
                    lastException);
            //always terminate with an error if something went wrong
            sfTerminate(record);
        }
    }

    /**
     * Change the name in the description text
     *
     * @return "Waiting for "
     */
    protected String getDescription() {
        return "Waiting for ";
    }

}
