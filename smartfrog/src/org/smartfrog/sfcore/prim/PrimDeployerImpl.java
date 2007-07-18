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

package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.deployer.ComponentDeployer;
import org.smartfrog.sfcore.deployer.PrimFactory;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import java.util.Enumeration;


/**
 * This class implements the deployment semantics for primitives. This means
 * looking up the sfClass attribute and creating an instance of that class.
 * After this the rest of the deployment is left to the instance. The deployer
 * implements the ComponentDeployer interface.
 *
 */
public class PrimDeployerImpl extends PrimImpl implements ComponentDeployer, MessageKeys {

    /** The target description to work off. */
    protected ComponentDescription target;

    /** The factory used to get the component instance. */
    private PrimFactory primFactory;


    public void setComponentFactory(PrimFactory primFactory) {
        this.primFactory = primFactory;
    }

    public void setTargetComponentDescription(ComponentDescription target) {
        this.target = target;
    }

    /**
     * Does the basic deployment. The instance created and the deployment
     * forwarded to the primitive. Subclasses can override this to provide
     * different deployment implementations.
     *
     * @param parent parent for deployed component
     *
     * @return Prim
     *
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    protected Prim deploy(Prim parent) throws SmartFrogDeploymentException {
        Context cxt = null;

        try {
            // create instance
            Prim dComponent = primFactory.getComponent(target);

            // deploy component after wiping out the parentage of any
            // descriptions in the context. Prim is not a valid parent, so
            // lose the parent baggage
            cxt = target.sfContext();

            for (Enumeration e = cxt.keys(); e.hasMoreElements();) {
                Object value = cxt.get(e.nextElement());

                if (value instanceof ComponentDescription) {
                    ((ComponentDescription) value).setParent(null);
                }
            }

            cxt.put(SmartFrogCoreKeys.SF_CODE_REPOSITORY, new CodeRepositoryWrapper(primFactory.getCodeRepository()));

            dComponent.sfDeployWith(parent, cxt);

            return dComponent;
        } catch (SmartFrogException sfdex){
            throw ((SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfdex));
        } catch (Throwable t) {
            throw new SmartFrogDeploymentException("Error when trying to deploy component", t, null, cxt);
        }
    }

    //
    // ComponentDeployer
    //

    /**
     * Deploy target description for which this deployer was created. This
     * implementation resolves the given name, forwarding if non-null. In case
     * of forwarding the resulting component is deploy resolved. If the name
     * is null the parameters are added to the description. The description is
     * NOT type, place and deploy since this is expected from higher level
     * functionality. Deployement happens via the internal deploy method
     *
     * @param name name of contained description to deploy (can be null)
     * @param parent parent for deployed component
     * @param params parameters for description
     *
     * @return Prim
     *
     * @exception SmartFrogDeploymentException failed to deploy description
     */
    public Prim deploy(Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException {
        // add parameters
        if (params != null) {
            for (Enumeration e = params.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                try {
                  target.sfReplaceAttribute(key, params.get(key));
                } catch (SmartFrogRuntimeException ex) {
                  throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(ex);
                }
            }
        }
        return deploy(parent);
    }

    // BEGIN LEGACY CODE //////////////
    // This is now in OldAlgorithmClassLoadingEnvironment, but DNSComponentDeployerImpl needs it here.
    // Should be removed very soon.


    private LogSF sfLog = LogFactory.sfGetProcessLog();


    /**
     * Efficiency holder of sfClass reference.
     */
    private static final Reference refClass = new Reference(
            SmartFrogCoreKeys.SF_CLASS);

    /**
     * Efficiency holder of sfCodeBase reference.
     */
    private static final Reference refCodeBase = new Reference(
            SmartFrogCoreKeys.SF_CODE_BASE);


    /**
     * Get the class for the primitive to be deployed. This is where the
     * sfClass attribute is looked up, using the classloader returned by
     * getPrimClassLoader
     *
     * @return class for target
     * @throws Exception failed to load class
     * @deprecated This is now in OldAlgorithmClassLoadingEnvironment
     */
    protected Class getPrimClass() throws SmartFrogResolutionException, SmartFrogDeploymentException {
        String targetCodeBase = null;
        String targetClassName;
        Object obj = null;
        try {

            targetCodeBase = getSfCodeBase(target);
            targetClassName = (String) target.sfResolve(refClass);

            // 3rd parameter = true: We look in the default code base if everything else fails.
            return SFClassLoader.forName(targetClassName, targetCodeBase, true);

        } catch (SmartFrogResolutionException resex) {
            resex.put(SmartFrogRuntimeException.SOURCE, target.sfCompleteName());
            resex.fillInStackTrace();

            throw resex;
        } catch (ClassCastException ccex) {
            throw new SmartFrogDeploymentException(refClass, null, getProcessComponentName(), target,
                    null, "Wrong class when resolving '" + refClass + "': '"
                    + obj + "' (" + obj.getClass().getName() + ")", ccex, targetCodeBase);
        } catch (ClassNotFoundException cnfex) {
            ComponentDescription cdInfo = new ComponentDescriptionImpl(null, new ContextImpl(), false);
            try {
                if (targetCodeBase != null) cdInfo.sfAddAttribute(SmartFrogCoreKeys.SF_CODE_BASE,
                        targetCodeBase);
                cdInfo.sfAddAttribute("java.class.path", System.getProperty("java.class.path"));
                cdInfo.sfAddAttribute("org.smartfrog.sfcore.processcompound.sfProcessName",
                        System.getProperty("org.smartfrog.sfcore.processcompound.sfProcessName"));
            } catch (SmartFrogException sfex) {
                if (sfLog.isDebugEnabled()) sfLog.debug("", sfex);
            }
            throw new SmartFrogDeploymentException(refClass, null, getProcessComponentName(), target, null, "Class not found", cnfex, cdInfo);
        }
    }

    protected final Object getProcessComponentName() throws SmartFrogResolutionException {
        return target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false);
    }

    /**
     * Gets the class code base by resolving the sfCodeBase attribute in the
     * given description.
     *
     * @param desc Description in which we resolve the code base.
     * @return class code base for that description.
     */
    private String getSfCodeBase(ComponentDescription desc) {
        try {
            return (String) desc.sfResolve(refCodeBase);
        } catch (Exception e) {
            return null;
        }
    }

    // END LEGACY CODE ////////////////
}
