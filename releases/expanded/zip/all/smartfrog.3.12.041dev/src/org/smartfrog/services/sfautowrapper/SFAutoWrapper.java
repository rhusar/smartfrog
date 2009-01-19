/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.sfautowrapper;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 *  Method
 *  Attributes []
 *  Next -> if not provided then next in Context.
 *  Failure -> Ref to next, it can be the attribute name with "" or Reference
 *  Condition _> String - use match (Regex)
 *               Integer =
 *               Booelan true/false
 *  Result -> created but what ever the object returns.  Also I could store/udate the values provided as parameters
 *
 *  ?What happens with constructor of an object.
 *    - Object -> contructor CD -> Instantiates object and place it here or provide object
 *    - Methods executed in it.
 *    - What about classloading? Constructor will do that even if it does not construct anything but uses file scanner
 */


/**
 * <br>SmartFrog automatic wrapper for Java objects
 */
public class SFAutoWrapper extends PrimImpl implements Prim {

    protected static LogSF staticLog = LogFactory.sfGetProcessLog();
    private static final String ATTR_OBJECT = "object";
    private static final String ATTR_METHOD = "method";
    private static final String ATTR_RESULT = "result";
    private static final String ATTR_NEXT = "next";
    private static final String ATTR_FAILURE = "failure";
    private static final String ATTR_MAIN = "main";
    private static final String ATTR_LIB_DIR = "libDir";
    private static final String ATTR_CLASS = "class";
    private static final String ATTR_LIB_PATTERN = "libPattern";
    private static final String ATTR_SYSTEM_PROPERTIES = "systemProperties";
    private static final String ATTR_FIELDS = "fields";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PARAMETERS = "parameters";


    /**
     * Default Constructor
     *
     * @throws RemoteException if the superclass is unhappy
     */
    public SFAutoWrapper() throws RemoteException {
    }

    /**
     * sfDeploy lifecycle method; setup information is collected here
     *
     * @throws SmartFrogException Deployment problems
     * @throws RemoteException    netork problems
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        staticLog = sfLog();
        ComponentDescription cd = null;
        try {
            constructor(sfResolve("constructor", cd, false));
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Failed during constructor!", e);
            }
            throw SmartFrogException.forward("Failed during constructor!", e);
        }
        if (sfLog().isInfoEnabled()) {
            sfLog().info("Autowrapper deployed!");
        }

    }

    /**
     * main extends DATA { object LAZY constructor:result; method extends DATA { name "version"; parameters extends
     * Vector {}; } next failure condition }
     *
     * @param cd the CD to work with
     * @throws SmartFrogResolutionException resolution problems
     * @throws IllegalAccessException forbidden methods
     */
    void runStateMachine(ComponentDescription cd) throws IllegalAccessException, SmartFrogResolutionException {
        // TODO a real state machine
        if (cd == null) {
            if (sfLog().isWarnEnabled()) {
                sfLog().warn("interrupted!");
            }
            return;
        } else {
            if (sfLog().isInfoEnabled()) {
                sfLog().info("Processing CD: \n" + cd.toString());
            }
        }
        Object object = null;
        ComponentDescription method = null;
        ComponentDescription next = null;
        ComponentDescription failure = null;

        object = cd.sfResolve(ATTR_OBJECT, true);
        if (object instanceof SFNull) {
            if (sfLog().isInfoEnabled()) {
                sfLog().info("Completed!");
            }
            return;
        }
        method = cd.sfResolve(ATTR_METHOD, method, true);
        Object result = null;
        try {
            result = invokeMethod(object, method);
            if (result == null) {
                result = SFNull.get();
            }
            cd.sfReplaceAttribute(ATTR_RESULT, result);
            //condition = cd.sfResolve("condition", condition, true);
            if (sfLog().isInfoEnabled()) {
                sfLog().info("result (method: " + method.sfResolve(ATTR_NAME, false) + "): " + result);
            }
            next = cd.sfResolve(ATTR_NEXT, next, true);
        } catch (Exception e) {
            if (log().isErrorEnabled()) log().err(e);
            failure = cd.sfResolve(ATTR_FAILURE, (ComponentDescription) null, false);
        }
        if (next != null) {
            //TODO Should evaluate condition
            runStateMachine(next);
        } else if (failure != null) {
            runStateMachine(failure);
        } else {
            if (sfLog().isInfoEnabled()) {
                sfLog().info("This should not happen!");
            }
        }

    }

    /**
     * sfStart lifecycle method starts the inner thread to decouple the caller
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (sfLog().isInfoEnabled()) {
            sfLog().info("Autowrapper started!");
        }

        ComponentDescription cd = null;
        try {
            runStateMachine(sfResolve(ATTR_MAIN, cd, false));
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Failed during StateMachine!", e);
            }
        }
        /* this is naughty; we are synchronized. Commenting out
        sfLog().info("Going to sleep for some time...");
        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException e) {
            if (log().isErrorEnabled()) log().err(e);
        }*/
    }

    /**
     * sfTerminateWith lifecycle method terminates the thread
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            if (sfLog().isInfoEnabled()) {
                sfLog().info("Autowrapper terminated!");
            }
            super.sfTerminateWith(tr);
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Error at sftermination:\n" + e, e);
            }
        }
    }

    /**
     * constructor extends DATA { class "org.smartfrog.Version"; parameters [param1,param2]; fields extends DATA {
     * field1 "StringValue1"; field2 1; //int value TODO show how to pass special values? field2 extends DATA {class "",
     * value"};
     *
     * } libDir //TODO it should also take an [] for jar files. //creates object attribute object; //Will be replaced at
     * runtime }
     *
     * -(libDir) jar, class, fields, parameters (if any). Object in attribute object. Create constructor for object and
     * returns it.
     *
     * @param cd cd to analyse
     * @return the constructed instance
     * @throws Exception if something failes
     */
    static public Object constructor(ComponentDescription cd) throws Exception {
        //Load jars if needed.

        File libDir = null;
        libDir = cd.sfResolve(ATTR_LIB_DIR, libDir, false);
        List<String> jars = new ArrayList<String>();
        List<URL> urls = null;
        String pattern=cd.sfResolve(ATTR_LIB_PATTERN,"",true);
        if (libDir != null) {
            FileSystem.scanDir(libDir, jars, pattern, true);
            urls = FileSystem.toFileURLs(jars);
            ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            Thread.currentThread().setContextClassLoader(classLoader);
        }

        // Get class for new object
        String jClassName = (String) cd.sfResolve(ATTR_CLASS, true);
        if (log().isInfoEnabled()) {
            log().info("Instantiating: " + jClassName);
        }
        Class jClass = Class.forName(jClassName);
        //Gets parameters for constructor if any
        Object[] parameters = getParameters(cd);
        if ((urls != null) && (log().isInfoEnabled())) {
            log().info("                URLs:" + jars.toString());
        }
        // get the right constructor method method
        Constructor jConstructor = jClass.getConstructor(toClassesArray(parameters));
        setSystemProperties(cd.sfResolve(ATTR_SYSTEM_PROPERTIES,(ComponentDescription)null, false));
        // Invoke the constructor
        Object object = jConstructor.newInstance(parameters);

        // Initialize needed fields if any

        // Fill the fields if they exist before starting the object
        /*
         * Java fields to fill after the constructor invocation and the start method,
         * if such a method exists
         */
        object = setFields(object, cd.sfResolve(ATTR_FIELDS, (ComponentDescription) null, false));
        cd.sfReplaceAttribute(ATTR_OBJECT, object);

        return object;
    }

    /**
     * method extends DATA { name "methodName"; parameters ""; object; //reference to object result; // created at
     * runtime }
     *
     * @param object the object instance to work on.
     * @param cd cd to resolve attributes and add the result
     * @return result the method result
     * @throws Exception something went wrong
     */
    static public Object invokeMethod(Object object, ComponentDescription cd) throws Exception {
        // Invoke the start method
        String methodName = null;
        methodName = cd.sfResolve(ATTR_NAME, methodName, true);
        if (log().isInfoEnabled()) {
            log().info("******* Invoking: " + methodName);
        }
        Class jClass = object.getClass();
        //Gets parameters for the method if any
        Object[] parameters = getParameters(cd);
        // get the right method
        Method method = null;
        if (parameters != null) {
            method = jClass.getMethod(methodName, toClassesArray(parameters));
        } else {
            Class[] classes = null;
            method = jClass.getMethod(methodName, classes);
        }
        setSystemProperties(cd.sfResolve(ATTR_SYSTEM_PROPERTIES, (ComponentDescription) null, false));
        Object result = method.invoke(object, parameters);
        if (result == null) {
            result = SFNull.get();
        }
        if (log().isInfoEnabled()) {
            log().info("  Result: " + methodName + " - " + result);
        }

        cd.sfReplaceAttribute(ATTR_RESULT, result);
        return result;
    }

    /**
     * Get the params of a CD
     * @param cd CD to extract
     * @return the parameters
     * @throws SmartFrogResolutionException
     */
    public static Object[] getParameters(ComponentDescription cd) throws SmartFrogResolutionException {
        Object[] parameters = null;
        Vector parametersV = null;
        parametersV = cd.sfResolve(ATTR_PARAMETERS, parametersV, false);
        if (parametersV != null) {
            if (log().isInfoEnabled()) {
                log().info("parameters: " + parametersV.toString());
            }
            parameters = parametersV.toArray();
//            //TODO temp solution
//            int len = parameters.length;
//            for (int i = 0; i<len; i++) {
//                if (parameters[i] instanceof Integer) {
//                  parameters[i]= ((Integer)parameters[i]).intValue();
//                } else if (parameters[i] instanceof Long) {
//                  parameters[i]= (long.class);
//                } else {
//                  //nothing
//                }
//            }
        }
        return parameters;
    }

    static public Object setFields(Object object, ComponentDescription cd) throws
            IllegalAccessException, NoSuchFieldException, SmartFrogResolutionException {
        if (cd == null) return object;
        Iterator attributes = cd.sfAttributes();
        String attribute = null;
        Object value = null;
        Field field = null;
        while (attributes.hasNext()) {
            attribute = attributes.next().toString();
            value = cd.sfResolve(attribute);
            field = object.getClass().getField(attribute);
            if (log().isInfoEnabled()) {
                log().info("   - set field: " + attribute + ": " + value);
            }
            field.set(object, value);
        }
        return object;
    }

    static public void setSystemProperties(ComponentDescription cd) throws SmartFrogResolutionException {
        if (cd == null) return;
        Iterator attributes = cd.sfAttributes();
        String attribute = null;
        Object value = null;
        while (attributes.hasNext()) {
            attribute = attributes.next().toString();
            value = cd.sfResolve(attribute);
            if (log().isInfoEnabled()) {
                log().info("   * set Sys.Property: " + attribute + "=" + value);
            }
            System.setProperty(attribute, value.toString());
        }
    }

    /**
     * Get classes from objects
     *
     * @param objects
     * @return a class array
     * @throws Exception
     */
    public static Class[] toClassesArray(Object[] objects) throws Exception {
        if (objects == null) return null;
        int len = objects.length;
        // get the parameters classes
        Class[] classes = new Class[len];
        for (int i = 0; i < len; i++) {
            if (objects[i] instanceof Integer) {
                classes[i] = (int.class);
            } else if (objects[i] instanceof Long) {
                classes[i] = (long.class);
            } else {
                classes[i] = (objects[i]).getClass();
            }
        }
        return classes;
    }

    private String prettyPrint(Object obj) {
        if (obj == null) {
            obj = SFNull.get();
        }
        StringBuilder sb = new StringBuilder();
        if (obj instanceof String[]) {
            Object[] objects = (Object[]) obj;
            for (Object object : objects) {
                sb.append(object).append('\n');
            }
        } else {
            return "prettyPrint" + obj.toString() + ' ' + obj.getClass().toString();
        }

        return sb.toString();
    }

    private static LogSF log() {
        return staticLog;
    }
}

