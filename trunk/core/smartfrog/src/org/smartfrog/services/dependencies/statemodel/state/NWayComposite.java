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

package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;
import java.util.Iterator;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;

/**
 *
 */
public class NWayComposite extends Composite implements Compound, StateChangeNotification {
   ComponentDescription template;
   ComponentDescription instances;
   int instanceCount;
   

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
