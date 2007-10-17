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

import java.io.InputStream;

import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;


/**
 * Defines the actual low-level parser interface. Objects that implement this
 * interface provide the means to parse SmartFrog text into component
 * descriptions.
 *
 */
public interface StreamParser {
    /**
     * Parses component(s) from an input stream. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param is input stream to parse and compile from
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing stream
     */
    public Phases sfParse(InputStream is) throws SmartFrogParseException;

    /**
     * Parses component(s) from an input stream. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param is input stream to parse and compile from
     * @param codebase an optional codebase where the include may be found. If null, use the default code base
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing stream
     */
    public Phases sfParse(InputStream is, String codebase) throws SmartFrogParseException;

    /**
     * Parses a reference from an input stream. Used by components and
     * developers to quickly build references from a string (eg. sfResolve in
     * Prim)
     *
     * @param is input stream to parse for a reference
     *
     * @return parsed reference
     *
     * @exception SmartFrogCompilationException failed to parse reference
     */
    public Reference sfParseReference(InputStream is) throws SmartFrogCompilationException;

    /**
     * Parses any value from an input stream. (the meaning of "any" is language dependant)
     *
     * @param is input stream to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse any value
     */
    public Object sfParseAnyValue(InputStream is) throws SmartFrogCompilationException;

    /**
     * Parses a primitive value from an input stream. (the meaning of primitive is language dependant)
     *
     * @param is input stream to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParsePrimitiveValue(InputStream is) throws SmartFrogCompilationException;

    /**
     * Parses tags from an input stream.
     *
     * @param is string to parse for a value
     *
     * @return parsed value
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogParseException failed to parse tags
     */
    public Object sfParseTags(InputStream is) throws SmartFrogCompilationException;

    /**
     * Parses a component description from an input stream.
     * All the language phases will have been applied, and the conversion to ComponentDescription
     * carried out.
     *
     * @param is input stream to parse for a value
     *
     * @return parsed component description
     *
     * @exception SmartFrogParseException failed to parse primitive value
     */
    public ComponentDescription sfParseComponentDescription(InputStream is) throws SmartFrogCompilationException;
}
