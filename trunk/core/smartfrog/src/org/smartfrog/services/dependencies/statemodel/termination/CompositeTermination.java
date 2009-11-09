/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dependencies.statemodel.termination;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.statemodel.state.StateComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 */
public class CompositeTermination extends StateComponent {
   Prim toTerminate = null;
   TerminationRecord tr = TerminationRecord.normal(sfCompleteName());
   boolean required;
   boolean detach = false;

   public CompositeTermination() throws RemoteException {
	  
   }

   public void sfDeploy() throws RemoteException, SmartFrogException {
      super.sfDeploy();
      toTerminate = (Prim) sfResolve("toTerminate", true);
      detach = sfResolve("detachFirst", detach, false);
   }

   public boolean threadBody() {
	   if (sfLog().isInfoEnabled()) sfLog().info("terminating composite with " + tr);
	     //System.out.println("terminating model with " + tr);
	     
	     try {
	        if (detach) {
	           toTerminate.sfDetachAndTerminate(tr);
	        } else {
	           toTerminate.sfTerminate(tr);
	        }
	     } catch (RemoteException e) {
	        sfLog().error("failed to terminate model - hope liveness picks it up!", e);
	     }
	   return true;
   }
}

