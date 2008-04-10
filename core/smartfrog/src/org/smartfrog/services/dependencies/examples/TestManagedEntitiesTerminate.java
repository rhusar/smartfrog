package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;
import java.util.Enumeration;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.languages.sf.functions.BaseFunction;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class TestManagedEntitiesTerminate extends BaseFunction {
				
    protected Object doFunction() {
    	TestManagedEntities tme = (TestManagedEntities) context.get("gettester");
    	tme.waitForTerminate();  
        return tme.getOutput();
    }
}
