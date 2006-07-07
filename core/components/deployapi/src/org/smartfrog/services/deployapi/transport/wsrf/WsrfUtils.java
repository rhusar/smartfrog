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
package org.smartfrog.services.deployapi.transport.wsrf;

import nu.xom.Element;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_CHARACTERISTICS;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_REFERENCES;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_P1_NAMESPACE;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CAPABILITY;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Where WSRF utility stuff goes
 * created 13-Apr-2006 11:39:01
 */

public final class WsrfUtils {

    private WsrfUtils() {
    }

    /**
     * test for an endpoint having muws capabilities. This call does not
     * talk to the server, it just processes the results (so is static)
     *
     * @param capabilities
     * @param uri
     * @return true iff the capability is found
     */
    public static boolean hasMuwsCapability(List<Element> capabilities, String uri) {

        for (Element e : capabilities) {
            if (uri.equals(e.getValue())) {
                return true;
            }
        }
        //failure
        return false;
    }


    /**
     * Create a new catalog/uri resolver with all the WSRF stuff in there
     *
     * @param loader
     * @return a resolver
     */
    public XmlCatalogResolver createWsrfCatalogResolver(ResourceLoader loader) {
        return new CdlCatalog(loader);
    }

    public QName makeQname(String prefix, String element, Map<String, String> context) {
        String uri = context.get(prefix);
        return new QName(uri, element, prefix);
    }


    public static String[] DEFAULT_TOPIC_DIALECTS= {
        "http://www.ibm.com/xmlns/stdwip/web-services/WSTopics/TopicExpression/simple"
    };

    /**
     * Create the list of management features
     *
     * @param props      property map
     * @param capability specific capability of this node
     */
    public static void addManagementCharacteristics(PropertyMap props,
                                                    String capability) {
        //the static listing of manageability charcteristics

        //base element of the list
        List<Element> capabilities=new ArrayList<Element>(3);
        //and a management capability for each entry
        capabilities.add(new SoapElement(PROPERTY_MUWS_MANAGEABILITY_CAPABILITY,
                capability));
        capabilities.add(new SoapElement(PROPERTY_MUWS_MANAGEABILITY_CAPABILITY,
                MUWS_CAPABILITY_MANAGEABILITY_REFERENCES));
        capabilities.add(new SoapElement(PROPERTY_MUWS_MANAGEABILITY_CAPABILITY,
                MUWS_CAPABILITY_MANAGEABILITY_CHARACTERISTICS));

        props.addStaticProperty(PROPERTY_MUWS_MANAGEABILITY_CAPABILITY, capabilities);
    }


    /**
     * Add the WS notification topics to a map
     * @param map property map
     * @param topics list of topics
     * @param fixed whether they are fixed or not
     * @param dialects list of dialects
     */
    public static void addWsTopics(PropertyMap map, List<Element> topics,boolean fixed,String[] dialects) {
        if(topics==null) {
            topics=new LinkedList<Element>();
        }
        map.addStaticProperty(CddlmConstants.PROPERTY_WSNT_TOPIC,topics);

        SoapElement fixedElt= new SoapElement("wsnt:FixedTopicSet",
                CddlmConstants.WSRF_WSNT_NAMESPACE,
                Boolean.toString(fixed));
        map.addStaticProperty(CddlmConstants.PROPERTY_WSNT_FIXED_TOPIC_SET, fixedElt);

        List<Element> topicExpressionDialects=new ArrayList<Element>(dialects.length);
        for(int i=0;i<dialects.length;i++) {
            topicExpressionDialects.add(new SoapElement("wsnt:TopicExpressionDialects",
                    CddlmConstants.WSRF_WSNT_NAMESPACE,
                    dialects[i]));
        }
        map.addStaticProperty(CddlmConstants.PROPERTY_WSNT_TOPIC_EXPRESSION_DIALOGS, topicExpressionDialects);
    }


    public static SoapElement WsRfRpElement(String name) {
        return new SoapElement(
                "wsrf-rp:" + name,
                Constants.WSRF_WSRP_NAMESPACE);
    }

    /**
     * Turn an element into a zero or one-element list
     * @param elt can be null for an empty list
     * @return a list containing the element
     */
    public static List<Element> listify(Element elt) {
        List<Element> list = new LinkedList<Element>();
        if(elt!=null) {
            list.add(elt);
        }
        return list;
    }
}
