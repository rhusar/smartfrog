package org.smartfrog.vast.testing.networking.messages;

import java.net.InetAddress;

public class PublishedAttribute implements VastMessage {
	String 	Key, Value, ProcessName;

	public PublishedAttribute(String processName, String key, String value) {
		Key = key;
		ProcessName = processName;
		Value = value;
	}

	public void invoke(InetAddress inFrom, MessageCallback inMessageCallback) {
		inMessageCallback.OnPublishedAttribute(inFrom, ProcessName, Key, Value);
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public String getProcessName() {
		return ProcessName;
	}

	public void setProcessName(String processName) {
		ProcessName = processName;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}
}
