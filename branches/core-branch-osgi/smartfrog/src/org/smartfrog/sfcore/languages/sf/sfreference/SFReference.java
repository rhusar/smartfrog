package org.smartfrog.sfcore.languages.sf.sfreference;

import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogLazyResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferenceResolver;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;

/**
 * Representation of reference for hte SF language
 */
public class SFReference extends Reference implements ReferencePhases {
	/**Force Eagerness*/
	public static boolean resolutionForceEager=false;
	
	public Reference sfAsReference() throws SmartFrogCompilationException {
        Reference r = new Reference();
        r.addElements(this);
        r.setEager(getEager());
        r.setData(getData());
        r.setOptional(getOptional());
        r.setDefaultValue(defaultValue);
        return r;
    }

    /**
     * Resolves this reference using the given reference resolver, and starting
     * at index of this reference. If the reference size is 0 the given
     * reference resolver is returned.
     *
     * @param rr ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     *
     * @return attribute found on resolving this reference
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException reference failed to resolve
     */
    public Object resolve(ReferenceResolver rr, int index)
        throws SmartFrogResolutionException {
        if (index == 0 && !getEager() && !resolutionForceEager) throw new SmartFrogLazyResolutionException("lazy reference resolved");
        return super.resolve(rr, index);
    }

    /**
     * Resolves this reference using the given remote reference resolver, and
     * starting at index of this reference. If the reference size is 0 the
     * given reference resolver is returned.
     *
     * @param rr ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     *
     * @return attribute found on resolving this reference
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if reference failed to resolve
     */
    public Object resolve(RemoteReferenceResolver rr, int index)
        throws SmartFrogResolutionException {
        if (index == 0 && !getEager()) throw new SmartFrogLazyResolutionException("lazy reference resolved");
        return super.resolve(rr, index);
    }
}