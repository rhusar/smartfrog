/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
import java.util.Set;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Storage is an abstract class for a storage object. Each recoverable component has
 * its own storage object that acts as an adapter to the actual storage implementation.
 */
public abstract class Storage {

	/**
	 * Attribute defining the class used to access the storage - the storage implementation
	 */
    public static final String STORAGE_CLASS_ATTR = "sfStorageClass";

    /**
     * Attribute defining the unique name for this component
     */
    public static final String COMPONENT_NAME_ATTR = "sfUniqueComponentName";

    /**
     * The default name used to locate a connection pool
     */
    public static final String CONNECTION_POOL_ATTR = "ConnectionPool";
    
    /**
     * This value is used to indicate that the component is an orphan. If the component
     * had a parent value of null it would be considered the root of a deployment. It
     * must have a parent that is missing to be an orphan - this value is used as a
     * "universally missing parent".
     */
    public static final String NO_PARENT_PENDING_TERMINATION = "noParent";

    /**
     * This method constructs the storage class defined in a storage description
     * and opens it for access to its backing store. If successful he storage object is 
     * returned. 
     * 
     * @param config a storage description
     * @return the opened storage object
     * @throws StorageException failed to construct the storage class or open the storage
     */
    public static Storage openStorage(ComponentDescription config) throws StorageException {

        if (config == null) {
            throw new StorageException("Storage config is null");
        }

        try {

            String className = config.sfResolve(STORAGE_CLASS_ATTR, (String) null, true);
            Storage storage = (Storage) Class.forName(className).newInstance();
            storage.openStorage0(config);
            return storage;

        } catch (SmartFrogResolutionException e) {
            throw new StorageException("Storage configuration is invalid: " + config, e);
        } catch (InstantiationException e) {
            throw new StorageException("Failed to construct storage class: " + config, e);
        } catch (IllegalAccessException e) {
            throw new StorageException("Failed to access storage class: " + config, e);
        } catch (ClassNotFoundException e) {
            throw new StorageException("Storage class not found: " + config, e);
        }

    }

    /**
     * This method is used by the storage implementation class to open its storage.
     * 
     * @param config a storage description
     * @throws StorageException failed to open the storage
     */
    public abstract void openStorage0(ComponentDescription config) throws StorageException;
    
    /**
     * Setter for storage exception notifications. This is used to indicate the object
     * that should be informed if the storage component hits a storage exception.
     * 
     * @param notify the target to notify
     */
    public abstract void exceptionNotifications(StorageExceptionNotification notify);

    /**
     * Opens a new transaction against the storage.
     * 
     * @return the transaction
     * @throws StorageException failed to open a transaction
     */
    public abstract Transaction getTransaction() throws StorageException;

    /**
     * Setup component register
     * 
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void initialiseRegister(Transaction xact) throws StorageException;
    
    /**
     * Setup attributes table
     * 
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void initialiseAttributes(Transaction xact) throws StorageException;    
    
    /**
     * Returns a set of storage descriptions for components that are the roots
     * of a local tree fragment to be recovered.
     * 
     * @param xact transaction to use
     * @return recovery root components
     * @throws StorageException
     */
    public abstract Set getRecoveryRoots(Transaction xact) throws StorageException;
    
    /**
     * Returns a set of storage descriptions for components that are the roots of
     * local tree fragments that have been orphaned due to failure during asynchronous
     * termination.
     * 
     * @param xact transaction to use
     * @return orphan root components
     * @throws StorageException
     */
    public abstract Set getOrphanRoots(Transaction xact) throws StorageException;
    
    /**
     * Constructs the component in the storage without any attributes. The
     * component must not already exist. If xact is null this will be done as a
     * seperate transaction. If xact is not null it will be done within that
     * transaction. The localParent attribute is the storage name of the local
     * parent of this component if it has one. If there is no local parent this
     * should be null to indicate that this is the root of the local tree fragment.
     * 
     * @param localParent unique storage name of parent
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void createComponent(String localParent, Transaction xact) throws StorageException;

    /**
     * Remove the component storage. If xact is null this will be done as a
     * seperate transaction. If xact is not null it will be done within that
     * transaction.
     * 
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void deleteComponent(Transaction xact) throws StorageException;
    
    /**
     * Change the parentage of this component. A null entry for the parent indicates
     * that this component is the root of a local tree fragment.
     * 
     * @param parent unique name of new parent
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void reparentComponent(String parent, Transaction xact) throws StorageException;

    /**
     * Add the given attribute to the storage. If xact is null this will be done
     * as a separate transaction. If xact is not null it will be done within
     * that transaction.
     * 
     * @param name unique name of attribute
     * @param tags component tags
     * @param value component value
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void addAttribute(String name, Serializable tags, Serializable value, Transaction xact)
            throws StorageException;

    /**
     * Remove the given attribute from storage. If xact is null this will be
     * done as a separate transaction. If xact is not null it will be done
     * within that transaction.
     * 
     * @param name of attribute
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void removeAttribute(String name, Transaction xact) throws StorageException;

    /**
     * Remove all attributes from storage. If xact is null this will be
     * done as a separate transaction. If xact is not null it will be done
     * within that transaction.
     * 
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void removeAllAttributes(Transaction xact) throws StorageException;

    /**
     * Replace the given attribute value in storage. This method will retain the
     * old tags field, changing just the value of the attribute. If xact is null
     * this will be done as a separate transaction. If xact is not null it will
     * be done within that transaction.
     * 
     * @param name name of attribute
     * @param value new value
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void replaceAttribute(String name, Serializable value, Transaction xact) throws StorageException;

    /**
     * Replace the given attribute tags in storage. This method will retain the
     * old value field, changing just the tags of the attribute. If xact is null
     * this will be done as a separate transaction. If xact is not null it will
     * be done within that transaction.
     * 
     * @param name name of attribute
     * @param tags new tags
     * @param xact transaction to use
     * @throws StorageException
     */
    public abstract void setTags(String name, Serializable tags, Transaction xact) throws StorageException;
    
    /**
     * Returns true if the storage object represents an existing stored 
     * component. This returns true if the storage represents a component
     * that exists and can be accessed through the store, and false otherwise.
     * Note that a storage object can be constructed before the component is
     * created, and that it remains after a component has been deleted, so
     * it is valid for a storage object to represent a component that does not
     * exist.
     * 
     * @param xact transaction to use
     * @return true if the component exists in the storage
     * @throws StorageException
     */
    public abstract boolean exists(Transaction xact) throws StorageException;

    /**
     * Populate the context with the contents of the storage object as a single
     * transaction. If xact is null this will be done as a separate transaction.
     * If xact is not null it will be done within that transaction.
     * 
     * @param xact transaction to use
     * @return the context
     * @throws StorageException
     */
    public abstract Context getContext(Transaction xact) throws StorageException;

    /**
     * Close the storage without removing it. Open transactions obtained are not 
     * affected storage closure, but further calls to the storage object 
     * will be invalid after this method.
     *
     * @throws StorageException
     */
    public abstract void close() throws StorageException;

    /**
     * Tests to see if the object is a storage component description. The object
     * must be a component description containing a storage class attribute.
     * 
     * @param obj the object to check
     * @return true if storage description false if not
     */
    public static boolean isStorageDescription(Object obj) {
        if (!(obj instanceof ComponentDescription)) {
            return false;
        } else {
            return ((ComponentDescription) obj).sfContainsAttribute(Storage.STORAGE_CLASS_ATTR);
        }
    }
    


}
