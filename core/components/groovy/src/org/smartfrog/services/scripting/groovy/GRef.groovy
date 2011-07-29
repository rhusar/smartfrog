package org.smartfrog.services.scripting.groovy

import org.smartfrog.sfcore.reference.ReferencePart

/**
 * This is a subclass of {@link org.smartfrog.sfcore.reference.Reference} created purely because in Groovy,
 * Reference is always in scope, and it gets confusing.
 */
class GRef extends org.smartfrog.sfcore.reference.Reference {

    GRef() {
    }

    GRef(final Object referencePart) {
        super(referencePart)
    }

    GRef(final String refString, final boolean parse) {
        super(refString, parse)
    }

    GRef(final ReferencePart referencePart) {
        super(referencePart)
    }
}
