package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;

/**
 * Extended element with a backpointer to the element
 */
public class ElementEx extends Element {

    /**
     * Arbitrary backpointer data.
     */
    DocNode backpointer;

    public ElementEx(String name) {
        super(name);
    }

    public ElementEx(String name, String uri) {
        super(name, uri);
    }

    public ElementEx(Element element) {
        super(element);
    }

    protected Element shallowCopy() {
        return new ElementEx(getQualifiedName(), getNamespaceURI());
    }
}
