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
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to let ant task derivatives run smartfrog.
 * How it invokes smartfrog is an implementation detail;
 * it may be calling the Java task, it may be calling smartfrog direct.
 * What is not a detail is that the combined classpath of ant+ any classpath parameters must include
 * all the relevant smartfrog JAR files.
 * <p/>
 * Smartfrog can be configured via system properties, an ini file, or the explicit
 * properties of this task. All the attributes of this task that configure SmartFrog
 * (port, liveness, spawning, stack trace) are undefined; whatever defaults are built into
 * SmartFrog apply, not any hard coded in the task. This also permits one to override smartfrog
 * by setting system properties inline, or in a property set.
 * <p/>
 * By default, all tasks are given a timeout of ten minutes; and propagate any
 * failure to execute to the build file. These can be adjusted via the
 * {@link SmartFrogTask#setTimeout(long)} and
 * {@link SmartFrogTask#setFailOnError(boolean)} calls respectively.
 * Note that when spawning, timeout and failonerror attributes are ignored (a verbose level message
 * warns of this). passing them on to the java task would result in a failure.
 */
public abstract class SmartFrogTask extends TaskBase implements SysPropertyAdder {
    /**
     * what is the default timeout for those tasks that have a timeout
     */
    public static final long DEFAULT_TIMEOUT_VALUE = 60 * 10 * 1000L;

    /**
     * option for 'don't timeout'
     */
    public static final long NO_DEFAULT_TIMEOUT = -1L;
    /**
     * spawn flag, false by default. Needs Ant1.7 or later to work.
     */
    private boolean spawn;

    public static final String MESSAGE_SPAWNED_DAEMON = "Spawned SmartFrog daemon started";
    public static final String MESSAGE_IGNORING_FAILONERROR = "ignoring failonerror setting for spawned application";
    public static final String MESSAGE_IGNORING_TIMEOUT = "ignoring timeout setting for spawned application";
    public static final String ERROR_HOST_NOT_SETTABLE = "host cannot be set on this task; it is set to ";
    public static final String ERROR_HOST_UNDEFINED = "host is undefined";
    public static final String LOCALHOST = "localhost";
    public static final String LOCALHOST_IPV6_LONG = "0:0:0:0:0:0:0:1";
    public static final String LOCALHOST_IPV6_SHORT = ":::::::1";

    public static final String ERROR_MISSING_APPLICATION_NAME = "Missing application name";
    public static final String ERROR_UNEXPECTED_FILE_TYPE = "Unexpected file type: ";
    public static final String ERROR_MISSING_INITIAL_SMARTFROG_FILE = "Not found: ";
    public static final String LOCALHOST_IPV4 = "127.0.0.1";
    public static final String EXIT_AFTER_STARTING = "-e";

    protected SmartFrogTask() {

    }

    /**
     * initialisation routine; set up some settings like the java task
     * that we configure as we go
     *
     * @throws BuildException
     */
    public void init() throws BuildException {
        super.init();
        smartfrog = getBaseJavaTask();
        setFailOnError(true);
        setTimeout(getDefaultTimeout());
    }

    /**
     * override point
     * @return default timeout, return 1 number less than 0 for no timeout
     */
    protected long getDefaultTimeout() {
        return DEFAULT_TIMEOUT_VALUE;
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

    protected File securityFile;

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
    protected File initialSmartFrogFile = null;


    /**
     * flag to set clear failonerror handling
     */
    protected boolean failOnError;

    /**
     * our security holder
     */
    private SecurityHolder securityHolder = new SecurityHolder();

    /**
     * timeout
     */
    private long timeout=0;


    /**
     * add a file to the list
     *
     * @param filename file to add
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
     * add a property file to the JVM
     * @param propFile property file
     */
    public void addConfiguredPropertyFile(PropertyFile propFile) {
        propFile.addPropertiesToJvm(this);
    }

    /**
     * set the hostname to deploy to (optional, defaults to localhost)
     * Some tasks do not allow this to be set at all.
     *
     * @param host hostname to deploy to
     */
    public void setHost(String host) {
        log("setting host to " + host, Project.MSG_DEBUG);
        this.host = host;
    }

    /**
     * port of daemon; optional -default is 3800
     * Some tasks do not allow this to be set at all.
     *
     * @param port port to talk on
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * liveness check in seconds; optional
     *
     * @param livenessCheckPeriod how often to check the children's health
     */
    public void setLivenessCheckPeriod(Integer livenessCheckPeriod) {
        this.livenessCheckPeriod = livenessCheckPeriod;
    }

    /**
     * retry count for liveness checks.
     *
     * @param livenessCheckRetries how many retries before giving up
     */
    public void setLivenessCheckRetries(Integer livenessCheckRetries) {
        this.livenessCheckRetries = livenessCheckRetries;
    }

    /**
     * can this process spawn new processes?
     *
     * @param allowSpawning can the daemon start new processes?
     */
    public void setAllowSpawning(boolean allowSpawning) {
        this.allowSpawning = Boolean.valueOf(allowSpawning);
    }

    /**
     * when to assume a spawn failed -in seconds.
     *
     * @param spawnTimeout timeout in seconds, or null
     */
    public void setSpawnTimeout(Integer spawnTimeout) {
        this.spawnTimeout = spawnTimeout;
    }


    /**
     * the name of a smartfrog file to load on startupe
     *
     * @param initialSmartFrogFile smartfrog file to load on startup
     */
    public void setInitialSmartFrogFile(File initialSmartFrogFile) {
        if (initialSmartFrogFile != null && initialSmartFrogFile.length() > 0) {
            if (!initialSmartFrogFile.exists()) {
                throw new BuildException(ERROR_MISSING_INITIAL_SMARTFROG_FILE + initialSmartFrogFile);
            }
            if (!initialSmartFrogFile.isFile()) {
                throw new BuildException(ERROR_UNEXPECTED_FILE_TYPE + initialSmartFrogFile);
            }
        }

        this.initialSmartFrogFile = initialSmartFrogFile;
    }


    /**
     * get the current host
     *
     * @return the current host value or "" for none defined
     */
    protected String getHost() {
        if (host == null) return "";
        return host;
    }


    /**
     * Name an ini file can contain data that overrides the basic settings.
     *
     * @param iniFile a file for preloading
     */
    public void setIniFile(File iniFile) {
        this.iniFile = iniFile;
    }


    /**
     * should the runtime log stack traces?
     *
     * @param logStackTraces should stack traces be logged?
     */
    public void setLogStackTraces(boolean logStackTraces) {
        this.logStackTraces = Boolean.valueOf(logStackTraces);
    }

    /**
     * set a reference to the security types
     *
     * @param securityRef security data
     */
    public void setSecurityRef(Reference securityRef) {
        securityHolder.setSecurityRef(securityRef);
    }

    /**
     * set a security definition
     *
     * @param security security element
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
        Java java = createJavaTask(getEntrypoint());
        java.setFork(true);
        java.setDir(getProject().getBaseDir());

        return java;
    }

    /**
     * Override point: declare the name of the entry point of this task.
     * @see SmartFrogJVMProperties#SMARTFROG_ENTRY_POINT
     * @return name of the class providing a static void Main(String args[]) method
     */
    protected String getEntrypoint() {
        return SmartFrogJVMProperties.SMARTFROG_ENTRY_POINT;
    }

    /**
     * add an argument to the java process
     *
     * @param value an argument
     */
    protected void addArg(String value) {
        smartfrog.createArg().setValue(value);
    }

    /**
     * set a flag to tell the runtime to exit after actioning something
     */
    protected void addExitFlag() {
        addArg(EXIT_AFTER_STARTING);
    }

    /**
     * sets the fail on error flag. Once this is done
     * you cannot spawn the process any more.
     */
    protected void enableFailOnError() {
        setFailOnError(true);
    }

    /**
     * Set failure policy.
     * (default=true)
     *
     * @param failOnError fail on error flag
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;

    }


    /**
     * assertions to enable in the new JVM.
     *
     * @param asserts assertion set
     */
    public void addAssertions(Assertions asserts) {
        smartfrog.addAssertions(asserts);
    }

    /**
     * adds the ini file by setting the appropriate system property.
     */
    protected void addIniFile() {
        if (iniFile != null && iniFile.exists()) {
            addJVMProperty(SmartFrogJVMProperties.INIFILE, iniFile.toString());
        }
    }

    /**
     * sets the spawn flag.
     * Makes it hard (no, impossible!) to log outputs.
     * only recommended for long-lived tasks, and complicates failonerror and timeout
     * logic
     * @param spawn spawn flag
     */
    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    /**
     * is spawen set
     * @return the spawn flag
     */
    public boolean isSpawn() {
        return spawn;
    }

    /**
     * set various standard properties if they are set in the task
     */
    protected void setStandardSmartfrogProperties() {
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.LOG_STACK_TRACE,
                logStackTraces);
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.ROOT_LOCATOR_PORT,
                port);
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.LIVENESS_DELAY,
                livenessCheckPeriod);
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.LIVENESS_FACTOR,
                livenessCheckRetries);
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.PROCESS_ALLOW,
                allowSpawning);
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.PROCESS_TIMEOUT,
                spawnTimeout);
        addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.SF_DEFAULT,
                initialSmartFrogFile);
    }


    /**
     * add a command
     *
     * @param command application command
     * @param name name
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
     * @param application the application name
     * @throws BuildException when unhappy
     */
    protected void verifyApplicationName(String application)
            throws BuildException {
        if (application == null || application.length() == 0) {
            throw new BuildException(ERROR_MISSING_APPLICATION_NAME);
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
     * @param propset set of properties to add
     */
    public void addSyspropertyset(PropertySet propset) {
        smartfrog.addSyspropertyset(propset);
    }

    /**
     * set a sys property on the smartfrog JVM
     *
     * @param name property name
     * @param value value
     */
    public void addJVMProperty(String name, String value) {
        Environment.Variable property = new Environment.Variable();
        property.setKey(name);
        property.setValue(value);
        addSysproperty(property);
    }


    /**
     * this is a convenience method for things that work
     * with the task -it defines a new JVM arg with the
     * string value.
     *
     * @param argument JVM argument
     */
    public void defineJVMArg(String argument) {
        smartfrog.createJvmarg().setValue(argument);
    }

    /**
     * part of the ANT interface; this method
     * creates a JVM argument for manipulation
     *
     * @return create a nested JVM arg
     */
    public Commandline.Argument createJVMarg() {
        return smartfrog.createJvmarg();
    }

    /**
     * set a sys property on the smartfrog JVM if the object used to set the property value is defined
     * and if it is not already declared in the properties list
     * @param name property name
     * @param object if defined the toString() value is used
     */
    public void addSmartfrogPropertyIfDefined(String name, Object object) {
        if (object != null) {
            addJVMProperty(name, object.toString());
        }
    }


    /**
     * run smartfrog and throw an exception if something went awry.
     * failure texts are for when smartfrog ran and failed; errorTexts when
     * smartfrog wouldnt run.
     *
     * @param failureText text when return value==-1
     * @param errorText   text when return value!=0 && !=1-
     * @return the return code from executing java
     * @throws BuildException if the return value was an error and failonerror!=false
     */
    protected boolean execSmartFrog(String failureText, String errorText) {
        //adopt the classpath
        setupClasspath(smartfrog);
        //last minute fixup of error properties.
        //this is because pre Ant1.7, even setting this to false stops spawn working
        //delayed setting only when the flag is true reduces the need to flip the bit
        propagateSpawnIncompatibleSettings();
        //do any security configurations we need
        securityHolder.applySecuritySettings(this);

        //last minute logging
        if(isDebug()) {
            log("Command: "+getCommandLine());
        }

        //run it
        int err = smartfrog.executeJava();
        if (isSpawn()) {
            //when spawning output gets lost, so we print something here
            log(MESSAGE_SPAWNED_DAEMON);
        }
        //else, let's post-analyse the deployment
        switch (err) {
            case 0:
                //success
                return true;
            case 1:
            case 69:
                if (!failOnError) {
                    return false;
                }

                throw new BuildException(failureText);
            default:
                //any other error code is an odd one
                if (!failOnError) {
                    return false;
                }
                throw new BuildException(errorText + " - error code " + err);
        }

    }

    /**
     * get the command line; return null when the method is absent (ant1.6 and earlier)
     * @return a command line or null for old Ant versions
     */
    protected String getCommandLine() {
        try {
            Method method=Java.class.getMethod("getCommandLine",new Class[0]);
            CommandlineJava commandLine = (CommandlineJava)
                    method.invoke(smartfrog,new Object[0]);
            return commandLine.describeJavaCommand();
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * this code looks at the spawn flag and only sets some properties if spawn
     * is true
     */
    private void propagateSpawnIncompatibleSettings() {
        if (isSpawn()) {
            if(failOnError) {
                setFailOnError(false);
                log(MESSAGE_IGNORING_FAILONERROR,Project.MSG_VERBOSE);
            }
            if(timeout>0) {
                setTimeout(0);
                log(MESSAGE_IGNORING_TIMEOUT,
                        Project.MSG_VERBOSE);
            }
            smartfrog.setSpawn(true);
        } else {
            propagateFailOnError();
            propagateTimeout();
        }
    }

    /**
     * propagate failonerror only if it is true.
     */
    private void propagateFailOnError() {
        if (failOnError) {
            log("Setting JVM timeout to " + timeout, Project.MSG_VERBOSE);
            smartfrog.setFailonerror(failOnError);
        }
    }

    /**
     * simpler entry point with a set message on errors
     *
     * @param failureText text when smartfrog returns '1'
     * @return the result of executing smartfrog
     * @throws BuildException on trouble, especially when failonerror=true
     */
    protected boolean execSmartFrog(String failureText) {
        return execSmartFrog(failureText, "Problems running smartfrog JVM");
    }


    /**
     * if we ask for localhost, set it to null.
     * This avoids problems with anything that demands that localhost is
     * undefined
     */
    protected void resetHostIfLocal() {

        if (LOCALHOST.equals(host) || LOCALHOST_IPV4.equals(host)
        || LOCALHOST_IPV6_LONG.equals(host) || LOCALHOST_IPV6_SHORT.equals(host)) {
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
            throw new BuildException(ERROR_HOST_NOT_SETTABLE + host);
        }
    }

    /**
     * set the timeout for execution. This is incompatible with spawning.
     * @param timeout timeout time
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * propagate the timeout to the Java process. This is incompatible with spawning.
     *
     */
    protected void propagateTimeout() {
        if (timeout > 0) {
            log("Setting JVM timeout to "+timeout,Project.MSG_VERBOSE);
            smartfrog.setTimeout(new Long(timeout));
        } else {
            //no valid timeout; ignore it.
            smartfrog.setTimeout(null);
        }
    }

    /**
     * verify that the host is defined; assert if it is not set
     *
     * @throws org.apache.tools.ant.BuildException
     *          if a host is undefined
     */
    protected void verifyHostDefined() {
        if (getHost() == null) {
            throw new BuildException(ERROR_HOST_UNDEFINED);
        }
    }


    protected void bindToLocalhost() {
        setHost(LOCALHOST);
    }
}
