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
package org.smartfrog.sfcore.languages.csf.csfcomponentdescription;

import org.smartfrog.sfcore.languages.csf.constraints.Constraint;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;

import java.util.Vector;

/**
 *  Defines the context interface used by Components. Context implementations
 *  need to respect the ordering and copying requirements imposed by Components.
 *
 */
public interface CSFComponentDescription extends SFComponentDescription, ComponentResolver {

   /**
    *  Set new coonstraints for this component.
    *
    * @param  constraints  new constraints for description
    * @return the previous constraints
    */
   public Vector setConstraints(Vector constraints);

   /**
    *  Return coonstraints for this component.
    *
    * @return the vector of constraints
    */
    public Vector getConstraints();

    /**
     *  add new coonstraint for this component.
     *
     * @param  constraint  new constraints for description
     */
    public void addConstraint(Constraint constraint);
    
}
