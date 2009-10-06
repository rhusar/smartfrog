/*
 * Created on Feb 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.prereqs;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFCheckPrereqs extends PrimImpl implements Prim {
	private final String JAVAPATH = "javaPath";
	private final String JAVAVERSION = "javaVersion";
	private final String ANTPATH = "antPath";
	private final String ANTVERSION = "antVersion";
	private final String CVERSION = "cVersion";
	private final String TARVENDOR = "tarVendor";
	private final String SEDVENDOR = "sedVendor";
	private final String MAKEVENDOR = "makeVendor";
	private final String SUDO = "sudo";
	private final String POSTGRESVER = "postgresVersion";
	private final String SHDTERMINATE = "shouldTerminate";
	private final String PERLPATH = "perlPath";
	private final String PERLVERSION = "perlVersion";

	private String javaPath, javaVersion;
	private String antPath, antVersion;
	private String cVersion,tarVendor;
	private String sedVendor, makeVendor;
	private String sudo, postgresVersion;
	private String perlPath, perlVersion;
	private boolean shouldTerminate;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFCheckPrereqs() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		javaPath = (String)sfResolve(JAVAPATH, javaPath, true);
		javaVersion = (String)sfResolve(JAVAVERSION, javaVersion, true);
		antPath = (String)sfResolve(ANTPATH, antPath, true);
		antVersion = (String)sfResolve(ANTVERSION, antVersion, true);
		cVersion = (String)sfResolve(CVERSION, cVersion, true);
		tarVendor = (String)sfResolve(TARVENDOR, tarVendor, true);
		sedVendor = (String)sfResolve(SEDVENDOR, sedVendor, true);
		makeVendor = (String)sfResolve(MAKEVENDOR, makeVendor, true);
		sudo = (String)sfResolve(SUDO, sudo, true);
	//	postgresVersion = (String)sfResolve(POSTGRESVER, postgresVersion, true);
		perlPath = (String)sfResolve(PERLPATH, perlPath, true);
		perlVersion = (String)sfResolve(PERLVERSION, perlVersion, true);
		
		// optional attribute
		shouldTerminate = (boolean)sfResolve(SHDTERMINATE, true, false);
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();		
		CheckPrereqs chk = new CheckPrereqs();
		
		try {
			chk.checkCmd(javaPath, "java", javaVersion, null);
			chk.checkCmd(antPath, "ant", antVersion, null);
			chk.checkCmd("tar", null, tarVendor);
			chk.checkCmd("sed", null, sedVendor);
			chk.checkCmd("cc", cVersion);
			chk.checkCmd("make", null, makeVendor);
			chk.checkCmd("sudo", null, null);
		//	chk.checkCmd("postgres", postgresVersion);
		//	chk.checkCmd("perl", null, null);
			chk.checkCmd(perlPath,"perl", perlVersion, null);

		} catch(IOException ioe) {
			sfLog().err("Exception in checking pre-requisites", ioe);
			throw new SmartFrogException("Exception in checking pre-requisites", 
					ioe);
		} catch (PrereqException pe) {
			sfLog().err(pe);
			throw new SmartFrogException(pe);
		} catch (InterruptedException ie) {
			sfLog().err(ie);
			throw new SmartFrogException(ie);
		}
		
		sfLog().info("All pre-requisites for GT4 satisfied");
		
		// terminate synchronously
		TerminationRecord tr = TerminationRecord.normal("Terminating ...", sfCompleteName());
		sfTerminate(tr);
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
