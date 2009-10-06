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

package org.smartfrog.vast.testing.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class ListeningThread extends Thread {
	/**
	 * Reference to the socket.
	 */
	private DatagramSocket refSocket = null;

	/**
	 * Reference to the broadcast communicator.
	 */
	private BroadcastCommunicator refBCast = null;

	public ListeningThread(DatagramSocket inSocket, BroadcastCommunicator inBCC) {
		refSocket = inSocket;
		refBCast = inBCC;
	}

	public void run() {
		byte[] buffer = new byte[1500];
		DatagramPacket p = new DatagramPacket(buffer, 0, buffer.length);

		while (true) {
			try {
				// wait for an incoming packet
				refSocket.receive(p);

				// dispatch it to the callbacks
				refBCast.processPacket(p);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
