package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.vast.architecture.VirtualMachineConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class DispatcherThread extends Thread {
	private AvalancheServer refAvl = null;
	private String TargetHost;
	private ArrayList<MessageQueueItem> MessageQueue = new ArrayList<MessageQueueItem>(10);
	private int SendingInterval = 2000;
	private boolean sendNext = true;

	public DispatcherThread(AvalancheServer refAvl, String inHost) {
		this.refAvl = refAvl;
		TargetHost = inHost;
	}

	public void run() {
		while (true) {
			while (!MessageQueue.isEmpty() && sendNext) {
				try {
					// send the command
					MessageQueueItem item = MessageQueue.get(0);

					System.out.println("Sending: " + item);

					// start the timer
					item.getConfig().startTimer();

					// send the message
					refAvl.sendXMPPExtension(TargetHost, item.getCmd().composeMessage(item.getConfig()));

					// remove it from the queue
					MessageQueue.remove(0);

				} catch (Exception e) {
					e.printStackTrace();
				}

				sendNext = false;

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

	public void sendNext() {
		sendNext = true;
	}

	public void queueMessage(VirtualMachineConfig inCfg, Command inCmd) {
		MessageQueue.add(new MessageQueueItem(inCmd, inCfg));
	}

	public int getSendingInterval() {
		return SendingInterval;
	}

	public void setSendingInterval(int sendingInterval) {
		SendingInterval = sendingInterval;
	}
}
