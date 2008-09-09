package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.vast.architecture.VirtualMachineConfig;
import org.smartfrog.services.xmpp.XMPPEventExtension;

public interface Command {
	/**
	 * The command times out.
	 * @param inCfg The virtual machine config class which sent the command.
	 */
	public void timeOut(VirtualMachineConfig inCfg);

	/**
	 * The command succeeded.
	 * @param inCfg The virtual machine config class which sent the command.
	 */
	public void success(VirtualMachineConfig inCfg);

	/**
	 * The command failed.
	 * @param inCfg The virtual machine config class which sent the command.
	 */
	public void failure(VirtualMachineConfig inCfg);

	/**
	 * Executes the command.
	 * @param inCfg The virtual machine config class which sent the command.
	 */
	public void execute(VirtualMachineConfig inCfg);

	/**
	 * Gets the next command in case this one succeeds.
	 * @return The next command in case this one succeeds.
	 */
	public Command getNextOnSuccess();

	/**
	 * Sets the next command in case this one succeeds.
	 * @param inCmd The next command in case this one succeeds.
	 */
	public void setNextOnSuccess(Command inCmd);

	/**
	 * Sets the next command in case this one fails.
	 * @param inCmd The next command in case this one fails.
	 */
	public void setNextOnFailure(Command inCmd);

	/**
	 * Gets the next command in case this one fails.
	 * @return The next command in case this one fails.
	 */
	public Command getNextOnFailure();

	/**
	 * Composes the XMPP event extension for this command.
	 * @param inCfg The virtual machine config class which sent the command.
	 * @return The composed message.
	 */
	public XMPPEventExtension composeMessage(VirtualMachineConfig inCfg);

	/**
	 * Handle the response.
	 * @param inCfg The virtual machine config class which sent the command.
	 * @param inExt The response XMPP extension.
	 */
	public void handlePacket(VirtualMachineConfig inCfg, XMPPEventExtension inExt);
}
