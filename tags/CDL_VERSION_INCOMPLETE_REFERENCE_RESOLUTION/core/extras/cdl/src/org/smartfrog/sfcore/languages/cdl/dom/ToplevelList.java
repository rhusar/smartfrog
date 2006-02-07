package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;

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
     * this is an override point, part of a shallowCopy.
     *
     * @return a new PropertyList or a subclass, with
     */
    protected PropertyList newList(String name, String namespace) {
        return new ToplevelList(name, namespace);
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
     * Register our nodes with our parse context.
     *
     * @throws CdlDuplicatePrototypeException if there is a registration
     *                                        already
     */
    public void registerPrototypes() throws CdlDuplicatePrototypeException {
        for (Node node : this) {
            if (node instanceof PropertyList) {
                PropertyList prototype = (PropertyList) node;
                prototype.setRoot(true);
                getParseContext().prototypeAddNew(prototype);
            }
        }
    }

    /**
     * test for being a toplevel list.
     * The relevant subclass overrides it to return true
     *
     * @return true always. 
     */
    public boolean isToplevel() {
        return true;
    }
}
