package org.smartfrog.test.unit.sfcore.languages.cdl.standard.suites.valid;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.test.unit.sfcore.languages.cdl.standard.CdlSmartFrogProcessorFactory;

/**
 */
public class Suite_04_ImportTest extends TestCase {

    /**
     * This is the suite
     *
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(Suite_04_ImportTest.class,
                CddlmConstants.TEST_PACKAGE_CDL_SET_04,
                new CdlSmartFrogProcessorFactory());
    }

}
