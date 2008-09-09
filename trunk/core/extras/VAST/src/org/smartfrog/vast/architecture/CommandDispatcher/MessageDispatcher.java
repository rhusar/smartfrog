package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.services.xmpp.XMPPEventExtension;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MessageDispatcher {
	private HashMap<String, DispatcherThread> dispatcherThreads = new LinkedHashMap<String, DispatcherThread>(10);
	private AvalancheServer refAvl = null;

	public MessageDispatcher(AvalancheServer refAvl) {
		this.refAvl = refAvl;
	}

	public synchronized void sendMessage(String inHost, XMPPEventExtension inMsg) {
		if (dispatcherThreads.containsKey(inHost)) {
			// queue message
			DispatcherThread dt = dispatcherThreads.get(inHost);
			dt.queueMessage(inMsg);
			if (!dt.isAlive())
				dt.start();
		} else {
			// create new dispatcher thread
			DispatcherThread dt = new DispatcherThread(refAvl, inHost);
			dt.queueMessage(inMsg);
			dt.start();

			// add it to the hashmap
			dispatcherThreads.put(inHost, dt);
		}
	}

	public AvalancheServer getRefAvl() {
		return refAvl;
	}

	public void setRefAvl(AvalancheServer refAvl) {
		this.refAvl = refAvl;
	}
}
