/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.persistence.storage;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;


/**
 * Defines the methods a simple and sequential storage
 * space should have.
 *
 */
public abstract class Storage implements Serializable {

    public static final String UNKNOWN_CLASS = "unknown class";
    public static final String CLASS_ATTRIB = "wfStorageClass";
    public static final String CONFIG_DATA = "wfStorageConfigData";
    public static final String NAME_ATTRIB = "wfStorageName";
    public static final String REPO_ATTRIB = "wfStorageRepository";


    /**
     * Obtains a vector of the stores in the appropriate repository.
     *
     * @param context Context
     * @return Vector
     * @throws StorageException
     */
    public static Vector getStores(Context context) throws StorageException {

        Object configObj = context.get(CONFIG_DATA);
        if (configObj == null) {
            throw new StorageException("Storage config missing");
        }
        if (!(configObj instanceof ComponentDescription)) {
            throw new StorageException(
                    "Storage config is not a component description");
        }
        return getStores((ComponentDescription) configObj);
    }


    /**
     * Obtains a vector of the stores in the appropriate repository.
     *
     * @param className String
     * @param configData ComponentDescription
     * @return Vector
     * @throws StorageException
     */
    public static Vector getStores(ComponentDescription configData) throws
            StorageException {

        String className = UNKNOWN_CLASS;
        try {
            className = configData.sfResolve(CLASS_ATTRIB, (String)null, true);
            Class storageclass = Class.forName(className);
            Class[] methodParams = new Class[1];
            methodParams[0] = ComponentDescription.class;
            Method getStores0Method = storageclass.getMethod("getStoresImpl",
                    methodParams);
            Object[] params = new Object[1];
            params[0] = configData;
            return (Vector) getStores0Method.invoke(null, params);
        } catch (InvocationTargetException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (IllegalArgumentException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (IllegalAccessException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (SecurityException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (NoSuchMethodException ex) {
            throw (StorageException) StorageException.forward(
                    "getStoresImpl method for " + className + " not found", ex);
        } catch (ClassNotFoundException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (SmartFrogResolutionException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage class is missing, config " + configData, ex);
        }

    }


    /**
     * Implementation of getStores method - static method over-ridden by the
     * implementation class. This method is called from the static method
     * getStores, which obtains the appropriate implementation.
     *
     * @param configData ComponentDescription
     * @return Vector
     * @throws StorageException
     */
    public static Vector getStoresImpl(ComponentDescription configData) throws
            StorageException {
        throw new StorageException(
                "getStoresImpl method implementation missing from storage class");
    }

    /**
     * Constructs the storage implementation specified in the context. This
     * version uses the constructor that expects to find a pre-existing persisted
     * component.
     *
     * @param context Context
     * @return Storage
     * @throws SmartFrogDeploymentException
     */
    public static Storage createExistingStorage(Context context) throws
            StorageException {

        Object configObj = context.get(CONFIG_DATA);
        if (configObj == null) {
            throw new StorageException("Storage config missing");
        }
        if (!(configObj instanceof ComponentDescription)) {
            throw new StorageException(
                    "Storage config is not a component description");
        }
        return createExistingStorage((ComponentDescription) configObj);
    }


    /**
     * Constructs the storage implementation specified by name and config. This
     * version uses the constructor that expects to find a pre-existing persisted
     * component.
     *
     * @param className String
     * @param configData ComponentDescription
     * @return Storage
     * @throws StorageException
     */
    public static Storage createExistingStorage(ComponentDescription configData) throws
            StorageException {

        String className = UNKNOWN_CLASS;
        try {
            className = configData.sfResolve(CLASS_ATTRIB, (String)null, true);
            String storageName = configData.sfResolve(NAME_ATTRIB, (String)null, true);
            Class storageclass = Class.forName(className);
            Class[] constparam = new Class[2];
            constparam[0] = String.class;
            constparam[1] = ComponentDescription.class;
            Constructor storageconstructor = storageclass.getConstructor(
                    constparam);
            Object[] params = new Object[2];
            params[0] = storageName;
            params[1] = configData;
            return (Storage) storageconstructor.newInstance(params);
        } catch (SmartFrogResolutionException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage name or storage class missing from config " +
                    configData, ex);
        } catch (ClassNotFoundException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage class " + className + " not found", ex);
        } catch (NoSuchMethodException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage constructor for " + className + " not found", ex);
        } catch (InvocationTargetException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (IllegalArgumentException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (IllegalAccessException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (InstantiationException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        }
    }


    /**
     * Constructs the storage implementation specified in the context. this
     * version uses the constructor that expects to create a new persisted
     * component storage.
     *
     * @param context Context
     * @return Storage
     * @throws SmartFrogDeploymentException
     */
    public static Storage createNewStorage(Context context) throws
            StorageException {

        Object configObj = context.get(CONFIG_DATA);
        if (configObj == null) {
            throw new StorageException("Storage config missing");
        }
        if (!(configObj instanceof ComponentDescription)) {
            throw new StorageException(
                    "Storage config is not a component description");
        }
        return createNewStorage((ComponentDescription) configObj);
    }


    /**
     * Constructs the storage implementation specified by name and config. this
     * version uses the constructor that expects to create a new persisted
     * component storage.
     *
     * @param context Context
     * @return Storage
     * @throws SmartFrogDeploymentException
     */
    public static Storage createNewStorage(ComponentDescription configData) throws
            StorageException {

        String className = UNKNOWN_CLASS;
        try {
            className = configData.sfResolve(CLASS_ATTRIB, (String)null, true);
            Class storageclass = Class.forName(className);
            Class[] constparam = new Class[1];
            constparam[0] = ComponentDescription.class;
            Constructor storageconstructor = storageclass.getConstructor(
                    constparam);
            Object[] params = new Object[1];
            params[0] = configData;
            return (Storage) storageconstructor.newInstance(params);
        } catch (ClassNotFoundException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage class " + className + " not found", ex);
        } catch (NoSuchMethodException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage constructor for " + className + " not found", ex);
        } catch (InvocationTargetException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (IllegalArgumentException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (IllegalAccessException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (InstantiationException ex) {
            throw (StorageException) StorageException.forward("Storage " +
                    className + ", config " + configData, ex);
        } catch (SmartFrogResolutionException ex) {
            throw (StorageException) StorageException.forward(
                    "Storage class is missing, config " + configData, ex);
        }

    }


    /**
     * Creates a new entry in the storage space and associates it to a directory
     *
     * @param entryname
     * @throws StorageException
     */
    public abstract void createEntry(String entryname, String directory) throws
            StorageException;


    public abstract boolean hasEntry(String entryname) throws StorageException;

    /**
     * Inserts a new record in the storage space for the specified entry
     * The entry must have been created before, otherwise an exception is thrown.
     *
     * @param obj Object value that should be written
     *
     * @throws StorageException In case some failure happens
     */
    public abstract void addEntry(String entryname, Serializable value) throws
            StorageException;

    /**
     * Deletes an indexed record of the given entry from the storage space
     *
     * @param index Pointer to the internal object that should be deleted
     *
     * @throws StorageException In case some failure happens
     */
    public abstract void deleteEntry(String entryname) throws StorageException;


    //public void doGarbageCollection (long version) throws StorageException;

    /**
     * Recovers an entry from stable storage
     *
     * @param id index of the required entry
     * @return
     */
    public abstract Serializable getEntry(String entryname) throws
            StorageException;

    public abstract void commit() throws StorageException;

    public abstract void abort() throws StorageException;

    public abstract void delete() throws StorageException;

    public abstract void disableCommit();

    public abstract void enableCommit();

    public abstract Object[] getEntries(String directory) throws
            StorageException;

    //public long getLastVersion() throws StorageException;

    public abstract StorageRef getStorageRef() throws StorageException;

    public abstract String getAgentUrl();

    public abstract void close() throws StorageException;

}
