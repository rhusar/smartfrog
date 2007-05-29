/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.context;

import org.smartfrog.services.www.MimeType;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * A mime type can be deployed within a servlet context
 * created 28-Jul-2005 16:29:47
 */

public class MimeTypeImpl extends ServletContextComponentImpl implements MimeType {
    private String extension;
    private String type;


    public MimeTypeImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        extension = sfResolve(ATTR_EXTENSION, extension, true);
        type = sfResolve(ATTR_TYPE, type, true);
        getServletContext().addMimeMapping(extension, type);
    }

    /**
     * remove the servlet context if it is absent.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (getServletContext() != null) {
            try {
                getServletContext().removeMimeMapping(extension);
            } catch (RemoteException e) {
                //swallowed

            } catch (SmartFrogException e) {
                //swallowed
            }
        }
    }
}
