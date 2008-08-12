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

import org.smartfrog.SFSystem;
import org.smartfrog.services.filesystem.files.FilesImpl;
import org.smartfrog.services.filesystem.files.Fileset;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Diagnostics;
import org.smartfrog.sfcore.common.ExitCodes;
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
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.security.SFSecurityProperties;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.Collections;
import java.io.IOException;
import org.smartfrog.sfcore.logging.LogSF;


/**
 * Implements deployment behaviour for a process. There is a single process
 * compound allowed per process. SFSystem asks SFProcess to make the
 * processcompound on startup. SFProcess also holds the logic for making a
 * process compound a root for a host. A single root process compound (defined
 * as owning a particular port) is allowed per host. Every processcompound tries
 * to locate its parent on deployment, if there is none, it tries to become the
 * root of the host.
 * <p/>
 * <p> Through the deployer class used for primitives "PrimProcessDeployerImpl"
 * the registration of components on deployment is guaranteed. A component being
 * registered only means that the component is known to the process compound and
 * will receive liveness from it. When the process compound is asked to
 * terminate (ie. asked to terminate the process) all components are terminated.
 * </p>
 * <p/>
 * <p> You do not need to instantiate this class in order to get new processes.
 * In your component description that you want to deploy, simply define
 * sfProcessName with a string name of the processname that you want to deploy
 * your component in. If the process does not exist, and processes are allowed
 * by the root compound on the host, the process will be created and your
 * component deployed. </p>
 */
public class ProcessCompoundImpl extends CompoundImpl
        implements ProcessCompound,
        MessageKeys {

    /**
     * A number used to generate a unique ID for registration
     */
    public static long registrationNumber = 0;

    /**
     * Name of this processcompound. If one is present it is a subprocess.
     */
    protected String sfProcessName = null;

    /**
     * Whether the process is a Root (whether is starts and registers with a
     * registry). Default is not. set using property org.smartfrog.sfcore.processcompound.sfProcessName="rootProcess"
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
     * (causing exit!). Since root process compound does not receive liveness it
     * will never do this. The GC is controlled by an attribute in
     * processcompound.sf "sfSubprocessGCTimeout" which indicates the number of
     * pings for which it should be consecutively with no components for the
     * process to be GCed. If this is set to 0, the GC is disabled.
     */
    protected int gcTimeout = -1;
    /**
     * The countdown to check the gcTimeout.
     */
    protected int countdown = 0;


    /**
     * A set that contains the names of the sub-processes that have been
     * requested, but not yet ready
     */
    protected Set<Object> processLocks = new HashSet<Object>();
    public static final String ATTR_PORT = "sfPort";
    private static final String JAVA_SECURITY_POLICY = "java.security.policy";
    

    private ShutdownHandler shutdownHandler = new ShutdownHandler() {
        public void shutdown(ProcessCompound processCompound) {
            ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
        }
    };
    private int nextSubprocessId = 0;
    private SubprocessStarter subprocessStarter = new PlainJVMSubprocessStarterImpl();

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
     * Parent process compoound can not add me to the attribute list since he
     * did not create me. Uses sfRegister with specific name to register with
     * parent compound.
     *
     * @param parent parent process compound to register with
     *
     * @throws SmartFrogException failed to register with parent
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
        ProcessCompound root;

        if (sfParent != null) {
            return (ProcessCompound) sfParent;
        }

        if (sfProcessName == null) {
            return null;
        }

        try {
            root = SFProcess.getRootLocator().getRootProcessCompound(null, ((Number) sfResolveHere(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT, false)).intValue());
        } catch (Throwable t) {
            throw (SmartFrogRuntimeException) SmartFrogRuntimeException.forward( "ProcessCompoundImpl.sfLocateParent()", t);
        }
        return root;
    }

    /**
     * Override standard compound behaviour to register all components that go
     * throug here as a child compound. Sub-components of given description will
     * not go through here, and so will not be registered here. A component is
     * registered through sfRegister. The component can define its name in the
     * process compound through the sfProcessComponentName attribute.
     *
     * @param name   name to name deployed component under in context
     * @param parent of deployer component
     * @param cmp    compiled component to deploy
     * @param parms  parameters for description
     *
     * @return newly deployed component
     *
     * @throws SmartFrogDeploymentException failed to deploy compiled component
     */
    public Prim sfDeployComponentDescription(Object name,
                                             Prim parent,
                                             ComponentDescription cmp,
                                             Context parms)
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

            //          sfRegister(result.sfResolveId(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME), result);

            return result;
        } catch (Exception sfex){
            throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfex);
        }
    }

    /**
     * Creates itself as the right form of process: root, sub or independant. If
     * sfProcessName is an empty string: independant if               is
     * rootProcess: become the root process anything else    is subprocess and
     * register with parent.
     *
     * @param parent parent prim. always null (and ignored) for this component
     * @param cxt    context for deployement
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while deploying
     * the component
     */
    public synchronized void sfDeployWith(Prim parent, Context cxt)
            throws SmartFrogDeploymentException, RemoteException {
        try {
            // Set context for sfResolves to work when registering as root
            sfContext = cxt;

            // find name for this process. If found, get parent
            sfProcessName = (String) sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_NAME,false);

            if (sfProcessName != null) {
                if (sfProcessName.equals(SmartFrogCoreKeys.SF_ROOT_PROCESS)) {
                    sfIsRoot = true;
                } else {
                    try {
                        sfParent = sfLocateParent();
                    } catch (Throwable t) {
                        throw new SmartFrogDeploymentException(MSG_PARENT_LOCATION_FAILED, t, this, null);

                    }
                }
            }
            //Clean any cached sfcompletename
            sfParentageChanged();
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
     * Exports this  component using portObj. portObj can be a port or a vector
     * containing a set of valid ports. If a vector is used the component tries
     * to see if the port used by the local ProcessCompound is in the vector set
     * and use that if so. If not tries to use the first one avaible
     *
     * @param portObj Object
     *
     * @return Object Reference to exported object
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while exporting
     * the component
     */
    protected Object sfExport(Object portObj)
            throws RemoteException, SmartFrogException {
        Object exportRef = null;
        int port = 0; //default value
        if ((portObj != null)) {
            if (portObj instanceof Integer) {
                port = ((Integer) portObj).intValue();
                exportRef = sfExportRef(port);
                sfAddAttribute(ATTR_PORT, new Integer(port));
            } else if (portObj instanceof Vector) {
                //if not in range use vector and try
                int size = ((Vector) (portObj)).size();
                for (int i = 0; i < size; i++) {
                    //get
                    try {
                        port = ((Integer) ((Vector) (portObj)).elementAt(i)).intValue();
                        exportRef = sfExportRef(port);
                        sfAddAttribute(ATTR_PORT, new Integer(port));
                        break;
                    } catch (SmartFrogException ex) {
                        if (i >= size - 1) {
                            throw ex;
                        }
                    }
                } //for
            }
        } else {
            exportRef = sfExportRef(port);
        }
        return exportRef;
    }


    /**
     * {@inheritDoc}
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     * registering the component
     */
    protected void registerWithProcessCompound()
            throws RemoteException, SmartFrogException {
        //This is a ProcessCompound. Don't need to register
    }

    /**
     * Starts the process compound. In addition to the normal Compound sfStart,
     * it notifes the root process compound (if it is a sub-process) that it is
     * now ready for action by calling sfNotifySubprocessReady.
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
        RemoteException {
        super.sfStart();
        //Set itself as single instance of process compound for this process
        try {
            SFProcess.setProcessCompound(this);
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }


        // This call and method will disapear once we refactor ProcessCompound
        // SFProcess.addDefaultProcessDescriptions will replace all this code.
        // @TODO fix after refactoring ProcessCompound.
        deployDefaultProcessDescriptions();

        // Add diagnostics report
        if (Logger.processCompoundDiagReport) {
            sfAddAttribute(SmartFrogCoreKeys.SF_DIAGNOSTICS_REPORT, sfDiagnosticsReport());
        }
        if (sfLog().isDebugEnabled() && Logger.logStackTrace) {
            StringBuffer report = new StringBuffer();
            Diagnostics.doShortReport(report, (Prim) null);
            sfLog().debug(report);
        } else if (sfLog().isTraceEnabled()) {
            sfLog().trace(sfDiagnosticsReport());
        }
        // Add boot time only in rootProcess
        if (sfIsRoot) {
            sfAddAttribute(SmartFrogCoreKeys.SF_BOOT_DATE, new Date(System.currentTimeMillis()));
        }
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
            throw new SmartFrogRuntimeException(MSG_FAILED_TO_CONTACT_PARENT, rex, this);
        }
        if (sfLog().isDebugEnabled()) sfLog().debug("ProcessCompound '"+sfProcessName+"' started.");
    }

    /**
     * @throws RemoteException In case of Remote/nework error
     * @throws SmartFrogException if fail deployment
     */
    private void deployDefaultProcessDescriptions() throws SmartFrogException, RemoteException {
        //Get a clone to protect possible concurrency access to it
        Properties props = (Properties)System.getProperties().clone();
        Context nameContext = null;
        String name ;
        String url;
        String key = null;
        try {
            for (Object o : props.keySet()) {
                key = o.toString();
                if (key.startsWith(SmartFrogCoreProperty.defaultDescPropBase)) {
                    // Collects all properties refering to default descriptions that
                    // have to be deployed inmediately after process compound
                    // is started.
                    url = (String) props.get(key);
                    name = key.substring(SmartFrogCoreProperty.defaultDescPropBase.length());
                    ComponentDescription cd = ComponentDescriptionImpl.sfComponentDescription(url.trim());
                    final Object givenName = cd.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false);
                    if (givenName != null) name = null;
                    sfCreateNewApp(name, cd, nameContext);
                }
            }
        } catch (SmartFrogDeploymentException ex) {
            throw ex;
        } catch (SmartFrogException sfex) {
            throw new SmartFrogDeploymentException("deploying default description for '" + key + '\'', sfex, this, nameContext);
        }
    }


    /**
     * Process compound sub-component termination policy is currently not to
     * terminate itself (which is default compound behaviour. Component is
     * removed from liveness and attribute table (if present).
     *
     * @param rec  termination record
     * @param comp component that terminated
     */
    public void sfTerminatedWith(TerminationRecord rec, Prim comp) {
        try {
            sfRemoveAttribute(sfAttributeKeyFor(comp));
        }
        catch (RemoteException ex) {
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }
        catch (SmartFrogRuntimeException ex) {
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }
    }

    /**
     * Override liveness sending failures to just remove component from table,
     * Does NOT to call termination since a child terminating does not mean that
     * this proces should die. If, however, the process is a sub-process, and
     * the failure is from the parent root process, then the process will carry
     * out normal component failure behaviour.
     *
     * @param source  sender that failed
     * @param target  target for the failure
     * @param failure The error
     */
    public void sfLivenessFailure(Object source, Object target, Throwable failure) {
        if ((source == this) && (sfParent != null) && (target == sfParent)) {
            super.sfLivenessFailure(source, target, failure);
        }

        try {
            sfRemoveAttribute(sfAttributeKeyFor(target));
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }
    }

    /**
     * Termination call. Could be due to parent failing or management interface.
     * In any case it means terminating all registered components and exiting
     * this process.
     *
     * @param rec termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord rec) {
        super.sfTerminateWith(rec);
        if (sfIsRoot) {
            try {
                SFProcess.getRootLocator().unbindRootProcessCompound();
                // TODO: Move to root locator, as this is RMI-specific
                shutdownRMIRegistry(sfLog());
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
            }
        }

        if (sfLog().isDebugEnabled())
            sfLog().debug("ProcessCompoundImpl terminating. systemExit = " + systemExit);

        if (systemExit) {
            try {
                String name = SmartFrogCoreKeys.SF_PROCESS_NAME;
                name = sfResolve(SmartFrogCoreKeys.SF_PROCESS_NAME, name, false);
                sfLog().out(
                        MessageUtil.formatMessage(MSG_SF_DEAD, name) + " " + new Date()
                );
            } catch (Throwable thr) {
                sfLog().ignore("When exiting",thr);
            }

            //@OSGI TO review 
			SFSystem.cleanShutdown();

            ExitCodes.exitWithError(ExitCodes.EXIT_CODE_SUCCESS);
        }
    }

    /**
     * Iterates over children telling each of them to terminate quietly with
     * given status. It iterates from the last one created to the first one.
     *
     * @param status status to terminate with
     */
    protected void sfSyncTerminateWith(TerminationRecord status) {
        // Terminate legitimate children except subProc
        for (Prim child : sfReverseChildren()) {
            try {
                if ((!(child instanceof ProcessCompound))
                        && (child.sfParent() == null)) {
                    //Logger.log("SynchTerminate sent to legitimate: "+ child.sfCompleteName());
                    (child).sfTerminateQuietlyWith(status);
                }
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
                // ignore
            }
        }
        // Terminate illegitimate children except subProc
        for (Prim child : sfReverseChildren()) {
            try {
                if ((!(child instanceof ProcessCompound))) {
                    //Logger.log("SynchTerminate sent to illegitimate: "+ child.sfCompleteName());
                    //Full termination notifying its parent
                    (child).sfTerminate(status);
                }
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
                // ignore
            }
        }
        // Terminate subprocesses
        for (Prim child : sfReverseChildren()) {
            try {
                //Logger.log("SynchTerminate sent to : "+ child.sfCompleteName());
                (child).sfTerminateQuietlyWith(status);
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
                // ignore
            }
        }
    }

    /**
     * Terminate children asynchronously using a separate thread for each call.
     * It iterates from the last one created to the first one.
     *
     * @param status status to terminate with
     */
    protected void sfASyncTerminateWith(TerminationRecord status) {
        // Terminate legitimate children except subProc

        for (Prim child : sfReverseChildren()) {
            try {
                if ((!(child instanceof ProcessCompound)) && (child.sfParent() == null)) {
                    //Logger.log("ASynchTerminate sent to legitimate: "+ child.sfCompleteName());
                    (new TerminatorThread(child, status).quietly()).start();
                }
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
                // ignore
            }
        }
        // Terminate illegitimate children except subProc
        for (Prim child : sfReverseChildren()) {
            try {
                if ((!(child instanceof ProcessCompound))) {
                    //Full termination notifying its parent
                    //Logger.log("ASynchTerminate sent to (illegitimate): "+ child.sfCompleteName());
                    (new TerminatorThread(child, status)).start();
                }
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
                // ignore
            }
        }
        // Terminate subprocesses
        for (Prim child : sfReverseChildren()) {
            try {
                //Logger.log("ASynchTerminate sent to: "+ child.sfCompleteName());
                (new TerminatorThread(child, status).quietly()).start();
            } catch (Exception ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
                // ignore
            }
        }

    }


    /**
     * Sets whether or not the ProcessCompound should terminate the JVM on exit.
     * This is, by default, set to true. It is used if the ProcessCompound is
     * created and managed by other code.
     *
     * @param exit whether or not to exit (true = exit)
     *
     * @throws RemoteException In case of network/rmi error
     */
    public void systemExitOnTermination(boolean exit) throws RemoteException {
        systemExit = exit;
    }

    public void replaceShutdownHandler(ShutdownHandler handler) {
        shutdownHandler = handler;
    }

    public void replaceSubprocessStarter(SubprocessStarter starter) {
        subprocessStarter = starter;
    }

    /**
     * Detach the process compound from its parent. The process compound will
     * try to become root process compound for this host. This might fail if the
     * root locator can not make this process compound root.
     *
     * @throws SmartFrogException failed detaching process compound
     * @throws RemoteException In case of network/rmi error
     */
    public void sfDetach() throws SmartFrogException, RemoteException {
        try {
            super.sfDetach();
            SFProcess.getRootLocator().setRootProcessCompound(this);
        } catch (SmartFrogException sfex) {
            // Add the context
            sfex.put("sfDetachFailure", sfContext);
            throw sfex;
        }
    }


    /**
     * {@inheritDoc}
     * @param source caller
     * @throws SmartFrogLivenessException liveness failure
     * @throws RemoteException In case of network/rmi error
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);

        if (source == null) {
            return;
        }

        if (!source.equals(sfLivenessSender)) {
            return;
        }
        // only check for subprocess GC if checking self
        if (gcTimeout == -1) {
            try {
                gcTimeout = ((Integer) sfResolveHere(SmartFrogCoreKeys.SF_SUBPROCESS_GC_TIMEOUT)).intValue();
            } catch (SmartFrogResolutionException ignored) {
                gcTimeout = 0;
            }

            if (sfLog().isTraceEnabled()) sfLog().debug ("SubProcessGC being initialised - " + gcTimeout);
            countdown = gcTimeout;
        }

        if (gcTimeout > 0) {
            if (sfLog().isTraceEnabled()) sfLog().trace ("SPGC lease being checked for " + this.sfCompleteNameSafe() + " - " + countdown);
            if ((countdown-- >= 0) && (sfChildList().size() == 0) && (sfParent != null)) {
                //Finished countdown
                if (countdown <= 0) {
                    if (sfLog().isDebugEnabled()) sfLog().debug ("SubProcessGC being activated");
                    sfTerminate(TerminationRecord.normal ("SubProcessGC self activated for "+ this.sfCompleteNameSafe(), this.sfCompleteNameSafe() , null));
                }
            } else {
                if (sfLog().isTraceEnabled()) sfLog().trace ("SubProcessGC lease being reset " + this.sfCompleteNameSafe() + " source "+ source );
                countdown = gcTimeout;
            }
        } else {
            // only send warn when debug enabled.
            if (sfLog().isDebugEnabled()) sfLog().warn("SubProcessGC not enabled");
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
     * application. sfCompleteName is cached.
     *
     * @return reference of attribute names to this component
     *
     * @throws RemoteException In case of network/rmi error
     * @TODO: clean cache when re-parenting
     */

    public Reference sfCompleteName() throws RemoteException {
        if (sfCompleteName == null) {
            Reference r;
            r = new Reference();

            String canonicalHostName = SmartFrogCoreKeys.SF_HOST;

            try {
                // read sfHost attribute. Faster that using sfDeployedHost().
                InetAddress address = ((InetAddress) sfResolveHere(
                        canonicalHostName,
                        false));
                canonicalHostName = address != null ? address.getCanonicalHostName() :
                        sfDeployedHost().getCanonicalHostName();
            } catch (SmartFrogException srex) {
                //if the network is in a complete mess, we can't get a complete name.
                //ignore it and continue
            }

            if (sfParent == null) {
                r.addElement(ReferencePart.host((canonicalHostName)));

                if (sfProcessName() == null) {
                    // Process created when using sfDeployFrom (use by sfStart & sfRun)
                    r.addElement(ReferencePart.here(SmartFrogCoreKeys.SF_RUN_PROCESS));
                } else {
                    r.addElement(ReferencePart.here(sfProcessName()));
                }
            } else {
                //r = sfParent.sfCompleteName(); // Only if you had a hierarchy
                //of processes.
                r.addElement(ReferencePart.host((canonicalHostName)));

                Object key = sfParent.sfAttributeKeyFor(this);

                if (key != null) {
                    r.addElement(ReferencePart.here(key));
                }
            }
            sfCompleteName = r;
        }
        return sfCompleteName;
    }

    /**
     * Register a component under given name. Exception is thrown if the name is
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
    public synchronized Object sfRegister(Object name, Prim comp) throws SmartFrogException, RemoteException {

        if ((name != null) && (sfContext.containsKey(name))) {
            throw SmartFrogResolutionException.generic(sfCompleteNameSafe(),
                    "Name '" + name + "' already used");
        }

        Object compName = name;

        if (compName == null) {
            // Make up a name for the component first get complete name of
            // component
            // Add a timestamp to the end and convert to string
            compName = SmartFrogCoreKeys.SF_UNNAMED + (new Date()).getTime() + '_' +
                    registrationNumber++;
        }

        // Add as attribute
        sfAddAttribute(compName, comp);

        // Add liveness so we know when to unregister
        if (!sfChildList().contains(comp)) {
            sfAddChild(comp);
        }
        return compName;
    }

    /**
     * DeRegisters a deployed component
     *
     * @param comp component to register
     *
     * @return true if child is removed successfully else false
     *
     * @throws SmartFrogException when component was not registered
     * @throws RemoteException In case of network/rmi error
     */
    public boolean sfDeRegister(Prim comp) throws SmartFrogException, RemoteException {
        boolean success = false;
        if (sfContext.contains(comp)) {
            sfContext.remove(sfContext.sfAttributeKeyFor(comp));
            success = true;
            //Remove all remaining instances of the same component if any. This is just to guard this corner case but it should no happen
            while (sfContext.contains(comp)){
               sfContext.remove(sfContext.sfAttributeKeyFor(comp)); 
            }
        }
        if (sfContainsChild(comp)) {
            success = sfRemoveChild(comp);
        }
        return success;
    }

    /**
     * Tries to find an attribute in the local context. If the attribute is not
     * found the thread will wait for a notification from
     * sfNotifySubprocessReady or until given timeout expires. Used to wait for
     * a new process compound to appear.
     *
     * @param name    name of attribute to wait for
     * @param timeout max time to wait in millis
     *
     * @return The object found
     *
     * @throws Exception attribute not found after timeout
     * @throws RemoteException if there is any network or remote error
     */
    public Object sfResolveHereOrWait(Object name, long timeout) throws Exception {
        long endTime = (new Date()).getTime() + timeout;
        synchronized (processLocks) {
            while (true) {
                try {
                    // try to return the attribute value
                    // if name in locks => process not ready, pretend not found...
                    if (processLocks.contains(name)) {
                        throw SmartFrogResolutionException.notFound(new
                                Reference(name),
                                sfCompleteNameSafe());
                    } else {
                        return sfResolveHere(name);
                    }
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
     *
     * @throws RemoteException if there is any network or remote error
     */
    public void sfNotifySubprocessReady(String name) throws RemoteException {

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
     * @throws Exception failed to deploy process
     */
    public ProcessCompound sfResolveProcess(Object name,
                                            ComponentDescription cd)
            throws Exception {
        ProcessCompound pc;

        if (sfParent() == null) { // I am the root
            try {
                pc = (ProcessCompound) sfResolve(new Reference(new HereReferencePart(
                        name)));
            } catch (SmartFrogResolutionException e) {
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace(" Creating a new ProcessCompound: " + name.toString(),e);
                }
                pc = addNewProcessCompound(name, cd);
                pc.sfParentageChanged();
            }
        } else { // I am a child process - find in the parent
            pc = ((ProcessCompound) sfParent()).sfResolveProcess(name, cd);
        }
        if (sfLog().isTraceEnabled()) {
            sfLog().trace("ProcessCompound '" + name + "' found.");
        }

        return pc;
    }

    // Internal
    //
    //

    /**
     * Checks is sub-processes are allowed through attribute system property
     * sfProcessAllow and checks that it is the root process compound. Uses
     * startProcess to start the actual sub-process. Then uses sfProcessTimeout
     * to wait for the new process compound to appear in attribute table. If
     * this does not happen the process is killed, and an exception is thrown.
     *
     * @param name name of new compound
     * @param cd component to deploy
     * @return ProcessCompound
     *
     * @throws Exception failed to deploy new naming compound
     */
    private ProcessCompound addNewProcessCompound(Object name, ComponentDescription cd)
        throws Exception {

        if (! isProcessCreationAllowed()) {
            throw SmartFrogResolutionException.generic(sfCompleteName(),
                    "Not allowed to create process '" + name.toString() + '\'');
        }

        // Locate timeout
        Object timeoutObj = null;
        long timeout;
        try {
            timeoutObj = sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_TIMEOUT);
            timeout = 1000 * ((Number)timeoutObj).intValue();
        } catch (ClassCastException ccex) {
            throw SmartFrogResolutionException.illegalClassType(
                    Reference.fromString(SmartFrogCoreKeys.SF_PROCESS_TIMEOUT),
                    sfCompleteNameSafe(),
                    timeoutObj,
                    timeoutObj.getClass().getName(),
                    "java.lang.Integer");
        }

        // Start process
        Process process = subprocessStarter.startProcess(this, name.toString(), nextSubprocessId, cd);
        nextSubprocessId++;

        try {
            // Wait for new compound to appear and try to return it
            ProcessCompound newPc = (ProcessCompound) sfResolveHereOrWait(name, timeout);
            if (sfLog().isDebugEnabled()){
                try {
                    sfLog().debug("New ProcessCompound "+name+" created: "+ newPc.sfCompleteName());
                } catch (Throwable thr) {
                    sfLog().debug("New ProcessCompound " + name + " created.");
                    sfLog().error(thr);
                }
            }
            return newPc;
        } catch (Exception ex) {
            // failed to find new compound. Destroy process and re-throw
            // exception
            if (process != null) {
                process.destroy();
            }
            throw ex;
        }
    }

    private boolean isProcessCreationAllowed() throws SmartFrogResolutionException {
        Object allowProcessAttr = sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_ALLOW, false);

        boolean allowProcess;
        if (allowProcessAttr == null) {
            allowProcess = false;
        } else if (allowProcessAttr instanceof Boolean) {
            allowProcess = ((Boolean) allowProcessAttr).booleanValue() && sfIsRoot;
        } else {
            allowProcess = false;
            if (sfLog().isErrorEnabled()) {
                SmartFrogResolutionException srex =
                        SmartFrogResolutionException.illegalClassType(
                                Reference.fromString(SmartFrogCoreKeys.SF_PROCESS_ALLOW),
                                sfCompleteNameSafe(),
                                allowProcessAttr,
                                allowProcessAttr.getClass().getName(),
                                "java.lang.Boolean");
                sfLog().error(srex);
            }
        }
        return allowProcess;
    }

    public static void shutdownRMIRegistry(LogSF sfLog) {
        try {
            Registry registry = SFSecurity.getNonStubRegistry();
            if (sfLog.isDebugEnabled())
                sfLog.debug("Shutting down RMI registry : " + registry);
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (Throwable t) {
            sfLog.error("Exception when shutting down registry", t);
        }
    }

    //Tags - Special case for rootProcess: rootProcess does not have tags.

    /**
     * Set the TAGS for this component. TAGS are simply uninterpreted strings
     * associated with each attribute. rooProcess does not do anything.
     * rootProcess does not have tags.
     *
     * @param tags a set of tags
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public void sfSetTags(Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            super.sfSetTags(tags);
        }
    }

    /**
     * Get the TAGS for this process compound. TAGS are simply uninterpreted
     * strings associated with each attribute. rooProcess returns null.
     * rootProcess does not have tags.
     *
     * @return the set of tags
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public Set sfGetTags() throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            return super.sfGetTags();
        } else {
            return null;
        }
    }

    /**
     * add a tag to the tag set of this component rootProcess does not have
     * tags.
     *
     * @param tag a tag to add to the set
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public void sfAddTag(String tag)
            throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            super.sfAddTag(tag);
        }
    }

    /**
     * remove a tag from the tag set of this component if it exists rootProcess
     * does not have tags.
     *
     * @param tag a tag to remove from the set
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public void sfRemoveTag(String tag)
            throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            super.sfRemoveTag(tag);
        }
    }

    /**
     * add a tag to the tag set of this component rootProcess does not have
     * tags.
     *
     * @param tags a set of tags to add to the set
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public void sfAddTags(Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            super.sfAddTags(tags);
        }
    }

    /**
     * remove a tag from the tag set of this component if it exists rootProcess
     * does not have tags.
     *
     * @param tags a set of tags to remove from the set
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public void sfRemoveTags(Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            super.sfRemoveTags(tags);
        }
    }

    /**
     * Return whether or not a tag is in the list of tags for this component
     * rootProcess returns false.  rootProcess does not have tags.
     *
     * @param tag the tag to chack
     *
     * @return whether or not the attribute has that tag
     *
     * @throws RemoteException network or RMI problems
     * @throws SmartFrogRuntimeException the attribute does not exist;
     */
    public boolean sfContainsTag(String tag)
            throws RemoteException, SmartFrogRuntimeException {
        if (sfParent != null) {
            return super.sfContainsTag(tag);
        }
        return false;
    }

}
