package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

import javax.xml.namespace.QName;

/**
 * This is the toplevel container
 */
public class ToplevelList extends PropertyList {

    public ToplevelList(CdlDocument owner) {
        super();
        setOwner(owner);
    }

    public ToplevelList(CdlDocument owner, Element element)
            throws CdlXmlParsingException {
        super(element);
        setOwner(owner);
    }

    /**
     * Look up a child elemnt
     *
     * @param childName name of child
     * @return element or null
     */
    public PropertyList lookup(QName childName) {
        for (DocNode child : childDocNodes()) {
            if (child instanceof PropertyList) {
                PropertyList propertyList = (PropertyList) child;
                if (propertyList.isNamed(childName)) {
                    return propertyList;
                }
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
    public static boolean isConfigurationElement(Element element) {
        return isNode(element, ELEMENT_CONFIGURATION);
    }

    /**
     * test that a node is of the right type
     *
     * @param element
     * @return true if the element namespace and localname match what we handle
     */
    public static boolean isSystemElement(Element element) {
        return isNode(element, ELEMENT_SYSTEM);
    }

    /**
     * Register our nodes with our parse context.
     *
     * @throws CdlDuplicatePrototypeException if there is a registration
     *                                        already
     */
    public void registerPrototypes() throws CdlDuplicatePrototypeException {
        for (DocNode docnode : childDocNodes()) {
            if (docnode instanceof PropertyList) {
                PropertyList prototype = (PropertyList) docnode;
                getParseContext().prototypeAddNew(prototype);
            }
        }
    }

}
