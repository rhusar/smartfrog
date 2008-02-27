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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;


/**
 * Defines the context interface used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by Components.
 *
 * @see Copying
 */
public interface Context extends Tags, PrettyPrinting, Copying, Serializable {

    /**
     * Returns true if the context contains value. Deprecated: replaced by
     * sfContainsValue()
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     */
    public boolean contains(Object value);

    /**
     * Returns true if the context contains the key. Deprecated: replaced by
     * sfContainsAttribute()
     *
     * @param key to check
     *
     * @return true if context contains key, false otherwise
     */
    public boolean containsKey(Object key);

    /**
     * Returns the values of the context. Deprecated: replaced by sfValues();
     *
     * @return enumeration over the values of the context
     *
     * @see Enumeration
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
     * Returns an enumeration over the keys of the context. Deprecated: replaced
     * by sfAttributes();
     *
     * @return enumeration
     */
    public Enumeration keys();


    /**
     * Adds an object value under given key to context.
     *
     * @param key   key of addition
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
     *
     * @deprecated sfAttributeKeyFor should be used instead.
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


    /**
     * Returns the first attribute which has a particular value "equal" in the
     * table.
     *
     * @param value value to find in table
     *
     * @return attibute object for value or null if none
     */
    public Object sfAttributeKeyForEqual(Object value);

    /**
     * Returns true if the context contains value reference (==). Replaces
     * contains()
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     *
     * @throws NullPointerException if the value is <code>null</code>.
     */
    public boolean sfContainsRefValue(Object value);

    /**
     * Returns the attribute key for a given value.
     *
     * @param value value to look up the key for
     *
     * @return key for given value or null if not found
     */
    public Object sfAttributeKeyFor(Object value);

    /**
     * Returns true if the context contains value. Replaces contains()
     *
     * @param value object to check for equality
     *
     * @return true if context contains value, false otherwise
     */
    public boolean sfContainsValue(Object value);


    /**
     * Returns true if the context contains attribute. Replaces containsKey()
     *
     * @param attribute to check
     *
     * @return true if context contains key, false otherwise
     */
    public boolean sfContainsAttribute(Object attribute);


    /**
     * Returns an iterator over the attribute names of the context. The remove
     * operation of this Iterator won't affect the contents of Context
     *
     * @return iterator
     */
    public Iterator sfAttributes();

    /**
     * Returns an iterator over the values of the context. The remove operation
     * of this Iterator won't affect the contents of Context
     *
     * @return iterator
     */
    public Iterator sfValues();

    /**
     * Add an attribute to context. Values should be marshallable types if they
     * are to be referenced remotely at run-time. If an attribute with this name
     * already exists it is <em>not</em> replaced.
     *
     * @param name  name of attribute
     * @param value object to be added in context
     *
     * @return value if successfull, null otherwise
     *
     * @throws SmartFrogContextException when name or value are null
     */
    public Object sfAddAttribute(Object name, Object value)
            throws SmartFrogContextException;

    /**
     * Remove named attribute from component context. Non present attribute
     * names are ignored.
     *
     * @param name name of attribute to be removed
     *
     * @return the removed value if successfull, null otherwise
     *
     * @throws SmartFrogContextException when name is null
     */
    public Object sfRemoveAttribute(Object name)
            throws SmartFrogContextException;


    /**
     * Replace named attribute in context. If attribute is not present it is
     * added to the context.
     *
     * @param name  of attribute to replace
     * @param value attribute value to replace or add
     *
     * @return the old value if present, null otherwise
     *
     * @throws SmartFrogContextException when name or value are null
     */
    public Object sfReplaceAttribute(Object name, Object value)
            throws SmartFrogContextException;

    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogContextException failed to find attribute
     */
    public Object sfResolveAttribute(Object name)
            throws SmartFrogContextException;


}
