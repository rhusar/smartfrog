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
package org.smartfrog.services.deployapi.test.unit;

import junit.framework.TestCase;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;
import org.smartfrog.services.deployapi.notifications.Event;
import org.smartfrog.services.deployapi.notifications.muws.ReceivedEvent;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.xmlutils.NodesIterator;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import nu.xom.XPathContext;
import nu.xom.Node;
import nu.xom.Element;

/**
 * created 31-Oct-2006 14:46:06
 */

public class CmpStateTest extends TestCase {
    private static final Log log= LogFactory.getLog(CmpStateTest.class);
    private NotificationSubscription sub;
    private Event event;
    private SoapElement message;
    private ReceivedEvent rx;
    private final XPathContext catalog = CdlCatalog.createXPathContext();

    /**
     * Constructs a test case with the given name.
     */
    public CmpStateTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        sub = new NotificationSubscription();
        event = new Event(null, LifecycleStateEnum.running, LifecycleStateEnum.failed, null);
        message = sub.createMuwsLifecycleEvent(event);
        rx = new ReceivedEvent(null, message);
    }
    public void testRoundTrip() throws Exception {
        LifecycleStateEnum state = rx.getState();
        assertEquals(event.state, state);
    }


    public void testResolve1t() throws Exception {
        Element element = assertResolvesE(".");
    }

    public void testResolve2() throws Exception {
        assertResolvesE("./cmp:LifecycleTransition");
    }

    public void testResolve3() throws Exception {
        assertResolves("./cmp:LifecycleTransition/muws-p2-xs:StateTransition/*", 2);
    }

    public void testResolve4() throws Exception {
        assertResolvesE(
                "./cmp:LifecycleTransition/muws-p2-xs:StateTransition/muws-p2-xs:EnteredState/*");
    }

    private NodesIterator assertResolves(String path) {
        return assertResolves(path, 1);
    }

    private Element assertResolvesE(String path) {
        NodesIterator nodesIterator = assertResolves(path, 1);
        Node n=nodesIterator.get(0);
        assertTrue("Not an element "+n.toString(),n instanceof Element);
        return (Element)n;
    }

    private NodesIterator assertResolves(String path, int size) {
        NodesIterator nodes = message.xpath(path, catalog);
        int actual = nodes.size();
        assertEquals("Could not resolve "+path,size, actual);
        return nodes;
    }


}
