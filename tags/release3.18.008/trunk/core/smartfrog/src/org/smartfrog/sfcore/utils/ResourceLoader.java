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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

/**
 * This is something that can hand off resource loading to whatever does
 * loading.
 * created Jul 1,
 * <p/>
 * 2004 4:44:38 PM
 */

public class ResourceLoader {

    private String codebase = null;
    private ClassLoader loader = null;
    public static final String ERROR_NOT_LOADED = "Resource not loaded: ";

    public ResourceLoader() {
        loader = getClass().getClassLoader();
    }

    public ResourceLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public ResourceLoader(Class clazz) {
        this(clazz.getClassLoader());
    }


    /**
     * load with a given codebase; goes through the smartfrog loader.
     *
     * @param sfCodebase the codebase
     */
    public ResourceLoader(String sfCodebase) {
        codebase = sfCodebase;
    }


    /**
     * get the sfcodebase from a component. This is used to trigger sfcodebase
     * operation.
     *
     * @param owner owner class
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogResolutionException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public ResourceLoader(Prim owner) throws SmartFrogResolutionException,
            RemoteException {
        ComponentHelper helper = new ComponentHelper(owner);
        codebase = helper.getCodebase();
    }

    /**
     * load a resource using the classpath of the component at question.
     *
     * @param resourcename name of resource on the classpath
     * @return an input stream if the resource was found and loaded
     * @throws RuntimeException if the resource is not on the classpath
     */
    private InputStream loadResourceThroughSmartFrog(String resourcename)
            throws RuntimeException {
        String targetCodeBase = codebase;

        InputStream in = SFClassLoader.getResourceAsStream(resourcename,
                targetCodeBase,
                true);
        return in;
    }

    /**
     * Load a resource through the loader
     * @param resourceName name of the resource
     * @return the input stream or null
     */
    private InputStream loadResourceThroughClassloader(String resourceName) {
        InputStream in = loader.getResourceAsStream(resourceName);
        return in;
    }

    /**
     * Assert that a resource is loaded
     * @param in input stream (may be null)
     * @param resourcename the name of the resource (For the error)
     * @throws IOException if the file was not found
     */
    private void assertResourceLoaded(InputStream in, String resourcename)
            throws IOException {
        if ( in == null ) {
            throw new IOException(ERROR_NOT_LOADED + resourcename);
        }
    }

    /**
     * load a resource.
     *
     * @param resourceName name of the resource
     * @return the open stream
     * @throws IOException if a resource is missing
     */
    public InputStream loadResource(String resourceName) throws IOException {
        InputStream in;
        if ( codebase != null ) {
            in = loadResourceThroughSmartFrog(resourceName);
        } else {
            in = loadResourceThroughClassloader(resourceName);
        }
        assertResourceLoaded(in, resourceName);
        return in;
    }

    /**
     * load a resource into a string.
     *
     * @param resourceName name of the resource
     * @return the resource loaded as a string
     * @throws IOException if a resource is missing
     */
    public String loadResourceAsString(String resourceName) throws IOException {
        InputStream in=loadResource(resourceName);
        InputStreamReader reader=new InputStreamReader(in);
        StringBuffer buffer=new StringBuffer();
        char[] block=new char[1024];
        int read;
        while(((read = reader.read(block))>=0)) {
            buffer.append(block,0,read);
        }
        return buffer.toString();
    }
}
