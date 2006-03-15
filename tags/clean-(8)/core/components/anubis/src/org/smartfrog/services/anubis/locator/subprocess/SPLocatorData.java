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
package org.smartfrog.services.anubis.locator.subprocess;



import java.util.HashMap;
import java.util.Map;

import org.smartfrog.sfcore.prim.Prim;

public class SPLocatorData {

    private Prim     subProcessLocator;
    private Liveness liveness;
    private Map      providers   = new HashMap();
    private Map      listeners   = new HashMap();
    private Map      stabilities = new HashMap();

    SPLocatorData(Prim sp, long timeout) {
        subProcessLocator = sp;
        liveness          = new Liveness(timeout);
        liveness.ping();
    }

    public Liveness getLiveness() {
        return liveness;
    }

    public Map getProviders() {
        return providers;
    }

    public Map getListeners() {
        return listeners;
    }

    public Map getStabilities() {
        return stabilities;
    }

    void clear() {
        subProcessLocator = null;
        liveness          = null;
        providers.clear();
        providers = null;
        listeners.clear();
        listeners = null;
        stabilities.clear();
        stabilities = null;
    }
}

