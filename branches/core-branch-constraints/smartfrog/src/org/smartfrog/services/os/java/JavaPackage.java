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
package org.smartfrog.services.os.java;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * created Sep 30, 2004 11:41:06 AM
 */


public interface JavaPackage extends Remote {

    /*
    files extends OptionalVector;
    //a string classpath that is generated automatically
    //classpath extends OptionalString;
    //lazy attribute
    //classpathList extends OptionalVector;
    //classes to look for
    requiredClasses extends OptionalVector;
    */

    final String ATTR_REQUIRED_CLASSES="requiredClasses";
    final String ATTR_REQUIRED_RESOURCES = "requiredResources";
    final String ATTR_SOURCE = "source";
    final String ATTR_URICLASSPATH = "uriClasspath";
    final String ATTR_URICLASSPATHLIST = "uriClasspathList";
    final String ATTR_CLASSPATH = "classpath";
    final String ATTR_CLASSPATHLIST = "classpathList";
    final String ATTR_USECODEBASE ="useCodebase";

    /**
     * get the vector of uris
     * @return  vector
     */
    public Vector getUriClasspathList() throws RemoteException;
}
