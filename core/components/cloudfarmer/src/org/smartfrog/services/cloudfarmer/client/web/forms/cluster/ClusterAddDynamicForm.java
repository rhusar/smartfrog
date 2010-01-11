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
public class ClusterAddDynamicForm extends AbstractMombasaActionForm {


    private int minWorkers = 1;
    private int maxWorkers = 1;

    public boolean large = false;

    public int getMinWorkers() {
        return minWorkers;
    }

    public void setMinWorkers(int minWorkers) {
        this.minWorkers = minWorkers;
    }


    public int getMaxWorkers() {
        return maxWorkers;
    }

    public void setMaxWorkers(int maxWorkers) {
        this.maxWorkers = maxWorkers;
    }

    public boolean isLarge() {
        return large;
    }

    public void setLarge(boolean large) {
        this.large = large;
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
        large = false;
        minWorkers = 1;
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