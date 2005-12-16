package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.test.unit.sfcore.languages.cdl.Filenames;

/**
 */
public class ResolutionFailureTest extends TestCase {

    /**
     * This is the suite
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(ResolutionFailureTest.class,
                CddlmConstants.TEST_PACKAGE_CDL_INVALID_SET_01,
                new CdlSmartFrogProcessorFactory());
    }

}
