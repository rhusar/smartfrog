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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import static org.ggf.cddlm.generated.api.CddlmConstants.CDL_API_TYPES_NAMESPACE;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.xml.utils.XomUtils;
import org.smartfrog.services.xml.utils.XsdUtils;

import java.net.URI;

/**
 * generic xom stuff
 */
public class XomHelper extends XomUtils {
    public static final String API = "api:";
    public static final String CMP = "cmp:";
    public static final String WSNT = "wsnt:";
    public static final String WSRF_RL = "wsrf-rl:";
    public static final String MUWSP1_XS = "muws-p1-xs:";
    public static final String TNS = CDL_API_TYPES_NAMESPACE;

    /**
     * Create a new API element with the api: prefix in the API namespace
     *
     * @param name localname
     * @return a new element
     */
    public static SoapElement apiElement(String name) {
        return new SoapElement(API + name,
                CDL_API_TYPES_NAMESPACE);
    }



    /**
     * Create a new API element
     *
     * @param name  localname
     * @param value string name
     * @return
     */
    public static SoapElement apiElement(String name, String value) {
        SoapElement e = apiElement(name);
        e.appendChild(value);
        return e;
    }

    /**
     * Create an API element with a uri as the value
     * @param name
     * @param value
     * @return
     */
    public static SoapElement apiElement(String name,URI value) {
        return apiElement(name,value.toString());
    }

    /**
     * Create a new Api element with the specified child element
     *
     * @param name  localname
     * @param child child element. May be null.
     * @return
     */
    public static SoapElement apiElement(String name, Element child) {
        SoapElement e = apiElement(name);
        if (child != null) {
            e.appendChild(child);
        }
        return e;
    }


    /**
     * Add a new API attribute to an element. This does not add the xmlns to the element, so
     * make sure it is there already.
     *
     * @param element element to add to
     * @param name    attribute name
     * @param value   string value of the element
     */
    public static void addApiAttr(Element element, String name, String value) {
        Attribute attribute = apiAttribute(name, value);
        element.addAttribute(attribute);
    }

    /**
     * Create an API attribute. This does not add the xmlns to the element, so
     * make sure it is there already.
     * @param name
     * @param value
     * @return
     */
    public static Attribute apiAttribute(String name, String value) {
        Attribute attribute = new Attribute(API + name,
                CDL_API_TYPES_NAMESPACE,
                value);
        return attribute;
    }

    /**
     * Get the value of an api attribute
     *
     * @param element  element to look at
     * @param name     attribute name
     * @param required flag if needed
     * @return the value
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException if  no match
     */
    public static String getApiAttrValue(Element element, String name, boolean required) {
        return getAttributeValue(element, CDL_API_TYPES_NAMESPACE, name, required);
    }

    /**
     * Get the value of any string attribute in any
     * @param element element
     * @param ns namespace of attribute
     * @param name attribute name
     * @param required required or not?
     * @return the value
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException if  no match and required==true
     */
    public static String getAttributeValue(Element element, String ns, String name, boolean required) {
        Attribute val = element.getAttribute(name, ns);
        if (val == null) {
            if (required) {
                throw FaultRaiser.raiseBadArgumentFault("No attribute "+ns+"#" + name + " on " + element);
            } else {
                return null;
            }
        }
        return val.getValue();
    }

    /**
     * Get the boolean value of an attribute
     *
     * @param element
     * @param name
     * @param required
     * @param defval
     * @return
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if the value doesnt map to a bool
     */
    public static boolean getBoolApiAttrValue(Element element, String name, boolean required, boolean defval) {
        String val = getApiAttrValue(element, name, required);
        if (val == null) {
            return defval;
        }
        return getXsdBoolValue(val);
    }

    /**
     * Create a new API element with the api: prefix in the API namespace
     *
     * @param name localname
     *
     * @return a new element
     */
    public static SoapElement cmpElement(String name) {
        return new SoapElement(CMP + name,
                CddlmConstants.CDL_CMP_TYPES_NAMESPACE);
    }

    public static SoapElement wsntElement(String name) {
        return new SoapElement(WSNT + name,
                CddlmConstants.WSRF_WSNT_NAMESPACE);
    }


    /**
     * @param string value to parse
     * @return value
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if the value doesnt map to a bool
     */
    public static boolean getXsdBoolValue(String string) {
        if (XsdUtils.isXsdBooleanTrue(string)) {
            return true;
        }
        if (XsdUtils.isXsdBooleanFalse(string)) {
            return false;
        }
        throw FaultRaiser.raiseBadArgumentFault("Not a valid boolean value:" + string);
    }

    /**
     * Create a MUWS resource ID element from a string ID
     *
     * @param id new ID
     * @return the new element
     */
    public static SoapElement makeResourceId(String id) {
        SoapElement element = new SoapElement(MUWSP1_XS +
                Constants.PROPERTY_MUWS_RESOURCEID.getLocalPart(),
                Constants.MUWS_P1_NAMESPACE);
        element.appendChild(id);
        return element;

    }

    /**
     * Move an element into the cdl namespace
     *
     * @param element
     * @param name
     */
    public static void adopt(Element element, String name) {
        //this is a bit convoluted, because we cannot change the prefix on an element until it is
        //in the right namespace. First we 'unnamespace it', then we push it into the right place
        element.setNamespacePrefix("");
        element.setNamespaceURI(CDL_API_TYPES_NAMESPACE);
        element.setNamespacePrefix("api");
        element.setLocalName(name);
    }

    /**
     * Get an element's value. Throws a BadArgument Deployment fault if it
     * doesnt resolve.
     *
     * @param node  node to start
     * @param query query to ask
     * @return string value
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     */
    public static String getElementValue(Node node, String query) {
        return getElementValue(node, query,true);
    }

    /**
     * Get the string valur of a node
     * @param node
     * @param query
     * @param required
     * @return string value or null
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     */
    public static String getElementValue(Node node, String query,boolean required) {
        Element element = getElement(node, query,required);
        if(element==null) {
            return null;
        }
        return element.getValue();
    }

    /**
     * Get an element. Throws a BadArgument Deployment fault if it doesnt
     * resolve.
     *
     * @param node  node to start
     * @param query query to ask
     * @return the element
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     */
    public static Element getElement(Node node, String query) {
        return getElement(node, query, true);
    }

    /**
     * Get an element. Throws a BadArgument Deployment fault if it doesnt
     * resolve.
     *
     * @param node     node to start
     * @param query    query to ask
     * @param required flag to indicate a node is required or not
     * @return the element or null if not found && required==false.
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
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

    /**
     * Create an addFile request with no metadata
     * @param name
     * @param mimeType
     * @param scheme
     * @param encodedPayload
     * @param payloadURI
     * @return
     */
    public static SoapElement addFileRequest(
            URI name,
            String mimeType,
            String scheme,
            String encodedPayload,
            URI payloadURI) {


        SoapElement request=apiElement("addFileRequest");
        request.appendChild(apiElement("name",name));
        request.appendChild(apiElement("mimetype",mimeType));
        request.appendChild(apiElement("scheme",scheme));
        if(payloadURI==null) {
            if(encodedPayload==null) {
                throw new IllegalArgumentException("Neither inline or external data specified");
            }
            request.appendChild(apiElement("data", encodedPayload));
        } else {
            if(encodedPayload!=null) {
                throw new IllegalArgumentException("both inline and external data specified");
            }
            request.appendChild(apiElement("uri",payloadURI));
        }
        return request;
    }


    public static SoapElement makeOption(String name,String value,boolean mustUnderstand) {
        SoapElement option = makeOption(name, mustUnderstand);
        option.addOrReplaceChild(apiElement("string",value));
        return option;
    }

    public static SoapElement makeOption(String name, boolean mustUnderstand) {
        SoapElement option = apiElement("option");
        option.addAttribute(apiAttribute("name", name));
        option.addAttribute(apiAttribute("mustUnderstand", Boolean.toString(mustUnderstand)));
        return option;
    }

}



