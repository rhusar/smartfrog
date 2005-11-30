/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.ant;

import java.lang.reflect.Method;
import java.lang.reflect.*;
import org.smartfrog.sfcore.security.SFClassLoader;
import java.io.InputStream;
import java.io.File;
import java.util.Properties;
import java.io.*;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Mapper;
import org.smartfrog.sfcore.common.SmartFrogException;
import java.util.Iterator;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.Project;

/*
 * Some code derived from article by Pankaj Kumar (pankaj_kumar at hp.com):
 * http://www.pankaj-k.net/spubs/articles/supercharging_beanshell_with_ant/
 */

/**
@todo: References - How are they resolved?
@todo: Split in: Project, Target, Task
@todo: Try to make the different parts remotable
@todo: Integrate build listener and log
@todo: improve error messages
@todo: test typdef and taskdef
*/
public class AntProject {

    private org.apache.tools.ant.Project project = null;

        Properties tasks = null;
        Properties types = null;

        public AntProject() throws SmartFrogDeploymentException {

            validateAnt();
//        org.apache.tools.ant.Diagnostics.validateVersion();
//       System.out.println("Ant version: "+org.apache.tools.ant.Main.getAntVersion());
            project = new org.apache.tools.ant.Project();
            project.setCoreLoader(null);
            project.init();


            //Register build listener @TODO replace this with our own listener
            org.apache.tools.ant.DefaultLogger logger = new org.apache.tools.ant.DefaultLogger();
            logger.setOutputPrintStream(System.out);
            logger.setErrorPrintStream(System.err);
//        logger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);
//        logger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_DEBUG); //-d
            logger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_VERBOSE); //-v
//        logger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_WARN);    //-q
            project.addBuildListener(logger);

            // @TODO set this with a SmartFrog property
            project.setBaseDir(new File("."));

            tasks = new Properties();
            // @TODO set this with a SmartFrog property
            String propFilename ="/org/apache/tools/ant/taskdefs/defaults.properties";
            try {
                tasks.load(tasks.getClass().getResourceAsStream(propFilename));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            types = new Properties();
            // @TODO set this with a SmartFrog property
            propFilename = "/org/apache/tools/ant/types/defaults.properties";
            try {
                types.load(tasks.getClass().getResourceAsStream(propFilename));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Set environment.
            org.apache.tools.ant.taskdefs.Property env_prop = new org.apache.tools.ant.taskdefs.Property();
            env_prop.setProject(project);
            env_prop.setEnvironment("myenv");
            env_prop.execute();
        }

        Object aobj = null;
        Object parent = null;


        final String ATR_ANT_ELEMENT = "AntElement";

        Object getElement(Object task, String name, ComponentDescription cd )throws Exception {

            System.out.println("  * "+name+" - Processing new element for "+task.getClass().getName());

            java.lang.reflect.Method[] methods = task.getClass().getMethods();

            String attribute = null;
            Object value = null;
            Iterator a = cd.sfAttributes();
            for (Iterator v = cd.sfValues(); v.hasNext(); ) {
                attribute = (String) a.next();
                value = v.next();
                if (value instanceof org.smartfrog.sfcore.componentdescription.ComponentDescription) {
                    //Create an inner element
                    String elementType = ((ComponentDescription)value).sfResolve(ATR_ANT_ELEMENT,attribute,false);
                    Object newElement = createElement(task,methods, elementType);
                    if (newElement !=null) {
                       getElement(newElement, (String)attribute,(ComponentDescription)value);
                    }
                } else {
                    // add attribute
                    setAttribute(task, methods, attribute, value);
                }
            }
            return task;
        }

        private void setAttribute(Object task,Method[] methods, String attribute, Object value) throws
            InstantiationException, InvocationTargetException,
            IllegalAccessException, IllegalArgumentException, SecurityException,
            NoSuchMethodException, ClassNotFoundException {
            if ((attribute.equals(this.ATR_ANT_ELEMENT))||(attribute.equals(this.ATR_TASK_NAME))) return;
            Method method = null;
            for (int m = 0; m<methods.length;) {
                method = methods[m++];
                //System.out.println("                methodAttribute "+method.getName());
                if (method.getName().equalsIgnoreCase("set"+attribute)||
                    (attribute.equalsIgnoreCase("text")&& method.getName().equals("addText"))) {

                    Class[] ptypes = method.getParameterTypes();
                    if (ptypes.length!=1) {
                        throw  new IllegalArgumentException("no such attribute to be added: "+attribute);
                    }
                    // May need for type conversion
                    if (!ptypes[0].equals(value.getClass())) {
                        value = convType((String)value, ptypes[0]);
                    }
                    if ((value instanceof String) && (!(((attribute.equalsIgnoreCase("text")&& method.getName().equals("addText")))))){
                       // Conversion for ${xxx} is not done in setText or addText
                       String oldValue = (String)value;
                       value = project.replaceProperties((String)value);
                       System.out.println("       -replaced properties in: '"+ oldValue + "' to '"+value+"'");
                    }
                    //System.out.println("    +  "+method.getName()+" - TO beAdded attribute "+attribute+" for "+task.getClass().getName()+", value "+value +", "+value.getClass().getName());
                    method.invoke(task, new Object[] {value});
                    System.out.println("    +  "+method.getName()+"    - Added attribute "+attribute+" for "+task.getClass().getName()+", value "+value);
                    return;
                }
            }
            throw  new IllegalArgumentException("no such attribute: "+attribute);
        }

        private Object createElement(Object task, java.lang.reflect.Method[] methods, String elementType) throws
            InstantiationException, InvocationTargetException,
            IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
            Method method = null;
            String mName = null;
            System.out.println(" # "+elementType+" - Creating element "+elementType+" for "+task.getClass().getName());
            for (int e = 0; e<methods.length;) {
                method = methods[e++];
                mName = method.getName();
                //System.out.println("            Method - "+mName);
                if (mName.equalsIgnoreCase("create"+elementType)) {
                    System.out.println("   #-Created element with: "+mName+"of  element type "+elementType+" to "+task.getClass().getName());
                    return method.invoke(task, new Object[] {});
                } else if (mName.equalsIgnoreCase("add"+elementType)|| mName.equalsIgnoreCase("addConfigured"+elementType)) {
                    Class[] ptypes = method.getParameterTypes();
                    if (ptypes.length==1) {
                        Object[] args = new Object[] {ptypes[0].newInstance()};
                        System.out.println("   #-Adding element with: "+mName+"of  element type "+elementType+" to "+task.getClass().getName());
                        method.invoke(task, args);
                        return args[0];
                    }

                }
            }
            // If a method to create element is not found, then try finding the element class and create a new instance
            //@TODO: project.createDataType(); or  project.getDataTypeDefinitions();
            try {

                //if (types.containsKey(elementType)) {
                if (project.getDataTypeDefinitions().containsKey(elementType)){
                    //Try to find the real class for Types that are defined in ant/types/default.properties
                    System.out.println("   #-Creating element for: "+elementType+" using "+project.getDataTypeDefinitions().get(elementType));
                    //Object obj = Class.forName(types.getProperty(elementType)).newInstance();
                    Object obj = ((Class)project.getDataTypeDefinitions().get(elementType)).newInstance();
                    //@todo: improve error messages
                    return obj;
                } else {
                    //Try to load the class directly
                    System.out.println("   #-Creating element for: "+elementType+" using "+elementType);
                    Object obj = Class.forName(elementType).newInstance();
                    return obj;
                    //@todo: improve error messages
                }
            } catch (Exception ex1) {
                IllegalArgumentException ex = new IllegalArgumentException("No such inner element: "+elementType +" in "+ task.getClass().getName());
                ex.initCause(ex1);
                throw ex;
            }
        }


/*
   Rules:
    Conversions Ant will perform for attributes:
    Ant will always expand properties before it passes the value of an attribute to the corresponding setter method.

    The most common way to write an attribute setter is to use a java.lang.String argument.
    In this case Ant will pass the literal value (after property expansion) to your task. But
    there is more!

//    If the argument of you setter method is boolean, your method will be passed the value true if the
//    value specified in the build file is one of true, yes, or on and false otherwise.

    char or java.lang.Character, your method will be passed the first character of the value specified in the build file.

//    any other primitive type (int, short and so on), Ant will convert the value of the attribute into this type, thus
//    making sure that you'll never receive input that is not a number for that attribute.

//    java.io.File, Ant will first determine whether the value given in the build file represents an absolute path name.
//    If not, Ant will interpret the value as a path name relative to the project's basedir.

    org.apache.tools.ant.types.Path, Ant will tokenize the value specified in the build file, accepting : and ; as
    path separators. Relative path names will be interpreted as relative to the project's basedir.

    java.lang.Class, Ant will interpret the value given in the build file as a Java class name and load the named
    class from the system class loader.

//    any other type that has a constructor with a single String argument, Ant will use this constructor to create a new
//    instance from the value given in the build file.

//    A subclass of org.apache.tools.ant.types.EnumeratedAttribute, Ant will invoke this classes setValue method. Use
//    this if your task should support enumerated attributes (attributes with values that must be part of a predefined
//    set of values). See org/apache/tools/ant/taskdefs/FixCRLF.java and the inner AddAsisRemove class used in setCr
//    for an example.

    What happens if more than one setter method is present for a given attribute? A method taking a String argument
    will always lose against the more specific methods. If there are still more setters Ant could chose from, only
    one of them will be called, but we don't know which, this depends on the implementation of your Java virtual machine.
*/


    Object convType (String arg, Class type) throws NoSuchMethodException, SecurityException,IllegalArgumentException, IllegalAccessException, InvocationTargetException,InstantiationException, ClassNotFoundException{
         System.out.println("          = ContType: "+ arg +", "+type);
      if (type.isPrimitive()){
        if (type.toString().equals("boolean")){
          String ucArg = arg.toUpperCase();
          return ((ucArg.equals("TRUE") || ucArg.equals("ON") ||
              ucArg.equals("YES")) ? Boolean.TRUE : Boolean.FALSE);
        } else if (type.toString().equals("int")){
          return Integer.valueOf(arg);
        } else if (type.toString().equals("short")){
          return Short.valueOf(arg);
        } else if (type.toString().equals("byte")){
          return Byte.valueOf(arg);
        } else if (type.toString().equals("long")){
           return Long.valueOf(arg);
        } else if (type.toString().equals("float")){
          return Float.valueOf(arg);
        } else if (type.toString().equals("double")){
          return Double.valueOf(arg);
        } else {
          throw new IllegalArgumentException("unknown type: " + type);
        }
      } else if (EnumeratedAttribute.class.isAssignableFrom(type)) {
        Object newType = type.newInstance();
        ((EnumeratedAttribute)newType).setValue(arg);
        System.out.println("Conv:Created EnumeratedAttribute:"+newType.toString());
        return newType;
      } else if (java.lang.String.class.equals(type)){
          return (String)arg;
      } else if (java.lang.Character.class.equals(type)){
          return (new Character(arg.charAt(0)));
          // Class doesn't have a String constructor but a decent factory method
      } else if (java.lang.Class.class.equals(type)) {
          return Class.forName(arg);
          // resolve relative paths through Project
      } else if (java.io.File.class.equals(type)) {
           return project.resolveFile(arg);
      } else {

// Original default constructor
//        Constructor ctor = null;
//        ctor = type.getConstructor(new Class[] {arg.getClass()});
//        return ctor.newInstance(new Object[] {arg});

          // Code derived from org.apache.tools.ant.IntrospectionHelper
          boolean includeProject;
          Constructor c;
          try {
             // First try with Project.
             c = type.getConstructor(new Class[] {Project.class, String.class});
             includeProject = true;
           } catch (NoSuchMethodException nme) {
                // OK, try without.
                try {
                    c = type.getConstructor(new Class[] {String.class});
                    includeProject = false;
                } catch (NoSuchMethodException nme2) {
                    // Well, no matching constructor.
                    return null;
                }
            }
            final boolean finalIncludeProject = includeProject;
            final Constructor finalConstructor = c;
            try {
                Object[] args = (finalIncludeProject) ? new Object[] {project, arg} : new Object[] {arg};
                Object attribute = finalConstructor.newInstance(args);
                if (project != null) {
                    project.setProjectReference(attribute);
                }
                return attribute;
            } catch (InstantiationException ie) {
                throw ie;
            }
      }
    }


    private void validateAnt() throws SmartFrogDeploymentException {
        if (SFClassLoader.getResourceAsStream("/org/apache/tools/ant/Project.class")==null) {
            throw new SmartFrogDeploymentException ("Cannot initialize Ant. WARNING: Perhaps ant.jar is not in CLASSPATH ...");
        }
    }

    void setenv(String key, String value) {
        project.setProperty("myenv."+key,value);
    }


    String getenv(String key) {
        return project.getProperty("myenv."+key);
    }

    String getenv(String key, String def) {
        String value = (String)project.getProperty("myenv."+key);
        return (value==null?def:value);
    }

    final String ATR_TASK_NAME = "AntTask";

   //Create task
   Task getTask(String tname, ComponentDescription cd) throws Exception {
      String taskname = cd.sfResolve(ATR_TASK_NAME,tname,false);
  //      String clazz = tasks.getProperty(taskname);
      Class clazz =  ((Class)(project.getTaskDefinitions().get(taskname)));
      System.out.println("- Creating task: "+tname+" ,type: "+taskname+", clazz: "+clazz);
      if (clazz == null){
        throw new Exception("no such task: " + taskname);
      }
      //Task tobj = (Task)Class.forName(clazz).newInstance();
      Task tobj = (Task)clazz.newInstance();
      tobj.setProject(project);
      tobj.setTaskName(tname);
      getElement(tobj,tname ,cd);
      return tobj;
    }
}
