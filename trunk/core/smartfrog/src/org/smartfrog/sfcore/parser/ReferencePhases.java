package org.smartfrog.sfcore.parser;

import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Interface for the cpnvertion of any language specific reference-like object into a reference
 */
public interface ReferencePhases {
        /**
     * Convert the reference phases implementing objectto a
     * Reference ready for the SmartFrog deployment engine.
     *
     * @return the convertion to a reference
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogCompilationException error converting phases to a
     * componentdescription
     */
    Reference sfAsReference() throws SmartFrogCompilationException;
}
