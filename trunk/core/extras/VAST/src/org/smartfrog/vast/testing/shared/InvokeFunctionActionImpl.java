package org.smartfrog.vast.testing.shared;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.vast.testing.networking.messages.VastMessage;
import org.smartfrog.vast.testing.networking.messages.InvokeFunction;

import java.rmi.RemoteException;
import java.util.Vector;

public class InvokeFunctionActionImpl extends PrimImpl implements InvokeFunctionAction {
	String 	Host,
			Name,
			FunctionName,
			ProcessName;
	int		Wait;
	Vector Parameters;

	public InvokeFunctionActionImpl() throws RemoteException {
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		// resolve stuff
		Host = (String) sfResolve(ATTR_HOST, true);
		FunctionName = (String) sfResolve(ATTR_FUNCTION_NAME, true);
		Wait = (Integer) sfResolve(ATTR_WAIT, true);
		Name = (String) sfResolve(ATTR_NAME);
		ProcessName = (String) sfResolve(ATTR_PROCESS_NAME, true);

		Parameters = (Vector) sfResolve(ATTR_PARAMETERS, true);
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
	}

	public VastMessage getActionMessage() throws RemoteException {
		return new InvokeFunction(FunctionName, ProcessName, Parameters);
	}

	public String getHost() throws RemoteException {
		return Host;
	}

	public String getName() throws RemoteException {
		return Name;
	}

	public int getWait() throws RemoteException {
		return Wait;
	}

	public String getFunctionName() throws RemoteException {
		return FunctionName;
	}

	public String getProcessName() throws RemoteException {
		return ProcessName;
	}
}
