package org.smartfrog.services.groovy.test.system.examples

import org.smartfrog.services.scripting.groovy.GroovyTestBase

class ComponentsTest extends GroovyTestBase {

    public void testTouchFile() {
        expectSuccessfulTestRun("/org/smartfrog/services/groovy/test/system/examples", "testTouchFile");
    }

}
