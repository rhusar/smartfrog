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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.services.xml.utils.ParserHelper;
import org.smartfrog.sfcore.prim.Prim;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.rmi.RemoteException;
import java.io.IOException;
import java.io.File;

import nu.xom.ParsingException;
import nu.xom.Document;
import nu.xom.Builder;

/**
 * base class for tests; currently extends the smartfrog testbase
 */
public abstract class TestBase extends SmartFrogTestBase {

    public static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";

    protected TestBase(String name) {
        super(name);
    }

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
     * Deploy an XML node
     * @param url
     * @param appName
     * @return
     * @throws Throwable
     */
    public XmlNode deployXmlNode(String url,String appName) throws Throwable {
        Prim prim=  deployExpectingSuccess(url, appName);
        try {
            XmlNode node=(XmlNode) prim;
            return node;
        } catch (Exception e) {
            terminateApplication(prim);
            throw e;
        }
    }

    /**
     * terminate a deployed app of type xmlnode
     * @param node
     * @throws RemoteException
     */
    protected void terminateNode(XmlNode node) throws RemoteException {
        terminateApplication((Prim)node);
    }

    /**
     * load an XML File
     * @param file
     * @return
     * @throws SAXException
     * @throws ParsingException
     * @throws IOException
     */
    public Document loadXMLFile(File file) throws SAXException,
            ParsingException, IOException {
        XMLReader xmlParser = ParserHelper.createXmlParser(true,true,false);
        Builder builder = new Builder(xmlParser, true);
        Document document = builder.build(file);
        return document;
    }
}
