package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

public class Junkprim extends PrimImpl implements Prim {

	public Junkprim() throws RemoteException{}
	public synchronized void sfStart() throws RemoteException, SmartFrogException {
		super.sfStart();
		System.out.println("Blargh!");
	}
}
