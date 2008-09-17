/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.utils;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.List;
import java.io.Serializable;

/**
 *
 * A serializable enumeration. 
 *
 */

public class SerializableEnumeration<T> implements Enumeration<T>, Serializable {

    private SerializableIterator<T> iterator;

    public SerializableEnumeration(SerializableIterator<T> iterator) {
        this.iterator = iterator;
    }

    public SerializableEnumeration(List<T> source) {
        iterator=new SerializableIterator<T>(source);
    }

    public SerializableEnumeration(T[] source) {
        iterator = new SerializableIterator<T>(source);
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if and only if this enumeration object contains at least one more element to provide;
     *         <code>false</code> otherwise.
     */
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    /**
     * Returns the next element of this enumeration if this enumeration object has at least one more element to provide.
     *
     * @return the next element of this enumeration.
     * @throws NoSuchElementException if no more elements exist.
     */
    public T nextElement() {
        return iterator.next();
    }
}
