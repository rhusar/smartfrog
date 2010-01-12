package org.smartfrog.services.cloudfarmer.server.mock;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.services.cloudfarmer.server.deployment.AbstractNodeDeployment;
import org.smartfrog.services.cloudfarmer.server.deployment.NodeDeploymentHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Mock nodes
 */
public class MockNodeDeploymentFactory extends PrimImpl implements NodeDeploymentServiceFactory {
    
    public MockNodeDeploymentFactory() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException {
        return NodeDeploymentHelper.export(new MockNodeDeploymentInstance(node));
    }


    /**
     * {@inheritDoc}
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException {
        return "MockNodeDeployment";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNodeDeploymentSupported() throws IOException, SmartFrogException {
        return true;
    }

    /**
     * This is the mock node; actions are logged but not acted on
     */
    private class MockNodeDeploymentInstance extends AbstractNodeDeployment implements NodeDeploymentService {

        private String hostname;
        Hashtable<String, ComponentDescription> applications = new Hashtable<String, ComponentDescription>();

        private MockNodeDeploymentInstance(ClusterNode node) {
            super(node);
            hostname = node.getHostname();
        }


        private ComponentDescription lookup(String app) {
            return applications.get(app);
        }

        private boolean appExists(String name) {
            return lookup(name) != null;
        }

        private void checkAppExists(String name) throws SmartFrogException {
            if (!appExists(name)) {
                throw new SmartFrogException("No application " + name);
            }
        }

        private void checkAppDoesntExist(String name) throws SmartFrogException {
            if (appExists(name)) {
                throw new SmartFrogException("Application already deployed " + name);
            }
        }


        @Override
        public void deployApplication(String name, ComponentDescription cd) throws IOException, SmartFrogException {
            sfLog().info("Deploying " + name + " at " + hostname);
            checkAppDoesntExist(name);
            applications.put(name, cd);
        }

        @Override
        public boolean terminateApplication(String name, boolean normal, String exitText)
                throws IOException, SmartFrogException {
            sfLog().info(
                    "Terminating " + name + (normal ? "normally" : "abnormally") + " " + exitText + " at " + hostname);
            if (!appExists(name)) {
                return false;
            }
            applications.remove(name);
            return true;
        }

        @Override
        public void pingApplication(String name) throws IOException, SmartFrogException {
            sfLog().info("Pinging " + name + " at " + hostname);
            checkAppExists(name);
        }

        @Override
        public String getServiceDescription() throws IOException, SmartFrogException {
            return "Mock to " + hostname;
        }

        @Override
        public String getApplicationDescription(String name) throws IOException, SmartFrogException {
            checkAppExists(name);
            return "Mock application " + name + " at " + hostname;
        }

        @Override
        public void terminate() throws IOException, SmartFrogException {

        }


    }

}