package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.vmware.VMWareConstants;
import org.smartfrog.sfcore.logging.LogSF;

public abstract class BaseCommand implements Command {
	protected Command NextSuccess, NextFailure;
	protected MessageDispatcher refMD = null;
	protected String Command;
	protected LogSF Log;
	protected int TimeOut;

	protected BaseCommand(MessageDispatcher refMD, String inCmd, LogSF inLog) {
		this.refMD = refMD;
		this.Command = inCmd;
		Log = inLog;
		TimeOut = 120 * 1000;
	}

	public void handlePacket(VirtualMachineConfig inCfg, XMPPEventExtension inExt) {
		if (inExt.getPropertyBag().get(VMWareConstants.VMCMD).equals(Command)) {
			if (inExt.getPropertyBag().get(VMWareConstants.VMRESPONSE).equals("success")) {
			    success(inCfg);
			} else {
				failure(inCfg);
			}
		}
	}

	public void execute(VirtualMachineConfig inCfg) {
		// stop the old timer
		inCfg.stopTimer();

		// set the timer
		inCfg.setTimer(TimeOut);

		// send the message
		refMD.sendMessage(inCfg, this);
	}

	public void failure(VirtualMachineConfig inCfg) {
		// stop timer
		inCfg.stopTimer();
		
		// go to failure
		inCfg.setCurrentCommand(NextFailure);
		NextFailure.execute(inCfg);
	}

	public Command getNextOnFailure() {
		return NextFailure;
	}

	public Command getNextOnSuccess() {
		return NextSuccess;
	}

	public void setNextOnFailure(Command inCmd) {
		NextFailure = inCmd;
	}

	public void setNextOnSuccess(Command inCmd) {
		NextSuccess = inCmd;
	}

	public void success(VirtualMachineConfig inCfg) {
		// stop timer
		inCfg.stopTimer();

		// go to success
		inCfg.setCurrentCommand(NextSuccess);
		NextSuccess.execute(inCfg);
	}

	public void timeOut(VirtualMachineConfig inCfg) {
		Log.info("timeout " + inCfg.getCurrentCommand());

		// retry
		//this.execute(inCfg);

		// goto failure behaviour
		failure(inCfg);
	}
}
