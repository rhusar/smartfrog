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

package org.smartfrog.sfcore.languages.cdl.utils;

import nu.xom.Element;
import nu.xom.Elements;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * make it easier to iterate over elements in the Java1.5 world
 */
public class ElementsIterator implements Iterator<Element>,Iterable<Element> {

    Elements elements;
    int index=0;
    int size=0;

    /**
     * Turn an Elements structure into an iterable
     * @param elements
     */
    public ElementsIterator(Elements elements) {
        this.elements = elements;
        size=elements.size();
    }

    /**
     * Iterate over all children
     * @param parent parent node
     */
    public ElementsIterator(Element parent) {
        this(parent.getChildElements());
    }

    /**
     * Iterate over all child elements in the given name/namespace 
     * @param parent
     * @param localPart
     * @param namespace
     */
    public ElementsIterator(Element parent,String localPart,String namespace) {
        this(parent.getChildElements(localPart,namespace));
    }

    /**
     * Iterate over all local child elements of the given name
     * @param parent parent node
     * @param localPart element name to look for
     */
    public ElementsIterator(Element parent,
                            String localPart) {
        this(parent,localPart, "");
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return index<size;
    }

    /**
     * Returns the next element in the iteration.  Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will return
     * each element in the underlying collection exactly once.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    public Element next() {
        if(index>=size) {
            throw new NoSuchElementException();
        }
        Element next=elements.get(index);
        ++index;
        return next;
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if the
     * underlying collection is modified while the iteration is in progress in
     * any way other than by calling this method.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
     *                                       not supported by this Iterator.
     * @throws IllegalStateException         if the <tt>next</tt> method has not
     *                                       yet been called, or the <tt>remove</tt>
     *                                       method has already been called
     *                                       after the last call to the
     *                                       <tt>next</tt> method.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Element> iterator() {
        return this;
    }

}
