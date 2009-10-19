/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.services.cloudfarmer.client.web.actions.cluster;

import org.smartfrog.services.cloudfarmer.client.web.exceptions.BadParameterException;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.AttributeNames;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HadoopCluster;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.sfcore.common.SmartFrogException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * struts action base class
 */
public abstract class AbstractMombasaAction extends AbstractStrutsAction implements AttributeNames {


    public static String bindClusterServer(HttpServletRequest request, boolean required)
            throws BadParameterException {
        return parameterToAttribute(request, CLUSTER, CLUSTER, required);
    }

    public static String bindWorkflowServer(HttpServletRequest request, boolean required)
            throws BadParameterException {
        return parameterToAttribute(request, WORKFLOWURL, WORKFLOWURL, required);
    }

    public static String bindFarmController(HttpServletRequest request, boolean required)
            throws BadParameterException {
        return parameterToAttribute(request, FARM_CONTROLLER_URL, FARM_CONTROLLER_URL, required);
    }


    public static HadoopCluster getCluster(HttpServletRequest request) {
        return (HadoopCluster) getAttributeFromRequestState(request, HADOOP_CLUSTER);
    }

    public static Workflow getWorkflow(HttpServletRequest request) {
        return (Workflow) getAttributeFromRequestState(request, WORKFLOW_INSTANCE);
    }

    public static void setWorkflow(HttpServletRequest request, Workflow workflow) {
        setAttribute(request, WORKFLOW_INSTANCE, workflow);
    }


    /**
     * Return any existing hadoop cluster on the request, or bind to one via the parameters
     *
     * @param request incoming request
     * @return the cluster
     * @throws BadParameterException if the cluster binding fails
     */
    public static HadoopCluster bindToHadoopCluster(HttpServletRequest request) throws BadParameterException {
        HadoopCluster cluster = getCluster(request);
        if (cluster == null) {
            String clusterURL = bindClusterServer(request, true);
            cluster = new HadoopCluster(clusterURL);
            setAttribute(request, HADOOP_CLUSTER, cluster);
        }
        return cluster;
    }


    public static RemoteDaemon getRemoteDaemon(HttpServletRequest request) {
        return (RemoteDaemon) request.getAttribute(REMOTE_DAEMON);
    }

    public static String bindRemoteDaemon(HttpServletRequest request, boolean required)
            throws BadParameterException {
        return parameterToAttribute(request, REMOTE_DAEMON_URL, REMOTE_DAEMON_URL, required);
    }

    public static RemoteDaemon bindToRemoteDaemon(HttpServletRequest request) throws SmartFrogException, IOException {
        RemoteDaemon server = getRemoteDaemon(request);
        if (server == null) {
            String url = bindRemoteDaemon(request, false);
            if (url == null) {
                url = "http://localhost";
                setAttribute(request, REMOTE_DAEMON_URL, url);
            }
            LOG.info("binding to remote server at " + url);
            try {
                server = new RemoteDaemon(url);
                server.bindOnDemand();
            } catch (IOException e) {
                LOG.error("Failed to bind to " + server, e);
                throw new IOException("Failed to connect to " + server + ":" + e, e);
            } catch (SmartFrogException e) {
                LOG.error("Failed to bind to " + server + " " + e, e);
                throw e;
            }
        }
        return server;
    }

    /**
     * Bind a request to a specific workflow instance
     *
     * @param request incoming request
     * @return the workflow
     * @throws Exception anything that went wrong
     */
    public static Workflow bindToWorkflow(HttpServletRequest request) throws Exception {
        //TODO
        return null;

    }


}
