package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public abstract class AbstractSubprocessStarter implements SubprocessStarter {
    private final Log sfLog = LogFactory.sfGetProcessLog();
    
    /**
     * Gets the process java start command. Looks up the sfProcessJava
     * attribute. sfProcessJava could be a String or a Collection
     *
     * @param cmd cmd to append to
     * @param cd  component description with extra process configuration (ex. sfProcessConfig)
     * @throws Exception failed to construct java command
     */
    private void addProcessJava(ProcessCompound parentProcess, List cmd, ComponentDescription cd) throws Exception {
        Object processCmd;
        processCmd = cd.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_JAVA, false);
        if (processCmd == null) {
            processCmd = parentProcess.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_JAVA, true);
        }
        if (processCmd instanceof Collection)
            cmd.addAll((Collection) processCmd);
        else
            cmd.add(processCmd);
    }

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
    public final Process startProcess(ProcessCompound parentProcess, String name, ComponentDescription cd) throws Exception {
        Vector runCmd = new Vector();

        addProcessJava(parentProcess, runCmd, cd);
        addParameters(parentProcess, runCmd, name, cd);

        String[] runCmdArray = new String[runCmd.size()];
        runCmd.copyInto(runCmdArray);
        if (sfLog.isDebugEnabled())
            sfLog.trace("startProcess[" + name + "].runCmd: " + runCmd.toString());

        Process subprocess;

        //noinspection CallToRuntimeExec
        subprocess = Runtime.getRuntime().exec(runCmdArray);

        if (subprocess != null)
            startStreamGobblerThreads(subprocess, name);
        
        doPostStartupSteps();
        return subprocess;
    }

    protected abstract void doPostStartupSteps() throws Exception;

    
    protected abstract void addParameters
            (ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd)
            throws Exception;


    private synchronized void startStreamGobblerThreads(Process process, Object name) {
        // Two gobblers will redirect the System.out and System.err to
        // the System.out of the any error message.
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "err");
        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "out");

        // kick them off
        errorGobbler.setName(name + ".errorGobbler");
        outputGobbler.setName(name + ".outputGobbler");
        errorGobbler.start();
        outputGobbler.start();

        // We forget about those threads, but it's fine because they stop by themselves
        // when the subprocess is killed, and get garbage collected.
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
}
