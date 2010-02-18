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

package org.smartfrog.vast.testing.networking;

/**
 * Base class for the logical clock implementation for vast.
 */
class LogicalClock {
	private int Counter = 0;

	/**
	 * A new event happened at this machine.
	 * @return The logical clock value.
	 */
	public int newEvent() {
		return ++Counter;
	}

	/**
	 * An event has been received.
	 * @param inClock The logical clock value of that event.
	 */
	public void receivedEvent(int inClock) {
		if (inClock >= Counter)
			Counter = inClock + 1;
	}

	/**
	 * Returns the current clock value without modifying it.
	 * @return The current clock value.
	 */
	public int getClock() {
		return Counter;
	}
}
