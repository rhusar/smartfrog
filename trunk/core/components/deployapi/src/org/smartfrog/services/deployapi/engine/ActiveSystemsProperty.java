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

import org.smartfrog.services.deployapi.transport.wsrf.Property;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.system.Constants;
import org.apache.axis2.om.OMElement;
import static org.smartfrog.services.deployapi.binding.XomHelper.*;
import javax.xml.namespace.QName;

import nu.xom.Element;

/**

 */
public class ActiveSystemsProperty implements Property {

    private final QName name= Utils.convert(Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);

    private final JobRepository jobs;

    public ActiveSystemsProperty(JobRepository jobs) {
        this.jobs = jobs;
    }

    public QName getName() {
        return name;
    }

    /*
            ActiveSystemsDocument doc=ActiveSystemsDocument.Factory.newInstance();
        SystemReferenceListType systems = doc.addNewActiveSystems();
        int size=jobs.size();
        EndpointReferenceType apps[]=new EndpointReferenceType[size];
        int counter=0;
        for(Job job:jobs) {
            apps[counter++]=(EndpointReferenceType)job.getEndpoint().copy();
        }
        systems.setSystemArray(apps);
        return doc;
    */

    public OMElement getValue() {
        Element result =apiElement("ActiveSystems");
        for (Job job : jobs) {
         //tODO           
        }
        return Utils.xomToAxiom(result);
    }
}
