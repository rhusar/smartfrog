package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.security.SmartFrogCorePropertySecurity;
import org.smartfrog.sfcore.security.SFSecurityProperties;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.SFSystem;

import java.util.*;
import java.rmi.RemoteException;

public class PlainJVMSubprocessStarterImpl implements SubprocessStarter {

    private final LogSF sfLog = LogFactory.sfGetProcessLog();

    /**
     * Does the work of starting up a new process. Looks up sfProcessJava,
     * sfProcessClass and sfProcessTimeout in the process compound to find out
     * which java to use, which class to start up and how long to max. wait
     * for it to appear in the compounds attribute table. Classpath is looked
     * up through standard system property java.class.path. The process is
     * started up with a -D option containing a quoted reference string giving
     * the full name for the new process. sfProcessINI attribute is passed as
     * the -i URL option to the sub-process indicating system properties to
     * use for the new process.
     * Any property prefixed by 'org.smartfrog.sfcore.processcompound.jvm.'+NAME+property=value
     * will be added  only to the subprocess named 'NAME' as a parameter
     * for the JVM. The parameter will be "property+value". @see addProcessDefines
     * Every attribute described by cd.sfProcessConfig will be added to the command line
     * as "-Dorg.smartfrog.processcompound.ATTRIBUTE_NAME=ATTRIBUTE_VALUE"
     *
     * @param name name of new process
     * @param cd   component description with extra process configuration (ex. sfProcessConfig)
     * @return new process
     * @throws Exception failed to locate all attributes, or start process
     */
    public Process startProcess(ProcessCompound parentProcess, String name, ComponentDescription cd) throws Exception {
        Vector runCmd = new Vector();

        addProcessJava(parentProcess, runCmd, cd);
        //addProcessClassName(runCmd,cd);

        addProcessDefines(runCmd, name);

        addProcessClassPath(parentProcess, runCmd, name, cd);
        addProcessSFCodeBase(parentProcess, runCmd, name, cd);

        addProcessEnvVars(runCmd, cd);
        addProcessAttributes(runCmd, name, cd);
        addProcessClassName(parentProcess, runCmd, cd);

        String[] runCmdArray = new String[runCmd.size()];
        runCmd.copyInto(runCmdArray);
        if (sfLog.isTraceEnabled())
            sfLog.trace("startProcess[" + name + "].runCmd: " + runCmd.toString());
        //noinspection CallToRuntimeExec
        return Runtime.getRuntime().exec(runCmdArray);
    }

    /**
     * Gets the process java start command. Looks up the sfProcessJava
     * attribute. sfProcessJava could be a String or a Collection
     *
     * @param cmd cmd to append to
     * @param cd  component description with extra process configuration (ex. sfProcessConfig)
     * @throws Exception failed to construct java command
     */
    protected void addProcessJava(ProcessCompound parentProcess, List cmd, ComponentDescription cd) throws Exception {
        Object processCmd;
        processCmd = cd.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_JAVA, false);
        if (processCmd == null) {
            processCmd = parentProcess.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_JAVA, false);
        }
        if (processCmd instanceof Collection)
            cmd.addAll((Collection) processCmd);
        else
            cmd.add(processCmd);
    }

    /**
     * Get the class name for the subprocess. Looks up the sfProcessClass
     * attribute out of the current target
     *
     * @param cmd command to append to
     * @param cd  component description with extra process configuration (ex. sfProcessClass)
     * @throws Exception failed to construct classname
     */
    protected void addProcessClassName(ProcessCompound parentProcess, List cmd, ComponentDescription cd) throws Exception {
        String pClass = (String) cd.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_CLASS, false);
        if (pClass == null) {
            pClass = (String) parentProcess.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_CLASS);
        }
        cmd.add(pClass);
    }

    /**
     * Constructs sequence of -D statements for the new sub-process by
     * iterating over the current process' properties and looking for those
     * prefixed by org.smartfrog (and not security properties) and creating an
     * entry for each of these. It modifies the sfProcessName property to be
     * that required. If security is on, you also pass some security related
     * properties.
     * System properties are ordered alphabetically before they are processed.
     * Any property prefixed by
     * 'org.smartfrog.sfcore.processcompound.jvm.'+NAME+.property=value
     * will be added  only to the subprocess named 'NAME' as a parameter
     * for the JVM. The parameter will be "value", "property" is only used to name
     * different properties in the initial command line. The property name is
     * important because all sys properties are ordered before they are processed
     * To change the class path in a SubProcess use:
     * 'org.smartfrog.sfcore.processcompound.java.class.path.NAME'
     * Example:
     * org.smartfrog.sfcore.processcompound.jvm.test.propertyname_A=-Xrunpri
     * will add the property '-Xrunpri' to the processCompound named 'test'.
     *
     * @param cmd  command to append to
     * @param name name for subprocess
     * @throws Exception failed to construct defines
     */
    protected void addProcessDefines(List cmd, Object name)
            throws Exception
    {
        Properties props = System.getProperties();
        //Sys properties get ordered
        Vector keysVector = new Vector();
        for (Enumeration keys = props.propertyNames(); keys.hasMoreElements();) {
            keysVector.add(keys.nextElement());
        }
        // Order keys
        keysVector = JarUtil.sort(keysVector);
        //process keys
        for (Enumeration keys = keysVector.elements(); keys.hasMoreElements();) {
            String key = keys.nextElement().toString();
            try {
                if ((key.startsWith(SmartFrogCorePropertySecurity.propBase)) &&
                        (!(key.startsWith(SFSecurityProperties.propBaseSecurity))))
                {
                    //Logger.log("Checking: "+name.toString());
                    //Logger.log("Key: "+key.toString());
                    if (!key.equals(SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_PROCESS_NAME)) {
                        // Special case relsolved in addClassPath

                        //Add special parameters to named subprocesses
                        //@todo add Junit test for this feature
                        //@todo test what happens with special characters
                        // prefixed by 'org.smartfrog.sfcore.processcompound.jvm'+NAME+.property=value
                        String specialParameters = SmartFrogCoreProperty.propBaseSFProcess + "jvm." + name + ".";

                        if (key.startsWith(specialParameters)) {
                            Object value = props.get(key);
                            String keyS = key.substring(specialParameters.length());
                            if (value == null) {
                                value = "";
                            }
                            cmd.add(value.toString());
                        } else {
                            //Properties to overwrite processcompound.sf attributes
                            Object value = props.get(key);
                            cmd.add("-D" + key + "=" + value.toString());
                        }
                    } else {
                        //Special - Add property to name ProcessCompound
                        cmd.add("-D" + (SmartFrogCoreProperty.propBaseSFProcess
                                + SmartFrogCoreKeys.SF_PROCESS_NAME + "=") +
                                name.toString());
                    }
                }
            } catch (Exception ex) {
                sfLog.error(ex);                
            }
        }

        if (SFSecurity.isSecurityOn()) {
            // Pass java.security.policy
            String secProp = props.getProperty("java.security.policy");

            if (secProp != null) {
                cmd.add("-Djava.security.policy=" + secProp);
            }

            // org.smartfrog.sfcore.security.propFile
            secProp = props.getProperty(SFSecurityProperties.propPropertiesFileName);

            if (secProp != null) {
                cmd.add("-D" +
                        SFSecurityProperties.propPropertiesFileName + "=" +
                        secProp);
            }

            //org.smartfrog.sfcore.security.keyStoreName
            secProp = props.getProperty(SFSecurityProperties.propKeyStoreName);

            if (secProp != null) {
                cmd.add("-D" + SFSecurityProperties.propKeyStoreName +
                        "=" + secProp);
            }
        }
    }


    /**
     * Constructs sequence of -D statements for the new sub-process by
     * iterating over the sfProcessConfig ComponentDescription.
     *
     * @param cmd command to append to
     * @param cd  component description with extra process configuration (ex. sfProcessConfig)
     * @throws Exception failed to construct defines
     */
    protected void addProcessAttributes(List cmd, Object name, ComponentDescription cd)
            throws Exception
    {
        ComponentDescription sfProcessAttributes = getProcessAttributes(name, cd);
        Object key;
        Object value;
        for (Iterator it = sfProcessAttributes.sfAttributes(); it.hasNext();) {
            key = it.next().toString();
            value = sfProcessAttributes.sfResolveHere(key);
            cmd.add("-D" +
                    SmartFrogCoreProperty.propBaseSFProcess +
                    key.toString() + "=" +
                    value.toString());
        }
    }

    /**
     * Constructs sequence of -D statements for the new sub-process by
     * iterating over the sfProcessEnvVars ComponentDescription
     *
     * @param cmd command to append to
     * @param cd  component description with extra process configuration (ex. sfProcessConfig)
     * @throws Exception failed to construct defines
     */
    protected void addProcessEnvVars(List cmd, ComponentDescription cd)
            throws Exception
    {
        ComponentDescription sfProcessEnvVars = (ComponentDescription) cd.sfResolveHere(
                SmartFrogCoreKeys.SF_PROCESS_ENV_VARS, false
        );
        if (sfProcessEnvVars == null) return;
        Object key;
        Object value;
        for (Iterator it = sfProcessEnvVars.sfAttributes(); it.hasNext();) {
            key = it.next().toString();
            value = sfProcessEnvVars.sfResolveHere(key);
            cmd.add("-D" + key.toString() + "=" + value.toString());
        }
    }

    /**
     * Resolves sfProcessConfig and adds to it all SystemProperties that
     * start with org.smartfrog.processcompound.PROCESS_NAME
     *
     * @param cd ComponentDescription
     * @return ComponentDescription
     */
    private ComponentDescription getProcessAttributes(Object name, ComponentDescription cd) throws SmartFrogResolutionException {
        ComponentDescription sfProcessAttributes;
        sfProcessAttributes = (ComponentDescription) cd.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_CONFIG, false);
        if (sfProcessAttributes == null)
            sfProcessAttributes = new ComponentDescriptionImpl(null, new ContextImpl(), false);
        ComponentDescriptionImpl.addSystemProperties(SmartFrogCoreProperty.propBaseSFProcess + name, sfProcessAttributes);
        return sfProcessAttributes;
    }

    /**
     * Gets the current class path out of the system properties and returns it
     * as a command line parameter for the subprocess.
     * The class path is created reading one of the following in order selection:
     * <p/>
     * 1.- from a property named sfcore.processcompound.PROCESS_NAME.java.class.path.
     * 2.- attribute java.class.path inside sfProcessAttribute componentDescription
     * <p/>
     * The result if any is added (default) to the system property:  System property java.class.path
     * or replaced if  sfProcessReplaceClassPath=true
     *
     * @param cmd  command to append ro
     * @param name process name
     * @param cd   component description with extra process configuration
     * @throws Exception failed to construct classpath
     */
    //@todo document how new classpath works for subProcesses.
    protected void addProcessClassPath(ProcessCompound parentProcess, List cmd, Object name, ComponentDescription cd) throws Exception {
        String res = null;
        String replaceBoolKey = SmartFrogCoreKeys.SF_PROCESS_REPLACE_CLASSPATH;
        String attributeKey = SmartFrogCoreKeys.SF_PROCESS_CLASSPATH;
        String sysPropertyKey = "java.class.path";
        String pathSeparator = SFSystem.getProperty("path.separator", ";");

        res = addProcessSpecialSystemVar(parentProcess, cd, res, replaceBoolKey, attributeKey, sysPropertyKey, pathSeparator);

        if (res != null) {
            cmd.add("-classpath");
            cmd.add(res);
        }
    }

    /**
     * Gets the current org.smartfrog.codebase out of the system properties and returns it
     * as a command line parameter for the subprocess.
     * The class path is created reading one of the following in order selection:
     * <p/>
     * 1.- from a property named sfcore.processcompound.PROCESS_NAME.'org.smartfrog.codebase'.
     * 2.- attribute 'org.smartfrog.codebase' inside sfProcessAttribute componentDescription
     * <p/>
     * The result if any is added (default) to the system property:  System property 'org.smartfrog.codebase'
     * or replaced if  sfProcessReplaceCodeBase=true
     *
     * @param cmd  command to append ro
     * @param name process name
     * @param cd   component description with extra process configuration
     * @throws Exception failed to construct classpath
     */
    //@todo document how new classpath works for subProcesses.
    protected void addProcessSFCodeBase(ProcessCompound parentProcess, List cmd, Object name, ComponentDescription cd) throws Exception {
        String res = null;
        String replaceBoolKey = SmartFrogCoreKeys.SF_PROCESS_REPLACE_SF_CODEBASE;
        String attributeKey = SmartFrogCoreKeys.SF_PROCESS_SF_CODEBASE;
        String sysPropertyKey = "org.smartfrog.codebase";
        String pathSeparator = " ";

        res = addProcessSpecialSystemVar(parentProcess, cd, res, replaceBoolKey, attributeKey, sysPropertyKey, pathSeparator);

        if (res != null) {
            cmd.add("-D" + sysPropertyKey + "=" + res);
        }
    }


    private String addProcessSpecialSystemVar(ProcessCompound parentProcess,
                                              ComponentDescription cd,
                                              String res,
                                              String replaceBoolKey,
                                              String attributeKey,
                                              String sysPropertyKey, String pathSeparator) throws
            SmartFrogResolutionException, RemoteException
    {
        Boolean replace;
        // Should we replace or overwrite?
        replace = ((Boolean) cd.sfResolveHere(replaceBoolKey, false));
        if (replace == null) {
            replace = ((Boolean) parentProcess.sfResolveHere(replaceBoolKey, false));
        }
        //by default add, not replace
        if (replace == null) replace = Boolean.valueOf(false);

        //Deployed description. This only happens during the first deployment of a SubProcess.
        String cdClasspath = (String) cd.sfResolveHere(attributeKey, false);
        //This will read the system property for org.smartfrog.sfcore.processcompound.NAME.sfProcessClassPath;
        String envPcClasspath = SFSystem.getProperty(SmartFrogCoreProperty.propBaseSFProcess
                + SmartFrogCoreKeys.SF_PROCESS_NAME
                + attributeKey, null);

        //General description for process compound
        String pcClasspath = (String) parentProcess.sfResolveHere(attributeKey, false);
        //Takes previous process classpath (rootProcessClassPath)
        String sysClasspath = SFSystem.getProperty(sysPropertyKey, null);

        if (replace.booleanValue()) {
            if (cdClasspath != null) {
                res = cdClasspath;
            } else if (envPcClasspath != null) {
                res = envPcClasspath;
            } else if (pcClasspath != null) {
                res = pcClasspath;
            } else if (sysClasspath != null) {
                res = sysClasspath;
            }
        } else {            
            res = "";
            if (cdClasspath != null) {
                res += cdClasspath;
                if (!res.endsWith(pathSeparator)) {
                    res += pathSeparator;
                }
            }
            if (envPcClasspath != null) {
                res += envPcClasspath;
                if (!res.endsWith(pathSeparator)) {
                    res += pathSeparator;
                }
            }
            if (pcClasspath != null) {
                res += pcClasspath;
                if (!res.endsWith(pathSeparator)) {
                    res += pathSeparator;
                }
            }
            if (sysClasspath != null) {
                res += sysClasspath;
                if (!res.endsWith(pathSeparator)) {
                    res += pathSeparator;
                }
            }
        }
        return res;
    }

}
