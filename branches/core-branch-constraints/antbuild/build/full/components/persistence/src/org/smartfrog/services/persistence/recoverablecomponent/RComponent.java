/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.persistence.recoverablecomponent;

import java.rmi.RemoteException;

import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;


public interface RComponent extends Compound {

    public static int StubWait = 5000;
    public static String WFSTATUSENTRY = "WOODFROG_WFSTATUS";
    public static String WFSTATUSDIRECTORY = "WOODFROG_WFSTATUS";
    public static String WFSTATUS_DEAD = "WOODFROG_WFDEAD";
    public static String WFSTATUS_STARTED = "WOODFROG_WFSTARTED";

    public static String CHILDRENSDIRECTORY = "WOODFROG_SFCHILDREN";
    public static String LIFECYCLECHILDREN = "WOODFROG_WFLIFECYCLECHILDREN";
    public static String SFCHILDREN = "WOODFROG_WFSFCHILDREN";
    public static String SFPARENT = "WOODFROG_WFSFPARENT";

    static final String DBStubEntry = "WFSTUBENTRY";
    static final String DBStubDirectory = "WFSTUBDIRECTORY";

    static final String STORAGEATTRIB = "wfStorage";

    public static String RECOVERY_ATTR = "sfRecovery";
    public static String WFSTATUS_DEPLOYED = "WOODFROG_WFDEPLOYED";

    public void sfRecover() throws SmartFrogException, RemoteException;

    public RComponentProxyLocator getProxyLocator() throws RemoteException,
            StorageException;

}
