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
package org.smartfrog.services.cloudfarmer.client.components;

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created 25-Nov-2009 14:41:35
 */

public class FarmCheckRoles extends AbstractFarmWorkflowClient implements FarmCustomer {

    private String successText;

    /**
     * {@value}
     */
    public static final String ATTR_ROLES = "roles";
    /**
     * {@value}
     */
    public static final String ATTR_USE_LIST_CLUSTER_ROLES = "useListClusterRoles";

    public FarmCheckRoles() throws RemoteException {
    }

    @Override
    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

    @Override
    protected void startupAction() throws IOException, SmartFrogException {
        boolean useListClusterRoles = sfResolve(ATTR_USE_LIST_CLUSTER_ROLES, true, true);
        List<String> roles = ListUtils.resolveStringList(this, new Reference(ATTR_ROLES), true);
        List<String> actual;
        if (useListClusterRoles) {
            actual = checkListClusterRoles(roles);
        } else {
            actual = checkListRoles(roles);
        }
        checkRoles(roles, actual);
        setSuccessText("Role list as expected");
    }

    private List<String> checkListClusterRoles(List<String> roles) throws IOException, SmartFrogException {
        ClusterRoleInfo[] clusterRoles = getFarmer().listClusterRoles();
        List<String> actual = new ArrayList<String>(clusterRoles.length);
        for (ClusterRoleInfo role : clusterRoles) {
            if (role == null) {
                throw new SmartFrogException("Empty Role in Cluster Role list");
            }
            String name = role.getName();
            if (name == null) {
                throw new SmartFrogException("No name in role " + role);
            }
            actual.add(name);
        }
        return actual;
    }

    private List<String> checkListRoles(List<String> roles) throws IOException, SmartFrogException {
        String[] availableRoles = getFarmer().listAvailableRoles();
        return Arrays.asList(availableRoles);
    }


    /**
     * Check the roles
     *
     * @param roles  expected
     * @param actual actual
     * @throws SmartFrogException any mismatch in roles
     */
    private void checkRoles(List<String> roles, List<String> actual) throws SmartFrogException {
        Collections.sort(roles);
        Collections.sort(actual);
        String expectedString = stringify(roles);
        String actualString = stringify(actual);
        String expectedVsActual = "Expected " + expectedString + " actual " + actualString;
        if (roles.size() != actual.size()) {
            throw new SmartFrogException(" Mismatch in role count."
                    + expectedVsActual);
        }
        Iterator<String> rolesIt = roles.iterator();
        Iterator<String> actualIt = actual.iterator();
        for (String role : roles) {
            if (!(role.equals(actualIt.next()))) {
                throw new SmartFrogException("Different roles than expected. " + expectedVsActual);
            }
        }
    }

    String stringify(List<String> roles) {
        StringBuilder result = new StringBuilder("[ ");
        for (String role : roles) {
            result.append(role);
            result.append(" ");
        }
        result.append("]");
        return result.toString();
    }

}
