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

package org.smartfrog.projects.alpine.xmlutils;

import nu.xom.Elements;
import nu.xom.Element;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * an iterator that iterates over elements
 */
public class BaseElementsIterator <E extends Element> implements Iterator<E>,
        Iterable<E>  {
    
    private Elements elements;
    
    private int index=0;

    public BaseElementsIterator(Elements elements) {
        this.elements = elements;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns <tt>true</tt> if <tt>next</tt>
     * would return an element rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return index<elements.size();
    }

    /**
     * Returns the next element in the iteration.  Calling this method repeatedly until the {@link #hasNext()} method
     * returns false will return each element in the underlying collection exactly once.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements.
     */
    @SuppressWarnings("unchecked")
    public E next() {
        if(!hasNext()) {
            return null;
        } else {
            return (E) elements.get(index++);
        }
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
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<E> iterator() {
        return this;
    }
}
