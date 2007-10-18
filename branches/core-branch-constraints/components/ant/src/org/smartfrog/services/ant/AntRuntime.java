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
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This is a little class to turn every ant property into a lazy attribute of
 * smartfrog.
 * created 27-Feb-2006 16:14:45
 */

public class AntRuntime extends PrimImpl implements RemoteReferenceResolver {

    private AntImpl owner;


    public AntRuntime(AntImpl project) throws RemoteException {
        this.owner = project;
    }


    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     * @return resolved attribute
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *                                  failed to find attribute
     */
    public Object sfResolveHere(Object name) throws SmartFrogResolutionException {

        if(owner !=null) {
            final String value = owner.getAntProperty(name.toString());
            if (value != null) {
                return value;
            }
        }
        return super.sfResolveHere(name);
    }

    /**
     * set static properties from a terminated project
     * @param properties
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void setStaticProperties(Hashtable properties) throws SmartFrogRuntimeException, RemoteException {
        if ((owner!=null)&&(properties !=null)) {
            Enumeration keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (key != null) {
                    String value = (String) properties.get(key);
                    if (value!=null) {
                        owner.sfReplaceAttribute(key, value);
                    }
                }
            }
        }
    }

}
