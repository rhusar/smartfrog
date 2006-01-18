package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.generate.GenerateContext;

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
        for (Node node : this) {
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
    public String getSfName(GenerateContext out) {
        //are we the system node?
        if (getOwner().getSystem() == this) {
            return GenerateContext.COMPONENT_SFSYSTEM;
        } else {
            if (getOwner().getConfiguration() == this) {
                return GenerateContext.COMPONENT_CONFIGURATION;
            } else {
                return super.getSfName(out);
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
