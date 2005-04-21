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
package org.smartfrog.sfcore.languages.cdl.dom;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * created 21-Apr-2005 14:26:55
 */

public class PropertyList extends DocumentedNode {

    /** child list */
    private List<PropertyList> children=new LinkedList<PropertyList>();

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public List<PropertyList> children() {
        return children;
    }
}
