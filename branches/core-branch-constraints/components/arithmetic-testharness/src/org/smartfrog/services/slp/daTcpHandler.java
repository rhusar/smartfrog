package org.smartfrog.services.slp;
/**
 * SLPv2 DA TCP message handler
 *   (1) read TCP message header & body, combine them together
 *   (2) call "daAction" to process the message (parse & reply)
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.io.*;
import java.net.*;

public class daTcpHandler extends Thread {

    da            daf;
    slpTcpHandler tcp;
    slpMsgParser  parser;
    DataOutputStream      d;
    ByteArrayOutputStream b;

    daTcpHandler(Socket socket, da daf) {
	this.daf = daf;
        tcp    = new slpTcpHandler(socket, daf);
        parser = new slpMsgParser(daf);
        b = new ByteArrayOutputStream();
        d = new DataOutputStream(b);
    }

    public slpTcpHandler getTcp() {
	return tcp;
    }

    public void run() {
        byte[] header, body;
        daAction dac = new daAction(daf, tcp, null);
	while (true) {
	    // read message header, and parse it
	    if ((header = tcp.getHeader()) == null) break;
	    parser.Header(header);

	    // read message body, and send reply
   	    int packetLen = parser.getPacketLen();
	    if ((body = tcp.getBody(packetLen)) == null) break;

       	    try {	// combine header & body for easy handling
            	b.reset();
            	d.write(header, 0, Const.header_len);
            	d.write(body,   0, packetLen - Const.header_len);
            } catch (Exception e) {
            	if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
            }
	    dac.action(parser, b.toByteArray());
	}
    }

}
