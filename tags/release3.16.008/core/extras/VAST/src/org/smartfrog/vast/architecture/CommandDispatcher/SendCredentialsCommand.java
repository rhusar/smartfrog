package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.sfcore.logging.LogSF;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SendCredentialsCommand extends BaseCommand {
	public SendCredentialsCommand(MessageDispatcher refMD, LogSF inLog) {
		super(refMD, VMWareConstants.VM_CMD_SET_GUEST_CRED, inLog);
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
		ext.getPropertyBag().put(VMWareConstants.VM_SET_GUEST_CRED_USER, inCfg.getGuestUser());
		ext.getPropertyBag().put(VMWareConstants.VM_SET_GUEST_CRED_PASS, inCfg.getGuestPass());

		return ext;
	}
}
