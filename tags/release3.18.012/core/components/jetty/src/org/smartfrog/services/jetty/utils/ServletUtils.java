/** (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty.utils;

import org.smartfrog.services.www.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ServletUtils {



    /**
     * Mime type used in SF data: {@value}.
     */
    public String MIME_TYPE_SMARTFROG = "application/x-smartfrog";

    /**
     * The charset/encoding used in SF data: {@value}.
     */
    public String ENCODING_SMARTFROG = "UTF-8";

    /**
     * Turn off caching
     * @param response response whose headers need writing to
     */
    public static void disableCaching(final HttpServletResponse response) {
        response.addHeader(HttpHeaders.CACHE_CONTROL, HttpHeaders.NO_CACHE);
        response.addHeader(HttpHeaders.EXPIRES, "Thu, 01 Dec 1994 16:00:00 GMT");
        disableKeepAlives(response);
    }

    public static void disableKeepAlives(final HttpServletResponse response) {
        response.addHeader("Connection","close");
    }

    public static void open(PrintWriter writer, String tag) {
        writer.append('<').append(tag).append('>');
    }
    public static void close(PrintWriter writer, String tag) {
        writer.append("</").append(tag).append(">\n");
    }
    public static void element(PrintWriter writer, String tag, String body) {
        open(writer, tag);
        if (body != null) {
            writer.append(body);
        }
        close(writer, tag);
    }
}
