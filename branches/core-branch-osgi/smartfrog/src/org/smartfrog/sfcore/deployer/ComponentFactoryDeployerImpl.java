package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDeployer;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimDeployerImpl;

import java.rmi.RemoteException;


/**
 * Implementation of {@link ComponentDeployer} that delegates the component creation to a
 * pre-existing {@link ComponentFactory} component. It still does the job of hooking the
 * component to its parent.
 * <p/>
 * Implementation note: in fact the hooking bit is done by inheriting from PrimDeplyerImpl. That will change soon.
 */
public class ComponentFactoryDeployerImpl extends PrimDeployerImpl
    implements ComponentDeployer {
    private ComponentDescription target;

    public ComponentFactoryDeployerImpl(ComponentDescription target) {
        super(target);
        this.target = target;
    }

    protected Prim getPrimInstance() throws SmartFrogDeploymentException, RemoteException {
        try {
            ComponentDescription metadata = (ComponentDescription) target.sfResolveHere(SmartFrogCoreKeys.SF_METADATA);
            ComponentFactory factory = (ComponentFactory) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FACTORY);

            return factory.getComponent(target);
        } catch (ClassNotFoundException e) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND), e, null, null);
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
}
