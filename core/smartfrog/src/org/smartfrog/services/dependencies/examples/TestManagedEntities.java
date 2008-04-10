package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.languages.sf.functions.BaseFunction;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class TestManagedEntities extends CompoundImpl implements Compound, WaitToFinish {		
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
