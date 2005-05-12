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
package org.smartfrog.sfcore.languages.cdl.utils;

import org.jdom.filter.Filter;
import org.jdom.Element;

/**
 * created 06-May-2005 13:58:26
 */

public class TypeFilter implements Filter {

    Class clazz;

    public TypeFilter(Class clazz) {
        assert clazz!=null;
        this.clazz = clazz;
    }


    /**
     * Check to see if the object is an element
     *
     * @param obj The object to verify.
     * @return <code>true</code> if the object matches a predfined set of
     *         rules.
     */
    public boolean matches(Object obj) {
        return obj.getClass().equals(clazz);
    }

    public static Filter elementFilter() {
        return new TypeFilter(Element.class);
    }
}
