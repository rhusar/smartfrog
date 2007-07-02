package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

public abstract class AbstractPrimFactoryUsingClassLoader extends PrimImpl implements PrimFactory {

    protected AbstractPrimFactoryUsingClassLoader() {}


    public Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException {
        String className = null;
        try {
            // Not pretty - but needed to have a proper error message, and pass tests.
            className = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);

            return getComponentImpl(askedFor);
        } catch (ClassNotFoundException e) {
            throw deploymentException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND, className), e, askedFor);
        } catch (InstantiationException instexcp) {
            throw deploymentException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR, "Prim"), instexcp, askedFor);
        } catch (IllegalAccessException illaexcp) {
            throw deploymentException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS, "Prim", "newInstance()"), illaexcp,
                    askedFor);
        } catch (SmartFrogResolutionException e) {
            throw deploymentException(MessageUtil.formatMessage(
                    MSG_UNRESOLVED_REFERENCE, SmartFrogCoreKeys.SF_CLASS), e,
                    askedFor);
        }
    }

    private SmartFrogDeploymentException deploymentException(String message, Exception e, ComponentDescription askedFor) {
        return new SmartFrogDeploymentException(message, e, this, askedFor.sfContext());
    }

    protected abstract Prim getComponentImpl(ComponentDescription askedFor)
            throws ClassNotFoundException, InstantiationException,
                   IllegalAccessException, SmartFrogResolutionException, SmartFrogDeploymentException;
    
}
