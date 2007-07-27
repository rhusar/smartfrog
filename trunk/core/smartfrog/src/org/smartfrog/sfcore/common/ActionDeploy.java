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
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.compound.Compound;


import java.rmi.RemoteException;
import java.util.Date;

/**
 * Deploy a component
 */
public class ActionDeploy extends ConfigurationAction {

    /**
     * Parses, deploys and starts "sfConfig" from a resource to the target process compound rethrows an exception if it fails,
     * after trying to clean up. This method will check if parent is a rootProcess and it so, it will register "url" as
     * a root component that will start its own liveness.
     *
     * @param url             URL of resource to parse
     * @param appName         name of the application
     * @param parent          parent for the new component. If null if will use 'target'.
     * @param target          the target process compound to request deployment
     * @param c               a context of additional attributes that should be set before deployment
     * @param deployReference reference to resolve in ComponentDescription. If ref is null the whole result
     *                        ComponentDescription is returned.
     * @return Prim Reference to deployed component
     * @throws SmartFrogException failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public static Prim Deploy(String url, String appName, Prim parent, Compound target,
                              Context c, Reference deployReference) throws SmartFrogException, RemoteException {
        return Deploy(url,appName,parent,target,c,deployReference,true);
    }

    /**
     * Parses,  deploys and optionally starts "sfConfig" from a resource to the target process
     * compound rethrows an exception if it fails, after trying to clean up.
     * This method will check if parent is a rootProcess and it so, it will
     * register "url" as a root component that will start its own liveness.
     *
     * @param url URL of resource to parse
     * @param appName name of the application
     * @param parent parent for the new component. If null if will use 'target'.
     * @param target the target process compound to request deployment
     * @param context a context of additional attributes that should be set before
     *        deployment
     * @param deployReference  reference to resolve in ComponentDescription.
     *        If ref is null the whole result ComponentDescription is returned.
     * @param start flag to set to true to start the component after deploying it by calling sfDeploy and sfStart
     * @return Prim Reference to deployed component
     *
     * @throws SmartFrogException failure in some part of the process
     * @throws RemoteException In case of network/rmi error
     */
    protected static Prim Deploy(String url, String appName, Prim parent, Compound target,
                              Context context, Reference deployReference, boolean start) throws SmartFrogException, RemoteException {

        //First thing first: system gets initialized
        //Protect system if people use this as entry point
        try {
            org.smartfrog.SFSystem.initSystem();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }


        Prim comp = null;
        //To calculate how long it takes to deploy a description
        long beginTime;
        long deployTime = 0;
        long startTime = 0;
        long parseTime;

        beginTime = System.currentTimeMillis();
        if (context == null) {
            context = new ContextImpl();
        }

        // Checks if 'parent' is a processCompound. If parent is a process compound
        // the parentage is made null and it is registered as an attribute, not a
        // child, so it is a root component and starts is own liveness
        if ((parent != null) && (parent instanceof ProcessCompound)) {
            // This component will be a root component
            parent = null;

        } else if ((parent != null) && (parent instanceof Compound) && (appName == null)) {
            //From ProcessCompoundImpl. Creates  name for unnamed components...
//             appName = SmartFrogCoreKeys.SF_UNNAMED + (new Date()).getTime() + "_" +
//                ProcessCompoundImpl.registrationNumber++;

        }
        // This is needed so that the root component is properly named
        // when registering with the ProcessCompound
        if ((parent == null) && (appName != null)) {
            context.put("sfProcessComponentName", appName);
        }

        // The processCompound/Compound is used to do the deployment!
        if ((parent != null) && (parent instanceof Compound)) {
            target = (Compound) parent;
        }

        //select the language first from the context, then from the URL itself
        String language;
        language=(String) context.get(SmartFrogCoreKeys.KEY_LANGUAGE);
        if(language==null) {
            language=url;
        }


        ComponentDescription cd;
        try {
            cd = ComponentDescriptionImpl.sfComponentDescription(url, language, null, deployReference);
            parseTime = System.currentTimeMillis();

        } catch (SmartFrogException sfex) {
            if (sfex instanceof SmartFrogDeploymentException) {
                throw sfex;
            } else {
                throw new SmartFrogDeploymentException(
                        "deploying description '" + url + "' for '" + appName + "'",
                        sfex,
                        comp,
                        context);
            }
        }
        try {
             comp = target.sfDeployComponentDescription(appName, parent, cd, context);
            if (start) {
                try {
                    comp.sfDeploy();
                    deployTime = System.currentTimeMillis();
                } catch (Throwable thr) {
                    if (thr instanceof SmartFrogLifecycleException) {
                        throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thr);
                    }
                    throw SmartFrogLifecycleException.sfDeploy("", thr, null);
                }

                try {
                    comp.sfStart();
                    startTime = System.currentTimeMillis();
                } catch (Throwable thr) {
                    if (thr instanceof SmartFrogLifecycleException) {
                        throw (SmartFrogLifecycleException) thr;
                    }
                    throw SmartFrogLifecycleException.sfStart("", thr, null);
                }
            }
         } catch (Throwable e) {
             //if the component is non null, get the name of the component
             //and then terminate it abnormally
             if (comp != null) {
                 Reference compName = null;
                 try {
                     compName = comp.sfCompleteName();
                 }
                 catch (Exception ignored) {
                 }
                 try {
                     comp.sfTerminate(TerminationRecord.abnormal("Deployment Failure: " + e, compName));
                 } catch (Exception ignored) {
                 }
             }
             throw (SmartFrogException.forward(e));
       }

        //finally, attach times
        addAttributeQuietly(comp, SmartFrogCoreKeys.SF_TIME_STARTED_AT, new Date(beginTime).toString());
        addTime(comp, SmartFrogCoreKeys.SF_TIME_PARSE, parseTime - beginTime);
        addTime(comp, SmartFrogCoreKeys.SF_TIME_DEPLOY, deployTime -parseTime);
        addTime(comp, SmartFrogCoreKeys.SF_TIME_START, startTime - deployTime);
        return comp;
     }

    /**
     * Add a time to the component; ignore any exceptions
     * @param comp compoent to add
     * @param name key name
     * @param time time to add; if <=0 the attribute is not added
     */
    private static void addTime(Prim comp, String name, long time) {
        if (time >= 0) {
            addAttributeQuietly(comp, name, new Long(time));
        }
    }

    /**
     * Add an an attribute, do not report any problems
     * @param comp component
     * @param name attribute name
     * @param value object to add
     */
    private static void addAttributeQuietly(Prim comp, String name, Object value) {
        try {
            comp.sfAddAttribute(name, value);
        } catch (SmartFrogRuntimeException ignored) {

        } catch (RemoteException ignored) {

        }
    }

    /**
     * Deploy Action.
     *
     * @param targetP   target where to execute the configuration command
     * @param configuration   configuration command to be executed
     * @return Object Reference to deployed component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     *
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

           prim = doDeploy(configuration, name, parent, targetP);

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

    /**
     * Override point; call the deployment operations
     * @param configuration configuration to deploy
     * @param name name of the component
     * @param parent parent flag
     * @param targetP target
     * @return the deployed prim
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for network problems
     */
    protected Prim doDeploy(ConfigurationDescriptor configuration, String name, Prim parent, ProcessCompound targetP) throws SmartFrogException, RemoteException {
        Prim prim;
        prim = Deploy(configuration.getUrl(),
                           name,
                           parent,
                           targetP,
                           configuration.getContext(),
                           configuration.getDeployReference(),
                getStartFlag(configuration));
        return prim;
    }

    /**
     * Override point: get the start flag for this configuration. The default always returns true.
     * @param configuration the configuration which is being deployed
     * @return true if the configuration should start.
     */
    protected boolean getStartFlag(ConfigurationDescriptor configuration) {
        return true;
    }

}
