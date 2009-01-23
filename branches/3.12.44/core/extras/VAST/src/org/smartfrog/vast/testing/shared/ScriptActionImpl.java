package org.smartfrog.vast.testing.shared;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.vast.testing.networking.messages.VastMessage;
import org.smartfrog.vast.testing.networking.messages.StartSfScript;

import java.rmi.RemoteException;

public class ScriptActionImpl extends PrimImpl implements ScriptAction {
	String 	Host,
			ScriptName,
			Name,
			ProcessName;
	int		Wait;

	public ScriptActionImpl() throws RemoteException {
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		sfLog().info("deploying SUTActionImpl");

		// resolve stuff
		Host = (String) sfResolve(ATTR_HOST, true);
		ScriptName = (String) sfResolve(ATTR_SCRIPT_NAME, true);
		Wait = (Integer) sfResolve(ATTR_WAIT, true);
		Name = (String) sfResolve(ATTR_NAME);
		ProcessName = (String) sfResolve(ATTR_PROCESS_NAME, true);
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
	}

	public String getProcessName() throws RemoteException {
		return ProcessName;
	}

	public String getScriptName() throws RemoteException {
		return ScriptName;
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

	public VastMessage getActionMessage() throws RemoteException {
		return new StartSfScript(null, ProcessName, ScriptName);
	}
}
