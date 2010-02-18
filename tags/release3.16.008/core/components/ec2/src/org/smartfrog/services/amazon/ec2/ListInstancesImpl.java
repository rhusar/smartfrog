/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.amazon.ec2;

import com.xerox.amazonws.ec2.Jec2;
import org.smartfrog.services.assertions.events.TimeoutTracker;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created 01-Apr-2008 15:15:07
 */

public class ListInstancesImpl extends EC2ComponentImpl
        implements ListInstances {

    protected static final Reference refInstances = new Reference(ATTR_INSTANCES);
    private List<String> instances;
    private String imageID;
    //minimum number of instances
    private int minCount;
    //max number
    private int maxCount;
    private String state;
    //time in millis to wait until the minCount has been reached. -1 means forever, 0 for never
    private int timeout;
    //polling frequency.
    private int pollInterval;

    public ListInstancesImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        instances = ListUtils.resolveStringList(this, refInstances, true);
        minCount = sfResolve(ATTR_MIN_COUNT, 0, true);
        maxCount = sfResolve(ATTR_MAX_COUNT, 0, true);
        imageID = sfResolve(ATTR_IMAGEID, "", true);
        state = sfResolve(ATTR_STATE, "", true);
        timeout = sfResolve(ATTR_TIMEOUT, 0, true);
        pollInterval = sfResolve(ATTR_POLL_INTERVAL, 0, true);
        createAndDeployWorker();
    }

    private void createAndDeployWorker() {
        deployWorker(new Ec2InstanceThread());
    }


    /**
     * Process the list of instances enumerated The base class lists the
     * instances at info level then checks the number of instances found.
     *
     * @param instanceList instances
     * @throws SmartFrogException for any SF exception
     * @throws RemoteException for networking problems
     */
    protected void processInstanceList(InstanceList instanceList)
            throws SmartFrogException, RemoteException {
        logInstances(instanceList);
        checkInstanceCount(instanceList);
    }

    /**
     * Check that the instance count is in range
     *
     * @param instanceList list of instances
     * @throws SmartFrogDeploymentException if the count is too high or low
     */
    protected void checkInstanceCount(List<ImageInstance> instanceList)
            throws SmartFrogDeploymentException {
        int count = instanceList.size();
        if (minCount >= 0 && count < minCount) {
            throw new SmartFrogDeploymentException(
                    "Too few instances deployed, expecting " + minCount + " but found " + count);
        }
        if (maxCount >= 0 && count > maxCount) {
            throw new SmartFrogDeploymentException(
                    "Too many instances deployed, expecting " + maxCount + " but found " + count);
        }
    }

    /**
     * Do whatever is needed to delay for the next poll
     * @return true if the delay should finish
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     * @throws InterruptedException if the thread got interrupted; this is interpreted as an exit
     */
    protected boolean delayForNextPoll()
            throws SmartFrogException, RemoteException, InterruptedException {
        Thread.sleep(pollInterval);
        return false;
    }

    /**
     * Thread to create the instance
     */
    private class Ec2InstanceThread extends WorkflowThread {


        /**
         * Create a basic thread. Notification is bound to a local notification
         * object.
         */
        private Ec2InstanceThread() {
            super(ListInstancesImpl.this, true);
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run
         * object, then that <code>Runnable</code> object's <code>run</code>
         * method is called; otherwise, this method does nothing and returns.
         * <p> Subclasses of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        @Override
        public void execute() throws Throwable {
            Jec2 binding = getEc2binding();
            TimeoutTracker tracker=new TimeoutTracker(timeout);
            InstanceList instanceList=null;
            boolean finished=false;
            while(!finished) {
                instanceList = InstanceList.describeInstances(binding,
                    instances,
                    imageID,
                    state);
                boolean overMinimum=instanceList.size()>minCount;
                finished = overMinimum || tracker.isTimedOut();
                if(!finished) {
                    //not finished, so let's poll
                    if(delayForNextPoll()) {
                        //the delay process said it is time to exit, but not
                        //bail out with an interrupted thread
                        break;
                    }
                }
            }
            //hand off to the implementation or subclass
            processInstanceList(instanceList);
        }


    }

}
