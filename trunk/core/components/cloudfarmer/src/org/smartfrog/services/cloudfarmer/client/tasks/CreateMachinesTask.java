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
package org.smartfrog.services.cloudfarmer.client.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Ask the farmer for a number of machines
 */

public class CreateMachinesTask extends AbstractRoleDrivenFarmerClientTask {
    protected int min;
    protected int max;
    private File destFile;
    private String propertyPrefix = "";

    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }

    public void setPropertyPrefix(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Execut the action
     *
     * @throws BuildException if something goes wrong with the build.
     */
    @Override
    public void execute() throws BuildException {
        checkRoleDefined();
        try {
            ClusterFarmer farmer = resolveFarmer();
            ClusterNode[] clusterNodes;
            clusterNodes = farmer.create(role, min, max);
            Properties props = new Properties();
            for (int i = 0; i < clusterNodes.length; i++) {
                ClusterNode node = clusterNodes[i];
                String propertyKey = propertyPrefix + i;
                String propertyValue = node.getExternalHostname();
                props.setProperty(propertyKey, propertyValue);
                getProject().setNewProperty(propertyKey, propertyValue);
            }

            if (destFile != null) {
                OutputStream out = null;
                try {
                    out = new FileOutputStream(destFile);
                    props.store(out, "");
                } finally {
                    FileUtils.close(out);
                }
            }
        } catch (SmartFrogException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
