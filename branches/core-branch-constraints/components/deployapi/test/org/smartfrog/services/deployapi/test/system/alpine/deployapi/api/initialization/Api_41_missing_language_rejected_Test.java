package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.initialization;

import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.ggf.cddlm.generated.api.CddlmConstants;
import static org.ggf.cddlm.generated.api.CddlmConstants.CDL_API_TYPES_NAMESPACE;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;

/**
 * created 13-Apr-2006 13:51:02
 */

public class Api_41_missing_language_rejected_Test extends StandardTestBase {




    public Api_41_missing_language_rejected_Test(String name) {
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

    public void testMissingLanguage() throws Exception {
        Document document = loadCdlDocument(CddlmConstants.INTEROP_API_TEST_DOC_1_VALID_DESCRIPTOR);
        Element cdl = (Element) document.getRootElement().copy();
        SoapElement request = getDescriptorHelper()
                .createInitRequestInline(null, cdl, null);
        try {

            getSystem().initialize(request);
            fail("This should have been rejected");
        } catch (Exception e) {
            //expected failure
        }

    }

}

