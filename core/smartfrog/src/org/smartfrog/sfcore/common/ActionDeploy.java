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
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;


public class ActionDeploy extends ConfigurationAction {


    /**
      * Parses and deploys "sfConfig" from a resource to the target process
      * compound rethrows an exception if it fails, after trying to clean up.
      *
      * @param url URL of resource to parse
      * @param appName name of the application
      * @param target the target process compound to request deployment
      * @param c a context of additional attributes that should be set before
      *        deployment
      * @return Reference to deployed component
      *
      * @exception SmartFrogException failure in some part of the process
      * @throws RemoteException In case of network/rmi error
      */
     public static Prim Deploy(String url, String appName, ProcessCompound target,
         Context c) throws SmartFrogException, RemoteException {
         Prim comp = null;
         Phases top;
         //To calculate how long it takes to deploy a description
         long deployTime = 0;
         long parseTime = 0;

         if (Logger.logStackTrace) {
             deployTime = System.currentTimeMillis();
         }
         if (c==null) c = new ContextImpl();
         if (appName!=null) c.put("sfProcessComponentName", appName);

         try {
             ComponentDescription cd;
             try {
                 cd = ComponentDescriptionImpl.sfComponentDescription(url);
                 if (Logger.logStackTrace) {
                     parseTime = System.currentTimeMillis()-deployTime;
                     deployTime = System.currentTimeMillis();
                 }
             } catch (SmartFrogException sfex) {
                 if (sfex instanceof SmartFrogDeploymentException)
                     throw sfex;
                 else
                     throw new SmartFrogDeploymentException(
                        "deploying description '"+url+"' for '"+appName+"'",
                        sfex,
                        comp,
                        c);
             }
             comp = target.sfDeployComponentDescription(null, null, cd, c);
             try {
                 comp.sfDeploy();
             } catch (Throwable thr){
                 if (thr instanceof SmartFrogLifecycleException){
                     throw (SmartFrogLifecycleException)
                         SmartFrogLifecycleException.forward(thr);
                 }
                 throw SmartFrogLifecycleException.sfDeploy("",thr,null);
             }
             try {
                 comp.sfStart();
             } catch (Throwable thr){
                 if (thr instanceof SmartFrogLifecycleException){
                     throw (SmartFrogLifecycleException)
                         SmartFrogLifecycleException.forward(thr);
                 }
                 throw SmartFrogLifecycleException.sfStart("",thr,null);
             }
         } catch (Throwable e) {
                if (comp != null) {
                   Reference compName = null;
                   try {
                       compName = comp.sfCompleteName();
                   }
                   catch (Exception ex) {
                   }
                   try {
                   comp.sfTerminate(TerminationRecord.
                            abnormal("Deployment Failure: " +
                                 e, compName));
                   } catch (Exception ex) {}
                }
                throw ((SmartFrogException) SmartFrogException.forward(e));
       }

       if (Logger.logStackTrace) {
           deployTime = System.currentTimeMillis()-deployTime;
           try {
               comp.sfAddAttribute("sfParseTime",new Long(parseTime));
               comp.sfAddAttribute("sfDeployTime",new Long(deployTime));
           } catch (Exception ex){
             //ignored, this is only information
           }
       }
       return comp;
     }

    /**
     * Deploy Action.
     *
     * @param targetP
     * @param configuration
     */
    public Object execute(ProcessCompound targetP, ConfigurationDescriptor configuration)
       throws SmartFrogException, RemoteException {
        Prim prim = Deploy(configuration.getUrl(), configuration.getName(), targetP, null);
        configuration.setSuccessfulResult();
        return prim;
    }

}
