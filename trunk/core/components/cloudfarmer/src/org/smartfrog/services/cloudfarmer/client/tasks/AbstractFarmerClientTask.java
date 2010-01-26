/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cloudfarmer.client.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.client.common.BaseRemoteDaemon;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * Base class for our farmer clients
 */
abstract class AbstractFarmerClientTask extends Task {

    /** {@value} */
    public static final String ERROR_NO_FARMER_URL = "No farmerURL defined";

    protected String farmerURL = "";

    protected String username;

    protected String password;
    private BaseRemoteDaemon daemon;

    public String getFarmerURL() {
        return farmerURL;
    }

    /**
     * Set the URL of the farmer.
     *
     * @param farmerURL URL of the farmer
     */
    public void setFarmerURL(String farmerURL) {
        this.farmerURL = farmerURL;
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

    /**
     * Resolve the remote daemon
     * @return a bound daemon
     * @throws SmartFrogException binding problems
     * @throws IOException network problems
     * @throws BuildException no farmer was provided
     */
    protected synchronized BaseRemoteDaemon resolveRemoteDaemon() throws SmartFrogException, IOException {
        if (daemon != null) {
            return daemon;
        }
        validateFarmerURL();
        daemon = new BaseRemoteDaemon(farmerURL);
        daemon.bindOnDemand();
        return daemon;
    }

    /**
     * validate the farmer settings
     * @throws BuildException no farmer was provided
     */
    protected void validateFarmerURL() {
        if (noFarmerURL()) {
            throw new BuildException(ERROR_NO_FARMER_URL);
        }
    }

    protected boolean noFarmerURL() {
        return farmerURL == null || farmerURL.isEmpty();
    }

    protected ClusterFarmer resolveFarmer() throws SmartFrogException, IOException {
        return resolveRemoteDaemon().resolveFarmer();
    }

    

    protected BuildException forward(Throwable t) {
        if (t instanceof BuildException) {
            return (BuildException) t;
        }
        BuildException be = new BuildException(t.getMessage()
                + (noFarmerURL() ? "" : " FarmerURL: " + farmerURL),
                t);
        return be;
    }
}
