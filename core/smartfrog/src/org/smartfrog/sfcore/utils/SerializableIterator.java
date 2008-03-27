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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * This is a special iterator that is designed to be serializable; you can send it over the wire and it does not complain.
 *
 * It works by taking a snapshot of all objects into an array.
 *
 * 1. It is not thread safe!
 * 2. If there is something in the array that doesn't serialize, you will still get an exception.
 *
 * Created 19-Mar-2008 13:45:47
 *
 */

public class SerializableIterator<T> implements Iterator<T>, Iterable<T>, Serializable {

    private Object[] elements= EMPTY;
    private int pos=0;
    private static final Object[] EMPTY = new Object[0];

    /**
     * build from a list
     * @param source source list
     */
    public SerializableIterator(List<T> source) {
        build(source);
    }

    private void build(List<T> source) {
        elements=new Object[source.size()];
        int counter=0;
        for(T element:source) {
            elements[counter++]=element;
        }
    }

    /**
     * Build from an array
     * @param source
     */
    public SerializableIterator(T[] source) {
        elements = new Object[source.length];
        int counter = 0;
        for (T element : source) {
            elements[counter++] = element;
        }
    }


    /**
     * Build from an iterator.
     * This requires two walks of the iteration, so is not super-efficient -it's O(n) work.
     * @param source source iterator
     */
    public SerializableIterator(Iterator<T> source) {

        //build an array list from the values
        ArrayList<T> values=new ArrayList<T>();
        while (source.hasNext()) {
            T o = source.next();
            values.add(o);
        }
        //and now we have the list
        build(values);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns <tt>true</tt> if <tt>next</tt>
     * would return an element rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return pos<elements.length;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements.
     */
    public T next() {
        return (T)elements[pos++];
    }

    /**
     * Removes from the underlying collection the last element returned by the iterator (optional operation).  This
     * method can be called only once per call to <tt>next</tt>.  The behavior of an iterator is unspecified if the
     * underlying collection is modified while the iteration is in progress in any way other than by calling this
     * method.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this Iterator.
     * @throws IllegalStateException         if the <tt>next</tt> method has not yet been called, or the <tt>remove</tt>
     *                                       method has already been called after the last call to the <tt>next</tt>
     *                                       method.
     */
    public void remove() {
        throw new UnsupportedOperationException("This iterator does not support removal");
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<T> iterator() {
        return this;
    }
}
