/**
 *
 */

package org.smartfrog.test.system.services.scripting.groovy;

import junit.framework.TestCase;
import org.smartfrog.test.SmartFrogTestBase;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class GroovyScriptEngineTest extends SmartFrogTestBase {

    public GroovyScriptEngineTest(final String name) {
        super(name);
    }

    public void testScriptEngineLoadsGroovy() throws Throwable {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("groovy");
        assertNotNull("No groovy found", engine);
        engine.put("key", "value");
        Object result = engine.eval("\"$key\"");
        String resultText = result.toString();
        getLog().info(resultText);
        assertEquals("value", resultText);
    }
}
