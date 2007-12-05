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
package org.smartfrog.services.xml.utils.iterators;

import nu.xom.Node;
import nu.xom.ParentNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A XOM node iterator. Valid for all, E, where E is derived from Node.
 * <p/>
 * There is a devious little hack here, where when you invoke us, you get a new
 * instance back. This for java1.5 foreach integration.
 * <p/>
 * created 16-May-2005 17:17:59
 */

public class BaseNodeIterator <E extends Node> implements Iterator<E>,
        Iterable<E> {

    LinkedList l;
    ParentNode parent;

    int index = 0;

    public BaseNodeIterator(ParentNode parent) {
        this.parent = parent;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return index < parent.getChildCount();
    }

    /**
     * Returns the next element in the iteration.  Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will return
     * each element in the underlying collection exactly once.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements.
     */
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        //bit of casting abuse here.
        return (E) (Object) parent.getChild(index++);
    }

    /**
     * Optional remove operation.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * iterator operator returns a new iterator over us.
     *
     * @return the iterator
     */
    public Iterator<E> iterator() {
        return new BaseNodeIterator<E>(parent);
    }

}
