package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.deployer.ComponentDeployer;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;


/**
 * Implementation of {@link ComponentDeployer} that delegates the component creation to a
 * pre-existing {@link ComponentFactory} component. It still does the job of hooking the
 * component to its parent.
 * <p/>
 * TODO: Now the hooking bit is done by inheriting from PrimProcessDeplyerImpl. Need to merge or separate things further.
 */
public class ComponentFactoryDeployerImpl extends PrimProcessDeployerImpl
        implements ComponentDeployer
{
    public ComponentFactoryDeployerImpl(ComponentDescription target) {
        super(target);    
    }

    protected Prim getPrimInstance() throws SmartFrogDeploymentException, RemoteException {
        ComponentDescription metadata = null;
        try {
            metadata = (ComponentDescription) target.sfResolveHere(SmartFrogCoreKeys.SF_METADATA);
        } catch (SmartFrogResolutionException e) {
            LogFactory.sfGetProcessLog().ignore(e);
        }

        try {
            ComponentFactory factory;

            if (metadata != null) {
                // Component using the new sfMeta syntax
                Reference factoryRef = (Reference) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FACTORY);
                factory = (ComponentFactory) metadata.sfResolve(factoryRef);
                return factory.getComponent(metadata);
            } else {
                // Component using the old sfClass-only syntax
                factory = defaultFactory();
                return factory.getComponent(target);
            }
        
        } catch (SmartFrogResolutionException e) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_UNRESOLVED_REFERENCE, SmartFrogCoreKeys.SF_CLASS), e,
                null, null);
        }
    }

    private ComponentFactory defaultFactory() {
        return new DefaultClassLoadingEnvironmentImpl();
    }
}
