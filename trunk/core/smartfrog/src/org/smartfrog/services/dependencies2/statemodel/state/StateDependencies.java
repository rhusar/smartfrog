package org.smartfrog.services.dependencies2.statemodel.state;


import org.smartfrog.services.dependencies2.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies2.statemodel.exceptions.SmartFrogStateLifecycleException;

/**

 */
public interface StateDependencies {
    public void register(DependencyValidation d) throws SmartFrogStateLifecycleException;
    public void deregister(DependencyValidation d) throws SmartFrogStateLifecycleException;
}

