package org.smartfrog.sfcore.parser;

import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.reference.Function;

import java.io.InputStream;

/**
 * A factory used to access user-provided resources needed at parse time.
 */
public interface ParseTimeResourceFactory {
    Function getFunction(String className) throws Exception;
    PhaseAction getPhaseAction(String className) throws Exception;
    InputStream getComponentDescription(String pathname);
    // getConstraintSolver also someday 
}
