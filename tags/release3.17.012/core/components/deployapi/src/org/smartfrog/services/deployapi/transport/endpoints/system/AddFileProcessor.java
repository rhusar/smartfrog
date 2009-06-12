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
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.binding.UriListType;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.filesystem.filestore.AddedFilestore;
import org.smartfrog.services.filesystem.filestore.FileEntry;
import org.smartfrog.services.xml.utils.XomUtils;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.io.IOException;

/** Implement addfile operation */
public class AddFileProcessor extends SystemProcessor {
    public static final String FILE_SCHEMA = "file";
    public static final String SUFFIX = "bin";

    public AddFileProcessor(WsrfHandler owner) {
        super(owner);
    }


    public Element process(SoapElement request) throws IOException {
        jobMustExist();
        AddedFilestore filestore = ServerInstance.currentInstance()
                .getFilestore();
        String name = XomHelper.getElementValue(request, "api:name");
        String scheme = XomHelper.getElementValue(request,
                "api:scheme");
        if(scheme!=null) {
            scheme.trim();
        }
        if (!(FILE_SCHEMA.equals(scheme))) {
            throw FaultRaiser.raiseNotImplementedFault("Unsupported scheme type: "+scheme);
        }
        String mimetype = XomHelper.getElementValue(request,
                "api:mimetype");
        Element metadata = XomHelper.getElement(request, "api:metadata", false);

        String uri= XomHelper.getElementValue(request, "api:uri", false);
        String data = XomHelper.getElementValue(request, "api:data", false);
        //create a respose
        Element response = XomHelper.apiElement("addFileResponse");
        FileEntry entry = getJob().createNewTempFile(".bin");
        UriListType uris=new UriListType();
        if(uri==null ) {
            if(data==null) {
                throw FaultRaiser.raiseBadArgumentFault("Neither uri nor data supplied");
            }
            byte[] payload= XomUtils.base64Decode(data);
            Utils.saveToBinaryFile(entry.getFile(), payload);
        } else {
            if (data != null) {
                throw FaultRaiser.raiseBadArgumentFault("Both uri and data supplied");
            }
            throwNotImplemented();
        }
        entry.setMimetype(mimetype);
        uris.add(entry.getUri());
        uris.toXml(response);
        return response;
    }

}
