/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.xml.test.system;

import junit.framework.AssertionFailedError;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.services.xml.utils.ParserHelper;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.SmartFrogTestBase;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * base class for tests; currently extends the smartfrog testbase
 */
public abstract class XmlTestBase extends SmartFrogTestBase {

    public static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";

    /**
     * Well known attribute for xml under a component
     */
    public static final String ATTR_XML = "xml";

    /**
     * Node of any deployed application
     */
    private Prim application;

    protected XmlTestBase(String name) {
        super(name);
    }

    /**
     * location for files.
     * {@value}
     *
     */
    public static final String FILE_BASE = "/org/smartfrog/services/xml/test/files/";

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        assertSystemPropertySet(CODEBASE_PROPERTY);
    }


    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        //terminate the node if it is not null.
        terminateApplication(application);
    }

    /**
     * Deploy an XML node
     * @param url
     * @param appName
     * @return
     * @throws Throwable
     */
    public XmlNode deployXmlNode(String url,String appName) throws Throwable {
        Prim prim = deployApplication2(url, appName);
        try {
            return (XmlNode) prim;
        } catch (Exception e) {
            terminateApplication(prim);
            throw e;
        }
    }

    /**
     * Deploy an application.
     * @param url
     * @param appName
     * @return
     * @throws Throwable
     */
    protected Prim deployApplication2(String url, String appName)
            throws Throwable {
        Prim prim = deployExpectingSuccess(url, appName);
        application = prim;
        return prim;
    }

    /**
     * Get the deployed application, or null
     * @return application, if deployed
     */
    public Prim getApplication() {
        return application;
    }

    /**
     * Get the application (if deployed) as an XML Node (if it is one)
     * @return the application or null
     * @throws ClassCastException if the app doesn't implement XmlNode
     */
    public XmlNode getApplicationAsNode() {
        if(application==null) {
            return null;
        }
        return (XmlNode)application;
    }

    /**
     * terminate a deployed app of type xmlnode. no-op if null
     * @param node node to terminate
     * @throws RemoteException
     */
    protected void terminateNode(XmlNode node) throws RemoteException {
        terminateApplication((Prim)node);
    }

    /**
     * load an XML File
     * @param file file to load
     * @param validate flag to ask for a validating parser
     * @return the loaded document.
     * @throws SAXException
     * @throws ParsingException
     * @throws IOException
     */
    public Document loadXMLFile(File file, boolean validate) throws SAXException,
            ParsingException, IOException {
        XMLReader xmlParser = ParserHelper.createXmlParser(validate,true,false);
        Builder builder = new Builder(xmlParser, validate);
        Document document = builder.build(file);
        return document;
    }


    /**
     * resolve the xmlnode name {@link #ATTR_XML} in the application
     * @return the resolved node
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws AssertionFailedError if the application is null
     */
    protected XmlNode resolveXmlNode() throws SmartFrogResolutionException,
            RemoteException {
        assertNotNull(getApplication());
        return (XmlNode) getApplication().sfResolve(ATTR_XML,(Prim)null,true);
    }
}
