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

package org.smartfrog.sfcore.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;


/**
 * Defines a simple classloader which knows how to download classes from a URL.
 * Classloaders with the same URL are shared to avoid class equality problems.
 * Does NOT know how to handle jar files yet.
 *
 */
public class URLClassLoader extends BaseClassLoader {
    /** Table to maintain loaders for URLs. */
    protected static Hashtable<URL, ClassLoader> loaders = new Hashtable<URL, ClassLoader>();

    /** Base for class download. */
    protected URL classBase;

    /**
     * Constructor.
     *
     * @param u URL for this loader
     */
    public URLClassLoader(URL u) {
        classBase = u;
    }

    /**
     * Get a classloader for given URL. Looks loader up in loader-table, or
     * creates and inserts a new one if a new URL.
     *
     * @param u URL to find loader for
     *
     * @return ClassLoader
     */
    public static ClassLoader forURL(URL u) {
        if (!loaders.containsKey(u)) {
            loaders.put(u, new URLClassLoader(u));
        }
        return loaders.get(u);
    }

    /**
     * Override to locate the defining bytes for given class relative to the
     * class base. This is were support for URL to jar file should be added
     * (file caching could be an issue?).
     *
     * @param className class to find bytes for
     *
     * @return bytes defining class
     *
     * @exception IOException error while reading bytes
     */
    protected byte[] findClassBytes(String className) throws IOException {
        URLConnection connection = getResource(className.replace('.', '/') +
                ".class").openConnection();
        InputStream inputStream = connection.getInputStream();
        int length = connection.getContentLength();
        byte[] data = new byte[length];
        inputStream.read(data);
        inputStream.close();

        return data;
    }

    /**
     * Sets a name relative to the class base for this classloader.
     *
     * @param name name to make relative to class base
     *
     * @return complete URL for name (from class base)
     *
     * @exception IOException error constructing URL
     */
    protected URL relToClassBase(String name) throws IOException {
        return new URL(classBase, name);
    }

    /**
     * Gets the given resource as an input stream. Checks if super class can do
     * this, then looks at an offset from the class base
     *
     * @param resource resource to look up.
     *
     * @return input stream to resource, or null if not found
     */
    public InputStream getResourceAsStream(String resource) {
        InputStream result = super.getResourceAsStream(resource);

        if (result == null) {
            try {
                return relToClassBase(resource).openConnection().getInputStream();
            } catch (Exception ex) {
                // ignore
            }
        }

        return result;
    }

    /**
     * Gets a URL for given resource. This method always offsets from the
     * classbase.
     *
     * @param name resource to get
     *
     * @return URL to resource or null on error
     */
    public URL getResource(String name) {
        try {
            return relToClassBase(name);
        } catch (Exception ex) {
            return null;
        }
    }
}
