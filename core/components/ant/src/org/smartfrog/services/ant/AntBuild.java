/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ant;

import java.rmi.Remote;

/**
 *
 * Created 31-Oct-2007 14:23:52
 *
 */

/*
    antSchema extends Schema {

        //list of [name,value] pairs defining ant properties
        properties extends OptionalVector;

        //list of targets to run. If empty, the default target is executed
        targets extends OptionalVector;


        //this is an optional base directory. If it exists, then
        //it is used as the base directory for the antfile, and for
        //any directories in the directories list.
        basedir extends OptionalFilenameType;

        //the name of an ant file. If relative, it is resolved

        buildfile extends FilenameType;


        //a list of directories. Will be resolved relative to basedir, when
        //relative resolution is required.
        directories extends OptionalFilenameList;

    }
 */
public interface AntBuild extends Remote {

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_PROPERTIES=Ant.ATTR_PROPERTIES;

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_TARGETS = "targets";

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_BASEDIR = Ant.ATTR_BASEDIR;

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_BUILDFILE = "buildfile";

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_DIRECTORIES = "directories";


    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_GENERICANTFILE = "genericantfile";


}
