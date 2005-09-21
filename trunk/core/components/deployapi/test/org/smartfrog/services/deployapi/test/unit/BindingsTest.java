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

import junit.framework.TestCase;
import org.smartfrog.services.deployapi.client.PortalEndpointer;
import org.smartfrog.services.deployapi.client.ConsoleOperation;
import org.smartfrog.services.deployapi.client.Deploy;
import org.smartfrog.services.deployapi.binding.bindings.LookupSystemBinding;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.ggf.xbeans.cddlm.api.LookupSystemRequestDocument;


/**
 * Unit test for the underpinnings
 * created 21-Sep-2005 14:50:46
 */

public class BindingsTest extends UnitTestBase {

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


}
