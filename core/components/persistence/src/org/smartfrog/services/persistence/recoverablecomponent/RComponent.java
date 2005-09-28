package org.smartfrog.services.persistence.recoverablecomponent;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;


public interface RComponent extends Compound {

	public static int StubWait = 5000;
	public static String WFSTATUSENTRY = "WOODFROG_WFSTATUS";
	public static String WFSTATUSDIRECTORY = "WOODFROG_WFSTATUS";
	public static String WFSTATUS_DEAD = "WOODFROG_WFDEAD";
	public static String WFSTATUS_STARTED = "WOODFROG_WFSTARTED";
	
	public static String ATTRIBUTESDIRECTORY = "WOODFROG_SFATTRIBUTES";

	public static String CHILDRENSDIRECTORY = "WOODFROG_SFCHILDREN";
	public static String LIFECYCLECHILDREN = "WOODFROG_WFLIFECYCLECHILDREN"; 
	public static String SFCHILDREN = "WOODFROG_WFSFCHILDREN";
	public static String SFPARENT = "WOODFROG_WFSFPARENT";

	static final String DBStubEntry = "WFSTUBENTRY";
	static final String DBStubDirectory = "WFSTUBDIRECTORY";
	
	static final String STORAGEATTRIB = "wfStorage";
	static final String STORAGECLASSATTRIB = "wfStorageClass";
	static final String STORAGEREPOSITORY = "wfStorageRepository";
	
	
	public void sfRecover() throws SmartFrogException, RemoteException;

	public RComponentProxyLocator getProxyLocator() throws RemoteException;
	
}
