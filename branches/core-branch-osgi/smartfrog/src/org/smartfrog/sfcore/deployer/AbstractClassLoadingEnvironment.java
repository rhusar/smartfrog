package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.parser.ParseTimeResourceFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Function;

public abstract class AbstractClassLoadingEnvironment extends PrimImpl
        implements PrimFactory, ParseTimeResourceFactory
{

    public final Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException {
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

    protected abstract Object newInstance(String className) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException;

    public final Function getFunction(ComponentDescription metadata) throws Exception {
        String className = (String) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FUNCTION_CLASS);
        return (Function) newInstance(className);
    }

    public final PhaseAction getPhaseAction(String className) throws Exception {
        return (PhaseAction) newInstance(className);
    }
}
