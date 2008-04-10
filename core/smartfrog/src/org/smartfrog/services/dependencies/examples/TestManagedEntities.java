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
package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.languages.sf.functions.BaseFunction;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class TestManagedEntities extends CompoundImpl implements Compound {		
	public TestManagedEntities() throws RemoteException {super();}  

	///CHECK SYNC...
	public synchronized void waitForTerminate(){
		try { 
			if (!sfIsTerminating()) wait(); 
		} catch (Exception e){}
	}
	
	public String getOutput(){
		String output = "";
		try { 
			output=(String) ((Prim)sfParent().sfResolve("test")).sfContext().get("output");
		} catch (Exception e){}
		return output;
	}
	
	public synchronized void sfTerminateWith(TerminationRecord tr) {
		super.sfTerminateWith(tr);
		notifyAll();
	}

	static public class TestManagedEntitiesTerminate extends BaseFunction {
		
	    protected Object doFunction() {
	    	TestManagedEntities tme = (TestManagedEntities) context.get("gettester");
	    	tme.waitForTerminate();  
	        return tme.getOutput();
	    }
	}
}
