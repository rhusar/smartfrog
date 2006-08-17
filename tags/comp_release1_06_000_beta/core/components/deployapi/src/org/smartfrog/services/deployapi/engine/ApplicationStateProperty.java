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
package org.smartfrog.services.deployapi.engine;

import org.smartfrog.services.deployapi.transport.wsrf.Property;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.LifecycleStateEnum;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import javax.xml.namespace.QName;

import nu.xom.Element;

import java.util.List;

/**
 * created 12-May-2006 13:17:11
 */

public class ApplicationStateProperty implements Property {

    Application owner;

    public ApplicationStateProperty(Application owner) {
        this.owner = owner;
    }

    public QName getName() {
        return Constants.PROPERTY_SYSTEM_SYSTEM_STATE;
    }

    public List<Element> getValue() {
        LifecycleStateEnum state = owner.getState();
        SoapElement cmpState = state.toCmpState();

        return WsrfUtils.listify(cmpState);
    }
}
