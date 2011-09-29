package org.smartfrog.services.groovy.test.system.examples

import org.smartfrog.services.scripting.groovy.GroovyTestBase

class EchoTest extends GroovyTestBase {

    public void testEcho() {
        expectSuccessfulTestRun("/org/smartfrog/services/groovy/test/system/examples", "testEcho");
    }

}
