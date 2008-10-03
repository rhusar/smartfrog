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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

/*
 * Some code derived from article by Pankaj Kumar (pankaj_kumar at hp.com):
 * http://www.pankaj-k.net/spubs/articles/supercharging_beanshell_with_ant/
 */

/**
 * A wrapper around an Ant project. 
 * TODO: References - How are they resolved?
 * TODO: Split in: Project, Target, Task
 * TODO: Try to make the different parts remotable
 * TODO: Integrate build listener and log
 * TODO: improve error messages
 * TODO: test typdef and taskdef
 * TODO: how to do properties
 * TODO: overload sfResolve for project and even task
 * TODO: review the creation of an element inside a project (task=null)
 */
public class AntProject {

    private Project project = null;

    private Object aobj = null;
    private Object parent = null;


    private Properties tasks = null;
    private Properties types = null;
    private LogSF log;
    private Prim owner;
    private ComponentHelper helper;
    private AntHelper antHelper;
    private InterruptibleLogger interruptibleLogger;

    public AntProject(Prim owner, LogSF log) throws SmartFrogException,
            RemoteException {
        try {
            this.log = log;
            this.owner=owner;
            antHelper=new AntHelper(owner);
            antHelper.validateAnt();
            log.debug("Ant version: " + org.apache.tools.ant.Main.getAntVersion());
            project = antHelper.createNewProject();
            helper = new ComponentHelper(owner);
            String codebase = helper.getCodebase();

            String logLevel=owner.sfResolve(Ant.ATTR_LOG_LEVEL,Ant.ATTR_LOG_LEVEL_INFO,false);
            int level=antHelper.extractLogLevel(logLevel, Project.MSG_INFO);

            //Register build listener
            interruptibleLogger = antHelper.listenToProject(project,level,log);

            // set this with a SmartFrog property
            String basedirpath= FileSystem.lookupAbsolutePath(owner, Ant.ATTR_BASEDIR,".",
                    new File("."),false,null);
            project.setBaseDir(new File(basedirpath));
            tasks = loadNamedPropertyResource(Ant.ATTR_TASKS_RESOURCE);
            types = loadNamedPropertyResource(Ant.ATTR_TYPES_RESOURCE);

            setUserProperties(Ant.ATTR_PROPERTIES,project);

            // Set environment.
            Property env_prop = new Property();
            env_prop.setProject(project);
            env_prop.setEnvironment(Ant.ENV_PREFIX);
            env_prop.execute();
        } catch (BuildException e) {
            throw SmartFrogAntBuildException.forward(e);
        }
    }

    private Properties loadNamedPropertyResource(String attribute) throws SmartFrogResolutionException, RemoteException {
        String resource = owner.sfResolve(attribute,"",true);
        Properties props = new Properties();
        try {
            //TODO: better classloader
            props.load(owner.getClass().getResourceAsStream(resource));
        } catch (IOException ex) {
            log.error("Failed to load resource" + resource, ex);
        }
        return props;
    }

    /**
     * load properties from a [[name,value]] list and set them in an ant project
     * @param attribute attribute to bind to
     * @param project project to configure
     * @throws SmartFrogResolutionException if the vector of properties isn't a list of pairs
     * @throws RemoteException In case of Remote/network error
     *  */
    private void setUserProperties(String attribute,Project project) throws SmartFrogResolutionException, RemoteException {
        Vector<Vector<String>> propList=ListUtils.resolveStringTupleList(owner,new Reference(attribute),true);
        antHelper.setUserProperties(project, propList);
    }


    public Project getProject() {
        return project;
    }

    public Object getElement(String name, ComponentDescription cd)
            throws SmartFrogResolutionException, ClassNotFoundException,
            SmartFrogAntBuildException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        String attribute = null;
        String elementType = cd.sfResolve(Ant.ATTR_ANT_ELEMENT, attribute, false);
        Object newElement = createElement(null, null, elementType);
        if (newElement != null) {
            ((DataType) newElement).setProject(project);
            return getElement(newElement, attribute, cd);
        }
        return newElement;
    }


    /**
     * Recursive construction of an element from a CD
     * @param task task the base element/task
     * @param name elem
     * @param cd component description to work off
     * @return a new element of indeterminate type
     * @throws SmartFrogResolutionException attribute resolution problems
     * @throws IllegalAccessException forbidden methods
     * @throws ClassNotFoundException no matching class
     * @throws InstantiationException unable to create the element
     * @throws NoSuchMethodException missing methods
     * @throws SmartFrogAntBuildException a BuildException was raised in the Ant methods
     */
    private Object getElement(Object task, String name, ComponentDescription cd) throws SmartFrogAntBuildException,
            IllegalAccessException, ClassNotFoundException, InstantiationException,
            NoSuchMethodException, SmartFrogResolutionException {
        //System.out.println("  * "+name+" - Processing new element for "+ task.getClass().getName());
        Method[] methods = task.getClass().getMethods();
        String attribute = null;
        Object value = null;
        Iterator a = cd.sfAttributes();
        for (Iterator v = cd.sfValues(); v.hasNext();) {
            attribute = (String) a.next();
            value = v.next();
            if (value instanceof ComponentDescription) {
                //Create an inner element
                String elementType = ((ComponentDescription) value).sfResolve(Ant.ATTR_ANT_ELEMENT, attribute, false);
                Object newElement = createElement(task, methods, elementType);
                if (newElement != null) {
                    getElement(newElement, attribute, (ComponentDescription) value);
                }
            } else {
                // add attribute but relsove first if it is a reference.
                if ((value instanceof Reference) && (owner != null)) {
                    try {
                        value = owner.sfResolve((Reference) value);
                    } catch (Exception ex) {
                        log.error("Failed to resolve reference value for " + attribute + " in " + name + " task", ex);
                    }
                }
                setAttribute(task, methods, attribute, value);
            }
        }
        return task;
    }

    /**
     * Set an attribute
     * @param task task
     * @param methods list of methods
     * @param attribute attribute to set
     * @param value value to set
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws SmartFrogAntBuildException a BuildException was raised in the Ant methods
     */
    private void setAttribute(Object task, Method[] methods, String attribute, Object value) throws
            InstantiationException,
            IllegalAccessException, IllegalArgumentException, SecurityException,
            NoSuchMethodException, ClassNotFoundException, SmartFrogAntBuildException {
        if ((attribute.equals(Ant.ATTR_ANT_ELEMENT)) || (attribute.equals(Ant.ATTR_TASK_NAME))) {
            //skip our declaration elements
            return;
        }
        Method method = null;
        for (int m = 0; m < methods.length;) {
            method = methods[m++];
            final boolean isAddTextMethod = "addText".equals(method.getName());
            final String attrLower = attribute.toLowerCase(Locale.ENGLISH);
            final boolean isTextAttribute = "text".equals(attrLower);

            if (method.getName().toLowerCase(Locale.ENGLISH).equals("set" + attrLower) ||
                    (isTextAttribute && isAddTextMethod)) {

                Class[] ptypes = method.getParameterTypes();
                if (ptypes.length != 1) {
                    throw new IllegalArgumentException("no such attribute to be added: " + attribute);
                }

                try {
                    if ((value instanceof String) && (!(((isTextAttribute && isAddTextMethod))))) {
                        // Conversion for ${xxx} is not done in setText or addText
                        String oldValue = (String) value;
                        value = project.replaceProperties(oldValue);
                    }
                    // May need for type conversion
                    if (!ptypes[0].equals(value.getClass())) {
                        value = convType(value.toString(), ptypes[0]);
                    }
                    ////System.out.println("    +  "+method.getName()+" - TO beAdded attribute "+attribute+" for "+task.getClass().getName()+", value "+value +", "+value.getClass().getName());
                    method.invoke(task, value);
                    //System.out.println("    +  "+method.getName()+"    - Added attribute "+attribute+" for "+task.getClass().getName()+", value "+value);
                    return;
                } catch (BuildException e) {
                    throw new SmartFrogAntBuildException(e);
                } catch (InvocationTargetException e) {
                    throw new SmartFrogAntBuildException(e);
                }
            }
        }
        throw new IllegalArgumentException("no such attribute: " + attribute);
    }

    /**
     * Create an element
     *
     * @param task
     * @param methods
     * @param elementType
     * @return
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SmartFrogAntBuildException a BuildException was raised in the Ant methods
     */
    private Object createElement(Object task, java.lang.reflect.Method[] methods, String elementType) throws
            InstantiationException,
            IllegalArgumentException, IllegalAccessException, ClassNotFoundException, SmartFrogAntBuildException {
        Method method = null;
        String mName = null;
        try {
            if (methods != null) {
                //System.out.println(" # "+elementType+" - Creating element "+ elementType+" for "+task.getClass().getName());
                for (int e = 0; e < methods.length;) {
                    method = methods[e++];
                    mName = method.getName();
                    ////System.out.println("            Method - "+mName);
                    final String lowerName = mName.toLowerCase(Locale.ENGLISH);
                    if (lowerName.equals("create" + elementType)) {
                        //System.out.println("   #-Created element with: "+mName+ "of  element type "+elementType+" to "+task.getClass().getName());
                        return method.invoke(task);
                    } else if (lowerName.equals("add" + elementType)
                            || lowerName.equals("addConfigured" + elementType)) {
                        Class[] ptypes = method.getParameterTypes();
                        if (ptypes.length == 1) {
                            Object[] args = new Object[]{ptypes[0].newInstance()};
                            //System.out.println("   #-Adding element with: "+  mName+"of  element type "+ elementType+" to "+ task.getClass().getName());
                            method.invoke(task, args);
                            return args[0];
                        }
                    }
                }
            }
        } catch (BuildException e) {
            throw new SmartFrogAntBuildException(e);
        } catch (InvocationTargetException e) {
            throw new SmartFrogAntBuildException(e);
        }
        // If a method to create element is not found, then try finding the element class and create a new instance
        //TODO: project.createDataType(); or  project.getDataTypeDefinitions();
        try {

            //if (types.containsKey(elementType)) {
            if (project.getDataTypeDefinitions().containsKey(elementType)) {
                //Try to find the real class for Types that are defined in ant/types/default.properties
                //System.out.println("   #-Creating element for: "+elementType+" using "+project.getDataTypeDefinitions().get(elementType));
                //Object obj = Class.forName(types.getProperty(elementType)).newInstance();
                Object obj = ((Class) project.getDataTypeDefinitions().get(elementType)).newInstance();
                //TODO: improve error messages
                return obj;
            } else {
                //Try to load the class directly
                //System.out.println("   #- Creating element for: "+elementType+" using "+elementType);
                Object obj = Class.forName(elementType).newInstance();
                return obj;
                //TODO: improve error messages
            }
        } catch (BuildException e) {
            throw new SmartFrogAntBuildException(e);
        } catch (Exception ex1) {
            log.error("cound ot create a datatype ",ex1);
            String taskName = "noTask";
            if (task != null) taskName = task.getClass().getName();
            IllegalArgumentException ex = new IllegalArgumentException(
                    "No such inner element: " + elementType + " in " + taskName);
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


    Object convType(String arg, Class type) throws NoSuchMethodException, SecurityException, IllegalArgumentException,
            IllegalAccessException, InstantiationException, ClassNotFoundException, SmartFrogAntBuildException {
        //System.out.println("          = ContType: "+ arg +", "+type);
        if (type.isPrimitive()) {
            final String typename = type.toString();
            if (typename.equals("boolean")) {
                String ucArg = arg.toUpperCase(Locale.ENGLISH);
                return ((ucArg.equals("TRUE") || ucArg.equals("ON") ||
                        ucArg.equals("YES")) ? Boolean.TRUE : Boolean.FALSE);
            } else if (typename.equals("int")) {
                return Integer.valueOf(arg);
            } else if (typename.equals("short")) {
                return Short.valueOf(arg);
            } else if (typename.equals("byte")) {
                return Byte.valueOf(arg);
            } else if (typename.equals("long")) {
                return Long.valueOf(arg);
            } else if (typename.equals("float")) {
                return Float.valueOf(arg);
            } else if (typename.equals("double")) {
                return Double.valueOf(arg);
            } else {
                throw new IllegalArgumentException("unknown type: " + type);
            }
        } else if (EnumeratedAttribute.class.isAssignableFrom(type)) {
            Object newType = type.newInstance();
            ((EnumeratedAttribute) newType).setValue(arg);
            //System.out.println("Conv:Created EnumeratedAttribute:"+newType.toString());
            return newType;
        } else if (java.lang.String.class.equals(type)) {
            return arg;
        } else if (java.lang.Character.class.equals(type)) {
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
                c = type.getConstructor(new Class[]{Project.class, String.class});
                includeProject = true;
            } catch (NoSuchMethodException nme) {
                // OK, try without.
                try {
                    c = type.getConstructor(new Class[]{String.class});
                    includeProject = false;
                } catch (NoSuchMethodException nme2) {
                    // Well, no matching constructor.
                    return null;
                }
            }
            final boolean finalIncludeProject = includeProject;
            final Constructor finalConstructor = c;
            try {
                Object[] args = (finalIncludeProject) ? new Object[]{project, arg} : new Object[]{arg};
                Object attribute = finalConstructor.newInstance(args);
                if (project != null) {
                    project.setProjectReference(attribute);
                }
                return attribute;
            } catch (InvocationTargetException e) {
                throw new SmartFrogAntBuildException(e);
            }
        }
    }


    public void setenv(String key, String value) {
        project.setProperty(Ant.ENV_PREFIX+"." + key, value);
    }


    public String getenv(String key) {
        return project.getProperty(Ant.ENV_PREFIX + "." + key);
    }

    public String getenv(String key, String def) {
        String value = project.getProperty(Ant.ENV_PREFIX + "." + key);
        return (value == null ? def : value);
    }


    /**
     * create a task
     * @param tname
     * @param cd
     * @return
     * @throws SmartFrogResolutionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws SmartFrogAntBuildException a BuildException was raised in the Ant methods
     */
    Task getTask(String tname, ComponentDescription cd) throws SmartFrogResolutionException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, SmartFrogAntBuildException {
        String taskname = cd.sfResolve(Ant.ATTR_TASK_NAME, tname, false);
        //      String clazz = tasks.getProperty(taskname);
        Class clazz = ((Class) (project.getTaskDefinitions().get(taskname)));
        //System.out.println("- Creating task: "+tname+" ,type: "+taskname+", clazz: "+clazz);
        if (clazz == null) {
            throw new SmartFrogResolutionException("no such task: " + taskname);
        }
        Task tobj = (Task) clazz.newInstance();
        tobj.setProject(project);
        tobj.setTaskName(tname);
        getElement(tobj, tname, cd);
        return tobj;
    }
}
