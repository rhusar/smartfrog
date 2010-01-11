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

    @Override
    public boolean isNodeDeploymentSupported() throws IOException, SmartFrogException {
        return true;
    }

    /**
     * This is the mock node; actions are logged but not acted on
     */
    private class MockNodeDeploymentInstance extends AbstractNodeDeployment implements NodeDeploymentService {

        private String hostname;

        private MockNodeDeploymentInstance(ClusterNode node) {
            super(node);
            hostname = node.getHostname();
        }

        @Override
        public void deployApplication(String name, ComponentDescription cd) throws IOException, SmartFrogException {
            sfLog().info("Deploying " + name + " at " + hostname);
        }

        @Override
        public boolean terminateApplication(String name, boolean normal, String exitText)
                throws IOException, SmartFrogException {
            sfLog().info(
                    "Terminating " + name + (normal ? "normally" : "abnormally") + " " + exitText + " at " + hostname);
            return true;
        }

        @Override
        public void pingApplication(String name) throws IOException, SmartFrogException {
            sfLog().info("Pinging " + name + " at " + hostname);
        }

        @Override
        public String getServiceDescription() throws IOException, SmartFrogException {
            return "Mock to " + hostname;
        }

        @Override
        public String getApplicationDescription(String name) throws IOException, SmartFrogException {
            return "Mock application " + name + " at " + hostname;
        }

        @Override
        public void terminate() throws IOException, SmartFrogException {

        }


    }
}