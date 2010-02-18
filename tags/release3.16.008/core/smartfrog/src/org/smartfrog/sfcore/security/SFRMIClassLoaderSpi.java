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

package org.smartfrog.sfcore.security;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;


/**
 * <code>RMIClassLoaderSpi</code> is the service provider interface for
 * <code>RMIClassLoader</code>. We wrap the default RMIClassLoaderSpi to add
 * extra checks.
 *
 */
public class SFRMIClassLoaderSpi extends RMIClassLoaderSpi {
    /** A debugging utility to print messages. */
    private static SFDebug debug;

    /**
     * Initializes the debugging.
     */
    static {
        debug = SFDebug.getInstance("SFRMIClassLoaderSpi");
    }

    /** Default RMI class loader implementation. */
    RMIClassLoaderSpi defaultProviderInstance = null;

    public SFRMIClassLoaderSpi() {
        defaultProviderInstance = RMIClassLoader.getDefaultProviderInstance();

        if (debug != null) {
            debug.println("Constructor called");
        }
    }

    /**
     * Provides the implementation for {@link
     * RMIClassLoader#loadClass(URL,String)}, {@link
     * RMIClassLoader#loadClass(String,String)}, and {@link
     * RMIClassLoader#loadClass(String,String,ClassLoader)}. Loads a class
     * from a codebase URL path, optionally using the supplied loader.
     * Typically, a provider implementation will attempt to resolve the named
     * class using the given <code>defaultLoader</code>, if specified, before
     * attempting to resolve the class from the codebase URL path.
     * 
     * <p>
     * An implementation of this method must either return a class with the
     * given name or throw an exception.
     * </p>
     *
     * @param codebase the list of URLs (separated by spaces) to load the class
     *        from, or <code>null</code>
     * @param name the name of the class to load
     * @param defaultLoader additional contextual class loader to use, or
     *        <code>null</code>
     *
     * @return the <code>Class</code> object representing the loaded class
     *
     * @throws MalformedURLException if <code>codebase</code> is
     *         non-<code>null</code> and contains an invalid URL, or if
     *         <code>codebase</code> is <code>null</code> and the system
     *         property <code>java.rmi.server.codebase</code> contains an
     *         invalid URL
     * @throws ClassNotFoundException if a definition for the class could not
     *         be found at the specified location
     */
    public Class loadClass(String codebase, String name,
        ClassLoader defaultLoader)
        throws MalformedURLException, ClassNotFoundException {
        Class result = defaultProviderInstance.loadClass(codebase, name,
                defaultLoader);

        if (debug != null) {
            debug.println("loadclass:#1 codebase=" + codebase + " name=" +
                name + " cl=" + defaultLoader);
        }

        SFClassLoader.quickRejectClass(result);

        if (debug != null) {
            debug.println("loadclass:#2 codebase=" + codebase + " name=" +
                name + " cl=" + defaultLoader);
        }

        return result;
    }

    /**
     * Provides the implementation for {@link
     * RMIClassLoader#loadProxyClass(String,String[],ClassLoader)}. Loads a
     * dynamic proxy class (see {@link java.lang.reflect.Proxy} that
     * implements a set of interfaces with the given names from a codebase URL
     * path, optionally using the supplied loader.
     * 
     * <p>
     * An implementation of this method must either return a proxy class that
     * implements the named interfaces or throw an exception.
     * </p>
     *
     * @param codebase the list of URLs (space-separated) to load classes from,
     *        or <code>null</code>
     * @param interfaces the names of the interfaces for the proxy class to
     *        implement
     * @param defaultLoader additional contextual class loader to use, or
     *        <code>null</code>
     *
     * @return a dynamic proxy class that implements the named interfaces
     *
     * @throws MalformedURLException if <code>codebase</code> is
     *         non-<code>null</code> and contains an invalid URL, or if
     *         <code>codebase</code> is <code>null</code> and the system
     *         property <code>java.rmi.server.codebase</code> contains an
     *         invalid URL
     * @throws ClassNotFoundException if a definition for one of the named
     *         interfaces could not be found at the specified location, or if
     *         creation of the dynamic proxy class failed (such as if {@link
     *         java.lang.reflect.Proxy#getProxyClass(ClassLoader,Class[])}
     *         would throw an <code>IllegalArgumentException</code> for the
     *         given interface list)
     */
    public Class loadProxyClass(String codebase, String[] interfaces,
        ClassLoader defaultLoader)
        throws MalformedURLException, ClassNotFoundException {
        Class result = defaultProviderInstance.loadProxyClass(codebase,
                interfaces, defaultLoader);

        if (debug != null) {
            debug.println("loadProxyClass:#1 codebase=" + codebase +
                " interf=" + interfaces + " cl=" + defaultLoader);
        }

        SFClassLoader.quickRejectClass(result);

        if (debug != null) {
            debug.println("loadProxyClass:#2 codebase=" + codebase +
                " interf=" + interfaces + " cl=" + defaultLoader);
        }

        return result;
    }

    /**
     * Provides the implementation for {@link
     * RMIClassLoader#getClassLoader(String)}. Returns a class loader that
     * loads classes from the given codebase URL path.
     * 
     * <p>
     * If there is a security manger, its <code>checkPermission</code> method
     * will be invoked with a <code>RuntimePermission("getClassLoader")</code>
     * permission; this could result in a <code>SecurityException</code>. The
     * implementation of this method may also perform further security checks
     * to verify that the calling context has permission to connect to all of
     * the URLs in the codebase URL path.
     * </p>
     *
     * @param codebase the list of URLs (space-separated) from which the
     *        returned class loader will load classes from, or
     *        <code>null</code>
     *
     * @return a class loader that loads classes from the given codebase URL
     *         path
     *
     * @throws MalformedURLException if <code>codebase</code> is
     *         non-<code>null</code> and contains an invalid URL, or if
     *         <code>codebase</code> is <code>null</code> and the system
     *         property <code>java.rmi.server.codebase</code> contains an
     *         invalid URL
     */
    public ClassLoader getClassLoader(String codebase)
        throws MalformedURLException { // SecurityException

        ClassLoader result = defaultProviderInstance.getClassLoader(codebase);

        if (debug != null) {
            debug.println("getClassLoader:#1 codebase=" + codebase);
        }

        SFClassLoader.quickRejectObject(result);

        if (debug != null) {
            debug.println("getClassLoader:#2 codebase=" + codebase);
        }

        return result;
    }

    /**
     * Provides the implementation for {@link
     * RMIClassLoader#getClassAnnotation(Class)}. Returns the annotation
     * string (representing a location for the class definition) that RMI will
     * use to annotate the class descriptor when marshalling objects of the
     * given class.
     *
     * @param cl the class to obtain the annotation for
     *
     * @return a string to be used to annotate the given class when it gets
     *         marshalled, or <code>null</code>
     */
    public String getClassAnnotation(Class cl) {
        return defaultProviderInstance.getClassAnnotation(cl);
    }
}
