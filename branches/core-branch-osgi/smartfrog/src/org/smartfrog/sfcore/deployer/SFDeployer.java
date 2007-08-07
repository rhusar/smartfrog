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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.rmi.RemoteException;


/**
 * Access point to the deployer infrastructure. At this point,
 * it simply uses either the default deployer or the sfDeployerClass provided
 * as part of the component description that is to be deployed.
 */
public class SFDeployer implements MessageKeys {

    private static final ComponentDeployer DEFAULT_DEPLOYER = new PrimProcessDeployerImpl();
    private static final PrimFactory DEFAULT_PRIM_FACTORY = new DefaultPrimFactory();
    private static final Reference PARENT_APP_ENV_REF;
    private static final CoreClassesClassLoadingEnvironment DEFAULT_CL_ENVIRONMENT = new CoreClassesClassLoadingEnvironment();

    static {
        PARENT_APP_ENV_REF = new Reference(ReferencePart.parent());
        PARENT_APP_ENV_REF.addElement(ReferencePart.here(SmartFrogCoreKeys.SF_APPLICATION_ENVIRONMENT));
    }

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

                if (!(tmp instanceof ComponentDescription))
                    SmartFrogResolutionException.notComponent(name, component.sfCompleteName());

                return deploy((ComponentDescription) tmp, null, parent, params);
            }

            return getDeployer(component).deploy(name, parent, params);

        } catch (SmartFrogRuntimeException sfex){
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
     * @throws SmartFrogRuntimeException failed to construct target deployer
     * @see PrimProcessDeployerImpl
     */
    private static ComponentDeployer getDeployer(ComponentDescription component)
            throws SmartFrogRuntimeException
    {
        String className = (String) component.sfResolveHere(SmartFrogCoreKeys.SF_DEPLOYER_CLASS, false);
        if (className != null)
            return oldDeployerSyntax(className, component);        
        else
            return newDeployerSyntax(component);
    }

    private static ComponentDeployer newDeployerSyntax(ComponentDescription component)
            throws SmartFrogRuntimeException
    {
        ComponentDeployer deployer;
        Reference deployerRef = (Reference) component.sfResolveHere(SmartFrogCoreKeys.SF_DEPLOYER, false);
        if (deployerRef != null) {

            try {
                deployer = (ComponentDeployer) component.sfResolve(deployerRef);
            } catch (ClassCastException e) {
                throw wrongType("The " + SmartFrogCoreKeys.SF_DEPLOYER 
                                + " attribute must be a ComponentDeployer.", e, component);
            }

            removeSfDeployerAttribute(component);
            
        } else {
            deployer = DEFAULT_DEPLOYER;
        }

        propagateApplicationEnvironmentAttribute(component);

        prepareDeployer(deployer, component);

        return deployer;
    }

    private static void removeSfDeployerAttribute(ComponentDescription component) throws SmartFrogResolutionException {
        try {
            /*
           TODO: Remove hack by separating the process compound location feature from the ComponentDeployer interface.
           This is necessary because the newly created process compound gets passed down the ComponentDescription again,
           and ends up calling this a second time. So this time the deployer resolves to the one in the parent process,
           through RMI - which is not what we want. If the deployer is only used to find a ProcessCompound,
           it even doesn't make any sense.
            */
            component.sfRemoveAttribute(SmartFrogCoreKeys.SF_DEPLOYER);
        } catch (SmartFrogRuntimeException e) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(e);
        }
    }

    /**
     * Propagate the application environment reference down the component tree, if present.
     * If not there, the application does not have an environment, meaning it only uses things from the core.
     * This is especially the case for sfDefault, but could also be a simple app with no user provided code.
     * @param component
     * @throws SmartFrogRuntimeException
     */
    private static void propagateApplicationEnvironmentAttribute(ComponentDescription component)
            throws SmartFrogRuntimeException
    {
        Prim appEnv = (Prim) component.sfResolve(PARENT_APP_ENV_REF, false);
        if (appEnv != null) try {
            component.sfReplaceAttribute(SmartFrogCoreKeys.SF_APPLICATION_ENVIRONMENT, appEnv.sfCompleteName());
        } catch (RemoteException e) {
            throw new SmartFrogRuntimeException
                    ("The application environment should not be remote. It is, and a network problem occurred.", e);
        }
    }

    private static ComponentDeployer oldDeployerSyntax(String className, ComponentDescription component)
            throws SmartFrogRuntimeException
    {        
        try {
            
            ComponentDeployer deployer = (ComponentDeployer)
                    Class.forName(className).newInstance();
            prepareDeployer(deployer, component);
            return deployer;

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
        }
    }

    private static void prepareDeployer(ComponentDeployer deployer, ComponentDescription component) throws SmartFrogRuntimeException {
        deployer.setComponentFactory(getComponentFactory(component));
        deployer.setTargetComponentDescription(component);
    }

    /**
     * Retrieves the PrimFactory to be used from the ComponentDescription.
     * The PrimFactory is configured with the relevant ClassLoadingEnvironment before being returned.
     * @param component The description to be read.
     * @return A ready-to-use PrimFactory.
     * @throws SmartFrogResolutionException If the required attributes are missing in the description.
     */
    private static PrimFactory getComponentFactory(ComponentDescription component)
            throws SmartFrogRuntimeException
    {
        ComponentDescription metadata;
        try {
            metadata = (ComponentDescription) component.sfResolveHere(SmartFrogCoreKeys.SF_METADATA, false);
        } catch (ClassCastException e) {
            throw wrongType("The " + SmartFrogCoreKeys.SF_METADATA
                    + " attribute must be a component description.", e, component);
        }

        // Component not using the new sfMeta syntax. We'll resolve directly in component
        if (metadata == null) metadata = component;
            
        PrimFactory factory = resolveFactory(metadata);
        factory.setClassLoadingEnvironment(resolveEnvironment(metadata));

        return factory;
    }

    private static PrimFactory resolveFactory(ComponentDescription cd) throws SmartFrogResolutionException {
        final Reference factoryRef = (Reference)
                cd.sfResolveHere(SmartFrogCoreKeys.SF_FACTORY, false);
        if (factoryRef == null) return DEFAULT_PRIM_FACTORY;
        else return (PrimFactory) cd.sfResolve(factoryRef);
    }

    public static ClassLoadingEnvironment resolveEnvironment(ComponentDescription cd) throws SmartFrogResolutionException {
        final ClassLoadingEnvironment env;
        final Object classLoadingEnvAttr = cd.sfResolveHere(SmartFrogCoreKeys.SF_CLASS_LOADING_ENVIRONMENT, false);
        if (classLoadingEnvAttr instanceof Reference) {
            // in a DATA block
            final Reference classLoadingEnvRef = (Reference) classLoadingEnvAttr;
            env = (ClassLoadingEnvironment) cd.sfResolve(classLoadingEnvRef);
        } else if (classLoadingEnvAttr instanceof ClassLoadingEnvironment) {
            // in a normal component description
            env = (ClassLoadingEnvironment) classLoadingEnvAttr;
        } else if (classLoadingEnvAttr instanceof ComponentDescription) {
            ComponentDescription block = (ComponentDescription) classLoadingEnvAttr;
            Reference ref = (Reference) block.sfResolveHere("ref");
            try {
                env = (ClassLoadingEnvironment) SFProcess.getProcessCompound().sfResolve(ref);
            } catch (RemoteException e) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(e);
            }
        } else {
            env = new CoreClassesClassLoadingEnvironment(); // funny business
        }
        assert env != null;
        return env;
    }

    private static SmartFrogResolutionException wrongType
            (String msg, ClassCastException e, ComponentDescription component)
    {
        return new SmartFrogResolutionException(
                msg + " Faulty description: "
                        + System.getProperty("line.separator")
                        + component,
                e);
    }

}
