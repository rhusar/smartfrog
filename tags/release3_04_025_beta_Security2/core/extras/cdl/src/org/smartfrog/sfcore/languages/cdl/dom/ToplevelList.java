package org.smartfrog.sfcore.languages.cdl.dom;

import javax.xml.namespace.QName;

/**
 * This is the toplevel container
 */
public class ToplevelList extends PropertyList {

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
}
