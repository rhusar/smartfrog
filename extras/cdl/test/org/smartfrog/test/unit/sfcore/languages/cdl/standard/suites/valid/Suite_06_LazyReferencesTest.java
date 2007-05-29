package org.smartfrog.test.unit.sfcore.languages.cdl.standard.suites.valid;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.test.unit.sfcore.languages.cdl.standard.CdlSmartFrogProcessorFactory;

/**
 */
public class Suite_06_LazyReferencesTest extends TestCase {

    /**
     * This is the suite
     *
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(Suite_06_LazyReferencesTest.class,
                CddlmConstants.TEST_PACKAGE_CDL_SET_06_LAZY_REFERENCES,
                new CdlSmartFrogProcessorFactory());
    }

}
