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
import org.apache.struts.util.LabelValueBean;
import org.smartfrog.services.cloudfarmer.client.web.forms.AbstractMombasaActionForm;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Any action agaisnt a workflow server
 */
public abstract class AbstractWorkflowServerActionForm extends AbstractMombasaActionForm {


    private ArrayList<LabelValueBean> options = new ArrayList<LabelValueBean>();
    private String name;
    private boolean isNameRequired = true;

    public ArrayList<LabelValueBean> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<LabelValueBean> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNameRequired() {
        return isNameRequired;
    }

    public void setNameRequired(boolean nameRequired) {
        isNameRequired = nameRequired;
    }

    /**
     * validate the request
     *
     * @param actionMapping      mapping
     * @param httpServletRequest request
     * @return any errors
     */
    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        ActionErrors errors = super.validate(actionMapping, httpServletRequest);
        if (isNameRequired && isEmptyOrNull(name)) {
            errors = addError(errors, "name", "error.noName");
        }
        return errors;
    }

}
