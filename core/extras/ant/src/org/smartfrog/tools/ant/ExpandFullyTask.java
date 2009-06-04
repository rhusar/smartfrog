/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.PropertySet;

import java.io.File;

/**
 * Parses the source .sf file and creates a fully expanded version, one
 * that can be copied to remote systems for deployment.
 * @ant.task category="SmartFrog" name="sf-expandfully"
 * <p/>
 * created 04-Jun-2009
 */

public class ExpandFullyTask extends TaskBase implements SysPropertyAdder {

    private File source, dest;

    /**
     * parser subprocess
     */
    private Java parser;
    public static final String ERROR_NO_SOURCE = "no source attribute";
    public static final String ERROR_NO_TEST = "no dest attribute";
    public static final String ERROR_NO_SOURCEFILE = "File not found :";
    public static final String EXPAND_FAILURE = "Expand failure";
    public static final String FAILED_WITH_ERROR_CODE = "Operation failed with error code ";

    /**
     * Called by the project to let the task initialize properly. The default
     * implementation is a no-op.
     *
     * @throws BuildException if something goes wrong with the build
     */
    public void init() throws BuildException {
        super.init();
        String entryPoint = SmartFrogJVMProperties.EXPANDFULLY_ENTRY_POINT;
        parser = createJavaTask(entryPoint);
        parser.setFailonerror(true);
        parser.setFork(true);
    }

    /**
     * name a single file for parsing. Exactly equivalent to a nested fileset
     * with a file attribute
     *
     * @param file file to parse
     */
    public void setSource(File file) {
        source = file;
    }

    /**
     * Set the name of the destination file to create
     * @param dest filename of the output file
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

    /**
     * execute the task
     *
     * @throws BuildException
     */
    @SuppressWarnings({"RefusedBequest"})
    public void execute() throws BuildException {
        String fullCommandLine;
        int err;
        //now let's configure the parser
        setupClasspath(parser);
        parser.setFork(true);
        if (source == null) {
            throw new BuildException(ERROR_NO_SOURCE);
        }
        if (dest == null) {
            throw new BuildException(ERROR_NO_TEST);
        }
        if (!source.exists()) {
            throw new BuildException(ERROR_NO_SOURCEFILE + source);
        }
        //and add various options to it
        parser.createArg().setFile(source);
        parser.createArg().setFile(dest);
        fullCommandLine = parser.getCommandLine().toString();
        log(fullCommandLine,Project.MSG_VERBOSE);
        //run it
        err = parser.executeJava();

        //process the results
        switch (err) {
            case 0:
                //success
                break;
            case 69:
            case 1:
                //parse fail
                throw new BuildException(EXPAND_FAILURE);
            default:
                //something else
                throw new BuildException(FAILED_WITH_ERROR_CODE + err
                +"\nJava Command: " + fullCommandLine);
        }

    }

    /**
     * add a property
     *
     * @param sysproperty system property
     */
    public void addSysproperty(Environment.Variable sysproperty) {
        parser.addSysproperty(sysproperty);
    }

    /**
     * Adds a set of properties as system properties.
     *
     * @param propset set of properties to add
     */
    public void addSyspropertyset(PropertySet propset) {
        parser.addSyspropertyset(propset);
    }

    /**
     * add a property file to the JVM
     *
     * @param propFile property file
     */
    public void addConfiguredPropertyFile(PropertyFile propFile) {
        propFile.addPropertiesToJvm(this);
    }

}