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
package org.smartfrog.services.amazon.farmer;

import com.xerox.amazonws.ec2.LaunchConfiguration;
import org.smartfrog.services.amazon.ec2.InstanceList;
import org.smartfrog.sfcore.prim.Prim;

/**
 * This is the binding of roles that is created at deploy time; it can be used to remember which instances are mapped where
 */

public class RoleBinding {
    
    private String role;
    private LaunchConfiguration launchConfig;
    private String description;
    private Prim source;
    private InstanceList instances;


    public RoleBinding(String role, Prim source, LaunchConfiguration launchConfig) {
        this.role = role;
        this.source = source;
        this.launchConfig = launchConfig;
        description = EC2ClusterRole.convertToString(launchConfig);
        instances = new InstanceList();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LaunchConfiguration getLaunchConfig() {
        return launchConfig;
    }

    public void setLaunchConfig(LaunchConfiguration launchConfig) {
        this.launchConfig = launchConfig;
    }

    public Prim getSource() {
        return source;
    }

    public void setSource(Prim source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Role "+ role + " " + description;
    }
}
