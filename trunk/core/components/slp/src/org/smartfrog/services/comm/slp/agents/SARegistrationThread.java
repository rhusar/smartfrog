/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.ServiceURL;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.Vector;

/**
 * This class implements a thread that handles registration of services with a DA on behalf of a SA. It will check if
 * new registrations are required, and will tell the SA to register a certaion service with a given DA if that needs
 * doing.
 */
class SARegistrationThread extends Thread {
    /** The time between each time the thread should check if re-registrations are needed. */
    private static final int SAFETY = 60; // 60 seconds

    /** Say how often the services are to be refreshed. */
    private static final int REFRESH_INTERVAL = ServiceURL.LIFETIME_MAXIMUM - SAFETY;

    /** The service agent owning the thread. */
    private ServiceAgent owner;

    /**
     * A list of services that must be registered with all DAs. This list is updated when the SA receives new
     * registrations from the user.
     */
    private LinkedList toRegister;

    /** A list of DAs to register all services with. This list is updated when new DAs are discovered. */
    private LinkedList toRegisterWith;

    /** A list of service to deregister. */
    private LinkedList toRemove;

    /** The thread is running as long as this is true. */
    private boolean isRunning = true;

    /** Object used for synchronization */
    private Object wSync = new Object();

    /** When this is true, the thread should have something to do... */
    private boolean hasWorkToDo = false;

    /** A timer keeping track of when to refresh registrations. */
    private Timer timer;

    /** Set to true when advertisements should be refreshed (with DA). */
    protected boolean itIsTimeToRefresh = false;

    /**
     * The database to use. This is the database used by the ServiceAgent. This thread never modifies the database, it
     * only reads data from it.
     */
    private SLPDatabase database;

    /**
     * Creates a new SARegistration thread.
     *
     * @param owner The SA owning the thread
     * @param db    The Advertisement database.
     */
    public SARegistrationThread(ServiceAgent owner, SLPDatabase db) {
        this.owner = owner;
        database = db;
        toRegister = new LinkedList();
        toRegisterWith = new LinkedList();
        toRemove = new LinkedList();
        timer = new Timer(true);
        long interval = REFRESH_INTERVAL * 1000;
        timer.schedule(new SARefreshTimerTask(this), interval, interval);
        hasWorkToDo = false;
    }

    /**
     * The method executed by the thread while it is running. Waits for registrations/deregistrations/new DAs/refresh
     * time and performs the required actions.
     */
    public void run() {
        while (isRunning) {
            // send registrations to DAs
            if (!toRegister.isEmpty()) {
                registerWithAllDAs();
            }
            // send deregistration messages to DAs.
            if (!toRemove.isEmpty()) {
                removeFromAllDAs();
            }
            // register all services with DAs.
            if (!toRegisterWith.isEmpty()) {
                registerAllServices();
            }
            // refresh advertisements with all DAs.
            if (itIsTimeToRefresh) {
                itIsTimeToRefresh = false;
                refreshServices();
            }
            // wait for something to do...
            waitForWork();
        }
    }

    /** This method stops the thread. */
    public void stopThread() {
        isRunning = false;
        tellThreadToWork();
    }

    /** The thread calls this when it is done with its work, and needs to wait for more. */
    private void waitForWork() {
        synchronized (wSync) {
            if (!hasWorkToDo) {
                try {
                    wSync.wait();
                } catch (Exception e) {
                }
            }
            hasWorkToDo = false;
        }
    }

    /**
     * This is called when the thread needs to wake up. Either because new services or DAs have been added or it is time
     * for refreshing registrations.
     */
    protected void tellThreadToWork() {
        synchronized (wSync) {
            hasWorkToDo = true;
            wSync.notifyAll();
        }
    }

    /**
     * Called by the SA when a new service has been added.
     *
     * @param reg The database entry for the new registration
     */
    public void newRegistration(SLPDatabaseEntry reg) {
        synchronized (toRegister) {
            toRegister.addLast(reg);
        }
        tellThreadToWork();
    }

    /**
     * Called by the SA when a service is to be deregistered from the DAs.
     *
     * @param reg The database entry for the service to be removed.
     */
    public void delRegistration(ServiceURL reg) {
        synchronized (toRemove) {
            toRemove.addLast(reg);
        }
        tellThreadToWork();
    }

    /**
     * Called by the SA when a new DA has been discovered.
     *
     * @param da The DAInfo describing the DA.
     */
    public void newDA(DAInfo da) {
        synchronized (toRegisterWith) {
            toRegisterWith.addLast(da);
        }
        tellThreadToWork();
    }

    /** The thread calls this method when new services have been added that need to be registered with all DAs. */
    private void registerWithAllDAs() {
        // send a registration for the given services to all DAs.
        Vector DAs = owner.getDAList();
        Iterator iter;
        SLPDatabaseEntry e;
        DAInfo da;
        while (!toRegister.isEmpty()) {
            synchronized (toRegister) {
                e = (SLPDatabaseEntry) toRegister.remove(0);
            }
            for (iter = DAs.iterator(); iter.hasNext();) {
                da = (DAInfo) iter.next();
                // send registration...
                int newLifetime = e.getRemainingLifetime();
                ServiceURL url = new ServiceURL(e.getURL().toString(), newLifetime);
                if (!owner.registerService(url, e.getAttributes(), da)) {
                    iter.remove(); // Failed to contact DA.
                }
            }
        }

        // in case we don't have any DAs:
        synchronized (toRegister) {
            toRegister.clear();
        }
    }

    /** The thread calls this when new DAs have been discovered, and it needs to register all services with them. */
    private void registerAllServices() {
        // register all services with the DAs in toRegisterWith
        LinkedList srv = database.getAllServices(); //owner.getServiceList();
        Iterator iter;
        DAInfo da;
        SLPDatabaseEntry e;
        while (!toRegisterWith.isEmpty()) {
            synchronized (toRegisterWith) {
                da = (DAInfo) toRegisterWith.remove(0);
            }
            for (iter = srv.iterator(); iter.hasNext();) {
                e = (SLPDatabaseEntry) iter.next();
                int newLifetime = e.getRemainingLifetime();
                if (newLifetime < 0 && e.getLifetime() != ServiceURL.LIFETIME_PERMANENT) {
                    database.removeEntry(e); // lifetime expired
                }
                // send reg...
                ServiceURL url = new ServiceURL(e.getURL().toString(), newLifetime);
                if (!owner.registerService(url, e.getAttributes(), da)) {
                    break; // failed to contact DA
                }
            }
        }

        // in case we don't have any services
        synchronized (toRegisterWith) {
            toRegisterWith.clear();
        }
    }

    /** Removes the services in toRemove from all DAs. */
    private void removeFromAllDAs() {
        // send a registration for the given services to all DAs.
        Vector DAs = owner.getDAList();
        Iterator iter;
        ServiceURL u;
        DAInfo da;
        while (!toRemove.isEmpty()) {
            synchronized (toRemove) {
                u = (ServiceURL) toRemove.remove(0);
            }
            for (iter = DAs.iterator(); iter.hasNext();) {
                da = (DAInfo) iter.next();
                // send registration...
                if (!owner.deregisterService(u, da)) {
                    iter.remove(); // DA is down
                }
            }
        }

        // in case we don't have any DAs:
        synchronized (toRemove) {
            toRemove.clear();
        }
    }

    /**
     * This is called when it is time to refresh registrations. The method reregisters all services with a lifetime of
     * LIFETIME_PERMANENT. It is run after a wait of REFRESH_INTERVAL seconds, and does not do any clever checks to see
     * if a service has been added recently. If a service was added 2 seconds before this method is called, it will
     * still be refreshed.
     */
    private void refreshServices() {
        /*
         ServiceLocationManager.getRefreshInterval currently always returns 0.
         When this is implemented, the timing of the refresh calls will have to
         be modified to support this. Although the min_refresh interval should
         never be higher than what we use now, as that could lead to registrations 
         getting lost for a period of time, which is not good.
        */
        //int min_refresh_interval = ServiceLocationManager.getRefreshInterval();
        //if(min_refresh_interval == 0) min_refresh_interval = ServiceURL.LIFETIME_MAXIMUM;
        //int timeUntilNextRefresh = REFRESH_INTERVAL;
        //if(timeUntilNextRefresh > min_refresh_interval) timeUntilNextRefresh = min_refresh_interval;

        //System.out.println("SAReg -> Refreshing services");
        Vector DAs = owner.getDAList();
        SLPDatabaseEntry e;
        DAInfo da;
        LinkedList toRefresh = database.getPermanentServices();
        Iterator iter = toRefresh.iterator();
        while (iter.hasNext()) {
            e = (SLPDatabaseEntry) iter.next();
            for (Iterator daIter = DAs.iterator(); daIter.hasNext();) {
                da = (DAInfo) daIter.next();
                owner.registerService(e.getURL(), e.getAttributes(), da);
            }
        }
    }
}
