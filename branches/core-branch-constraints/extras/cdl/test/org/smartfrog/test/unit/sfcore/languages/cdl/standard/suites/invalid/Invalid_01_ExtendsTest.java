package org.smartfrog.test.unit.sfcore.languages.cdl.standard.suites.invalid;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.test.unit.sfcore.languages.cdl.standard.CdlSmartFrogProcessorFactory;

/**
 */
public class Invalid_01_ExtendsTest extends TestCase {

    /**
     * This is the suite
     *
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(Invalid_01_ExtendsTest.class,
                CddlmConstants.TEST_PACKAGE_CDL_INVALID_SET_01,
                new CdlSmartFrogProcessorFactory());
    }

}
