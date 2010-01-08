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
package org.smartfrog.services.cloudfarmer.server.ec2;

import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import org.smartfrog.services.amazon.ec2.EC2Instance;
import org.smartfrog.services.amazon.ec2.EC2Utils;
import org.smartfrog.services.cloudfarmer.server.common.ClusterRoleImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created 29-Sep-2009 11:37:58
 */

public class EC2ClusterRole extends ClusterRoleImpl implements EC2Instance {
    public static final String ERROR_NO_VALID_IMAGE_ID = "No valid imageID for role ";

    public EC2ClusterRole() throws RemoteException {
    }

    /**
     * extends the parent's startup actions by creating a launch configuration
     * @throws SmartFrogException SF problems
     * @throws RemoteException network problems
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        LaunchConfiguration lc = createLaunchConfiguration("none", this);
    }


    /**
     * Create an EC2 launch configuration from a target role 
     * @param role role name
     * @param targetRole the prim defining the role. It is not required that this is an instance of EC2ClusterRole
     * @return the launch configuration
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     */
    public static LaunchConfiguration createLaunchConfiguration(String role, Prim targetRole)
            throws SmartFrogResolutionException, RemoteException {
        String imageID = targetRole.sfResolve(ATTR_IMAGE_ID, "", true);
        String zone = targetRole.sfResolve(ATTR_AVAILABILITY_ZONE, "", true);
        String keyName = targetRole.sfResolve(ATTR_KEY_NAME, "", true);
        String userData = targetRole.sfResolve(ATTR_USER_DATA, "", true);
        InstanceType size = InstanceType.getTypeFromString(targetRole.sfResolve(ATTR_INSTANCE_TYPE, "", true));
        Vector<String> groups = ListUtils.resolveStringList(targetRole, new Reference(ATTR_SECURITY_GROUP), true);
        LaunchConfiguration lc = new LaunchConfiguration(imageID.trim());
        lc.setConfigName(role);
        lc.setAvailabilityZone(zone);
        lc.setInstanceType(size);
        lc.setKeyName(keyName);
        lc.setSecurityGroup(groups);
        lc.setUserData(userData.getBytes());
        if (lc.getImageId().isEmpty()) {
            throw new SmartFrogResolutionException(ERROR_NO_VALID_IMAGE_ID + role + " " + EC2Utils.convertToString(lc)
                    + ("- raw image ID :'" + imageID + "'"));
        }
        return lc;
    }

}
