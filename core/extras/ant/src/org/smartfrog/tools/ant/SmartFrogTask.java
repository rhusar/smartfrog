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
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.taskdefs.Java;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Class to let ant task derivatives run smartfrog. How it invokes smartfrog is an implementation detail;
 * it may be calling the Java task, it may be calling smartfrog direct.
 * What is not a detail is that the combined classpath of ant+ any classpath parameters must include
 * all the relevant smartfrog JAR files.
 *
 * Smartfrog can be configured via system properties, an ini file, or the explicit
 * properties of this task. All the attributes of this task that configure smartfrog
 * (port, liveness, spawning, stack trace) are undefined; whatever defaults are built into
 * SmartFrog apply, not any hard coded in the task. This also permits one to override smartfrog
 * by setting system properties inline, or in a property set.
 *
 */
public class SmartFrogTask extends Task {

    public SmartFrogTask() {

    }

    public void init() throws BuildException {
        smartfrog = getBaseJavaTask();
    }


    /**
     * name of host
     */
    private String hostname;

    /**
     * name of an app
     */
    private String applicationName;

    /**
     * source files
     */
    private List sourceFiles = new LinkedList();

    /**
     * ini file
     */
    private File iniFile;

    /**
     * our JVM
     */
    private Java smartfrog;

    /**
    SmartFrog daemon connection port.
    org.smartfrog.ProcessCompound.sfRootLocatorPort=3800;
    */
    private Integer port; // = 3800;

    /**
    Liveness check period (in seconds); default=15.
    org.smartfrog.ProcessCompound.sfLivenessDelay=15;
    */
    private Integer livenessCheckPeriod; // = 15;


    /**
     *  Liveness check retries
     * org.smartfrog.ProcessCompound.sfLivenessFactor=5;
    */
    private Integer  livenessCheckRetries; // = 5;
    /**
     * Allow spawning of subprocess
     *# org.smartfrog.ProcessCompound.sfProcessAllow=true;
    */
    private Boolean allowSpawning; // = true;

    /**
     * Subprocess creation/failure timeout - default=60 seconds
     * (slower machines might need longer periods to start a new subprocess)
     *  org.smartfrog.ProcessCompound.sfProcessTimeout=60;
     */
    private Integer  spawnTimeout; // = 60;


    /**
    * stack tracing
    * map to org.smartfrog.logger.logStrackTrace
    */
    private Boolean logStackTraces; //= false;

    /**
     * name of a file
     * org.smartfrog.iniSFFile
     */
    private File initialSmartfrogFile;


    /**
     * codebase string
     *
     */
    private List codebase=new LinkedList();

    /**
     * add a file to the list
     * @param filename
     */
    public void addSourceFile(File filename) {
        sourceFiles.add(filename.toString());
    }

    /**
     * add a URL to the list.
     * @param url URL (or file) to add
     */
    public void addSourceURL(String url) {
        sourceFiles.add(url);
    }

    /**
     * set the hostname to deploy to (optional)
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * port of daemon
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * liveness check in seconds; optional
     * @param livenessCheckPeriod
     */
    public void setLivenessCheckPeriod(Integer livenessCheckPeriod) {
        this.livenessCheckPeriod = livenessCheckPeriod;
    }

    /**
     * retry count
     * @param livenessCheckRetries
     */
    public void setLivenessCheckRetries(Integer livenessCheckRetries) {
        this.livenessCheckRetries = livenessCheckRetries;
    }

    /**
     * can this process spawn new processes?
     * @param allowSpawning
     */
    public void setAllowSpawning(Boolean allowSpawning) {
        this.allowSpawning = allowSpawning;
    }

    /**
     * when to assume a spawn failed -in seconds.
     * @param spawnTimeout
     */
    public void setSpawnTimeout(Integer spawnTimeout) {
        this.spawnTimeout = spawnTimeout;
    }

    /**
     * log stack traces on failure
     * @param logStackTraces
     */
    public void setLogStackTraces(Boolean logStackTraces) {
        this.logStackTraces = logStackTraces;
    }

    /**
     * the name of a smartfrog file to load
     * @param initialSmartfrogFile
     */
    public void setInitialSmartfrogFile(File initialSmartfrogFile) {
        if(!initialSmartfrogFile.exists()) {
            throw new BuildException("Not found: "+initialSmartfrogFile);
        }
        if(!initialSmartfrogFile.isFile()) {
            throw new BuildException("Unexpected file type: " + initialSmartfrogFile);
        }

        this.initialSmartfrogFile = initialSmartfrogFile;
    }


    protected String getHostname() {
        return hostname;
    }

    protected String getApplicationName() {
        return applicationName;
    }


    /**
     *  set the app name; optional on some actions
     * @param applicationName
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * An ini file can contain data that overrides the basic settings.
     * @param iniFile
     */
    public void setIniFile(File iniFile) {
        this.iniFile = iniFile;
    }

    /**
     * JVM classpath
     * @param classpath
     */
    public void setClasspath(Path classpath) {
        smartfrog.setClasspath(classpath);
    }

    /**
     * classpath reference
     * @param classpathRef
     */
    public void setClasspathRef(Reference classpathRef) {
        smartfrog.setClasspathRef(classpathRef);
    }

    /**
     * should the runtime log stack traces
     * @param logStackTraces
     */
    public void setLogStackTraces(boolean logStackTraces) {
        this.logStackTraces = new Boolean(logStackTraces);
    }

    /**
     * execution logic
     * @throws BuildException
     */
    public void execute() throws BuildException {

    }

    /**
     * get the base java task
     * @return
     */
    protected Java getBaseJavaTask() {
        Java java = (Java) getProject().createTask("java");
        java.setFailonerror(true);
        java.setFork(true);
        java.setClassname("org.smartfrog.SFSystem");
        return java;
    }

    /**
     * adds the hostname to the task
     * @param task
     */
    protected void addHostname(Java task) {
        if (hostname != null) {
            task.createArg().setValue("-h");
            task.createArg().setValue(hostname);
        }
    }

    /**
     * set a flag to tell the runtime to exit after actioning something
     * @param task
     */
    protected void addExitFlag(Java task) {
        task.createArg().setValue("-e");
    }

    /**
     * adds the ini file by setting the appropriate system property
     */
    protected void addIniFile() {
        if (iniFile != null && iniFile.exists()) {
            addSmartfrogProperty("org.smartfrog.iniFile",iniFile.toString());
        }
    }

    protected void setStandardSmartfrogProperties() {
        addSmartfrogPropertyIfDefined("org.smartfrog.logger.logStrackTrace",
            logStackTraces);
        addSmartfrogPropertyIfDefined("org.smartfrog.ProcessCompound.sfRootLocatorPort",
            port);
        addSmartfrogPropertyIfDefined("org.smartfrog.ProcessCompound.sfLivenessDelay",
            livenessCheckPeriod);
        addSmartfrogPropertyIfDefined("org.smartfrog.ProcessCompound.sfLivenessFactor",
            livenessCheckRetries);
        addSmartfrogPropertyIfDefined("org.smartfrog.ProcessCompound.sfProcessAllow",
            allowSpawning);
        addSmartfrogPropertyIfDefined("org.smartfrog.ProcessCompound.sfProcessTimeout",
            spawnTimeout);
        /*
        addSmartfrogPropertyIfDefined("",
                );
        addSmartfrogPropertyIfDefined("",
                );
        addSmartfrogPropertyIfDefined("",
                );
        */
    }


    protected boolean addApplicationName(String command, Java task) {
        if (applicationName != null) {
            task.createArg().setValue(command);
            task.createArg().setValue(applicationName);
            return true;
        }
        return false;
    }


    /**
     * Adds a system property.
     *
     * @param sysp system property
     */
    public void addSysproperty(Environment.Variable sysp) {
        smartfrog.addSysproperty(sysp);
    }

    /**
     * Adds a set of properties as system properties.
     *
     * @param sysp set of properties to add
     *
     * @since Ant 1.6
     */
    public void addSyspropertyset(PropertySet sysp) {
        smartfrog.addSyspropertyset(sysp);
    }
    /**
     * set a sys property on the smartfrog JVM
     * @param name
     * @param value
     */
    protected void addSmartfrogProperty(String name,String value) {
        Environment.Variable property = new Environment.Variable();
        property.setKey(name);
        property.setValue(value);
        smartfrog.addSysproperty(property);
    }


    /**
     * set a sys property on the smartfrog JVM if the object used to set the property value is defined
     * @param name
     * @param object: if defined the toString() value is used
     */
    protected void addSmartfrogPropertyIfDefined(String name, Object object) {
        if(object!=null) {
            addSmartfrogProperty(name, object.toString());
        }
    }


    /**
     * the name and url of an application. Name is optional; descriptor is not.
     * Interpretation of descriptor is by smartfrog; it includes resources as well
     * as codebase urls
     */
    public static class Application {
        private String name;
        private String descriptor;

        /**
         * optional name of the app
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * not so optional location of the app
         * @param descriptor
         */
        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        public String getName() {
            return name;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public void validate() {
            if(descriptor==null) {
                String appname=name==null?"application":name;
                throw new BuildException("no descriptor provided for "+appname);
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
         * @param url
         */
        public void setURL(String url) {
            location=url;
        }

        /**
         * provide a URL. This is for the convenience of programmatic access, not
         * ant build files
         * @param url
         */
        public void setURL(URL url) {
            location = url.toExternalForm();
        }

        /**
         * name a JAR file for addition to the path
         * @param file
         */
        public void setFile(File file) {
            if(!file.exists()) {
                throw new BuildException("Not found :"+file);
            }
            if(file.isDirectory()) {
                throw new BuildException("Not a JAR file :"+file);
            }
            try {
                setURL(file.toURL());
            } catch (MalformedURLException e) {
                throw new BuildException(e);
            }
        }

        /**
         * get the location
         * @return
         */
        public String getLocation() {
            return location;
        }

        /**
         * take a list of codebase elements and then turn them into a string
         * @param codebases
         * @return
         */
        public static String getCodebaseString(List codebases) {
            StringBuffer results=new StringBuffer();
            Iterator it=codebases.iterator();
            while (it.hasNext()) {
                Codebase codebase = (Codebase) it.next();
                String l= codebase.getLocation();
                if(l==null) {
                    throw new BuildException("Undefined codebase");
                }
                results.append(l);
                results.append(':');
            }
            return new String(results);
        }

    }

}
