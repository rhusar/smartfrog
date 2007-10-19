package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.smartfrog.test.unit.sfcore.languages.cdl.Filenames;

/**
 */
public class ResolutionDebuggingTest extends TestCase  {

    /**
     * This is the suite
     * @return
     */
    public static TestSuite suite() {
        return SingleDocumentTestCase.createSuite(ResolutionDebuggingTest.class,
                Filenames.VALID_RESOURCES+"debugging",
                new CdlSmartFrogProcessorFactory());
    }

}
