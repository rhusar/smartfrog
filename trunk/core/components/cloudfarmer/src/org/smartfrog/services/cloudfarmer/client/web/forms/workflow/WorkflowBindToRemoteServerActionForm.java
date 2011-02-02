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

package org.smartfrog.services.cloudfarmer.client.web.forms.workflow;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.smartfrog.services.cloudfarmer.client.web.forms.AbstractMombasaActionForm;

import javax.servlet.http.HttpServletRequest;

/**
 * Form to bind the workflow to a remote server
 */

public class WorkflowBindToRemoteServerActionForm extends AbstractMombasaActionForm {

    private String hostname = "localhost";
    private int port = 3800;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    /**
     * {@inheritDoc}
     *
     * @param actionMapping mapping
     * @param request       request
     * @return a list of errors
     */
    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors actionErrors = super.validate(actionMapping, request);
        if (port <= 0 || port > 65535) {
            actionErrors = addError(actionErrors, "port", "error.portRange");
        }
        if (isEmptyOrNull(hostname)) {
            actionErrors = addError(actionErrors, "hostname", "error.noHostname");
        }
        return actionErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkflowBindToRemoteServerActionForm that = (WorkflowBindToRemoteServerActionForm) o;

        if (port != that.port) return false;
        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hostname != null ? hostname.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
