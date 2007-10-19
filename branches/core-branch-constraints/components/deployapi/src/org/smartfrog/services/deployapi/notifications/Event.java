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

import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

import java.util.Date;

import nu.xom.Attribute;

/**
 * created 27-Sep-2006 11:59:01
 */
public class Event {

    public Application application;

    public Date timestamp;

    public TerminationRecord record;


    public LifecycleStateEnum state, oldState;

    public Event(Application application, LifecycleStateEnum newState, LifecycleStateEnum oldState,
                 TerminationRecord record) {
        assert newState != null;
        this.application = application;
        state = newState;
        this.oldState = oldState;
        this.record = record;
        timestamp = new Date();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "Event for <" + (application!=null?application.getId():"") + "> entering state "
                + state
                + " at " + XsdUtils.toIsoTime(timestamp)
                + " " + (record != null ? record + toString() : "");
    }


    /**
     * Get a state transition from the current and old state
     *
     * @return
     */
    public SoapElement makeCmpLifecycleTransition() {

        SoapElement transition =
                new SoapElement("cmp:LifecycleTransition", Constants.CDL_CMP_TYPES_NAMESPACE);
        SoapElement stateTransition = new SoapElement("muws-p2-xs:StateTransition",
                Constants.MUWS_P2_NAMESPACE);
        stateTransition.addAttribute(
                new Attribute("muws-p2-xs:Time", Constants.MUWS_P2_NAMESPACE,
                        XsdUtils.toIsoTime(timestamp)));
        transition.appendChild(stateTransition);
        stateTransition.appendChild(muwsStateChange("EnteredState", state));
        if(oldState!=LifecycleStateEnum.undefined) {
            stateTransition.appendChild(muwsStateChange("PreviousState", oldState));
        }
        return transition;
    }

    private SoapElement muwsStateChange(String local, LifecycleStateEnum s) {
        SoapElement muws = new SoapElement("muws-p2-xs:" + local, Constants.MUWS_P2_NAMESPACE);
        muws.appendChild(
                new SoapElement("cmp:" + s.getXmlName(), Constants.CDL_CMP_TYPES_NAMESPACE));
        return muws;
    }
}
