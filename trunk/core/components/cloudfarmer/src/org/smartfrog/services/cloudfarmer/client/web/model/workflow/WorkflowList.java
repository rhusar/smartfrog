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


package org.smartfrog.services.cloudfarmer.client.web.model.workflow;

import org.smartfrog.services.cloudfarmer.client.common.BaseRemoteDaemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is something which can be attached to pages; a workflow list
 */
public class WorkflowList extends ArrayList<Workflow> implements Serializable {

    private BaseRemoteDaemon daemon;

    public WorkflowList(BaseRemoteDaemon daemon) {
        this.daemon = daemon;
    }

    public WorkflowList(int initialCapacity, BaseRemoteDaemon daemon) {
        super(initialCapacity);
        this.daemon = daemon;
    }

    public WorkflowList() {
    }

    public BaseRemoteDaemon getDaemon() {
        return daemon;
    }

    protected void setDaemon(BaseRemoteDaemon daemon) {
        this.daemon = daemon;
    }

    /**
     * Return a reference to ourselves. This is for easier rendering in struts
     *
     * @return the "this" pointer
     */
    public List<Workflow> getList() {
        return this;
    }


}
