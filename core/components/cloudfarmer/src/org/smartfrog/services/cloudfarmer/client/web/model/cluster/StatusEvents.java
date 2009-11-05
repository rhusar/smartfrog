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

/**
 * Created 05-Nov-2009 11:59:38
 */

public final class StatusEvents extends ArrayList<StatusEvent> implements Cloneable {
    
    public void addEvent(boolean error, String message) {
        add(new StatusEvent(error, message));
    }

    @Override
    public StatusEvents clone() {
        return (StatusEvents) super.clone();
    }

    /**
     * Return this list in a way that is easy for JSP pages to handle
     * @return the this pointer
     */
    public ArrayList<StatusEvent> getList() {
        return this;
    }
}
