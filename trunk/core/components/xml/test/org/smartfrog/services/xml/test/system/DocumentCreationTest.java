/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.xml.interfaces.XmlNode;
import org.smartfrog.services.xml.interfaces.XmlDocument;
import org.smartfrog.services.xml.interfaces.XmlTextNode;
import org.smartfrog.services.xml.utils.ParserHelper;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import junit.awtui.TestRunner;

import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ValidityException;
import nu.xom.ParsingException;
import nu.xom.Document;

/**
 * created 27-Jan-2005 14:02:01
 */

public class DocumentCreationTest extends TestBase {

    public DocumentCreationTest(String name) {
        super(name);
    }

    /**
     * load a document, save it to a temp file; reload it
     * @throws Throwable
     */
    public void testDocumentLoad() throws Throwable {
        XmlNode node=deployXmlNode(FILE_BASE+"testDocument.sf","testDocument");
        File tempfile=null;
        try {
            XmlDocument doc=(XmlDocument) node;
            String xml=doc.toXML();
            tempfile = File.createTempFile("doc",".xml");
            doc.save(tempfile.getAbsolutePath());
            Document xdom=loadXMLFile(tempfile);
        } finally {
            terminateNode(node);
            if(tempfile!=null) {
                tempfile.delete();
            }
        }
    }

    public void testTextNode() throws Throwable {
        XmlNode node = deployXmlNode(FILE_BASE + "textNode.sf",
                "textNode");
        try {
            XmlTextNode element = (XmlTextNode) node;
            String xml = element.toXML();
            assertContains(xml,"nested text element");
        } finally {
            terminateNode(node);
        }
    }


}
