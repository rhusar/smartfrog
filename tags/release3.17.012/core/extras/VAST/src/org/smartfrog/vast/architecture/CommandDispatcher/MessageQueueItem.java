package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.vast.architecture.VirtualMachineConfig;

public class MessageQueueItem {
	private Command Cmd;
	private VirtualMachineConfig Config;

	public MessageQueueItem(Command cmd, VirtualMachineConfig config) {
		Cmd = cmd;
		Config = config;
	}

	public Command getCmd() {
		return Cmd;
	}

	public void setCmd(Command cmd) {
		Cmd = cmd;
	}

	public VirtualMachineConfig getConfig() {
		return Config;
	}

	public void setConfig(VirtualMachineConfig config) {
		Config = config;
	}
}
