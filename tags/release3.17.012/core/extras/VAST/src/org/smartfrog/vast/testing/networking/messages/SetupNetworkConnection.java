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

public class SetupNetworkConnection implements VastMessage {
	private int Index;
	private String IPAddress;
	private String NetMask;

	public void invoke(InetAddress inFrom, MessageCallback inMessageCallback) {
		inMessageCallback.OnSetupNetworkConnection(Index, IPAddress, NetMask);
	}

	public SetupNetworkConnection(int index, String IPAddress, String netMask) {
		Index = index;
		this.IPAddress = IPAddress;
		NetMask = netMask;
	}

	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	public String getNetMask() {
		return NetMask;
	}

	public void setNetMask(String netMask) {
		NetMask = netMask;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int index) {
		Index = index;
	}
}
