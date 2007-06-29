package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Implements the sfCodebase-aware component creation, as currently documented.
 * The code was originally in PrimImpl.
 */
public class OldAlgorithmComponentFactory extends AbstractComponentFactoryUsingClassLoader {

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


    protected Prim getComponentImpl(ComponentDescription askedFor)
            throws ClassNotFoundException, InstantiationException,
                   IllegalAccessException, SmartFrogResolutionException,
                   SmartFrogDeploymentException
    {
        return (Prim) getPrimClass(askedFor).newInstance();
    }


    /**
     * Get the class for the primitive to be deployed. This is where the
     * sfClass attribute is looked up, using the classloader returned by
     * getPrimClassLoader
     *
     * @return class for target
     * @throws Exception failed to load class
     */
    private Class getPrimClass(ComponentDescription target) throws SmartFrogResolutionException, SmartFrogDeploymentException {
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
            Object name = null;
            if (target.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
                name = target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false);
            }
            throw new SmartFrogDeploymentException(refClass, null, name, target,
                    null, "Wrong class when resolving '" + refClass + "': '"
                    + obj + "' (" + obj.getClass().getName() + ")", ccex, targetCodeBase);
        } catch (ClassNotFoundException cnfex) {
            Object name = null;
            if (target.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
                name = target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false);
            }
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
            throw new SmartFrogDeploymentException(refClass, null, name, target, null, "Class not found", cnfex, cdInfo);
        }
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

}
