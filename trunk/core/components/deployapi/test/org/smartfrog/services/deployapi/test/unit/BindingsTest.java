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

import org.smartfrog.services.deployapi.binding.bindings.LookupSystemBinding;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.system.Constants;
import org.ggf.xbeans.cddlm.api.LookupSystemRequestDocument;
import org.apache.axis2.om.OMElement;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.Element;


/**
 * Unit test for the underpinnings
 * created 21-Sep-2005 14:50:46
 */

public class BindingsTest extends UnitTestBase {
    public static final String RESOURCE_ID = "12345";

    public BindingsTest(String name) {
        super(name);
    }

    public void testSimpleFactory() throws Exception {
        LookupSystemRequestDocument doc=LookupSystemRequestDocument.Factory.newInstance();
    }

    public void testLookupSystemBinding() throws Exception {
        LookupSystemBinding binding = new LookupSystemBinding();
        LookupSystemRequestDocument doc=binding.createRequest();
    }

    public void testAxiomToXom() throws Exception {
        LookupSystemBinding binding = new LookupSystemBinding();
        LookupSystemRequestDocument request = binding.createRequest();
        LookupSystemRequestDocument.LookupSystemRequest lookupSystemRequest = request.addNewLookupSystemRequest();
        lookupSystemRequest.setResourceId(RESOURCE_ID);
        OMElement om = binding.convertRequest(request);
        Document doc= Utils.axiomToXom(om);
        Nodes nodes = doc.query("api:lookupSystemRequest/api:ResourceId",
                Constants.XOM_CONTEXT);
        assertEquals("query returned one node",1,nodes.size());
        Element elt=(Element) nodes.get(0);
        assertEquals(RESOURCE_ID,elt.getValue());
    }

}
