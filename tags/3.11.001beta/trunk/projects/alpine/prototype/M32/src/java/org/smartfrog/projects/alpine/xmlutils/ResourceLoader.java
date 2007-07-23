/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.xmlutils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This is something that can hand off resource loading to whatever does
 * loading. The base implementation uses the classloader of ourselves, or
 * a classloader that gets handed in, or that of a class that gets passed in.
 * created Jul 1, 2004 4:44:38 PM
 */

public class ResourceLoader {

    /**
     * classloader to load from
     */
    private ClassLoader loader = null;
    public static final String ERROR_MISSING_RESOURCE = "Not found: ";
    public static final int LOAD_BLOCK_SIZE = 1024;

    public ResourceLoader() {
        loader = getClass().getClassLoader();
    }

    public ResourceLoader(ClassLoader loader) {
        assert loader != null;
        this.loader = loader;
    }

    public ResourceLoader(Class clazz) {
        this(clazz.getClassLoader());
    }


    private InputStream loadResourceThroughClassloader(String resourceName) {
        assert resourceName != null;
        InputStream in = loader.getResourceAsStream(resourceName);
        return in;
    }

    /**
     * internal health check; test that a resource was loaded.
     *
     * @param in
     * @param resourcename
     * @throws IOException
     */
    private void assertResourceLoaded(InputStream in, String resourcename)
            throws IOException {
        if (in == null) {
            throw new IOException(ERROR_MISSING_RESOURCE + resourcename);
        }
    }

    /**
     * load a resource.
     *
     * @param resourceName the name of the resource
     * @return the resource as an input stream
     * @throws IOException if a resource is missing
     */
    public InputStream loadResource(String resourceName) throws IOException {
        InputStream in;
        in = loadResourceThroughClassloader(resourceName);
        assertResourceLoaded(in, resourceName);
        return in;
    }

    /**
     * load a resource into a string.
     *
     * @param resourceName the name of the resource to load
     * @return the resource in the string
     * @throws IOException if a resource is missing
     */
    public String loadResourceAsString(String resourceName) throws IOException {
        InputStream in = loadResource(resourceName);
        InputStreamReader reader = new InputStreamReader(in);
        StringBuffer buffer = new StringBuffer();
        char[] block = new char[LOAD_BLOCK_SIZE];
        int read;
        while (((read = reader.read(block)) >= 0)) {
            buffer.append(block);
        }
        return buffer.toString();
    }
}
