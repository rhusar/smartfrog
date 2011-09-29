package org.smartfrog.services.orchcomponent.policy;

import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.services.orchcomponent.desiredstate.DesiredEventHandler;
import org.smartfrog.services.orchcomponent.model.OrchConstants;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

public class IntegrityConstraintHandler implements DesiredEventHandler {	
	
	private Vector<Reference> policies;
	
	public IntegrityConstraintHandler(Vector<Reference> policies) {
		this.policies=policies;
	}
		
	public void handleDesiredEvent(ComponentDescription base) {
		for (Reference policy: policies) try{base.sfResolve(policy);}catch(Exception e){} 
	}
}
