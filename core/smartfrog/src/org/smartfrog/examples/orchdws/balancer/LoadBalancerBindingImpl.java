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
import java.util.HashMap;

import org.smartfrog.services.dependencies.statemodel.state.InvokeAsynchronousStateChange;
import org.smartfrog.services.dependencies.statemodel.state.StateComponent;
import org.smartfrog.services.dependencies.statemodel.state.StateComponentTransitionException;
import org.smartfrog.services.dependencies.statemodel.state.ThreadedState;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

public class LoadBalancerBindingImpl extends ThreadedState implements Prim, LoadBalancerBinding {
	
	public LoadBalancerBindingImpl() throws RemoteException {super();}  
	
	private Balancer lb = null;
	
	public boolean requireThread() throws StateComponentTransitionException { 
		HashMap<String, ComponentDescription> hm = getPossibleTransitions();
	    if (hm.get("tstart")!=null){ 
	    	if (sfLog().isInfoEnabled()) sfLog().info("LoadBalancerBinding Transitioning:"+name+" going to bound...");
	    	if (lb==null) try { lb = (Balancer) sfResolve("lb"); } catch (Exception e){/*Shouldn't happen*/}
	    	try { lb.enableServerInstance(sfIndex); } catch (Exception e){/*Shouldn't happen*/}
	    	acquireLock();
	    	setTransitionToCommit("tstart");
	    	go();
	    	clean();
	    	return false;
	    } else if (hm.get("tstop")!=null) return true;
		
		return false; 
	}  
	
	public boolean threadBody() throws StateComponentTransitionException {
		boolean toUnbound=false;
		if (sfLog().isInfoEnabled()) sfLog().info("LoadBalancerBinding Transitioning:"+name+" scheduled to go to unbound...");
		asyncResponse=true; //following potentially calls back to switchToUnbound...
		try { toUnbound=lb.disableServerInstance(sfIndex); } catch (Exception e){/*Shouldn't happen*/}
		asyncResponse=false;
		return toUnbound; //indicates async...  
	}
	
	public void switchToUnbound(){
		if (!LoadBalancerBindingImpl.this.asyncResponse) return;
		//complete...
		if (sfLog().isInfoEnabled()) sfLog().info("LoadBalancerBinding Transitioning:"+name+" actually going to unbound...");
		
		invokeAsynchronousStateChange(new InvokeAsynchronousStateChange(){
        	public void actOn(StateComponent _lbb) {
        		try {
        			//System.out.println("***Ready for action...***"+LoadBalancerBindingImpl.this.asyncResponse);
        			_lbb.setTransitionToCommit("tstop");
        			_lbb.go();
        		}catch (StateComponentTransitionException scte){/*Shouldn't happen*/}
            }
       });
	   if (sfLog().isInfoEnabled()) sfLog().info("LoadBalancerBinding Transitioning:"+name+" gone to unbound...");
	}
}
