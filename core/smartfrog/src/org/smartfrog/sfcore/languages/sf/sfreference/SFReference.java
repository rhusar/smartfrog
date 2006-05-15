package org.smartfrog.sfcore.languages.sf.sfreference;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;

/**
 * Representation of reference for hte SF language
 */
public class SFReference extends Reference implements ReferencePhases {
    public Reference sfAsReference() throws SmartFrogCompilationException {
        Reference r = new Reference();
        r.addElements(this);
        r.setEager(getEager());
        r.setData(getData());
        return r;
    }
}
