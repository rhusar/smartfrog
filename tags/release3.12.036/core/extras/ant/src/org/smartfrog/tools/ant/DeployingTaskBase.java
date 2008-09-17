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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used as an extended base for those tasks that do deployment, as it supports
 * declaring of applications as nested elements.
 * created 27-Feb-2004 15:27:47
 */

public abstract class DeployingTaskBase extends SmartFrogTask {


    /**
     * list of applications
     */
    protected List<Application> applications = new LinkedList<Application>();


    /**
     * codebase string
     */
    protected List<Codebase> codebase = new LinkedList<Codebase>();
    public static final String ERROR_NO_APPLICATIONS_DECLARED = "No applications declared";
    public static final String ACTION_DEPLOY = "DEPLOY";
    public static final String DEFAULT_SUBPROCESS = "";

    /**
     * add a new application to the list.
     * @return the created application
     */
    public Application createApplication() {
        Application application = createNewApplication();
        applications.add(application);
        return application;
    }

    /**
     * application factory is here for easy overriding
     *
     * @return the new application.
     */
    protected Application createNewApplication() {
        return new Application(this);
    }

    /**
     * get the count of applications
     *
     * @return current application count
     */
    protected int getApplicationCount() {
        return applications.size();
    }

    /**
     * test for apps existing
     *
     * @throws BuildException if the count is zero
     */
    protected void checkApplicationsDeclared() {
        if (getApplicationCount() == 0) {
            throw new BuildException(ERROR_NO_APPLICATIONS_DECLARED);
        }
    }

    /**
     * deploy the applications listed by creating a -a app descriptor list
     * on the command line
     */
    public void deployApplications() {
        verifyHostDefined();
        setupCodebase();
        for (Application application : applications) {
            application.validate();
            addArg("-a");
            String path = makePath(application);
            String subprocess = getSubprocess();

            addArg(application.getName() + ":" //NAME
                    + ACTION_DEPLOY + ":"      //Action: DEPLOY,TERMINATE,DETACH,DETaTERM
                    + path                     //URL
                    + "" + ":"                 // sfConfig or empty
                    + getHost() + ":"          // host
                    + subprocess);             // subprocess

        }
    }

    /**
     * Create a path from an application
     * @param application application
     * @return a path from its descriptor
     */
    private String makePath(Application application) {
        return "'\"" + application.getDescriptor() + "\"':";
    }

//    private String makePath2(Application application) {
//        return "\"" + application.getDescriptor() + "\":";
//    }
//    private String makePathWindows(Application application) {
//        return "\\\"" + application.getDescriptor() + "\\\":";
//    }

    /**
     * Get the subprocess we are deploying to.
     * The default is {@link #DEFAULT_SUBPROCESS}, though subclasses may override
     * this
     * @return the name of the subprocess to deployto
     */
    protected String getSubprocess() {
        return DEFAULT_SUBPROCESS;
    }

    /**
     * Add a new codebase element to the current set. The URLs of the codebase
     * will be visible to the deploying app.
     *
     * @param codebaseEntry a new codebase entry
     */
    public void addCodebase(Codebase codebaseEntry) {
        if (codebase == null) {
            codebase = new LinkedList<Codebase>();
        }
        codebase.add(codebaseEntry);
    }

    /**
     * set up the codebase params on the command line, if needed
     * @see org.smartfrog.sfcore.security.SFClassLoader#SF_CODEBASE_PROPERTY
     */
    private void setupCodebase() {
        if (codebase != null && !codebase.isEmpty()) {
            //add the codebase for extra stuff
            String codelist = Codebase.getCodebaseString(codebase);
            log("Codebase set to " + codelist, Project.MSG_VERBOSE);
            addJVMProperty(SmartFrogJVMProperties.CODEBASE, codelist);
        }
    }

    /**
     * the name and url of an application.
     * Interpretation of descriptor is by smartfrog; it includes resources as well
     * as codebase urls
     */
    public static class Application {

        /**
         * owner task
         */
        private TaskBase owner;
        public static final String ERROR_NO_APPLICATION_NAME = "no application name";
        public static final String ERROR_NO_APPLICATION_DESCRIPTOR = "no descriptor provided for ";
        public static final String ERROR_FILE_NOT_FOUND = "File does not exist: ";
        public static final String ERROR_NO_WRITE = "could not write to: ";
        public static final String APPLICATION_ENCODING = "UTF-8";

        /**
         * Create a bound application
         * @param owner owning task
         */
        public Application(TaskBase owner) {
            this.owner = owner;
        }

        /**
         * name of app
         */
        private String name;

        /**
         * descriptor: File, url, resource
         */
        private String descriptor;

        /**
         * text of an application
         */
        private String text;

        /**
         * name of the app
         *
         * @param name application name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * location of the app's descriptor
         *
         * @param descriptor resource path to the descripto
         */
        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        /**
         * set the file of the app. This is the same as the descriptor, except
         * that it must exist
         *
         * @param file descriptor filename
         */
        public void setFile(File file) {
            if (!file.exists()) {
                throw new BuildException(ERROR_FILE_NOT_FOUND+file);
            }
            descriptor = file.toString();
        }

        public String getName() {
            return name;
        }

        public String getDescriptor() {
            return descriptor;
        }

        /**
         * validate the descriptor
         */
        public void validate() {
            if (name == null) {
                throw new BuildException(ERROR_NO_APPLICATION_NAME);
            }


            if (descriptor == null) {
                throw new BuildException(ERROR_NO_APPLICATION_DESCRIPTOR + name);
            }
        }

        /**
         * set text inside. This will get saved.
         *
         * @param unexpandedText text to add; properties are expanded in the process.
         */
        public void addText(String unexpandedText) {
            //convert properties
            unexpandedText = owner.getProject().replaceProperties(unexpandedText);
            this.text=unexpandedText;
            //create a temp file
            File tempfile = FileUtils.getFileUtils().createTempFile("deploy",
                    ".sf", null);
            //mark for cleanup later
            if(owner.isDebug()) {
                owner.log("Application temporary files is "+tempfile);
                owner.log("This is not deleted in debug mode");
            } else {
                //no debugging, kill the file after we exit ant.
                tempfile.deleteOnExit();
            }
            owner.log("Saving to temporary file "+tempfile,Project.MSG_VERBOSE);
            owner.log(unexpandedText, Project.MSG_VERBOSE);
            OutputStream out = null;
            OutputStreamWriter writer = null;
            PrintWriter printer = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(tempfile));
                writer = new OutputStreamWriter(out, APPLICATION_ENCODING);
                printer = new PrintWriter(writer);
                printer.write(this.text);
                printer.flush();
                //remember our name
                setFile(tempfile);
            } catch (IOException e) {
                throw new BuildException(ERROR_NO_WRITE + tempfile, e);
            } finally {
                FileUtils.close(writer);
            }

        }
    }

}
