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
import org.apache.struts.action.ActionMessage;
import org.smartfrog.services.cloudfarmer.client.web.forms.AbstractMombasaActionForm;

import javax.servlet.http.HttpServletRequest;


/**
 * @author slo
 */
public class ClusterCreateActionForm extends AbstractMombasaActionForm {

    private int minimumSize;
    private int maximumSize;
    private String clusterName;

    /**
     * Validate the cluster
     *
     * @param mapping mapping
     * @param request servlet request
     * @return any errors
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        if (minimumSize < 1) {
            errors.add("minimumSize", new ActionMessage("error.minimumSize.tooLow", minimumSize));
        } else if (maximumSize < minimumSize) {
            errors.add("maximumSize",
                    new ActionMessage("error.maximumSize.belowMinimumSize", minimumSize, maximumSize));
        }
        return errors;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public int getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(int minimumSize) {
        this.minimumSize = minimumSize;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }


}
