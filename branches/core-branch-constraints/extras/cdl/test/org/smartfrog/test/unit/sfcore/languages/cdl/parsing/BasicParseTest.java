/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.unit.sfcore.languages.cdl.parsing;

import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

/**
 * created 20-Jun-2005 16:07:10
 */

public class BasicParseTest extends XmlTestBase {

    public BasicParseTest(String name) {
        super(name);
    }


    public void testTextRetained() throws Exception {
        //test that text is retained
        CdlDocument doc = loadCDLToDOM(CDL_DOC_TYPE_1);
        String localname = "WebServer";
        PropertyList component = lookup(doc, localname);
        PropertyList port = component.getChildTemplateMatching("", "port");
        assertNotNull(port);
        assertAttributeValueEquals(port, CddlmConstants.XML_CDL_NAMESPACE,
                CddlmConstants.ATTRIBUTE_USE, "required");
        assertAttributeValueEquals(port, CddlmConstants.XML_CDL_NAMESPACE,
                CddlmConstants.ATTRIBUTE_TYPE, "xsd:positiveInteger");
        assertElementValueEquals(port, "80");
    }

}
