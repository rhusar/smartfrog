package org.smartfrog.test.unit.java;

import org.smartfrog.services.os.java.LocalCachePolicy;
import org.smartfrog.services.os.java.Maven2Policy;

/**
 */
public class Maven2LocalPolicyTest extends AbstractLocalPolicyTestBase {

    public Maven2LocalPolicyTest() {
    }

    LocalCachePolicy createPolicy() throws Exception {
        return new Maven2Policy();
    }

    public void testExpectedPath() throws Exception {
        String path = createLoggingPath();
        String expected = MAVEN2_PATH;
        assertEquals(expected, path);
    }

}
