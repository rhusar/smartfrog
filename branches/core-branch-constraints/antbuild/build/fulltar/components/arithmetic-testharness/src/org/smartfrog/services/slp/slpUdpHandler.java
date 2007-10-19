package org.smartfrog.services.slp;
/**
 * A generic UDP message handler
 *   (1) send & receive UDP messages
 *   (2) get parameters of the UDP socket
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.net.*;
import java.io.*;

public class slpUdpHandler {

    DatagramSocket socket;
    DatagramPacket send_packet, recv_packet;
    byte rbuf[];

    slpUdpHandler(DatagramSocket socket) {
	this.socket = socket;
        rbuf = new byte[Const.max_packet];
    }

    public String getPeerName() { //domain name of remote host
	return recv_packet.getAddress().getHostName();
    }

    public InetAddress getAddress() {
	return recv_packet.getAddress();
    }

    public int getPort() {
	return recv_packet.getPort();
    }

    /*
     * return an error string if failure
     * return null if success
     */
    public String send(byte[] sbuf, String dest, int port) {
        try {
            InetAddress addr = InetAddress.getByName(dest);
            send_packet = new DatagramPacket(sbuf, sbuf.length, addr, port);
            socket.send(send_packet);
	    return null;
	} catch (UnknownHostException e) {
	    return "Invalid host name or address: " + dest;
        } catch (Exception e) {
            return e.toString();
        }
    }

    /*
     * return received packet in byte[] if success
     * return null if failure
     */
    public byte[] receive() {
	try {
            recv_packet = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(recv_packet);
	    return rbuf;
	} catch (InterruptedIOException e) { // TimeOut
	    return null;
	} catch (Exception e) {
            if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	    return null;
        }
    }
}
