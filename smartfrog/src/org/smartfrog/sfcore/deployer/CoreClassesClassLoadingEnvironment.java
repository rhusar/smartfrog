package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.parser.ParseTimeResourceFactory;
import org.smartfrog.sfcore.reference.Function;
import org.smartfrog.sfcore.languages.sf.PhaseAction;

import java.io.InputStream;

/**
 * The component factory that should be used for framework components.
 * Those come from the same classloader as this class.
 *
 * This is not a Prim so that it can be used without problem when there isn't a logger available.
 * (and process compound, etc). So some code is copied from AbstractClassLoadingEnvironment (eeek).
 */
public class CoreClassesClassLoadingEnvironment implements PrimFactory, ParseTimeResourceFactory, MessageKeys {

    public final Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException {
        String className = null;
        try {

            className = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
            return (Prim) newInstance(className);

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
        return new SmartFrogDeploymentException(message, e, null, askedFor.sfContext());
    }

    public final Function getFunction(ComponentDescription metadata) throws Exception {
        String className = (String) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FUNCTION_CLASS);
        return (Function) newInstance(className);
    }

    public final PhaseAction getPhaseAction(String className) throws Exception {
        return (PhaseAction) newInstance(className);
    }

    protected Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return Class.forName(className).newInstance();
    }

    public InputStream getComponentDescription(String pathname) {
        return getClass().getResourceAsStream(pathname);
    }
}
