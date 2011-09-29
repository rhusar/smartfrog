package org.smartfrog.services.slp;
/**
 * Store the information and peering state to each peer
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.util.*;

class Peer {
    String url;			// url of this peer
    String scope;		// supporting scope
    slpTcpHandler tcp;		// TCP connection ID
    boolean directFwd;		// enable/disable directFwd
    int aliveTimer;		// alive timer

    Peer(String url, String scope, slpTcpHandler tcp) {
	this.url   = url;
	this.scope = scope;
	this.tcp   = tcp;
	directFwd  = true; //false;
	aliveTimer = Const.expire_interval + Const.mainTimer_interval;
    }

    public synchronized int getAliveTimer() {
	aliveTimer -= Const.mainTimer_interval;
	return aliveTimer;
    }

    public synchronized void resetAliveTimer() {
	aliveTimer = Const.expire_interval;
    }

    public synchronized String getURL() {	 // URL
	return url;
    }

    public synchronized String getScope() {	 // scope
	return scope;
    }

    public synchronized slpTcpHandler getTcp() { // get peering connection
	return tcp;
    }

    public synchronized void setTcp(slpTcpHandler tcp) { // set peering conn
	this.tcp = tcp;
    }

    public synchronized void closeTcp() { 	 // close peering connection
	if (tcp != null) {
	    tcp.close();
	    tcp = null;
	}
    }

    public synchronized void setDirectFwd(boolean mode) { // direct forwarding
	directFwd = mode;
    }

    public synchronized boolean isDirectFwd() { // is direct forwarding
	return directFwd;
    }
}
