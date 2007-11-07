/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ant;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;

import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * This is a little class to turn every ant property into a lazy attribute of
 * smartfrog.
 * created 27-Feb-2006 16:14:45
 */

public class AntRuntime extends PrimImpl implements RemoteReferenceResolver {

    private AntImpl owner;


    public AntRuntime(AntImpl project) throws RemoteException {
        owner = project;
    }


    /**
     * Find an attribute in this context by looking up the specific Ant property
     *
     * @param name attribute key to resolve
     * @return resolved attribute
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHere(Object name) throws SmartFrogResolutionException {

        if (owner != null) {
            final String value = owner.getAntProperty(name.toString());
            if (value != null) {
                return value;
            }
        }
        return super.sfResolveHere(name);
    }

    /**
     * set static properties from a terminated project
     * @param properties hashtable of properties (type (string,string))
     * @throws SmartFrogRuntimeException when name or value are null, or injection failed
     * @throws RemoteException In case of Remote/network error
     */
    public void setStaticProperties(Hashtable<String,String> properties) throws SmartFrogRuntimeException, RemoteException {
        if ((owner != null) && (properties != null)) {
            propagateAntProperties(owner,properties);
        }
    }

    /**
     * Set the static attributes of a component to those of an Ant project
     * @param component component
     * @param properties properties to set
     * @throws SmartFrogRuntimeException on failure to replace an attribute
     * @throws RemoteException network problems
     */
    public static void propagateAntProperties(Prim component,Hashtable<String, String> properties)
            throws SmartFrogRuntimeException, RemoteException {
        Hashtable<String, String> props=properties;
        for(String property: props.keySet()) {
            String value= props.get(property);
            if(value!=null) {
                component.sfReplaceAttribute(property, value);
            }
        }
    }

}
