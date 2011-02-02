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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.initialization;

import nu.xom.Document;
import nu.xom.Element;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_26_unknown_languages_rejected_Test extends StandardTestBase {




    public Api_26_unknown_languages_rejected_Test(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createSystem(null);
    }

    public void testUnknownLanguage() throws Exception {
        Document document = loadCdlDocument(CddlmConstants.INTEROP_API_TEST_DOC_1_VALID_DESCRIPTOR);
        Element cdl = (Element) document.getRootElement().copy();
        SoapElement request = getDescriptorHelper()
                .createInitRequestInline("http://www.gridforum.org/namespaces/2008/02/cddlm/CDL-1.5", cdl, null);
        try {
            getSystem().initialize(request);
            fail("expected a fault");
        } catch (AlpineRuntimeException e) {
            //success
        }
    }

}

