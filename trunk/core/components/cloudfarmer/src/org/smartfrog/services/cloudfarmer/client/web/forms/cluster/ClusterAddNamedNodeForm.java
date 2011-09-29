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
 * Form for adding a node
 */
public class ClusterAddNamedNodeForm extends AbstractMombasaActionForm {

    private String name;

    private String url;

    public boolean master = false;

    public boolean worker = false;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public boolean isWorker() {
        return worker;
    }

    public void setWorker(boolean worker) {
        this.worker = worker;
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
        worker = false;
        master = false;
        name = "";
        url = "";
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors actionErrors = super.validate(mapping, request);
        try {
            ClusterController controller;
            controller = AbstractClusterAction.bindToClusterController(request);
            name = name.trim();
            if (name.isEmpty()) {
                actionErrors = addError(actionErrors, "name", "error.name.empty");
            } else {
                if (controller.lookupHost(name) != null) {
                    actionErrors = addError(actionErrors, "name", "error.name.already.defined");
                }
            }
            controller.refreshHostList();
            boolean hasMaster = controller.hasMaster();
            if (isWorker() && isMaster()) {
                actionErrors = addError(actionErrors, "master", "error.twin.roles.not.supported");
            }
            if (!isMaster() && !hasMaster) {
                actionErrors = addError(actionErrors, "master", "error.no.master");
            }
            if (isMaster() && hasMaster) {
                actionErrors = addError(actionErrors, "master", "error.duplicate.master");
            }
        } catch (Exception e) {
            actionErrors = addError(actionErrors, "name", "error.no.controller");
        }
        return actionErrors;
    }


}
