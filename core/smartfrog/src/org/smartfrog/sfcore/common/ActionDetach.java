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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;
import java.rmi.RemoteException;


public class ActionDetach extends ConfigurationAction{

    /**
      * Detaches appName from component target
      *
      * @param appName name of the application
      * @param target the target process compound to request deployment
      * @return Reference to detached component
      * @exception SmartFrogException failure in some part of the process
      * @throws RemoteException In case of network/rmi error
      */
     public static Prim Detach(String name, ProcessCompound targetP)  throws SmartFrogException,
            RemoteException {
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
     * @param targetP
     * @param configuration
     */
    public Object execute(ProcessCompound targetP,
                          ConfigurationDescriptor configuration) throws SmartFrogException,
            RemoteException {
            if (targetP==null)
               targetP = SFProcess.sfSelectTargetProcess(configuration.getHost(),
                       configuration.getSubProcess());
            Prim result = Detach(configuration.getName(), targetP);
            configuration.setSuccessfulResult();
            return result;

    }
}
