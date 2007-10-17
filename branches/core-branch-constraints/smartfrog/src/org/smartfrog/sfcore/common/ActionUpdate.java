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
import org.smartfrog.sfcore.prim.Update;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.reference.Reference;


import java.rmi.RemoteException;

/**
 * Deploy a component
 */
public class ActionUpdate extends ConfigurationAction {
    public static final String KEY_LANGUAGE = "#language";


    /**
     * Parses and updates "sfConfig" from a resource to the target process
     * compound rethrows an exception if it fails, after trying to clean up.
     * This method will check if parent is a rootProcess and it so, it will
     * register "url" as a root component that will start its own liveness.
     *
     * @param url             URL of resource to parse
     * @param component       reference for the component to update.
     * @param deployReference reference to resolve in ComponentDescription.
     *                        If ref is null the whole result ComponentDescription is returned.
     * @return Prim Reference to deployed component
     * @throws SmartFrogException
     *                                  failure in some part of the process
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public static Update update(String url, Update component,
                                Context c, Reference deployReference) throws SmartFrogException, RemoteException {

        //First thing first: system gets initialized
        //Protect system if people use this as entry point
        try {
            org.smartfrog.SFSystem.initSystem();
        } catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }

        if (c == null) c = new ContextImpl();

        //select the language first from the context, then from the URL itself
        String language;
        language = (String) c.get(KEY_LANGUAGE);
        if (language == null) {
            language = url;
        }

        ComponentDescription cd;
        try {
            cd = ComponentDescriptionImpl.sfComponentDescription(url, language, null, deployReference);
        } catch (SmartFrogException sfex) {
            if (sfex instanceof SmartFrogUpdateException)
                throw sfex;
            else
                throw new SmartFrogUpdateException(
                        "update description " + url,
                        sfex);
        }

        component.sfUpdateComponent(cd);
        return component;
    }

    /**
     * Deploy Action.
     *
     * @param targetP       target where to execute the configuration command
     * @param configuration configuration command to be executed
     * @return Object Reference to deployed component
     * @throws SmartFrogException
     *                                  failure in some part of the process
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public Object execute(ProcessCompound targetP, ConfigurationDescriptor configuration)
            throws SmartFrogException, RemoteException {
        Update component = null;
        String name = null;
        Reference ref = null;
        Prim prim = null;
        try {
            name = configuration.getName();
            //Placement
            if (name != null) {
                try {
                    ref = Reference.fromString(name);
                } catch (SmartFrogResolutionException ex) {
                    throw new SmartFrogResolutionException(null,
                            targetP.sfCompleteName(),
                            MessageUtil.formatMessage(MessageKeys.
                                    MSG_ILLEGAL_REFERENCE)
                                    + " when parsing '" + name + "'");
                }
                try {
                    component = (Update) targetP.sfResolve(ref);
                } catch (ClassCastException e) {
                    throw new SmartFrogResolutionException(null,
                            targetP.sfCompleteName(),
                            MessageUtil.formatMessage(MessageKeys.
                                    MSG_ILLEGAL_REFERENCE)
                                    + " when parsing '" + name + "': must refer to an Updatable component");
                }
            } else {
                throw new SmartFrogResolutionException(null,
                        targetP.sfCompleteName(),
                        MessageUtil.formatMessage(MessageKeys.
                                MSG_ILLEGAL_REFERENCE)
                                + " when parsing '" + name + "': name cannot be left null");
            }

            prim = (Prim) update(configuration.getUrl(),
                    component,
                    configuration.getContext(),
                    configuration.getDeployReference());

        } catch (SmartFrogException sex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED, null, sex);
            throw sex;
        } catch (RemoteException rex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED, null, rex);
            throw rex;
        }
        configuration.setSuccessfulResult();
        return prim;
    }

}
