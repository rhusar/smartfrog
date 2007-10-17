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
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import java.rmi.RemoteException;

/**
 * Implements Diagnostics Action for a component
 */
public class ActionDiagnostics extends ConfigurationAction {


    /**
      * Gets and prints a diagnostics report from the target component
      *
      * @param prim component to get report from.
      * @return Diagnostics report to deployed component
      *
      * @exception SmartFrogException failure in some part of the process
      * @throws RemoteException In case of network/rmi error
      */
     public static ComponentDescription Diagnostics(Prim prim) throws SmartFrogException, RemoteException {

        //First thing first: system gets initialized
        //Protect system if people use this as entry point
        try {
            org.smartfrog.SFSystem.initSystem();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
        ComponentDescription cd = null;
        if (prim !=null) cd = prim.sfDiagnosticsReport();
        return cd;
     }

    /**
     * Deploy Action.
     *
     * @param targetP   target where to execute the configuration command
     * @param configuration   configuration command to be executed
     * @return Object Reference to component whose Diagnostics report is generated
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public Object execute(ProcessCompound targetP, ConfigurationDescriptor configuration)
       throws SmartFrogException, RemoteException {
       Prim prim = null;
       String name = null;
       Reference ref = null;
       ComponentDescription report = null;
       try {
           name = configuration.getName();
           //Placement
           if (name!=null) {
               try {
                   ref = Reference.fromString(name);
               } catch (SmartFrogResolutionException ex) {
                   throw new SmartFrogResolutionException(null,
                       targetP.sfCompleteName(),
                       MessageUtil.formatMessage(MessageKeys.MSG_ILLEGAL_REFERENCE)
                       +" when parsing '"+name+"'");
               }
               prim = (Prim)targetP.sfResolve(ref);
               report  = Diagnostics(prim);
           } else {
               throw new SmartFrogException("No valid target name provided for diagnostics");
           }
       } catch (SmartFrogException sex){;
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,sex);
            throw sex;
        } catch (RemoteException rex){
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,rex);
            throw rex;
       }
        if (report!=null) {
            configuration.setContextAttribute("diagnosticsReport",report);
        } else{
            configuration.setContextAttribute("diagnosticsReport"," - Report empty -");
        }
        configuration.setSuccessfulResult();
        return report;
    }

}
