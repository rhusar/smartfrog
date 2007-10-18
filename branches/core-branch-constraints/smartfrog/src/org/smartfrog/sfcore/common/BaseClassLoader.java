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
import java.util.Hashtable;


/**
 * Basic class loader functionality. Provides system class lookups only. Hook
 * methods are provided for subclasses to provide different mechanisms to
 * locate a Class or its defining bytes.
 * 
 */
public class BaseClassLoader extends ClassLoader {
    /** table of loaded classes */
    private Hashtable classes = new Hashtable();

    /**
     * Constructor. 
     */
    public BaseClassLoader() {
    }

    /**
     * Overriden to forward to the expanded method with resolve set to true,
     * since clients will generally want to resolve all their classes before
     * returning.
     *
     * @param className class to load
     *
     * @return requested class
     *
     * @throws ClassNotFoundException class not found
     */
    public Class loadClass(String className) throws ClassNotFoundException {
        return (loadClass(className, true));
    }

    /**
     * Main method to locate or load a given class. Calls findClass to locate a
     * Class first, it not found calls findClassBytes to see it defining bytes
     * can be downloaded from somewhere. If defined from bytes the class is
     * maintained in an internal table.
     *
     * @param className class to locate or load
     * @param resolveIt whether to resolve referred classes
     *
     * @return Class object
     *
     * @throws ClassNotFoundException class not found
     */
    public synchronized Class loadClass(String className, boolean resolveIt)
        throws ClassNotFoundException {
        Class result;
        byte[] classBytes = null;

        try {
            result = findClass(className);

            if (result != null) {
                return result;
            }
        } catch (Exception e) {
        }

        // Try to load it from preferred source
        try {
            classBytes = findClassBytes(className);
        } catch (IOException ioex) {
            ioex.printStackTrace();
            throw new ClassNotFoundException(ioex.getMessage());
        }

        // Define it (parse the class file)
        result = defineClass(className, classBytes, 0, classBytes.length);

        if (result == null) {
            throw new ClassFormatError();
        }

        // Resolve if necessary
        if (resolveIt) {
            resolveClass(result);
        }

        classes.put(className, result);

        return result;
    }

    /**
     * Hook method to locate a given class. Looks in the maintained classes
     * table to locate a previously loaded class. Then asks primordial
     * classloader to load class.
     *
     * @param className class to locate
     *
     * @return requested class; from table or system
     *
     * @throws ClassNotFoundException class not found
     */
    protected Class findClass(String className) throws ClassNotFoundException {
        // Check our local cache of classes
        Class result = (Class) classes.get(className);

        if (result == null) {
            // Check with the primordial class loader
            result = super.findSystemClass(className);
        }

        return result;
    }

    /**
     * This classloaded expects to find everything using the primordial
     * classloader, so this always throws an IOException. Subclasses can
     * implement this method to locate defining bytes elsewhere (eg. at a URL)
     *
     * @param className class to find bytes for
     *
     * @return bytes defining class
     *
     * @throws IOException error while trying to locate or load bytes
     */
    protected byte[] findClassBytes(String className) throws IOException {
        throw new IOException(className + " not found");
    }
}
