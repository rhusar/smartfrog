/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.notifications;

import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.system.LifecycleStateEnum;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.util.Date;

/**
 * created 27-Sep-2006 11:59:01
 */
@SuppressWarnings("deprecation")
public class Event {

    public Application application;

    public Date timestamp;

    public TerminationRecord record;


    public LifecycleStateEnum state;


    public Event(Application application, LifecycleStateEnum type, TerminationRecord record) {
        this.application = application;
        state = type;
        this.record= record;
        timestamp=new Date();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "Event for <"+application+"> entering state "
                +state
                +" at "+timestamp.toGMTString()
                +" "+record!=null?record+toString():"";
    }
}
