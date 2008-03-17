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

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.deployer.SFDeployer;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Access point to the single allowed process compound for a VM. It holds the
 * single instance of the process compound. It also knows how to get a process
 * compound from a given host and process name by forwarding the request to the
 * process compound on that host. Thirdly it maintains a root locator which
 * knows how to make a process compound the root process compound for a host,
 * and to get a root process compound.
 */
public class SFProcess implements MessageKeys {

    /**
     * Log for SFProcess (Process Log).
     */
    private static LogSF sfLog = LogFactory.sfGetProcessLog();
    /**
     * Single instance of process compound for this process
     */
    protected static ProcessCompound processCompound;

    /**
     * processCompound description
     */
    protected static ComponentDescription processCompoundDescription;

    /**
     * Root locator to get and set the root process compound for this HOST
     */
    protected static RootLocator rootLocator;

    /**
     * Reference to root locator class.
     */
    protected static final Reference refRootLocatorClass = new Reference(
            SmartFrogCoreKeys.SF_ROOT_LOCATOR_CLASS);

    /**
     * Reference to process compound.
     */
    protected static final Reference refProcessCompound = new Reference(
            "ProcessCompound");
    private static final String INTERRUPT_HANDLER = "org.smartfrog.sfcore.processcompound.InterruptHandlerImpl";
    private static final String ERROR_NO_INTERRUPT_HANDLER = "Could not create an interrupt handler from " + INTERRUPT_HANDLER
            + "\nSmartFrog may be running on a JVM which does not support this feature";

//    /** ProcessLog. This log is used to log into the core log: SF_CORE_LOG
//     *  It can be replaced using sfSetLog()
//     */
//    private LogSF sflog = sfLog();

    private SFProcess() {
    }

    /**
     * Sets the root locator for this process. The root locator will be used to
     * set the root proces compound for this host and get a root process
     * compound for other hosts. The root locator can only be set once.
     *
     * @param c root locator to use.
     *
     * @throws Exception if failed to set root locator
     */
    public static synchronized void setRootLocator(RootLocator c)
            throws Exception {
        if (rootLocator != null) {
            throw new Exception("Root locator already set");
        }
        rootLocator = c;
    }


    /**
     * Sets the single instance of process compound for this process. The
     * ProcessCompound can only be set once.
     *
     * @param pc root locator to use.
     *
     * @throws Exception if failed to set process compound
     */
    public static synchronized void setProcessCompound(ProcessCompound pc)
            throws Exception {
        if (processCompound != null) {
            throw new Exception("ProcessCompound already set");
        }
        processCompound = pc;
    }


    /**
     * Gets the root locator for this process. If the root locator is not set a
     * default root locator is created and returned based on the
     * sfRootLocatorClass system property (offset by propBase). Default root
     * locator is the DefaultRootLocatorImpl.
     *
     * @return root locator for this process
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while deploying
     * the component
     */
    public static synchronized RootLocator getRootLocator()
            throws SmartFrogException, RemoteException {
        String className = null;

        try {
            if (rootLocator == null) {
                className = (String) getProcessCompoundDescription().sfResolve(
                        refRootLocatorClass);
                rootLocator = (RootLocator) SFClassLoader.forName(className)
                        .newInstance();
            }
        } catch (ClassNotFoundException cnfexcp) {
            // TODO: Check
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND, className), cnfexcp);
        } catch (InstantiationException instexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR, className), instexcp);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS, className, "newInstance()"), illaexcp);
        }

        return rootLocator;
    }

    /**
     * Returns the process local process compound.
     *
     * @return host process compound
     */
    public static ProcessCompound getProcessCompound() {
        return processCompound;
    }

    /**
     * Deploys given component description. First does a type and deploy
     * resolution step. If any error occurs on deployment the component is
     * terminated
     *
     * @param comp description of component that is to be deployed
     *
     * @return Prim
     *
     * @throws SmartFrogException Failed to deploy component
     * @throws RemoteException trouble on the wire
     */
    protected static Prim deployComponent(ComponentDescription comp)
            throws SmartFrogException, RemoteException {
        Prim dComp = null;

        try {
            //comp.deployResolve();
            dComp = SFDeployer.deploy(comp, null, null, null);
            dComp.sfDeploy();
        } catch (SmartFrogException ex) {
            // Deployment failure, try terminating
            if (dComp != null) {
                try {
                    dComp.sfTerminate(TerminationRecord.abnormal(
                            "Deployment Failure: " + ex,
                            comp.sfCompleteName(),
                            ex));
                } catch (Exception termex) {
                    // ignore
                    if (sfLog().isIgnoreEnabled()) {
                        sfLog().ignore(ex);
                    }
                }
            }

            throw (SmartFrogException) ex.fillInStackTrace();
        }

        return dComp;
    }

    /**
     * Starts given component. If any errors occur on start the component is
     * terminated.
     *
     * @param comp component to start
     *
     * @return started deployed component
     *
     * @throws Exception if failed to start component
     */
    protected static Prim startComponent(Prim comp) throws Exception {
        try {
            comp.sfStart();
            return comp;
        } catch (Exception ex) {
            Reference newRef = ComponentHelper.completeNameSafe(comp);
            try {
                TerminationRecord tr = TerminationRecord.abnormal(
                        "Failed to start ",
                        newRef,
                        ex);
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(newRef.toString(),
                            SmartFrogException.forward(ex),
                            tr);
                }
                comp.sfTerminate(tr);
            } catch (Exception termEx) {
                // ignore
            }

            throw ex;
        }
    }

    public static void addDefaultProcessDescriptions
            (ComponentDescription compDesc)
            throws SmartFrogException, RemoteException {
        Properties props = System.getProperties();
        String name = null;
        String url = null;
        String key = null;
        for (Enumeration e = props.keys(); e.hasMoreElements();) {
            key = e.nextElement().toString();
            if (key.startsWith(SmartFrogCoreProperty.defaultDescPropBase)) {
                // Collects all properties refering to default descriptions that
                // have to be deployed inmediately after process compound
                // is started.
                url = (String) props.get(key);
                name = key.substring(SmartFrogCoreProperty.defaultDescPropBase.length());
                //SFSystem.deployFromURL(url,name, comp);
                ComponentDescription cd = ComponentDescriptionImpl.sfComponentDescription(
                        url);
                compDesc.sfReplaceAttribute(name,
                        cd); //.getContext().put(name,cd);
            }
        }
    }


    /**
     * Flag that indicates the process is terminated
     */
    private static volatile boolean processCompoundTerminated = false;


    /**
     * Flag that indicates the process is terminated
     *
     * @return the current flag value
     */
    static boolean isProcessCompoundTerminated() {
        return processCompoundTerminated;
    }

    /**
     * Sets processCompoundTerminated to true, and returns its previous value,
     * in a synchronized operation
     *
     * @return the previous value.
     */
    static synchronized boolean markProcessCompoundTerminated() {
        boolean isTerminated = processCompoundTerminated;
        if (!isTerminated) {
            processCompoundTerminated = true;
        }
        return isTerminated;
    }

    /**
     * Deploys the local process compound, if not already there
     *
     * @param addShutdownHook flag to enable shutdown hook listening
     *
     * @return local process compound
     *
     * @throws SmartFrogException if failed to deploy process compound
     */
    public static synchronized ProcessCompound deployProcessCompound(boolean addShutdownHook)
            throws SmartFrogException, RemoteException {

        if (processCompound != null) {
            return processCompound;
        }

        //conditionally add a shutdown hook when the JVM permits it
        if (addShutdownHook) {
            try {
                Class irqHandlerClass = Class.forName(INTERRUPT_HANDLER);
                Constructor constructor = irqHandlerClass.getConstructor(new Class[0]);
                InterruptHandler handler = (InterruptHandler) constructor.newInstance(
                        new Object[0]);
                handler.bind("INT", sfLog());
            } catch (NoClassDefFoundError e) {
                //class not found
                sfLog().error(ERROR_NO_INTERRUPT_HANDLER, e);
            } catch (Exception ex) {
                //all the other ways things could fail; see SFOS-159
                sfLog().error(ERROR_NO_INTERRUPT_HANDLER, ex);
            }
        }

        ComponentDescription descr = (ComponentDescription) getProcessCompoundDescription()
                .copy();
        ComponentDescription descrCache = (ComponentDescription) descr.copy();

        try {
            // A process compound sets processcompound in SFProcess at the end of its
            // sfStart lifecycle method! Setting it twice will result in a exception!
            startComponent(deployComponent(descr));

            //cache process component description
            processCompoundDescription = descrCache;

        } catch (Exception e) {
            throw SmartFrogDeploymentException.forward(e);
        }

        // This call and method will disapear once we refactor ProcessCompound
        // addDefaultProcessDescriptions will replace all this code.
        // @TODO fix after refactoring ProcessCompound.
        //deployDefaultProcessDescriptions((ProcessCompound)processCompound);

        return processCompound;
    }


    /**
     * Resets the root process compound
     *
     * @param terminatorCompleteName reference of terminatorCompleteName
     *
     * @return new root process compound
     *
     * @throws SmartFrogException if failed to deploy process compound, the root
     * process compound didn't exist or ir the local process compound is not a
     * root process compound
     */
    public static synchronized ProcessCompound resetRootProcessCompound(
            Reference terminatorCompleteName)
            throws SmartFrogException, RemoteException {
        if ((processCompound != null) && processCompound.sfIsRoot()) {
            //Terminate process compound but without system exit
            processCompound.systemExitOnTermination(false);
            TerminationRecord termR = TerminationRecord.normal(
                    "Restarting ProcessCompound: " +
                            processCompound.sfCompleteName(),
                    terminatorCompleteName);
            processCompound.sfAddAttribute("sfSyncTerminate", Boolean.TRUE);
            processCompound.sfTerminate(termR);
            // reset cached processCompoundDescription
            processCompoundDescription = null;
            return deployProcessCompound(true);
        }
        if (processCompound == null) {
            throw new SmartFrogRuntimeException(
                    "Process Compound cannot be reset: is null");
        } else {
            throw new SmartFrogRuntimeException(
                    "Process Compound cannot be reset");
        }
    }


    /**
     * Gets the description for the process compound. Retrieves the default
     * description out of processcompound.sf. Then allows overrides from any
     * system property starting with the contents of the propBase variable. The
     * description is type and deployResolved. Since system properties do not
     * handle numbers, the number representation for system properties is
     * restricted to doubles. For each value in the targetted system properties
     * conversion is attempted to a number.
     *
     * @return component description for process compound
     *
     * @throws SmartFrogException failed to create description
     * @throws RemoteException In case of network/rmi error
     */
    public static ComponentDescription getProcessCompoundDescription()
            throws SmartFrogException, RemoteException {

        if (processCompoundDescription != null) {
            //return cache
            return processCompoundDescription;
        }

        ComponentDescription newProcessCompoundDescription = getCoreProcessCompoundDescription();

        // Cannot be used yet because ProcessCompound acts also as deployer
        // This will be used once we refactor ProcessCompound.
        //@TODO: review once ProcessCompound/SFProcess are reviewed.
        // this would replace ProcessCompoundImpl.deployDefaultProcessDescriptions
        //addDefaultProcessDescriptions (processCompoundDescription);

        // Add system properties
        newProcessCompoundDescription = ComponentDescriptionImpl.addSystemProperties(
                SmartFrogCoreProperty.propBaseSFProcess
                , newProcessCompoundDescription);

        return newProcessCompoundDescription;
    }

    /**
     * Gets the core description for process compound out of
     * processcompound.sf.
     *
     * @return process compound description type and deployResolved
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException In case of SmartFrog system error
     */
    public static ComponentDescription getCoreProcessCompoundDescription()
            throws SmartFrogException, RemoteException {
        String urlProcessCompound = "org/smartfrog/sfcore/processcompound/processcompound.sf";
        return ComponentDescriptionImpl.sfComponentDescription(
                urlProcessCompound);
    }


    /**
     * Sets the description which will create the process compound if it has not
     * already been created
     *
     * @param descr description to maintain
     */
    public static void setProcessCompoundDescription(ComponentDescription descr) {
        processCompoundDescription = descr;
    }

    /**
     * Select target process compound using host and subprocess names
     *
     * @param host       host name. If null, assumes localhost.
     * @param subProcess subProcess name (optional; can be null)
     *
     * @return ProcessCompound the target process compound
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ProcessCompound sfSelectTargetProcess(String host,
                                                        String subProcess)
            throws SmartFrogException, RemoteException {
        try {
            if (host == null) {
                return sfSelectTargetProcess((InetAddress) null, subProcess);
            } else {
                return sfSelectTargetProcess(InetAddress.getByName(host),
                        subProcess);
            }
        } catch (UnknownHostException uhex) {
            throw new SmartFrogException(MessageUtil.formatMessage(
                    MSG_UNKNOWN_HOST,
                    host), uhex);
        }

    }

    /**
     * Select target process compound using host InetAddress and subprocess
     * name
     *
     * @param host       host InetAddress object. If null, assumes localhost.
     * @param subProcess subProcess name (optional; can be null)
     *
     * @return ProcessCompound the target process compound
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ProcessCompound sfSelectTargetProcess(InetAddress host,
                                                        String subProcess)
            throws SmartFrogException, RemoteException {
        ProcessCompound target = null;
        try {
            target = SFProcess.getProcessCompound();
            if (host != null) {
                target = SFProcess.getRootLocator()
                        .getRootProcessCompound(host);
            }
            if (subProcess != null) {
                try {
                    Object targetObj = null;
                    targetObj = target.sfResolve(subProcess); //target.sfResolveHere(subProcess);
                    try {
                        target = (ProcessCompound) targetObj;
                    } catch (java.lang.ClassCastException thr) {
                        throw SmartFrogResolutionException.illegalClassType(
                                Reference.fromString(subProcess),
                                target.sfCompleteName(),
                                targetObj,
                                targetObj.getClass().getName(),
                                "ProcessCompound");
                    }
                } catch (Exception ex) {
                    throw new SmartFrogException(
                            "Error selecting target process '" + subProcess + "' in '" + target
                                    .sfCompleteName() + "'",
                            ex);
                }
            }
        } catch (SmartFrogException sfex) {
            throw SmartFrogException.forward(sfex);
        } catch (UnknownHostException uhex) {
            throw new SmartFrogException(MessageUtil.formatMessage(
                    MSG_UNKNOWN_HOST,
                    host), uhex);
        } catch (ConnectException cex) {
            throw new SmartFrogException(MessageUtil.formatMessage(
                    MSG_CONNECT_ERR,
                    host), cex);
        } catch (RemoteException rmiEx) {
            throw new SmartFrogException(MessageUtil.formatMessage(
                    MSG_REMOTE_CONNECT_ERR,
                    host), rmiEx);
        } catch (Throwable ex) {
            throw new SmartFrogException(MessageUtil.formatMessage(
                    MSG_UNHANDLED_EXCEPTION), ex);
            //throw SmartFrogException.forward(ex);
        }
        return target;
    }

    /**
     * Request the host to which this process is bound to
     *
     * @return the host InetAddress
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogResolutionException if we cannot determine even our
     * local host
     */
    private static InetAddress hostInetAddress = null;

    public static InetAddress sfDeployedHost() throws SmartFrogException {

        if (hostInetAddress != null) {
            return hostInetAddress;
        }

        try {
            String hostName = System.getProperty("java.rmi.server.hostname");
            try {
                if (hostName != null) {
                    hostInetAddress = InetAddress.getByName(hostName);
                    return hostInetAddress;
                }
            } catch (UnknownHostException ex) {
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(MessageUtil.formatMessage(
                            MSG_FAILED_INET_ADDRESS_LOOKUP), ex);
                }
            }
            try {
                //this can still do a network reverse DNS lookup, and hence fail
                hostInetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                //no, nothing there either
                hostInetAddress = InetAddress.getByName(null);

            }
            return hostInetAddress;
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(MessageUtil.formatMessage(
                        MSG_FAILED_INET_ADDRESS_LOOKUP), ex);
            }
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(
                    MessageUtil.formatMessage(MSG_FAILED_INET_ADDRESS_LOOKUP),
                    ex);
        }
    }


    /**
     * Log for SFProcess.
     *
     * @return ProcessLog
     */
    private static LogSF sfLog() {
        return sfLog;
    }

}
