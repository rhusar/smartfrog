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

import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;

/**
 * created 20-Dec-2005 13:03:54
 */

public class ClasspathUrlStreamHandler extends URLStreamHandler {

    ResourceLoader loader;

    public ClasspathUrlStreamHandler(ResourceLoader loader) {
        this.loader = loader;
    }

    /**
     * Opens a connection to the object referenced by the
     * <code>URL</code> argument.
     * This method should be overridden by a subclass.
     * <p/>
     * <p>If for the handler's protocol (such as HTTP or JAR), there
     * exists a public, specialized URLConnection subclass belonging
     * to one of the following packages or one of their subpackages:
     * java.lang, java.io, java.util, java.net, the connection
     * returned will be of that subclass. For example, for HTTP an
     * HttpURLConnection will be returned, and for JAR a
     * JarURLConnection will be returned.
     *
     * @param u the URL that this connects to.
     * @return a <code>URLConnection</code> object for the <code>URL</code>.
     * @throws java.io.IOException if an I/O error occurs while opening the
     *                             connection.
     */
    protected URLConnection openConnection(URL u) throws IOException {
        return new ClasspathUrlConnection(u);
    }
}
