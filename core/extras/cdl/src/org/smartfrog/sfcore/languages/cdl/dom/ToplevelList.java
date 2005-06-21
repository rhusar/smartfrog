package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import nu.xom.Node;
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
        for (Node node : nodes()) {
            if (node instanceof PropertyList) {
                PropertyList prototype = (PropertyList) node;
                getParseContext().prototypeAddNew(prototype);
            }
        }
    }

    /**
     * create a smartfrog name from a component This is a string that is a valid
     * SF name. no spaces, colons or other forbidden stuff, and it includes the
     * qname if needed.
     * <p/>
     * If there is a weakness in this algorithm, it is that it is neither
     * complete nor unique. Better to have unique names in the firstplace,
     * maybe.
     * <p/>
     * A big troublespot is qnames. Things would be simpler if they were not
     * there, or aliased to something. but they are always incorporated, if
     * present.
     *
     * @return a safer string.
     */
    public String getSfName() {
        //are we the system node?
        if(getOwner().getSystem()==this) {
            return "sfSystem";
        } else {
            if (getOwner().getConfiguration() == this) {
                return "configuration";
            } else {
                return super.getSfName();
            }
        }
    }
}
