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
package org.smartfrog.services.deployapi.test.system.alpine;

import junit.framework.TestCase;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import nu.xom.ParsingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.om.soap11.SoapFactory;
import org.smartfrog.projects.alpine.om.soap11.Soap11Constants;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.DirectExecutor;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.alpineclient.model.PortalSession;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.File;

/**
 * created 11-Apr-2006 14:57:19
 */

public abstract class AlpineTestBase extends TestCase {

    protected static final Log log = LogFactory.getLog(AlpineTestBase.class);
    private AlpineEPR portalEPR;
    private PortalSession portal;
    private SystemSession system;
    public static final String ENDPOINT_PROPERTY = "endpoint";
    private boolean validating = false;
    private boolean concurrent = false;
    public static final String CONCURRENT_PROPERTY = "concurrent";
    public static final String VALIDATING_PROPERTY = "validating";
    public XPathContext xpath;
    private ResourceLoader resourceLoader;
    DescriptorHelper descriptorHelper;
    File tempdir;

    /**
     * Constructs a test case with the given name.
     */
    protected AlpineTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        //TODO: be more dynamic on smartfrog and so read our settings directly.
        // But how to get our deploying prim?
        String target = getJunitParameter(ENDPOINT_PROPERTY, true);
        bindToPortal(target);
        xpath = CdlCatalog.createXPathContext();
        resourceLoader = new ResourceLoader(getClass());
        tempdir= FileSystem.tempDir("deployapi","tmp",null);
        descriptorHelper = new DescriptorHelper(tempdir);
    }


    /**
     * Destroy any system we are bonded to during teardown
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (system != null) {
            system.destroy();
        }
        FileSystem.recursiveDelete(tempdir);
    }

    /**
     * Get a junit parameter. Fail if it is missing and required=true
     *
     * @param property property name
     * @return the property or null for not found
     */
    protected String getJunitParameter(String property, boolean required) {
        String target = System.getProperty(property);
        if (required && target == null) {
            fail("No property " + property);
        }
        return target;
    }



    /**
     * bind to a target; set up the endpoint, endpointURL and epr fields from the target URL
     *
     * @param target
     * @throws java.net.MalformedURLException
     */
    protected void bindToPortal(String target) throws MalformedURLException {
        portalEPR = new AlpineEPR(target);
        concurrent = getBoolParameter(CONCURRENT_PROPERTY);
        validating = getBoolParameter(VALIDATING_PROPERTY);
        final Executor executor = createExecutor();
        portal = new PortalSession(portalEPR, validating, new TransmitQueue(executor));
    }


    public static Log getLog() {
        return log;
    }

    private Boolean getBoolParameter(String property) {
        return Boolean.valueOf(getJunitParameter(property, false));
    }

    public AlpineEPR getPortalEPR() {
        return portalEPR;
    }

    public PortalSession getPortal() {
        return portal;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }


    public File getTempdir() {
        return tempdir;
    }

    public DescriptorHelper getDescriptorHelper() {
        return descriptorHelper;
    }

    /**
     * Override point: create the executor for this project.
     *
     * @return a direct or concurrent executor.
     */
    protected Executor createExecutor() {
        return concurrent ?
                createConcurrentExecutor() :
                new DirectExecutor();
    }

    /**
     * override point; create an executor for concurrent execution
     * defaults to {@link java.util.concurrent.Executors#newSingleThreadExecutor()}
     *
     * @return a concurrent executor
     */
    protected ExecutorService createConcurrentExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    protected Element getProperty(QName property) {
        Element result = getPortal().getResourceProperty(property);
        final String value = result.getValue();
        assertNotNull(value);
        return result;
    }

    public Element getPropertyLog(QName property) {
        Element result = getProperty(property);
        if (log.isInfoEnabled()) {
            log.info(property + " = " + result);
        }
        return result;
    }

    protected void assertCapable(String uri) {
        Element capabilities = getMuwsCapabilities();
        assertTrue("Missing capability " + uri, WsrfUtils.hasMuwsCapability(capabilities, uri));
    }

    protected Element getMuwsCapabilities() {
        return getProperty(CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CHARACTERISTICS);
    }

    protected void assertQueryResolves(SoapElement element, String query) {
        Nodes nodes = element.query(query, xpath);
        assertTrue("did not resolve :" + query, nodes.size() > 0);
    }

    public SystemSession getSystem() {
        return system;
    }

    public void setSystem(SystemSession system) {
        this.system = system;
    }

    public XPathContext getXpath() {
        return xpath;
    }

    public void setXpath(XPathContext xpath) {
        this.xpath = xpath;
    }

    protected SystemSession createSystem(String hostname) {
        SystemSession system = getPortal().create(hostname);
        //this sets it up for cleanup on teardown
        setSystem(system);
        return system;
    }

    protected SoapMessageParser createXmlParser() throws SAXException {
        return new SoapMessageParser(new org.smartfrog.projects.alpine.xmlutils.ResourceLoader(getClass()),
                Soap11Constants.URI_SOAP12, false, new SoapFactory());
    }

    protected MessageDocument parseString(String xml,String uri) throws IOException, ParsingException, SAXException {
        SoapMessageParser xmlParser = createXmlParser();
        return xmlParser.parseString(xml,uri);
    }

}
