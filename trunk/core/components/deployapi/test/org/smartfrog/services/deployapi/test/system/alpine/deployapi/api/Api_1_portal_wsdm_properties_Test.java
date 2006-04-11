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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api;

import org.ggf.cddlm.generated.api.CddlmConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import nu.xom.Element;


/**
 * created 11-Apr-2006 15:20:57
 */

public class Api_1_portal_wsdm_properties_Test extends StandardTestBase {

    private static final Log log= LogFactory.getLog(Api_1_portal_wsdm_properties_Test.class);

    public Api_1_portal_wsdm_properties_Test(String name) {
        super(name);
    }


    public void testResourceId() throws Exception {
        Element result = getPortalSession().getResourceProperty(CddlmConstants.PROPERTY_MUWS_RESOURCEID);
        final String value = result.getValue();
        log.info("Resource ID="+value);
        assertNotNull(value);
    }

}
