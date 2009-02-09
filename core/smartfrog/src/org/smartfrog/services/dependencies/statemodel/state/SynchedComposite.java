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
	   
	   //System.out.println("In SynchedComposite...");
	   
	   try {
		   System.out.println("Replacing attribute in: "+this.sfCompleteName+": name:"+name+" : "+value);
		   if (name.equals("run") && (value instanceof Boolean) && ((Boolean)value).booleanValue()) {
			   this.sfRun();
			   super.sfReplaceAttribute("running", new Boolean(true));
		   }
	   } catch (SmartFrogException e){throw new SmartFrogRuntimeException(e);}
	   return result;
   }
}
