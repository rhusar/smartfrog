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

package org.smartfrog.vast.testing.runner;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.vast.archive.TarArchive;
import org.smartfrog.vast.archive.ZipArchive;
import org.smartfrog.vast.archive.BaseArchive;
import org.smartfrog.vast.testing.networking.BroadcastCommunicator;
import org.smartfrog.vast.testing.networking.messages.MessageCallback;
import org.smartfrog.vast.testing.networking.messages.HelloBroadcast;

import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestRunnerImpl extends PrimImpl implements TestRunner, MessageCallback {
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
	 * The multicast communicator class.
	 */
	private BroadcastCommunicator MCC;

	public TestRunnerImpl() throws RemoteException {

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

		setupSUT();
	}

	public void setupSUT() throws RemoteException, SmartFrogException {
		// create a directory for vast
		File vastDir = new File(sfHome + "/SUT");
		if (!vastDir.exists())
			vastDir.mkdir();

		// extract the daemon to the sut folder
		String daemon = (new File(sfHome))        // smartfrog/dist
							.getParentFile()	// smartfrog
							.getParentFile()	//
							.getPath() + "release";

		BaseArchive arch;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			arch = new ZipArchive(daemon + ".zip");
		} else {
			arch = new TarArchive(daemon + ".tar");
		}
		try {
			arch.extract(vastDir.getPath());
		} catch (IOException e) {
			throw SmartFrogException.forward("Error while extracting sfDaemon", e);
		}

		// extract the content of the SUT package
		try {
			File SUT = new File(sfHome + "/vast");
			for (File file : SUT.listFiles()) {
				if (file.getName().endsWith(".tar")) {
					TarArchive tar = new TarArchive(file.getPath());
					tar.extract(vastDir.getPath());
				} else if (file.getName().endsWith(".zip")) {
					ZipArchive zip = new ZipArchive(file.getPath());
					zip.extract(vastDir.getPath());
				}
			}
		} catch (IOException e) {
			sfLog().error("Error while extracting SUT packages.", e);
			throw SmartFrogException.forward(e);
		}

		// set up the multicast communicator and register the callback
		try {
			MCC = new BroadcastCommunicator(BroadcastAddress, NIC, Port);
			MCC.addCallback(this);
		} catch (Exception e) {
			e.printStackTrace();
			sfLog().error("Error while trying to setup MCC.", e);
			throw SmartFrogException.forward(e);
		}

		try {
			// test loop
			while (true) {
				// send test message
				MCC.sendMessage(new HelloBroadcast());

				Thread.sleep(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void OnHelloBroadcast(String inFrom) {
		sfLog().info("Hello multicast from: " + inFrom);
	}
}
