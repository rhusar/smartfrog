package org.smartfrog.services.slp;
/**
 * SLPv2 DA UDP message processing server
 * it MUST join the multicast group to process DAAdvert messages
 *    (1) read UDP message
 *    (2) call "daAction" to process message (parse & reply)
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.net.*;

class daUdpServer extends Thread {

    da            daf;
    slpUdpHandler udp;

    daUdpServer(da daf) {
	this.daf = daf;
	try {
	    // create UDP socket, and join multicast group for DAAdvert
	    MulticastSocket socket = new MulticastSocket(Const.port);
	    socket.setTimeToLive(255);
	    InetAddress mcaddr = InetAddress.getByName(Const.mcast_addr);
	    socket.joinGroup(mcaddr);
	    udp = new slpUdpHandler(socket);
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

    public slpUdpHandler getUdp() {
	return udp;
    }

    public void run() {
	try {
	    slpMsgParser  parser = new slpMsgParser(daf);
	    daAction      dac    = new daAction(daf, null, udp);

	    while (true) {
		// accept a SLP message in UDP
		byte[] buf = udp.receive();

		// parse packet header
		parser.Header(buf);

		// process message body and send reply
		dac.action(parser, buf);
	    }
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

}
