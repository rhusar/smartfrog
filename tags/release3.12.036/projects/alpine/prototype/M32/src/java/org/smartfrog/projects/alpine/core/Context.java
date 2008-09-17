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

package org.smartfrog.projects.alpine.core;

import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.interfaces.Validatable;

import java.util.Hashtable;

/**
 * A mostly un-threadsafe object.
 * A basic context consists of a list of things off name,value pairs
 */
public class Context implements Validatable {
    
    /**
     * string uri to value map.
     */ 
    private Hashtable<String, Object> content=new Hashtable<String, Object>();

    /**
     * get our content map
     * @return the Hashtable of this context
     */ 
    public Hashtable<String, Object> content() {
        return content;
    }

    
    /**
     * look up an entry
     * @param key string to search on
     * @return object or null
     * @see Hashtable#get(Object)   
     */ 
    public Object get(String key) {
        return content.get(key);
    }
    
    /**
     * add or replace an entry in the table.
     * @param key
     * @param value
     * @see Hashtable#put(Object,Object)   
     */ 
    public void put(String key, Object value) {
        content.put(key,value);
    }

    /**
     * Remove an entry
     * @see Hashtable#remove(Object)  
     * @param key
     */ 
    public void remove(String key) {
        content.remove(key);
    }
    
    /**
     * validate an instance by checking that everything in our content that is validatable,
     * thinks that it is valid. 
     * Return true if the object is valid, thrown an exception if not. It is imperative that this call
     * has <i>No other side effects</i>.
     *
     * @return true unless an exception is thrown
     * @throws ValidationException with text if not valid
     */
    public boolean validate() throws ValidationException {
        for(Object entry:content.values()) {
            if(entry instanceof Validatable) {
                Validatable testpoint=(Validatable) entry;
                testpoint.validate();
            }
        }
        return true;
    }

    /**
     * copy something from one context to another, if it is set in the source
     * @param source source context
     * @param key key to look for
     */
    public void copy(Context source,String key) {
        final Object value = source.get(key);
        if(value!=null) {
            put(key,value);
        }
    }

}
