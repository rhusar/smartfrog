package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.NodeFactory;
import nu.xom.Element;

/**
 * A node factory that handles elements specially, with a data tag
 */
public class ExtendedNodeFactory extends NodeFactory {

    public Element startMakingElement(String name, String namespace) {
        return new ElementEx(name, namespace);  
    }

}
