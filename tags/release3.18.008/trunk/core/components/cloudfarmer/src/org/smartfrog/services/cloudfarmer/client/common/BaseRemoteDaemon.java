package org.smartfrog.services.cloudfarmer.client.common;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.hadoop.descriptions.TemplateNames;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.WorkflowList;
import org.smartfrog.services.cloudfarmer.server.deployment.NodeDeploymentOverRMI;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 *
 */
public class BaseRemoteDaemon extends AbstractEndpoint {
    private static final long serialVersionUID = -3102169331874193302L;
    //we have to reconnect when deserializing
    protected transient NodeDeploymentOverRMI nodeDeploy;

    public BaseRemoteDaemon() {
    }

    public BaseRemoteDaemon(String baseURL) {
        super(baseURL);
    }

    public String getHostname() {
        return nodeDeploy.getHostname();
    }

    public int getPort() {
        return nodeDeploy.getPort();
    }

    /**
     * Print the hostname and port
     *
     * @return printable description
     */
    @Override
    public String toString() {
        return "workflow server " + nodeDeploy.toString();
    }

    /**
     * Implement on-demand binding
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException network/RMI trouble
     */
    public ProcessCompound bindOnDemand() throws SmartFrogException, IOException {
        return nodeDeploy.bindOnDemand();
    }

    /**
     * Get any process to which we are bound -there is no on demand binding here
     *
     * @return the process or null
     */
    public synchronized ProcessCompound getBoundProcess() {
        return nodeDeploy.getBoundProcess();
    }

    /**
     * Break the connection
     *
     * @throws IOException on network failure
     * @throws SmartFrogException SF problems
     */
    public synchronized void unbind() throws SmartFrogException, IOException {
        nodeDeploy.terminate();
    }

    /**
     * List all active workflows. This is a remote operation, fairly chatty.
     *
     * @return a list of workflows
     * @throws IOException on network failure
     * @throws SmartFrogException SF problems
     */
    public WorkflowList listWorkflows() throws IOException, SmartFrogException {
        ProcessCompound root = getBoundProcess();
        WorkflowList result = new WorkflowList(this);
        Enumeration<Liveness> children = root.sfChildren();
        while (children.hasMoreElements()) {
            Liveness liveness = children.nextElement();
            Object key = root.sfAttributeKeyFor(liveness);
            Workflow childApp = new Workflow(this, key, (Prim) liveness);
            result.add(childApp);
        }
        return result;
    }

    /**
     * Create a workflow
     *
     * @param name workflow name
     * @param cd components to deploy
     * @return the deployed workflow
     * @throws IOException on network failure
     * @throws SmartFrogException SF problems
     */
    public Workflow createWorkflow(String name, ComponentDescription cd) throws IOException, SmartFrogException {
        log.info("Creating a workflow " + name);
        ProcessCompound root = getBoundProcess();
        Prim app = root.sfCreateNewApp(name, cd, null);
        Workflow workflow = new Workflow(this, name, app);
        return workflow;
    }

    /**
     * Creates a local CD that is then pushed over
     *
     * @return a blank CD
     */
    private ComponentDescription createCD() {
        ComponentDescription cd = new ComponentDescriptionImpl(null, null, false);
        return cd;
    }

    /**
     * Create a CD of a given classname
     *
     * @param classname class to use in the CD
     * @return the stub CD
     * @throws SmartFrogRuntimeException on a failure to create
     */
    private ComponentDescription createCD(String classname) throws SmartFrogRuntimeException {
        ComponentDescription cd = createCD();
        cd.sfAddAttribute("sfClass", classname);
        return cd;
    }

    /**
     * Creates a local CD that is then pushed over
     *
     * @return a blank CD
     * @throws SmartFrogRuntimeException on a failure to create
     */
    protected ComponentDescription createCompoundCD() throws SmartFrogRuntimeException {
        return createCD("org.smartfrog.sfcore.compound.CompoundImpl");
    }

    /**
     * retrieve a workflow by name. This is a remote operation.
     *
     * @param name string to look up
     * @param mandatory true iff the name is required
     * @return a resolved workflow  or null if there was none and mandatory==false
     * @throws IOException for network problems
     * @throws SmartFrogException any resolution problems
     */
    public Workflow lookup(String name, boolean mandatory) throws IOException, SmartFrogException {
        Prim prim = nodeDeploy.lookupPrim(name, mandatory);
        return new Workflow(this, name, prim);
    }

    /**
     * Resolve and terminate a workflow
     *
     * @param name the workflow name
     * @param normal flag for normal exit; false for abnormal
     * @param exitText exit text
     * @return true if the resolution succeeded and the termination begain
     * @throws IOException for network problems
     * @throws SmartFrogException any resolution problems
     */
    public boolean terminate(String name, boolean normal, String exitText) throws IOException, SmartFrogException {
        return nodeDeploy.terminateApplication(name, normal, exitText);
    }


    /**
     * Resolve the farmer using our URL path as the path.
     * @return the resolve cluster farmer
     * @throws SmartFrogResolutionException resolution or binding problems
     * @throws IOException network trouble
     */
    public ClusterFarmer resolveFarmer()
            throws SmartFrogResolutionException, IOException {
        URL server = getTargetURL();
        String path = server.getPath();
        return resolveFarmer(getBoundProcess(), path);
    }

    /**
     * Resolve the farmer.
     * @param path path to convert and resolve
     * @return the resolve cluster farmer
     * @throws SmartFrogResolutionException if the path does not resolve, or what it resolves to is something
     * unexpected
     * @throws IOException network trouble
     */
    public ClusterFarmer resolveFarmer(String path)
            throws SmartFrogResolutionException, IOException {
        return resolveFarmer(getBoundProcess(), path);
    }

    /**
     * code to resolve the farmer. This is kept separate just to make testing easier
     *
     * @param process process to work with
     * @param path path to convert and resolve
     * @return the resolve cluster farmer
     * @throws SmartFrogResolutionException if the path does not resolve, or what it resolves to is something
     * unexpected
     * @throws IOException network trouble
     */
    public static ClusterFarmer resolveFarmer(ProcessCompound process, String path)
            throws SmartFrogResolutionException, IOException {
        if (process == null) {
            throw new SmartFrogResolutionException("Null ProcessCompound parameter");
        }
        String newpath = convertPath(path);
        Reference ref = new Reference(newpath, true);
        Prim farmerPrim;
        farmerPrim = process.sfResolve(ref, (Prim) null, true);
        if (!(farmerPrim instanceof ClusterFarmer)) {
            throw new SmartFrogResolutionException(
                    "There is no ClusterFarmer at " + newpath + " instead an instance of "
                            + farmerPrim.getClass(), farmerPrim);
        }
        return (ClusterFarmer) farmerPrim;
    }

    /**
     * Do any path conversion to make it easier to resolve references
     *
     * @param path path to convert
     * @return processed path. Default expansion and / to : conversion will have taken place, leading / stripped
     */
    public static String convertPath(String path) {
        String newpath;
        newpath = path.replace('/', ':');
        while (newpath.startsWith(":")) {
            newpath = newpath.substring(1);
        }
        while (newpath.endsWith(":")) {
            newpath = newpath.substring(0, newpath.length() - 1);
        }
        if (newpath.isEmpty()) {
            newpath = TemplateNames.FARMER_PATH;
        }
        return newpath;
    }
}
