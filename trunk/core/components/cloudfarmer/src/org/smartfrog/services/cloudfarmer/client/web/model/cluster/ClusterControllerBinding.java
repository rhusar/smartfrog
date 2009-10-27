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
package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created 03-Sep-2009 15:19:07
 */

public class ClusterControllerBinding implements Serializable {

    /**
     * The default controller is {@link ClusterControllerFactory#SMARTFROG_CONTROLLER}
     */
    
    public static final int DEFAULT_CONTROLLER = ClusterControllerFactory.SMARTFROG_CONTROLLER;
    private int controller = DEFAULT_CONTROLLER;

    private String URL = ClusterControllerFactory.DEFAULT_FARMER_URL;

    private String username;

    private String password;

    private static final String PREFIX = "ClusterControllerBinding.";


    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
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
     * Persist the controller binding
     *
     * @param prefs the preferences to use
     * @throws IOException        trouble saving
     */
    public void saveBinding(Object prefs) throws IOException {
/*        prefs.setValue(PREFIX + "controller", Integer.toString(controller));
        prefs.setValue(PREFIX + "url", URL);
        prefs.setValue(PREFIX + "username", username);
        prefs.setValue(PREFIX + "password", password);
        prefs.store();*/
    }

    public void loadBinding(Object prefs) {
/*        String c = prefs.getValue(PREFIX + "controller", Integer.toString(controller));
        controller = Integer.valueOf(c);
        URL = prefs.getValue(PREFIX + "url", URL);
        username = prefs.getValue(PREFIX + "username", username);
        password = prefs.getValue(PREFIX + "password", password);*/
    }


}
