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

package org.smartfrog.vast.architecture;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.avalanche.server.engines.sf.SFAdapter;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.vast.archive.ZipArchive;
import org.smartfrog.vast.archive.TarArchive;
import org.smartfrog.vast.architecture.CommandDispatcher.CommandController;
import org.jivesoftware.smack.packet.Packet;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;
import java.io.*;

/**
 * The environment builder class.
 */
public class EnvironmentConstructorImpl extends CompoundImpl implements EnvironmentConstructor {
	/**
	 * Reference to the avalanche server component.
	 */
	AvalancheServer refAvlServer = null;

	/**
	 * Quick access reference.
	 */
	VirtualMachineConfig VastController = null;

	/**
	 * The command controller.
	 */
	CommandController cmdCtrl = null;

	/**
	 * The list containing the configuration details for the physical machine.
	 */
	private ArrayList<PhysicalMachineConfig> listPhysicalMachines = new ArrayList<PhysicalMachineConfig>();

	/**
	 * The list containing the configuration details for the virtual machines.
	 */
	private ArrayList<VirtualMachineConfig> listVirtualMachines = new ArrayList<VirtualMachineConfig>();

	/**
	 * Compiled arguments for the helper to set the hostnames of all vitual machines for all of them.
	 */
	private String HostnameList = "";

	public EnvironmentConstructorImpl() throws RemoteException {

	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
	}

	protected void sfDeployWithChildren() throws SmartFrogDeploymentException {
		super.sfDeployWithChildren();

		sfLog().info("deploying with children");

		// iterate through the attributes of this description
		Enumeration e = sfContext().keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();

			if (key.equals(ATTR_PHYSICAL_MACHINES)) {
				// entries of the physical machines found
				ComponentDescription phyParent = (ComponentDescription) sfContext().get(key);
				if (phyParent != null) {

					// iterate through the phycial machine descriptions
					Enumeration phy = phyParent.sfContext().keys();
					while (phy.hasMoreElements()) {
						ComponentDescription phyObj = (ComponentDescription) phyParent.sfContext().get(phy.nextElement());
						if (phyObj != null) {
							// create a new physical machine object
							PhysicalMachineConfig conf = new PhysicalMachineConfig();

							// set the appropriate values
							try {
								// set the access mode
								resolveBasicMachineAttributes(phyObj, conf);

							} catch (SmartFrogResolutionException ex) {
								sfLog().error("error while resolving a physical machine description", ex);
								throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(ex);
							}

							this.listPhysicalMachines.add(conf);
						}
					}
				}
			} else if (key.equals(ATTR_VIRTUAL_MACHINES)) {
				// entries of the physical machines found
				ComponentDescription virtParent = (ComponentDescription) sfContext().get(key);
				if (virtParent != null) {

					// iterate through the phycial machine descriptions
					Enumeration virt = virtParent.sfContext().keys();
					while (virt.hasMoreElements()) {
						ComponentDescription virtObj = (ComponentDescription) virtParent.sfContext().get(virt.nextElement());
						if (virtObj != null) {
							// create a new physical machine object
							VirtualMachineConfig conf = new VirtualMachineConfig();

							// set the appropriate values
							try {
								resolveBasicMachineAttributes(virtObj, conf);

								conf.setAffinity((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_AFFINITY, true));
								conf.setSourceImage((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_SOURCE_IMAGE, true));
								conf.setDisplayName((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_DISPLAY_NAME, true));
								conf.setGuestUser((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_GUEST_USER, true));
								conf.setGuestPass((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_GUEST_PASS, true));
								conf.setToolsTimeout((Integer) virtObj.sfResolve(VirtualMachineConfig.ATTR_TOOLS_TIMEOUT, true));
								conf.setVastNetworkIP((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_VAST_NETWORK_IP, true));
								conf.setVastNetworkMask((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_VAST_NETWORK_MASK, true));
								conf.setHostMask((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_HOST_NETWORK_MASK, true));
								conf.setVastController((Boolean) virtObj.sfResolve(VirtualMachineConfig.ATTR_VAST_CONTROLLER, true));
								conf.setSUTPackage(virtObj.sfResolve(VirtualMachineConfig.ATTR_SUT_PACKAGE, "", false));

								// construct the hostname parameters
								HostnameList = String.format("-dns %s %s %s", conf.getDisplayName(), conf.getHostAddress(), HostnameList);

								if (conf.isVastController())
									VastController = conf;

							} catch (SmartFrogResolutionException ex) {
								sfLog().error("error while resolving a virtual machine description", ex);
								throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(ex);
							}

							this.listVirtualMachines.add(conf);
						}
					}
				}
			}
		}

		setRemainingValues();

		sfLog().info("successfully deployed with children");
	}

	/**
	 * Sets the remaining values of the virtual machines which can only be set after all descriptions have been resolved.
	 */
	private void setRemainingValues() {
		 for (VirtualMachineConfig virt : listVirtualMachines) {
			 for (PhysicalMachineConfig phy : listPhysicalMachines) {
				 if (virt.getAffinity().equals(phy.getHostAddress())) {
					 for (Argument arg : phy.getListArguments())
						if (arg.getName().equals("AVALANCHE_HOME"))
							virt.setHelperPathOnHostOS(String.format("%s/smartfrog/dist/vast/helper.jar", arg.getValue()));

					 break;
				 }
			 }

			 virt.setHostList(HostnameList);
		 }
	}

	/**
	 * Copies the template default.ini file to the package location and replaces the template indicators with the given ip.
	 * @param inSrc
	 * @param inDest
	 * @param inIP
	 */
	private void copyDefaultIni(File inSrc, File inDest, String inIP) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inSrc));
		BufferedWriter writer = new BufferedWriter(new FileWriter(inDest));
		String line = null;
		while ((line = reader.readLine()) != null) {
			writer.write(line.replace("[vast ip]", inIP));
		}
		reader.close();
		writer.close();
	}

	/**
	 * Prepares the SUT packages.
	 */
	private void prepareSUTPackages() throws SmartFrogException, RemoteException {
		String strBasePath = String.format("%s/temp/vast/", refAvlServer.getAvalancheHome());

		for (VirtualMachineConfig conf : listVirtualMachines) {
			try {
				File destSUT = null;
				File destVAST;
					
				if (!conf.isVastController()) {
					// copy the appropriate sut package to the sf distribution package
					destSUT = new File(strBasePath + "smartfrog/dist/vast/" + conf.getSUTPackage());
					FileSystem.fCopy(new File(strBasePath + "SUT/" + conf.getSUTPackage()), destSUT);

					// copy the vast test runner library to the sf dist package
					destVAST = new File(strBasePath + "smartfrog/dist/lib/vast-runner.jar");
					FileSystem.fCopy(new File(strBasePath + "lib/vast-runner.jar"), destVAST);
				} else {
					// copy the vast test controller library to the sf dist package
					destVAST = new File(strBasePath + "smartfrog/dist/lib/vast-controller.jar");
					FileSystem.fCopy(new File(strBasePath + "lib/vast-controller.jar"), destVAST);
				}

				// create the archive
				if (conf.getOS().equals("windows")) {
					// zip
					ZipArchive archive = new ZipArchive(strBasePath + conf.getSUTPackage() + ".zip");
					archive.create();
					archive.add(strBasePath + "smartfrog/");
					archive.close();

					conf.setSUTPackage(conf.getSUTPackage() + ".zip");
				} else {
					// tar
					TarArchive archive = new TarArchive(strBasePath + conf.getSUTPackage() + ".tar");
					archive.create();
					archive.add(strBasePath + "smartfrog/");
					archive.close();

					// gzip
					GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(strBasePath + conf.getSUTPackage() + ".tar.gz"));
					FileInputStream in = new FileInputStream(strBasePath + conf.getSUTPackage() + ".tar");
					byte[] buffer = new byte[2048];
					int bytes;
					while ((bytes = in.read(buffer, 0, buffer.length)) > 0)
						gzip.write(buffer, 0, bytes);
					in.close();
					gzip.close();

					conf.setSUTPackage(conf.getSUTPackage() + ".tar.gz");
				}

				// delete it so it wont be contained in the
				// following packages
				if (destSUT != null)
					if (destSUT.exists())
						destSUT.delete();

				if (destVAST.exists())
					destVAST.delete();
			} catch (Exception e) {
				e.printStackTrace();
				throw new SmartFrogException(e);
			}
		}
	}

	/**
	 * Resolves the basic attributes which are equal for physical and virtual machines.
	 *
	 * @param inMachineDesc The ComponentDescription with the attributes.
	 * @param inConf		The configuration where the attributes should be stored.
	 * @throws SmartFrogResolutionException
	 * @throws SmartFrogDeploymentException
	 */
	private void resolveBasicMachineAttributes(ComponentDescription inMachineDesc, PhysicalMachineConfig inConf) throws SmartFrogResolutionException, SmartFrogDeploymentException {
		// resolve the access modes
		ComponentDescription AModes = (ComponentDescription) inMachineDesc.sfContext().get(PhysicalMachineConfig.ATTR_ACCESS_MODES);
		resolveConnectionModes(AModes, inConf.getListAccessModes());

		// resolve the transfer modes
		ComponentDescription TModes = (ComponentDescription) inMachineDesc.sfContext().get(PhysicalMachineConfig.ATTR_TRANSFER_MODES);
		resolveConnectionModes(TModes, inConf.getListTransferModes());

		// resolve the arguments
		ComponentDescription Arguments = (ComponentDescription) inMachineDesc.sfContext().get(PhysicalMachineConfig.ATTR_ARGUMENTS);
		if (Arguments != null) {
			// for each argument
			Enumeration enumArgs = Arguments.sfContext().keys();
			while (enumArgs.hasMoreElements()) {
				// add the argument to the list
				ComponentDescription arg = (ComponentDescription) Arguments.sfContext().get(enumArgs.nextElement());

				Argument newArg = new Argument();
				newArg.setName((String) arg.sfResolve(PhysicalMachineConfig.ATTR_ARG_NAME, true));
				newArg.setValue((String) arg.sfResolve(PhysicalMachineConfig.ATTR_ARG_VALUE, true));

				inConf.getListArguments().add(newArg);
			}
		}

		// resolve the remaining unique attributes
		inConf.setHostAddress((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_HOST_ADDRESS, true));
		inConf.setArchitecture((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_ARCHITECTURE, true));
		inConf.setOS((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_OS, true));
		inConf.setPlatform((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_PLATFORM, true));
	}

	/**
	 * Resolves the connection modes of a component description.
	 *
	 * @param inModeList The list where the modes should be stored.
	 * @param inModes	The parent component description which contains the connection modes.
	 * @throws SmartFrogResolutionException
	 */
	private void resolveConnectionModes(ComponentDescription inModes, ArrayList<ConnectionMode> inModeList) throws SmartFrogResolutionException {
		if (inModes != null) {
			// for each connection mode
			Enumeration enumModes = inModes.sfContext().keys();
			while (enumModes.hasMoreElements()) {
				ComponentDescription Mode = (ComponentDescription) inModes.sfContext().get(enumModes.nextElement());

				// add an connection mode to the list
				ConnectionMode conMode = new ConnectionMode();

				conMode.setType(((String) Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_TYPE, true)).toLowerCase());
				conMode.setUser((String) Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_USERNAME, true));
				conMode.setPassword((String) Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_PASSWORD, true));
				conMode.setIsDefault(Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_IS_DEFAULT, false, false));

				inModeList.add(conMode);
			}
		}
	}

	public void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();

		sfLog().info("starting");

		// get the reference to the avalanche server
		refAvlServer = (AvalancheServer) sfResolve(ATTR_AVALANCHE, true);

		// register the listener with avalanche
		refAvlServer.addXMPPHandler(new VastListener(this));

		// create the command controller
		cmdCtrl = new CommandController(refAvlServer, sfLog());

		try {
			prepareSUTPackages();
		} catch (Exception e1) {
			throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e1);
		}

		// update/create the physical machines in the database
		updateMachines();

		// trigger setup
		ignitePhysicalHosts();

		sfLog().info("successfully started");
	}

	protected synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);

		// stop the virtual machines
		stopVirtualMachines();

		// give the daemon some time before killing it
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			sfLog().error(e);
		}

		// stop the daemons on the ignited machines
		stopPhysicalHosts();

		// give the daemon some time before killing it
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			sfLog().error(e);
		}
	}

	/**
	 * Stops the daemons running on the physical hosts.
	 */
	private void stopPhysicalHosts() {
		for (PhysicalMachineConfig phy : listPhysicalMachines) {
			try {
				SFAdapter.stopDaemon(phy.getHostAddress());
			} catch (Exception e) {
				sfLog().error("Error while stopping physical host: " + phy.getHostAddress(), e);
			}
		}
	}

	/**
	 * Sends a stop command to all virtual machines.
	 */
	private void stopVirtualMachines() {
		for (VirtualMachineConfig virt : listVirtualMachines) {
			if (checkPhysicalExistance(virt.getAffinity())) {
				try {
					refAvlServer.sendVMCommand(virt.getAffinity(), virt.getDisplayName(), "stop");
				} catch (Exception e) {
					sfLog().error("Error while trying to stop virtual machines", e);
				}
			}
		}
	}

	/**
	 * Machines will be created/updated according to the sf description file.
	 *
	 * @throws RemoteException
	 * @throws SmartFrogException
	 */
	private void updateMachines() throws RemoteException, SmartFrogException {
		// first add the physical hosts which should be ignited
		// to the database of avalanche
		for (PhysicalMachineConfig phy : listPhysicalMachines)
			updateMachine(phy);

		// then add the virtual hosts
		for (VirtualMachineConfig vm : listVirtualMachines)
			updateMachine(vm);
	}

	/**
	 * Insert the machine config into the avalanche database.
	 *
	 * @param inMachineConfig
	 * @throws RemoteException
	 * @throws SmartFrogException
	 */
	private void updateMachine(PhysicalMachineConfig inMachineConfig) throws RemoteException, SmartFrogException {
		// update the unique attributes
		if (inMachineConfig instanceof VirtualMachineConfig) {
			refAvlServer.updateHost(inMachineConfig.getHostAddress(),
				inMachineConfig.getArchitecture(),
				inMachineConfig.getPlatform(),
				inMachineConfig.getOS(),
				((VirtualMachineConfig) inMachineConfig).getVastNetworkIP());
		} else
			refAvlServer.updateHost(inMachineConfig.getHostAddress(),
				inMachineConfig.getArchitecture(),
				inMachineConfig.getPlatform(),
				inMachineConfig.getOS(),
				null);

		// clear the lists
		refAvlServer.clearAccessModes(inMachineConfig.getHostAddress());
		refAvlServer.clearTransferModes(inMachineConfig.getHostAddress());
		refAvlServer.clearArguments(inMachineConfig.getHostAddress());

		// add the access modes
		for (ConnectionMode mode : inMachineConfig.getListAccessModes()) {
			refAvlServer.addAccessMode(inMachineConfig.getHostAddress(),
				mode.getType(),
				mode.getUser(),
				mode.getPassword(),
				mode.getIsDefault());
		}

		// add the transfer modes
		for (ConnectionMode mode : inMachineConfig.getListTransferModes()) {
			refAvlServer.addTransferMode(inMachineConfig.getHostAddress(),
				mode.getType(),
				mode.getUser(),
				mode.getPassword(),
				mode.getIsDefault());
		}

		// add the arguments
		for (Argument arg : inMachineConfig.getListArguments()) {
			refAvlServer.addArgument(inMachineConfig.getHostAddress(),
				arg.getName(),
				arg.getValue());
		}
	}

	private void ignitePhysicalHosts() throws SmartFrogException, RemoteException {
		// construct the host name array
		sfLog().info("igniting hosts:");
		String[] hostNames = new String[listPhysicalMachines.size()];
		for (int i = 0; i < hostNames.length; ++i) {
			sfLog().info(listPhysicalMachines.get(i).getHostAddress());
			hostNames[i] = listPhysicalMachines.get(i).getHostAddress();
		}

		// ignite the hosts
		refAvlServer.igniteHosts(hostNames);
	}

	/**
	 * Checks whether the physical part of the environment is ready for the next step.
	 */
	private boolean checkPhysicalEnv() {
		for (PhysicalMachineConfig phy : listPhysicalMachines)
			if (!phy.isRunning())
				return false;

		sfLog().info("all physical hosts running");
		return true;
	}

	/**
	 * Checks whether the virtual part of the environment is ready for the next step.
	 */
	private boolean checkVirtualEnv() {
		for (VirtualMachineConfig vm : listVirtualMachines)
			if (!vm.isRunning())
				return false;

		sfLog().info("all virtual machines are running");
		return true;
	}

	/**
	 * A host started.
	 *
	 * @param inHost
	 */
	public void hostStarted(String inHost) {
		for (PhysicalMachineConfig phy : listPhysicalMachines) {
			if (phy.getHostAddress().equals(inHost)) {
				// set the machine to be running
				phy.setRunning(true);

				// check if all machines are running
				if (checkPhysicalEnv()) {
					// start the command control
					cmdCtrl.executeCommands(listVirtualMachines);
				}

				return;
			}
		}

		for (VirtualMachineConfig vm : listVirtualMachines) {
			if (vm.getHostAddress().equals(inHost)) {
				// set the vm to be running
				vm.setRunning(true);

				// check if all virtual machines are running
				if (checkVirtualEnv()) {
					// send go message to the controller
					try {
						// create the message
						XMPPEventExtension ext = new XMPPEventExtension();
						ext.setMessageType(MonitoringConstants.VAST_START);

						// send the message
						refAvlServer.sendXMPPExtension(VastController.getHostAddress(), ext);
					} catch (Exception e) {
						sfLog().error(e);
					}
				}

				return;
			}
		}
	}

	/**
	 * A host vanished from xmpp.
	 *
	 * @param inHost
	 */
	public void hostVanished(String inHost) {
		for (PhysicalMachineConfig phy : listPhysicalMachines) {
			if (phy.getHostAddress().equals(inHost)) {
				// set the machine to be stopped
				phy.setRunning(false);

				return;
			}
		}

		for (VirtualMachineConfig vm : listVirtualMachines) {
			if (vm.getHostAddress().equals(inHost)) {
				// set the vm to be stopped
				vm.setRunning(false);
				return;
			}
		}
	}

	/**
	 * Checks if a physical host is existing in the description data.
	 *
	 * @param inName Name of the host.
	 * @return True if it is, false if it is not existing.
	 */
	private boolean checkPhysicalExistance(String inName) {
		for (PhysicalMachineConfig phy : listPhysicalMachines)
			if (phy.getHostAddress().equals(inName))
				return true;
		return false;
	}

	/**
	 * Handles incoming VM messages.
	 *
	 * @param inPacket		  The packet containing the message.
	 * @param inPacketExtension The packet extension containing the relevant data.
	 */
	public void handleVMMessages(Packet inPacket, XMPPEventExtension inPacketExtension) {
		cmdCtrl.handleResponse(inPacket, inPacketExtension);
	}
}
