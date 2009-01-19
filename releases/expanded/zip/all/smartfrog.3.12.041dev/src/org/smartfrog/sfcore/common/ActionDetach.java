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
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;

/**
 * Detach a component
 */
public class ActionDetach extends ConfigurationAction{

    /**
      * Detaches appName from component target
      *
      * @param name name of the application
      * @param targetP the target process compound to request deployment
      * @return Prim Reference to detached component
      * @throws SmartFrogException failure in some part of the process
      * @throws RemoteException In case of network/rmi error
      */
     public static Prim Detach(String name, ProcessCompound targetP)  throws SmartFrogException,
            RemoteException {
            //First thing first: system gets initialized
            //Protect system if people use this as entry point
            try {
                SFSystem.initSystem();
            } catch (Exception ex) {
                throw SmartFrogException.forward(ex);
            }

            try {
                Prim targetC = (Prim)targetP.sfResolveWithParser(name);
                targetC.sfDetach();
                return targetC;
            } catch (Throwable thr) {
                throw SmartFrogException.forward(thr);
            }
     }



    /**
     * this has to be implemented by subclasses; execute a configuration command against
     * a specified target
     *
     * @param targetP   target where to execute the configuration command
     * @param configuration   configuration command to be executed
     * @return Object Reference to detached component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public Object execute(ProcessCompound targetP,
                          ConfigurationDescriptor configuration) throws SmartFrogException,
            RemoteException {
            Prim result=null;
            try {
                if (targetP==null)
                    targetP = SFProcess.sfSelectTargetProcess(configuration.getHost(),configuration.getSubProcess());
                    result = Detach(configuration.getName(), targetP);
            } catch (SmartFrogException sex){
                 configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,sex);
                 throw sex;
             } catch (RemoteException rex){
                 configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,rex);
                 throw rex;
            }
            configuration.setSuccessfulResult();
            return result;

    }
}
