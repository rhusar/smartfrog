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

package org.smartfrog.sfcore.deployer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDeployer;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;



/**
 * Access point to the deployer infrastructure. At this point,
 * it simply uses either the default deployer or the sfDeployerClass provided
 * as part of the component description that is to be deployed.
 *
 */
public class SFDeployer implements MessageKeys {
    /**
     * Name of default deployer.
     */
    private static final String DEFAULT_DEPLOYER =
            "org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl";

    /**
     * Name of default implementation of ComponentDescription.
     */
    private static final String COMPONENT_DESCRIPTION =
        "org.smartfrog.sfcore.componentdescription.ComponentDescription";


    //
    // ComponentDeployer
    //

    /**
     * Deploy description. Constructs the real deployer using getDeployer
     * method and forwards to it. If name is set, name is resolved on target,
     * the new target deploy resolved and deployment forwarded to the new
     * target
     *
     * @param component the description of the component to be deployed
     * @param name name of contained description to deploy (can be null)
     * @param parent parent for deployed component
     * @param params parameters for description
     *
     * @return Reference to component
     *
     * @throws SmartFrogDeploymentException In case failed to forward deployment
     * or deploy
     */
    public static Prim deploy(ComponentDescription component, Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException {
        try {
            // resolve name to description and deploy from there
            if (name != null) {
                Object tmp = component.sfResolve(name);

                if (!(tmp instanceof ComponentDescription)) {
                    SmartFrogResolutionException.notComponent(name, component.sfCompleteName());
                }

                return deploy((ComponentDescription) tmp, null, parent, params);
            }
            return getDeployer(component).deploy(name, parent, params);
        } catch (SmartFrogException sfex){
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(sfex);
        }
    }

    /**
     * Gets the real deployer for this description target. Looks up
     * sfDeployerClass. If not found. PrimProcessDeployerImpl is used. The
     * constructor used is the one taking a compnent description as an
     * argument
     *
     * @param component the component description to mine for the deployer information
     * @return deployer for target
     *
     * @throws SmartFrogException failed to construct target deployer
     * @see org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl
     */
    protected static ComponentDeployer getDeployer(ComponentDescription component) throws SmartFrogException {
        String className = (String) component.sfResolveHere(SmartFrogCoreKeys.SF_DEPLOYER_CLASS,false);

        if (className == null) {
            className = DEFAULT_DEPLOYER;
        }

        try {
            Class deplClass = SFClassLoader.forName(className);
            Class[] deplConstArgsTypes = { SFClassLoader.
                    forName(COMPONENT_DESCRIPTION) };
            Constructor deplConst = deplClass.
                                getConstructor(deplConstArgsTypes);
            Object[] deplConstArgs = { component };

            return (ComponentDeployer) deplConst.newInstance(deplConstArgs);
        } catch (NoSuchMethodException nsmetexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_METHOD_NOT_FOUND, className, "getConstructor()"),
                nsmetexcp, null, component.sfContext());
        } catch (ClassNotFoundException cnfexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND, className), cnfexcp, null, component.sfContext());
        } catch (InstantiationException instexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR, className), instexcp, null, component.sfContext());
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS, className, "newInstance()"), illaexcp,
                null, component.sfContext());
        } catch (InvocationTargetException intarexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INVOCATION_TARGET, className), intarexcp,
                null, component.sfContext());
        }
    }
}
