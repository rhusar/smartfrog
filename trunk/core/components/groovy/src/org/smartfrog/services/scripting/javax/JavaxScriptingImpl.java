/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import javax.script.ScriptException;
import java.rmi.RemoteException;

/**
 * ScriptPrimImpl is a SmartFrog component which allows the user to write bits of java code in the description.
 * BeansShell is used to interpret them. It also implements the RemoteBSH which allows bsh scripts to be remotely
 * executed
 */
public class JavaxScriptingImpl extends PrimImpl implements JavaxScript {
    /**
     * The Beanshell interpreter
     */
    private ScriptHelper scriptHelper;
    private ScriptHelper.LoadedEngine engine;

    public static final String ERROR_EVAL = "There was an error in evaluating the script:";

    /**
     * Standard RMI constructor
     * @throws RemoteException superclass trouble
     */
    public JavaxScriptingImpl() throws RemoteException {
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
    @Override
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        String language = sfResolve(ATTR_LANGUAGE, "", true);
        scriptHelper = new ScriptHelper(this);
        engine = scriptHelper.createEngine(language);
        engine.bindAttributes();
        resolveAndEvaluate(ATTR_SF_DEPLOY_RESOURCE, ATTR_SF_DEPLOY_CODE);
    }

    /**
     * Start phase : execute the startup code, then maybe begin the termination phase
     *
     * @throws SmartFrogException startup failure
     * @throws RemoteException remote failure
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        resolveAndEvaluate(ATTR_SF_START_RESOURCE, ATTR_SF_START_CODE);
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,null,sfCompleteName(),null);
    }

    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        try {
            engine.resolveAndEvaluate(ATTR_SF_PING_RESOURCE, ATTR_SF_PING_CODE);
        } catch (Exception e) {
            throw (SmartFrogLivenessException)
                    SmartFrogLivenessException.forward(ERROR_EVAL + e, e);
        }
    }

    /**
     * During termination phase execute the code described within the 'sfTerminateWithCode' attribute. Any exception is
     * caught and left untreated.
     *
     * @param status the TerminationRecord for this phase.
     */
    @Override
    public void sfTerminateWith(TerminationRecord status) {
        try {
            engine.resolveAndEvaluate(ATTR_SF_TERMINATE_WITH_RESOURCE, ATTR_SF_TERMINATE_WITH_CODE);
        } catch (SmartFrogException e) {
            sfLog().ignore(e);
        } catch (RemoteException e) {
            sfLog().error(e);
        } catch (ScriptException e) {
            sfLog().error(e);
        }
        super.sfTerminateWith(status);
    }

    /**
     * Resolve the attributes, evaluate the code, convert Scripting Exceptions into SmartFrog ones
     * @param resource resource attribute to look for
     * @param inline inline source attribute to look for
     * @return the return value of the evaluation
     * @throws SmartFrogException if the resolution or the script fails
     * @throws RemoteException network problems
     */
    protected Object resolveAndEvaluate(String resource, String inline) throws SmartFrogException, RemoteException {
        try {
            return engine.resolveAndEvaluate(resource, inline);
        } catch (ScriptException e) {
            throw SmartFrogLifecycleException.forward(ERROR_EVAL + e,
                    e, this);
        }
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
            engine.set(name, obj);
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
            return engine.eval(script);
        } catch (ScriptException e) {
            throw new SmartFrogException(e, this);
        }
    }


}