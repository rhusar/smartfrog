package org.smartfrog.services.www.context;

import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.JavaWebApplicationServer;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * a non-instantiable abstract server component
 */
public abstract class ApplicationServerContextImpl extends PrimImpl
        implements ApplicationServerContext {
    /**
     * delegate class itself
     */
    private ApplicationServerContext delegate;

    static final Reference SERVER = new Reference(ATTR_SERVER);


    /**
     * constructor
     * @throws RemoteException from the superclass
     */
    protected ApplicationServerContextImpl() throws RemoteException {
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
     * Get the server. Only valid after {@link #bindToServer()} has been
     * successful
     *
     * @return the server
     */
    public JavaWebApplicationServer getServer() {
        return server;
    }

    /**
     * Bind to the server, by extracting the value of {@link #ATTR_SERVER} and
     * saving it somewhere that {@link #getContextHandle()} can retrieve it.
     *
     * @throws SmartFrogResolutionException if things do not resolve
     * @throws RemoteException  In case of network/rmi error
     */
    protected void bindToServer()
            throws SmartFrogResolutionException, RemoteException {
        final Prim prim = sfResolve(SERVER,
                (Prim) null,
                true);
        if (prim instanceof JavaWebApplicationServer) {
            server = (JavaWebApplicationServer) prim;
        } else {
            //wrong class type. Throw it with something better than a
            //class cast exception
            throw SmartFrogResolutionException.illegalClassType(SERVER,
                    sfCompleteNameSafe(),
                    prim, prim.getClass().toString(),
                    JavaWebApplicationServer.class.toString());
        }
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        bindToServer();
        validate();
        delegate = deployThisComponent();
        delegate.deploy();
    }

    /**
     * Override point: validate the component before
     * calling the delegate.
     * The base class does nothing.
     * @throws SmartFrogException error while validating
     * @throws RemoteException    In case of network/rmi error
     */
    protected void validate()
            throws SmartFrogException, RemoteException {
        
    }

    /**
    * {@inheritDoc}
    * @throws SmartFrogException error while deploying
    * @throws RemoteException    In case of network/rmi error
    */
    public void deploy() throws SmartFrogException, RemoteException {
        if (delegate != null) {
            delegate.deploy();
        }
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        start();
    }


    /**
     * Start by delegating to the subclass
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public void start() throws SmartFrogException, RemoteException {
        validateDuringStartup();
        if (delegate != null) {
            delegate.start();
        }
    }

    /**
     * Override point: validate the component before starting up <p/> The base class does nothing.
     *
     * @throws SmartFrogException error while validating
     * @throws RemoteException In case of network/rmi error
     */
    protected void validateDuringStartup()
            throws SmartFrogException, RemoteException {

    }

    /**
     * Undeploy us if bound, do nothing if not.
     * The context handle is reset, so
     * we no longer consider ourselves bound
     *
     * @throws SmartFrogException failure while terminating
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void terminate() throws SmartFrogException, RemoteException {
        try {
            if (delegate != null) {
                delegate.terminate();
            }
        } finally {
            delegate = null;
        }
    }

    /**
     * subclasses must implement this to deploy their component. It is called
     * during sfDeploy, after we have bound to a server
     *
     * @return a new context
     *
     * @throws RemoteException  network trouble
     * @throws SmartFrogException errors during deployment
     */
    protected abstract ApplicationServerContext deployThisComponent()
            throws RemoteException, SmartFrogException;

    /**
     * Get the delegate (may be null)
     * @return a potentially null delegate
     */
    public ApplicationServerContext getDelegate() {
        return delegate;
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException liveness failure
     * @throws RemoteException    network trouble
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        ping();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            terminate();
        } catch (RemoteException e) {
            //ignore
        } catch (SmartFrogException e) {
            //ignore
        }
    }


    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException failure of the ping
     * @throws RemoteException network trouble
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (delegate == null) {
            throw new SmartFrogLivenessException("No active delegate");
        }
        delegate.ping();
    }


}
