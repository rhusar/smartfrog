package org.smartfrog.test.unit.java;

import org.smartfrog.services.os.java.LocalCachePolicy;
import org.smartfrog.services.os.java.Maven1Policy;

/**
 */
public class Maven1LocalPolicyTest extends AbstractLocalPolicyTestBase {

    public Maven1LocalPolicyTest() {
    }


    LocalCachePolicy createPolicy() throws Exception {
        return new Maven1Policy();
    }

    public void testExpectedPath() throws Exception {
        String path = createLoggingPath();
        String expected = MAVEN1_PATH;
        assertEquals(expected, path);
    }

}
