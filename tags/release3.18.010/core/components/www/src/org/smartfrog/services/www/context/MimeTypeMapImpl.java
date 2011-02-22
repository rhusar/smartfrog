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

import org.smartfrog.services.www.MimeTypeMap;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A mime type can be deployed within a servlet context created 28-Jul-2005 16:29:47
 */

public class MimeTypeMapImpl extends ServletContextComponentImpl implements MimeTypeMap {
    private Map<String, String> mappings;


    /**
     * constructor
     *
     * @throws RemoteException from the superclass
     */
    public MimeTypeMapImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        mappings = new HashMap<String, String>();
        ComponentDescription mapCD = null;
        mapCD = sfResolve(ATTR_MAP, mapCD, true);
        Context context = mapCD.sfContext();
        Iterator keys = context.sfAttributes();
        while (keys.hasNext()) {
            Object key = keys.next();
            String type = context.get(key).toString();
            String ext = key.toString();
            mappings.put(ext, type);
            getServletContext().addMimeMapping(ext, type);
        }
    }

    /**
     * remove the servlet context if it is absent.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        ServletContextIntf servletCtx = getServletContext();
        if (servletCtx != null) {
            for (String extension : mappings.keySet()) {
                try {
                    servletCtx.removeMimeMapping(extension);
                } catch (RemoteException e) {
                    //swallowed

                } catch (SmartFrogException e) {
                    //swallowed
                }
            }
        }
    }
}