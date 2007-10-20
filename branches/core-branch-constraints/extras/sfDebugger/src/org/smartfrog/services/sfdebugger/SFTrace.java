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

This library was developed along with Manjunatha H S and Vedavyas H Raichur 
from Sri JayChamrajendra College of Engineering, Mysore, India. 
The work was part of the final semester Project work.

*/

package org.smartfrog.services.sfdebugger;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimHook;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.sfdebugger.simpleShell;

/**
 * Implements the debug component in SmartFrog System. It debugs the lifecycle
 * methods of the components.
 */
public class SFTrace extends PrimImpl implements Prim{
    /** Deploy Debugger */
    private DeployAction Deployaction = new DeployAction();

    /** Start Debugger */
    private StartAction Startaction = new StartAction();

    /** Terminate Debugger */
    private TerminateAction Terminateaction = new TerminateAction();
    /**
     * Constructor.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public SFTrace() throws RemoteException {
    }

    /**
     * Deploys the component.
     *
     * @throws SmartFrogException in case of error while deploying
     * @throws RemoteException in case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();

        try {
		 sfDeployHooks.addHook(Deployaction);
		
		 sfStartHooks.addHook(Startaction);
		
		 sfTerminateWithHooks.addHook(Terminateaction);
        } catch (Throwable t) {
            
            throw new SmartFrogDeploymentException(t, this);
        }
    }

    /**
     * Starts the component.
     *
     * @throws SmartFrogException in case of error in starting
     * @throws RemoteException in case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
					      RemoteException {
        super.sfStart();
    }

    /**
     * Terminate the component.
     *
     * @param r TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord r) {
        try {
            sfDeployHooks.removeHook(Deployaction);
        } catch (Exception e) {
            System.out.println(" Couldn't remove all deploy hooks " + e);
        }

        try {
            sfStartHooks.removeHook(Startaction);
        } catch (Exception e) {
            System.out.println(" Couldn't remove all start hooks " + e);
        }
        try {
            sfTerminateWithHooks.removeHook(Terminateaction);
        } catch (Exception e) {
            System.out.println(" Couldn't remove terminate all hooks " + e);
        }

        super.sfTerminateWith(r);
    }

     /**
     *Utility inner class- deploy debugger
     */
    private class DeployAction implements PrimHook{
        public void sfHookAction(Prim p,TerminationRecord tr) throws SmartFrogException{
            	try {
	        if (p.sfContainsAttribute("DeployHook")) {	
			System.out.println("Deploy HookAction for " +p.sfCompleteName().toString());
			simpleShell ss = new simpleShell(p);
			ss.processAttributes();
		}
	}catch (RemoteException ex) {
			ex.printStackTrace();
		}	
	}
        
	
    }

     /**
     *Utility inner class- start debugger
     */
    private class StartAction implements PrimHook{
        public void sfHookAction(Prim p,TerminationRecord tr) throws SmartFrogException{
            	try {
	        if (p.sfContainsAttribute("StartHook")) {	
			System.out.println("Start HookAction for " +p.sfCompleteName().toString());
			simpleShell ss = new simpleShell(p);
			ss.processAttributes();
		}
		}catch (RemoteException ex) {
			ex.printStackTrace();
		}
                   	
	}
        
	
    }

      /**
     *Utility inner class- terminate debugger
     */
    private class TerminateAction implements PrimHook{
        public void sfHookAction(Prim p,TerminationRecord tr) throws SmartFrogException{
            	try {
		if (p.sfContainsAttribute("TerminateHook"))	{
			System.out.println("Terminate HookAction for " +p.sfCompleteName().toString());
			simpleShell ss = new simpleShell(p);
			ss.processAttributes();
		}
		}catch (RemoteException ex) {
			ex.printStackTrace();
		}	
	}
    }
}
