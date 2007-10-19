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

/**
 * Stub objects are intended to act as placeholders inside
 * {@link ResolutionResult} objects when the target resource
 * does not exist but is intended for creation. Stub objects
 * allow the system to make the necessary differentiations in
 * order to wrap and function correctly. The AttributeStub is
 * used in place of objects that inherit from {@link org.smartfrog.sfcore.reference.Reference}
 * and objects that are not SmartFrog components.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class AttributeStub
{
	/**
	 * Creates a new, empty AttributeStub to be used in a ResolutionResult.
	 */
	public AttributeStub()
	{
		super();
	}
}
