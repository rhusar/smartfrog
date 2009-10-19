/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created 02-Sep-2009 16:25:15
 */

public class HostInstanceList extends ArrayList<HostInstance> {

    public HostInstanceList(int initialCapacity) {
        super(initialCapacity);
    }

    public HostInstanceList() {
    }

    public HostInstanceList(Collection<? extends HostInstance> c) {
        super(c);
    }

    /**
     * for struts integration
     * @return list of host instances
     */
    public List<HostInstance> getList() {
        return this;
    }

    public HostInstance getMaster() {
        for (HostInstance instance : this) {
            if (instance.isMaster()) {
                return instance;
            }
        }
        return null;
    }
}
