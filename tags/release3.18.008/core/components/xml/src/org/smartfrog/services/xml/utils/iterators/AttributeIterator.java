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

import nu.xom.Attribute;
import nu.xom.Element;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An attribute iterator created 16-May-2005 17:25:15
 */

public class AttributeIterator implements Iterator<Attribute>,
        Iterable<Attribute> {

    private Element element;
    private int index=0;
    private Attribute currentAttribute;

    public AttributeIterator(Element element) {
        this.element = element;
    }

    public boolean hasNext() {
        return index<element.getAttributeCount();
    }

    public Attribute next() {
        Attribute attribute = getCurrent();
        index++;
        return attribute;
    }

    /**
     * Get the current element
     * @return the current attribute
     * @throws NoSuchElementException if there isnt one
     */
    private Attribute getCurrent() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Attribute attribute = element.getAttribute(index);
        return attribute;
    }

    /**
     * remove the current attribute. 
     *
     * @throws NoSuchElementException if there isnt one
     */
    public void remove() {
        Attribute attr=getCurrent();
        element.removeAttribute(attr);
    }


    /**
     * Return a new iterator over the element
     * @return a new iterator over the element
     */
    public Iterator<Attribute> iterator() {
        return new AttributeIterator(element);
    }
}
