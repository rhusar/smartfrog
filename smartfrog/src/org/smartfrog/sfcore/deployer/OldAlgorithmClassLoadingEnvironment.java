package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.io.InputStream;
import java.net.URL;

/**
 * Implements the sfCodebase-aware component creation, as currently documented.
 * The code was originally in PrimImpl.
 */
public class OldAlgorithmClassLoadingEnvironment extends AbstractClassLoadingEnvironment {

    /**
     * Efficiency holder of sfClass reference.
     */
    private static final Reference refClass = new Reference(
            SmartFrogCoreKeys.SF_CLASS);

    protected Prim getComponentImpl(ComponentDescription askedFor)
            throws ClassNotFoundException, InstantiationException,
                   IllegalAccessException, SmartFrogResolutionException,
                   SmartFrogDeploymentException
    {
        return (Prim) getPrimClass(askedFor).newInstance();
    }

    protected Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return SFClassLoader.forName(className).newInstance();
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
        String targetClassName;
        Object obj = null;
        try {

            targetClassName = (String) target.sfResolve(refClass);

            // 3rd parameter = true: We look in the default code base if everything else fails.
            return SFClassLoader.forName(targetClassName, null, true);

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
                    + obj + "' (" + obj.getClass().getName() + ")", ccex, null);
        } catch (ClassNotFoundException cnfex) {
            Object name = null;
            if (target.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
                name = target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false);
            }
            ComponentDescription cdInfo = new ComponentDescriptionImpl(null, new ContextImpl(), false);
            try {
                cdInfo.sfAddAttribute("java.class.path", System.getProperty("java.class.path"));
                cdInfo.sfAddAttribute(SmartFrogCoreProperty.sfProcessName,
                        System.getProperty(SmartFrogCoreProperty.sfProcessName));
            } catch (SmartFrogException sfex) {
                if (sfLog().isDebugEnabled()) sfLog().debug("", sfex);
            }
            throw new SmartFrogDeploymentException(refClass, null, name, target, null, "Class not found", cnfex, cdInfo);
        }
    }

    public InputStream getResourceAsStream(String pathname) {
        return SFClassLoader.getResourceAsStream(pathname);
    }

    protected URL getResource(String pathname) {
        throw new UnsupportedOperationException();
    }

    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }
}
