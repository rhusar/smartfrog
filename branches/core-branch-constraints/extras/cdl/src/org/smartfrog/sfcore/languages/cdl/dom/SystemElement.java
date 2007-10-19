/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

/**
 * This is the system element, which is different from the toplevel list and slightly different
 * from a property list, if need be.
 *
 * It repesents the cdl:system node. It's parents are not PropertyList elements, though all its children will be
 *
 *
 * created 31-Jan-2006 11:10:44
 */

public class SystemElement extends PropertyList {

    public SystemElement(String name) {
        super(name);
        setRoot(true);
    }

    public SystemElement(String name, String uri) {
        super(name, uri);
        setRoot(true);
    }

    public SystemElement(Element element) {
        super(element);
        setRoot(true);
    }

    /**
     * Parse from XML
     *
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException
     *
     */
    @Override
    public void bind() throws CdlXmlParsingException {
        super.bind();
        setRoot(true);
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
     * this is an override point, part of a shallowCopy.
     *
     * @return a new PropertyList or a subclass, with
     */
    protected PropertyList newList(String name, String namespace) {
        return new SystemElement(name, namespace);
    }

/*
    public String getSfName(GenerateContext out) {
        return SmartFrogCoreKeys.SF_CONFIG;
    }
*/
}
