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
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;


public class ActionDetachAndTerminate extends ConfigurationAction{


    /**
       * Detaches and Terminates name from component targetP
       *
       * @param appName name of the application
       * @param target the target process compound to request deployment
       * @return Reference to detached component
       * @exception SmartFrogException failure in some part of the process
       * @throws RemoteException In case of network/rmi error
       */
       public static Prim DetachAndTerminate(String name, ProcessCompound targetP)
             throws SmartFrogResolutionException, RemoteException  {
          Prim targetC=(Prim) targetP.sfResolveWithParser(name);
          boolean isRootProcess = false;
          if (targetC instanceof ProcessCompound) {
             isRootProcess = ((ProcessCompound)targetC).sfIsRoot();
          }
          try {
              targetC.sfDetachAndTerminate(new TerminationRecord(TerminationRecord.NORMAL,
                    "External Management Action",
                    targetP.sfCompleteName()));
          } catch (RemoteException ex) {
              HandleTerminationException(ex, isRootProcess);
          }
          return targetC;
      }


     /**
      * Detach and Terminate action
      *
      * @param targetP       target process
      * @param configuration
      */
     public Object execute(ProcessCompound targetP,
                           ConfigurationDescriptor configuration) throws SmartFrogException,
             RemoteException {
         if (targetP==null) targetP=
            SFProcess.sfSelectTargetProcess(configuration.host,configuration.subProcess);
         Prim targetC = DetachAndTerminate(configuration.name,targetP);
         configuration.setSuccessfulResult();
         return targetC;
     }

}
