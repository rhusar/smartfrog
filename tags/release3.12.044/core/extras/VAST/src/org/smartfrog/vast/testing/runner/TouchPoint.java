package org.smartfrog.vast.testing.runner;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.vast.testing.networking.messages.PublishedAttribute;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TouchPoint extends Remote {
	/**
	 * Sets the reference to the test runner.
	 * @param inTR The test runner that touches this point.
	 * @throws RemoteException
	 */
	public void touch(TestRunner inTR) throws RemoteException;

	/**
	 * Publishes an attribute.
	 * @param inSource The component to which the attribute belongs.
	 * @param inKey The attribute key.
	 * @param inValue The attribute's value.
	 * @throws RemoteException
	 * @throws SmartFrogException
	 */
	public void publish(Prim inSource, String inKey, String inValue) throws RemoteException, SmartFrogException;
}
