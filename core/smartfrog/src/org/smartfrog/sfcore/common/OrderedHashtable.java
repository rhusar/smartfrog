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
import java.util.Hashtable;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogCoreProperty;


/**
 * Implements a hashtable which maintains the order in which the elements were
 * added to the hashtable. This is important for deployment and resolution of
 * components since lexical ordering needs to be maintained for overwrites.
 *
 */
public class OrderedHashtable extends Hashtable implements Copying,
    Serializable {



    /**
     * Initial capacity for hashtables. Looks up
     * Common.OrderedHashtable.initCap (offset by SFSystem.propBase). Defaults
     * to 10 if not there.
     */
    public static int initCap = Integer.getInteger(SmartFrogCoreProperty.initCapOrderedHashTable, 10)
                                       .intValue();

    /**
     * Load percentage for hashtable growth. Looks up
     * Common.OrderedHashtable.loadFac (offset by SFSystem.propBase). Defaults
     * to 95(%) if not there.
     */
    public static float loadFac = Integer.getInteger(SmartFrogCoreProperty.loadFacOrderedHashTable, 95)
                                         .floatValue() / 100;

    /**
     * Increment size for keys in orderer hashtable.
     * Common.OrderedHashtable.keysInc (offset by SFSystem.propBase). Defaults
     * to 5 if not there.
     */
    public static int keysInc = Integer.getInteger(SmartFrogCoreProperty.keysIncOrderedHashTable, 5)
                                       .intValue();

    /** Vector for ordered keys. */
    public Vector orderedKeys = new Vector(initCap, keysInc);

    /**
     * Constructs an ordered hashtable with default capacity (10) and load
     * factor (0.95).
     */
    public OrderedHashtable() {
        this(initCap, loadFac);
    }

    /**
     * Constructs an ordered hashtable with given capacity and load factor.
     * Also makes sure the orderedKeys vector has at least that capacity.
     *
     * @param cap initial capacity
     * @param fac load factor
     */
    public OrderedHashtable(int cap, float fac) {
        super(cap, fac);
        orderedKeys.ensureCapacity(cap);
    }

    /**
     * Clears the keys as well as the hashtable.
     * Overwrites Hashtable.clear().
     */
    public void clear() {
        super.clear();
        orderedKeys.removeAllElements();
    }

    /**
     * Does a deep copy of the hashtable. Values in the hashtable which
     * understand the Copying interface get copied properly.
     *
     * @return copy of hashtable
     */
    public Object copy() {
        OrderedHashtable r = (OrderedHashtable) clone();

        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = get(key);

            if (value instanceof Copying) {
                value = ((Copying) value).copy();
            }

            r.primPut(key, value);
        }

        return r;
    }

    /**
     * Internal method to call the basic put method on hashtables. This will
     * NOT add the key into the ordered table. Used mainly by copy().
     * Overwrites Hashtable.put().
     *
     * @param key key of attribute to put
     * @param value value to put
     *
     * @return previous value or null if none
     */
    protected Object primPut(Object key, Object value) {
        return super.put(key, value);
    }

    /**
     * Add an object to the hashtable.
     *
     * @param key key for association
     * @param value value for hashtable
     *
     * @return previous value for key or null if none
     */
    public Object put(Object key, Object value) {
        Object r = primPut(key, value);

        if (orderedKeys == null) {
            // we are deserializing ignore adds to ordered keys
            return r;
        }

        // No previous value, add key
        if (!orderedKeys.contains(key)) {
            orderedKeys.addElement(key);
        }

        return r;
    }

    /**
     * Removes an entry from the hashtable.
     * Overwrites Hashtable.remove().
     *
     * @param key key to remove
     *
     * @return removed object
     */
    public Object remove(Object key) {
        Object r = super.remove(key);

        if (r != null) {
            orderedKeys.removeElement(key);
        }

        return r;
    }

    /**
     * Renames an entry in the otable, leaving its position in the table
     * unchanged.
     *
     * @param key1 the initial key to be renamed
     * @param key2 the new key name
     *
     * @return the initial key, or null if it wasn't in the table
     */
    public Object rename(Object key1, Object key2) {
        int i = orderedKeys.indexOf(key1);

        if (i == -1) {
            return null;
        }

        orderedKeys.setElementAt(key2, i);
        super.put(key2, get(key1));
        super.remove(key1);

        return key1;
    }

    /**
     * Returns an ordered enumeration over the keys of this hashtable.
     *
     * @return enumeration over keys
     */
    public Enumeration keys() {
        return orderedKeys.elements();
    }

    /**
     * Returns the values of the hashtable in order.
     *
     * @return ordered enumeration over values
     */
    public Enumeration elements() {
        Vector r = new Vector(orderedKeys.size());

        for (Enumeration e = keys(); e.hasMoreElements();)
            r.addElement(get(e.nextElement()));

        return r.elements();
    }

    /**
     * Does a shallow copy of the hashtable and the ordered keys.
     *
     * @return shallow copy of this table
     */
    public Object clone() {
        Object ret = super.clone();
        ((OrderedHashtable) ret).orderedKeys = (Vector) orderedKeys.clone();

        return ret;
    }
}
