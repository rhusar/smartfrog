/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.engine;

import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.xml.java5.iterators.NodeIterator;

import java.util.HashMap;


/**
 * created 28-Nov-2005 12:41:03
 */

public class OptionPropertyMap extends HashMap<String, String> {
    private static final String ELEMENT_PROPERTY = "property";


    public void importMap(Element map) {
        NodeIterator nodes = new NodeIterator(map);
        for (Node node : nodes) {
            if (!(node instanceof Element)) {
                //comments and things, presumably.
                continue;
            }
            Element tuple = (Element) node;
            if(!tuple.getLocalName().equals(ELEMENT_PROPERTY)) {
                throw FaultRaiser.raiseBadArgumentFault(
                        "Expected an element called "+ELEMENT_PROPERTY
                                +"; got: "+tuple);
            }
            String name= XomHelper.getElementValue(tuple,"api:name");
            String value = XomHelper.getElementValue(tuple, "api:value");
            put(name,value);
        }
    }

    /**
     * Export the graph by appending a series of properties to the
     * supplied parent element.
     * @param parent parent to append the children to
     */
    public void export(Element parent) {
        for(String name:keySet()) {
            String value=get(name);
            Element tuple=XomHelper.apiElement(ELEMENT_PROPERTY);
            Element nameElt=XomHelper.apiElement("name",name);
            Element valueElt = XomHelper.apiElement("value", value);
            tuple.appendChild(nameElt);
            tuple.appendChild(valueElt);
            parent.appendChild(tuple);
        }
    }

}
