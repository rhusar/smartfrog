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

package org.smartfrog.sfcore.processcompound;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.HashSet;
import java.util.Set;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.security.SFSecurityProperties;



/**
 * Implements deploymeent behaviour for a process. There is a single process
 * compound allowed per process. SFSystem asks SFProcess to make the
 * processcompound on startup. SFProcess also holds the logic for making a
 * process compound a root for a host. A single root process compound (defined
 * as owning a particular port) is allowed per host. Every processcompound
 * tries to locate its parent on deployment, if there is none, it tries to
 * become the root of the host.
 *
 * <p>
 * Through the deployer class used for primitives "PrimProcessDeployerImpl" the
 * registration of components on deployment is guaranteed. A component being
 * registered only means that the component is known to the process compound
 * and will receive liveness from it. When the process compound is asked to
 * terminate (ie. asked to terminate the process) all components are
 * terminated.
 * </p>
 *
 * <p>
 * You do not need to instantiate this class in order to get new processes. In
 * your component description that you want to deploy, simply define
 * sfProcessName with a string name of the processname that you want to deploy
 * your component in. If the process does not exist, and processes are allowed
 * by the root compound on the host, the process will be created and your
 * component deployed.
 * </p>
 *
 */
public class ProcessCompoundImpl extends CompoundImpl implements ProcessCompound,
    MessageKeys {

    /** A number used to generate a unique ID for registration */
    public  static long registrationNumber = 0;

    /** Name of this processcompound. If one is present it is a subprocess. */
    protected String sfProcessName = null;

    /**
     * Whether the process is a Root (whether is starts and registers with a
     * registry). Default is not. set using property
     * org.smartfrog.sfcore.processcompound.sfProcessName="rootProcess"
     */
    protected boolean sfIsRoot = false;

    /**
     * Whether the ProcessCompound should cause the JVM to exit on termination.
     * By default set to true.
     */
    protected boolean systemExit = true;

    /**
     * On liveness check on a process compound checks if it has any components
     * registered. If not, the process compound \"garbage collects\" itself
     * (causing exit!). Since root process compound does not receive liveness
     * it will never do this. The GC is controlled by an attribute in
     * processcompound.sf "sfSubprocessGCTimeout" which indicates the number
     * of pings for which it should be consecutively with no components for
     * the process to be GCed. If this is set to 0, the GC is disabled.
     */
    protected int gcTimeout = -1;
    /** The countdown to check the gcTimeout. */
    protected int countdown = 0;


    /**
     * A set that contains the names of the sub-processes that have
     * been requested, but not yet ready
     */
    protected Set processLocks = new HashSet();;




    public ProcessCompoundImpl() throws RemoteException {
    }

    /**
     * Test whether the Process Compound is the root process compound or not.
     *
     * @return true if it is the root
     *
     * @throws RemoteException In case of network/rmi error
     */
    public boolean sfIsRoot() throws RemoteException {
        return sfIsRoot;
    }

    /**
     * Parent process compoound can not add me to the attribute list
     * since he did not create me. Uses sfRegister with specific name to
     * register with parent compound.
     *
     * @param parent parent process compound to register with
     *
     * @exception SmartFrogException failed to register with parent
     */
    protected void sfRegisterWithParent(ProcessCompound parent)
        throws SmartFrogException {
        if (parent == null) {
            return;
        }

        try {
            parent.sfRegister(sfProcessName, this);
        } catch (RemoteException rex) {
            throw new SmartFrogRuntimeException(MSG_FAILED_TO_CONTACT_PARENT,
                rex, this);
        } catch (SmartFrogException resex) {
            resex.put(SmartFrogCoreKeys.SF_PROCESS_NAME, sfProcessName);
            throw resex;
        }
    }

    /**
     * Locate the parent process compound. If sfParent is already set, it is
     * returned, otherwise the parent is looked up using local host process
     * compound, sitting on port given by sfRootLocatorPort attribute.
     *
     * @return parent process compound or null if root
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of SmartFrog system error
     */
    protected ProcessCompound sfLocateParent()
        throws SmartFrogException, RemoteException {
        ProcessCompound root = null;

        if (sfParent != null) {
            return (ProcessCompound) sfParent;
    }

        if (sfProcessName == null) {
            return null;
        }

        try {
            root = SFProcess.getRootLocator().getRootProcessCompound(null,
                    ((Number) sfResolveId(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT)).intValue());
        } catch (Throwable t){
            throw (SmartFrogRuntimeException)SmartFrogRuntimeException.forward(t);
        }
        return root;
    }

    /**
     * Override standard compound behaviour to register all components that go
     * throug here as a child compound. Sub-components of given description
     * will not go through here, and so will not be registered here. A
     * component is registered through sfRegister. The component can define
     * its name in the process compound through the sfProcessComponentName
     * attribute.
     *
     * @param name name to name deployed component under in context
     * @param parent of deployer component
     * @param cmp compiled component to deploy
     * @param parms parameters for description
     *
     * @return newly deployed component
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     */
    public Prim sfDeployComponentDescription(Object name, Prim parent,
        ComponentDescription cmp, Context parms)
        throws SmartFrogDeploymentException {
        try {
            Prim result;

            if (parent == null) {
                result = super.sfDeployComponentDescription(name, this, cmp, parms);
                // TODO: take care when user calls it
                result.sfDetach();
            } else {
                result = super.sfDeployComponentDescription(name, parent, cmp, parms);
            }

            sfRegister(result.sfResolveId(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME), result);

            return result;
        } catch (Exception sfex){
            if (sfex instanceof SmartFrogDeploymentException){
                throw (SmartFrogDeploymentException)sfex;
            } else {
                throw new SmartFrogDeploymentException(sfex);
            }
        }
    }

    /**
     * Creates itself as the right form of process: root, sub or independant.
     * If sfProcessName is an empty string: independant if               is
     * rootProcess: become the root process anything else    is subprocess and
     * register with parent.
     *
     * @param parent parent prim. always null (and ignored) for this component
     * @param cxt context for deployement
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeployWith(Prim parent, Context cxt)
        throws SmartFrogDeploymentException, RemoteException {
        try {
            // Set context for sfResolves to work when registering as root
            sfContext = cxt;

            // find name for this process. If found, get parent
            sfProcessName = (String) sfResolveId(SmartFrogCoreKeys.SF_PROCESS_NAME);

            if (sfProcessName != null) {
                if (sfProcessName.equals(SmartFrogCoreKeys.SF_ROOT_PROCESS)) {
                    sfIsRoot = true;
                } else {
                    try {
                        sfParent = sfLocateParent();
                    } catch (Throwable t) {
                        throw new SmartFrogDeploymentException(MSG_PARENT_LOCATION_FAILED,
                                t, this, null);

                    }
                }
            }

            // Now go on with normal deployment
            try {
                super.sfDeployWith(sfParent, sfContext);
            } catch (SmartFrogDeploymentException sfex) {
                if (sfProcessName != null) {
                    sfex.put(SmartFrogCoreKeys.SF_PROCESS_NAME, sfProcessName);
                }

                sfex.put("sfDeployWith", "failed");
                throw sfex;
            }

            // super.sfDeployWith should take care of throwables...
            // Set to root if no parent
            if ((sfParent == null) && sfIsRoot) {
                SFProcess.getRootLocator().setRootProcessCompound(this);
            }

            // Register with parent (does nothing if parent in null)
            sfRegisterWithParent((ProcessCompound) sfParent);
        } catch (SmartFrogException sfex){
            throw ((SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfex));
        }
    }


    /**
     * Starts the process compound. In addition to the normal Compound sfStart,
     * it notifes the root process compound (if it is a sub-process) that
     * it is now ready for action by calling sfNotifySubprocessReady.
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
    super.sfStart();

    // the last act is to inform the root process compound that the
    // subprocess is now ready for action - only done if not the root
    try {
        if (!sfIsRoot()) {
        ProcessCompound parent = sfLocateParent();
        if (parent != null) {
            parent.sfNotifySubprocessReady(sfProcessName);
        }
        }
    } catch (RemoteException rex) {
        throw new SmartFrogRuntimeException(MSG_FAILED_TO_CONTACT_PARENT,
                        rex, this);
    }
    }

    /**
     * Process compound sub-component termination policy is currently not to
     * terminate itself (which is default compound behaviour. Component is
     * removed from liveness and attribute table (if present).
     *
     * @param rec termination record
     * @param comp component that terminated
     */
    public void sfTerminatedWith(TerminationRecord rec, Prim comp) {
      try {
        sfRemoveAttribute(sfAttributeKeyFor(comp));
      }
      catch (RemoteException ex) {
        Logger.logQuietly(ex);
      }
      catch (SmartFrogRuntimeException ex) {
        Logger.logQuietly(ex);
      }
    }

    /**
     * Override liveness sending failures to just remove component from table,
     * Does NOT to call termination since a child terminating does not mean
     * that this proces should die. If, however, the process is a sub-process,
     * and the failure is from the parent root process, then the process will
     * carry out normal component failure behaviour.
     *
     * @param source sender that failed
     * @param target target for the failure
     * @param failure The error
     */
    public void sfLivenessFailure(Object source, Object target,
        Throwable failure) {
        if ((source == this) && (sfParent != null) && (target == sfParent)) {
            super.sfLivenessFailure(source, target, failure);
        }

        try {
            sfRemoveAttribute(sfAttributeKeyFor(target));
        } catch (Exception ex) {
            // ignore
        }
    }

    /**
     * Termination call. Could be due to parent failing or management
     * interface. In any case it means terminating all registered components
     * and exiting this process.
     *
     * @param rec termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord rec) {
        super.sfTerminateWith(rec);

        //System.out.println("terminating with " + rec.toString());
        if (systemExit) {
            try {
                String name = SmartFrogCoreKeys.SF_PROCESS_NAME;
                name = sfResolve(SmartFrogCoreKeys.SF_PROCESS_NAME, name, false);
                if (Logger.logStackTrace) {
                    Logger.log(MessageUtil.formatMessage(MSG_SF_DEAD, name)+" "+ new Date(System.currentTimeMillis()));
                } else {
                    Logger.log(MessageUtil.formatMessage(MSG_SF_DEAD, name));
                }
            } catch (Throwable thr){
            }
            System.exit(0);
        }
    }

    /**
     * Sets whether or not the ProcessCompound should terminate the JVM on
     * exit. This is, by default, set to true. It is used if the
     * ProcessCompound is created and managed by other code.
     *
     * @param exit whether or not to exit (true = exit)
     *
     * @throws RemoteException In case of network/rmi error
     */
    public void systemExitOnTermination(boolean exit) throws RemoteException {
        systemExit = exit;
    }

    /**
     * Detach the process compound from its parent. The process compound will
     * try to become root process compound for this host. This might fail if
     * the root locator can not make this process compound root.
     *
     * @exception SmartFrogException failed detaching process compound
     * @throws RemoteException In case of network/rmi error
     */
    public void sfDetach() throws SmartFrogException, RemoteException {
        try {
            super.sfDetach();
            SFProcess.getRootLocator().setRootProcessCompound(this);
        } catch (SmartFrogException sfex) {
            // Add the context
            sfex.put("sfDetachFailure", this.sfContext);

            //TODO: Check if component is to be terminated
        }
    }

    public synchronized void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);

        if (gcTimeout == -1) {
            try {
                gcTimeout = ((Integer) sfResolveHere(SmartFrogCoreKeys.SF_SUBPROCESS_GC_TIMEOUT)).intValue();
            } catch (SmartFrogResolutionException r) {
                gcTimeout = 0;
            }

            //System.out.println("SPGC being initialised - " + gcTimeout);
            countdown = gcTimeout;
        }

        if (gcTimeout > 0) {
            //System.out.println("SPGC lease being checked " + countdown);
            if ((countdown-- < 0) && (sfChildren.size() == 0) &&
                    (sfParent != null)) {
                //System.out.println("SPGC being activated");
                sfTerminate(TerminationRecord.normal(null));
            } else {
                //System.out.println("SPGC lease being reset");
                countdown = gcTimeout;
            }
        } else {
            //System.out.println("SPGC not enabled");
        }
    }

    //
    // ProcessCompound
    //

    /**
     * Returns the processname for this process. Reference is be empty if this
     * compound is the root for the host.
     *
     * @return process name for this process
     */
    public String sfProcessName() {
        return sfProcessName;
    }

    /**
     * Returns the complete name for this component from the root of the
     * application.
     *
     * @return reference of attribute names to this component
     *
     * @throws RemoteException In case of network/rmi error
     */
     //sfCompleteName is cached. @TODO: clean cache when re-parenting
    public Reference sfCompleteName() throws RemoteException {
        if (sfCompleteName==null){
            Reference r;
            r = new Reference();

            String canonicalHostName = SmartFrogCoreKeys.SF_HOST;

            try {
                // read sfHost attribute. Faster that using sfDeployedHost().
                canonicalHostName = ((java.net.InetAddress)sfResolveId(
                    canonicalHostName)).getCanonicalHostName();
            } catch (NullPointerException exSfHost) {
                canonicalHostName = this.sfDeployedHost().getCanonicalHostName();
            }

            if (sfParent==null) {
                r.addElement(ReferencePart.host((canonicalHostName)));

                if (this.sfProcessName()==null) {
                    // Process created when using sfDeployFrom (use by sfStart &
                    //  sfRun)
                    r.addElement(ReferencePart.here(SmartFrogCoreKeys.
                        SF_RUN_PROCESS));
                } else {
                    r.addElement(ReferencePart.here(this.sfProcessName()));
                }
            } else {
                //r = sfParent.sfCompleteName(); // Only if you had a hierarchy
                //of processes.
                r.addElement(ReferencePart.host((canonicalHostName)));

                Object key = sfParent.sfAttributeKeyFor(this);

                if (key!=null) {
                    r.addElement(ReferencePart.here(key));
                }
            }
            sfCompleteName=r;
            System.out.println("completeNameCreated2: "+sfCompleteName.toString());
        }
        return sfCompleteName;
    }

    /**
     * Register a comonent under given name. Exception is thrown if the name is
     * already used. If name is null a name is made up for the component.
     * Consisting of the complete name of the component concatenated with the
     * current time.
     *
     * @param name name for component or null for made up name
     * @param comp component to register
     *
     * @return name of component used
     *
     * @throws SmartFrogException In case of resolution failure
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized Object sfRegister(Object name, Prim comp)
        throws SmartFrogException, RemoteException {
        if ((name != null) && (sfContext.containsKey(name))) {
            throw SmartFrogResolutionException.generic(sfCompleteNameSafe(),
                "Name '" + name + "' already used");
        }

        Object compName = name;

        if (compName == null) {
            // Make up a name for the component first get complete name of
            // component
            // Add a timestamp to the end and convert to string
            compName = SmartFrogCoreKeys.SF_UNNAMED + (new Date()).getTime() + "_" +
                registrationNumber++;
        }

        // Add as attribute
        sfAddAttribute(compName, comp);

        // Add liveness so we know when to unregister
        if (!sfChildren.contains(comp)) {
            sfAddChild(comp);
        }

        return compName;
    }

    /**
     * Tries to find an attribute in the local context. If the attribute is not
     * found the thread will wait for a notification from sfNotifySubprocessReady
     * or until given timeout expires. Used to wait for a new process
     * compound to appear.
     *
     * @param name name of attribute to wait for
     * @param timeout max time to wait in millis
     *
     * @return The object found
     *
     * @throws Exception attribute not found after timeout
     * @throws RemoteException if there is any network or remote error
     */
    public Object sfResolveHereOrWait(Object name, long timeout)
        throws Exception {
        long endTime = (new Date()).getTime() + timeout;

    synchronized (processLocks) {
        while (true) {
        try {
            // try to return the attribute value
            // if name in locks => process not ready, pretend not found...
            if (processLocks.contains(name)) {
            throw SmartFrogResolutionException.notFound(new Reference(name),
                                    sfCompleteNameSafe());
            }
            else
            return sfResolveHere(name);
        } catch (SmartFrogResolutionException ex) {
            // not found, wait for leftover timeout
            long now = (new Date()).getTime();

            if (now >= endTime) {
            throw ex;
            }
            processLocks.add(name);
            processLocks.wait(endTime - now);
        }
        }
    }
    }

    /**
     * Allows a sub-process to notify the root process compound that it is now
     * ready to receive deployment requests.
     *
     * @param name the name of the subprocess
     * @throws RemoteException if there is any network or remote error
     *
     */
    public void sfNotifySubprocessReady(String name)
        throws RemoteException {

        // Notify any waiting threads that an attribute was added
    synchronized (processLocks) {
        processLocks.remove(name);
        processLocks.notifyAll();
    }
    }

    /**
     * Find a process for a given name in the root process compound. If the
     * process is not found it is created.
     *
     * @param name name of process
     *
     * @return ProcessCompound associated with the input name
     *
     * @exception Exception failed to deploy process
     */
    public ProcessCompound sfResolveProcess(Object name) throws Exception {
        ProcessCompound pc = null;

        if (sfParent() == null) { // am the root

            try {
                pc = (ProcessCompound) sfResolve(new Reference(
                            new HereReferencePart(name)));
            } catch (SmartFrogResolutionException e) {
                pc = addNewProcessCompound(name);
            }
        } else { // am a child process - find in the parent
            pc = ((ProcessCompound) sfParent()).sfResolveProcess(name);
        }

        return pc;
    }

    // Internal
    //
    //

    /**
     * Checks is sub-processes are allowed through attribute system property
     * sfProcessAllow and checks that it is the root process compound. Uses
     * startProcess to start the actual sub-process. Then uses
     * sfProcessTimeout to wait for the new process compound to appear in
     * attribute table. If this does not happen the process is killed, and an
     * exception is thrown.
     *
     * @param name name of new compound
     *
     * @return ProcessCompound
     *
     * @exception Exception failed to deploy new naming compound
     */
    protected ProcessCompound addNewProcessCompound(Object name)
        throws Exception {
        // Check if process creation is allowed
        boolean allowProcess;

        Object ap = sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_ALLOW);

        if (ap == null) {
            allowProcess = false;
        } else if (ap instanceof String) {
            allowProcess = Boolean.valueOf((String) ap).booleanValue() &&
                sfIsRoot;
        } else {
            allowProcess = ((Boolean) ap).booleanValue() && sfIsRoot;
        }

        if (!allowProcess) {
            throw SmartFrogResolutionException.generic(sfCompleteName(),
                "Not allowed to create process");
        }

        // Locate timeout
        long timeout = 1000 * ((Number) sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_TIMEOUT)).intValue();

        // Start process
        Process process = startProcess(name);

        if (process != null) {
            // IMPORTANT COMMENT: We loose track of this two threads.
            // May be we should be keep a reference to them and later on
            // clean them nicely.
            // Two gobblers will redirect the System.out and System.err to
            // the System.out of the any error message?
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(),
                    "err");

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(),
                    "out");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();
        }

        try {
            // Wait for new compound to appear and try to return it
            return (ProcessCompound) sfResolveHereOrWait(name, timeout);
        } catch (Exception ex) {
            // failed to find new compound. Destroy process and re-throw
            // exception
            process.destroy();
            throw ex;
        }
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
     *
     * @param name name of new process
     *
     * @return new process
     *
     * @exception Exception failed to locate all attributes, or start process
     */
    protected Process startProcess(Object name) throws Exception {
        Vector runCmd = new Vector();
        addProcessJava(runCmd);
        addProcessClassPath(runCmd);
        addProcessDefines(runCmd, name);
        addProcessClassName(runCmd);

        String[] runCmdArray = new String[runCmd.size()];
        runCmd.copyInto(runCmdArray);

        return Runtime.getRuntime().exec(runCmdArray);
    }

    /**
     * Gets the process java start command. Looks up the sfProcessJava
     * attribute
     *
     * @param cmd cmd to append to
     *
     * @exception Exception failed to construct java command
     */
    protected void addProcessJava(Vector cmd) throws Exception {
        cmd.addElement((String) sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_JAVA));
    }

    /**
     * Get the class name for the subprocess. Looks up the sfProcessClass
     * attribute out of the current target
     *
     * @param cmd command to append to
     *
     * @exception Exception failed to construct classname
     */
    protected void addProcessClassName(Vector cmd) throws Exception {
        cmd.addElement((String) sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_CLASS));
    }

    /**
     * Gets the current class path out of the system properties and returns it
     * as a command line parameter for the subprocess.
     *
     * @param cmd command to append ro
     *
     * @exception Exception failed to construct classpath
     */
    protected void addProcessClassPath(Vector cmd) throws Exception {
        String res = SFSystem.getProperty("java.class.path", null);

        if (res != null) {
            cmd.addElement("-classpath");
            cmd.addElement(res);
        }
    }

    /**
     * Constructs sequence of -D statements for the new sub-process by
     * iterating over the current process' properties and looking for those
     * prefixed by org.smartfrog (and not security properties) and creating an
     * entry for each of these. It modifies the sfProcessName property to be
     * that required. If security is on you also pass some security related
     * properties.
     *
     * @param cmd command to append to
     * @param name name for subprocess
     *
     * @exception Exception failed to construct defines
     */
    protected void addProcessDefines(Vector cmd, Object name)
        throws Exception {
        Properties props = System.getProperties();

        for (Enumeration e = props.keys(); e.hasMoreElements();) {
            String key = e.nextElement().toString();

            if ((key.startsWith(SmartFrogCoreProperty.propBase)) &&
                    (!(key.startsWith(SFSecurityProperties.propBaseSecurity)))) {
                if (!key.equals(SmartFrogCoreProperty.sfProcessName)) {
                    Object value = props.get(key);
                    cmd.addElement("-D" + key.toString() + "=" +
                        value.toString());
                } else {
                    cmd.addElement("-D" +
                        (SmartFrogCoreProperty.sfProcessName+"=") +
                        name.toString());
                }
            }
        }

        if (SFSecurity.isSecurityOn()) {
            // Pass java.security.policy
            String secProp = props.getProperty("java.security.policy");

            if (secProp != null) {
                cmd.addElement("-Djava.security.policy=" + secProp);
            }

            // org.smartfrog.sfcore.security.propFile
            secProp = props.getProperty(SFSecurityProperties.propPropertiesFileName);

            if (secProp != null) {
                cmd.addElement("-D" +
                    SFSecurityProperties.propPropertiesFileName + "=" +
                    secProp);
            }

            //org.smartfrog.sfcore.security.keyStoreName
            secProp = props.getProperty(SFSecurityProperties.propKeyStoreName);

            if (secProp != null) {
                cmd.addElement("-D" + SFSecurityProperties.propKeyStoreName +
                    "=" + secProp);
            }
        }
    }
}
