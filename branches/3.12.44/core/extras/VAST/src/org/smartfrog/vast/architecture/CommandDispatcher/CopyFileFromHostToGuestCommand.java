package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.vast.architecture.Argument;
import org.smartfrog.sfcore.logging.LogSF;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CopyFileFromHostToGuestCommand extends BaseCommand {
	public CopyFileFromHostToGuestCommand(MessageDispatcher refMD, LogSF inLog) {
		super(refMD, VMWareConstants.VM_CMD_COPY_HOST_TO_GUEST, inLog);
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
		for (Argument arg : inCfg.getListArguments())
			if (arg.getName().equals("AVALANCHE_HOME"))
				ext.getPropertyBag().put(VMWareConstants.VM_COPY_HTOG_DEST, String.format("%s/helper.jar", arg.getValue()));

		ext.getPropertyBag().put(VMWareConstants.VM_COPY_HTOG_SOURCE, inCfg.getHelperPathOnHostOS());

		return ext;
	}
}
