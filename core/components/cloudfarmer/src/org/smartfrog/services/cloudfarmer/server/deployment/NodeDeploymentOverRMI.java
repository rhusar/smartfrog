package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.DefaultRootLocatorImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * This is not an SF component; it is something that can be created by such components and passed back to callers; you
 * can also have a wrapper component that creates an instance
 */
public class NodeDeploymentOverRMI implements NodeDeploymentService {

    private final Log log = LogFactory.getLog(NodeDeploymentOverRMI.class);

    private String hostname = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    //we have to reconnect when deserializing
    private ProcessCompound boundProcess;
    public static final int DEFAULT_PORT = 3800;
    public static final String DEFAULT_HOST = "localhost";
    public static final String ATTR_DESCRIPTION = "description";


    /**
     * Bind to a host and port
     *
     * @param hostname host
     * @param port port value
     */
    public NodeDeploymentOverRMI(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Cinstruct from a URL
     *
     * @param baseURL URL to work with
     * @throws MalformedURLException if this does not parse
     */
    public NodeDeploymentOverRMI(String baseURL) throws MalformedURLException {
        URL url = new URL(baseURL);
        hostname = url.getHost();
        if (url.getPort() != -1) {
            port = url.getPort();
        }
    }


    /**
     * Print the hostname and port
     *
     * @return printable description
     */
    @Override
    public String toString() {
        return "RMI-connection to " + hostname + ":" + port;
    }

    /**
     * Bind to the remote node. This may fail with an error
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException network/RMI trouble
     */
    public synchronized ProcessCompound bind() throws SmartFrogException, IOException {
        InetAddress addr = InetAddress.getByName(hostname);
        DefaultRootLocatorImpl defaultRootLocator = new DefaultRootLocatorImpl();
        try {
            return defaultRootLocator.getRootProcessCompound(addr, port);
        } catch (SmartFrogException e) {
            throw e;
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }

    @Override
    public void terminate() throws IOException, SmartFrogException {
        unbind();
    }

    /**
     * Break the connection
     */
    private synchronized void unbind() {
        boundProcess = null;
    }
    /**
     * Implement on-demand binding
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException        network/RMI trouble
     */
    public synchronized ProcessCompound bindOnDemand() throws SmartFrogException, IOException {
        if (boundProcess == null) {
            boundProcess = bind();
        }
        return boundProcess;
    }

    /**
     * Get any process to which we are bound -there is no on demand binding here
     *
     * @return the process or null
     */
    public synchronized ProcessCompound getBoundProcess() {
        return boundProcess;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }


    /**
     * retrieve a workflow by name. This is a remote operation.
     *
     * @param name string to look up
     * @param mandatory true iff the name is required
     * @return a resolved prim or null if there was none and mandatory==false
     * @throws IOException for network problems
     * @throws SmartFrogException any resolution problems
     */
    public Prim lookupPrim(String name, boolean mandatory) throws IOException, SmartFrogException {
        ProcessCompound root = getBoundProcess();
        Object resolved = root.sfResolveHere(name, mandatory);
        if (resolved == null) {
            return null;
        }
        if (resolved instanceof ComponentDescription) {
            throw new SmartFrogResolutionException("The name " + name + " resolves to an undeployed component");
        }
        if (!(resolved instanceof Prim)) {
            throw new SmartFrogResolutionException("The name " + name
                    + " resolves to " + resolved.toString()
                    + " and not a deployed application");
        }
        Prim prim = (Prim) resolved;
        return prim;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void deployApplication(String name, ComponentDescription cd) throws IOException, SmartFrogException {
        log.info("Deploying the application " + name + " at "+toString());
        ProcessCompound root = getBoundProcess();
        Prim app = root.sfCreateNewApp(name, cd, null);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public boolean terminateApplication(String name, boolean normal, String exitText)
            throws IOException, SmartFrogException {
        Prim remoteApplication = lookupPrim(name, false);
        if (remoteApplication != null) {
            TerminationRecord status;
            status = new TerminationRecord(normal ? TerminationRecord.NORMAL : TerminationRecord.ABNORMAL,
                    exitText,
                    null);
            remoteApplication.sfTerminate(status);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void pingApplication(String name) throws IOException, SmartFrogException {
        Prim app = lookupPrim(name, true);
        app.sfPing(getBoundProcess());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getServiceDescription() throws IOException, SmartFrogException {
        return toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getApplicationDescription(String name) throws IOException, SmartFrogException {
        Prim app = lookupPrim(name, true);
        return app.sfResolve(ATTR_DESCRIPTION, "", false);
    }
}
