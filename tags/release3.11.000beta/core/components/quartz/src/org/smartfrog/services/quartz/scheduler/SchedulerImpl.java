/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.quartz.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * Starts the quartz scheduler on startup
 */
public class SchedulerImpl extends PrimImpl implements Prim {

    private Scheduler scheduler = null;

    private static final Log log = LogFactory.getLog(SchedulerImpl.class);

    public SchedulerImpl() throws RemoteException {
    }


    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        try {

            log.info("Initializing");

            SchedulerFactory schedFact = new StdSchedulerFactory();

            scheduler = schedFact.getScheduler();
            log.info("Initialization Complete");
            scheduler.start();
            log.info("Started Scheduler");
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    public  void sfTerminateWith(TerminationRecord t) {
        synchronized(this) {
            try {
                if(scheduler!=null) {
                    log.info("Shutting Down");
                    scheduler.shutdown(true);
                    log.info("Shutdown Complete");
                }
            } catch (Exception ex) {
                log.error("when shutting down the scheduler",ex);
            } finally {
                scheduler=null;
            }
        }
        super.sfTerminateWith(t);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
