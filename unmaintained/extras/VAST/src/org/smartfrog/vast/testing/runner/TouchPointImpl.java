package org.smartfrog.vast.testing.runner;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.vast.testing.networking.messages.PublishedAttribute;

import java.rmi.RemoteException;

public class TouchPointImpl extends PrimImpl implements TouchPoint {
	private TestRunner refTestRunner = null;

	public TouchPointImpl() throws RemoteException {
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
	}

	public void touch(TestRunner inTR) throws RemoteException {
		refTestRunner = inTR;
		sfLog().info("Got touched by: " + inTR);
	}

	public void publish(Prim inSource, String inKey, String inValue) throws RemoteException, SmartFrogException {
		// set the attribute in the pc
		inSource.sfReplaceAttribute(inKey, inValue);

		// send the broadcast
		if (refTestRunner != null)
			refTestRunner.PublishAttribute(new PublishedAttribute(inSource.sfDeployedProcessName(), inKey, inValue));
		else
			sfLog().error("Need to be touched before being able to broadcast messages.");
	}
}
