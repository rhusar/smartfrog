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
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to let ant task derivatives run smartfrog. How it invokes smartfrog is an implementation detail;
 * it may be calling the Java task, it may be calling smartfrog direct.
 * What is not a detail is that the combined classpath of ant+ any classpath parameters must include
 * all the relevant smartfrog JAR files.
 * <p>
 * Smartfrog can be configured via system properties, an ini file, or the explicit
 * properties of this task. All the attributes of this task that configure SmartFrog
 * (port, liveness, spawning, stack trace) are undefined; whatever defaults are built into
 * SmartFrog apply, not any hard coded in the task. This also permits one to override smartfrog
 * by setting system properties inline, or in a property set.
 * <p>
 * By default, all tasks are given a timeout of ten minutes; and propagate any
 * failure to execute to the build file. These can be adjusted via the
 * {@link SmartFrogTask#setFailOnError(boolean)} call, and
 * {@link SmartFrogTask#setFailOnError(boolean)} calls respectively.
 */
public abstract class SmartFrogTask extends TaskBase {

    /**
     * the name of a root process
     */
    protected static final String ROOT_PROCESS = "rootProcess";

    public SmartFrogTask() {

    }

    /**
     * initialisation routine; set up some settings like the java task
     * that we configure as we go
     * @throws BuildException
     */
    public void init() throws BuildException {
        smartfrog = getBaseJavaTask();
        setFailOnError(true);
        setTimeout(DEFAULT_TIMEOUT_VALUE);
    }


    /**
     * name of host
     */
    private String host = null;


    /**
     * source files
     */
    protected List sourceFiles = new LinkedList();

    /**
     * ini file
     */
    protected File iniFile;

    /**
     * our JVM
     */
    protected Java smartfrog;

    /**
     * SmartFrog daemon connection port.
     * org.smartfrog.ProcessCompound.sfRootLocatorPort=3800;
     */
    protected Integer port; // = 3800;

    /**
     * Liveness check period (in seconds); default=15.
     * org.smartfrog.ProcessCompound.sfLivenessDelay=15;
     */
    protected Integer livenessCheckPeriod; // = 15;


    /**
     * Liveness check retries
     * org.smartfrog.ProcessCompound.sfLivenessFactor=5;
     */
    protected Integer livenessCheckRetries; // = 5;
    /**
     * Allow spawning of subprocess
     * # org.smartfrog.ProcessCompound.sfProcessAllow=true;
     */
    protected Boolean allowSpawning; // = true;

    /**
     * Subprocess creation/failure timeout - default=60 seconds
     * (slower machines might need longer periods to start a new subprocess)
     * org.smartfrog.ProcessCompound.sfProcessTimeout=60;
     */
    protected Integer spawnTimeout; // = 60;


    /**
     * stack tracing
     * map to org.smartfrog.logger.logStrackTrace
     */
    protected Boolean logStackTraces; //= false;

    /**
     * name of a file
     */
    protected File initialSmartFrogFile=null;


    /**
     * flag to set clear failonerror handling
     */
    protected boolean failOnError;

    /**
     * what is the default timeout for those tasks that have a timeout
     */
    public static final long DEFAULT_TIMEOUT_VALUE = 60*10*1000L;

    /**
     * our security holder
     */
    private SecurityHolder securityHolder=new SecurityHolder();

    /**
     * add a file to the list
     *
     * @param filename
     */
    public void addSourceFile(File filename) {
        sourceFiles.add(filename.toString());
    }

    /**
     * add a URL to the list.
     *
     * @param url URL (or file) to add
     */
    public void addSourceURL(String url) {
        sourceFiles.add(url);
    }


    /**
     * set the hostname to deploy to (optional)
     *
     * @param host
     */
    public void setHost(String host) {
        log("setting host to "+host, Project.MSG_DEBUG);
        this.host = host;
    }

    /**
     * port of daemon; optional -default is 3800
     *
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * liveness check in seconds; optional
     *
     * @param livenessCheckPeriod
     */
    public void setLivenessCheckPeriod(Integer livenessCheckPeriod) {
        this.livenessCheckPeriod = livenessCheckPeriod;
    }

    /**
     * retry count for liveness checks.
     *
     * @param livenessCheckRetries
     */
    public void setLivenessCheckRetries(Integer livenessCheckRetries) {
        this.livenessCheckRetries = livenessCheckRetries;
    }

    /**
     * can this process spawn new processes?
     *
     * @param allowSpawning
     */
    public void setAllowSpawning(boolean allowSpawning) {
        this.allowSpawning = new Boolean(allowSpawning);
    }

    /**
     * when to assume a spawn failed -in seconds.
     *
     * @param spawnTimeout
     */
    public void setSpawnTimeout(Integer spawnTimeout) {
        this.spawnTimeout = spawnTimeout;
    }


    /**
     * the name of a smartfrog file to load on startupe
     *
     * @param initialSmartFrogFile
     */
    public void setInitialSmartFrogFile(File initialSmartFrogFile) {
        if(initialSmartFrogFile!=null && initialSmartFrogFile.length()>0 ) {
            if (!initialSmartFrogFile.exists()) {
                throw new BuildException("Not found: " + initialSmartFrogFile);
            }
            if (!initialSmartFrogFile.isFile()) {
                throw new BuildException(
                        "Unexpected file type: " + initialSmartFrogFile);
            }
        }

        this.initialSmartFrogFile = initialSmartFrogFile;
    }


    /**
     * get the current host
     *
     * @return
     */
    protected String getHost() {
        if (host==null) return "";
        return host;
    }


    /**
     * Name an ini file can contain data that overrides the basic settings.
     *
     * @param iniFile
     */
    public void setIniFile(File iniFile) {
        this.iniFile = iniFile;
    }


    /**
     * should the runtime log stack traces?
     *
     * @param logStackTraces
     */
    public void setLogStackTraces(boolean logStackTraces) {
        this.logStackTraces = new Boolean(logStackTraces);
    }

    /**
     * set a reference to the security types
     * @param securityRef
     */
    public void setSecurityRef(Reference securityRef) {
       securityHolder.setSecurityRef(securityRef);
    }

    /**
     * set a security definition
     * @param security
     */
    public void addSecurity(Security security) {
        securityHolder.addSecurity(security);
    }

    /**
     * get the base java task with common config options
     *
     * @return a java task set up with forking, the entry point set to SFSystem, timeout maybe enabled
     */
    protected Java getBaseJavaTask() {
        Java java = createJavaTask("org.smartfrog.SFSystem", getTaskTitle());
        java.setFork(true);
        java.setDir(getProject().getBaseDir());
        return java;
    }

    /**
     * get the title string used to name a task
     *
     * @return the name of the task
     */
    protected abstract String getTaskTitle();

    /**
     * add an argument to the java process
     * @param value
     */
    protected void addArg(String value) {
        smartfrog.createArg().setValue(value);
    }

    /**
     * set a flag to tell the runtime to exit after actioning something
     */
    protected void addExitFlag() {
        addArg("-e");
    }

    /**
     * sets the fail on error flag. Once this is done
     * you cannot spawn the process any more.
     */
    protected void enableFailOnError() {
        setFailOnError(true);
    }

    /**
     * set the fail on error flag.
     *
     * @param failOnError
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;

    }


    /**
     * assertions to enable in this program
     *
     * @param asserts assertion set
     */
    public void addAssertions(Assertions asserts) {
        smartfrog.addAssertions(asserts);
    }

    /**
     * adds the ini file by setting the appropriate system property
     */
    protected void addIniFile() {
        if (iniFile != null && iniFile.exists()) {
            addSmartfrogProperty("org.smartfrog.iniFile", iniFile.toString());
        }
    }

    /**
     * set various standard properties if they are set in the task
     */
    protected void setStandardSmartfrogProperties() {
        addSmartfrogPropertyIfDefined("org.smartfrog.logger.logStackTrace",
                logStackTraces);
        addSmartfrogPropertyIfDefined(
                "org.smartfrog.ProcessCompound.sfRootLocatorPort",
                port);
        addSmartfrogPropertyIfDefined(
                "org.smartfrog.ProcessCompound.sfLivenessDelay",
                livenessCheckPeriod);
        addSmartfrogPropertyIfDefined(
                "org.smartfrog.ProcessCompound.sfLivenessFactor",
                livenessCheckRetries);
        addSmartfrogPropertyIfDefined(
                "org.smartfrog.ProcessCompound.sfProcessAllow",
                allowSpawning);
        addSmartfrogPropertyIfDefined(
                "org.smartfrog.ProcessCompound.sfProcessTimeout",
                spawnTimeout);
        addSmartfrogPropertyIfDefined("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault",
                initialSmartFrogFile);
    }




    /**
     * add a command
     *
     * @param command
     * @param name
     * @throws BuildException if there is no app name
     */
    protected void addApplicationCommand(String command, String name) {
        verifyApplicationName(name);
        addArg(command);
        addArg(name);
    }

    /**
     * verify the app name is valid by whatever logic we have
     * current asserts that it is non null
     *
     * @param application
     * @throws BuildException when unhappy
     */
    protected void verifyApplicationName(String application)
            throws BuildException {
        if (application == null || application.length() == 0) {
            throw new BuildException("Missing application name");
        }
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
     * @since Ant 1.6
     */
    public void addSyspropertyset(PropertySet sysp) {
        smartfrog.addSyspropertyset(sysp);
    }

    /**
     * set a sys property on the smartfrog JVM
     *
     * @param name
     * @param value
     */
    public void addSmartfrogProperty(String name, String value) {
        Environment.Variable property = new Environment.Variable();
        property.setKey(name);
        property.setValue(value);
        smartfrog.addSysproperty(property);
    }


    /**
     * set a sys property on the smartfrog JVM if the object used to set the property value is defined
     *
     * @param name
     * @param object: if defined the toString() value is used
     */
    public void addSmartfrogPropertyIfDefined(String name, Object object) {
        if (object != null) {
            addSmartfrogProperty(name, object.toString());
        }
    }


    /**
     * run smartfrog and throw an exception if something went awry.
     * failure texts are for when smartfrog ran and failed; errorTexts when
     * smartfrog wouldnt run.
     *
     * @param failureText text when return value==-1
     * @param errorText   text when return value!=0 && !=1-
     * @throws BuildException if the return value from java!=0
     */
    protected void execSmartfrog(String failureText, String errorText) {
        //adopt the classpath
        setupClasspath(smartfrog);
        //last minute fixup of error properties.
        //this is because pre Ant1.7, even setting this to false stops spawn working
        //delayed setting only when the flag is true reduces the need to flip the bit
        if (failOnError) {
            smartfrog.setFailonerror(failOnError);
        }
        int err = smartfrog.executeJava();
        if (!failOnError) {
            return;
        }
        switch (err) {
            case 0:
                return;
                //-1 is an expected error, but
                //for some reason smartfrog on HP-UX returns something else.
                //so we catch 255 as well.
            case -1:
            case 255:
                throw new BuildException(failureText);
            default:
                throw new BuildException(errorText + " - error code " + err);
        }
    }

    /**
     * simpler entry point with a set message on errors
     *
     * @param failureText text when smartfrog returns '1'
     */
    protected void execSmartfrog(String failureText) {
        execSmartfrog(failureText, "Problems running smartfrog JVM");
    }


    /**
     *  if we ask for localhost, set it to null.
     * This avoids problems with anything that demands that localhost is
     * undefined
     */
    protected void resetHostIfLocal() {

        if ("localhost".equals(host) || "127.0.0.1".equals(host))
        {
            host = null;
            return;
        }

    }
    /**
     * check no host;
     *
     * @throws org.apache.tools.ant.BuildException
     *          if a host is defined
     */
    protected void verifyHostUndefined() {
        if (host != null && host.length() > 0) {
            throw new BuildException("host cannot be set on this task; it is set to "+host);
        }
    }



    /**
     * set the timeout for execution. This is incompatible with spawning.
     * @param timeout
     */
    public void setTimeout(long timeout) {
        if(timeout>0) {
            smartfrog.setTimeout(new Long(timeout));
        } else {
            //no valid timeout; ignore it.
            smartfrog.setTimeout(null);
        }
    }

    /**
     * verify that the host is defined; assert if it is not set
     * @throws org.apache.tools.ant.BuildException
     *          if a host is undefined
     */
    protected void verifyHostDefined() {
        if(getHost()==null) {
            throw new BuildException("host is undefined");
        }
    }


}
