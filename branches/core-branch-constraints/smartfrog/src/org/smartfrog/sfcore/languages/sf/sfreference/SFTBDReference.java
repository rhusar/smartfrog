package org.smartfrog.sfcore.languages.sf.sfreference;

import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.common.*;


import java.util.Iterator;

/**
 * Representation of ApplyReference for the SF Language
 */
public class SFTBDReference extends SFReference implements ReferencePhases {

    public SFTBDReference() {
        super();
    }

    /**
     * Get tje run-time version of the reference
     *
     * @return the reference
     * @throws org.smartfrog.sfcore.common.SmartFrogCompilationException
     */
    public Reference sfAsReference() throws SmartFrogCompilationException {
        return this;
    }

    /**
     * Returns a copy of the reference, by cloning itself and the function part
     *
     * @return copy of reference
     * @see org.smartfrog.sfcore.common.Copying
     */
    public Object copy() {
        return this.clone();
    }

    /**
     * Makes a clone of the reference. The inside ref holder is cloned, but the
     * contained component is NOT.
     *
     * @return clone of reference
     */
    public Object clone() {
        return new SFTBDReference();
    }

    /**
     * Checks if this and given reference are equal. Two references are
     * considered to be equal if the component they wrap are ==
     *
     * @param ref to be compared
     * @return true if equal, false if not
     */
    public boolean equals(Object ref) {
        return this == ref;
    }

    /**
     * Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          if reference failed to resolve
     */
    public Object resolve(ReferenceResolver rr, int index)
            throws SmartFrogResolutionException {
        throw new SmartFrogResolutionException ("attribute is still TBD (to be defined)");

    }

    /**
     * Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if reference failed to resolve
     */
    public Object resolve(RemoteReferenceResolver rr, int index)
            throws SmartFrogResolutionException {
        throw new SmartFrogResolutionException ("attribute is still TBD (to be defined)");
    }

    /**
     * Returns string representation of the reference.
     * Overrides Object.toString.
     *
     * @return String representing the reference
     */
    public String toString() {
        String res = "TBD";
        return res;
    }
}
