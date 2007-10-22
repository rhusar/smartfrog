package org.smartfrog.services.slp;

/**
 * A UA/SA discovers all DAs.
 *   (1) send DA discovery message upon request
 *   (2) process all DAAdvert messages from DAs, add to DA list
 *       if a new DA is discovered.
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.net.*;

class uaFindDa extends Thread {

    ua uaf;
    boolean tryDiscovery;
    boolean running = true;
    int xid;

    uaFindDa(ua uaf) {
	this.uaf     = uaf;
	tryDiscovery = false;
    }

    public void discover(int xid) {
	this.xid = xid;
	tryDiscovery = true;
    }
    //!!! does not kill the thread (opened sockets), merely stops the loop (after 1 timeout).
    public void stopThread(){
      running = false;
    }

    public void run() {
	try {
	    DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(Const.SoTimeout);
            slpUdpHandler  udp    = new slpUdpHandler(socket);
            slpMsgComposer composer = new slpMsgComposer();
	    slpMsgParser   parser = new slpMsgParser();
	    uaAction       uac    = new uaAction(uaf);

	    while (running) {
	    	while (!tryDiscovery) Thread.sleep(Const.daDiscover_interval);
		//System.out.println( "Discovery via findDA thread");
		uaf.clearDA();
            	byte[] mesg = composer.SrvRqst(xid, Const.mcast_flag, "en", "",
					Const.DAAdvert_Rqst, "", "", "");
	    	String ret = udp.send(mesg, Const.mcast_addr, Const.port);
		if (ret != null) uaf.append(ret);
		tryDiscovery = false;

	    	while (true) {  // try to collect all replies from DAs
		    byte[] buf = udp.receive();
		    if (buf == null) break; // TimeOut, no more reply
		    parser.Header(buf);
		    uac.action(parser, buf, Const.header_len);
		}
	    }
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

}
