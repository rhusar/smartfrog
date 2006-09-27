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

/**
 * created 27-Sep-2006 12:16:34
 */
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
public interface WSNConstants {


    String SUBSCRIBE ="Subscribe";
    String SIMPLE_DIALECT="http://www.ibm.com/xmlns/stdwip/web-services/WSTopics/TopicExpression/simple";
    String CONSUMER_REFERENCE = "ConsumerReference";
    String TOPIC_EXPRESSION = "TopicExpression";
    String USE_NOTIFY = "UseNotify";
    String SUBSCRIPTION_POLICY = "SubscriptionPolicy";
    String INITIAL_TERMINATION_TIME = "InitialTerminationTime";
    String DIALECT = "dialect";
}
