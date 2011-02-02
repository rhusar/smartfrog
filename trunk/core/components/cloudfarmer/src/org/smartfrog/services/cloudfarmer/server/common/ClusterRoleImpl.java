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
package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.Range;
import org.smartfrog.services.cloudfarmer.api.NodeLink;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;


/**
 * Prim that defines a cluster role
 */

public class ClusterRoleImpl extends PrimImpl implements ClusterRole {

    private ClusterRoleInfo info;

    public ClusterRoleImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        info = resolveRoleInfo(this);
        info.setName(sfCompleteName.lastElement().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterRoleInfo buildClusterRoleInfo() throws RemoteException {
        return info.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Cluster Role " + info;
    }

    /**
     * This will look up the role information
     * 
     * @param name name to fill in
     * @return role info 
     * @throws RemoteException              network trouble
     * @throws SmartFrogResolutionException resolution problems
     */
    public ClusterRoleInfo resolveRoleInfo(String name) throws RemoteException, SmartFrogResolutionException {
        ClusterRoleInfo roleInfo = resolveRoleInfo(this);
        roleInfo.setName(name);
        return roleInfo;
    }


    /**
     * This will build the role information. that includes options.
     *
     * @param target info target
     * @return role info -without any name
     * @throws RemoteException              network trouble
     * @throws SmartFrogResolutionException resolution problems
     */
    public static ClusterRoleInfo resolveRoleInfo(Prim target) throws RemoteException, SmartFrogResolutionException {
        ClusterRoleInfo role = new ClusterRoleInfo();
        role.setDescription(target.sfResolve(ATTR_DESCRIPTION, "", true));
        role.setLongDescription(target.sfResolve(ATTR_LONG_DESCRIPTION, "", true));
        role.setRoleSize(resolveRange(target, ATTR_MIN, ATTR_MAX));
        role.setRecommendedSize(resolveRange(target, ATTR_RECOMMENDED_MIN, ATTR_RECOMMENDED_MAX));
        ComponentDescription cd = null;
        cd = target.sfResolve(ATTR_OPTIONS, cd, true);
        Iterator optionset = cd.sfAttributes();
        while (optionset.hasNext()) {
            String optionKey = (String) optionset.next();
            String value = cd.sfResolve(optionKey).toString();
            role.replaceOption(optionKey, value);
        }
        role.setLinks(resolveLinks(target));
        return role;
    }

    /**
     * resolve a range pair
     *
     * @param target target component
     * @param minName name of the min attribute
     * @param maxName name of the max attribute
     * @return the new range
     * @throws RemoteException              network trouble
     * @throws SmartFrogResolutionException resolution problems
     */
    public static Range resolveRange(Prim target, String minName, String maxName)
            throws RemoteException, SmartFrogResolutionException {
        int min = target.sfResolve(minName, 0, true);
        int max = target.sfResolve(maxName, 0, true);
        Range range = new Range(min, max);
        return range;
    }

    /**
     * resolve the links; made static for reuse
     * @param target target prim
     * @return the list of links (unbound)
     * @throws SmartFrogResolutionException failed to resolve 
     * @throws RemoteException network 
     */
    public static NodeLink[] resolveLinks(Prim target) throws SmartFrogResolutionException, RemoteException {
        ComponentDescription linksCD = target.sfResolve(new Reference(ATTR_LINKS),
                (ComponentDescription) null, true);
        Reference targetRef = target.sfCompleteName();
        return resolveLinks(targetRef, linksCD);
    }

    /**
     * resolve the links; made static for reuse
     * @param targetRef ref to the target
     * @param linksCD CD of links
     * @return the list of links (unbound)
     * @throws SmartFrogResolutionException failed to resolve 
     * @throws RemoteException network 
     */
    public static NodeLink[] resolveLinks(Reference targetRef, ComponentDescription linksCD)
            throws SmartFrogResolutionException {
        Context ctx = linksCD.sfContext();
        int size = ctx.size();
        ArrayList<NodeLink> links = new ArrayList<NodeLink>(size);
        for (int i = 0; i < size; i++) {
            String name = ctx.getKey(i).toString();
            Object value = ctx.getVal(i);
            if (value instanceof Vector) {
                Vector vector = (Vector) value;
                if (vector.size() != 3) {
                    throw new SmartFrogResolutionException(targetRef,
                            new Reference(ATTR_LINKS),
                            "Wrong number of elements in links entry " + name);
                }
                String protocol = vector.get(0).toString();
                int port;
                Object o = vector.get(1);
                if (o instanceof Integer) {
                    port = (Integer) o;
                } else {
                    String portString = o.toString();
                    try {
                        port = Integer.valueOf(portString);
                    } catch (NumberFormatException e) {
                        throw new SmartFrogResolutionException(targetRef,
                                new Reference(ATTR_LINKS),
                                "Not an integer '" + portString + "' in link entry " + name);
                    }
                }
                String path = vector.get(2).toString();
                NodeLink link = new NodeLink(name, protocol, port, path);
                links.add(link);
            }
        }
        return links.toArray(new NodeLink[links.size()]);
    }

}
