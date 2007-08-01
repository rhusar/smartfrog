package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.Function;
import org.smartfrog.sfcore.parser.ParseTimeComponentFactory;
import org.smartfrog.sfcore.languages.sf.PhaseAction;

public class DefaultComponentFactory extends PrimImpl implements PrimFactory, ParseTimeComponentFactory {
    private ClassLoadingEnvironment environment = null;
    private static final Reference sfClassReference = new Reference(
            ReferencePart.attrib(SmartFrogCoreKeys.SF_CLASS)
    );

    public final Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException {
        String className = null;
        try {

            className = (String) askedFor.sfResolve(sfClassReference);
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
        return new SmartFrogDeploymentException(message, e, this, askedFor.sfContext());
    }

    protected Object newInstance(String className) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException
    {
        ClassLoader loader = environment.getClassLoader();
        Class clazz = loader.loadClass(className);
        SFSecurity.checkSecurity(clazz);
        return clazz.newInstance();
    }

    public final Function getFunction(ComponentDescription metadata) throws Exception {
        String className = (String) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FUNCTION_CLASS);
        return (Function) newInstance(className);
    }

    public final PhaseAction getPhaseAction(String className) throws Exception {
        return (PhaseAction) newInstance(className);
    }

    public void setClassLoadingEnvironment(ClassLoadingEnvironment environment) {
        this.environment = environment;
    }
}
