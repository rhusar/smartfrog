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
package org.smartfrog.services.cddlm.cdl.base;

import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

/**
 * created 01-Feb-2006 11:19:17
 */

public class CdlComponentImpl extends CompoundImpl implements CdlComponent {
    public static final String ATTR_TEXT = "sfText";

    public CdlComponentImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
    }

    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
    }

    public Object resolve(QName name, boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        Reference r = new Reference(name);
        return sfResolve(r, mandatory);
    }

    public String resolveText(QName name, boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        Reference r = new Reference(name);
        r.addElement(ReferencePart.attrib(ATTR_TEXT));
        return (String) sfResolve(r, mandatory);
    }
}
