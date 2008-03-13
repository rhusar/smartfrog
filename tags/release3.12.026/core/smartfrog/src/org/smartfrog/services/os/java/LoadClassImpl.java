/* (C) Copyright 2005-2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

/**
 * Class to force load another class (and keep it in memory till we undeploy. Liveness checks verify that the class
 * loaded properly. A flag can also force instantiate an instance if they have an empty constructor. Otherwise, just the
 * class gets loaded. created 20-Sep-2005 11:28:38
 */

public class LoadClassImpl extends PrimImpl implements LoadClass {
    public static final String ERROR_NO_PUBLIC_CONSTRUCTOR = "No public empty constructor for class ";
    public static final String MESSAGE_CREATING_AN_INSTANCE = "Creating an instance of ";
    private static final Reference REF_CLASSES = new Reference(ATTR_CLASSES);

    public LoadClassImpl() throws RemoteException {
    }

    /**
     * Classes we load
     */
    private Class[] classInstances = new Class[0];
    /**
     * any instance
     */
    private Object[] objectInstances = new Object[0];


    private List<String> classes;

    private boolean create = false;

    private boolean retain = true;

    /**
     * a log
     */
    private Log log;


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        log = LogFactory.getOwnerLog(this);
        classes = ListUtils.resolveStringList(this, REF_CLASSES, true);
        create = sfResolve(ATTR_CREATE, create, true);
        retain = sfResolve(ATTR_RETAIN, retain, true);
        int size = classes.size();
        classInstances = new Class[size];
        int instanceSize = size;
        if (!create) {
            instanceSize = 0;
        }
        objectInstances = new Object[instanceSize];

        int count = 0;
        for (String classname : classes) {
            log.debug("Loading class " + classname);
            Class clazz = loadClass(this, classname);
            classInstances[count] = clazz;
            count++;
        }
        if (create) {
            for (int i = 0; i < classInstances.length; i++) {
                Class clazz = classInstances[i];
                log.debug("Creating class " + clazz.getName());
                objectInstances[i] = createInstance(clazz);
            }
        }
        if (!retain) {
            cleanup();
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                null,
                null,
                null);
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        cleanup();
    }

    /**
     * forget about all our classes
     */
    private void cleanup() {
        //because Java lacks deterministic finalization, we don't know
        //when cleanup engages
        //all we can do is prepare for it.
        //delete all object instances from the array
        if (objectInstances != null) {
            for (int i = 0; i < objectInstances.length; i++) {
                objectInstances[i] = null;
            }
            objectInstances = null;
        }
        if (classInstances != null) {
            //delete all class instances
            for (int i = 0; i < classInstances.length; i++) {
                classInstances[i] = null;
            }
            classInstances = null;
        }

        //our component has purged all knowlege of the components, it is up to GC to do the rest.
        //we trigger this, even though it is only a hint
        System.gc();
    }

    /**
     * Create an instance of a class using the empty constructor
     *
     * @param clazz class to load
     * @return an instance
     * @throws SmartFrogException if something went wrong
     * @throws RemoteException    for network trouble
     */
    public static Object createInstance(Class clazz) throws SmartFrogException, RemoteException {
        Object instance;
        Class params[] = new Class[0];
        Constructor defaultConstructor;
        try {
            defaultConstructor = clazz.getConstructor(params);
        } catch (NoSuchMethodException e) {
            throw new SmartFrogException(ERROR_NO_PUBLIC_CONSTRUCTOR + clazz.getName());
        }
        Object params2[] = new Object[0];
        String details = MESSAGE_CREATING_AN_INSTANCE + clazz.getName();
        try {
            instance = defaultConstructor.newInstance(params2);
        } catch (InstantiationException e) {
            throw SmartFrogException.forward(details, e);
        } catch (IllegalAccessException e) {
            throw SmartFrogException.forward(details, e);
        } catch (InvocationTargetException e) {
            throw SmartFrogException.forward(details, e);
        }
        return instance;
    }

    /**
     * Load a class using the classloader of a nominated component
     *
     * @param owner     owner component
     * @param classname name of the class to load
     * @return the class
     * @throws RemoteException    for network trouble
     * @throws SmartFrogException if the loading failed; in which case a nested ClassNotFoundException will exist.
     */
    public static Class loadClass(Prim owner, String classname) throws RemoteException, SmartFrogException {
        ComponentHelper helper = new ComponentHelper(owner);
        return helper.loadClass(classname);
    }
}
