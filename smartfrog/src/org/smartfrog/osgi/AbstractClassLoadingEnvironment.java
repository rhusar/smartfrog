package org.smartfrog.osgi;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;

public abstract class AbstractClassLoadingEnvironment extends PrimImpl implements ClassLoadingEnvironment {

    protected AbstractClassLoadingEnvironment() {}


    public Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException {
        String className = null;
        try {
            // Not pretty - needed to have a proper error message, and pass tests.
            className = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);

            return getComponentImpl(askedFor);
        } catch (ClassNotFoundException e) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND, className), e, null, null);
        } catch (InstantiationException instexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR, "Prim"), instexcp, null, null);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS, "Prim", "newInstance()"), illaexcp,
                    null, null);
        } catch (SmartFrogResolutionException e) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_UNRESOLVED_REFERENCE, SmartFrogCoreKeys.SF_CLASS), e,
                    null, null);
        }
    }

    protected abstract Prim getComponentImpl(ComponentDescription askedFor)
            throws ClassNotFoundException, InstantiationException,
                   IllegalAccessException, SmartFrogResolutionException, SmartFrogDeploymentException;

    public InputStream getResourceAsStream(String location)
        throws RemoteException
    {
        try {
            return getResource(location).openStream();
        } catch (IOException e) {
            return null; // Same as in java.lang.ClassLoader to be consistent
        }
    }
}
