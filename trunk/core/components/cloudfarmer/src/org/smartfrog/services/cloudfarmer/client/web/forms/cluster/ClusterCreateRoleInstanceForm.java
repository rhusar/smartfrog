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


package org.smartfrog.services.cloudfarmer.client.web.forms.cluster;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.smartfrog.services.cloudfarmer.client.web.actions.cluster.AbstractClusterAction;
import org.smartfrog.services.cloudfarmer.client.web.forms.AbstractMombasaActionForm;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;

import javax.servlet.http.HttpServletRequest;

/**
 * Form for adding a node on a dynamic cluster
 */
public class ClusterCreateRoleInstanceForm extends AbstractMombasaActionForm {


    private int minNodes = 1;
    private int maxNodes = 1;
    private String role;

    public int getMinNodes() {
        return minNodes;
    }

    public void setMinNodes(int minNodes) {
        this.minNodes = minNodes;
    }

    public int getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * {@inheritDoc}
     *
     * @param actionMapping mappings
     * @param request       request
     */
    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest request) {
        super.reset(actionMapping, request);
        minNodes = 1;
        maxNodes = 1;

        Object rolename = request.getAttribute(AttributeNames.ATTR_ROLE);
        log.info("Resetting create instance form to role " + rolename);
        if (rolename != null) {
            String s = rolename.toString().trim();
            if (!s.isEmpty()) {
                role = s;
            }
        }
    }


    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors actionErrors = super.validate(mapping, request);
        try {
            ClusterController controller;
            controller = AbstractClusterAction.bindToClusterController(request);
        } catch (Exception e) {
            actionErrors = addError(actionErrors, "name", "error.no.controller");
        }
        return actionErrors;
    }


}
