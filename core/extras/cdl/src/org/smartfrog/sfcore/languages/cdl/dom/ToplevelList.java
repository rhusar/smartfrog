package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;

import javax.xml.namespace.QName;

import org.smartfrog.sfcore.languages.cdl.CdlParsingException;

/**
 * This is the toplevel container
 */
public class ToplevelList extends PropertyList {

    public ToplevelList() {
        super();
    }

    public ToplevelList(Element element) throws CdlParsingException {
        super(element);
    }

    /**
     * Look up a child elemnt
     * @param childName name of child
     * @return element or null
     */
    PropertyList lookup(QName childName) {
        for(PropertyList child:children()) {
            if(child.isNamed(childName)) {
                return child;
            }
        }
        //No match
        return null;
    }

    /**
     * test that a node is of the right type
     *
     * @param element
     * @return true if the element namespace and localname match what we handle
     */
    static boolean isConfigurationElement(Element element) {
        return isNode(element, ELEMENT_CONFIGURATION);
    }

    /**
     * test that a node is of the right type
     *
     * @param element
     * @return true if the element namespace and localname match what we handle
     */
    static boolean isSystemElement(Element element) {
        return isNode(element, ELEMENT_SYSTEM);
    }


}
