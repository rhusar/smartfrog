package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;

import javax.xml.namespace.QName;

/**
 * This is the toplevel container
 */
public class ToplevelList extends PropertyList {

    public ToplevelList(String name) {
        super(name);
    }

    public ToplevelList(String name, String uri) {
        super(name, uri);
    }

    public ToplevelList(Element element) {
        super(element);
    }

    /**
     * <p/>
     * Creates a very shallow copy of the element with the same name and
     * namespace URI, but no children, attributes, base URI, or namespace
     * declaration. This method is invoked as necessary by the {@link
     * nu.xom.Element#copy() copy} method and the {@link
     * nu.xom.Element#Element(nu.xom.Element) copy constructor}. </p>
     * <p/>
     * <p/>
     * Subclasses should override this method so that it returns an instance of
     * the subclass so that types are preserved when copying. This method should
     * not add any attributes, namespace declarations, or children to the
     * shallow copy. Any such items will be overwritten. </p>
     *
     * @return an empty element with the same name and namespace as this
     *         element
     */
    protected Element shallowCopy() {
        return new ToplevelList(getQualifiedName(), getNamespaceURI());
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
     * Test that a (namespace,localname) pair matches our type
     *
     * @param namespace
     * @param localname
     * @return true for a match
     */
    public static boolean isConfigurationElement(String namespace,
            String localname) {
        return isNode(namespace, localname, ELEMENT_CONFIGURATION);
    }

    /**
     * Test that a (namespace,localname) pair matches our type
     *
     * @param namespace
     * @param localname
     * @return true for a match
     */
    public static boolean isSystemElement(String namespace,
            String localname) {
        return isNode(namespace, localname, ELEMENT_SYSTEM);
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
