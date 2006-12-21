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

package org.smartfrog.services.deployapi.engine;

import nu.xom.Element;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.wsrf.Property;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.xml.java5.Xom5Utils;

import javax.xml.namespace.QName;
import java.util.List;

/**

 */
public class ActiveSystemsProperty implements Property {

    private ServerInstance owner;

    public ActiveSystemsProperty(ServerInstance owner) {
        this.owner = owner;
    }

    public QName getName() {
        return Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS;
    }

    public List<Element> getValue() {
        Element response = Xom5Utils.element(Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);
        JobRepository jobs = owner.getJobs();
        for (Application job : jobs) {
            Element epr = (Element) job.getEndpointer().copy();
            XomHelper.adopt(epr, CddlmConstants.ELEMENT_NAME_SYSTEM);
            response.appendChild(epr);
        }
        return WsrfUtils.listify(response);
    }

}
