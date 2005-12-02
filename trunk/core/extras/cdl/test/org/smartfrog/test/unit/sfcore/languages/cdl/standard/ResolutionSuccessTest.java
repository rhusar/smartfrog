package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.smartfrog.test.unit.sfcore.languages.cdl.Filenames;

/**
 */
public class ResolutionSuccessTest extends TestCase  {

    /**
     * This is the suite
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(Filenames.NORMATIVE_VALID_RESOLUTION,
                new CdlSmartFrogProcessorFactory());
    }

}
