/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.framework.activator;

import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.SFSystem;
import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RebindingRComponentImpl;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.locator.Locator;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 * SFActiveImpl is a rebinding recoverable component that implements the SFActive interface.
 * The framework ensures that an instance of this component called sfActive is present when 
 * the framework is active. Because it is a recoverable component it will be removed on 
 * deactivation and created on activation, ensuring that the SFActive interface is only 
 * available when the framework is active. Because it is a rebinding component, once a client 
 * has obtained a stub for the sfActive component the stub will follow the the component 
 * wherever it goes masking failovers, so the client can perform deployment and termination 
 * to the active node without being aware of any changes of location or failures.
 */
public class SFActiveImpl extends RebindingRComponentImpl implements SFActive, Rebind, Prim, Compound {
    
    private static final String deployUsage = 
        "Usage: deploy|start <hostlist> <ref> <sfUrl> [<urlSubRef>]";
    private static final String terminateUsage =
        "Usage: terminate|term|stop <hostlist> terminate <ref>";
    private final static boolean isTiming = Boolean.parseBoolean(System.getProperty(DIAG_TIMING_PROP));


    public SFActiveImpl() throws RemoteException {
        super();
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.SFActive#deploy(java.lang.String, org.smartfrog.sfcore.reference.Reference, org.smartfrog.sfcore.componentdescription.ComponentDescription)
     */
    public void deploy(String name, Reference parent, ComponentDescription cd) throws RemoteException, SmartFrogException {
        
        /**
         * Guard the interface - admit if open - even though we will
         * be un-exported.
         */
        if( sfIsInterfaceClosed() ) {
            if( sfLog().isDebugEnabled() ) {
                sfLog().debug("interface closed invoking deploy - throwing RemoteException");
            }
            throw new RemoteException("Interface is closed");
        }
        
        /**
         * Catch runtime exceptions and then any method specific 
         * exceptions and either rethrow if the interface is open
         * or replace with a RemoteException if the interface is closed. 
         * 
         * Note: if a call succeeds we allow it through even if the interface 
         * is closed. It clearly succeeded in what it was doing and had no
         * effect on the recovery storage as that was cut off too.
         */
        try {
            
            
            long start = 0;
            if( isTiming ) {
                start = System.currentTimeMillis();
            }
            
            if( parent == null ) {
                sfCreateNewApp(name, cd, null);
            } else {
                RComponent rComponent = (RComponent)sfResolve(parent);
                rComponent.sfCreateNewChild(name, cd, null);
            }

            if( isTiming ) {
                long end = System.currentTimeMillis();
                if( sfLog().isInfoEnabled() ) {
                    sfLog().info("Deploy of " + name + " took " + (end-start) + "millis");
                }
            }
            
        } catch(RuntimeException rte) {
            if( sfIsInterfaceOpen() ) {
                throw rte;
            } else {
                if( sfLog().isDebugEnabled() ) {
                    sfLog().debug("intercepted RuntimeException when interface closed, throwing RemoteEception, original: ", rte );
                }
                throw new RemoteException("Interface closed before completion");
            }
        } catch(SmartFrogException sfe) {
            if( sfIsInterfaceOpen() ) {
                throw sfe;
            } else {
                if( sfLog().isDebugEnabled() ) {
                    sfLog().debug("intercepted SmartFrogException when interface closed, throwing RemoteEception, original: ", sfe );
                }
                throw new RemoteException("Interface closed before completion");
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.activator.SFActive#terminate(org.smartfrog.sfcore.reference.Reference)
     */
    public void terminate(Reference reference) throws RemoteException, SmartFrogException {
        /**
         * Guard the interface - admit if open - even though we will
         * be un-exported.
         */
        if( sfIsInterfaceClosed() ) {
            if( sfLog().isDebugEnabled() ) {
                sfLog().debug("interface closed invoking deploy - throwing RemoteException");
            }
            throw new RemoteException("Interface is closed");
        }
        
        /**
         * Catch runtime exceptions and then any method specific 
         * exceptions and either rethrow if the interface is open
         * or replace with a RemoteException if the interface is closed. 
         * 
         * Note: if a call succeeds we allow it through even if the interface 
         * is closed. It clearly succeeded in what it was doing and had no
         * effect on the recovery storage as that was cut off too.
         */
        try {
            
            Prim prim = (Prim)sfResolve(reference);
            if( prim instanceof RComponent ) {
                ((RComponent)prim).sfDetachPendingTermination(Transaction.nullTransaction);
            } else {
                prim.sfTerminate(TerminationRecord.normal(reference));
            }
            
        } catch(RuntimeException rte) {
            if( sfIsInterfaceOpen() ) {
                throw rte;
            } else {
                if( sfLog().isDebugEnabled() ) {
                    sfLog().debug("intercepted RuntimeException when interface closed, throwing RemoteEception, original: ", rte );
                }
                throw new RemoteException("Interface closed before completion");
            }
        } catch(SmartFrogException sfe) {
            if( sfIsInterfaceOpen() ) {
                throw sfe;
            } else {
                if( sfLog().isDebugEnabled() ) {
                    sfLog().debug("intercepted SmartFrogException when interface closed, throwing RemoteEception, original: ", sfe );
                }
                throw new RemoteException("Interface closed before completion");
            }
        }
        
    }


    
    
    public static void main(String[] args) {
        
        SFSystem system=new SFSystem();

        //First thing first: system gets initialized
        try {
            system.initSystem();
        } catch (Exception ex) {
            try {
                if (system.sfLog().isErrorEnabled()) {
                    system.sfLog().error(ex);
                }
            } catch (Throwable ex1) {ex.printStackTrace();}
            system.exitWith("Failed to initialize SmartFrog", ExitCodes.EXIT_ERROR_CODE_GENERAL);
        }
        
        if( args.length < 1 ) {
            reportUsageAndExit(" - INCORRECT usage\n" + deployUsage + "\n" + terminateUsage);
        } else if( "help".equals(args[0]) || "-?".equals(args[0]) || "?".equals(args[0]) ) {
            System.out.println(deployUsage + "\n" + terminateUsage);
            System.exit(ExitCodes.EXIT_CODE_SUCCESS);
        } else if( "deploy".equals(args[0]) || "start".equals(args[0]) ) {
            sfActiveDeploy(args);
        } else if( "terminate".equals(args[0]) || "term".equals(args[0]) || "stop".equals(args[0]) ) {
            sfActiveTerminate(args);
        } else {
            reportUsageAndExit(" - INCORRECT usage\n" + deployUsage + "\n" + terminateUsage);
        }
    }

        
    private static void sfActiveDeploy(String[] args) {
        
        Vector<String> hosts = null;
        String name = null;
        Reference parent = null;
        String sfUrl = null;
        String subRef = null;
        ComponentDescription cd = null;
        
        if( (args.length != 4) && (args.length != 5) ) {
            reportUsageAndExit(" - INCORRECT number of arguments\n" + deployUsage);
        }
                
        hosts = extractHosts(args[1]);
        String[] ref = args[2].split(":");
        name = ref[ref.length-1];
        sfUrl = args[3];
        if( args.length == 5 ) {
            subRef = args[4]; 
        }

        try {
            parent = extractParent(ref);
        } catch(Exception e) {
            exitWithException(
                    " - FAILED to interpret parent reference",
                    ExitCodes.EXIT_ERROR_CODE_BAD_ARGS,
                    e);
        }
        
        try {
            cd = extractCD(sfUrl, subRef);
        } catch(Exception e) {
            exitWithException(
                    " - FAILED to interpret the sfUrl and/or subRef",
                    ExitCodes.EXIT_ERROR_CODE_BAD_ARGS,
                    e);
        }
        
        SFActive active = null;
        try {
            active = (SFActive)Locator.multiHostSfResolve(hosts, ACTIVE_ATTR);
        } catch (SmartFrogResolutionException e) {
            exitWithException(
                    " - FAILED to locate an active server",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
        }
        
        try {
            active.deploy(name, parent, cd);
        } catch (Exception e) {
            exitWithException(
                    " - FAILED to deploy on the active server",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
        }
        
        System.out.println("\n - Success\n");
        
    }
    
    static private void sfActiveTerminate(String[] args) {
        
        Vector<String> hosts = null;
        Reference target = null;
        
        if( (args.length != 3) ) {
            reportUsageAndExit(" - INCORRECT number of arguments\n" + terminateUsage);
        }
                
        hosts = extractHosts(args[1]);
        try {
            target = Reference.fromString("PROCESS:" + args[2]);
        } catch(Exception e) {
            exitWithException(
                    " - FAILED to interpret target reference",
                    ExitCodes.EXIT_ERROR_CODE_BAD_ARGS,
                    e);
        }
        
        SFActive active = null;
        try {
            active = (SFActive)Locator.multiHostSfResolve(hosts, ACTIVE_ATTR);
        } catch (SmartFrogResolutionException e) {
            exitWithException(
                    " - FAILED to locate an active server",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
        }
        
        try {
            active.terminate(target);
        } catch (Exception e) {
            exitWithException(
                    " - FAILED to terminate on the active server",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
        }
        
        System.out.println("\n - Success\n");
    }
    
    static private void reportUsageAndExit(String msg) {
        if( msg != null ) {
            System.out.println(msg);
        }
        System.exit(ExitCodes.EXIT_ERROR_CODE_BAD_ARGS);        
    }
    
        
    static private void exitWithException(String msg, int errorCode, Exception e) {

        StringBuffer buf = new StringBuffer();
        buf.append("\n" + msg);
        buf.append("\n    Result:");
        buf.append("\n      * Exception: " + e.getMessage());
        if( e.getCause() != null ) {
            buf.append("\n        Cause: " + e.getCause().getMessage());
        }
        System.out.println(buf);
        if( SFSystem.sfLog().isDebugEnabled() ) {
            SFSystem.sfLog().debug(buf, e);
        }

        System.exit(errorCode);
    }

    static private Reference extractParent(String[] ref) throws SmartFrogResolutionException {
        if( ref.length <= 1 ) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int i = 0;
        buf.append("PROCESS:");
        buf.append(ref[i++]);
        while( i < ref.length - 1) {
            buf.append(":").append(ref[i++]);
        }
        return Reference.fromString(buf.toString());
    }
        
    static private Vector<String> extractHosts(String arg) {
        Vector<String> result = new Vector<String>();
        String[] tokens = arg.split(",");
        for( String str : tokens ) {
            result.add(str.trim());
        }
        return result;
    }
    
    static private ComponentDescription extractCD(String sfUrl, String subRef) throws SmartFrogException {
        if (subRef == null) {
            return ComponentDescriptionImpl.sfComponentDescription(sfUrl);
        } else {
            return ComponentDescriptionImpl.sfComponentDescription(sfUrl, null, Reference.fromString(subRef));
        }
    }

}
