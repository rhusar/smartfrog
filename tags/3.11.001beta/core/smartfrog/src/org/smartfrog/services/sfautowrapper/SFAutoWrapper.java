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

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.*;
import org.smartfrog.sfcore.common.SFNull;

import java.lang.reflect.*;
import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
import java.net.URLClassLoader;
import java.net.URL;

import org.smartfrog.services.filesystem.FileSystem;


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


    /**
     * Default Constructor
     */
    public SFAutoWrapper() throws RemoteException {
    }

    /**
     * sfDeploy lifecycle method; setup information is collected here
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        staticLog = sfLog();
        ComponentDescription cd=null;
        try {
            constructor(sfResolve("constructor",cd,false));
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {sfLog().error("Failed during constructor!",e);}
            throw SmartFrogException.forward("Failed during constructor!",e);
        }
        if (sfLog().isInfoEnabled()) {
            sfLog().info("Autowrapper deployed!");
        }

    }

    /**
     *  main extends DATA {
     *     object LAZY constructor:result;
     *     method extends DATA {
     *        name "version";
     *       parameters extends Vector {};
     *     }
     *     next
     *     failure
     *     condition
     *  }
     * @param cd
     * @throws SmartFrogResolutionException
     */
    void runStateMachine (ComponentDescription cd) throws IllegalAccessException, SmartFrogResolutionException {
         // TODO a real state machine
         if (cd == null) {
            if (sfLog().isWarnEnabled()) {sfLog().warn("interrupted!");}
             return;
         } else {
            if (sfLog().isInfoEnabled()) {sfLog().info("Processing CD: \n"+ cd.toString());} 
         }
         Object object =null;
         ComponentDescription method=null;
         Vector parameters=null;
         ComponentDescription next=null;
         ComponentDescription failure=null;
         Object condition = null; //String.match, boolean, [componentdescription or LAZY predicate]

         object = cd.sfResolve ("object",true);
         if (object instanceof SFNull) {
             if (sfLog().isInfoEnabled()) {sfLog().info("Completed!");}
             return;
         }
         method = cd.sfResolve("method", method, true);
        Object result = null;
        try {
            result = invokeMethod(object,method);
            if (result==null) {
                result = SFNull.get();
            }
            cd.sfReplaceAttribute("result",result);
            //condition = cd.sfResolve("condition", condition, true);
            if (sfLog().isInfoEnabled()) {sfLog().info("result (method: "+method.sfResolve("name",false)+"): "+result);}
            next = cd.sfResolve("next", next, true);
        } catch (Exception e) {
            if (log().isErrorEnabled()) log().err(e);
            ComponentDescription nullCd=null;
            failure = cd.sfResolve ("failure", nullCd , false);
        }
        if (next!=null) {
            //TODO Should evaluate condition
            runStateMachine(next);
        } else if (failure!=null) {
            runStateMachine(failure);
        } else {
            if (sfLog().isInfoEnabled()) {sfLog().info("This should not happen!");}
        }

    }

    /**
     * sfStart lifecycle method
     * starts the inner thread to decouple the caller
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (sfLog().isInfoEnabled()) {
            sfLog().info("Autowrapper started!");
        }

        ComponentDescription cd = null;
        try {
            runStateMachine(sfResolve("main",cd,false));
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {sfLog().error("Failed during StateMachine!",e);}
        }
        sfLog().info("Going to sleep for some time...");
        try {
            Thread.currentThread().sleep(15*1000);
        } catch (InterruptedException e) {
            if (log().isErrorEnabled()) log().err(e);
        }
    }

    /**
     * sfTerminateWith lifecycle method
     * terminates the thread
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            if (sfLog().isInfoEnabled()) {
                sfLog().info("Autowrapper terminated!");
            }
            super.sfTerminateWith(tr);
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Error at sftermination:\n"+e, e);
            }
        }
    }

    /**
     *  constructor extends DATA {
     *    class "org.smartfrog.Version";
     *    parameters [param1,param2];
     *    fields extends DATA {
     *      field1 "StringValue1";
     *      field2 1; //int value TODO show how to pass special values? field2 extends DATA {class "", value"};
     *
     *    }
     *    libDir //TODO it should also take an [] for jar files.
     *    //creates object attribute
     *    object; //Will be replaced at runtime
     *  }
     *
     * -(libDir) jar, class, fields, parameters (if any). Object in attribute object.
     * Create constructor for object and returns it.
     */
    static public Object constructor(ComponentDescription cd) throws Exception, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        //Load jars if needed.

        File libDir=null;
        libDir = (File)cd.sfResolve("libDir",libDir,false);
        List urls = new ArrayList();
        if (libDir!=null) {
            FileSystem.scanDir(libDir,urls,"*(jar|zip)",true);
            ClassLoader classLoader = new URLClassLoader((URL[]) urls.toArray(new URL[0]));
            Thread.currentThread().setContextClassLoader(classLoader);
        }

        // Get class for new object
        String jClassName = (String)cd.sfResolve("class",true);
        if (log().isInfoEnabled()){
           log().info ("******* Instanciating: "+ jClassName);
        }
        Class jClass = Class.forName(jClassName);
        //Gets parameters for constructor if any
        Object[] parameters = getParameters(cd);
        if ((libDir!=null)&&(log().isInfoEnabled())) {
           log().info ("                URLs:"+urls.toString() );
        }
        // get the right constructor method method
        Constructor jConstructor = jClass.getConstructor(toClassesArray(parameters));
        setSystemProperties ((ComponentDescription) cd.sfResolve ("systemProperties",false));
        // Invoke the constructor
        Object object = jConstructor.newInstance(parameters);

        // Initialize needed fields if any

        // Fill the fields if they exist before starting the object
        /**
         * Java fields to fill after the constructor invocation and the start method,
         * if such a method exists
         */
        object = setFields (object, (ComponentDescription) cd.sfResolve ("fields",false));
        cd.sfReplaceAttribute("object",object);

        return object;
    }

    /**
     * method extends DATA {
     *    name "methodName";
     *    parameters "";
     *    object; //reference to object
     *    result; // created at runtime
     * }
     *
     * @param object
     * @param cd
     * @return result
     */
    static public Object invokeMethod (Object object, ComponentDescription cd) throws Exception, SmartFrogRuntimeException, IllegalAccessException, InvocationTargetException {
        // Invoke the start method
        String methodName = null;
        methodName = cd.sfResolve ("name",methodName,true);
        if (log().isInfoEnabled()){
           log().info ("******* Invoking: "+ methodName);
        }
        Class jClass = object.getClass();
        //Gets parameters for the method if any
        Object[] parameters = getParameters(cd);
        // get the right method
        Method method = null;
        if (parameters !=null) {
           method = jClass.getMethod(methodName,toClassesArray(parameters));
        } else {
           Class[] classes = null;
           method = jClass.getMethod(methodName,classes);
        }
        setSystemProperties ((ComponentDescription) cd.sfResolve ("systemProperties",false));
        Object result = method.invoke(object, parameters);
        if (result==null) {
            result = SFNull.get();
        }
        if (log().isInfoEnabled()){
           log().info ("  Result: "+ methodName + " - "+result);
        }

        cd.sfReplaceAttribute("result",result);
        return result;
    }

    public static Object[] getParameters(ComponentDescription cd) throws SmartFrogResolutionException {
        Object[] parameters = null;
        Vector parametersV = null;
        parametersV = ((Vector) cd.sfResolve ("parameters",parametersV,false));
        if (parametersV!=null) {
            if (log().isInfoEnabled()){
               log().info ("       + parameters: "+ parametersV.toString());
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

    static public Object setFields (Object object, ComponentDescription cd) throws IllegalAccessException, NoSuchFieldException, SmartFrogResolutionException {
        if (cd ==null) return object;
        Iterator attributes = cd.sfAttributes();
        String attribute = null;
        Object value =null;
        Field field = null;
        while (attributes.hasNext()){
            attribute =attributes.next().toString();
            value = cd.sfResolve (attribute);
            field = object.getClass().getField(attribute);
            if (log().isInfoEnabled()){
               log().info ("   - set field: "+ attribute + ": "+value);
            }
            field.set(object,value);
        }
        return object;
    }

    static public void setSystemProperties (ComponentDescription cd) throws SmartFrogResolutionException {
        if (cd ==null) return;
        Iterator attributes = cd.sfAttributes();
        String attribute = null;
        Object value =null;
        while (attributes.hasNext()){
           attribute =attributes.next().toString();
           value = cd.sfResolve (attribute);
           if (log().isInfoEnabled()){
              log().info ("   * set Sys.Property: "+ attribute + "="+value);
           }
           System.setProperty(attribute,value.toString());
        }
    }
    /**
     * Get classes from objects
     */
    public static Class[] toClassesArray (Object[] objects) throws Exception {
        if (objects== null) return null;
        int len = objects.length;
        // get the parameters classes
        Class[] classes = new Class[len];
        for (int i = 0; i<len; i++) {
            if (objects[i] instanceof Integer) {
              classes[i]= (int.class);
            } else if (objects[i] instanceof Long) {
              classes[i]= (long.class);
            } else {
              classes[i]= (objects[i]).getClass();
            }
        }
        return classes;
    }

    private String prettyPrint (Object obj){
       if (obj==null) {
          obj = SFNull.get();
       }
       StringBuffer sb = new StringBuffer();
       if (obj instanceof String[]){
           Object[] objects = (Object[]) obj;
           int length = objects.length;
           for (int i=0; i<objects.length; i++ ){
              sb.append(objects[i]).append("\n");
           }
       } else {
           return "prettyPrint"+obj.toString()+" "+obj.getClass().toString();
       }

       return sb.toString();
   }

   private static LogSF log() {
       return staticLog;
   }
}

