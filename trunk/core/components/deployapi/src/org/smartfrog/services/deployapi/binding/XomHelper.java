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

package org.smartfrog.services.deployapi.binding;

import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.system.Constants;
import org.ggf.cddlm.generated.api.CddlmConstants;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Node;
import nu.xom.Nodes;

import javax.xml.namespace.QName;

/** generic xom stuff */
public class XomHelper {
    public static final String API = "api:";
    public static final String WSRF_RL = "wsrf-rl:";
    public static final String MUWSP1_XS = "muws-p1-xs:";
    public static final String TNS = CddlmConstants.CDL_API_TYPES_NAMESPACE;

    public static Element apiElement(String name) {
        return new Element(API + name,
                CddlmConstants.CDL_API_TYPES_NAMESPACE);
    }


    public static void addApiAttr(Element element, String name, String value) {
        Attribute attribute = new Attribute(API + name,
                Constants.CDL_API_TYPES_NAMESPACE,
                value);
        element.addAttribute(attribute);
    }

    public static Element makeResourceId(String id) {
        Element element = new Element(MUWSP1_XS +
                Constants.PROPERTY_MUWS_RESOURCEID.getLocalPart(),
                Constants.MUWS_P1_NAMESPACE);
        element.appendChild(id);
        return element;

    }

    /**
     * Get an element's value. Throws a BadArgument Deployment fault if it
     * doesnt resolve.
     *
     * @param node  node to start
     * @param query query to ask
     * @return
     */
    public static String getElementValue(Node node, String query) {
        return getElement(node, query).getValue();
    }

    /**
     * Get an element. Throws a BadArgument Deployment fault if it doesnt
     * resolve.
     *
     * @param node  node to start
     * @param query query to ask
     * @return the element
     */
    public static Element getElement(Node node, String query) {
        return getElement(node, query, true);
    }

    /**
     * Get an element. Throws a BadArgument Deployment fault if it doesnt
     * resolve.
     *
     * @param node  node to start
     * @param query query to ask
     * @param required flag to indicate a node is required or not
     * @return the element or null if not found && required==false.
     */
    public static Element getElement(Node node,
                                     String query,
                                     boolean required) {
        Nodes nodes = node.query(query, Constants.XOM_CONTEXT);
        if (nodes.size() == 0) {
            if (required) {
                throw FaultRaiser.raiseBadArgumentFault("Nothing at " + query);
            } else {
                return null;
            }
        }
        Node n = nodes.get(0);
        if (!(n instanceof Element)) {
            throw FaultRaiser.raiseBadArgumentFault("Expected an element at " +
                    query
                    +
                    " but got " +
                    n);
        }
        return (Element) n;
    }

}
