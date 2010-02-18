package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import org.smartfrog.test.unit.sfcore.languages.cdl.Filenames;
import org.ggf.cddlm.generated.api.CddlmConstants;

/**
 */
public class CdlResolutionTest extends CdlDocumentTestBase implements Filenames {

    /**
     * implementations should return the name of the resource dir
     *
     * @return
     */
    protected String getTestFileResourceDir() {
        return CddlmConstants.TEST_PACKAGE_CDL_SET_01;
    }


}
