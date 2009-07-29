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

import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;
import org.smartfrog.sfcore.languages.sf.functions.Constraint;
import org.smartfrog.sfcore.prim.Liveness;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 *
 */
public class SynchedComposite extends Composite {

   public SynchedComposite() throws RemoteException {
	   super();
   }

   public synchronized Object sfReplaceAttribute(Object name, Object value)
	throws SmartFrogRuntimeException, RemoteException {

	   Object result = super.sfReplaceAttribute(name,value);
	   
	   try {
		   if (name.equals("run") && (value instanceof Boolean) && ((Boolean)value).booleanValue()) {
			   this.sfRun();
			   super.sfReplaceAttribute("running", new Boolean(true));
		   }
	   } catch (SmartFrogException e){throw new SmartFrogRuntimeException(e);}
	   return result;
   }
}
