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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.vast.archive.TarArchive;
import org.smartfrog.vast.archive.ZipArchive;
import org.smartfrog.vast.archive.BaseArchive;
import org.smartfrog.vast.testing.networking.BroadcastCommunicator;
import org.smartfrog.vast.testing.networking.messages.MessageCallback;
import org.smartfrog.vast.testing.networking.messages.PublishedAttribute;
import org.smartfrog.vast.testing.shared.LogFromStreamThread;
import org.smartfrog.vast.helper.Helper;
import org.smartfrog.vast.helper.HelperFactory;

import java.rmi.RemoteException;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.lang.reflect.Method;

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
	private BroadcastCommunicator BCC;
	/**
	 * Base directory of the SUT daemon.
	 */
	private File SUTBaseDir;

	/**
	 * Directory where the vast files can be found.
	 */
	private File VastDir;
	/**
	 * Address the sfDaemon of the SUT should be bount to.
	 */
	private String SUTNetAddress;
	/**
	 * The port the sfDaemon of the test runner should listen on.
	 */
	private int SFDaemonPort;

	private Map<String, String> SUTEnv;

	/**
	 * Helper interface to control the nics.
	 */
	private Helper nicHelper;

	private boolean SUTDaemonRunning = false;

	private ProcessCompound SUTDaemon = null;

	public TestRunnerImpl() throws RemoteException {

	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		// resolve the home dir of the smartfrog daemon
		sfHome = (String) sfResolve(ATTR_SFHOME, true);

		// create a directory for vast
		VastDir = new File(sfHome + "/SUT");
		if (!VastDir.exists())
			VastDir.mkdir();

		// set the SUT base dir
		SUTBaseDir = new File(VastDir.getPath() + "/smartfrog/dist");

		// resolve the mcc attributes
		Port = (Integer) sfResolve(ATTR_PORT, true);
		NIC = (String) sfResolve(ATTR_NIC, true);
		SUTNetAddress = (String) sfResolve(ATTR_SUT_NET_ADDRESS, true);
		SFDaemonPort = (Integer) sfResolve(ATTR_DAEMON_PORT, true);

		String multi = (String) sfResolve(ATTR_BROADCAST_ADDRESS, true);
		try {
			BroadcastAddress = InetAddress.getByName(multi);
		} catch (UnknownHostException e) {
			throw SmartFrogException.forward(e);
		}

		// get the helper
		nicHelper = HelperFactory.getHelper(System.out);
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();

		// setup the sut env
		try {
			SUTEnv = new HashMap<String, String>(System.getenv());
			SUTEnv.put("SFSERVERHOSTNAME", SUTNetAddress);
			String oldPath = SUTEnv.get("PATH").replace(SUTEnv.get("SFHOME") + "/bin", ".");
			SUTEnv.put("SFHOME", SUTBaseDir.getPath());
			SUTEnv.put("PATH", String.format("%s/bin:%s", SUTBaseDir.getPath(), oldPath));
			SUTEnv.remove("CLASSPATH");
		} catch (Exception e) {
			sfLog().trace(e);
			throw SmartFrogException.forward(e);
		}

		setupSUT();
	}

	public void setupSUT() throws RemoteException, SmartFrogException {
		// extract the daemon to the sut folder
		String daemon = (new File(sfHome))        	// smartfrog/dist
							.getParentFile()		// smartfrog
							.getParentFile()		//
							.getPath() + "/release";

		BaseArchive arch;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			arch = new ZipArchive(daemon + ".zip");
		} else {
			arch = new TarArchive(daemon + ".tar");
		}
		try {
			arch.extract(VastDir.getPath());
		} catch (IOException e) {
			throw SmartFrogException.forward("Error while extracting sfDaemon", e);
		}

		// extract the content of the SUT package
		try {
			File SUT = new File(sfHome + "/vast");
			for (File file : SUT.listFiles()) {
				if (file.getName().endsWith(".tar")) {
					arch = new TarArchive(file.getPath());
				} else {
					arch = new ZipArchive(file.getPath());
				}
				arch.extract(SUTBaseDir.getPath());
			}
		} catch (IOException e) {
			sfLog().error("Error while extracting SUT packages.", e);
			throw SmartFrogException.forward(e);
		}

		// set up the multicast communicator and register the callback
		try {
			BCC = new BroadcastCommunicator(BroadcastAddress, NIC, Port);
			BCC.addCallback(this);
		} catch (Exception e) {
			e.printStackTrace();
			sfLog().error("Error while trying to setup BCC.", e);
			throw SmartFrogException.forward(e);
		}

		// start the SUT daemon
		OnStartSUTDaemon();
	}

	public void OnHelloBroadcast(InetAddress inFrom) {

	}

	public void OnRunSfScript(String inScript, Map<String, String> inEnv) {
		// path to the sfRun script
		String sfRun = SUTBaseDir.getPath() + "/bin/sfRun";
		Map<String, String> env = (inEnv == null ? SUTEnv : inEnv);

		try {
			// execute the script
			String cmd = String.format("%s %s", sfRun, inScript);
			sfLog().info("Executing: " + cmd);
			Process ps = Runtime.getRuntime().exec(cmd, envToStrings(env), SUTBaseDir);

			printOutput(ps);
		} catch (Exception e) {
			sfLog().error("Error while executing sf script with sfRun.", e);
		}
	}

	/**
	 * Converts the environment variables map to a string array suitable for <code>exec()</code>.
	 * @param inEnv
	 * @return
	 */
	private String[] envToStrings(Map<String, String> inEnv) {
		if (inEnv == null)
			return null;

		String[] array = new String[inEnv.size()];
		int i = 0;
		for (String key : inEnv.keySet()) {
			array[i] = String.format("%s=%s", key, inEnv.get(key));
			++i;
		}
		return array;
	}

	public synchronized void OnStartSUTDaemon() {
		sfLog().info("OnStartSUTDaemon()");
		if (!SUTDaemonRunning) {
			try {
				// environment variables for the SUT daemon
				String cmd = String.format("%s/bin/sfDaemon -J -Dorg.smartfrog.sfcore.processcompound.sfRootLocatorBindAddress=%s -p %d -headless",
												SUTBaseDir.getPath(),
												SUTNetAddress,
												SFDaemonPort);

				sfLog().info("Executing: " + cmd);
				Process ps = Runtime.getRuntime().exec(cmd, envToStrings(SUTEnv), SUTBaseDir.getParentFile());

				printOutput(ps);

				// ping the daemon for 2 minutes
				// once it's reachable continue
				for(int i = 0; i < 120; ++i)
				{
					try {
						SUTDaemon = SFProcess.sfSelectTargetProcess(SUTNetAddress, null);
						SUTDaemon.sfPing(this);
						break;
					} catch (Exception e) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
						}
					}

					if (i == 120) {
						sfLog().error("Daemon still not running after two minutes.");
						// TODO: send error message or implement error behaviour
						return;
					}
				}

				SUTDaemonRunning = true;

				// start the touchpoint
				OnStartSfScript("org/smartfrog/vast/testing/runner/TouchPoint.sf", "touchpoint", SUTEnv);

				// wait for the touchpoint
				TouchPoint tp;
				for(int i = 0; i < 120; ++i)
				{
					try {
						// resolve the touchpoint
						tp = (TouchPoint) SUTDaemon.sfResolve("touchpoint", true);

						// touch the touchpoint so the SUT knows how to send broadcasts
						tp.touch(this);
						
						break;
					} catch (Exception e) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
						}
					}

					if (i == 120) {
						sfLog().error("Touchpoint still not available after two minutes.");
						// TODO: send error message or implement error behaviour
						return;
					}
				}

				// start avalanche
				OnStartSfScript("org/smartfrog/services/quartz/monitor/monitor.sf", "cpumonitor", SUTEnv);
				OnStartSfScript("org/smartfrog/avalanche/client/monitor/xmpp/avalanche.sf", "avalanche", SUTEnv);

			} catch (Exception e) {
				sfLog().error("Error while starting SUT sfDaemon.", e);
			}
		}
	}

	public void OnStartSfScript(String inScript, String inProcessName, Map<String, String> inEnv) {
		// path to the sfStart script
		String sfStart = SUTBaseDir.getPath() + "/bin/sfStart";
		Map<String, String> env = (inEnv == null ? SUTEnv : inEnv);

		try {
			// execute the script
			String cmd = String.format("%s %s %s %s", sfStart, SUTNetAddress, inProcessName, inScript);
			sfLog().info("Executing: " + cmd);
			Process ps = Runtime.getRuntime().exec(	cmd,
													envToStrings(env),
													SUTBaseDir);

			printOutput(ps);

		} catch (Exception e) {
			sfLog().error("Error while executing sf script with sfStart.", e);
		}
	}

	private void printOutput(Process inPs) {
		// read the output and the error stream
		// doing this asynchronously prevents blocking
		// of the process
		try {
			LogFromStreamThread rtOut = new LogFromStreamThread(inPs.getInputStream(), sfLog());
			LogFromStreamThread rtErr = new LogFromStreamThread(inPs.getErrorStream(), sfLog());
			rtOut.start();
			rtErr.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void OnCutNetworkConnection(int inIndex, String inIP) {
		// get the nic names
		ArrayList<String> nicNames = nicHelper.retrieveNICNames();

		// cut the connection
		nicHelper.cutNetworkConnection(nicNames.get(inIndex), inIP);
	}

	public void OnSetupNetworkConnection(int inIndex, String inIP, String inMask) {
		// get the nic names
		ArrayList<String> nicNames = nicHelper.retrieveNICNames();

		// setup the connection
		nicHelper.setNetworkAddress(nicNames.get(inIndex), inIP, inMask);
	}

	public void OnInvokeFunction(String inFunctionName, String inProcessName, Vector inParameters) {
		try {
			// aquire the process
			Object process = SUTDaemon.sfResolve(inProcessName, true);

			// find the method
			Method methods[] = process.getClass().getMethods();
			for (Method m : methods) {
				if (m.getName().equals(inFunctionName)) {
					// name matching, see if this is the correct overloaded one
					// by matching the parameters
					boolean bMatching = true;
					for (int i = 0; i < m.getParameterTypes().length; ++i) {
						if (!m.getParameterTypes()[i].equals(inParameters.get(i))) {
							bMatching = false;
							break;
						}
					}

					if (bMatching) {
						// matching function found, invoke it
						m.invoke(inFunctionName, inParameters);
						return;
					}
				}
			}
		} catch (Exception e) {
			sfLog().error(e);
		}
	}

	public void OnPublishedAttribute(InetAddress inHost, String inProcessName, String inKey, String inValue) {
		// to nothing
	}

	public void PublishAttribute(PublishedAttribute inAttrMsg) throws RemoteException {
		try {
			BCC.sendMessage(null, inAttrMsg);
		} catch (IOException e) {
			sfLog().error(e);
		}
	}
}
