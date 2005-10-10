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

import nu.xom.Element;
import nu.xom.Document;
import org.apache.axis2.om.OMElement;
import org.smartfrog.services.deployapi.components.AddedFilestore;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.binding.XomHelper;

import java.io.IOException;

/** Implement addfile operation */
public class AddFileProcessor extends SystemProcessor {

    public AddFileProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }


    public OMElement process(OMElement request) throws IOException {
        jobMustExist();
        AddedFilestore filestore = ServerInstance.currentInstance()
                .getFilestore();
        Document document = Utils.axiomToXom(request);
        Element root = document.getRootElement();
        Element body = XomHelper.getElement(document,
                "api:addFileRequest");

        String name = XomHelper.getElementValue(body, "api:name");
        String schema = XomHelper.getElementValue(body,
                "api:schema");
        String mimetype = XomHelper.getElementValue(body,
                "api:mimetype");
        Element response = null;
        Element metadata=XomHelper.getElement(body,"api:metadata",true);
        //filestore.createNewFile(
        return Utils.xomToAxiom(response);
    }

}
