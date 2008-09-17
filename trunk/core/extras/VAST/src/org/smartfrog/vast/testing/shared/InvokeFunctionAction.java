package org.smartfrog.vast.testing.shared;

import java.rmi.RemoteException;

public interface InvokeFunctionAction extends SUTAction {
	public static final String ATTR_FUNCTION_NAME = "FunctionName";
	public static final String ATTR_PARAMETERS = "Parameters";

	public String getFunctionName() throws RemoteException; 
}
