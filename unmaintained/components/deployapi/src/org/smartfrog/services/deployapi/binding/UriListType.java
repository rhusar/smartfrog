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
package org.smartfrog.services.deployapi.binding;

import nu.xom.Element;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * List of URIs, bound to the api:item namespace in XML
 * created 27-Apr-2006 15:04:20
 */

public class UriListType extends ArrayList<URI> {
    public static final String ITEM = "item";


    public UriListType() {
    }

    public UriListType(Element parent) {
        parse(parent);
    }

    void parse(Element parent) {
        try {
            for (Element item : XsdUtils.elements(parent, ITEM, CddlmConstants.CDL_API_TYPES_NAMESPACE)) {
                add(new URI(item.getValue()));
            }
        } catch (URISyntaxException e) {
            throw new ServerException("Invalid URI " + e.getInput() + " at " + e.getIndex(), e);
        }
    }

    public void toXml(Element parent) {
        for (URI uri : this) {
            parent.appendChild(XomHelper.apiElement(ITEM, uri));
        }
    }
}
