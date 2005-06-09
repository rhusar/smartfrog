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

import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;

import java.util.HashMap;

/**
 * Represents an imported document.
 * created 08-Jun-2005 13:39:12
 */

public class ImportedDocument /*extends CdlDocument */{

    private String namespace;

    private String location;

    /**
     * Get the namespace of a document (may be null)
     *
     * @return namespace or null
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * get the location of a document
     *
     * @return the documents location
     */
    public String getLocation() {
        return location;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static class ImportMap extends HashMap<String,ImportedDocument> {
        /**
         * Constructs an empty <tt>HashMap</tt> with the default initial capacity
         * (16) and the default load factor (0.75).
         */
        public ImportMap() {
        }

        /**
         * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
         * values themselves are not cloned.
         *
         * @return a shallow copy of this map.
         */
        public Object clone() {
            return super.clone();
        }

        
    }
}
