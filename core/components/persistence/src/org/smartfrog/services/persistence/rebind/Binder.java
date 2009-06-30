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


package org.smartfrog.services.persistence.rebind;

import java.io.Serializable;



/**
 * The Binder implements binding - the RebindingStub uses this
 * object to perform binding (obtaining a new stub for the remote
 * object). It can be specialised with different
 * implementations on a per RebindingStub basis.
 */
public interface Binder extends Serializable {

	/**
	 * Obtain a new stub for the remote server object.
	 * 
	 * @return The new stub
	 * @throws BindException unable to obtain a new stub
	 */
    public Object getStub() throws BindException;

    /**
     * Determine if the remote server object is dead. Usually it is 
     * impossible to determine that the remote server object does not
     * exist, so this method actually indicates that the Binder has 
     * reached its criteria for determining that it is unable to bind 
     * to the remote server object.
     * 
     * @return true if the binder considers the remote server object inaccessible, false otherwise 
     * @throws BindException 
     * 
     * TODO is bind exception not needed?
     */
    public boolean isDead() throws BindException;
    
    /**
     * The equals operator for Binders. Two binders are considered equal if they both bind to 
     * the same server object. Binders must contain the notion of identity of a remote server object
     * to perform binding. Binder equality is a notion of remote server object equality that
     * spans incarnations of a recoverable object. 
     * 
     * @param obj the Binder that this Binder is tested against.
     * @return true if the two binders refer to the same identity, false otherwise
     */
    public boolean equals(Object obj);
    
    /**
     * The delay to apply after an unsuccessful attempt to get a stub before trying again.
     */
    public void retryDelay();

}
