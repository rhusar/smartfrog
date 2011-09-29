/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.servlets;

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.NodeLink;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.server.examples.HadoopRoleNames;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Date;

/**
 * Created 25-Feb-2010 17:14:03
 */

public class HostsAndRolesServlet extends AbstractMombasaServlet {

    public static final String ERROR_NO_NAMENODE_LINK = "Could not find namenode link";
    private static final String PARAM_PREFIX = "prefix";
    private static final String DEFAULT_PREFIX = "cluster";
    private static final String ROLE = "role";
    private static final String HOST = "host";
    private static final String SIZE = "size";

    public HostsAndRolesServlet() {
    }


    /**
     * This is the inner GET action bonded to a controller. The host and roles lists are refreshed
     *
     * @param request    incoming request
     * @param response   outgoing response
     * @param controller the controller
     * @throws IOException IO problems
     */
    @Override
    protected void getAction(HttpServletRequest request,
                             HttpServletResponse response,
                             ClusterController controller) throws IOException {

        String prefix;
        prefix = request.getParameter(PARAM_PREFIX);
        if (prefix == null) {
            prefix = DEFAULT_PREFIX;
        }
        if (prefix.length() > 0) {
            prefix = prefix + ".";
        }
        Properties properties = new Properties();
        List<ClusterRoleInfo> roleList = controller.getRoles();
        HostInstanceList hosts = controller.getHosts();

        //publish the sizes
        properties.put(prefix + ROLE + "." + SIZE, "" + roleList.size());
        

        //run through the role list
        Map<String, HostInstanceList> roleMap = new HashMap<String, HostInstanceList>(roleList.size());
        int count = 1;
        for (ClusterRoleInfo role : roleList) {
            String roleName = role.getName();
            properties.put(prefix + ROLE + "." + (count++), roleName);
            roleMap.put(roleName, new HostInstanceList());
        }

        //run through the hosts
        count = 1;
        for (HostInstance host : hosts) {
            String hostname = host.getExternalHostname();
            if (hostname != null) {
                properties.put(prefix + HOST + "." + (count), hostname);
                String roleName = host.getRole();
                properties.put(prefix + HOST + "." + (count) + ".role", roleName);
                count++;
                //now the role list
                HostInstanceList hostsInRole = roleMap.get(roleName);
                if (hostsInRole == null) {
                    //just in case the role count changes
                    roleMap.put(roleName, new HostInstanceList());
                }
                hostsInRole.add(host);
            }
        }
        properties.put(prefix + HOST + "." + SIZE, "" + count);
        
        //now for each role, push out the hosts
        for (String roleName: roleMap.keySet()) {
            HostInstanceList hostList = roleMap.get(roleName);
            String roleprefix = prefix + ROLE + "." + roleName + ".";
            properties.put(roleprefix + SIZE, ""+ hostList.size());
            count = 1;
            for (HostInstance host : hostList) {
                properties.put(roleprefix + (count++) , host.getExternalHostname());
            }
        }
        
        //now we have a property list of hosts and roles and things
        response.setContentType("text/plain; charset=ISO_8859-1");
        Date now = new Date(System.currentTimeMillis());
        String comments = "Created "+now.toString();
        ServletOutputStream responseOut = response.getOutputStream();
        properties.store(responseOut, comments);
        responseOut.close();
        
    }


}