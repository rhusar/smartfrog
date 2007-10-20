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
package org.smartfrog.sfcore.languages.cdl;

import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.StreamParser;
import org.smartfrog.sfcore.parser.StreamLanguageParser;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.InputStream;
import java.io.Reader;

/**
 * This is a parser for CDL XML streams. The base SFParser class contains some
 * assumptions about package names That force the name of this class. DO NOT
 * RENAME. created 18-Apr-2005 13:50:53
 *
 * @see org.smartfrog.sfcore.parser.SFParser
 * @see org.smartfrog.sfcore.languages.sf.SFParser for the reference example
 */

public class SFParser implements StreamLanguageParser {
    private static final String NOT_IMPLEMENTED = "Not implemented";

    /**
     * Parses component(s) from an input stream. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param is input stream to parse and compile from
     * @return root component containing parsed component(s)
     * @throws SmartFrogParseException error parsing stream
     */
    public Phases sfParse(InputStream is) throws SmartFrogParseException {
        try {

            //TODO: use the smartfrog classloader
            ResourceLoader loader = new ResourceLoader(this.getClass());
            //first, parse the CDL into smartfrog

            ParseContext parseContext = new ParseContext(null,loader);
            CdlParser parser = parseContext.createParser();
            CdlDocument cdlDocument = parser.parseStream(is);
            //here it is loaded; create the dom
            cdlDocument.parse(parseContext);

            //create the CD graph
            CdlComponentDescription graph = cdlDocument.convertToComponentDescription();

            return graph;

        } catch (Throwable thrown) {
            throw (SmartFrogParseException) SmartFrogParseException.forward(
                    thrown);
        }


    }

    /**
     * Parses component(s) from an input stream. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param is       input stream to parse and compile from
     * @param codebase an optional codebase where the include may be found. If
     *                 null, use the default code base
     * @return root component containing parsed component(s)
     * @throws org.smartfrog.sfcore.common.SmartFrogParseException
     *          error parsing stream
     */
    public Phases sfParse(InputStream is, String codebase)
            throws SmartFrogParseException {
        return sfParse(is);
    }



    /**
     * Parses a reference from an input stream. Used by components and
     * developers to quickly build references from a string (eg. sfResolve in
     * Prim)
     *
     * @param is input stream to parse for a reference
     * @return parsed reference
     * @throws org.smartfrog.sfcore.common.SmartFrogParseException
     *          failed to parse reference
     */
    public ReferencePhases sfParseReference(InputStream is) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    /**
     * Parses any value from an input stream. (the meaning of "any" is language
     * dependant)
     *
     * @param is input stream to parse for a value
     * @return parsed value
     * @throws SmartFrogParseException failed to parse any value
     */
    public Object sfParseAnyValue(InputStream is)
            throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    /**
     * Parses a primitive value from an input stream. (the meaning of primitive
     * is language dependant)
     *
     * @param is input stream to parse for a value
     * @return parsed value
     * @throws SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParsePrimitiveValue(InputStream is)
            throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }


    protected void notImplemented() throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }


    public Phases sfParse(Reader reader) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    public Phases sfParse(Reader reader, String codebase) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    public ReferencePhases sfParseReference(Reader reader) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    public Object sfParseAnyValue(Reader reader) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    public Object sfParsePrimitiveValue(Reader reader) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }

    public Object sfParseTags(Reader reader) throws SmartFrogParseException {
        //TODO
        throw new SmartFrogParseException(NOT_IMPLEMENTED);
    }
}
