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

package org.smartfrog.services.deployapi.transport.endpoints.system;

import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.AlpineProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;

/**

 */
public abstract class SystemProcessor extends AlpineProcessor {

    public SystemProcessor(WsrfHandler owner) {
        super(owner);
    }


    public Application getJob() {
        return job;
    }

    public void setJob(Application job) {
        this.job = job;
    }

    protected Application job;

    /**
     * fail if there is no job
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     */
    protected void jobMustExist() {
        if(job==null) {
            throw raiseNoSuchApplicationFault("No system found");
        }
    }
    
    
}
