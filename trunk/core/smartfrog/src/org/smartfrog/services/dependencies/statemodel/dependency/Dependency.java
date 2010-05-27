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
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.RUNNING;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.RELEVANT;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.ENABLED;

/**
 * Class that implements the dependency between two StateDependencies-implementing objects.
 * <p/>
 * On creation will add the dependency, or termination will remove it.
 */
public class Dependency extends PrimImpl implements Prim, DependencyValidation, RunSynchronisation {

   String transition = null;  //CUT
   StateDependencies by = null;
   DependencyValidation on = null;
   String name="";

   public Dependency() throws RemoteException {
   }

   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
      super.sfDeploy();
      //transition = sfResolve(TRANSITION, (String) null, false);
      name = this.sfParent().sfAttributeKeyFor(this).toString();
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

   public synchronized void sfStart() throws SmartFrogException, RemoteException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);
	   super.sfStart();   
	   
	   //SHOULD BE A CHECK IN HERE ON RUNNING...
	   Boolean running = null;
       try {
           running = (Boolean) sfResolve(new Reference(ReferencePart.attrib(RUNNING)));
       } catch (SmartFrogResolutionException ignored) {
           sfLog().ignore(ignored); //ok
       }

       if (running!=null && running) sfRun();  //set it up now ("run" it)...
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }
   
   public synchronized void sfRun() throws SmartFrogException, RemoteException {

       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

       Prim byp = null;
       try {
           byp = (Prim) sfResolve("by");
       } catch (ClassCastException e) {
           //treat as String
           String byS = sfResolve("by").toString();
           byp = (Prim) sfResolve(new Reference(ReferencePart.attrib(byS)));
       }
       Prim register = sfResolve("register", this, false);

       if (byp instanceof StateDependencies){
           by = (StateDependencies)  byp;
       } else {
           //We need to look into by to get it...
           by = (StateDependencies) byp.sfResolve("sfOrchestratedTarget");
           register.sfReplaceAttribute("by", by);
       }

       sfLog().debug("BY: "+by);

       by.register((DependencyValidation)register);

       try {
           Prim onp = null;
           try {
               onp = (Prim) sfResolve("on");
           } catch (ClassCastException e) {
               //treat as String
               String onS = sfResolve("on").toString();
               onp = (Prim) sfResolve(new Reference(ReferencePart.attrib(onS)));
           }

           if (onp instanceof DependencyValidation) {
               on = (DependencyValidation) onp;
           } else {
               //We need to look into by to get it...
               on = (DependencyValidation) onp.sfResolve("sfOrchestratedTarget");
               register.sfReplaceAttribute("on", on);
           }
       } catch (SmartFrogResolutionException ignore) {
            sfLog().ignore(ignore); //ok
       }

       sfLog().debug("ON: " + on);
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

   public synchronized void sfTerminateWith(TerminationRecord tr) {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
       try {
           by.deregister(this);
       } catch (RemoteException e) {
           sfLog().error(e);
           throw new RuntimeException(e); //force a hard reset, which should follow...
       }
       super.sfTerminateWith(tr);
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
   }

   public String toString(){
	   return name;
   }
   
   public boolean isEnabled() throws RemoteException, SmartFrogRuntimeException {
       if (sfLog().isDebugEnabled()) sfLog().debug(Thread.currentThread().getStackTrace()[1]);

       boolean relevant=true;
	   boolean enabled=false;
	   boolean isEnabled=true;
      
       try {
           relevant = sfResolve(RELEVANT, true, false);
       } catch (SmartFrogResolutionException ignored) {
           sfLog().debug(ignored); //ok
       }

       sfLog().debug("RELEVANT: " + relevant);

       if (relevant){
           try {
               enabled = sfResolve(ENABLED, false, false);
               sfLog().debug("ENABLED: " + enabled);
           } catch (SmartFrogResolutionException ignored) {
               sfLog().debug(ignored); //ok
           }

           isEnabled = (enabled && (on == null || on.isEnabled()));
       }
       sfLog().debug("ISENABLED: " + isEnabled);

       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
       return isEnabled;
   }

   public String getTransition(){
	   return transition;
   }
}
