package org.smartfrog.vast.testing.networking.messages;

import java.net.InetAddress;
import java.util.Vector;

public class InvokeFunction implements VastMessage {
	String FunctionName;
	Vector Patameters;

	public InvokeFunction(String functionName, Vector patameters) {
		FunctionName = functionName;
		Patameters = patameters;
	}

	public void invoke(InetAddress inFrom, MessageCallback inMessageCallback) {
		inMessageCallback.OnInvokeFunction(FunctionName, Patameters);
	}

	public String getFunctionName() {
		return FunctionName;
	}

	public void setFunctionName(String functionName) {
		FunctionName = functionName;
	}

	public Vector getPatameters() {
		return Patameters;
	}

	public void setPatameters(Vector patameters) {
		Patameters = patameters;
	}
}
