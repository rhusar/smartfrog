package org.smartfrog.sfcore.languages.cdl.importing;

import java.util.HashMap;

/**
 */
public class ImportedDocumentMap extends HashMap<String, ImportedDocument> {
    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public ImportedDocumentMap() {
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
    public Object clone() {
        return super.clone();
    }


}