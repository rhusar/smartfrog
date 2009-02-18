/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.scripting.javax;

import org.smartfrog.sfcore.prim.PrimImpl;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Compilable;
import javax.script.ScriptException;
import javax.script.CompiledScript;
import java.io.Reader;

/**
 * Anything to help with script engine work
 */

public class ScriptHelper {

    private PrimImpl owner;
    private ScriptEngineManager manager;

    public ScriptHelper(PrimImpl owner) {
        this.owner = owner;
        manager = new ScriptEngineManager();
    }


    LoadedEngine createEngine(String language) {
        ScriptEngine engine = manager.getEngineByName(language);
        return new LoadedEngine(language, engine);
    }

    /**
     * Represents a loaded engine
     */
    public static class LoadedEngine {
        public ScriptEngine engine;
        public String language;
        private boolean isJavaFX;

        public LoadedEngine(String language, ScriptEngine engine) {
            this.engine = engine;
            this.language = language;
            isJavaFX = "FX".equalsIgnoreCase(language);
        }

        public ScriptEngine getEngine() {
            return engine;
        }

        public String getLanguage() {
            return language;
        }

        /**
         * Put a name value pair into the context of the engine.
         * This extends {@link ScriptEngine#put(String,Object)} by
         * special support for JavaFX naming policy, copying Ant's example
         * @param name name to use
         * @param value value to put
         */
        public void set(String name, Object value) {
            if (isJavaFX) {
                engine.put(name + ":" + value.getClass().getName(), value);
            } else {
                engine.put(name, value);
            }
        }

        public Object eval(String script) throws ScriptException {
            return engine.eval(script);
        }

        public Object eval(Reader reader) throws ScriptException {
            return engine.eval(reader);
        }

        public boolean canCompile() {
            return engine instanceof Compilable;
        }

        CompiledScript compile(String script)
                throws ScriptException {
            return getCompilable().compile(script);
        }

        CompiledScript compile(Reader reader)
                throws ScriptException {
            return getCompilable().compile(reader);
        }

        private Compilable getCompilable() {
            return ((Compilable)engine);
        }





    }
}
