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

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.apache.tools.ant.Project;

import java.util.Vector;

/**
 *
 * Created 31-Oct-2007 15:08:12
 *
 */

public class AntHelper {

    private Prim owner;


    public AntHelper(Prim owner) {
        this.owner = owner;
    }

    public Project createNewProject() {
        Project project = new Project();
        project.setCoreLoader(null);
        project.init();
        return project;
    }

    /**
     * Listen to a project on the specified log, at the given level.
     * You don't need to do this when creating a project from another project
     * @param project project
     * @param level ant log level
     * @param log smartfrog log
     */
    public void listenToProject(Project project, int level, LogSF log) {
        //Register build listener
        org.apache.tools.ant.DefaultLogger logger = new AntToSmartFrogLogger(log);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setMessageOutputLevel(level);
        project.addBuildListener(logger);
    }


    public void setUserProperties(Project project, Vector propList) throws SmartFrogResolutionException {
        if (propList != null) {
            for (Object aPropList : propList) {
                Vector entry = (Vector) aPropList;
                if (entry.size() != 2) {
                    throw new SmartFrogResolutionException("Property entry of the wrong size" + entry);
                }
                String name = entry.get(0).toString();
                String value = entry.get(1).toString();
                project.setUserProperty(name, value);
            }
        }
    }

    public void validateAnt() throws SmartFrogDeploymentException {
        if (SFClassLoader.getResourceAsStream("/org/apache/tools/ant/Project.class") == null) {
            throw new SmartFrogDeploymentException(
                    "Cannot initialize Ant. WARNING: Perhaps ant.jar is not in CLASSPATH ...");
        }
    }

}
