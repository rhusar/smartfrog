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
import org.smartfrog.sfcore.processcompound.ProcessCompoundImpl;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.compound.Compound;


import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Date;


public class ActionDeploy extends ConfigurationAction {


    /**
      * Parses and deploys "sfConfig" from a resource to the target process
      * compound rethrows an exception if it fails, after trying to clean up.
      * This method will check if parent is a rootProcess and it so, it will
      * register "url" as a root component that will start its own liveness.
      *
      * @param url URL of resource to parse
      * @param appName name of the application
      * @param parent parent for the new component. If null if will use 'target'.
      * @param target the target process compound to request deployment
      * @param c a context of additional attributes that should be set before
      *        deployment
      * @return Reference to deployed component
      *
      * @exception SmartFrogException failure in some part of the process
      * @throws RemoteException In case of network/rmi error
      */
     public static Prim Deploy(String url, String appName,Prim parent, Compound target,
         Context c, Reference deployReference) throws SmartFrogException, RemoteException {

        //First thing first: system gets initialized
        //Protect system if people use this as entry point
        try {
            org.smartfrog.SFSystem.initSystem();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }


         Prim comp = null;
         Phases top;
         //To calculate how long it takes to deploy a description
         long deployTime = 0;
         long parseTime = 0;

//         if (Logger.logStackTrace) {
             deployTime = System.currentTimeMillis();
//         }
         if (c==null) c = new ContextImpl();

           // Checks if 'parent' is a processCompound. If parent is a process compound
           // the parentage is made null and it is registered as an attribute, not a
           // child, so it is a root component and starts is own liveness
           if ((parent!=null)&&(parent instanceof ProcessCompound)){
               // This component will be a root component
               parent=null;

           } else if ((parent!=null)&&(parent instanceof Compound)&&(appName==null)){
             //From ProcessCompoundImpl. Creates  name for unnamed components...
//             appName = SmartFrogCoreKeys.SF_UNNAMED + (new Date()).getTime() + "_" +
//                ProcessCompoundImpl.registrationNumber++;

          }
          // This is needed so that the root component is properly named
          // when registering with the ProcessCompound
          if ((parent==null)&&(appName!=null)) c.put("sfProcessComponentName", appName);

              // The processCompound/Compound is used to do the deployment!
          if ((parent!=null)&&(parent instanceof Compound)){
            target = (Compound)parent;
          }


         try {
             ComponentDescription cd;
             try {
                 cd = ComponentDescriptionImpl.sfComponentDescription(url,null,deployReference);
//                 if (Logger.logStackTrace) {
                     parseTime = System.currentTimeMillis()-deployTime;
                     deployTime = System.currentTimeMillis();
//                 }
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
             comp = target.sfDeployComponentDescription(appName, parent, cd, c);
             //comp = target.sfDeployComponentDescription(null, parent, cd, c);
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

//       if (Logger.logStackTrace) {
           deployTime = System.currentTimeMillis()-deployTime;
           try {
               comp.sfAddAttribute("sfParseTime",new Long(parseTime));
               comp.sfAddAttribute("sfDeployTime",new Long(deployTime));
           } catch (Exception ex){
             //ignored, this is only information
           }
//       }
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
       Prim parent = null;
       String name = null;
       Reference ref = null;
       Prim prim=null;
       try {
           name = configuration.getName();
           //Placement
           if (name!=null) {
               try {
                   ref = Reference.fromString(name);
               } catch (SmartFrogResolutionException ex) {
                   throw new SmartFrogResolutionException(null,
                       targetP.sfCompleteName(),
                       MessageUtil.formatMessage(MessageKeys.
                                                 MSG_ILLEGAL_REFERENCE)
                       +" when parsing '"+name+"'");
               }

               if (ref.size()>1) {
                   ReferencePart refPart = ref.lastElement();
                   name = refPart.toString();
                   name = name.substring(
                       name.lastIndexOf(HereReferencePart.HERE+" ")+
                       HereReferencePart.HERE.length()+1);
                   ref.removeElement(refPart);
                   parent = (Prim)targetP.sfResolve(ref);
               }
           }

           prim = Deploy(configuration.getUrl(),
                              name,
                              parent,
                              targetP,
                              configuration.getContext(),
                              configuration.getDeployReference());

       } catch (SmartFrogException sex){
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,sex);
            throw sex;
        } catch (RemoteException rex){
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,null,rex);
            throw rex;
       }
        configuration.setSuccessfulResult();
        return prim;
    }

}
