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
package org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker;

/**
 * Created 18-Nov-2009 16:30:13
 */


public interface MasterWorkerRoles {
    /**
     * This is the hostname of the master, which hosts both the NN and JT.
     * <pre>
     * binding.master.hostname PROPERTY binding.master.hostname;
     * </pre>
     * 
     * 
     */
    String BINDING_MASTER_HOSTNAME = "binding.master.hostname";
    
     
    
    /** {@value} */
    String MASTER = "master";
    /** {@value} */
    String WORKER = "worker";
}
