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
 * interface for an ant build
 *
 */

public interface AntBuild extends AntWorkflowComponent {


    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_TARGETS = "targets";

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
    String ATTR_ANTFILE = "antfile";

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_KEEPGOINGACROSSFILES = "keepGoingAcrossFiles";

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_KEEPGOINGINSINGLEBUILD = "keepGoingInSingleBuild";

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_SHUTDOWNTIMEOUT = "shutdownTimeout";

    /**
     * SmartFrog attribute {@value}
     */
    String ATTR_SKIPUNIMPLEMENTEDTARGETS="skipUnimplementedTargets";
}
