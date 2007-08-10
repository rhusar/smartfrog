package org.smartfrog.test.unit.projects.alpine.other;

import org.smartfrog.test.unit.projects.alpine.ParserTestBase;
import org.smartfrog.projects.alpine.xmlutils.ParserHelper;
import org.xml.sax.XMLReader;

/**
 */
public class XercesTest extends ParserTestBase {


    public XercesTest(String name) {
        super(name);
    }


    public void testTTT() throws Exception {
        XMLReader xerces = ParserHelper.createXmlParser(true, true, true);
        assertNotNull(xerces);
    }


    public void testFFF() throws Exception {
        XMLReader xerces = ParserHelper.createXmlParser(false,false,false);
        assertNotNull(xerces);
    }

}
