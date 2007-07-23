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
package org.smartfrog.sfcore.languages.cdl.components;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;

import javax.xml.namespace.QName;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * created 24-Jan-2006 13:37:36
 * This extends Cdl Component descriptions with extra operations.
 */


public interface CdlComponentDescription extends SFComponentDescription {

    /**
     * Get the Qualified name of a node
     * @return the qname
     * @throws RemoteException
     */
    public QName getQName() throws RemoteException;

    /**
     * Helper operation to do a full resolve of a child thing
     * @param child
     * @param mandatory
     * @return the thing at the end of the link, or null for no match
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if there is no match and mandatory==true
     */
    Object resolve(QName child,boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Like sfReplace but with some special magic related to stuff in the local namespace, which
     * is turned into non-qname stuff.
     * @param child
     * @param value
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    void replace(QName child,Object value) throws SmartFrogResolutionException, RemoteException, SmartFrogException;
}
