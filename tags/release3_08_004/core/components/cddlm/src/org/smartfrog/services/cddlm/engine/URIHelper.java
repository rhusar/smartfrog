/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.engine;

import org.apache.axis.types.URI;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * classes to help URI creation. As a side touch, we dont usually throw
 * excetions created Sep 9, 2004 4:38:16 PM
 */

public class URIHelper {

    private URIHelper() {
    }

    /**
     * create a java URI from a string
     *
     * @param axisURI
     * @return
     * @throws RuntimeException if anything went wrong
     */
    public static java.net.URI toJavaUri(org.apache.axis.types.URI axisURI) {
        assert axisURI != null;
        String s = axisURI.toString();
        try {
            return new java.net.URI(s);
        } catch (URISyntaxException e) {
            String message = "converting " + axisURI.toString();
            throw raiseConversionFault(message, e);
        }
    }

    /**
     * what our fault policy is
     *
     * @param message
     * @param e
     * @return
     */
    private static RuntimeException raiseConversionFault(String message,
            Exception e) {
        return new RuntimeException(message, e);
    }

    public static URL toJavaURL(org.apache.axis.types.URI axisURI) {
        try {
            return new URL(axisURI.toString());
        } catch (MalformedURLException e) {
            throw raiseConversionFault("converting " + axisURI.toString(), e);

        }
    }

    /**
     * create an axis uri from a string
     *
     * @param s
     * @return
     */
    public static org.apache.axis.types.URI toAxisUri(String s) {
        try {
            return new org.apache.axis.types.URI(s);
        } catch (URI.MalformedURIException e) {
            throw raiseConversionFault("converting " + s, e);
        }
    }

}
