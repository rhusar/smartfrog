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

import org.smartfrog.SFLoader;
import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.prim.Prim;
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

    private ClassLoader loader = null;
    private ClassLoadingEnvironment loadingEnv = null;

    public ResourceLoader() {
        loader = getClass().getClassLoader();
    }

    public ResourceLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public ResourceLoader(Class clazz) {
        this(clazz.getClassLoader());
    }

    public ResourceLoader(Prim component) throws SmartFrogResolutionException, RemoteException {
        try {
            loadingEnv = new ComponentHelper(component).getClassLoadingEnvironment();
        } catch (ClassCastException e) {
            throw new SmartFrogResolutionException("The value of sfClassLoadingEnvironment is not of the correct class", e);
        }
    }

    /**
     * 
     * @param source The {@link ClassLoadingEnvironment} to load resources from.
     */
    public ResourceLoader(ClassLoadingEnvironment source) {
        loadingEnv = source;
    }

    /**
     * load a resource using the classpath of the component at question.
     *
     * @param resourcename name of resource on the classpath
     * @return an input stream if the resource was found and loaded
     * @throws RuntimeException if the resource is not on the classpath
     */
    private InputStream loadResourceThroughSmartFrog(String resourcename) throws IOException {        
        return SFLoader.getInputStream(resourcename, loadingEnv);
    }

    private InputStream loadResourceThroughClassloader(String resourceName) throws IOException {
        InputStream in = loader.getResourceAsStream(resourceName);
        if (in == null) {
            throw new IOException("Not found: " + resourceName);
        }
        return in;
    }

    /**
     * load a resource.
     *
     * @param resourceName
     * @return
     * @throws IOException if a resource is missing
     */
    public InputStream loadResource(String resourceName) throws IOException {
        InputStream in;
        if ( loader == null ) {
            in = loadResourceThroughSmartFrog(resourceName);
        } else {
            in = loadResourceThroughClassloader(resourceName);
        }        
        return in;
    }

    /**
     * load a resource into a string.
     *
     * @param resourceName
     * @return
     * @throws IOException if a resource is missing
     */
    public String loadResourceAsString(String resourceName) throws IOException {

        InputStreamReader reader = null;
        try {
            InputStream in = loadResource(resourceName);
            reader = new InputStreamReader(in);
            StringBuffer buffer = new StringBuffer();
            char[] block = new char[1024];
            while((reader.read(block) >=0)) {
                buffer.append(block);
            }
            return buffer.toString();
        } finally {
            if (reader != null) reader.close();            
        }
    }
}
