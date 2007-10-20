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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import javax.xml.namespace.QName;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * created 01-Feb-2006 11:18:53
 */


public interface CdlCompound extends LifecycleSource {
    /**
     * The name under which XML text gets added
     * {@value}
     */
    String ATTR_TEXT = "sfText";
    /**
     * URL of an endpoint to notify
     */
    /*
    public String ATTR_NOTIFICATION_ENDPOINT = "endpoint";

    public String ATTR_TIMEOUT = "timeout";

    public String ATTR_IDENTIFIER = "identifier";
*/
    String ATTR_JOBURI = "joburi";

    /**
     * Reference to something listening for events
     */
    String ATTR_LISTENER = "listener";

    /**
     * Resolve a reference
     * @param name qname of the reference
     * @param mandatory flag to indicate a mandatory reference
     * @return whatever resolved
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException for resolution problems
     * @throws java.rmi.RemoteException for network problems
     */
    Object resolve(QName name, boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    /**
     * Resolve the sfText node under the named reference
     * @param name qname of the reference
     * @param mandatory flag to indicate a mandatory reference
     * @return whatever resolved
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException for resolution problems
     * @throws java.rmi.RemoteException for network problems
     */
    String resolveText(QName name, boolean mandatory) throws SmartFrogResolutionException, RemoteException;

    LifecycleListener getListener() throws RemoteException;
}
