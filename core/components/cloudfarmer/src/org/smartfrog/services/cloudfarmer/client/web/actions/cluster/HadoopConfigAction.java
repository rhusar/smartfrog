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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.methods.GetMethod;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.server.examples.HadoopRoleNames;
import org.smartfrog.services.cloudfarmer.api.NodeLink;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Generate the Hadoop Config
 */

public class HadoopConfigAction extends AbstractClusterAction {

    /**
     * {@inheritDoc}
     */

    protected String getActionName() {
        return "HadoopConfigAction";
    }

    /**
     * Lists the hosts to the "hosts" attribute
     *
     * @param mapping    mapping
     * @param form       incoming form
     * @param request    incoming request
     * @param response   response to build up
     * @param controller the cluster controller
     * @return the follow-up action
     * @throws Exception any exception to handle server-side
     */
    @Override
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 ClusterController controller) throws Exception {

        try {
            log.info("Building the Hadoop config file");
            addClusterAttributes(request, controller);
            String farmerDescription = controller.getRemoteDescription();
            HostInstance master = controller.getMaster();
            if (master == null) {
                return forwardErrorAction(request, mapping,
                        ACTION_NOT_FOUND,
                        "Cluster has no hadoop master",
                        null,
                        null);
            }
            String hostname = master.getExternalHostname();

            NodeLink nodeLink = master.resolveNodeLink(HadoopRoleNames.LINK_NAMENODE_CONFIGURATION);
            HttpClient httpclient = new HttpClient();
            HttpMethod getRequest = new GetMethod(nodeLink.getPath());
            HttpState state = new HttpState();
            HttpConnection conn = new HttpConnection(hostname, 
                    nodeLink.getPort(), 
                    Protocol.getProtocol(nodeLink.getProtocol()));
            //do the work
            getRequest.execute(state, conn);


            String configXML = controller.getDiagnosticsText();
            request.setAttribute("hadoopXML", configXML);
            return success(mapping);
        } catch (Exception e) {
            return failure(request, mapping, "Failed to get the configuration :" + e, e);
        }
    }
}