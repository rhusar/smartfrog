/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *  Basic example component.
 *  The Counter component (in components.sf) is a basic primitive component so
 *  its component description class CounterImpl extends PrimImpl (the base
 *  class for all the deployed components) which provides the default lifecycle
 *  template methods for a primitive component.
 *  Although PrimImpl itself implements Prim (the base interface for all the
 *  deployed components) CounterImpl also implements Prim because it is
 *  necessary for RMI that component also does so; the rmic compiler will
 *  otherwise not behave correctly.
 *  The CounterImpl class needs to be prepared for RMI for remote deployment
 *  This is done by creating and compiling the stubs and skeletons using the
 *  rmic compiler.
 *  This class is included in rmitargets that is read by the rmic compiler.
 */
public class SchedulerImpl extends PrimImpl implements Prim {
  
       	public Scheduler sched = null;	
		
	Log log = LogFactory.getLog(SchedulerImpl.class);

    public SchedulerImpl() throws RemoteException {
    }

    
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
            super.sfDeploy();
    }

   
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        super.sfStart();
	try {

		log.info("------- Initializing ----------------------");

	SchedulerFactory schedFact = new StdSchedulerFactory();

	sched = schedFact.getScheduler();
	log.info("------- Initialization Complete -----------");
       
	
	sched.start();
	log.info("------- Started Scheduler -----------------");
	System.out.println("Quartz Started");
	} catch (Exception ex) {}
    }

    public synchronized void sfTerminateWith(TerminationRecord t) {
	    try {
            log.info("------- Shutting Down ---------------------");
		sched.shutdown(true);
		log.info("------- Shutdown Complete -----------------");
	    } catch (Exception ex) {}
	    super.sfTerminateWith(t);
    }
}
