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

package org.smartfrog.services.deployapi.test.unit;

import nu.xom.Document;
import nu.xom.Nodes;
import org.apache.axis2.om.OMElement;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;

import java.io.InputStream;


/**

 */
public class UtilsTest extends UnitTestBase {

    public UtilsTest(String name) {
        super(name);
    }

    public void testLoadResource() throws Exception {
        InputStream in = loadResource(DOC_CREATE);
        String loaded = Utils.loadInputStream(in, Constants.CHARSET_UTF8);
        OMElement om1 = Utils.loadAxiomFromString(loaded);
        Document document = Utils.axiomToXom(om1);
        assertDocHasCreateTest(document);
        OMElement om2 = Utils.xomToAxiom(document);
        Document doc = Utils.axiomToXom(om2);
        assertDocHasCreateTest(document);
    }

    private Document assertDocHasCreateTest(Document document)  {
        Nodes n = document.query(
                "test:tests/test:test[@name='createRequestHostname']",Constants.XOM_CONTEXT);
        assertEquals(1, n.size());
        return document;
    }
}
