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

import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDeployer;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;


/**
 * This class implements the deployment semantics for primitives. This means
 * looking up the sfClass attribute and creating an instance of that class.
 * After this the rest of the deployment is left to the instance. The deployer
 * implements the ComponentDeployer interface.
 *
 */
public class PrimDeployerImpl implements ComponentDeployer, MessageKeys {

    /** ProcessLog. This log is used to log into the core log: SF_CORE_LOG */
    private LogSF  sflog = LogFactory.sfGetProcessLog();

    /** Efficiency holder of sfClass reference. */
    protected static final Reference refClass = new Reference(
                SmartFrogCoreKeys.SF_CLASS);

    /** Efficiency holder of sfCodeBase reference. */
    protected static final Reference refCodeBase = new Reference(
                SmartFrogCoreKeys.SF_CODE_BASE);

    /** The target description to work of. */
    public ComponentDescription target;

    /**
     * Constructs a component deployer for given description.
     *
     * @param descr target description
     */
    public PrimDeployerImpl(ComponentDescription descr) {
        target = descr;
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
            final Class primClass = getPrimClass();
            Prim dComponent = createPrimInstance(primClass);

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

            dComponent.sfDeployWith(parent, cxt);

            return dComponent;
        } catch (InstantiationException instexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR, "Prim"), instexcp, null, cxt);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS, "Prim", "newInstance()"), illaexcp, null, cxt);
        } catch (SmartFrogException sfdex){
            throw ((SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfdex));
        } catch (Throwable t) {
            throw new SmartFrogDeploymentException(null, t, null, cxt);
       }
    }

    /**
     *  Create a instance of Prim from primClass
     * @param primClass
     * @return Prim instance
     * @throws Exception
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected Prim createPrimInstance(Class primClass) throws Exception {
        Prim dComponent = (Prim) primClass.newInstance();
        return dComponent;
    }

    /**
     * Gets the class code base by resolving the sfCodeBase attribute in the
     * given description.
     *
     * @param desc Description in which we resolve the code base.
     *
     * @return class code base for that description.
     */
    protected String getSfCodeBase(ComponentDescription desc) {
        try {
            return (String) desc.sfResolve(refCodeBase);
        } catch (Exception e) {
            // Not found, return null...
        }

        return null;
    }

    /**
     * Get the class for the primitive to be deployed. This is where the
     * sfClass attribute is looked up, using the classloader returned by
     * getPrimClassLoader
     *
     * @return class for target
     *
     * @exception Exception failed to load class
     */
    protected Class getPrimClass() throws Exception {
        String targetCodeBase=null;
        String targetClassName=null;
        Object obj=null;
        try {
            // extract code base
            targetCodeBase = getSfCodeBase(target);

            // extract class name
            obj =  target.sfResolve(refClass);
            targetClassName = (String) obj;

            // We look in the default code base if everything else fails.
            return SFClassLoader.forName(targetClassName,targetCodeBase, true);
        } catch (SmartFrogResolutionException resex) {
            resex.put(SmartFrogRuntimeException.SOURCE, target.sfCompleteName());
            resex.fillInStackTrace();

            throw resex;
        } catch (java.lang.ClassCastException ccex){
            Object name = null;
            if (target.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
                name =target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME,false);
            }
            throw new SmartFrogDeploymentException (refClass,null,name,target,
              null,"Wrong class when resolving '"+refClass+ "': '"
              +obj+"' ("+obj.getClass().getName()+")" , ccex, targetCodeBase);
        } catch (java.lang.ClassNotFoundException cnfex){
            Object name = null;
            if (target.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
                name =target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME,false);
            }
            ComponentDescription cdInfo = new ComponentDescriptionImpl(null,new ContextImpl(),false);
            try {
              if (targetCodeBase != null) cdInfo.sfAddAttribute("sfCodeBase",
                  targetCodeBase);
              cdInfo.sfAddAttribute("java.class.path",System.getProperty("java.class.path"));
              cdInfo.sfAddAttribute("org.smartfrog.sfcore.processcompound.sfProcessName",
                  System.getProperty("org.smartfrog.sfcore.processcompound.sfProcessName"));
            } catch (SmartFrogException sfex){
              if (sflog.isDebugEnabled()) sflog.debug("",sfex);
            }
            throw new SmartFrogDeploymentException (refClass,null,name,target,null,"Class not found", cnfex, cdInfo);
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
}
