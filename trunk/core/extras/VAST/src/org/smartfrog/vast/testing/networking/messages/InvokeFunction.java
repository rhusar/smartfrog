package org.smartfrog.vast.testing.networking.messages;

import java.net.InetAddress;
import java.util.Vector;

public class InvokeFunction implements VastMessage {
	String FunctionName, ProcessName;
	Vector Patameters;

	public InvokeFunction(String functionName, String processName, Vector patameters) {
	FunctionName = functionName;
	Patameters = patameters;
	ProcessName = processName;
}

	public void invoke(InetAddress inFrom, MessageCallback inMessageCallback) {
		inMessageCallback.OnInvokeFunction(FunctionName, ProcessName, Patameters);
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

	public String getProcessName() {
		return ProcessName;
	}

	public void setProcessName(String processName) {
		ProcessName = processName;
	}
}
