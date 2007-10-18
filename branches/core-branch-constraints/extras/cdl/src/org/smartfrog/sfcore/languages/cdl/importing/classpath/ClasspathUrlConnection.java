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
package org.smartfrog.sfcore.languages.cdl.importing.classpath;

import org.smartfrog.services.xml.utils.ResourceLoader;

import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;

/**
 * A stub URL connection that doesnt load stuff
 * off the classpath. its just too complex to set up.
 *
 * It is used instead as something to pass in when creating a URL via the UrlFactory,
 * so that classpath: URLs can be passed around.
 * created 20-Dec-2005 13:05:43
 */

public class ClasspathUrlConnection extends URLConnection {


    public ClasspathUrlConnection(URL url) {
        super(url);
    }

    /**
     * throws an IOE, because we havent i
     * @throws java.io.IOException if an I/O error occurs while opening the
     *                             connection.
     * @see java.net.URLConnection#connected
     * @see #getConnectTimeout()
     * @see #setConnectTimeout(int)
     */
    public void connect() throws IOException {
        throw new IOException("Not implemented");
    }


}
