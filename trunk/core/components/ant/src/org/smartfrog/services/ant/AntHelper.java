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
import org.smartfrog.sfcore.utils.ListUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;

import java.util.Vector;
import java.util.Properties;

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

    public Project createNewProject() throws SmartFrogAntBuildException {
        try {
            Project project = new Project();
            project.setCoreLoader(null);
            project.init();
            return project;
        } catch (BuildException e) {
            throw new SmartFrogAntBuildException(e);
        }
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
        BuildLogger logger = new AntToSmartFrogLogger(log);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setMessageOutputLevel(level);
        project.addBuildListener(logger);
    }

    public void setUserProperties(Project project, Vector propList) throws SmartFrogResolutionException {
        if (propList != null) {
            Properties props= ListUtils.convertToProperties(propList);
            addUserProperties(project,props);
        }
    }

    /**
     * Add the properties to a project's user properties, those that are inherited all the way down.
     * @param project project
     * @param props properties
     */
    public void addUserProperties(Project project,Properties props) {
        for(String name:props.stringPropertyNames()) {
            project.setUserProperty(name, props.getProperty(name));
        }
    }



    public void validateAnt() throws SmartFrogDeploymentException {
        if (SFClassLoader.getResourceAsStream("/org/apache/tools/ant/Project.class") == null) {
            throw new SmartFrogDeploymentException(
                    "Cannot initialize Ant. WARNING: Perhaps ant.jar is not in CLASSPATH ...");
        }
    }

    /**
     * try and turn the value passed in to a log level
     * @param current current log level (==default)
     * @param value string value to check
     * @param sought string to look for
     * @param mapping the new level to return
     * @return mapping iff sought==value; else current.
     */
    private static int extractLogLevel(int current, String value, String sought, int mapping) {
        return sought.equals(value) ? mapping : current;
    }

    /**
     * Take the level string and turn it into a number for ant
     * @param logLevel incoming log level
     * @param initialLevel the initial log level
     * @return the ant log level
     */
    public int extractLogLevel(String logLevel, int initialLevel) {
        int level=initialLevel;
        level = extractLogLevel(level, logLevel, Ant.ATTR_LOG_LEVEL_DEBUG, Project.MSG_DEBUG);
        level = extractLogLevel(level, logLevel, Ant.ATTR_LOG_LEVEL_VERBOSE, Project.MSG_VERBOSE);
        level = extractLogLevel(level, logLevel, Ant.ATTR_LOG_LEVEL_INFO, Project.MSG_INFO);
        level = extractLogLevel(level, logLevel, Ant.ATTR_LOG_LEVEL_WARN, Project.MSG_WARN);
        level = extractLogLevel(level, logLevel, Ant.ATTR_LOG_LEVEL_ERROR, Project.MSG_ERR);
        return level;
    }
}
