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

import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

import java.io.Reader;

/**
 * Defines the actual low-level parser interface. Objects that implement this
 * interface provide the means to parse SmartFrog text into component
 * descriptions.
 *
 */
public interface ReaderParser {
    /**
     * Parses component(s) from a reader. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param reader the reader to parse and compile from
     *
     * @return root component containing parsed component(s)
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException error parsing stream
     */
    public Phases sfParse(Reader reader) throws SmartFrogCompilationException;

    /**
     * Parses component(s) from a reader. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param reader the reader to parse and compile from
     * @param codebase an optional codebase where the include may be found. If null, use the default code base
     *
     * @return root component containing parsed component(s)
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException error parsing stream
     */
    public Phases sfParse(Reader reader, String codebase) throws SmartFrogCompilationException;

    /**
     * Parses a reference from a reader. Used by components and
     * developers to quickly build references from a string (eg. sfResolve in
     * Prim)
     *
     * @param reader the reader to parse and compile from
     *
     * @return parsed reference
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException failed to parse reference
     */
    public Reference sfParseReference(Reader reader) throws SmartFrogCompilationException;

    /**
     * Parses any value from a reader. (the meaning of "any" is language dependant)
     *
     * @param reader the reader to parse and compile from
     *
     * @return parsed value
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException failed to parse any value
     */
    public Object sfParseAnyValue(Reader reader) throws SmartFrogCompilationException;

    /**
     * Parses a primitive value from a reader. (the meaning of primitive is language dependant)
     *
     * @param reader the reader to parse and compile from
     *
     * @return parsed value
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException failed to parse primitive value
     */
    public Object sfParsePrimitiveValue(Reader reader) throws SmartFrogCompilationException;

    /**
     * Parses tags from a reader.
     *
     * @param reader the reader to parse and compile from
     *
     * @return parsed value
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException failed to parse tags
     */
    public Object sfParseTags(Reader reader) throws SmartFrogCompilationException;
}
