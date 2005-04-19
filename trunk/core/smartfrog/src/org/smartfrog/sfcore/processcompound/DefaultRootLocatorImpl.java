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

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.NotBoundException;
import java.rmi.AccessException;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;




/**
 * Defines a default root locator for SmartFrog Processes. The root locator
 * knows how to set a process compound to be the root of a host, as well as
 * the method on how to get the root process compound on a given host and
 * port. This implementation uses the rmi registry to set the root process
 * compound in. Root Locators should not allow multiple process compounds to
 * set themselves as root.
 *
 */
public class DefaultRootLocatorImpl implements RootLocator, MessageKeys {

 /**
  * A new thread is needed to bind/unbind the register. Otherwise, since
  * the stack typically involves an RMI call, the register
  *  will not let us do the unbind/bind (no remote modifications allowed)
  *
  */
 private static class AsyncResetProcessCompound extends Thread {

     private ProcessCompound pc;

     private SmartFrogException ex = null;

     private boolean bind = false;

     /**
      * A new thread is needed to bind/unbind the register. Otherwise, since
      * the stack typically involves an RMI call, the register
      *  will not let us do the unbind/bind (no remote modifications allowed)
      *  If unbinding then PC can be null.
      *  How to use it (ex: binding = true):
      *      AsyncResetProcessCompound depThr = new AsyncResetProcessCompound(pc, true);
      *      depThr.start();
      *      // It holds the lock on SFProcess.class until the thread terminates
      *     depThr.join();
      *     // it re-throws exceptions in the thread here...
      *     return depThr.getProcessCompound(); or depThr.getProcessCompound();
      *
      * @param pc ProcessCompound
      * @param bind boolean
      */
     public AsyncResetProcessCompound(ProcessCompound pc, boolean bind) {
         this.pc = pc;
         this.bind = bind;
     }

     public void run() {
         try {
             if (bind){
                //Bind
                registry.bind(defaultName, pc);
             }else {
                //Unbind
                registry.unbind(defaultName);
             }
         } catch (Exception e) {
             // to be thrown in getProcessCompound
             String msg = "unbinding";
             if (bind) {msg = "binding";}
             ex = SmartFrogRuntimeException.forward("Exception while "+msg
                                             + "root ProcessCompound", e);
         }
     }

     /**
      * Gets the new Process compound or rethrows any exception that
      * happened during the reset.
      *
      * @return The new process compound.
      * @exception SmartFrogException if an error occurs during reset.
      */
     public ProcessCompound getProcessCompound()
         throws SmartFrogException {
         if (ex != null){
             throw ex;
         }
         return pc;
     }
    }



    /** Name under which the root process compound will name itself. */
    protected static String defaultName = "RootProcessCompound";

    /** Port for registry. */
    protected static int registryPort = -1;

     /** RMI Registry. */
    protected static Registry registry = null;

    /**
     * Constructs the DefaultRootLocatorImpl object.
     */
    public DefaultRootLocatorImpl() {
    }

    /**
     * Gets the port of RMI registry on which input process compound is running.
     *
     * @param c Instance of process compound
     *
     * @return port number
     *
     * @throws SmartFrogException fails to get the registry port
     * @throws RemoteException In case of network/rmi error
     */
    protected static int getRegistryPort(ProcessCompound c)
        throws SmartFrogException, RemoteException {
        Object portObj=null;
        try {
            if (registryPort==-1) {
                portObj = (c.sfResolveHere(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT,false));
                if (portObj==null) {
                    throw new SmartFrogResolutionException(
                        "Unable to locate registry port from ", c);
                }
                Number port = (Number)portObj;
                registryPort = port.intValue();
            }
        } catch (ClassCastException ccex){
            throw new SmartFrogResolutionException(
                "Wrong object for "+SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT
                +": "+portObj+", "+portObj.getClass().getName()+"", ccex, c);
        }

        return registryPort;
    }

    /**
     * Tries to make the requesting process compound the root of the entire
     * host. This might fail since another process compound has already done
     * this.
     *
     * @param c compound which wants to become root for machine
     *
     * @throws SmartFrogException could not create locator or bind compound
     * @throws RemoteException In case of network/rmi error
     *
     * @see #getRootProcessCompound
     */
    public synchronized void setRootProcessCompound(ProcessCompound c)
        throws SmartFrogException, RemoteException {

        registryPort = getRegistryPort(c);

        try {
            if (registry==null) {
                registry = SFSecurity.createRegistry(registryPort);
            }
            //registry.bind(defaultName, c);
            /**
             * Uses a new thread to bind/unbind the register. Otherwise, since
             * the stack typically involves an RMI call, the register
             *  will not let us do the unbind/bind (no remote modifications allowed)
             *
             */
             AsyncResetProcessCompound depThr = new AsyncResetProcessCompound(c, true);
             depThr.start();
             // It holds the lock on SFProcess.class until the thread terminates
             depThr.join();
             // it re-throws exceptions in the thread here...
             depThr.getProcessCompound();
        } catch (Throwable t) {
            if (t instanceof java.rmi.server.ExportException){
                throw new SmartFrogRuntimeException ( MessageUtil.formatMessage(MSG_ERR_SF_RUNNING) , t);

            }
            throw SmartFrogRuntimeException.forward(t);
        }
    }


    /**
     * Unbinds root process compound from local registry.
     *
     * @param c process compound to set as root
     *
     * @throws RemoteException if there is any network/rmi error
     * @throws SmartFrogRuntimeException if failed to unbind
     *
     */
    public synchronized void unbindRootProcessCompound()
        throws SmartFrogException, RemoteException{
       if (registry!=null) {
        try {
            //registry.unbind(defaultName);
            /**
             * Uses a new thread to bind/unbind the register. Otherwise, since
             * the stack typically involves an RMI call, the register
             *  will not let us do the unbind/bind (no remote modifications allowed)
             *
             */
             //Unbind (bindi=false)
             AsyncResetProcessCompound depThr = new AsyncResetProcessCompound(null, false);
             depThr.start();
             // It holds the lock on SFProcess.class until the thread terminates
             depThr.join();
             // it re-throws exceptions in the thread here...
             depThr.getProcessCompound();
        } catch (Exception ex) {
          throw SmartFrogRuntimeException.forward(ex);
        }
       }

    }


    /**
     * Gets the root process compound for a given host. If the passed host is
     * null the root process compound for the local host is looked up. Checks
     * if the local process compound is equal to the requested one, and
     * returns the local object instead of the stub to avoid all calls going
     * through RMI
     *
     * @param hostAddress host to look up root process compound
     *
     * @return the root process compound on given host
     *
     * @throws Exception if error locating root process compound on host
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress hostAddress)
        throws Exception {

        return getRootProcessCompound (hostAddress, -1);
    }

    /**
     * Gets the root process compound for a given host on a specified port. If
     * the passed host is null the root process compound for the local host is
     * looked up. If the passed port number is negative (ex.-1) the default port number (3800)
     *  is  used. . Checks if the local process compound is equal to the
     * requested one, and returns the local object instead of the stub to
     * avoid all calls going through RMI
     *
     * @param hostAddress host to look up root process compound
     * @param portNum port to locate registry for root process conmpound if not
     *        default
     *
     * @return the root process compound on given host
     *
     * @throws Exception error locating root process compound on host
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress hostAddress,
        int portNum) throws Exception {

        ProcessCompound localCompound = SFProcess.getProcessCompound();

        if((localCompound == null) &&(portNum <=-1)) {
            throw new SmartFrogRuntimeException("No local process compound");
        }

        if (hostAddress == null) {
            hostAddress = InetAddress.getLocalHost();
        }

        if (portNum <= -1){
            portNum = getRegistryPort(localCompound);
        }

        if ((localCompound != null)&&
            hostAddress.equals(InetAddress.getLocalHost()) &&
              localCompound.sfIsRoot()) {
            return localCompound;
        }

        Registry reg = SFSecurity.getRegistry(hostAddress.getHostAddress(), portNum);

        ProcessCompound pc = (ProcessCompound) reg.lookup(defaultName);

        // Get rid of the stub if local
        if ((localCompound != null)&&(pc.equals(localCompound))) {
            return localCompound;
        }

        return pc;
    }
}
