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

import javax.xml.namespace.QName;

import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.notifications.EventSubscription;
import org.smartfrog.services.deployapi.notifications.Event;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.util.List;
import java.util.UUID;
import java.net.URL;
/*
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
 * created 26-Sep-2006 16:35:14.
 * This represents a WSNT subscription. It contains a static WS resource properties map
 * as well as parsed data.
 *
 */

public class NotificationSubscription implements EventSubscription, WSRPResourceSource,WSNConstants {

    private PropertyMap resources = new PropertyMap();
    private String id= UUID.randomUUID().toString();
    private QName topic;
    private AlpineEPR callback;
    private boolean useNotifyMessage;
    private String precondition;
    private String selector;
    private Element subscriptionPolicy;
    private String terminationTime;
    private String subscriptionURL;
    private SoapElement subscriptionEndpointer;
    private AlpineEPR subscriptionEPR;
    protected static final String SUBSCRIPTION_REFERENCE = "SubscriptionReference";
    protected static final String SUBSCRIBE_RESPONSE = "SubscribeResponse";

    public NotificationSubscription() {
    }

    /**
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     * @param request incoming request
     */
    public NotificationSubscription(Element request) {
        parse(request);
    }


    /**
     * check on ID only for equality
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NotificationSubscription that = (NotificationSubscription) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    /**
     * use ID for hash code
     * @return
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return super.toString();
    }

    private void addWsntResource(String name,Element value) {
        if(value!=null) {
            resources.addStaticProperty(
                new QName(Constants.WSRF_WSNT_NAMESPACE,name,"wsnt"),
                value);
        }
    }

    private void addWsntResource(String name, String value) {
        resources.addStaticProperty(
                new QName(Constants.WSRF_WSNT_NAMESPACE, name, "wsnt"),
                value);
    }
    /**
     *
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     * @param request
     */
    private void parse(Element request) {
        resources.addStaticProperty(Constants.PROPERTY_MUWS_RESOURCEID,
                XomHelper.makeResourceId(getId()));
        if (!WSNConstants.SUBSCRIBE.equals(request.getLocalName())) {
            throw FaultRaiser.raiseBadArgumentFault("wrong element: "+request);
        }
        Element endref = XomHelper.getElement(request, "wsnt:"+CONSUMER_REFERENCE, true);
        addWsntResource(CONSUMER_REFERENCE,endref);
        callback = new AlpineEPR(endref, Constants.WS_ADDRESSING_NAMESPACE);
        Element topicExpression =
                XomHelper.getElement(request, "wsnt:"+TOPIC_EXPRESSION, true);
        String dialect =
                XomHelper.getAttributeValue(topicExpression, Constants.WSRF_WSNT_NAMESPACE, DIALECT, true);
        if (!dialect.equals(WSNConstants.SIMPLE_DIALECT)) {
            throw FaultRaiser.raiseBadArgumentFault("Unsupported Dialect " + dialect);
        }
        addWsntResource(SUBSCRIPTION_POLICY, topicExpression.getValue());
        String useNotify = XomHelper.getElementValue(request, "wsnt:"+USE_NOTIFY, false);
        if (useNotify != null) {
            useNotifyMessage = XomHelper.getXsdBoolValue(useNotify);
        }
        addWsntResource(USE_NOTIFY,Boolean.toString(useNotifyMessage));

        subscriptionPolicy = XomHelper.getElement(request, "wsnt:"+SUBSCRIPTION_POLICY, false);
        addWsntResource(SUBSCRIPTION_POLICY,subscriptionPolicy);
        String termTime = XomHelper.getElementValue(request, "wsnt:"+INITIAL_TERMINATION_TIME, false);
        //todo: act on the term time.
    }

    /**
     * Create a subscription response, which is an EPR in a different namespace
     * @return
     */
    public SoapElement createSubscribeResponse() {
        /*
       <wsnt:SubscribeResponse>
   <wsnt:SubscriptionReference>
   <wsa:Address> Address of Subscription Manager </wsa:Address>
   <wsa:ReferenceProperties>
   Subscription Identifier
   </wsa:ReferenceProperties>
   …
   </wsnt:SubscriptionReference>
   …
   </wsnt:SubscribeResponse>
   */

        SoapElement root = XomHelper.wsntElement(SUBSCRIBE_RESPONSE);
        SoapElement address= subscriptionEPR.toXom(SUBSCRIPTION_REFERENCE,
                Constants.WS_ADDRESSING_NAMESPACE, "wsa");
        address.setNamespacePrefix("wsnt");
        address.setNamespaceURI(Constants.WSRF_WSNT_NAMESPACE);
        root.addOrReplaceChild(address);
        return root;
    }

       /**
        * Something happened to this job
        *
        * @param event the event of interest
        */
    public void event(Event event) {
        //TODO: callback
    }

    public String getId() {
        return id;
    }

    /**
     * Probe the event for still being valid
     *
     * @return true if the event is still valid; false if it has expired and should be deleted
     */
    public boolean probe() {
        //todo: expiry
        return true;
    }

    public QName getTopic() {
        return topic;
    }

    public AlpineEPR getCallback() {
        return callback;
    }

    public boolean isUseNotifyMessage() {
        return useNotifyMessage;
    }

    public String getPrecondition() {
        return precondition;
    }

    public String getSelector() {
        return selector;
    }

    public Element getSubscriptionPolicy() {
        return subscriptionPolicy;
    }

    public String getTerminationTime() {
        return terminationTime;
    }


    public String getSubscriptionURL() {
        return subscriptionURL;
    }

    public void setSubscriptionURL(String subscriptionURL) {
        this.subscriptionURL = subscriptionURL;
        subscriptionEPR = new AlpineEPR(subscriptionURL);
        subscriptionEndpointer = subscriptionEPR.toXom(Constants.ENDPOINT_REFERENCE,
                Constants.WS_ADDRESSING_NAMESPACE, "wsa");
    }

    /**
     * Get a property value
     *
     * @param property
     * @return null for no match;
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if they feel like it
     */
    public List<Element> getProperty(QName property) {
        //delegate to the map
        return resources.getProperty(property);
    }

    /**
     * Create a subscription URL and set our URL to it. R
     * @param baseURL
     * @return the new URL
     */
    public String createSubscriptionURL(URL baseURL) {
        setSubscriptionURL(baseURL+ "?" + Constants.SUBSCRIPTION_ID_PARAM + "=" + id);
        return subscriptionURL;
    }

    public Element getSubscriptionEndpointer() {
        return subscriptionEndpointer;
    }

    public AlpineEPR getSubscriptionEPR() {
        return subscriptionEPR;
    }

    private static final String SEARCH_STRING= Constants.SUBSCRIPTION_ID_PARAM + "=";

    /**
     * Get the subscription ID from the query
     * @param query
     * @return the trimmed string containing the subscription
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException if not found
     */
    public static String extractSubscriptionIDFromQuery(String query) {
        if (query == null) {
            throw FaultRaiser.raiseNoSuchApplicationFault("No subscription in address");
        }
        int index = query.indexOf(Constants.SUBSCRIPTION_ID_PARAM);
        if (index == -1) {
            String message = "Didn't find query (" +
                    SEARCH_STRING +
                    ") in " +
                    query;
            throw FaultRaiser.raiseBadArgumentFault(message);
        }
        int start = index + SEARCH_STRING.length();
        int end = query.indexOf("&", start);
        if (end == -1) {
            end = query.length();
        }
        String substrate = query.substring(start, end).trim();
        if (substrate.length() == 0) {
            throw FaultRaiser.raiseBadArgumentFault("Empty subscription in " + query);
        }

        return substrate;
    }

}
