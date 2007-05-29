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

package org.smartfrog.services.trace;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.smartfrog.services.display.PrintMsgInt;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimHook;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 * Implements the trace component in SmartFrog System. It traces the lifecycle
 * methods of the components.
 */
public class SFTrace extends PrimImpl implements SFTraceIntf {
    /** Counter for message printing. */
    private static long counter = 0;
    /** PrintMsgInt object. */
    private PrintMsgInt printMsgImp = null;
    /** Inet address. */
    private java.net.InetAddress localhost = null;
    /** String name for rootLocatorPrt. */
    private String rootLocatorPort = "defaultRootLocatorPort";
    /** Strin for process name. */
    private String processName = "defaultProcessName";
    /** Flag indicating verbose is on or off. */
    private boolean verbose = true;

    /** the log component to which to generate the trace logs */
    private LogSF log = null;

    private boolean deployHook = true;
    private boolean startHook = true;
    private boolean terminateHook = true;

    /**
     * Instances of the different tracers. (Non anonymous to allow removal on
     * sfTerminate)
     */
    /** Deploy tracer. */
    private SfDeployTracer sfDeployTracer = new SfDeployTracer();
    /** Start tracer. */
    private SfStartTracer sfStartTracer = new SfStartTracer();
    /** Terminate tracer. */
    private SfTerminateWithTracer sfTerminateWithTracer = new SfTerminateWithTracer();

    /**
     * Constructor.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public SFTrace() throws RemoteException {
    }

    /**
     * Deploys the component.
     *
     * @throws SmartFrogException in case of error while deploying
     * @throws RemoteException in case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {

        super.sfDeploy();

        try {
            try {
                localhost = SFProcess.getProcessCompound().sfDeployedHost();
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error("sfTRACE: Exception deployment: " + ex.toString(),ex);
                }
            }

            try {
                rootLocatorPort = SFProcess.getProcessCompound()
                        .sfResolve(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT)
                        .toString();
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error("sfTRACE: Exception deployment:" + ex.toString(),ex);
                }
            }

            try {
                processName = getSfProcessName();
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error("sfTRACE: Exception deployment:" + ex.toString());
                }
            }

            verbose = sfResolve(ATR_VERBOSE, verbose, false);

            printMsgImp = (PrintMsgInt) sfResolve(ATR_OUTPUT_MSG, false);

            //DeployHook
            deployHook = sfResolve(ATR_DEPLOY_HOOK, deployHook, false);
            // applied by default
            if (deployHook) {
                sfDeployHooks.addHook(sfDeployTracer);
            }

            //StartHook
            startHook = sfResolve(ATR_START_HOOK, startHook, false);
            // applied by default
            if (startHook) {
                sfStartHooks.addHook(sfStartTracer);
            }

            //TeminateWithHook
            terminateHook =
                    sfResolve(ATR_TERMINATE_HOOK, terminateHook, false);
            // applied by default
            if (terminateHook) {
                sfTerminateWithHooks.addHook(sfTerminateWithTracer);
            }

        } catch (Throwable t) {
            // TODO: Need to be revisited
            throw new SmartFrogDeploymentException(t, this);
        }
    }

    String getSfProcessName() {
        String value = (String) System.getProperty("org.smartfrog.sfcore.processcompound.sfProcessName");

        if (value == null) {
            return SmartFrogCoreKeys.SF_ROOT;
        } else {
            return value;
        }
    }

    String getDN(Prim prim) {
        return getUniqueID(prim);
    }

    String getUniqueID(Object value) {
        String id = null;

        try {
            if ((value instanceof Prim)) {
                id = ((Prim) value).sfCompleteName().toString();
            }

            // TODO: Check
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("sfTRACE: Exception(getUniqueID :" + ex.toString(),ex);
            }
            id = "defaultUniqueID";
        }

        return id;
    }

    /**
     * Starts the component.
     *
     * @throws SmartFrogException in case of error in starting
     * @throws RemoteException in case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     * Terminate the component.
     *
     * @param r TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord r) {
        try {
            sfDeployHooks.removeHook(sfDeployTracer);
        } catch (Exception e) {
            printMsg(" Couldn't remove all deploy hooks " + e,  new Date(System.currentTimeMillis()));
        }

        try {
            sfStartHooks.removeHook(sfStartTracer);
        } catch (Exception e) {
            printMsg(" Couldn't remove all start hooks " + e,  new Date(System.currentTimeMillis()));
        }
        try {
            sfTerminateWithHooks.removeHook(sfTerminateWithTracer);
        } catch (Exception e) {
            printMsg(" Couldn't remove terminate all hooks " + e,  new Date(System.currentTimeMillis()));
        }

        super.sfTerminateWith(r);
    }

    /**
     * Prints message phase.
     *
     * @param msg message
     * @param tag tag
     * @param date date
     */
    private void printMsgPhase(String msg, String tag, Date date) {
        if ("".equals(msg)) {
            msg = "ROOT[" + rootLocatorPort + "]";
        } else {
            msg = "ROOT[" + rootLocatorPort + "]>" + msg;
        }

        printMsg(msg + ", " + tag, date);
    }

    /**
     * Prints message deploy.
     *
     * @param msg message
     * @param date date
     */
    private void printMsgDeploy(String msg, Date date) {
        printMsgPhase(msg, "DEPLOYING", date);
    }

    /**
     * Prints message start.
     *
     * @param msg message
     * @param date date
     */
    private void printMsgStart(String msg, Date date) {
        printMsgPhase(msg, "STARTING", date);
    }

    /**
     * Prints message terminate.
     *
     * @param msg message
     * @param terminationType termination type
     * @param terminationMsg termination message
     * @param date termination date
     */
    private void printMsgTerminate(String msg, String terminationType, String terminationMsg, Date date) {
        printMsgPhase(msg, "TERMINATED - "+terminationType+" (" + terminationMsg + ")", date);
    }

   SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS z, yyyy/MM/dd");
    /**
     * Prints message.
     *
     * @param msg message
     * @param date date
     */
    private void printMsg(String msg, Date date) {
        if (date == null) date =  new Date(System.currentTimeMillis());
        try {
            //counter++;
            if (verbose) {
                msg = msg + " in " + processName;

                if (localhost != null) {
                    msg = msg + " at " + localhost.toString();

                    if (rootLocatorPort != null) {
                        msg = msg + ":" + rootLocatorPort;
                    }
                }
                msg = msg + ", [" +(dateFormatter.format(date)) + "]";
            }

            if (printMsgImp == null) {
                sfLog().out("[sfTRACE] "+msg);
            } else {
                try {
                    printMsgImp.printMsg(msg + "\n");
                } catch (Exception ex) {
                    if (sfLog().isErrorEnabled())
                        sfLog().error("sfTRACE: "+ex, ex);
                }
            }
        } catch (Throwable th) {
	    if (sfLog().isErrorEnabled())
            sfLog().error("sfTRACE.printMsg " + th.toString(),th);
        }
    }

    /**
     * Utility inner class- deploy tracer
     */
    private class SfDeployTracer implements PrimHook {
    /**
     * sfHookAction for deploying
     *
     * @param prim prim component
     * @param terminationRecord TerminationRecord object
     *
     * @throws SmartFrogException in case of any error
     */
        public void sfHookAction(Prim prim, TerminationRecord terminationRecord)
            throws SmartFrogException {
            Date date = new Date(System.currentTimeMillis());
            try { prim.sfReplaceAttribute("sfTraceDeployLifeCycle",date);
            } catch (RemoteException rex){
                printMsg(rex.toString(),null);
            }
            printMsgDeploy(getDN(prim), date);
        }
    }

    /**
     * Utility inner class- start tracer
     */
    private class SfStartTracer implements PrimHook {
    /**
     * sfHookAction for starting
     *
     * @param prim prim component
     * @param terminationRecord TerminationRecord object
     *
     * @throws SmartFrogException in case of any error
     */
        public void sfHookAction(Prim prim, TerminationRecord terminationRecord)
            throws SmartFrogException {
            Date date = new Date(System.currentTimeMillis());
            try {
                prim.sfReplaceAttribute("sfTraceStartLifeCycle",date);
            } catch (RemoteException rex){
                printMsg(rex.toString(),null);
            }
            printMsgStart(getDN(prim), date);
        }
    }

    /**
     * Utility inner class- terminate tracer
     */
    private class SfTerminateWithTracer implements PrimHook {
    /**
     * sfHookAction for terminating
     *
     * @param prim prim component
     * @param terminationRecord TerminationRecord object
     *
     * @throws SmartFrogException in case of any error
     */
        public void sfHookAction(Prim prim, TerminationRecord terminationRecord)
            throws SmartFrogException {
            Date date = new Date(System.currentTimeMillis());
            try {
                prim.sfReplaceAttribute("sfTracesfTraceTerminateLifeCycle",date);
            } catch (RemoteException rex){
                printMsg(rex.toString(),null);
            }
            printMsgTerminate(getDN(prim), terminationRecord.errorType, terminationRecord.toString(), date);
        }
    }
}
