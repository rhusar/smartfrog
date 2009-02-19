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
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Compilable;
import javax.script.ScriptException;
import javax.script.CompiledScript;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.rmi.RemoteException;

/**
 * Anything to help with script engine work
 */

public class ScriptHelper {

    private PrimImpl owner;
    private ComponentHelper compHelper;
    private ScriptEngineManager manager;
    public static final String ERROR_NO_ENGINE = "Failed to load a scripting engine for the language ";

    public ScriptHelper(PrimImpl owner) {
        this.owner = owner;
        compHelper = new ComponentHelper(owner);
        manager = new ScriptEngineManager();
    }


    public LoadedEngine createEngine(String language) throws SmartFrogLifecycleException {
        ScriptEngine engine = manager.getEngineByName(language);
        if(engine==null) {
            throw new SmartFrogLifecycleException(ERROR_NO_ENGINE
                    +language);
        }
        return new LoadedEngine(language, engine);
    }

    /**
     * Load a resource
     * @param resource resource to load
     * @return a reader for that resource
     * @throws SmartFrogException if the resource can not be found
     * @throws RemoteException for network problems
     */
    public Reader loadResource(String resource) throws SmartFrogException, RemoteException {
        InputStream inputStream = compHelper.loadResource(resource);
        return new InputStreamReader(inputStream);
    }


    /**
     * Represents a loaded engine, has some helper methods
     */
    public class LoadedEngine {

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
                engine.put(name + ':' + value.getClass().getName(), value);
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

        public Object evalResource(String resource) throws ScriptException, SmartFrogException, RemoteException {
            Reader reader = loadResource(resource);
            return eval(reader);
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


        public Object compileResource(String resource) throws ScriptException, SmartFrogException, RemoteException {
            Reader reader = loadResource(resource);
            return compile(reader);
        }

        private Compilable getCompilable() {
            return ((Compilable)engine);
        }


        /**
         * Bind the attributes of the owner to the script context.
         */
        public void bindAttributes() {
            set(Variables.PRIM, owner);
            set(Variables.SELF, owner);
            set(Variables.LOG, owner.sfLog());

            // go through all the attributes and bind them in the interpreter.
            for (Enumeration e = owner.sfContext().keys(); e.hasMoreElements();) {
                String attName = (String) e.nextElement();
                set(attName, owner.sfContext().get(attName));
            }
        }


        /**
         * Resolve the resource and inline attributes (both of which must be present),
         * <ol>
         * <li>If the resource attribute resolves to a non-empty string, then the attribute
         * {@link JavaxScript#ATTR_SF_SCRIPT_CODE_BASE} is resolved and prepended to the resource
         * attribute; the resource is then loaded and executed</li>
         * <li>If the inline attribute resolves to a non-empty string, it is evaluated</li>
         * </ol>
         * @param attrResource name of an attribute that should resolve to a resource
         * @param attrInline name of an an attribute containing an inline string
         * @return the result of the evaluation, or null if neither got evaluated
         * @throws SmartFrogException Failure to load the resource, resolve attributes
         * @throws RemoteException network problems
         * @throws ScriptException if the script failed
         */
        public Object resolveAndEvaluate(String attrResource, String attrInline) throws SmartFrogException,
                RemoteException, ScriptException {
            String resourceName = owner.sfResolve(attrResource, "", true);
            String inline = owner.sfResolve(attrInline, "", true);

            if(!resourceName.isEmpty()) {
                String codebase = owner.sfResolve(JavaxScript.ATTR_SF_SCRIPT_CODE_BASE,"",true);
                return evalResource(codebase+resourceName);
            } else if(!inline.isEmpty()) {
                return eval(inline);
            } else {
                return null;
            }
        }
    }
}
