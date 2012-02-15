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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;

import org.smartfrog.sfcore.security.SFClassLoader;


/**
 * This is the null include handler for the SmartFrog parser. This simply
 * ignores the include file and returns an empty set of attributes
 *
 */
public class NullIncludeHandler implements IncludeHandler {

    String baseCodebase;

    /**
     * Constructor.
     */
    public NullIncludeHandler() {
        baseCodebase = null;
    }

    /**
     * Constructor.
     *
     * @param baseCodebase the codebase for this include handler to which will be appended the codebase passed in the
     * parseIncldue method.
     */
    public NullIncludeHandler(String baseCodebase) {
        this.baseCodebase = baseCodebase;
    }

    /**
     * Doesn't parse the given include. 
     *
     * @param include include file to not parse
     * @param codebase an optional codebase where the include may be found. If null, use the default codebase
     *
     * @return empty vector 
     *
     * @exception Exception should never occur!
     */
    public Vector parseInclude(String include, String codebase) throws Exception {
        return new Vector();
    }

    /**
     * Doesn't parse the given include.
     *
     * @param include include file to not parse
     * @param codebase an optional codebase where the include may be found. If null, use the default codebase
     * @param optional a boolean that states whether it is an error for the include file not to exist (false = not optional)
     *
     * @return empty vector
     *
     * @exception Exception should never occur!
     */
    public Vector parseInclude(String include, String codebase, boolean optional) throws Exception {
        return new Vector();
    }

}
