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
package org.smartfrog.services.deployapi.notifications.muws;

import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.xmlutils.NodesIterator;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import nu.xom.Node;
import nu.xom.XPathContext;

/**
 * created 10-Oct-2006 16:10:38
 */

public class ReceivedEvent {

    private static final XPathContext catalog= CdlCatalog.createXPathContext();
    public SoapElement message;
    long timestamp;

    public ReceivedEvent(MessageContext messageContext, SoapElement event) {
        message=event;
        timestamp=System.currentTimeMillis();
    }

    /*
    <cmp:LifecycleTransition>
<muws-p2-xs:StateTransition Time=”2005-03-01T01:54:30Z”>
<muws-p2-xs:EnteredState>
<cmp:RunningState/>
</muws-p2-xs:EnteredState>
<muws-p2-xs:PreviousState>
<cmp:InitializedState/>
</muws-p2-xs:PreviousState>
</muws-p2-xs:StateTransition>
</cmp:LifecycleTransition>
     */
    public LifecycleStateEnum getState() {

        for(Node n:message.xpath("./cmp:LifecycleTransition/muws-p2-xs:StateTransition/muws-p2-xs:EnteredState/*", catalog)) {
            SoapElement e=(SoapElement) n;
            String statename = e.getLocalName();
            return LifecycleStateEnum.extract(statename);
        }
        throw new CdlXmlParsingException("no EnteredState transition in \n"+message.toXML());
    }

}
