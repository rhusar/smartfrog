package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 */
public class PlainStateComponent extends ThreadedState implements Prim {
	
	public PlainStateComponent() throws RemoteException {super();}  
	
	protected boolean requireThread()  throws StateComponentTransitionException { return false; }
	protected boolean threadBody() throws StateComponentTransitionException { return true; }
}


