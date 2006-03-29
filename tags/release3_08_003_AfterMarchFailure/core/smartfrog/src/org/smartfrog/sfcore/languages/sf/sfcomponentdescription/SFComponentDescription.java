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

/**
 *  Defines the context interface used by Components. Context implementations
 *  need to respect the ordering and copying requirements imposed by Components.
 *
 */
public interface SFComponentDescription extends ComponentDescription,
      ComponentResolver, Phases {
   /**
    *  Get prototype for this description. This is the component from which
    *  attributes get copied into this description.
    *
    * @return    type for this description
    *
    * @see #setType
    */
   public Reference getType();


   /**
    *  Set new type for this component.
    *
    * @param  type  new prototype for description
    *
    * @return       old type
    *
    * @see #getType
    */
   public Reference setType(Reference type);
}
