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
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
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
    protected List applications = new LinkedList();


    /**
     * codebase string
     */
    protected List codebase = new LinkedList();

    /**
     * keep in sync with whatever the classloader uses, including
     * the format it takes
     * @see org.smartfrog.sfcore.security.SFClassLoader#SF_CODEBASE_PROPERTY
     */
    protected static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";

    /**
     * add a new application to the list.
     */
    public Application createApplication() {
        Application application = createNewApplication();
        applications.add(application);
        return application;
    }

    /**
     * application factory is here for easy overriding
     *
     * @return
     */
    protected Application createNewApplication() {
        return new Application(this);
    }

    /**
     * get the count of applications
     *
     * @return
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
            throw new BuildException("No applications declared");
        }
    }

    /**
     * deploy the applications listed by creating a -a app descriptor list
     * on the command line
     */
    public void deployApplications() {
        verifyHostDefined();
        setupCodebase();
        Iterator it = applications.iterator();
        while (it.hasNext()) {
            Application application = (Application) it.next();
            application.validate();
            addArg("-a");
            addArg(application.getName() + ":" //NAME
                    + "DEPLOY" + ":"              //Action: DEPLOY,TERMINATE,DETACH,DETaTERM
                    + application.getDescriptor() + ":"                    //URL
                    + "" + ":"                    // sfConfig or empty
                    + getHost() + ":"              // host
                    + "");                // subprocess

        }
    }

    /**
     * Add a new codebase element to the current set. The URLs of the codebase
     * will be visible to the deploying app.
     *
     * @param codebaseEntry a new codebase entry
     */
    public void addCodebase(Codebase codebaseEntry) {
        if (codebase == null) {
            codebase = new LinkedList();
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
            addJVMProperty(CODEBASE_PROPERTY, codelist);
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
        private Task owner;

        public Application(Task owner) {
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
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * location of the app's descriptor
         *
         * @param descriptor
         */
        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        /**
         * set the file of the app. This is the same as the descriptor, except
         * that it must exist
         *
         * @param file
         */
        public void setFile(File file) {
            if (!file.exists()) {
                throw new BuildException("File " + file + " does not exist");
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
                throw new BuildException("no application name");
            }


            if (descriptor == null) {
                throw new BuildException("no descriptor provided for " + name);
            }
        }

        /**
         * set text inside. This will get saved.
         *
         * @param text
         */
        public void addText(String text) {
            //convert properties
            text = owner.getProject().replaceProperties(text);
            this.text=text;
            //create a temp file
            File tempfile = FileUtils.newFileUtils().createTempFile("deploy",
                    ".sf", null);
            //mark for cleanup later
            tempfile.deleteOnExit();
            owner.log("Saving to temporary file "+tempfile,Project.MSG_VERBOSE);
            owner.log(text, Project.MSG_VERBOSE);
            OutputStream out = null;
            OutputStreamWriter writer = null;
            PrintWriter printer = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(tempfile));
                writer = new OutputStreamWriter(out, "UTF-8");
                printer = new PrintWriter(writer);
                printer.write(this.text);
                printer.flush();
                //remember our name
                setFile(tempfile);
            } catch (IOException e) {
                throw new BuildException("could not write to " + tempfile, e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ignored) {

                    }
                }
            }

        }
    }

    /**
     * this contains information pointing to the location of code.
     * It can either be a URL or a file path to a Java file.
     */
    public static class Codebase {

        /**
         * location of a JAR file
         */
        private String location;

        /**
         * the URL of the JAR file
         *
         * @param url
         */
        public void setURL(String url) {
            location = url;
        }

        /**
         * provide a URL. This is for the convenience of programmatic access, not
         * ant build files
         *
         * @param url
         */
        public void setURL(URL url) {
            location = url.toExternalForm();
        }

        /**
         * name a JAR file for addition to the path
         * The path must be visible to the server process(es) at this location,
         * which means it is either on a shared filestore, or you are only
         * deploying to a local daemon.
         *
         * @param file
         */
        public void setFile(File file) {
            if (!file.exists()) {
                throw new BuildException("Not found :" + file);
            }
            if (file.isDirectory()) {
                throw new BuildException("Not a JAR file :" + file);
            }
            try {
                setURL(file.toURL());
            } catch (MalformedURLException e) {
                throw new BuildException(e);
            }
        }

        /**
         * get the location
         *
         * @return
         */
        public String getLocation() {
            return location;
        }

        /**
         * take a list of codebase elements and then turn them into a string
         *
         * @param codebases
         * @return
         */
        public static String getCodebaseString(List codebases) {
            StringBuffer results = new StringBuffer();
            Iterator it = codebases.iterator();
            while (it.hasNext()) {
                Codebase codebase = (Codebase) it.next();
                String l = codebase.getLocation();
                if (l == null) {
                    throw new BuildException("Undefined codebase");
                }
                results.append(l);
                //space separated options here
                results.append(' ');
            }
            return new String(results);
        }


    }

}
