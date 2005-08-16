package org.smartfrog.services.www.context;

import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.JavaWebApplicationServer;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * a non-instantiable abstract server component
 *
 */
public abstract class ApplicationServerContextImpl extends PrimImpl implements ApplicationServerContext {

    public ApplicationServerContextImpl() throws RemoteException {
    }

    /**
     * our context handle. null if not deployed
     */
    private String contextHandle;

    /**
     * our server
     */
    private JavaWebApplicationServer server;

    public void setContextHandle(String contextHandle) {
        this.contextHandle = contextHandle;
    }

    public String getContextHandle() {
        return contextHandle;
    }

    /**
     * Get the server. Only valid after {@link #bindToServer()} has been successful
     * @return the server
     */
    public JavaWebApplicationServer getServer() {
        return server;
    }

    /**
     * Bind to the server, by extracting the value of {@link #ATTR_SERVER} and saving
     * it somewhere that {@link #getContextHandle()} can retrieve it.
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    protected void bindToServer() throws SmartFrogResolutionException, RemoteException {
        server=(JavaWebApplicationServer) sfResolve(ATTR_SERVER,(Prim)null,true);
    }

    /**
     * undeploy us if bound, do nothing if not. the context handle is reset,
     * so we no longer consider ourselves bound
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void undeploy() throws SmartFrogException, RemoteException {
        if(contextHandle!=null && server!=null) {
            String handle = contextHandle;
            contextHandle=null;
            server.undeployApplicationServerContext(handle);
        }
    }


    public void start() throws SmartFrogException, RemoteException {

    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {

    }
}
