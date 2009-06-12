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

import org.restlet.data.Request;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.resource.Representation;
import org.smartfrog.services.restlet.client.RestletUtils;

/**
 *
 * Created 06-Mar-2008 14:26:40
 *
 */

public class ExtendedRequest extends Request {

    /**
     * Constructor.
     */
    public ExtendedRequest() {
    }

    /**
     * Constructor.
     *
     * @param method      The call's method.
     * @param resourceRef The resource reference.
     */
    public ExtendedRequest(Method method, Reference resourceRef) {
        super(method, resourceRef);
    }

    /**
     * Constructor.
     *
     * @param method      The call's method.
     * @param resourceRef The resource reference.
     * @param entity      The entity.
     */
    public ExtendedRequest(Method method, Reference resourceRef, Representation entity) {
        super(method, resourceRef, entity);
    }

    /**
     * Constructor.
     *
     * @param method      The call's method.
     * @param resourceUri The resource URI.
     */
    public ExtendedRequest(Method method, String resourceUri) {
        super(method, resourceUri);
    }

    /**
     * Constructor.
     *
     * @param method      The call's method.
     * @param resourceUri The resource URI.
     * @param entity      The entity.
     */
    public ExtendedRequest(Method method, String resourceUri, Representation entity) {
        super(method, resourceUri, entity);
    }

    /**
     * Add an http header
     * @param header header name
     * @param value value
     */
    public void addHeader(String header,String value) {
        RestletUtils.addHttpHeader(this, header, value);
    }
}
