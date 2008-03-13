package org.smartfrog.services.slp;
/**
 * A generic TCP message handler
 *   (1) send TCP messages
 *   (2) receive TCP message header & body separately
 *   (3) get parameters & close the TCP socket
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class slpTcpHandler {

    da daf;
    Socket socket;
    DataInputStream  in;
    DataOutputStream out;
    byte header[], body[];
    String peer;

    slpTcpHandler(Socket socket, da daf) {
	try {
	    this.daf = daf;
	    this.socket = socket;
	    in  = new DataInputStream(socket.getInputStream());
	    out = new DataOutputStream(socket.getOutputStream());
	    header = new byte[Const.header_len];
	    body = new byte[Const.max_packet];
	    peer = socket.getInetAddress().getHostName();
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

    public Socket getSocket() {
	return socket;
    }

    public String getPeerName() {
	return peer;
    }

    public void close() {
	try {
	    in.close();
	    out.close();
	    socket.close();
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

    public byte[] getHeader() {
	int nread = 0;

	// read header
	try {
	    nread = in.read(header, 0, Const.header_len);
	} catch (InterruptedIOException e) { // TimeOut
	    System.err.println(e.getMessage());
	    return null;
	} catch (SocketException e) { // keep silent
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}

	// EOF means the connection has been closed
	if (nread <= 0) {
	    if (daf != null) {
		daf.checkPeerConn(peer, socket);
	    }
	    return null;
	}
	if (nread != Const.header_len) {
	    System.err.println("SLP header error");
	    return null;
	}
	return header;
    }

    public byte[] getBody(int packet_len) {
	int blen, start = 0, nread;
	blen = packet_len - Const.header_len;
	if (blen > body.length) {
	    body = new byte[blen];	// in cast we received a big packet
	}
        try {
   	    while (blen > 0) {
       		nread = in.read(body, start, blen);
       		start = start + nread;
   		blen = blen - nread;
       	    }
        } catch (Exception e) {
   	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
   	    return null;
        }
	return body;
    }

    public void send(byte[] buf, int len) {
	try {
	    out.write(buf, 0, len);
	    out.flush();
	} catch (IOException e) {
	    System.err.println(e.getMessage());
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }
}
