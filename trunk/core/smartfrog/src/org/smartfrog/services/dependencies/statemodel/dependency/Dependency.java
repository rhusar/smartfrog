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

package org.smartfrog.services.dependencies.statemodel.dependency;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.statemodel.state.RunSynchronisation;
import org.smartfrog.services.dependencies.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.TRANSITION;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.RUNNING;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.RELEVANT;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.ENABLED;

/**
 * Class that implements the dependency between two StateDependencies-implementing objects.
 * <p/>
 * On creation will add the dependency, or termination will remove it.
 */
public class Dependency extends PrimImpl implements Prim, DependencyValidation, RunSynchronisation {

   String transition = null;
   StateDependencies by = null;
   DependencyValidation on = null;
   String name="";

   public Dependency() throws RemoteException {
   }

   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      transition = sfResolve(TRANSITION, (String) null, true);
      name = this.sfParent().sfAttributeKeyFor(this).toString();
   }

   public synchronized void sfStart() throws SmartFrogException, RemoteException { 
	   super.sfStart();   
	   
	   //SHOULD BE A CHECK IN HERE ON RUNNING...
	   Boolean running = null;
       try {
           running = (Boolean) sfResolve(new Reference(ReferencePart.attrib(RUNNING)));
       } catch (SmartFrogResolutionException ignored) {
           sfLog().ignore(ignored); //ok
       }

       if (running!=null && running) sfRun();  //set it up now ("run" it)...
   }
   
   public synchronized void sfRun() throws SmartFrogException, RemoteException {

	   by = (StateDependencies) sfResolve("by");
       by.register(this);
       try {
           on = (DependencyValidation) sfResolve("on");
       } catch (SmartFrogResolutionException ignored) {
           sfLog().ignore(ignored); //ok
       }
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
       try {
           by.deregister(this);
       } catch (RemoteException e) {
           sfLog().error(e);
           throw new RuntimeException(e); //force a hard reset, which should follow...
       }
       super.sfTerminateWith(tr);
   }

   public String toString(){
	   return name;
   }
   
   public boolean isEnabled() throws RemoteException {
	   boolean relevant=true;
	   boolean enabled=false;
	   boolean isEnabled=true;
      
       try {
           relevant = sfResolve(RELEVANT, true, false);
       } catch (SmartFrogResolutionException ignored) {
           sfLog().debug(ignored); //ok
       }

       if (relevant){
           try {
               enabled = sfResolve(ENABLED, false, false);
           } catch (SmartFrogResolutionException ignored) {
               sfLog().debug(ignored); //ok
           }

           isEnabled = (enabled && (on == null || on.isEnabled()));
       }
       return isEnabled;
   }

   public String getTransition(){
	   return transition;
   }
}
