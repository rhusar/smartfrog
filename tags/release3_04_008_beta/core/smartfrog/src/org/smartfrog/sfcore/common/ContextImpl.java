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

import org.smartfrog.sfcore.common.SmartFrogContextException;


/**
 * Implements the context interface. This implementation relies on the
 * OrderedHashtable class in the Utilities, but another class can be used. The
 * important thing for any implementation is the fact that the order in which
 * entries are added to the context should be maintained even through the
 * enumeration returning methods.
 *
 */
public class ContextImpl extends OrderedHashtable implements Context,
    Serializable {

    /**
     * Creates an empty context with default capacity.
     */
    public ContextImpl() {
    }

    /**
     * Constructs a context with initial capacity and a load trigger for
     * expansion.
     *
     * @param cap initial capacity
     * @param load load capacity trigger
     */
    public ContextImpl(int cap, float load) {
        super(cap, load);
    }

    /**
     * Returns the first key for which the value is the given one.
     * Deprecated: replaced by sfAttributeFor(value);
     * @param value value to look up
     *
     * @return key for value or null if not found
     */
    public Object keyFor(Object value) {
        return sfAttributeFor(value);
    }

    /**
     * Returns the first attribute which has a particular value in the table.
     *
     * @param value value to find in table
     *
     * @return attibute object for value or null if none
     */
    public Object sfAttributeFor(Object value){
        if (!contains(value)) {
            return null;
        }
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object theKey = e.nextElement();

            if (get(theKey).equals(value)) {
                return theKey;
            }
        }
        return null;
    }

    /**
     * Returns true if the context contains value.
     * Replaces contains()
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     */
    public boolean sfContainsValue(Object value){
       return containsKey(value);
    }


    /**
     * Returns true if the context contains attribute.
     * Replaces containsKey()
     * @param attribute to check
     *
     * @return true if context contains key, false otherwise
     */
    public boolean sfContainsAttribute(Object attribute){
       return containsKey(attribute);
    }


    /**
     * Returns an ordered iterator over the attribute names in the context.
     * The remove operation of this Iterator won't affect
     * the contents of Context
     *
     * @return iterator
     */
    public  Iterator sfAttributes(){
        return orderedAttributes();

    }

    /**
     * Returns an ordered iterator over the values in the context.
     * The remove operation of this Iterator won't affect
     * the contents of Context

     * @return iterator
     */
    public  Iterator sfValues(){
        return orderedValues();
    }



    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogContextException failed to find attribute
     */
    public Object sfResolveAttribute(Object name) throws SmartFrogContextException {
        Object result = this.get(name);
        if (result == null) {
            throw new SmartFrogContextException(
                       MessageUtil.formatMessage(MessageKeys.MSG_NOT_FOUND_ATTRIBUTE, name));
        }
        return result;
    }


    /**
      * Adds an attribute to this context under given name.
      *
      * @param name name of attribute
      * @param value value of attribute
      *
      * @return previous value for name or null if none
      *
      * @throws SmartFrogRuntimeException when name or value are null or name already used
      */
     public synchronized Object sfAddAttribute(Object name, Object value)
         throws SmartFrogContextException{
         if ((name == null) || (value == null)) {
           if (name == null) {
               throw new SmartFrogContextException(
               MessageUtil.formatMessage(MessageKeys.MSG_NULL_DEF_METHOD, "'name'",
                                         "sfAddAttribute") );
           }
           if (value == null) {
               throw new SmartFrogContextException(
               MessageUtil.formatMessage(MessageKeys.MSG_NULL_DEF_METHOD, "'value'",
                                         "sfAddAttribute"));
           }

             return null;
         }

         if (this.containsKey(name)) {
             throw new SmartFrogContextException(
             MessageUtil.formatMessage(MessageKeys.MSG_REPEATED_ATTRIBUTE, name));

         }

         return this.put(name, value);
     }

     /**
      * Removes an attribute from this context.
      *
      * @param name of attribute to be removed
      *
      * @return removed attribute value if successfull or null if not
      *
      * @throws SmartFrogContextException when name is null
      */
     public synchronized Object sfRemoveAttribute(Object name)
         throws SmartFrogContextException{
         if (name == null) {
               throw new SmartFrogContextException(
               MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                                       "sfRemoveAttribute"));
         }
         return this.remove(name);
     }

     /**
      * Replace named attribute in context. If attribute is not
      * present it is added to the context.
      *
      * @param name of attribute to replace
      * @param value value to add or replace
      *
      * @return the old value if present, null otherwise
      *
      * @throws SmartFrogContextException when name or value are null
      */
     public synchronized Object sfReplaceAttribute(Object name, Object value)
         throws SmartFrogContextException {
         if ((name == null) || (value == null)) {
             if (name == null) {
                 throw new SmartFrogContextException(
                 MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                                           "sfReplaceAttribute"));
             }
             if (value == null) {
                 throw new SmartFrogContextException(
                 MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'value'",
                                           "sfReplaceAttribute"));
             }

             return null;
         }

         return this.put(name, value);
     }


     /**
      * Returns the attribute key given a value.
      *
      * @param value value to look up key for
      *
      * @return key for attribute value or null if none
      */

     // perhaps this should be synchronized... but causes problems with sfCompleteName if it is
     public Object sfAttributeKeyFor(Object value) {
         return this.keyFor(value);
     }


}
