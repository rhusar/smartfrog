package org.smartfrog.sfcore.parser;

import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.reference.Function;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;

/**
 * A factory used to access user-provided resources needed at parse time.
 */
public interface ParseTimeComponentFactory {
    Function getFunction(ComponentDescription metadata) throws Exception;
    PhaseAction getPhaseAction(String className) throws Exception;
    // getConstraintSolver also someday

    void setClassLoadingEnvironment(ClassLoadingEnvironment environment);
}
