package org.smartfrog.projects.alpine.om.soap11;

import org.smartfrog.projects.alpine.om.ExtendedNodeFactory;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import nu.xom.Element;

/**
 * A factory that creates nothing but soap nodes
 */
public class SoapElementFactory extends ExtendedNodeFactory {


    public boolean inScope(String element, String namespace) {
        return true;
    }

    /**
     * Make a new element
     *
     * @param fullname  this comes in with a prefix: on it, which we will need
     *                  to strip off
     * @param namespace namespace URI
     *
     * @return the new element
     */
    public Element startMakingElement(String fullname, String namespace) {
        String name = XsdUtils.extractLocalname(fullname);
        return new SoapElement(name, namespace);
    }

}
