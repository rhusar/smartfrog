package org.smartfrog.services.www.context;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.JavaWebApplication;

import java.rmi.RemoteException;

/**
 * A WAR File
 */
public class WebApplicationImpl extends ApplicationServerContextImpl implements JavaWebApplication {

    public WebApplicationImpl() throws RemoteException {
    }

    protected ApplicationServerContextEntry deployThisComponent() throws RemoteException, SmartFrogException {
        return getServer().deployWebApplication(this);
    }

}
