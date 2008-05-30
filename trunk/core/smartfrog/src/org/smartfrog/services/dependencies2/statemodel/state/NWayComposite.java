package org.smartfrog.services.dependencies2.statemodel.state;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.util.Iterator;

/**
 *
 */
public class NWayComposite extends Composite implements Compound, StateChangeNotification, NotificationLock {
   ComponentDescription template;
   ComponentDescription instances;
   int instanceCount;
   boolean numeric = true;


   public NWayComposite() throws RemoteException {
   }

   public synchronized void sfDeployWith(Prim p, Context ctx) throws RemoteException, SmartFrogDeploymentException {
      super.sfDeployWith(p, ctx);
      template = (ComponentDescription) ctx.get("template");
      Object i = ctx.get("instances");
      try {
         if (i instanceof ComponentDescription) {
            instances = (ComponentDescription) i;
            for (Iterator e = instances.sfAttributes(); e.hasNext();) {
               Object key = e.next();
               ComponentDescription data = (ComponentDescription) instances.sfResolveHere(key);
               lifecycleChildren.add(sfDeployComponentDescription(key, this, (ComponentDescription) template.copy(), (Context) data.sfContext().copy()));
            }
         } else if (i instanceof Number) {
            instanceCount = ((Number) i).intValue();
            for (int e = 1; e <= instanceCount; e++) {
               lifecycleChildren.add(sfDeployComponentDescription("instance" + e, this, (ComponentDescription) template.copy(), null));
            }
         }
      } catch (Exception sfex) {
         new TerminatorThread(this, sfex, null).quietly().start();
         throw(SmartFrogDeploymentException) SmartFrogDeploymentException.forward(sfex);
      }
   }
}
