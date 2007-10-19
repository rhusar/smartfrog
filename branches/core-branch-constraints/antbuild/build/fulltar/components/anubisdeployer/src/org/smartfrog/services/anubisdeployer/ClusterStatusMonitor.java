/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.anubisdeployer;


import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisListener;
import org.smartfrog.services.anubis.locator.AnubisValue;

public class ClusterStatusMonitor extends PrimImpl implements Prim, ClusterMonitor {

    private AnubisLocator anubis = null;

    private ComponentDescription data = new ComponentDescriptionImpl(null, new ContextImpl(), true);


    private HashSet registrations = new HashSet();

    private Thread notifierThread;
    private static final String ATTR_ANUBIS_LOCATOR = "anubisLocator";
    private static final String ATTR_HOSTNAME = "hostname";

    // /////////////////////////////////////////////////////
    //
    // Constructor method
    //
    // /////////////////////////////////////////////////////


    public ClusterStatusMonitor() throws RemoteException {
    }

    // /////////////////////////////////////////////////////
    //
    // Template methods
    //
    // /////////////////////////////////////////////////////


    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        anubis = (AnubisLocator) sfResolve(ATTR_ANUBIS_LOCATOR);
        anubis.registerListener(idListener);
    }


    public synchronized void sfStart() throws SmartFrogException, RemoteException {
	super.sfStart();
        notifierThread = new NotifierThread();
        notifierThread.start();
    }


    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            anubis.deregisterListener(idListener);
        } catch (Exception e) {
        }
        try {
            notifierThread.stop();
        } catch (Exception e) {
        }

        super.sfTerminateWith(tr);
    }


    // /////////////////////////////////////////////////////
    //
    // ClusterMonitor methods
    //
    // /////////////////////////////////////////////////////


    /**
     * Obtain the current resource information about the cluster
     */
    public ComponentDescription clusterStatus()
            throws RemoteException {
        return data;
    }

    /**
     * Register for notification in changes in cluster resource information
     *
     * @param callback the interface to notify of changes in status
     */
    public ComponentDescription registerForClusterStatus(ClusterStatus callback)
            throws RemoteException {
        synchronized (registrations) {
            registrations.add(callback);
            return data;
        }
    }

    /**
     * Deregister for notification in changes in cluster resource information
     *
     * @param callback the interface to remove from tbe notification
     */
    public void deregisterForClusterStatus(ClusterStatus callback)
            throws RemoteException {
        synchronized (registrations) {
            registrations.remove(callback);
        }
    }

    /*
    * *******************************************************************************************************************
    */

    private AnubisListener idListener = new AnubisListener("Cluster") {
        private Hashtable values = new Hashtable();

        public void newValue(AnubisValue value) {
            synchronized (values) {
                if (values.containsKey(value)) {
                    modifyResource((ComponentDescription) value.getValue(), (ComponentDescription) values.get(value));
                } else {
                    addResource((ComponentDescription) value.getValue());
                }
                values.put(value, value.getValue());
            }
        }

        public void removeValue(AnubisValue value) {
            synchronized (values) {
                removeResource((ComponentDescription) values.get(value));
                values.remove(value);
            }
        }
    };

    /*
     * *******************************************************************************************************************
     */


    /*
     *    methods for use with Anubis listener
     */

    private void addResource(ComponentDescription desc) {
        synchronized (data) {
            // add to data, indexed by hostname
            try {
                Object hostname = desc.sfResolveHere(ATTR_HOSTNAME, false);
                if (hostname != null) { // should never be - but in case...
                    try {
                        data.sfAddAttribute(hostname, desc);
                    } catch (SmartFrogRuntimeException se) {
                        sfLog().warn(se);
                    }
                }
            } catch (SmartFrogResolutionException re) {
                //should never happen
                sfLog().ignore(re);
            }
        }
        notifyRegistrationsOfStatus();
    }


    private void removeResource(ComponentDescription desc) {
        synchronized (data) {
            // remove index by hostname (note desc is old data...)
            try {
                Object hostname = desc.sfResolveHere(ATTR_HOSTNAME, false);
                if (hostname != null) { // should never be - but in case...
                    try {
                        data.sfRemoveAttribute(hostname);
                    } catch (SmartFrogRuntimeException se) {
                        //shouldn't happen
                        sfLog().warn(se);
                    }
                }
            } catch (SmartFrogResolutionException re) {
                //should never happen
                sfLog().ignore(re);
            }
        }
        notifyRegistrationsOfStatus();
    }


    private void modifyResource(ComponentDescription newDesc, ComponentDescription oldDesc) {
        synchronized (data) {
            // replace index by hostname
            try {
                Object hostname = newDesc.sfResolveHere(ATTR_HOSTNAME, false);
                if (hostname != null) { // should never be - but in case...
                    try {
                        data.sfReplaceAttribute(hostname, newDesc);
                    } catch (SmartFrogRuntimeException se) {
                        //shouldn't happen...
                        sfLog().warn(se);
                    }
                }
            } catch (SmartFrogResolutionException re) {
                //should never happen
                sfLog().ignore(re);
            }
        }
        notifyRegistrationsOfStatus();
    }

    /*
     * *******************************************************************************************************************
     */

    /*
     *    Notification method
     */

    private volatile int statusCounter = 0;
    private Object notificationLock = new Object();

    private void doNotifyRegistrationsOfStatus() {
        synchronized (registrations) {
            for (Iterator i = registrations.iterator(); i.hasNext();) {
                ClusterStatus a = null;
                try {
                    a = (ClusterStatus) i.next();
                    a.clusterStatus(data);
                } catch (Exception e) {
                    try {
                        deregisterForClusterStatus(a);
                    } catch (Exception ex) {
                        sfLog().warn(ex);
                    }
                }
            }
        }
    }



    private void notifyRegistrationsOfStatus() {
        synchronized (notificationLock) {
            statusCounter += 1;
            notificationLock.notify();
        }
    }


    class NotifierThread extends Thread {
        public void run() {
            int counter = 0;
            boolean statusChanged = false;

            while (true) {
                synchronized (notificationLock) {
                    statusChanged = false;
                    if (counter != statusCounter) {
                        counter = statusCounter;
                        statusChanged = true;
                    }
                    if (!statusChanged) {
                        try {
                            notificationLock.wait();
                        } catch (InterruptedException e) {
                        }
                        if (counter != statusCounter) {
                            counter = statusCounter;
                            statusChanged = true;
                        }
                    }
                }

                if (statusChanged) {
                    doNotifyRegistrationsOfStatus();
                }
            }
        }
    }


}
