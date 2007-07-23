/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.mbeanbrowser;

import java.util.Comparator;
import org.smartfrog.services.jmx.common.SFAttribute;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SFAttributeComparator implements Comparator {

    /**
     *  TODO JavaDoc method compare
     *
     *@param  o1
     *@param  o2
     *@return int
     */
    public int compare(Object o1, Object o2) {
        SFAttribute a1 = (SFAttribute) o1;
        SFAttribute a2 = (SFAttribute) o2;
        return (a1.getName().compareTo(a2.getName()));
    }

}
