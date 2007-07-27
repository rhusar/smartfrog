/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.sf.sfcomponentdescription;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;

/**
 *  Specifies the resolution interface to be implemented by all attributes
 *  (including component attributes, that need resolution at compile time and
 *  pre-deployment time.
 */
public interface ComponentResolver {
   /**
    *  Internal method that place resolves a parsed component. Place resolving
    *  places all attributes which have a reference (eager) as attribute name in
    *  their target component.
    *
    * @throws  SmartFrogResolutionException failed to place resolve
    */
   public void placeResolve() throws SmartFrogResolutionException;


   /**
    *  Internal recursive method for doing the actual placement resolution.
    *  Implementors place any attributes with an eager reference as key in the
    *  prospective component.
    *
    * @param  resState      resolution state
    *
    * @throws  SmartFrogResolutionException failed to place resolve
    */
   public void doPlaceResolve(ResolutionState resState) throws SmartFrogResolutionException;


   /**
    *  Internal method that type resolves a parsed component. Place resolving
    *  finds all supertypes and flattens them out.
    *
    * @throws  SmartFrogResolutionException failed to type resolve
    */
   public void typeResolve() throws SmartFrogResolutionException;


   /**
    *  Internal recursive method for doing the actual type resolution.
    *
    * @param  resState      resolution state
    *
    * @throws  SmartFrogResolutionException failed to type resolve
    */
   public void doTypeResolve(ResolutionState resState) throws SmartFrogResolutionException;


   /**
    *  Internal method that performs a pre-deployment resolution on the object
    *  implementing this interface. Pre-deployment resolution means finding all
    *  eager reference values, resolving them, and copying the result into the
    *  target component
    *
    * @throws  SmartFrogResolutionException failed to deploy resolve
    */
   public void linkResolve() throws SmartFrogResolutionException;


   /**
    *  Internal recursive method for doing the actual link resolution.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogResolutionException failed to deploy resolve
    */
   public void doLinkResolve(ResolutionState resState) throws SmartFrogResolutionException;
}
