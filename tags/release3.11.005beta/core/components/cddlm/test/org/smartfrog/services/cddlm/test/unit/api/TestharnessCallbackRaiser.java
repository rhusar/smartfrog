/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.cddlm.test.unit.api;

import org.smartfrog.services.cddlm.api.CallbackRaiser;
import org.smartfrog.services.cddlm.engine.JobState;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleStateEnum;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 * Date: 16-Sep-2004
 * Time: 11:13:56
 */
public class TestharnessCallbackRaiser extends CallbackRaiser {

    private JobState job;

    private int count=0;

    private LifecycleStateEnum state;

    private String stateInfo;

    public JobState getJob() {
        return job;
    }

    public void setJob(JobState job) {
        this.job = job;
    }

    public LifecycleStateEnum getState() {
        return state;
    }

    public void setState(LifecycleStateEnum state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    /**
     * raise an event
     *
     * @param object object (may be null
     * @param sfe
     */
    public void raiseLifecycleEvent(JobState job, Prim object, SmartFrogException sfe) {
        count++;
        setJob(job);
        setState(job.getState());
        setStateInfo(job.getStateInfo());
    }
}
