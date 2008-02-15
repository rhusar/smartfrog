/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ReadOnlySetWrapper can be used to wrap any object that implements
 * the Set interface to convert it into a read-only version. It 
 * implements all the Set methods except those that change the 
 * composition of the set. Methods to add or remove objects from 
 * the set return UnsurpportedOperationException.
 * 
 * The wrapper that is used to wrap null will behave as an empty set.
 */
public class ReadOnlySetWrapper implements Set, Serializable {
    
    private static final Set nullSet = new HashSet();

    private Set impl;
    
    private ReadOnlySetWrapper(Set set) {
        setImpl(set);
    }
    
    public static Set wrap(Set set) {
        return new ReadOnlySetWrapper(set);
    }
    
    public void setImpl(Set set) {
        if( set == null ) {
            impl = nullSet;
        } else {
            impl = set;
        }
    }
    
    public Set getImpl() {
        return impl;
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        return impl.contains(o);
    }

    public boolean containsAll(Collection c) {
        return impl.containsAll(c);
    }

    public boolean equals(Object o) {
        return impl.equals(o);
    }

    public int hashCode() {
        return impl.hashCode();
    }

    public boolean isEmpty() {
        return impl.isEmpty();
    }

    public Iterator iterator() {
        return impl.iterator();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return impl.size();
    }

    public Object[] toArray() {
        return impl.toArray();
    }

    public Object[] toArray(Object[] a) {
        return impl.toArray(a);
    }
    
    public String toString() {
        return impl.toString();
    }
    
}
