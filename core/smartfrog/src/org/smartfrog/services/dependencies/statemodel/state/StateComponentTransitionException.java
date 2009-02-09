package org.smartfrog.services.dependencies.statemodel.state;

import org.smartfrog.sfcore.common.SmartFrogException;

public class StateComponentTransitionException extends SmartFrogException {
	private int code;
	
	StateComponentTransitionException(int code){
		this.code = code;
	}
	
	StateComponentTransitionException(String msg, int code){
		super(msg);
		this.code = code;
	}
	
	public static final int g_NOTRANSITIONS=0x0;
	public static final int g_NOSUCHAVAILABLETRANSITION=0x1;
	public static final int g_NOTRANSITIONSELECTED=0x2;
	public static final int g_INVALIDSTATEFUNCTION=0x3;
	public static final int g_ALLOWEDVALUEUNRESOLVABLE=0x4;
	public static final int g_INVALIDALLOWEDVALUE=0x5;
	public static final int g_MUSTSUPPLYVALUETOSET=0x6;
	public static final int g_INVALIDSUPPLIEDVALUE=0x7;
	public static final int g_VALUESLEFTTOSET=0x8;
	public static final int g_VALUEALREADYSET=0x9;
	public static final int g_NOTPERMITTED=0x10;
	public static final int g_DEPENDENCYVALUEUNRESOLVABLE=0x4;
	public static final int g_UNABLETOAPPLYEFFECTS=0x4;
	public static final int g_COMPONENTNOTENABLED=0x10;
	
	public int getExceptionCode(){ return code; }	
}
