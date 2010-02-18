package org.smartfrog.services.dependencies.statemodel.state;


import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.exceptions.SmartFrogStateLifecycleException;

/**

 */
public interface StateDependencies {
    public void register(DependencyValidation d) throws SmartFrogStateLifecycleException;
    public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException;
}

