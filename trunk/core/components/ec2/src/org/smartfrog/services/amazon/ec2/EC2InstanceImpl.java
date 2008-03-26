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

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.ReservationDescription;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Component to deploy an instance
 */

public class EC2InstanceImpl extends EC2ComponentImpl implements EC2Instance {

    private String instanceType;
    private String imageID;
    private boolean shutdown;
    private String userData;
    private ReservationDescription reservation;
    private String instanceID;


    public EC2InstanceImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();

        instanceType = sfResolve(ATTR_INSTANCETYPE, "", true);
        imageID = sfResolve(ATTR_IMAGEID, "", true);
        userData = sfResolve(ATTR_USER_DATA, "", true);
        shutdown = sfResolve(ATTR_SHUTDOWN, false, true);
        if (imageID.length() == 0) {
            throw new SmartFrogLifecycleException("No image to deploy");
        }
        setWorker(new Ec2InstanceThread());
        getWorker().start();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        ReservationDescription.Instance instance = getInstance();
        if (shutdown && instance != null) {
            String[] instances = new String[1];
            instances[1] = instance.getInstanceId();
            try {
                getEc2binding().terminateInstances(instances);
            } catch (EC2Exception e) {
                sfLog().warn("When terminating " + instance, e);

            }
        }

    }

    protected void reserve(ReservationDescription reservationInfo) {
        reservation = reservationInfo;
        ReservationDescription.Instance instance = getInstance();
        instanceID = instance.getImageId();

    }

    protected synchronized ReservationDescription.Instance getInstance() {
        if (reservation != null) {
            List<ReservationDescription.Instance> instances = reservation.getInstances();
            if (!instances.isEmpty()) {
                return instances.get(0);
            }
        }
        //no match
        return null;
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
            super(EC2InstanceImpl.this, false);
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run
         * object, then that <code>Runnable</code> object's <code>run</code>
         * method is called; otherwise, this method does nothing and returns.
         * <p> Subclasses of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            InstanceType size = InstanceType.getTypeFromString(instanceType);
            sfLog().info("Deploying a " + instanceType + " image ID " + imageID);
            reservation = getEc2binding().runInstances(
                    imageID,
                    1,
                    1,
                    null,
                    userData,
                    null,
                    true,
                    size);
            sfLog().info("Reserved an instance:" + reservation);
        }
    }

}
