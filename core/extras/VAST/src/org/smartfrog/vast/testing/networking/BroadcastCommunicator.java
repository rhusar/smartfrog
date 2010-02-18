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

import org.smartfrog.vast.testing.networking.messages.MessageCallback;
import org.smartfrog.vast.testing.networking.messages.VastMessage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class BroadcastCommunicator extends Thread {
	/**
	 * The list of the command callbacks.
	 */
	private ArrayList<MessageCallback> listCallbacks = new ArrayList<MessageCallback>();
	/**
	 * The logical clock of the communicator.
	 */
	private LogicalClock logicalClock = new LogicalClock();

	/**
	 * The multicast socket.
	 */
	private DatagramSocket socket;

	/**
	 * The group the socket is currently joined.
	 */
	private InetAddress BroadcastAddress;

	/**
	 * The port of MCC.
	 */
	private int Port;

	/**
	 * The local ip address.
	 */
	private InetAddress LocalAddress;

	/**
	 * The listening thread for incoming datagram packets.
	 */
	private ListeningThread listener;

	/**
	 * Constructor.
	 * @param inBroadcastAddress The broadcast address.
	 * @param inNicIP The network interface card's ip which should be used.
	 * @param inPort The port to listen on.
	 * @throws java.io.IOException 
	 */
	public BroadcastCommunicator(InetAddress inBroadcastAddress, String inNicIP, int inPort) throws IOException {
		// create the socket
		socket = new DatagramSocket(inPort);
		socket.setBroadcast(true);

		BroadcastAddress 	= inBroadcastAddress;
		Port 				= inPort;
		LocalAddress 		= InetAddress.getByName(inNicIP);

		// create and start the listening thread
		listener = new ListeningThread(socket, this);
		listener.start();
	}

	/**
	 * Gets the multicast group the socket is currently joined.
	 * @return
	 */
	public InetAddress getBroadcastAddress() {
		return BroadcastAddress;
	}

	public void sendMessage(InetAddress inTarget, VastMessage inVastMessage) throws IOException {
		// create the vast packet
		VastPacket packet = new VastPacket(logicalClock.newEvent(), inVastMessage);
		packet.setTarget(inTarget);

		// create the streams
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);

		// write the packet
		out.writeObject(packet);

		// get the byte array
		byte[] array = bout.toByteArray();

		// create the datagram
		DatagramPacket p = new DatagramPacket(array, array.length);
		p.setAddress(BroadcastAddress);
		p.setPort(Port);

		// send the packet
		socket.send(p);
	}

	/**
	 * Adds a callback interface.
	 * @param inMessageCallback
	 */
	public void addCallback(MessageCallback inMessageCallback) {
		if (inMessageCallback != null)
			listCallbacks.add(inMessageCallback);
	}

	/**
	 * Removes a callback interface.
	 * @param inMessageCallback
	 */
	public void removeCallback(MessageCallback inMessageCallback) {
		if (inMessageCallback != null)
			listCallbacks.remove(inMessageCallback);
	}

	/**
	 * Closes the socket connection.
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		listener.interrupt();
		socket.close();
	}

	/**
	 * Processes a datagram packet.
	 * @param inPacket
	 */
	public void processPacket(DatagramPacket inPacket) throws IOException, ClassNotFoundException {
		// ensure it's not our own package
		if (!inPacket.getAddress().equals(LocalAddress)) {
			// create the streams
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(inPacket.getData()));

			// read the object
			VastPacket pkt = (VastPacket) in.readObject();

			// sync the clock
			logicalClock.receivedEvent(pkt.getClock());

			System.out.println("Received packet:" + pkt);

			// ensure it's meant for us
			if (pkt.getTarget() == null || pkt.getTarget().equals(LocalAddress)) {
				// retrieve the message
				VastMessage msg = pkt.getMessage();

				System.out.println("Received message:" + msg);

				// invoke the callback functions
				for (MessageCallback mc : listCallbacks)
					msg.invoke(inPacket.getAddress(), mc);
			}
		}
	}
}
