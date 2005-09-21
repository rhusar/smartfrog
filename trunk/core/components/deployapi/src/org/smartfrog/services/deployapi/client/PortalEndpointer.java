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
package org.smartfrog.services.deployapi.client;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.smartfrog.services.deployapi.system.Constants;

/**
 * created Aug 31, 2004 4:27:08 PM represents a server binding.
 */

public class PortalEndpointer extends Endpointer {


    /**
     * this is the prefix we look for on the command line
     */
    public static final String URL_COMMAND = "-url:";


    public PortalEndpointer() {
    }

    public PortalEndpointer(URL url) {
        setURL(url);
    }

    public PortalEndpointer(String url) {
        super(url);
    }


    public static PortalEndpointer createDefaultBinding() throws IOException {
        PortalEndpointer endpointer = new PortalEndpointer();
        URL defURL = new URL("http",
                Constants.DEFAULT_HOST,
                Constants.DEFAULT_SERVICE_PORT,
                Constants.DEFAULT_PATH);

        endpointer.setURL(defURL);
        return endpointer;
    }

    public String toString() {
        if (url == null) {
            return "(unbound)";
        } else {
            return url.toExternalForm();
        }
    }

    /**
     * convert to an external form.
     *
     * @return
     */
    public String toCommandLineElement() {
        return URL_COMMAND + url.toExternalForm();
    }

    /**
     * get the binding of this element, null for no match,
     *
     * @param commandLineElement
     * @return
     * @throws java.net.MalformedURLException if there was anything wrong with the URL
     */
    public static PortalEndpointer fromCommandLineElement(
            String commandLineElement)
            throws MalformedURLException {
        boolean isOption = commandLineElement.indexOf(URL_COMMAND) == 0;
        //isOption |= commandLineElement.indexOf(URL_COMMAND2) == 0;
        if (isOption) {
            String urlBody = commandLineElement.substring(URL_COMMAND.length());
            if ("".equals(urlBody)) {
                throw new MalformedURLException(
                        "no URL in " + commandLineElement);
            }
            URL newurl = new URL(urlBody);
            PortalEndpointer endpointer = new PortalEndpointer(newurl);
            return endpointer;
        } else {
            return null;
        }
    }

    /**
     * get the binding of this element, null for no match,
     *
     * @param commandLine full command line args
     * @return
     * @throws java.net.MalformedURLException if there was anything wrong with the URL
     */
    public static PortalEndpointer fromCommandLine(String[] commandLine)
            throws MalformedURLException {
        PortalEndpointer endpointer = null;
        for (int i = 0; i < commandLine.length; i++) {
            endpointer = fromCommandLineElement(commandLine[i]);
            if (endpointer != null) {
                //mark that element as null
                commandLine[i] = null;
                break;
            }
        }
        return endpointer;
    }



}
