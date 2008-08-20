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

package org.smartfrog.sfcore.languages.sf;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;

import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.SFLoader;


/**
 * This is the default include handler for the SmartFrog parser. This simply
 * creates a new parser and returns an AttributeList. The include name is
 * located using SFSystem.stringToURl which will try to use the name as a URL,
 * or make a file URL to locate it. Otherwise the classloader's
 * getResourceAsStream is used to locate the include. Subclasses can override
 * this to do more sophisticated include storage. The format of an
 * AttributeList as returned by DefaultParser is a vector of Object[] with
 * element 0 the name of the attribute and element 1 the value.
 *
 */
public class DefaultIncludeHandler implements IncludeHandler {

    String baseCodebase;

    /**
     * Constructor.
     */
    public DefaultIncludeHandler() {
        baseCodebase = null;
    }

    /**
     * Constructor.
     *
     * @param baseCodebase the codebase for this include handler to which will be appended the codebase passed in the
     * parseIncldue method.
     * @deprecated
     */
    public DefaultIncludeHandler(String baseCodebase) {
        this.baseCodebase = baseCodebase;
    }

    /**
     * Parses given include. This implementation constructs a new DefaultParser
     * on the result of openInclude and uses the AttributeList methods to
     * construct the vector of attributes
     *
     * @param include include file to parse
     * @param from an optional from where the include may be found. If null, use the default from
     *
     * @return vector of attribute name X value pairs
     *
     * @exception Exception error while locating or parsing include
     */
    public Vector parseInclude(String include, SFReference from) throws Exception {
        return (
            new DefaultParser(
                org.smartfrog.sfcore.parser.SFParser.openInclude(include, from),
                new DefaultIncludeHandler()
            )
        ).AttributeList();
    }

    /**
     * Locate the include and returns an input stream on it. This uses
     * SFSystem.stringToURL to check whether include is a URL or a file. On
     * failure it tries to use standard getResourceAsStream to get the inlude
     * of the classpath. Subclasses can override to provide additional means
     * of locating includes.
     *
     * @param include include to locate
     * @param codebase an optional codebase where hte include may be found. If null, use the default code base
     *
     * @return Reader (UTF-8) on located include
     *
     * @exception Exception failed to locate or open include
     */
    protected Reader openInclude(String include, String codebase) throws Exception {
        InputStream is = null;

        //is = SFClassLoader.getResourceAsStream(include, actualCodebase(codebase), true);
        //@todo review
		is = SFLoader.getInputStreamSFException(include);

        if (is == null) {
            throw new Exception("Include file: " + include + " not found");
        }

        return new InputStreamReader(is, "utf-8");
    }

    /**
     * build a concatenated codebase from the base codebase and the codebase passed as a parameter

     *
     *  @param codebase the codeebase to concatenate to the base. May be null.
     * @deprecated
     */
    protected String actualCodebase(String codebase) {
        String actualCodebase = null;
        if ((baseCodebase != null) && (codebase != null)) actualCodebase = baseCodebase + " " + codebase;
        else if (baseCodebase != null) actualCodebase = baseCodebase;
        else if (codebase != null) actualCodebase = codebase;
        return actualCodebase;
    }
}
