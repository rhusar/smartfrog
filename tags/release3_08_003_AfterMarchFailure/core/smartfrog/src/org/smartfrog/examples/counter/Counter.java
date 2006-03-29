/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.examples.counter;

/**
 * Defines the attributes for counter component.
 */
public interface Counter {
    /** String name for optional attribute "debug". Value {@value}. */
    final static String ATR_DEBUG = "debug";
    /** String name for optional attribute "sleep". Value {@value}. */
    final static String ATR_PAUSE = "pause";
    /** String name for optional attribute "counter". Value {@value}. */
    final static String ATR_COUNTER = "counter";
    /** String name for optional attribute "message". Value {@value}. */
    final static String ATR_MESSAGE = "message";
    /** String name for mandatory attribute "limit". Value {@value}.*/
    final static String ATR_LIMIT = "limit";

    /**
     * string name for optional attribute, sleep. Value {@value}.
     */
    final static String ATR_SLEEP = "sleep";
    /**
      * string name for optional attribute, terminate. Value {@value}.
     */
    final static String ATR_TERMINATE = "terminate";
}
