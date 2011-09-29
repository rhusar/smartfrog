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

package org.smartfrog.services.persistence.framework.connectionpool;

import java.net.InetAddress;
import java.rmi.RemoteException;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;

/**
 * ConnectionPoolSizeImpl implements a command that can be used to manage the 
 * size of the connection pool. It does this via the ConnectionPoolSize interface.
 * The main method expects arguments in the form: <code>reduce|reset|size &lthost&gt &ltcp ref&gt</code> where
 * the keyword <code>reduce</code> reduces the size of the connection pool by 1,
 * the keyword <code>reset</code> resets the size to the default (configured) size,
 * the keyword <code>size</code> reports the current size and default (configured) size,
 * <code>&lthost&gt</code> is the target host and 
 * <code>&ltcp ref&gt</code> is the smartfrog reference for the connection pool.
 */
public class ConnectionPoolSizeImpl {
	
    private static final String usage = 
        "reduce|reset|size <host> <cp ref> ";

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
        
        /**
         * give help
         */
        if( args.length==1 && ( "help".equals(args[0]) || "-?".equals(args[0]) || "?".equals(args[0]) ) ) {
        	reportUsageAndExit(usage, ExitCodes.EXIT_CODE_SUCCESS);
        } 
        
        /**
         * incorrect number of arguments or bad op argument
         */
        if( args.length != 3 || !(args[0].equals("reduce") || args[0].equals("reset") || args[0].equals("size")) ) {
            reportUsageAndExit(usage, ExitCodes.EXIT_ERROR_CODE_BAD_ARGS);
        } 
        
        /**
         * Get the connection pool component
         */
        ConnectionPoolSize poolSize = null;
        try {
            Prim proc = (Prim) SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(args[1]));
			poolSize = (ConnectionPoolSize)proc.sfResolve(Reference.fromString(args[2]));
        } catch (ClassCastException e) {
            exitWithException(
                    "target component does not implement the connection pool size management interface",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
        } catch (Exception e) {
            exitWithException(
                    "to locate connection pool",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
        }

        /**
         * do the operation
         */
        try {
			if( "reduce".equals(args[0]) ) {
				if( poolSize.freeOneConnection() ) {
					reportSuccess("\n    pool size is " + poolSize.getCurrentMaxSize());
				} else {
					reportFailure("\n    pool may be too small or closed");
				}
			} else if( "reset".equals(args[0]) ) {
				poolSize.resetMaxSize();
				reportSuccess("\n    pool size is " + poolSize.getDefaultMaxSize());
			} else if("size".equals(args[0])) {
	        	StringBuffer buf = new StringBuffer(50);
				buf.append("\n").append("    Current max size is ").append(poolSize.getCurrentMaxSize());
				buf.append("\n").append("    Default max size is ").append(poolSize.getDefaultMaxSize());
				reportSuccess(buf.toString());
			}
		} catch (RemoteException e) {
			exitWithException(
                    "to perform operation",
                    ExitCodes.EXIT_ERROR_CODE_GENERAL,
                    e);
		} 
    }
    
    static private void reportSuccess(String msg) {
    	System.out.println("\n - SUCCESS " + msg + "\n");
    	System.exit(ExitCodes.EXIT_CODE_SUCCESS);
    }
    
    static private void reportFailure(String msg) {
    	System.out.println("\n - FAILED " + msg);
    	System.exit(ExitCodes.EXIT_ERROR_CODE_GENERAL);
    }
    
    
    static private void reportUsageAndExit(String msg, int errorCode) {
        if( msg != null ) {
            System.out.println("\n - Usage: " + msg);
        }
        System.exit(errorCode);        
    }
    
        
    static private void exitWithException(String msg, int errorCode, Exception e) {

        StringBuffer buf = new StringBuffer();
        buf.append("\n - FAILED " + msg);
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

}
