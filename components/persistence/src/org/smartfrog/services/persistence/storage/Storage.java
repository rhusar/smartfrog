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
import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.services.persistence.storage.nullstorage.NullStorageImpl;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;


/**
 * Defines the methods a simple and sequential storage
 * space should have.
 *
 */
public abstract class Storage implements Serializable {

    public static final String UNKNOWN_CLASS = "unknown class";
    public static final String CLASS_ATTRIB = "wfStorageClass";
    public static final String CONFIG_DATA = "wfStorageConfigData";
    public static final String NAME_ATTRIB = "wfStorageDatabaseName";
    public static final String REPO_ATTRIB = "wfStorageRepository";
    public static final String DBNAME_ATTRIB = "wfStorageDatabaseName";
    public static final String REGISTER_ATTRIB = "recoveryRegister";

    protected ComponentDescription configData;
    
 
    /**
     * Obtains a vector of the stores in the appropriate repository.
     * If the context does not contain a storage description or there is
     * a null in its place then this will use the NullStorageImpl implementation.
     *
     * @param context Context
     * @return Vector
     * @throws StorageException
     */
    public static Vector getStores(Context context) throws StorageException {

        /**
         * configObj will be null if the value not present or SFNull if
         * set to null in the SF description.
         * If either are the case construct a NullStorageImpl implementation.
         * Otherwise continue with the configured implementation.
         */
        Object configObj = context.get(CONFIG_DATA);
        if (configObj == null || configObj instanceof SFNull ) {
        	return NullStorageImpl.getStores(context);
//            throw new StorageException("Storage config missing");
        }
        if (!(configObj instanceof ComponentDescription)) {
            throw new StorageException(
                    "Storage config is not a component description");
        }
        return getStores((ComponentDescription) configObj);
    }


    /**
     * Obtains a vector of the stores in the appropriate repository.
     * If the configData has null value then the NullStorageImpl 
     * implementation will be used.
     *
     * @param configData ComponentDescription
     * @return Vector
     * @throws StorageException
     */
    public static Vector getStores(ComponentDescription configData) throws
            StorageException {

    	/**
    	 * If configData is null use the NullStorageImpl implementation
    	 */
    	if( configData == null ) {
    		return NullStorageImpl.getStores(configData);
    	}
    	
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
     * component. If the context does not include the storage description or
     * it includes a null value in place of for the stroage description it
     * will create a NullStorageImpl implementation of the storage.
     *
     * @param context Context
     * @return Storage
     * @throws SmartFrogDeploymentException
     */
    public static Storage createExistingStorage(Context context) throws
            StorageException {

        /**
         * configObj will be null if the value is null or it is not present.
         * If either are the case construct a NullStorageImpl implementation.
         * Otherwise continue with the configured implementation.
         */
        Object configObj = context.get(CONFIG_DATA);
        if (configObj == null || configObj instanceof SFNull ) {
        	// @TODO: log creation of null storage
        	return new NullStorageImpl();
//            throw new StorageException("Storage config missing");
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
     * component. If the configData parameter is a null value in place of for the 
     * stroage description it will create a NullStorageImpl implementation of the 
     * storage.
     *
     * @param configData ComponentDescription
     * @return Storage
     * @throws StorageException
     */
    public static Storage createExistingStorage(ComponentDescription configData) throws
            StorageException {

    	/**
    	 * If the configData is null construct a NullStorageImpl implementation.
    	 * Otherwise continue with the configured storage.
    	 */
    	if( configData == null ) {
    		return new NullStorageImpl();
    	}
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
     * Constructs the storage implementation specified in the context.
     * This version uses the constructor that expects to create a new persisted
     * component storage. If the context does not include the storage description or
     * it includes a null value in place of for the stroage description it
     * will create a NullStorageImpl implementation of the storage.
     *
     * @param context Context
     * @return Storage
     * @throws SmartFrogDeploymentException
     */
    public static Storage createNewStorage(Context context) throws StorageException {
    	
    	Storage newStorage = null;

        /**
         * configObj will be null if the value is null or it is not present.
         * If either are the case construct a NullStorageImpl implementation.
         * Otherwise continue with the configured implementation.
         */
    	Object configObj = context.get(CONFIG_DATA);
    	if (configObj == null || configObj instanceof SFNull ) {
    		// @TODO: log creation of null storage
    		return new NullStorageImpl();
//    		throw new StorageException("Storage config missing");
    	}

    	if (!(configObj instanceof ComponentDescription)) {
    		throw new StorageException(
    		"Storage config is not a component description");
    	}
    	
    	ComponentDescription config = (ComponentDescription)configObj;
    	
    	registerIfRequired(config);
    	
    	try {
    		newStorage = createNewStorage(config);
    	} catch (StorageException ex) {
    		try { deregisterIfRequired(config); }
    		catch(Exception e) { e.printStackTrace(); }
    		throw ex;
    	}
    	
    	return newStorage;
    }


    /**
     * Constructs the storage implementation specified by name and config.
     * This version uses the constructor that expects to create a new persisted
     * component storage. If the configData parameter is a null value in place of for the 
     * stroage description it will create a NullStorageImpl implementation of the 
     * storage.
     *
     * @param configData Context
     * @throws SmartFrogDeploymentException
     */
    public static Storage createNewStorage(ComponentDescription configData) throws
            StorageException {

    	/**
    	 * If the configData is null construct a NullStorageImpl implementation.
    	 * Otherwise continue with the configured storage.
    	 */
    	if( configData == null ) {
    		return new NullStorageImpl();
    	}
    	
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
     * Registers the storage description with a recovery register if one is defined.
     * The recoveryRegister is defined if the description contains it as an attribute.
     * 
     * @param config - the storage description
     * @throws StorageException - failed to register
     */
    protected static void registerIfRequired(ComponentDescription config) throws StorageException {
    	
    	Prim register;
    	try {
    		register = config.sfResolve(Storage.REGISTER_ATTRIB, (Prim)null, false);
    	} catch(Exception e) {
    		return;
    	}

    	try {
    		String dbname = (String)config.sfResolve(Storage.DBNAME_ATTRIB);
    		if( register != null ) {
    			ComponentDescription cd = new ComponentDescriptionImpl(null, (Context)config.sfContext().copy(), false);
    			register.sfAddAttribute(dbname, cd);
    		}
    	} catch(Exception e) {
    		throw new StorageException("Failed to register storage with recovery register", e);
    	}
    }
    
    
    /**
     * Deregisters the storage description from a recovery register if one is defined.
     * The recoveryRegister is defined if the description contains it as an attribute.
     * 
     * @param config - the storage description
     * @throws StorageException - failed to deregister
     */
    protected static void deregisterIfRequired(ComponentDescription config) throws StorageException {
    	Prim register;
    	try {
    		register = config.sfResolve(Storage.REGISTER_ATTRIB, (Prim)null, false);
    	} catch(Exception e) {
    		return;
    	}

    	try {
    		String dbname = (String)config.sfResolve(Storage.DBNAME_ATTRIB);
    		if( register != null ) {
    			register.sfRemoveAttribute(dbname);
    		}
    	} catch(Exception e) {
    		throw new StorageException("Failed to deregister storage from recovery register", e);
    	}	
    	
    	
    }
    
    
    /**
     * Tests to see if the object is a storage component description.
     * The object
     * must be a component description containing a storage class attribute.
     * 
     * @param obj object to test
     * @return true if storage description false if not
     */
	public static boolean isStorageDescription(Object obj) {
		if( !(obj instanceof ComponentDescription ) ) {
			return false;
		} else {
			return ((ComponentDescription)obj).sfContainsAttribute(Storage.CLASS_ATTRIB);
		}
	}



    /**
     * Inserts a new record in the storage space for the specified entry.
     * The entry must have been created before, otherwise an exception is thrown.
     *
     * @param entryname Object value that should be written
     * @param value new value
     *
     * @throws StorageException In case some failure happens
     */
    public abstract void addEntry(String entryname, Serializable value) throws
            StorageException;

    /**
     * Inserts a new record in the storage space for the specified entry.
     * The entry must have been created before, otherwise an exception is thrown.
     *
     * @param entryname Object value that should be written
     * @param value new value
     *
     * @throws StorageException In case some failure happens
     */
    public abstract void replaceEntry(String entryname, Serializable value) throws
            StorageException;

    /**
     * Recovers an entry from stable storage
     *
     * @param entryname index of the required entry
     * @return the entry
     */
    public abstract Serializable getEntry(String entryname) throws
            StorageException;

    /**
     * Deletes an indexed record of the given entry from the storage space
     *
     * @param entryname the internal object that should be deleted
     *
     * @throws StorageException In case some failure happens
     */
    public abstract void removeEntry(String entryname) throws StorageException;


    public abstract void commit() throws StorageException;

    public abstract void abort() throws StorageException;

    public void delete() throws StorageException {
    	deregisterIfRequired(configData);
    }

    public abstract void disableCommit();

    public abstract void enableCommit();

    public abstract Object[] getEntries() throws
            StorageException;

    public abstract StorageRef getStorageRef() throws StorageException;

    public abstract String getAgentUrl();

    public abstract void close() throws StorageException;

}
