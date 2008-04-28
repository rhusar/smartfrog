/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.utils.setproperty;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.util.Properties;
import java.util.Iterator;
import java.util.List;
import java.rmi.RemoteException;

/**
 * Utility classes to work with properties.
 */

public class PropertiesUtils {

    private PropertiesUtils() {
    }

    /**
     /**
     * Build a properties structure from a component description; all local properties get turned into name-value pairs
     * @param cd the component description
     * @return a newly built Properties instance
     * @throws SmartFrogResolutionException for any failure to resolve an attribute or value
     */
    public static Properties build(ComponentDescription cd) throws SmartFrogResolutionException {
        Properties props = new Properties();
        Iterator<Object> attrs = cd.sfAttributes();
        while (attrs.hasNext()) {
            Object attr = attrs.next();
            String key = attr.toString();
            String value = cd.sfResolveHere(attr).toString();
            props.put(key, value);
        }
        return props;
    }

    /**
     * Build a properties structure from a Prim; all local properties get turned into name-value pairs
     * @param prim prim to build from
     * @return a newly built Properties instance
     * @throws SmartFrogResolutionException for any failure to resolve an attribute or value
     * @throws RemoteException for network problems
     */
    public static Properties build(Prim prim) throws SmartFrogResolutionException, RemoteException {
        Properties props = new Properties();
        Iterator<Object> attrs = prim.sfAttributes();
        while (attrs.hasNext()) {
            Object attr = attrs.next();
            String key = attr.toString();
            String value = prim.sfResolveHere(attr).toString();
            props.put(key, value);
        }
        return props;
    }

    /**
     * Resolve a prim or CD underneath another component
     * @param prim parent
     * @param reference child reference
     * @param required flag if this is required
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public static Properties resolveAndBuild(Prim prim, Reference reference,boolean required)
            throws SmartFrogResolutionException, RemoteException {
        Properties result;
        Object value = prim.sfResolve(reference, required);
        if (value == null) {
            return null;
        }
        if (value instanceof Prim) {
            result = PropertiesUtils.build((Prim) value);
        } else if (value instanceof ComponentDescription) {
            result = PropertiesUtils.build((ComponentDescription) value);
        } else {
            throw new SmartFrogResolutionException(prim.sfCompleteName(), reference,
                    "Unsupported property source ");
        }
        return result;
    }

    /**
     * Build from a list of tuples
     * @param tuples tuple list
     * @return the result
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple
     */
    public static Properties build(List<?> tuples) throws SmartFrogResolutionException {
        return ListUtils.convertToProperties(tuples);
    }


    /**
     * Add the second property set to the first
     * @param p1 first property set
     * @param p2 second property set
     * @param overwrite flag to set to true to overwrite things
     */
    public static void concat(Properties p1, Properties p2, boolean overwrite) {
        for(Object key:p2.keySet()) {
            if(overwrite || p1.get(key)==null) {
                p1.put(key,p2.get(key));
            }
        }
    }
}
