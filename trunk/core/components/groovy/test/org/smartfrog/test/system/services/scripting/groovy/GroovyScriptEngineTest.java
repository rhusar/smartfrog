/**
 *
 */

package org.smartfrog.test.system.services.scripting.groovy;

import junit.framework.TestCase;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class GroovyScriptEngineTest extends TestCase {


    public void testScriptEngineLoadsGroovy() throws Throwable {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("groovy");
        assertNotNull("No groovy found", engine);
        engine.put("key", "value");
        Object result = engine.eval("\"$key\"");
        System.out.println(result);
        assertEquals("value", result.toString());
    }
}
