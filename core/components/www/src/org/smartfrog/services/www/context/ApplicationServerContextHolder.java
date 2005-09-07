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
package org.smartfrog.services.www.context;

import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.ServletContextIntf;

import java.util.Collection;
import java.util.HashMap;

/**
 * This is a helper class to hold and manage application server contexts;
 *
 */
public class ApplicationServerContextHolder {


    private HashMap contexts=new HashMap();

    private int key_count=1;

    private static final String PREFIX = "_ctx_";


    /**
     * create a new context.
     * @return a new context
     */
    public synchronized String createNewKey() {
        String key=PREFIX +key_count;
        key_count++;
        return key;
    }

    /**
     * get a new entry
     * @param entry
     */
    public synchronized void add(ApplicationServerContextEntry entry) {
        if(entry.getContextHandle()==null) {
            entry.setContextHandle(createNewKey());
        }
        contexts.put(entry.getContextHandle(),entry);
    }

    /**
     * Get the contexts.
     * @return the collection of all entries
     */
    public Collection contexts() {
        return contexts.values();
    }

    /**
     * look up an entry
     * @param key
     * @return the entry or null for no match
     */
    public ApplicationServerContextEntry lookup(String key) {
        return (ApplicationServerContextEntry) contexts.get(key);
    }

    /**
     * remove an entry
     * @param key entry to remove
     */
    public synchronized void remove(String key) {
        contexts.remove(key);
    }

    /**
     * Create and add a servlet entry
     * @param implementation
     * @return the new entry
     */
    public ApplicationServerContextEntry createServletEntry(ServletContextIntf implementation) {
        return createEntry(ApplicationServerContextEntry.TYPE_SERVLET_CONTEXT, implementation);
    }

    /**
     * Create and add a any context entry
     * @param implementation
     * @return the new entry
     */
    public ApplicationServerContextEntry createEntry(int type, ApplicationServerContext implementation) {
        ApplicationServerContextEntry entry = new ApplicationServerContextEntry();
        entry.setType(type);
        entry.setImplementation(implementation);
        add(entry);
        return entry;
    }

}
