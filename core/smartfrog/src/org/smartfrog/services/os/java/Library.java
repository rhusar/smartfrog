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
package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileUsingComponent;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A set of libraries to use. Can feed directly into a java invocation
 * created 04-Apr-2005 12:16:46
 */


public interface Library extends FileUsingComponent {

    /*
    repositories extends Vector;
    //cache dir; is created if needed
    cacheDir extends Component;
    //runtime elements
    //libraries extends Vector
    //librariesCodebase extends String
    */

    /**
     * {@value}
     */
    String ATTR_REPOSITORIES = "repositories";
    /**
     * {@value}
     */
    String ATTR_CACHE_DIR = "cacheDir";
    /**
     * {@value}
     */
    String ATTR_LIBRARIES = "libraries";
    /**
     * {@value}
     */
    String ATTR_LIBRARIES_CODEBASE = "librariesCodebase";
    
    /**
     * {@value}
     */
    String ATTR_FLATTEN = "flatten";
	
    /**
     * Determine the absolute path of an artifact in the local library cache. 
     * @param project project name
     * @param artifact artifact name
     * @param version version string (may be null)
     * @param extension extension string (may be null for non JAR artifacts)
     * @return the path to the artifact. It may or may not exist. 
     * @throws RemoteException
     */
	public String determineArtifactPath(String project,String artifact,String version,String extension)
        throws RemoteException;

}
