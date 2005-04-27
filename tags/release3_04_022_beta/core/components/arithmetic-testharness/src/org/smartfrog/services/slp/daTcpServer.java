package org.smartfrog.services.slp;
/**
 * SLPv2 DA TCP server, accept TCP connection and create
 * a TCP message handler for each connection
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.net.*;

class daTcpServer extends Thread {

    da           daf;

    daTcpServer(da daf) {
	this.daf = daf;
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(Const.port, 5);
            while (true) {
                Socket client = server.accept();
	   	(new daTcpHandler(client, daf)).start();
            } 
	} catch (Exception e) {
          //  System.err.println(e);
	 // System.err.println(" Server creation aborted " +e);
         //   System.exit(1);
        } 
    }
}
