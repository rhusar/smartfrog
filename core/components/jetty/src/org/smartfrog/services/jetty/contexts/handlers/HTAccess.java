package org.smartfrog.services.jetty.contexts.handlers;

import org.mortbay.http.handler.HTAccessHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.jetty.contexts.ServletContextIntf;

import java.rmi.RemoteException;

/**
 * A HTAccess handler class for jetty server
 *
 * @author Ritu Sabharwal
 */


public class HTAccess extends HandlerImpl implements HTAccessIntf {
    protected Reference accessFileRef = new Reference(ACCESS_FILE);

    protected String accessFile = ".htaccess";

    protected HTAccessHandler hthandler = new HTAccessHandler();

    /**
     * Standard RMI constructor
     */
    public HTAccess() throws RemoteException {
        super();
    }

    /**
     * sfDeploy: adds the HTAccess Handler to ServetletHttpContext of jetty server
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        accessFile = sfResolve(accessFileRef, accessFile, false);
        hthandler.setAccessFile(accessFile);
        addHandler(hthandler);
    }
}
