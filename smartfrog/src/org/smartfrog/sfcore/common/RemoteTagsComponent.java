/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Interface that defines the access to, and manipulation of, tags of
 * component descriptions and (in its remote form) prims.
 */
public interface RemoteTagsComponent extends Remote {

   /**
    * Set the TAGS for this Component. TAGS are simply uninterpreted strings associated
    * with each attribute.
    *
    * @param tags a set of tags
    * @throws RemoteException network problems
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfSetTags( Set tags) throws RemoteException, SmartFrogRuntimeException;

   /**
    * Get the TAGS for this Component. TAGS are simply uninterpreted strings associated
    * with each attribute.
    *
    * @return the set of tags
    * @throws RemoteException network problems
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public Set sfGetTags() throws RemoteException, SmartFrogRuntimeException;

   /**
    * add a tag to the tag set of this component
    *
    * @param tag a tag to add to the set
    * @throws RemoteException network problems
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfAddTag( String tag) throws RemoteException, SmartFrogRuntimeException;

   /**
    * remove a tag from the tag set of this component if it exists
    * @param tag a tag to remove from the set
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    *
    */
   public void sfRemoveTag( String tag) throws RemoteException, SmartFrogRuntimeException;

  /**
    * add a tag to the tag set of this component
    *
    * @param tags  a set of tags to add to the set
    * @throws RemoteException network problems
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfAddTags( Set tags) throws RemoteException, SmartFrogRuntimeException;

   /**
    * remove a tag from the tag set of this component if it exists
    *
    * @param tags  a set of tags to remove from the set
    * @throws RemoteException network problems
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public void sfRemoveTags(Set tags)  throws RemoteException, SmartFrogRuntimeException;

   /**
    * Return whether or not a tag is in the list of tags for this component
    *
    * @param tag the tag to chack
    *
    * @return whether or not the attribute has that tag
    * @throws RemoteException network problems
    * @throws SmartFrogRuntimeException failed to perform the operation - implementations
    * may throw subtypes of SmartFrogRuntimeException to indicate specific problems
    */
   public boolean sfContainsTag( String tag) throws RemoteException, SmartFrogRuntimeException;

}
