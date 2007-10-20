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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.io.InputStream;
import java.io.IOException;

/**
 * This is a URL factory.
 * What is special about it is that it is bound to a resource loader, and can
 * handle classpath: URLs by resolving stuff on that resolver.
 *
 * Use this when working with URLs, rather than new URL() directly, because the
 * latter is not as easily extended as one woudl like
 * created 20-Dec-2005 12:57:04
 */

public class UrlFactory {

    private ResourceLoader loader;
    public static final String PROTOCOL_CLASSPATH = "classpath";

    ClasspathUrlStreamHandler handler;

    public UrlFactory(ResourceLoader loader) {
        this.loader = loader;
        handler=new ClasspathUrlStreamHandler(loader);
    }

    /**
     * Create a URL. normal URLs are done normally, but classpath: URLs are also handled
     * @param path
     * @return
     * @throws MalformedURLException
     */
    public URL createUrl(String path)
            throws MalformedURLException {
        try {
            //built in stuff
            return new URL(path);
        }
        catch (MalformedURLException failure) {
            // Ignore: try our handler list next.

            if(usesClasspathProtocol(path)) {
                //yes, a classpath protocol
                URLStreamHandler handler=getHandler();
                URL url= new URL(null,path,handler);
                return url;
            } else {
                throw failure;
            }
        }
    }

    /**
     * Create a url
     * @param base
     * @param path
     * @return
     * @throws MalformedURLException
     */
    public URL createUrl(URL base,String path)
            throws MalformedURLException {
        return new URL(base,path);
    }

    /**
     * Create a classpath URL
     * @param resource
     * @return the resource with the relevant prefix
     * @throws MalformedURLException
     */
    public URL createClasspathUrl(String resource) throws MalformedURLException {
        StringBuffer buffer=new StringBuffer();
        buffer.append(PROTOCOL_CLASSPATH);
        buffer.append(':');
        if(!resource.startsWith("/")) {
            buffer.append('/');
        }
        buffer.append(resource);
        return createUrl(buffer.toString());
    }

    /**
     * open the stream of a URL. For a normal URL this just calls
     * {@link java.net.URL#openStream()}. For classpath: URLs, we
     * load the resource in via the loader instead
     * @param url
     * @return
     * @throws IOException
     */
    public InputStream openStream(URL url) throws IOException {
        if(!isClasspathProtocol(url.getProtocol())) {
            return url.openStream();
        }
        String path = url.getPath();
        //now get bits after any prefixes.
        int first=0;
        while(first<path.length() && path.charAt(first)=='/') {
            first++;
        }
        path=path.substring(first);
        return loader.loadResource(path);
    }

    /**
     * get the protocol of a url
     * @param path path to extract
     * @return the protocol or null for no protocol
     */
    public static String getProtocol(String path)  {
        int colon = path.indexOf(':');
        if (colon <= 0) {
            return null;
        }
        String protocol = path.substring(0, colon);
        return protocol;
    }

    /**
     * Test for a path implementing the classpath: protocol
     * @param path
     * @return
     */
    public static boolean usesClasspathProtocol(String path) {
        String protocol = getProtocol(path);
        return isClasspathProtocol(protocol);
    }

    private static boolean isClasspathProtocol(String protocol) {
        return PROTOCOL_CLASSPATH.equals(protocol);
    }

    private URLStreamHandler getHandler() {
        return handler;
    }
}
