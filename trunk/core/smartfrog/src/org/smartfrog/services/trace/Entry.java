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

package org.smartfrog.services.trace;




/**
 * Defines the Entry interface.
 *
 * @deprecated 12 December 2001
 */
public interface Entry {
    /** String for return carriage.*/
    static String CRLF = "\r\n";

    /**
     *  Removes a child.
     *
     * @param  DN  dN attribute
     * @return     if removed true else false
     */
    boolean remove(String DN);


     /**
     * Adds a new child.
     *
     * @param  DN     dN attribute
     * @param  value  value
     * @return        if added true else false
     */
    boolean add(String DN, Object value);

    /**
     * Gets the leaf attribute of the Entry object.
     *
     * @return The leaf value
     */
    boolean isLeaf();

    /**
     * Return the textual representation.
     *
     * @return textual representation
     */
    String toString();

    /**
     * Gets the name attribute of the Entry object.
     *
     * @return The name value
     */
    String getName();

    /**
     * Gets the parentDN attribute of the Entry object.
     *
     * @return The parentDN value
     */
    String getParentDN();

    /**
     * Gets the dN attribute of the Entry object.
     *
     * @return The dN value
     */
    String getDN();

    /**
     * Gets the rDN attribute of the Entry object.
     *
     * @return The rDN value
     */
    String getRDN();

    /**
     * Gets the childrenCount attribute of the Entry object.
     *
     * @return The childrenCount value
     */
    int getChildrenCount();

    //Object getChildren();
    //Object getAttributes();
}
