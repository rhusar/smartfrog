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
package org.smartfrog.examples.orchdws.balancer;

import java.rmi.RemoteException;

import org.smartfrog.examples.orchdws.apache.Apache;
import org.smartfrog.services.dependencies.statemodel.state.InvokeAsynchronousStateChange;
import org.smartfrog.services.dependencies.statemodel.state.StateComponent;
import org.smartfrog.services.dependencies.statemodel.state.StateComponentTransitionException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

public class ServerInstanceImpl extends StateComponent implements Prim, ServerInstance {
	
	public ServerInstanceImpl() throws RemoteException {super();}  
	
	private Balancer lb = null;
	private ComponentDescription template = null;
	private Compound parent = null;
	private Prim deployed = null;
	private Integer sfIndex;
	
	public synchronized void sfStart()
    throws SmartFrogException, RemoteException {
		super.sfStart();
		try {lb = (Balancer) sfResolve("lb"); } catch (Exception e){/*Shouldn't happen*/}
		try {template = (ComponentDescription) sfResolve("template", true); } catch (Exception e){/*Shouldn't happen*/}
		try {parent = (Compound) sfParent(); } catch (Exception e){/*Shouldn't happen*/}
		try {sfIndex = (Integer) sfResolve(new Reference(ReferencePart.attrib("sfIndex"))); } catch (Exception e){}
		
		System.out.println("WOOHOO"+sfIndex);
	}
		
	public boolean threadBody() throws StateComponentTransitionException {
		
		if (enabled.get("tstart")!=null) {	
			if (sfLog().isInfoEnabled()) sfLog().info("ServerInstance Transitioning:"+name+" going to up...");
	    
			String server = null;
			try { server = lb.lookUpHost(sfIndex); } catch (Exception e){/*Shouldn't happen*/}
			Context instanceContext = new ContextImpl();
	        instanceContext.put(SmartFrogCoreKeys.SF_PROCESS_HOST, server);
	        
	        if (sfLog().isDebugEnabled()) sfLog().debug ( "server instance being created");
	        try { deployed = parent.sfDeployComponentDescription("apacheServer", parent, template, instanceContext); } catch (Exception e){/*Shouldn't happen*/}
	        if (sfLog().isDebugEnabled()) sfLog().debug ( "instance created");
	        try { deployed.sfDeploy(); } catch (Exception e){/*Shouldn't happen*/}
            if (sfLog().isDebugEnabled()) sfLog().debug ( "deployed");
            try { deployed.sfStart(); } catch (Exception e){/*Shouldn't happen*/}
            if (sfLog().isDebugEnabled()) sfLog().debug ( "started");
	    	go("tstart");
	    	return true;
    	} else if (enabled.get("tstop")!=null) {
    		//Set apache state to false to start shutdown...
    		try { ((Apache)deployed.sfResolve("apache")).setApacheState(false); } catch (Exception e){/*Shouldn't happen*/}
    		return false;
    	}
		return true;
	}
	
	public void switchToDown(){
		//complete...
		if (sfLog().isInfoEnabled()) sfLog().info("ServerInstance Transitioning:"+name+" actually going to down...");
		try {
		invokeAsynchronousStateChange(new InvokeAsynchronousStateChange(){
        	public void actOn(StateComponent _si) {
        		try {
        			_si.go("tstop");
        		}catch (StateComponentTransitionException scte){/*Shouldn't happen*/}
            }
       });
		} catch (Exception e){}
		if (sfLog().isInfoEnabled()) sfLog().info("ServerInstance Transitioning:"+name+" scheduling going to down...");
		try { deployed.sfDetachAndTerminate(TerminationRecord.normal(sfCompleteName())); } catch (Exception e){/*Shouldn't happen*/}
	   if (sfLog().isInfoEnabled()) sfLog().info("ServerInstance Transitioning:"+name+" gone to down...");
	}
}
