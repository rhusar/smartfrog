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
package org.smartfrog.projects.alpine.transport.http;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;

import java.io.IOException;

/**
 * created 23-Mar-2006 17:36:20
 */

public class ProgressingPostMethod extends PostMethod {

    /**
     * No-arg constructor.
     *
     * @since 1.0
     */
    public ProgressingPostMethod() {
    }

    /**
     * Constructor specifying a URI.
     *
     * @param uri either an absolute or relative URI
     * @since 1.0
     */
    public ProgressingPostMethod(String uri) {
        super(uri);
    }

    /**
     * Writes the request body to the given {@link org.apache.commons.httpclient.HttpConnection connection}.
     *
     * @param state the {@link org.apache.commons.httpclient.HttpState state} information associated with this method
     * @param conn  the {@link org.apache.commons.httpclient.HttpConnection connection} used to execute
     *              this HTTP method
     * @return <tt>true</tt>
     * @throws java.io.IOException if an I/O (transport) error occurs. Some transport exceptions
     *                             can be recovered from.
     * @throws org.apache.commons.httpclient.HttpException
     *                             if a protocol exception occurs. Usually protocol exceptions
     *                             cannot be recovered from.
     */
    protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException, HttpException {
        return super.writeRequestBody(state, conn);
    }
}
