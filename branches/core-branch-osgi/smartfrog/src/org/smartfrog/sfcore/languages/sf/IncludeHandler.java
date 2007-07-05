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

package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;

import java.util.Vector;

/**
 * This is the default include handler for the SmartFrog parser.
 */
public interface IncludeHandler {
     /**
     * Parses given include. This implementation constructs a new DefaultParser
     * on the result of openInclude and uses the AttributeList methods to
     * construct the vector of attributes
     *
     * @param include include file to parse
     * @param codebase an optional codebase where the include may be found. If null, use the default code base
     *
     * @return vector of attribute name X value pairs
     *
     * @exception Exception error while locating or parsing include
     */
    public Vector parseInclude(String include, SFReference codebase) throws Exception;
}
