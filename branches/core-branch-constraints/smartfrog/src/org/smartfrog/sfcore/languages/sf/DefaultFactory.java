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
package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;

/**
 *  This is the default factory for tree nodes for the SmartFrog parser.
 */
public class DefaultFactory implements Factory {
   /**
    *  Constructor.
    */
   public DefaultFactory() { }


   /**
    *  Return a ComponentDescriptionImpl for a root or default node - an error
    *  otherwise.
    *
    *  @param type of node to create
    *  @return ComponentDescription for root and default nodes
    *  @exception ParseException if found any other node type
    */
   public SFComponentDescription node(String type) throws ParseException {
      if (type.equals("root") | type.equals("default")) {
         return (new SFComponentDescriptionImpl(null, null,
               new ContextImpl(), false));
      } else {
         throw new ParseException("unknown node type " + type);
      }
   }
}
