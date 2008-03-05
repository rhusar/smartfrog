/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.restlet.overrides;

import org.restlet.data.Response;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.data.Reference;

/**
 * This is an extended response code. One thing we can do is detect forbidden response codes and fail early, not late
 * Created 28-Feb-2008 16:22:51
 *
 */

public class ExtendedResponse extends Response {

    private int min=0, max=-1;

    public ExtendedResponse(Request request) {
        super(request);
    }

    public ExtendedResponse(Request request, int min, int max) {
        super(request);
        this.min = min;
        this.max = max;
    }

    /**
     * Fix redirection handling by setting the base reference to the host of the system
     * @param redirectUri URI to redirect to
     */
    public void setRedirectRef(String redirectUri) {
        Reference baseRef=null;
        Reference resourceRef = getRequest().getResourceRef();
        if (resourceRef != null) {
            if (resourceRef.getBaseRef() != null) {
                baseRef = resourceRef.getBaseRef();
            } else {
                baseRef = resourceRef;
            }
        }
        setRedirectRef(new Reference(baseRef, redirectUri).getTargetRef());
    }

    /**
     * Sets the status.
     *
     * @param status The status to set.
     * @throws RuntimeException if the status code is in the forbidden range
     */
    public void setStatus(Status status) {
        super.setStatus(status);
        int code = status.getCode();
        if ((max >= 0 && code > max) || (min > 0 && code < min)) {
            throw new RuntimeException("Status out of range " + status.toString() + " \n" + status.getDescription());
        }
    }

 
}
