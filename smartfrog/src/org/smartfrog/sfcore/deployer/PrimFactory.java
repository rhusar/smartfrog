package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

public interface PrimFactory {
    /**
     * Creates the component instance from a given description. It is not mandatory that the given description
     * be that of the whole component: it can be a sub-attribute of the component description for example.
     * This is left for implementations to decide.
     *
     * Implementations must also set the {@link org.smartfrog.sfcore.common.SmartFrogCoreKeys#SF_CODE_REPOSITORY}
     * attribute to the repository the component comes from.
     *
     * @param askedFor The ComponentDescription to work off.
     * @return The newly created component instance.
     * @throws SmartFrogDeploymentException If the component could not be created (class loading issues, missing attributes, etc).
     */
    Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException;

    /**
     * Returns the class loader used to create classes from this factory. It is needed to allow RMI
     * to create instances of classes coming from this factory through deserialization.
     * The only method used will be loadClass() so the class loader returned can be a proxy that only implements
     * that method properly.
     * @return The classloader that underlies this factory.
     */
    ClassLoader getClassLoader();

    /**
     * Allows access to the {@link CodeRepository} this factory is using. This is used by {@link ComponentDeployer}s to set up the
     * {@link org.smartfrog.sfcore.common.SmartFrogCoreKeys#SF_CODE_REPOSITORY} attribute of components created through it.
     * @return The CodeRepository associated with this factory.
     */
    CodeRepository getCodeRepository();
}
