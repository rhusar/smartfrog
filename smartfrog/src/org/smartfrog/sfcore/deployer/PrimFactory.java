package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

public interface PrimFactory {
    /**
     * Creates the component instance from its description.
     * Uses the {@link ClassLoadingEnvironment} passed by {@link this.setClassLoadingEnvironment} to load the Prim class if needed.
     * Can perform arbitrary transformations on the instance before handing it out. 
     *
     * @param askedFor Description of the component to be created.
     * @return The newly created component instance.
     * @throws SmartFrogDeploymentException If the component could not be created (class loading issues, missing attributes, etc).
     */
    Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException;

    /**
     * Sets the ClassLoadingEnvironment to be used by {@link this.getComponent}.
     * @param environment The ClassLoadingEnvironment to work from.
     */
    void setClassLoadingEnvironment(ClassLoadingEnvironment environment);
}
