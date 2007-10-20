package org.smartfrog.test.unit.sfcore.languages.cdl.standard.suites.valid;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.test.unit.sfcore.languages.cdl.standard.CdlSmartFrogProcessorFactory;

/**
 */
public class Suite_05_StructureTest extends TestCase {

    /**
     * This is the suite
     *
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(Suite_05_StructureTest.class,
                CddlmConstants.TEST_PACKAGE_CDL_SET_05_STRUCTURE,
                new CdlSmartFrogProcessorFactory());
    }

}
