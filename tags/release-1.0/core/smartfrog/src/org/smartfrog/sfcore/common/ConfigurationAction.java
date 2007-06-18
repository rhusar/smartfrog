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

import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;
import org.smartfrog.SFSystem;

/**
 * This code contains the methods to do things from configurations.
 * It is a factoring out of SFSystem.
 *
 */

public abstract class ConfigurationAction {

       /**
         * Select target process compound using host and subprocess names
         *
         * @param host host name. If null, assumes localhost.
         * @param subProcess subProcess name (optional; can be null)
         * @return ProcessCompound the target process compound
         * @throws SmartFrogException In case of SmartFrog system error
         * @throws RemoteException In case of network/rmi error
         */
        public static ProcessCompound selectTargetProcess(String host,
            String subProcess) throws SmartFrogException, RemoteException {
            return SFProcess.sfSelectTargetProcess(host,subProcess);
        }


    /**
     * this has to be implemented by subclasses; execute a configuration command against
     * a specified target.
     * This version looks up the target and notes if it was a root process or not.
     * then
     * @param targetP target process
     * @param configuration configuration command to be executed
     * @return Object Reference to component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public abstract Object execute(ProcessCompound targetP, ConfigurationDescriptor configuration)
            throws SmartFrogException, RemoteException;

    /**
     * Locate the target from the configuration, then call #execute() with
     * the target specified
     * this is an optional override point, giving the overrider the option of
     * using an alternate target mapping process
     *
     * @param configuration   configuration command to be executed
     * @return Object Reference to component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public Object execute(ConfigurationDescriptor configuration) throws SmartFrogException,
            RemoteException {
        ProcessCompound targetProcess;
        targetProcess = selectTargetProcess(configuration.getHost(), configuration.getSubProcess());
  //      assert targetProcess!=null;
        return execute(targetProcess,configuration);
    }

    /**
     * special handler for processing exceptions during termination;
     * socket failures are actually viewed as successful terminations, as they
     * are often the symptom of success.
     *
     * @param ex RemoteException to be handled
     * @param rootProcess  boolean indicating if the component is rootProcess  or not
     * @return boolean indiacating if action was successful or not.
     * @throws RemoteException  In case of network/rmi error
     *
     */
    protected static boolean HandleTerminationException(RemoteException ex,
                                            boolean rootProcess) throws RemoteException {
        if (!rootProcess)
            throw ex;
        //TODO: Check exception handling
        if ((ex.getCause() instanceof java.net.SocketException) ||
                (ex.getCause() instanceof java.io.EOFException)) {
            //Logger.log(MessageUtil.formatMessage(MessageKeys.MSG_SF_TERMINATED));
            //if (SFSystem.sfLog().isTraceEnabled()) {
            //  SFSystem.sfLog().trace(MessageUtil.formatMessage(MessageKeys.MSG_SF_TERMINATED));
            //}
            SFSystem.sfLog().out(MessageUtil.formatMessage(MessageKeys.MSG_SF_TERMINATED));
            return true;
        } else {
            //Logger.log(ex);
            if (SFSystem.sfLog().isTraceEnabled()) {
              SFSystem.sfLog().trace(ex);
            }
        }
        return false;
    }


}
