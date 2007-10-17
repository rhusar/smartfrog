/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
import java.util.List;

/**
 * An iterator to walk backwards through a list
 * @author Steve Loughran
 *
 */
public class ReverseListIterator<Type> implements Iterator<Type>, Iterable<Type> {

	private List<Type> list;
	private int index;


    /**
	 * @param list the list to reverse over
	 */
	public ReverseListIterator(List<Type> list) {
		this.list = list;
        index= list.size() -1;
	}

	public boolean hasNext() {
		return index>=0;
	}

	public Type next() {
		Type t=list.get(index);
		index--;
		return t;
	}

	public void remove() {
		list.remove(index);
	}

    /**
     * When asked for an iterator, we return a new reverse list iterator.
     * This lets us act as a factory for ourselves in Java 5 lists
     * @return a new reverse list iterator
     */
    public ReverseListIterator<Type> iterator() {
        return new ReverseListIterator<Type>(list);
    }

}
