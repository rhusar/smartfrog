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
package org.smartfrog.services.rpm.manager.local;

import org.smartfrog.services.rpm.manager.AbstractRpmManager;
import org.smartfrog.services.rpm.manager.RpmFile;
import org.smartfrog.services.rpm.manager.RpmManager;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.reference.Reference;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created 14-Apr-2008 17:11:58
 */

public class ArtifactCheckingManagerImpl extends AbstractLocalRpmManager implements RpmManager {

    public ArtifactCheckingManagerImpl() throws RemoteException {
    }


}
