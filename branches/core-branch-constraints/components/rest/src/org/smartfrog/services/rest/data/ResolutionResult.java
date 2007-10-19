/**
	(C) Copyright 2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.rest.data;

import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.rmi.RemoteException;

/**
 * Used to store a subject and its owner within the context
 * of a SmartFrog REST request. The owner is expected to be
 * a traversable SmartFrog component (that is, inherit from
 * {@link org.smartfrog.sfcore.prim.Prim} or {@link org.smartfrog.sfcore.componentdescription.ComponentDescription}.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class ResolutionResult
{
	/**
	 * Creates a new Result object which holds reference to the subject, its owner
	 * and the root process as defined by the target in the HTTP request.
	 *
	 * @param subject Any object within a SmartFrog system
	 * @param owner Any object capable of ownership (that is, descendants of {@link org.smartfrog.sfcore.prim.Prim} and {@link org.smartfrog.sfcore.componentdescription.ComponentDescription}.
	 * @param rootProcessCompound A reference to the root process associated with this HTTP REST request.
	 */
	public ResolutionResult(Object subject, Object owner, ProcessCompound rootProcessCompound)
	{
		this.owner = owner;
		this.subject = subject;
		this.rootProcessCompound = rootProcessCompound;
	}

	/**
	 * Returns a reference to the subject (that is, intended target) of this request. Can be any object.
	 * @return Reference to the subject.
	 */
	public Object getSubject()
	{
		return subject;
	}

	/**
	 * Returns a reference to the owner object (assumed to be an object capable of parentage, see {@link #ResolutionResult}.
	 * @return Reference to the owner.
	 */
	public Object getOwner()
	{
		return owner;
	}

	/**
	 * Returns a reference to the root process associated with this specific HTTP REST request.
	 * @return Reference to the root process compound.
	 */
	public ProcessCompound getRootProcessCompound()
	{
		return rootProcessCompound;
	}

	/**
	 * Determines if the intended target is actually the root process itself.
	 *
	 * @return <code>true</code> if the intended target is the root process compound, <code>false</code> otherwise.
	 * @throws RemoteException If a network error occurs during attempted remote invocation of this method.
	 */
	public boolean subjectIsRoot() throws RemoteException
	{
		return (subject instanceof ProcessCompound) && ((ProcessCompound) subject).sfIsRoot();
	}

	/**
	 * Determines if the objects deemed owner is actually the root process itself.
	 *
	 * @return <code>true</code> if the named owner is the root process compound, <code>false</code> otherwise.
	 * @throws RemoteException If a network error occurs during attempted remote invocation of this method.
	 */
	public boolean ownerIsRoot() throws RemoteException
	{
		return (owner instanceof ProcessCompound) && ((ProcessCompound) owner).sfIsRoot();
	}

	private final Object owner;
	private final Object subject;
	private final ProcessCompound rootProcessCompound;
}