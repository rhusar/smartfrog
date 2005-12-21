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
package org.smartfrog.sfcore.languages.cdl.importing;

import java.net.URL;
import java.io.IOException;

/**
 * created 01-Nov-2005 17:26:43
 */

public class RelativeClasspathResolver extends ClasspathResolver {

    private URL baseLocation;

    /**
     * use our own classloader
     */
    public RelativeClasspathResolver(URL baseLocation) {
        this.baseLocation = baseLocation;
    }

    /**
     * map the path to a URI. For in-classpath resolution, URLs of the type
     * returned by
     *
     * @param path
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    public URL convertToSourceURL(String path) throws IOException {
        URL url = resolve(path);
        if(url!=null) {
            return url;
        }
        //otherwise, it is a relative operation. So we have to create a local URL then,
        //what? apply a transform.
        URL relative=new URL(baseLocation,path);
        return relative;
        //return super.resolveToURL(path);
    }
}
