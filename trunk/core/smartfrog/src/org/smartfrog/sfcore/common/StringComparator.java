/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import java.io.Serializable;
import java.util.Comparator;

/**
 *  Compare two strings
 *
 *@author     julgui
 *created    04 October 2001
 */
class StringComparator  implements Comparator, Serializable {

   /**
    *  Description of the Method
    *
    *@param  o1  Description of Parameter
    *@param  o2  Description of Parameter
    *@return     Description of the Returned Value
    */
   public int compare(
         Object o1, Object o2) {
      if (!(o1 instanceof String)) {
         throw new ClassCastException();
      }
      if (!(o2 instanceof String)) {
         throw new ClassCastException();
      }

      int result = ((String)o1).
            compareTo(((String)o2));
      return result * (-1);
   }
   //end compare()
}
