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

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;

/**
 * Detach and Terminate a component
 */
public class ActionDetachAndTerminate extends ConfigurationAction{

    /**
     * Error text when there is no application to terminate.
     * {@value}
     */
    public static final String ERROR_NO_APP_NAME = "No application name provided";


    /**
     * Detaches and Terminates name from component targetP
     *
     * @param name name of the application
     * @param targetP the target process compound to request deployment
     * @return Reference to detached component
     * @exception SmartFrogException failure in some part of the process
     * @throws RemoteException In case of network/rmi error
     */
    public static Prim DetachAndTerminate(String name, ProcessCompound targetP) throws
        SmartFrogException, RemoteException {
        //First thing first: system gets initialized
        //Protect system if people use this as entry point
        try {
            SFSystem.initSystem();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }

        if (name==null) {
            throw new SmartFrogException(ERROR_NO_APP_NAME);
        }
        Prim targetC = (Prim)targetP.sfResolveWithParser(name);
        boolean isRootProcess = false;
        try {
            if (targetC instanceof ProcessCompound) {
                isRootProcess = ((ProcessCompound)targetC).sfIsRoot();
            }
            try {
                targetC.sfDetachAndTerminate(TerminationRecord.normal("External Management Action",
                    targetP.sfCompleteName()));
            } catch (RemoteException ex) {
                HandleTerminationException(ex, isRootProcess);
            }
            return targetC;
        } catch (Throwable thr) {
            throw SmartFrogException.forward(thr);
        }

    }

    /**
     * Detach and Terminate action
     *
     * @param targetP   target where to execute the configuration command
     * @param configuration   configuration command to be executed
     * @return Object Reference to detached and terminated component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
     public Object execute(ProcessCompound targetP,
                           ConfigurationDescriptor configuration) throws SmartFrogException,
             RemoteException {
         Prim targetC=null;
         try {
             if (targetP==null)
                 targetP =
                     SFProcess.sfSelectTargetProcess(configuration.getHost(),
                     configuration.getSubProcess());
             targetC = DetachAndTerminate(configuration.getName(), targetP);
         } catch (SmartFrogException sex){
              configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,sex);
              throw sex;
          } catch (RemoteException rex){
              configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,rex);
              throw rex;
         }
         configuration.setSuccessfulResult();
         return targetC;
     }

}
