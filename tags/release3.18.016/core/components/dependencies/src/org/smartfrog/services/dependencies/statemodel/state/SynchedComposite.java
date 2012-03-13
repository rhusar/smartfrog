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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import java.rmi.RemoteException;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.RUNNING;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.RUN;

/**
 *
 */
public class SynchedComposite extends Composite {

   public SynchedComposite() throws RemoteException {  
   }

    
  public synchronized Object sfReplaceAttribute(Object key, Object value)
	throws SmartFrogRuntimeException, RemoteException {
	   Object result = super.sfReplaceAttribute(key, value);
       if (key.equals(RUN) && (value instanceof Boolean) && ((Boolean)value)) {
           try {
               this.sfRun();
           } catch (SmartFrogException e) {
               sfLog().error(e);
               throw new SmartFrogRuntimeException(e);
           }
           super.sfReplaceAttribute(RUNNING, true);
       }
	   return result;
   }
}
