package org.smartfrog.sfcore.parser;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.reference.Function;

import java.io.InputStream;

/**
 * A factory used to access user-provided resources needed at parse time.
 */
public interface ParseTimeResourcesFactory {
    Function getFunction(ComponentDescription cd);
    PhaseAction getPhaseAction(ComponentDescription cd);
    InputStream getComponentDescription(String pathname);
    // getConstraintSolver also someday 
}
