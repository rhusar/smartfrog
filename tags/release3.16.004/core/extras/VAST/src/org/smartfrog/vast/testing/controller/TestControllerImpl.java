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
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.vast.testing.networking.BroadcastCommunicator;
import org.smartfrog.vast.testing.networking.messages.MessageCallback;
import org.smartfrog.vast.testing.networking.messages.StartSfScript;
import org.smartfrog.vast.testing.shared.TestSuite;
import org.smartfrog.vast.testing.shared.SUTTestSequence;
import org.smartfrog.vast.testing.shared.SUTAction;
import org.smartfrog.vast.testing.shared.SUTAttribute;
import org.smartfrog.avalanche.client.monitor.xmpp.AvlXMPPListener;
import org.smartfrog.services.xmpp.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;

import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TestControllerImpl extends PrimImpl implements TestController, MessageCallback, LocalXmppPacketHandler {
	/**
	 * home directory of the smartfrog daemon
	 */
	private String sfHome;

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
	 * Reference to the avl listener.
	 */
	private AvlXMPPListener refAvlListener;

	/**
	 * The multicast communicator class.
	 */
	private BroadcastCommunicator MCC;

	/**
	 * The attributes that have been published up till now.
	 */
	private HashMap<String, String> PublishedAttributes = new LinkedHashMap<String, String>();

	/**
	 * The test suite.
	 */
	private TestSuite testSuite;

	public TestControllerImpl() throws RemoteException {

	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		// resolve the home dir of the smartfrog daemon
		sfHome = (String) sfResolve(ATTR_SFHOME, true);

		// resolve the mcc attributes
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

		// get the reference to the avl listener
		refAvlListener = (AvlXMPPListener) sfResolve(ATTR_AVL, true);

		// register with the listener
		refAvlListener.registerPacketHandler(this);

		// resolve the test suite
		testSuite = (TestSuite) sfResolve(ATTR_TEST_SUITE, true);
	}

	/**
	 * Run the tests.
	 */
	private void runTests() {
		try {
			// execute each sequence
			for (SUTTestSequence seq : testSuite.getTestSequences()) {
				// execute the actions
				for (SUTAction action : seq.getActions()) {
					MCC.sendMessage(InetAddress.getByName(action.getHost()), action.getActionMessage());
					if (action.getWait() > 0) {
						try {
							Thread.sleep(action.getWait() * 1000);
						} catch (Exception e) {
							sfLog().error(e);
						}
					}
				}

				// wait
				if (seq.getWait() > 0) {
					try {
							Thread.sleep(seq.getWait() * 1000);
						} catch (Exception e) {
							sfLog().error(e);
						}
				}

				// check the result
				boolean failure = false;
				for (SUTAttribute attr : seq.getResult().getAttributes()) {
					failure = (!attr.getValue().equals(PublishedAttributes.get(String.format("%s:%s:%s", attr.getHost(), attr.getProcess(), attr.getName()))));
				}

				if (failure && !seq.getExpectFailure())
					throw new SmartFrogException("Unexpected failure in sequence " + seq.getName());
				else {
					// sequence passed, TODO: log
					sfLog().info("Sequence passed: " + seq.getName());
				}
			}
		} catch (Exception e) {
			sfLog().error(e);
		}
	}

	/**
	 * Stop the tests.
	 */
	private void stopTests() {

	}

	public PacketFilter getFilter() {
		return new XMPPEventFilter();
	}

	public void processPacket(Packet packet) {
		sfLog().info("TestControllerImpl: Received packet: " + packet.getFrom() + ": " + packet.toXML());

        // get the extension
        XMPPEventExtension ext = (XMPPEventExtension) packet.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
        if (ext != null)
        {
            sfLog().info("Received monitoring constant: " + ext.getMessageType());
			switch (ext.getMessageType()) {
				case MonitoringConstants.VAST_START:
					runTests();
					break;
				case MonitoringConstants.VAST_STOP:
					stopTests();
					break;
				default: break;
			}
		}
	}

	public void OnHelloBroadcast(InetAddress inFrom) {

	}

	public void OnStartSUTDaemon() {
		
	}

	public void OnCutNetworkConnection(int inIndex, String inIP) {

	}

	public void OnSetupNetworkConnection(int inIndex, String inIP, String inMask) {
		
	}

	public void OnRunSfScript(String inScript, Map<String, String> inEnv) {

	}

	public void OnStartSfScript(String inScript, String inProcessName, Map<String, String> inEnv) {
		
	}

	public void OnInvokeFunction(String inFunctionName, String inProcessName, Vector inParameters) {

	}

	public void OnPublishedAttribute(InetAddress inHost, String inProcessName, String inKey, String inValue) {
		PublishedAttributes.put(String.format("%s:%s:%s", inHost, inProcessName, inKey), inValue);
		sfLog().info(inHost + ", " + inProcessName + " published attribute " + inKey + "=" + inValue);
	}
}
