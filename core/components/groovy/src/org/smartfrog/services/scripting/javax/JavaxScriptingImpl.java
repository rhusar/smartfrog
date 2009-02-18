/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.services.scripting.RemoteScriptPrim;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * ScriptPrimImpl is a SmartFrog component which allows the user to write bits of java code in the description.
 * BeansShell is used to interpret them. It also implements the RemoteBSH which allows bsh scripts to be remotely
 * executed
 */
public class JavaxScriptingImpl
        extends PrimImpl
        implements Prim, RemoteScriptPrim {
    /**
     * The Beanshell interpreter
     */
    private ScriptHelper scriptHelper;
    public ScriptHelper.LoadedEngine interpreter;

    /**
     * The location of the code
     */
    private String sfScriptCodeBase = "";
    public static final String SCRIPT_PRIM = "prim";
    public static final String SCRIPT_STATUS = "status";
    public static final String ERROR_EVAL = "There was an error in evaluating the script:";

    /**
     * Standard RMI constructor
     * @throws RemoteException superclass trouble
     */
    public JavaxScriptingImpl() throws RemoteException {
        super();
    }


    /**
     * Deploy the ScriptPrimImpl component.
     * It binds the 'prim' string to this instance for the interpreter.
     * The following attributes are looked up : -
     * <ol>
     * <li>If the 'attributesAsVariables' attribute is set to true / "true" in
     * the description, binds all attributes present during deploy phase to their keys for easier use in the
     * interpreter.</li>
     *  <li> If the 'sfScriptCodeBase' attribute is present it will be used to find all the source scripts for all
     * phases.</li>
     * <li> If the 'sfDeployCode' is present the deploy script is evaluated.</li>
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        String language = sfResolve(ATTR_LANGUAGE, "", true);
        scriptHelper = new ScriptHelper(this);
        interpreter = scriptHelper.createEngine(language);
        try {
            interpreter.set(SCRIPT_PRIM, this);
            boolean attAsVar = sfResolve(ATTR_ATTRIBUTES_AS_VARIABLES, false, true);

            if (attAsVar) {
                // go through all the attributes and bind them in the interpreter.
                for (Enumeration e = sfContext().keys(); e.hasMoreElements();) {
                    String attName = (String) e.nextElement();
                    interpreter.set(attName, sfContext().get(attName));
                }
            }
            sfScriptCodeBase = sfResolve(ATTR_SF_SCRIPT_CODE_BASE, "", true);
            String sfDeployCodeSource = sfScriptCodeBase + sfResolve(ATTR_SF_DEPLOY_CODE);
            interpreter.eval(getScript(sfDeployCodeSource));
        } catch (ScriptException e) {
            throw SmartFrogLifecycleException.forward(ERROR_EVAL + e,
                    e, this);
        }

    }

    /**
     * Start phase : execute the code described with the 'sfStartCode' attribute.
     *
     * @throws Exception if the start phase fails.
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            String sfStartCodeSource = sfScriptCodeBase +
                    sfResolve(ATTR_SF_START_CODE);
            interpreter.eval(getScript(sfStartCodeSource));
        } catch (ScriptException e2) {
            throw SmartFrogLifecycleException.forward(ERROR_EVAL + e2.getMessage(),
                    e2, this);
        }
    }

    /**
     * During termination phase execute the code described within the 'sfTerminateWithCode' attribute. Any exception is
     * caught and left untreated.
     *
     * @param status the TerminationRecord for this phase.
     */
    public void sfTerminateWith(TerminationRecord status) {
        try {
            String sfTerminateWithCodeSource = sfScriptCodeBase +
                    sfResolve(ATTR_SF_TERMINATE_WITH_CODE);
            interpreter.set(SCRIPT_STATUS, status);
            interpreter.eval(getScript(sfTerminateWithCodeSource));
        } catch (SmartFrogResolutionException e) {
            sfLog().ignore(e);
        } catch (RemoteException e) {
            sfLog().error(e);
        } catch (ScriptException e) {
            sfLog().error(e);
        }
        super.sfTerminateWith(status);
    }


    /**
     * Bind an object to a name in the beanshell interpreter.
     *
     * @param name the name you want the object to be called in the interpreter
     * @param obj  the object you want to register in the interpreter.
     */
    public synchronized void setRemote(String name, Object obj) throws
            SmartFrogException, RemoteException {
        try {
            interpreter.set(name, obj);
        }
        catch (Throwable thr) {
            throw SmartFrogException.forward(thr);
        }
    }

    /**
     * Evaluate the String as a beanshell script. The string is handed off to the internal interpreter object
     *
     * @param script the script as a string.
     * @return the result
     * @throws SmartFrogException execution failure
     * @throws RemoteException network trouble
     */
    public synchronized Object eval(String script) throws SmartFrogException,
            RemoteException {
        try {
            return interpreter.eval(script);
        } catch (ScriptException e) {
            throw new SmartFrogException(e, this);
        }
    }

    /**
     * Interpret the string passed as a value in the ScriptPrimImpl description. If it's a file, return a reader on this
     * file. If it's the script itself, a reader on the string.
     *
     * @param script the String describing either the script or its URL.
     * @return a Reader on the script.
     */
    public Reader getScript(String script) {
        try {
            return new InputStreamReader(SFClassLoader.getResourceAsStream(script));
        } catch (Exception ignored) {
            return new StringReader(script);
        }
    }
}