package org.smartfrog.sfcore.common;

import java.util.Iterator;

public class ReadOnlyIterator<V> implements Iterator<V> {
	private Iterator<V> it;
	
	public ReadOnlyIterator(final Iterator<V> it) {
		this.it = it;
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public V next() {
		return it.next();
	}
	
}
