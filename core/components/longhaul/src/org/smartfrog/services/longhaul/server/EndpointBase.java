/**
 * 
 */
package org.smartfrog.services.longhaul.server;

import java.rmi.RemoteException;

import javax.ws.rs.Path;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**

 *
 */

public abstract class EndpointBase {

    static ProcessCompound root;
    
    public EndpointBase() {
	bindToRootProcess();
    }

    static synchronized void bindToRootProcess()  {
	if(root==null) {
	    try {
		ProcessCompound localProcess = SFProcess.getProcessCompound();		
		root = SFProcess.getRootLocator().getRootProcessCompound(null, 
		((Number) localProcess.sfResolveHere(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT, false)).intValue());
	    } catch (Exception e) {
		//log and continue
	    }
	}
    }
    
    
    
    
}
