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

package org.smartfrog.sfcore.common;

import java.util.Enumeration;


/**
 * Defines the context interface used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 *
 * @see Copying
 */
public interface Context extends Copying {
    /**
     * Returns true if the context contains value.
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     */
    public boolean contains(Object value);

    /**
     * Returns true if the context contains the key.
     *
     * @param key to check
     *
     * @return true if context contains key, false otherwise
     */
    public boolean containsKey(Object key);

    /**
     * Returns the values of the context.
     *
     * @return enumeration over the values of the context
     *
     * @see java.util.Enumeration
     */
    public Enumeration elements();

    /**
     * Returns object stored under given key, null if not found.
     *
     * @param key to look up
     *
     * @return object under key if found, null otherwise
     */
    public Object get(Object key);

    /**
     * Returns true if the context is empty.
     *
     * @return true if context empty, false otherwise
     */
    public boolean isEmpty();

    /**
     * Returns an enumeration over the keys of the context.
     *
     * @return enumeration
     */
    public Enumeration keys();

    /**
     * Adds an object value under given key to context.
     *
     * @param key key of addition
     * @param value value of addition
     *
     * @return the previous value under key, or null if none
     */
    public Object put(Object key, Object value);

    /**
     * Renames an entry in the otable, leaving its position in the table
     * unchanged.
     *
     * @param key1 the initial key to be renamed
     * @param key2 the new key name
     *
     * @return the initial key, or null if it wasn't in the table
     */
    public Object rename(Object key1, Object key2);

    /**
     * Removes context entry under given key.
     *
     * @param key context entry to be removed
     *
     * @return object that was removed
     */
    public Object remove(Object key);

    /**
     * Returns the number of context entries.
     *
     * @return size of context (in entries)
     */
    public int size();

    /**
     * Returns the first key which has a particular value in the table.
     *
     * @param value value to find in table
     *
     * @return key for value or null if none
     */
    public Object keyFor(Object value);

    /**
     * Return string representation of context.
     *
     * @return string representation
     */
    public String toString();

    /**
     * Returns a normal (shallow) clone of the context.
     *
     * @return clone of context
     */
    public Object clone();
}
