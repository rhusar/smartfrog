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

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.OrderedHashtable;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.deployer.SFDeployer;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;

import sun.misc.Signal;
import sun.misc.SignalHandler;


/**
 * Access point to the single allowed process compound for a VM. It holds the
 * single instance of the process compound. It also knows how to get
 * a process compound from a given host and process name by forwarding the
 * request to the process compound on that host. Thirdly it maintains a
 * root locator which knows how to make a process compound the root
 * process compound for a host, and to get a root process compound.
 *
 */
public class SFProcess implements MessageKeys {
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



    private SFProcess (){
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
    public static void setRootLocator(RootLocator c) throws Exception {
        if (rootLocator != null) {
            throw new Exception("Root locator already set");
        }
        rootLocator = c;
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
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public static RootLocator getRootLocator()
        throws SmartFrogException, RemoteException {
        String className = null;

        try {
            if (rootLocator == null) {
                className = (String) getProcessCompoundDescription().sfResolve(refRootLocatorClass);

                return (RootLocator) SFClassLoader.forName(className)
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
     * @exception Exception Failed to deploy component
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
                            "Deployment Failure: " + ex, comp.getCompleteName()));
                } catch (Exception termex) {
                    // ignore
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
            Reference newRef = null;

            try {
                newRef = comp.sfCompleteName();
            } catch (Exception exName) {
            }

            try {
                TerminationRecord tr = TerminationRecord.abnormal(
                        "Failed to start ", newRef);
                Logger.log(newRef.toString(),tr,SmartFrogException.forward(ex));
                comp.sfTerminate(tr);
            } catch (Exception termEx) {
                // ignore
            }

            throw ex;
        }
    }

    public static void deployDefaultProcessDescriptions(ProcessCompound comp)
        throws SmartFrogException, RemoteException {
        Properties props = System.getProperties();
        Prim p;
        Context nameContext = null;
        String name = null;
        String url = null;
        String key = null;
        try {
            for (Enumeration e = props.keys(); e.hasMoreElements(); ) {
                key = e.nextElement().toString();
                if (key.startsWith(SmartFrogCoreProperty.defaultDescPropBase)) {
                    // Collects all properties refering to default descriptions that
                    // have to be deployed inmediately after process compound
                    // is started.
                    url = (String)props.get(key);
                    name = key.substring(SmartFrogCoreProperty.defaultDescPropBase.length());
                    nameContext = new ContextImpl();
                    nameContext.put(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, name);
                    ComponentDescription cd = ComponentDescriptionImpl.
                        sfComponentDescription(url.trim());
                    // Parent is null: default descriptions are root components
                    p = comp.sfDeployComponentDescription(null, null, cd,
                        nameContext);
                    p.sfDeploy();
                    p.sfStart();
                }
            }

        } catch (SmartFrogException sfex){
           if (sfex instanceof SmartFrogDeploymentException) throw sfex;
           else throw new SmartFrogDeploymentException
             ("deploying default description for '" + key+"'", sfex,comp,nameContext);
        }
    }

    public static void addDefaultProcessDescriptions
        (ComponentDescription compDesc) throws SmartFrogException, RemoteException {
        Properties props = System.getProperties();
        String name =null;
        String url=null;
        String key=null;
        for (Enumeration e = props.keys(); e.hasMoreElements();) {
            key = e.nextElement().toString();
            if (key.startsWith(SmartFrogCoreProperty.defaultDescPropBase)) {
                // Collects all properties refering todefault descriptions that
                // have to be deployed inmediately after process compound
                // is started.
                url = (String)props.get(key);
                name = key.substring(SmartFrogCoreProperty.defaultDescPropBase.length());
                //SFSystem.deployFromURL(url,name, comp);
                ComponentDescription cd = ComponentDescriptionImpl.sfComponentDescription(url);
                compDesc.getContext().put(name,cd);
            }
        }
    }

     private static boolean processCompoundTerminated = false;

    /**
     * signal handler for control-C events
     */
    private static class InterruptHandler implements SignalHandler {

        private SignalHandler oldHandler;

        public void handle(Signal sig) {
            if (!processCompoundTerminated) {
                processCompoundTerminated = true;
                if (processCompound != null) {
                    try {
                        Logger.log("Terminating sfDaemon gracefully!!");
                        processCompound.sfTerminate(new TerminationRecord(TerminationRecord.NORMAL,
                                "sfDaemon forced to terminate ",
                                ((Prim) processCompound).sfCompleteName()));
                    } catch (RemoteException re) {
                        Logger.log(re);
                        //log and ignore
                    } catch (Throwable thr) {
                        Logger.log(thr);
                    }
                }
            } else {
                Logger.log("sfDaemon killed!");
                System.exit(0);
            }
        }

        /**
         * bind to a signal. On HP-UX+cruise control this fails with an error,
         * one we dont see on the command line.
         * This handler catches the exception and logs it, so that smartfrog
         * keeps running even if graceful shutdown is broken.
         * @param name name of interrupt to bind to.
         */
        public void bind(String name) {
            try {
                oldHandler=Signal.handle(new Signal(name), this);
            } catch (IllegalArgumentException e) {
                //this happens when binding fails. In this situation, warn, but keep going
                Logger.log("Failed to set control-C handler -is JVM running with -Xrs set?");
                Logger.log(e);
            }
        }
    }


    /**
     * Deploys the local process compound, if not already there
     *
     * @return local process compound
     *
     * @throws SmartFrogException if failed to deploy process compound
     */
    public static synchronized ProcessCompound deployProcessCompound()
        throws SmartFrogException,RemoteException {

        if (processCompound != null) {
            return processCompound;
        }

        new InterruptHandler().bind("INT");


        ComponentDescription descr =
              (ComponentDescription) getProcessCompoundDescription().copy();

        try {
            processCompound = (ProcessCompound) startComponent(deployComponent(descr));
        } catch (Exception e) {
            throw SmartFrogDeploymentException.forward(e);
        }

        // This call and method will disapear once we refactor ProcessCompound
        // addDefaultProcessDescriptions will replace all this code.
        // @TODO fix after refactoring ProcessCompound.
        deployDefaultProcessDescriptions((ProcessCompound)processCompound);


        return processCompound;
    }





    /**
     * Gets the description for the process compound. Retrieves the default
     * description out of processcompound.sf. Then allows overrides from any
     * system property starting with the contents of the propBase variable.
     * The description is type and deployResolved. Since system properties do
     * not handle numbers, the number representation for system properties is
     * restricted to doubles. For each value in the targetted system
     * properties conversion is attempted to a number.
     *
     * @return component description for process compound
     *
     * @exception SmartFrogException failed to create description
     * @throws RemoteException In case of network/rmi error
     */
    public static ComponentDescription getProcessCompoundDescription()
        throws SmartFrogException, RemoteException {
        if (processCompoundDescription != null) {
            return processCompoundDescription;
        }

        processCompoundDescription = getCoreProcessCompoundDescription();

        // Cannot be used yet because ProcessCompound acts alos as deployer
        // This will be used once we refactor ProcessCompound.
        //addDefaultProcessDescriptions (processCompoundDescription);

        Properties props = System.getProperties();

        for (Enumeration e = props.keys(); e.hasMoreElements();) {
            String key = e.nextElement().toString();
            if (key.startsWith(SmartFrogCoreProperty.defaultDescPropBase)) {

              // Ignore, will be use to deploy  default descriptions.

            } else if (key.startsWith(SmartFrogCoreProperty.propBaseSFProcess)) {
                Object value = props.get(key);

                try {
                    // convert to number
                    value = Double.valueOf((String) value);
                } catch (Exception ex) {
                    // ignore, value is not a number
                }

                String cxtKey = key.substring(SmartFrogCoreProperty.propBaseSFProcess.length());
                processCompoundDescription.getContext().put(cxtKey, value);
            }
        }

        return processCompoundDescription;
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
        String urlProcessCompound =
                "org/smartfrog/sfcore/processcompound/processcompound.sf";
        Vector phases = new Vector();
        phases.add("type");
        phases.add("link");
        return ComponentDescriptionImpl.sfComponentDescription(urlProcessCompound, phases, refProcessCompound);
    }


    /**
     * Sets the description which will create the process compound if it has
     * not already been created
     *
     * @param descr description to maintain
     */
    public static void setProcessCompoundDescription(ComponentDescription descr) {
        processCompoundDescription = descr;
    }

    /**
     * Select target process compound using host and subprocess names
     *
     * @param host host name. If null, assumes localhost.
     * @param subProcess subProcess name (optional; can be null)
     * @return ProcessCompound the target process compound
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ProcessCompound sfSelectTargetProcess(String host,
        String subProcess) throws SmartFrogException, RemoteException {
        try {
            if (host==null) {
                return sfSelectTargetProcess((InetAddress)null,
                                           subProcess);
            } else {
                return sfSelectTargetProcess(InetAddress.getByName(host),
                                           subProcess);
            }
        } catch (UnknownHostException uhex) {
            throw new SmartFrogException(MessageUtil.formatMessage(
                MSG_UNKNOWN_HOST, host), uhex);
        }

    }

    /**
     * Select target process compound using host InetAddress and subprocess name
     *
     * @param host host InetAddress object. If null, assumes localhost.
     * @param subProcess subProcess name (optional; can be null)
     * @return ProcessCompound the target process compound
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ProcessCompound sfSelectTargetProcess(InetAddress host,
                                                      String subProcess)
            throws SmartFrogException, RemoteException {
        ProcessCompound target = null;
        try {
            target = SFProcess.getProcessCompound();
            if (host != null) {
                target = SFProcess.getRootLocator().
                        getRootProcessCompound(host);
            }
            if (subProcess != null) {
                try {
                  try {
                    target = (ProcessCompound) target.sfResolveHere(subProcess); //target.sfResolveHere(subProcess);
                  }
                  catch (java.lang.ClassCastException thr) {
                    throw SmartFrogResolutionException.illegalClassType(
                        Reference.fromString(subProcess),
                        target.sfCompleteName(),
                        target.sfResolve(subProcess).getClass().getName(),
                        "ProcessCompound");
                  }
                } catch (Exception ex){
                  throw new SmartFrogException("Error selecting target process '"+subProcess+"' in '"+target.sfCompleteName()+"'",ex);
                }
            }
        } catch (Throwable ex) {
            throw SmartFrogException.forward(ex);
        }
        return target;
    }

}
