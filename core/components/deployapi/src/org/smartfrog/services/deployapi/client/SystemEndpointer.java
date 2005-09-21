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
package org.smartfrog.services.deployapi.client;

import org.ggf.xbeans.cddlm.api.CreateResponseDocument;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.apache.axis2.addressing.EndpointReference;

import java.net.URL;

/**
 * Model for a remote system.
 * Needs a resourceID for hashCode and equals to work, so cannot be inserted into collections until then.
 * created 21-Sep-2005 12:55:10
 */

public class SystemEndpointer extends Endpointer{

    private String resourceID;


    public SystemEndpointer() {
    }

    public SystemEndpointer(URL url) {
        super(url);
    }

    public SystemEndpointer(EndpointReference endpointer, String resourceID) {
        super(endpointer);
        this.resourceID = resourceID;
    }

    public SystemEndpointer(CreateResponseDocument.CreateResponse response) {
        resourceID=response.getResourceId();
        bindToEndpointer(EprHelper.Wsa2003ToEPR(response.getSystemReference()));
    }

    public SystemEndpointer(String url) {
        super(url);
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }


    /**
     * we use resourceID for equality, not the url itself
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SystemEndpointer that = (SystemEndpointer) o;

        if (resourceID != null ? !resourceID.equals(that.resourceID) : that.resourceID != null) return false;

        return true;
    }

    /**
     * hash is based on resourceID
     * @return
     */
    public int hashCode() {
        return (resourceID != null ? resourceID.hashCode() : 0);
    }
}
