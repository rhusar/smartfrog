package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.vast.architecture.Argument;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class ExecuteInGuestCommand extends BaseCommand {
	public ExecuteInGuestCommand(MessageDispatcher refMD, LogSF inLog) {
		super(refMD, VMWareConstants.VM_CMD_EXECUTE, inLog);
	}

	public XMPPEventExtension composeMessage(VirtualMachineConfig inCfg) {
		XMPPEventExtension ext = new XMPPEventExtension();
		try {
            ext.setHost(InetAddress.getLocalHost().toString());
        } catch (UnknownHostException e) {
            ext.setHost("");
        }
        ext.setMessageType(MonitoringConstants.VM_MESSAGE);

        // set the command
        ext.getPropertyBag().put("vmcmd", Command);

		// set the path (used like an identifier)
		ext.getPropertyBag().put("vmname", inCfg.getDisplayName());

		// parameters

		String gwAddr = inCfg.getHostAddress().substring(0, inCfg.getHostAddress().lastIndexOf(".")) + ".1";
		ext.getPropertyBag().put(VMWareConstants.VM_EXECUTE_PARAM, String.format("-jar /tmp/helper.jar -nic %s %s gw %s -nic %s %s -hname %s %s",
																					inCfg.getHostAddress(),
																					inCfg.getHostMask(),
																					gwAddr,
																					inCfg.getVastNetworkIP(),
																					inCfg.getVastNetworkMask(),
																					inCfg.getDisplayName(),
																					inCfg.getHostList()));

		for (Argument arg : inCfg.getListArguments())
			if (arg.getName().equals("JAVA_HOME"))
				ext.getPropertyBag().put(VMWareConstants.VM_EXECUTE_CMD, String.format("%s/bin/java", arg.getValue()));

		return ext;
	}

	/**
	 * Pings a host.
	 * @param inHost Address to ping.
	 * @return True if the ping was successful, false otherwise.
	 */
	private boolean ping(String inHost) {
		ProcessBuilder pb;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			pb = new ProcessBuilder("ping", inHost);
		}
		else {
			pb = new ProcessBuilder("ping", "-c", "4", inHost);
		}

		try {
			Process p = pb.start();
			p.waitFor();
			return (p.exitValue() == 0);
		} catch (Exception e) {
			Log.error(e);
		}

		return false;
	}

	public void success(VirtualMachineConfig inCfg) {
		// stop timer
		inCfg.stopTimer();

		try {
			// try to ping the machine for 5 * 22 seconds
			for (int i = 0; i < 2; ++i) {
				if (ping(inCfg.getHostAddress())) {
					 // vast helper executed, now ignite the virtual machines
					// with the appropriate package (sf + test runner + SUT)
					refMD.getRefAvl().igniteHosts(	new String[]{inCfg.getHostAddress()},
													String.format("%s/temp/vast/%s", refMD.getRefAvl().getAvalancheHome(), inCfg.getSUTPackage()),
													String.format("%s/temp/vast/sfinstaller.vm", refMD.getRefAvl().getAvalancheHome()));
					return;
				} else
					Log.info("Ping to " + inCfg.getHostAddress() + " failed. Retrying.");
			}

			if (inCfg.getNetworkSetupHelperTries() < 1) {
				Log.error("Error: virtual machine " + inCfg.getDisplayName() + " not reachable. Retrying network setup helper.");

				inCfg.setNetworkSetupHelperTries(inCfg.getNetworkSetupHelperTries() + 1);
				
				// copy the helper into the vm again
				failure(inCfg);
			} else {
				Log.error("Error: virtual machine " + inCfg.getDisplayName() + " not reachable. Restarting virtual machine.");

				// exploit!
				inCfg.setCurrentCommand(NextSuccess);
				NextSuccess.execute(inCfg);
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}
}
