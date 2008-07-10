/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.vast.testing.controller;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.vast.testing.networking.BroadcastCommunicator;
import org.smartfrog.vast.testing.networking.messages.MessageCallback;

import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

public class TestControllerImpl extends PrimImpl implements TestController, MessageCallback {
	/**
	 * home directory of the smartfrog daemon
	 */
	private String sfHome;
	/**
	 * TTL for MCC.
	 */
	private int TTL;
	/**
	 * Port for MCC.
	 */
	private int Port;
	/**
	 * NIC for MCC.
	 */
	private String NIC;
	/**
	 * Multicast address for MCC.
	 */
	private InetAddress BroadcastAddress;

	/**
	 * The multicast communicator class.
	 */
	private BroadcastCommunicator MCC;

	public TestControllerImpl() throws RemoteException {

	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		// resolve the home dir of the smartfrog daemon
		sfHome = (String) sfResolve(ATTR_SFHOME, true);

		// resolve the mcc attributes
		TTL = (Integer) sfResolve(ATTR_TTL, true);
		Port = (Integer) sfResolve(ATTR_PORT, true);
		NIC = (String) sfResolve(ATTR_NIC, true);
		String multi = (String) sfResolve(ATTR_BROADCAST_ADDRESS, true);
		try {
			BroadcastAddress = InetAddress.getByName(multi);
		} catch (UnknownHostException e) {
			throw SmartFrogException.forward(e);
		}
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();

		try {
			// set up the mcc
			MCC = new BroadcastCommunicator(BroadcastAddress, NIC, Port);
			MCC.addCallback(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void OnHelloBroadcast(String inFrom) {
		
	}
}
