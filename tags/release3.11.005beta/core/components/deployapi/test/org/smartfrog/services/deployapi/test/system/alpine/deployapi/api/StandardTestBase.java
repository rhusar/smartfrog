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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.test.system.alpine.AlpineTestBase;
import org.smartfrog.services.xml.utils.XomUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * created 11-Apr-2006 14:56:59
 */

public abstract class StandardTestBase extends AlpineTestBase {


    protected int delay = 0;
    private static final String TEST_DELAY = "test.delay.seconds";
    /**
     * {@value}
     */
    public static final String TEST_IMPLEMENTED = "test.implemented";
    private static final int SUBSCRIBE_WAIT_TIMEOUT = 5000;
    private static final String PROPERTY_WAIT_TIMEOUT = "wait.timeout";

    protected StandardTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        String delayTime = getJunitParameter(TEST_DELAY, false);
        if (delayTime != null && delayTime.length() > 0) {
            delay = Integer.valueOf(delayTime);
        }
        if (delay > 0) {
            Thread.sleep(delay * 1000);
        }
    }

    protected boolean isSystemInActiveSystems(String id) {
        SoapElement activeSystems = (SoapElement) getPortal()
                .getResourcePropertySingle(CddlmConstants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);
        boolean found = false;
        for (Element e : activeSystems.elements()) {
            assertEquals(CddlmConstants.ELEMENT_NAME_SYSTEM, e.getLocalName());
            assertEquals(CddlmConstants.CDL_API_TYPES_NAMESPACE, e.getNamespaceURI());
            AlpineEPR epr = new AlpineEPR(e, CddlmConstants.WS_ADDRESSING_NAMESPACE);
            epr.validate();
            SystemSession system = new SystemSession(getPortal(), epr);
            String newResID = system.getResourcePropertySingle(CddlmConstants.PROPERTY_MUWS_RESOURCEID).getValue();
            assertNotNull(newResID);
            assertTrue(newResID.length() > 0);
            if (newResID.equals(id)) {
                found = true;
            }
        }
        return found;
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            fail("Sleep interrupted");
        }
    }

    /**
     * Create an addFile Request from the resource on the classpath.
     *
     * @param cdlResource resource
     * @param name        name to turn into a URI
     * @return a request with the payload inline
     * @throws SAXException       parse problem
     * @throws IOException        IO problem
     * @throws ParsingException   parse problem
     * @throws URISyntaxException bad URI
     */
    protected SoapElement createAddFileRequest(String cdlResource, String name) throws SAXException, IOException,
            ParsingException, URISyntaxException {
        Document document = loadCdlDocument(cdlResource);
        //base-64 encode it
        String encoded = XomUtils.base64Encode(document);

        SoapElement request = XomHelper.addFileRequest(
                new URI(name),
                "application+xml",
                "file",
                encoded,
                null
        );
        return request;
    }

    /**
     * Load a CDL Document and parse it under the Alpine SOAP parser
     *
     * @param cdlResource
     * @return a loaded document
     * @throws SAXException     parse problem
     * @throws IOException      IO problem
     * @throws ParsingException parse problem
     */
    public Document loadCdlDocument(String cdlResource) throws SAXException, IOException, ParsingException {
        SoapMessageParser parser = createXmlParser();
        Document document = parser.parseResource(cdlResource);
        return document;
    }

    /**
     * Throw an exception if the {@link #TEST_IMPLEMENTED} system property does not resolve
     */
    protected void failNotImplemented() {
        String implemented = getJunitParameter(TEST_IMPLEMENTED, false);
        if (implemented == null) {
            fail("This test has not been implemented, which may mean that the"
                    + " underlying features have not been implemented in the client");
        }
    }


    /**
     * load a CDL document and initialize the system inline with it
     *
     * @param cdlResource path to the resource containing the descriptor
     * @throws Exception if something went wrong.
     */
    protected void initializeSystem(String cdlResource) throws Exception {
        assertNotNull("No system", getSystem());
        Document document = loadCdlDocument(cdlResource);
        Element cdl = (Element) document.getRootElement().copy();
        SoapElement request = getDescriptorHelper().createInitRequestInline(CddlmConstants.XML_CDL_NAMESPACE, cdl, null);
        getSystem().initialize(request);
        getSystem().setTimeout(getSubscribeWaitTimeout());
    }

    /**
     * initialize and run a system from a CDL Resource
     *
     * @param cdlResource path to the resource containing the descriptor
     * @throws Exception if something went wrong.
     */
    protected void runSystem(String cdlResource) throws Exception {
        initializeSystem(cdlResource);
        getSystem().run();
    }

    /**
     * Get the junit parameter for waiting
     *
     * @return
     */
    protected int getSubscribeWaitTimeout() {
        return getJunitParameter(PROPERTY_WAIT_TIMEOUT, SUBSCRIBE_WAIT_TIMEOUT, false);
    }
}
