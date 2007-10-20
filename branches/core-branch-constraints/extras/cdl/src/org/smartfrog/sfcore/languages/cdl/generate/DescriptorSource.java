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
package org.smartfrog.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * created 24-Jan-2006 14:41:56
 */


public interface DescriptorSource {

    /**
     * Add a new description
     * @param parent node: add attribute or children
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void exportDescription(CdlComponentDescription parent) throws RemoteException, SmartFrogException;

}
