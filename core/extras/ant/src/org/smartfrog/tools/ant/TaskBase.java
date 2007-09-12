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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * This is a foundation task that manages classpath setup for everything underneath.
 *
 * @author steve loughran
 *         created 26-Feb-2004 11:50:17
 */

public abstract class TaskBase extends Task {
    /**
     * user supplied classpath
     */
    protected Path classpath;
    /**
     * user supplied reference to a classpath
     */
    protected Reference classpathRef;

    /**
     * flag set to include the ant runtime if class or classpath defined
     */
    protected boolean includeAntRuntime = true;
    /**
     * debug things
     */

    private boolean debug;

    /**
     * Called by the project to let the task initialize properly. The default
     * implementation is a no-op.
     *
     * @throws BuildException if something goes wrong with the build
     */
    public void init() throws BuildException {
        super.init();
        String property = getProject().getProperty(SmartFrogJVMProperties.ANT_DEBUG_PROPERTY);
        debug = Project.toBoolean(property);
    }


    /**
     * get at the debug switch.
     * @return true iff the property {@link SmartFrogJVMProperties#ANT_DEBUG_PROPERTY}
     * is true at init time
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the include ant run time policy.
     *
     * If a new classpath is passed in, should the existing one
     * (used when declaring the task) be included?
     *
     * If it is not, the new classpath
     * must include smartfrog.jar.
     * Default: false.
     *
     * @param includeAntRuntime flag for the ant runtime
     */
    public void setIncludeAntRuntime(boolean includeAntRuntime) {
        this.includeAntRuntime = includeAntRuntime;
    }

    /**
     * the classpath to run the parser
     *
     * @param cp classpath element
     */
    public void addClasspath(Path cp) {
        this.classpath = cp;
    }

    /**
     * a reference to the classpath to use to run smartfrog
     *
     * @param classpathRef classpath reference
     */
    public void setClasspathRef(Reference classpathRef) {
        this.classpathRef = classpathRef;
    }

    /**
     * create a java task
     *
     * @param entryPoint class to run as main
     * @return the java task ready to run
     */
    protected Java createJavaTask(String entryPoint) {
        Java java;
        java = new Java();
        bindToChild(java);
        java.setClassname(entryPoint);
        return java;
    }

    /**
     * set the classpath to the first valid item from
     * <ol>
     * <li>That of any classpath element
     * <li>That of any classpathref attribute
     * <li>If loaded by an AntClassLoader, by that loader's path
     * <li>the java.class.path
     * </ol>
     *
     * @param java the java task to set up
     */
    protected void setupClasspath(Java java) {
        boolean useRuntimeClasspath = includeAntRuntime;
        if (classpath != null) {
            java.setClasspath(classpath);
        } else {
            if (classpathRef != null) {
                java.setClasspathRef(classpathRef);
            } else {
                //no path defined, use the runtime
                useRuntimeClasspath = true;
            }
        }
        //now use the runtime classpath if requested.
        if (useRuntimeClasspath) {
            String pathstring;
            ClassLoader cl = getClass().getClassLoader();
            if (cl instanceof AntClassLoader) {
                AntClassLoader acl = (AntClassLoader) cl;
                pathstring = acl.getClasspath();
                log("using classpath of task", Project.MSG_DEBUG);
            } else {
                log("resorting to the java.class.path", Project.MSG_DEBUG);
                pathstring = System.getProperty("java.class.path");
            }
            Path path = new Path(getProject(), pathstring);
            java.setClasspath(path);
        }
    }

    /**
     * set a child task up as bound to this.
     * It is the inverse of Task.bindToOwner() in Ant1.7+,
     * implemented here, adding a call to init() for good measure.
     * @param child a child task
     */
    public void bindToChild(Task child) {
        new TaskHelper(this).bindTask(child);
    }
}
