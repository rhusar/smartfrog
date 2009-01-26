package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.sfcore.logging.LogSF;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CreateVirtualMachineCommand extends BaseCommand {
	public CreateVirtualMachineCommand(MessageDispatcher refMD, LogSF inLog) {
		super(refMD, VMWareConstants.VM_CMD_CREATE, inLog);
		TimeOut = 600 * 1000;
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
		ext.getPropertyBag().put("vmname", null);

		// fill the propertybag with thr necessary parameters
		ext.getPropertyBag().put(VMWareConstants.VM_CREATE_MASTER, inCfg.getSourceImage());
		ext.getPropertyBag().put(VMWareConstants.VM_CREATE_NAME, inCfg.getDisplayName());
		ext.getPropertyBag().put(VMWareConstants.VM_CREATE_USER, inCfg.getGuestUser());
		ext.getPropertyBag().put(VMWareConstants.VM_CREATE_PASS, inCfg.getGuestPass());

		return ext;
	}
}
