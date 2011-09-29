package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;

public class CreateNewChild extends RComponentImpl implements RComponent {

	private static final String CD_ATTR = "cd";
	private static final String CHILD_ATTR = "child";
	private static final String ATTR_ATTR = "attr";
	private ComponentDescription cd;
	
	public CreateNewChild() throws RemoteException {
		super();
	}

	@Override
	public synchronized void sfDeploy() throws SmartFrogException,
			RemoteException {
		super.sfDeploy();
		cd = (ComponentDescription)sfResolve(CD_ATTR);
	}

	@Override
	public synchronized void sfStart() throws SmartFrogException,
			RemoteException {
		super.sfStart();
		if(!sfContainsAttribute(CHILD_ATTR)) {
			sfCreateNewChild(CHILD_ATTR, cd, null);
		}
		if(!sfContainsAttribute(ATTR_ATTR)) {
			sfAddAttribute(ATTR_ATTR, "value");
		}

	}
	
	
	

}
