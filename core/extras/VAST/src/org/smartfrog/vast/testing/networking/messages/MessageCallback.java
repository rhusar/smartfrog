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

package org.smartfrog.vast.testing.networking.messages;

import java.net.InetAddress;
import java.util.Map;
import java.util.Vector;

public interface MessageCallback {
	/**
	 * A hello broadcast has been received.
	 * @param inFrom Sender of this message.
	 */
	public void OnHelloBroadcast(InetAddress inFrom);

	/**
	 * Executes a sf script with sfRun.
	 * @param inScript The URL/path to the script.
	 * @param inEnv The environment variables that should be used or null to inherit the environment of the current process.
	 */
	public void OnRunSfScript(String inScript, Map<String, String> inEnv);

	/**
	 * Start the sfDaemon of the System Under Test.
	 */
	public void OnStartSUTDaemon();

	/**
	 * Cuts a network connection.
	 * @param inIndex The index of the NIC which should be cut.
	 * @param inIP The address of the connection which should be cut.
	 */
	public void OnCutNetworkConnection(int inIndex, String inIP);

	/**
	 * Sets up a network connection.
	 * @param inIndex The index of the NIC which should be used.
	 * @param inIP The ip address to set.
	 * @param inMask The subnet mask of the ip.
	 */
	public void OnSetupNetworkConnection(int inIndex, String inIP, String inMask);

	/**
	 * Executes a sf script with sfStart.
	 * @param inScript The URL/path of the script.
	 * @param inProcessName The name for the sub process.
	 * @param inEnv The environment variables that should be used or null to inherit the environment of the current process.
	 */
	public void OnStartSfScript(String inScript, String inProcessName, Map<String, String> inEnv);

	/**
	 * Invokes a function with the given parameters.
	 * @param inFunctionName The name of the function.
	 * @param inParameters The parameters.
	 */
	public void OnInvokeFunction(String inFunctionName, Vector inParameters);
}
