package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.smartfrog.test.unit.sfcore.languages.cdl.Filenames;

/**
 */
public class ResolutionFailureTest extends TestCase {

    /**
     * This is the suite
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(Filenames.NORMATIVE_INVALID_RESOLUTION,
                new CdlSmartFrogProcessorFactory());
    }

}
