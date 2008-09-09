package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.services.xmpp.XMPPEventExtension;

import java.util.ArrayList;

public class DispatcherThread extends Thread {
	private AvalancheServer refAvl = null;
	private String TargetHost;
	private ArrayList<XMPPEventExtension> Messages = new ArrayList<XMPPEventExtension>(10);
	private int SendingInterval = 2000;

	public DispatcherThread(AvalancheServer refAvl, String inHost) {
		this.refAvl = refAvl;
		TargetHost = inHost;
	}

	public void run() {
		while (true) {
			while (!Messages.isEmpty()) {
				// send the command
				try {
					refAvl.sendXMPPExtension(TargetHost, Messages.get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// remove it from the queue
				Messages.remove(0);

				try {
					Thread.sleep(SendingInterval);
				} catch (InterruptedException e) {

				}
			}

			// sleep for a while and see if new messages have been queued
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {

			}
		}
	}

	public void queueMessage(XMPPEventExtension inMsg) {
		Messages.add(inMsg);
	}

	public int getSendingInterval() {
		return SendingInterval;
	}

	public void setSendingInterval(int sendingInterval) {
		SendingInterval = sendingInterval;
	}
}
