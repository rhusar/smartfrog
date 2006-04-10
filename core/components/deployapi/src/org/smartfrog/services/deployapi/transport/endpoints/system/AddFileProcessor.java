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

import nu.xom.Document;
import nu.xom.Element;
import org.apache.ws.commons.om.OMElement;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.filesystem.filestore.AddedFilestore;
import org.smartfrog.services.filesystem.filestore.FileEntry;

import java.io.IOException;

/** Implement addfile operation */
public class AddFileProcessor extends SystemProcessor {
    public static final String PREFIX = "file";
    public static final String SUFFIX = "bin";

    public AddFileProcessor(WsrfHandler owner) {
        super(owner);
    }


    public Element process(Element request) throws IOException {
        throwNotImplemented();
        jobMustExist();
        AddedFilestore filestore = ServerInstance.currentInstance()
                .getFilestore();
        Element root = request;
        Element body = XomHelper.getElement(root,
                "api:addFileRequest");

        String name = XomHelper.getElementValue(body, "api:name");
        String schema = XomHelper.getElementValue(body,
                "api:schema");
        if (!(PREFIX.equals(schema))) {
            throw FaultRaiser.raiseNotImplementedFault("Unsupported schema type");
        }
        String mimetype = XomHelper.getElementValue(body,
                "api:mimetype");
        Element response = XomHelper.apiElement("addFileResponse");
        Element metadata = XomHelper.getElement(body, "api:metadata", true);
        FileEntry newFile = filestore.createNewFile(PREFIX, SUFFIX);

        //TODO: save to a file
        //TODO: create a respose
        return response;
    }

}
