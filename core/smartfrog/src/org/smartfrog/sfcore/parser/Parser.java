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

package org.smartfrog.sfcore.parser;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;


/**
 * Defines the main parser interface. Adds the ability to parse strings as well
 * as streams.
 *
 */
public interface Parser extends StreamParser {
    /**
     * Parses component(s) from a string. Returns the root component. This is a
     * utility access method which currently does not support localization.
     *
     * @param str string to parse
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogException error parsing string
     */
    public Phases sfParse(String str) throws SmartFrogException;

    /**
     * Parses a reference from a string. Used by components and developers to
     * quickly build references from a string (eg. sfResolve in Prim)
     *
     * @param txt textual representation of the reference
     *
     * @return parsed reference
     *
     * @exception SmartFrogException failed to parse reference
     */
    public Reference sfParseReference(String txt) throws SmartFrogException;
}
