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
import nu.xom.Attribute;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_CHARACTERISTICS;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_REFERENCES;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CAPABILITY;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
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
    public static final String ERROR_UNSUPPORTED_DIALECT = "Unsupported Dialect:";

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
    public static XmlCatalogResolver createWsrfCatalogResolver(ResourceLoader loader) {
        return new CdlCatalog(loader);
    }

    public static QName makeQname(String prefix, String element, Map<String, String> context) {
        String uri = context.get(prefix);
        return new QName(uri, element, prefix);
    }


    public static String[] DEFAULT_TOPIC_DIALECTS= {
        WSNConstants.SIMPLE_DIALECT
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

    public static SoapElement WsntElement(String name) {
        return new SoapElement(
                "wsnt:" + name,
                Constants.WSRF_WSNT_NAMESPACE);
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

    /* create a message like
<wsnt:Subscribe>
<wsnt:ConsumerReference>
wsa:endpointReference
</wsnt: ConsumerReference>
<wsnt:TopicExpression dialect = “xsd:anyURI”>
{any}
</wsnt:TopicExpression>
<wsnt:UseNotify> xsd:boolean </wsnt:UseNotify>?
<wsnt:Precondition> wsrp:QueryExpression </Precondition>?
<wsnt:Selector> wsrp:QueryExpression </wsnt:Selector>?
<wsnt:SubscriptionPolicy> {any} </wsnt:SubscriptionPolicy>?
<wsnt:InitialTerminationTime>
xsd:dateTime
</wsnt:InitialTerminationTime>?
</wsnt: Subscribe>
*/

    /**
     * Creates a simple request. Simple topics only, obviously.
     * @param epr callback epr
     * @param topic topic to use
     * @param useNotify whether to raw notify or not
     * @param terminationTime term time (optional)
     * @return a configured subscription
     */
    public static SoapElement createSubscriptionRequest(AlpineEPR epr,
                                                 QName topic,
                                                 boolean useNotify,
                                                 String terminationTime) {
        SoapElement subscribe= WsntElement(WSNConstants.SUBSCRIBE);
        SoapElement consumer;
        consumer = epr.toXomInNewNamespace(
                WSNConstants.CONSUMER_REFERENCE,
                Constants.WSRF_WSNT_NAMESPACE, "wsnt",
                Constants.WS_ADDRESSING_NAMESPACE, "wsa");
        subscribe.appendChild(consumer);
        SoapElement topicElement=WsntElement(WSNConstants.TOPIC_EXPRESSION);
        addSimpleDialectAttribute(topicElement);
        topicElement.addAttribute(new Attribute(WSNConstants.DIALECT,
                WSNConstants.SIMPLE_DIALECT));
        topicElement.appendQName(topic);
        assert assertSimpleDialect(topicElement);
        subscribe.appendChild(topicElement);
        SoapElement notifyElement=WsntElement(WSNConstants.USE_NOTIFY);
        notifyElement.appendChild(Boolean.toString(useNotify));
        subscribe.appendChild(notifyElement);
        if(terminationTime!=null) {
            SoapElement itt = WsntElement(WSNConstants.INITIAL_TERMINATION_TIME);
            itt.appendChild(terminationTime);
            subscribe.appendChild(itt);
        }
        return subscribe;
    }

    /**
     * Add a dialect element to an attribute
     * @param topicElement
     */
    public static void addSimpleDialectAttribute(Element topicElement) {
        Attribute attribute;
        attribute = new Attribute(WSNConstants.DIALECT,
                WSNConstants.SIMPLE_DIALECT);
/*
        attribute = new Attribute("wsnt:" + WSNConstants.DIALECT,
                Constants.WSRF_WSNT_NAMESPACE,
                WSNConstants.SIMPLE_DIALECT);
*/
        topicElement.addAttribute(attribute);
    }

    /**
     * Get the dialect info from the topic
     * @param topic topic to scan
     * @return the value of the @Dialect attribute
     */
    public static String extractDialect(Element topic) {
        String dialect =
               // XomHelper.getAttributeValue(topic, Constants.WSRF_WSNT_NAMESPACE, WSNConstants.DIALECT, true);
                XomHelper.getAttributeValue(topic,"",WSNConstants.DIALECT, true);
        return dialect;
    }

    /**
     * Verify that the message contains a single dialect.
     * @param topic topic to scan
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException if the dialect is unsupported
     */
    public static void expectSimpleDialect(Element topic) {
        String dialect=extractDialect(topic);
        if (!WSNConstants.SIMPLE_DIALECT.equals(dialect)) {
            throw FaultRaiser.raiseBadArgumentFault(ERROR_UNSUPPORTED_DIALECT + dialect);
        }
    }

    /**
     * Verify that the message contains a single dialect.
     *
     * @param topic topic to scan
     * @return true so that we can be used in assert statements
     */
    public static boolean assertSimpleDialect(Element topic) {
        expectSimpleDialect(topic);
        return true;
    }

}
