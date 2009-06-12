/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

/**
 * Ant Project help
 * created 14-Mar-2006 16:17:39
 */

public class ProjectHelper {

    private Project project;

    private int counter = 0;

    public ProjectHelper(Project project) {
        if(project==null) {
            throw new BuildException("No project");
        }
        this.project = project;
    }

    /**
     * using our memory location and a per-instance counter,
     * make up a new unique number.
     *
     * @return a new property name that is currently unique
     */
    public synchronized String createUniquePropertyName() {
        String prefix = toString() + "_";
        String name;
        do {
            name = prefix + counter++;
        } while (project.getProperty(name) != null);
        return name;
    }
}
