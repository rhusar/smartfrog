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
package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.net.InetAddress;
import java.rmi.RemoteException;

/**
 * This code contains the methods to do things from configurations.
 * It is a factoring out of SFSystem.
 *
 * @author steve loughran
 *         created 18-Mar-2004 10:10:05
 */

public abstract class ConfigurationAction {

    /**
     * Gets the ProcessCompound running on the host.
     *
     * @param hostName   Name of the host
     * @param remoteHost boolean indicating if the host is remote host
     * @return ProcessCompound
     */
    public static ProcessCompound getTargetProcessCompound(String hostName,
                                                           boolean remoteHost) throws SmartFrogException,
            RemoteException {
        ProcessCompound target = null;
        try {
            if (!remoteHost) {
                target = SFProcess.getProcessCompound();
            } else {
                target = SFProcess.getRootLocator().
                        getRootProcessCompound(InetAddress.getByName(hostName));
            }
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
        return target;
    }

    /**
     * Select target process compound using host and subprocess names
     *
     * @param host       host name. If null, assumes localhost.
     * @param subProcess subProcess name (optional; can be null)
     * @return ProcessCompound the target process compound
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ProcessCompound selectTargetProcess(String host,
                                                      String subProcess)
            throws SmartFrogException, RemoteException {
        ProcessCompound target = null;
        try {
            target = SFProcess.getProcessCompound();
            if (host != null) {
                target = SFProcess.getRootLocator().
                        getRootProcessCompound(InetAddress.getByName(host));
            }
            if (subProcess != null) {
                target = (ProcessCompound) target.sfResolveHere(subProcess);
            }
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
        return target;
    }


    /**
     * check for a process being root.
     * @param targetC
     * @return
     * @throws RemoteException
     */
    public boolean IsRootProcess(Prim targetC) throws RemoteException {
        if (targetC instanceof ProcessCompound) {
            if (((ProcessCompound) targetC).sfIsRoot()) {
                return true;
            }
        }
        return false;
    }

    /**
     * look up the named target for the action
     * @param targetP
     * @param cfgDesc
     * @return a resolved target
     * @throws SmartFrogResolutionException if the name is unknown
     * @throws RemoteException if something happened on the network
     */
    public Prim LookupTarget(Prim targetP, ConfigurationDescriptor cfgDesc) throws SmartFrogResolutionException,
            RemoteException {
        Prim targetC = null;
        targetC = (Prim) targetP.sfResolveWithParser(cfgDesc.name);
        return targetC;
    }


    /**
     * this has to be implemented by subclasses; execute a configuration command against
     * a specified target.
     * This version looks up the target and notes if it was a root process or not.
     * then
     * @param targetP target process
     * @param configuration
     */
    public abstract Object execute(ProcessCompound targetP, ConfigurationDescriptor configuration)
            throws SmartFrogException, RemoteException;

    /**
     * Locate the target from the configuration, then call #execute() with
     * the target specified
     * this is an optional override point, giving the overrider the option of
     * using an alternate target mapping process
     *
     * @param configuration
     */
    public Object execute(ConfigurationDescriptor configuration) throws SmartFrogException,
            RemoteException {
        ProcessCompound targetProcess;
        targetProcess = selectTargetProcess(configuration.host, configuration.subProcess);
        assert targetProcess!=null;
        return execute(targetProcess,configuration);
    }

    /**
     * special handler for processing exceptions during termination;
     * socket failures are actually viewed as successful terminations, as they
     * are often the symptom of success.
     * @param ex
     * @param rootProcess
     * @param configuration
     * @throws RemoteException
     */
    protected void HandleTerminationException(RemoteException ex,
                                            boolean rootProcess,
                                            ConfigurationDescriptor configuration) throws RemoteException {
        if (!rootProcess)
            throw ex;
        //TODO: Check exception handling
        if ((ex.getCause() instanceof java.net.SocketException) ||
                (ex.getCause() instanceof java.io.EOFException)) {
            Logger.log(MessageUtil.formatMessage(MessageKeys.MSG_SF_TERMINATED));
            configuration.setSuccessfulResult();
        } else {
            Logger.log(ex);
        }
    }


}
