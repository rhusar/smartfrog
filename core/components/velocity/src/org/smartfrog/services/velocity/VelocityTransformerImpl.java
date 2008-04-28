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
package org.smartfrog.services.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.PropertiesUtils;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * Created 24-Apr-2008 14:26:28
 */

public class VelocityTransformerImpl extends PrimImpl {
    private VelocityContext context;
    private static final Reference ATTR_PROPERTYLIST = new Reference("propertyList");
    private static final Reference REF_PROPERTIES = new Reference("properties");
    private Properties velocityProperties;
    private VelocityEngine engine;


    public VelocityTransformerImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Object value = sfResolve(REF_PROPERTIES, true);
        if (value instanceof Prim) {
            velocityProperties = PropertiesUtils.build((Prim) value);
        } else if (value instanceof ComponentDescription) {
            velocityProperties = PropertiesUtils.build((ComponentDescription) value);
        } else {
            throw new SmartFrogResolutionException(this.sfCompleteName(), REF_PROPERTIES,
                    "Unsupported property source ");
        }
        Properties listprops = ListUtils.resolveProperties(this, ATTR_PROPERTYLIST, true);
        PropertiesUtils.concat(velocityProperties, listprops, true);

        try {
            engine = new VelocityEngine();
            engine.init(velocityProperties);
        } catch (Exception e) {
            throw SmartFrogLifecycleException.forward("Failed to start velocity", e, this);
        }
        context = new VelocityContext();
    }

}
