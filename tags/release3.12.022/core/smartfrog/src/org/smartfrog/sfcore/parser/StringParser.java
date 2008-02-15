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
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Defines the main parser interface. Adds the ability to parse strings as well
 * as streams.
 *
 */
public interface StringParser {
    /**
     * Parses component(s) from a string. Returns the root component. This is a
     * utility access method which currently does not support localization.
     *
     * @param str string to parse
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing string
     */
    public Phases sfParse(String str) throws SmartFrogParseException;

    /**
     * Parses component(s) from an resource url. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param url url to resource to parse and compile from
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing stream
     */
     public Phases sfParseResource(String url) throws SmartFrogParseException;


    /**
     * Parses a reference from a string. Used by components and developers to
     * quickly build references from a string (eg. sfResolve in Prim)
     *
     * @param txt textual representation of the reference
     *
     * @return parsed reference
     *
     * @exception SmartFrogParseException failed to parse reference
     */
    public Reference sfParseReference(String txt) throws SmartFrogCompilationException;

    /**
     * Parses any value from a string. (the meaning of "any" is language dependant)
     *
     * @param txt string to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse any value
     */
    public Object sfParseAnyValue(String txt) throws SmartFrogCompilationException;

    /**
     * Parses a primitive value from a string. (the meaning of primitive is language dependant)
     *
     * @param txt string to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParsePrimitiveValue(String txt) throws SmartFrogCompilationException;

    /**
     * Parses a component description from a string.
     * All the langauge phases will have been applied, and the conversion to ComponentDescription
     * carried out.
     *
     * @param txt input to parse for a value
     *
     * @return parsed component description
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
     public ComponentDescription sfParseComponentDescription(String txt) throws SmartFrogCompilationException;

    /**
     * Parses tags from a string.
     *
     * @param txt string to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParseTags(String txt) throws SmartFrogCompilationException;

}
