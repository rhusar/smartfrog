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

import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterControllerBinding;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterControllerFactory;
import org.smartfrog.services.cloudfarmer.client.web.forms.AbstractMombasaActionForm;

/**
 * @author slo
 */
public class ClusterChangeManagerForm extends AbstractMombasaActionForm {


    private int controller = ClusterControllerBinding.DEFAULT_CONTROLLER;

    private String url;

    private String username;

    private String password;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getController() {
        return controller;
    }

    public void setController(int controller) {
        this.controller = controller;
    }

    /**
     * read, trim and maybe replace cluster controller bindings
     *
     * @return
     */
    public ClusterControllerBinding createControllerBinding() {
        ClusterControllerBinding binding = new ClusterControllerBinding();
        int controllerID = getController();
        binding.setController(controllerID);
        String newurl = getUrl();
        if (newurl.trim().isEmpty()) {
            newurl = ClusterControllerFactory.getDefaultURL(controllerID);
            setUrl(newurl);
        }
        binding.setURL(newurl);

        binding.setPassword(getPassword().trim());
        binding.setUsername(getUsername().trim());
        return binding;
    }
}