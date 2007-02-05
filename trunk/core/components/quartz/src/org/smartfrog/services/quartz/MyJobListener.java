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

package org.smartfrog.services.quartz;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.logging.LogSF;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class MyJobListener implements JobListener {
  
	public String getName(){
		return "myjoblistener";
		}

    public void jobToBeExecuted(JobExecutionContext context){
    		System.out.println("Job to be executed");
    	}

    public void jobExecutionVetoed(JobExecutionContext context){
    		System.out.println("Job execution veoted");
    }

    public void jobWasExecuted(JobExecutionContext context,
            JobExecutionException jobException){
    		System.out.println("Job was executed");
    }
      
    
   
}
