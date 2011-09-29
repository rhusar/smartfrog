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
package org.smartfrog.services.restlet.client;

import org.restlet.data.Form;
import org.restlet.data.Message;
import org.restlet.data.Parameter;

import java.util.HashMap;

/**
 * Utility methods for restlet support.
 * Created 04-Mar-2008 15:01:34
 *
 */

public class RestletUtils {
    private static final String HEADERS = "org.restlet.http.headers";

    /**
     * Get the headers
     * @param message message to parse
     * @return the headers as a form
     */
    public static Form extractHttpHeaders(Message message) {
        return (Form) message.getAttributes().get(HEADERS);
    }

    public static HashMap<String, String> headersToHashMap(Message message) {
        HashMap<String, String> headers = new HashMap<String, String>();
        Form form = RestletUtils.extractHttpHeaders(message);
        if (form != null) {
            for (String header : form.getNames()) {
                headers.put(header, form.getFirstValue(header));
            }
        }
        return headers;
    }

    public static void addHttpHeaders(Message message,Form headers) {
        message.getAttributes().put(HEADERS,headers);
    }

    public static void addHttpHeader(Message message, String key, String value) {
        Form headers = extractHttpHeaders(message);
        if (headers == null) {
            headers = new Form();
            addHttpHeaders(message, headers);
        }
        Parameter param = new Parameter(key, value);
        headers.add(param);
    }

}
