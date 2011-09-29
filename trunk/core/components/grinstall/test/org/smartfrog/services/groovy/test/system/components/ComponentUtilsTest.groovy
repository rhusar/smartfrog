package org.smartfrog.services.groovy.test.system.components

import org.smartfrog.services.groovy.install.utils.ComponentUtils

/**
 *
 */

class ComponentUtilsTest extends GroovyTestCase {

    public void testClassExtraction() throws Throwable {

        ComponentUtils cu = new ComponentUtils()
        def tree = cu.extractClassHierarchy(this)
        GroovyTestCase gtc = this;
        assertTrue("No GroovyTestCase in $tree",
                tree.contains("GroovyTestCase"))
    }
}