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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.cddlm.generated.api.CddlmConstants;


/**
 * created 11-Apr-2006 15:20:57
 */

public class Api_1_portal_wsdm_properties_Test extends StandardTestBase {

    private static final Log log = LogFactory.getLog(Api_1_portal_wsdm_properties_Test.class);

    public Api_1_portal_wsdm_properties_Test(String name) {
        super(name);
    }


    public void testResourceId() throws Exception {
        getPropertyLog(CddlmConstants.PROPERTY_MUWS_RESOURCEID);
    }

    public void testMuwsCapabilitiesExists() throws Exception {
        getPropertyLog(CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CAPABILITY);
    }

    public void testCapabilityMuwsManageabilityReferences() throws Exception {
        assertCapable(CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_REFERENCES);
    }

    public void testCapabilityMuwsManageabilityCharacteristics() throws Exception {
        assertCapable(CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_CHARACTERISTICS);
    }

    public void testCapabilityPortal() throws Exception {
        assertCapable(CddlmConstants.CDL_API_PORTAL_CAPABILITY);
    }

    public void testWsTopics() throws Exception {
        getPropertyLog(CddlmConstants.PROPERTY_WSNT_TOPIC);
    }

    public void testWsTopicSet() throws Exception {
        getPropertyLog(CddlmConstants.PROPERTY_WSNT_FIXED_TOPIC_SET);
    }

    public void testWsTopicDialogs() throws Exception {
        getPropertyLog(CddlmConstants.PROPERTY_WSNT_TOPIC_EXPRESSION_DIALOGS);
    }

}
