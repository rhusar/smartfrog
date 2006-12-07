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

import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**

 */
public class WaitForPageImpl extends LivenessPageComponent
    implements WaitForPage, Runnable {

    int timeout = 0;

    /** thread to do the work */
    private Thread worker = null;

    public WaitForPageImpl() throws RemoteException {
    }

    public Thread getWorker() {
        return worker;
    }

    public void setWorker(Thread worker) {
        this.worker = worker;
    }


    /**
     * override point --should we check for workflow termination after startup
     *
     * @return true if the workflow attributes should be checked during startup
     */
    protected boolean terminateAfterStartup() {
        return true;
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();
        timeout = sfResolve(ATTR_TIMEOUT, timeout, true);
        worker=new Thread(this);
        worker.setName(this.sfCompleteName().toString());
        worker.start();
    }


    /**
     * terminate the worker thread if needed
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        Thread thread = getWorker();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }


    /**
     * poll the liveness page
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
        long endTime = now+timeout*1000;
        long sleepTime = getCheckFrequency()*1000;
        boolean timedOut;
        boolean success=false;
        boolean interrupted=false;
        SmartFrogLivenessException lastException=null;
        getLog().info("Starting to wait for "+timeout +"s on "+getLivenessPage().toString());
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
                break;
            }
            now = System.currentTimeMillis();
            timedOut=now>endTime;
        } while(!timedOut);
        
        //exit the loop, post process our current state
        if(interrupted) {
            //interrupted? Bail out immediately as
            //we were probably terminated
            return;
        }
        
        //now look at the result
        if(!success) {
            //on a failure, grab the full text
            getLivenessPage().setFetchErrorText(true);
            lastException=poll();
            success=lastException==null;
        }
        TerminationRecord record;
        Reference name=getHelper().completeNameOrNull();
        //create an exception appropriate for the fault.
        if(success) {
            //successful exit
            record = TerminationRecord.normal(name);
        } else {
            //failure. Abnormal termination
            record = TerminationRecord.abnormal(
                "Timeout waiting for a page to go live"+getLivenessPage().getTargetURL(),
                name,
                lastException);
        }
        //now do a terminate with the relevant exception
        getHelper().sfSelfDetachAndOrTerminate(record);
    }

    /**
     * Change the name in the description text
     * @return "Waiting for "
     */
    protected String getDescription() {
        return "Waiting for ";
    }

}
