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

package org.smartfrog.sfcore.languages.sf.functions;

/**
 * Defines the Date function that returns a string representation of the current
 * date and time.
 */  
public class Date extends BaseFunction {
    /**
     * Interface method. Returns the date and time.
     * Currently this is in ctime() format
     *  {@link java.util.Date#toString}; it will move to ISO8601/RFC3339
     * format with the next release
      //TODO generate RFC3339 timestamps
     * @return system date
     */
    protected Object doFunction() {
        return new java.util.Date().toString();
    }
}
