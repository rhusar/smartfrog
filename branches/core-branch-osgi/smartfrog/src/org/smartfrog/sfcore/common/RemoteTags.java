package org.smartfrog.sfcore.common;

import java.util.Set;
import java.util.Iterator;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the access to, and manipulation of, tags in
 * contexts, component descriptions and (in its remote form) prims.
 */
public interface RemoteTags extends Remote {

   // TAGS interface for a context
   /**
    * Set the TAGS for an attribute. TAGS are simply uninterpreted strings associated
    * with each attribute.
    *
    * @param name attribute key for tags
    * @param tags a set of tags
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfSetTags(Object name, Set tags) throws RemoteException, SmartFrogRuntimeException;

   /**
    * Get the TAGS for an attribute. TAGS are simply uninterpreted strings associated
    * with each attribute.
    *
    * @param name attribute key for tags
    * @return the set of tags
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public Set sfGetTags(Object name) throws RemoteException, SmartFrogRuntimeException;

   /**
    * add a tag to the tag set of an attribute
    *
    * @param name attribute key for tags
    * @param tag a tag to add to the set
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfAddTag(Object name, String tag) throws RemoteException, SmartFrogRuntimeException;

   /**
    * remove a tag from the tag set of an attribute if it exists
    *
    * @param name attribute key for tags
    * @param tag a tag to remove from the set
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    *
    */
   public void sfRemoveTag(Object name, String tag) throws RemoteException, SmartFrogRuntimeException;

         /**
    * add a tag to the tag set of an attribute
    *
    * @param name attribute key for tags
    * @param tags  a set of tags to add to the set
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfAddTags(Object name, Set tags) throws RemoteException, SmartFrogRuntimeException;

   /**
    * remove a tag from the tag set of an attribute if it exists
    *
    * @param name attribute key for tags
    * @param tags  a set of tags to remove from the set
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfRemoveTags(Object name, Set tags)  throws RemoteException, SmartFrogRuntimeException;

   /**
    * Return whether or not a tag is in the list of tags for an attribute
    *
    * @param name the name of the attribute
    * @param tag the tag to chack
    *
    * @return whether or not the attribute has that tag
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public boolean sfContainsTag(Object name, String tag) throws RemoteException, SmartFrogRuntimeException;

}
