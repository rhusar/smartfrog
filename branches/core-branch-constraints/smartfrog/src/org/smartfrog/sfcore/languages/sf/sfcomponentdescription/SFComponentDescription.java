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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Vector;

/**
 *  Defines the context interface used by Components. Context implementations
 *  need to respect the ordering and copying requirements imposed by Components.
 *
 */
public interface SFComponentDescription extends ComponentDescription,
      ComponentResolver, Phases {
   /**
    *  Get prototypes for this description. This are the components from which
    *  attributes get copied into this description.
    *
    * @return    types for this description
    *
    * @see #setTypes
    */
   public Vector getTypes();


   /**
    *  Set new types for this component.
    *
    * @param  type  new prototypes for description
    *
    * @return       old types
    *
    * @see #getTypes
    */
   public Vector setTypes(Vector type);

   /**
    *  add a new type for this component.
    *
    * @param  type  new prototype for description
    *
    */
   public void addType(Reference type);

      /**
    *  add a new set of attributes for this component.
    *
    * @param  type  new set of attributes for description
    *
    */
    public void addType(SFComponentDescription type);
}
