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

import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smartfrog.services.cloudfarmer.client.web.actions.cluster.AbstractMombasaAction;


/**
 * This action always succeeds
 */
@SuppressWarnings({"RefusedBequest"})
public class SuccessAction extends AbstractMombasaAction {

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name
     */

    @Override
    protected String getActionName() {
        return "SuccessAction";
    }


    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        return success(mapping);
    }

    public static ClusterController getFarmController(HttpServletRequest request) {
        return (ClusterController) getAttributeFromRequestState(request, FARM_CONTROLLER);
    }


}