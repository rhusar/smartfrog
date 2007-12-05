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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.reference.Reference;

import java.io.Writer;

/**
 * Defines the actual low-level unparser interface. Objects that implement this
 * interface provide the means to unparse SmartFrog component
 * descriptions into text.
 *
 */
public interface WriterUnparser {
    /**
     * Unparses component(s) to a writer.
     *
     * @param w writer to output the description
     * @param data the description to write
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogParseException error unparsing stream
     */
    public void sfUnparse(Writer w, ComponentDescription data) throws SmartFrogParseException;

    /**
     * Unparses a reference to a writer.
     *
     * @param w writer to output the description
     * @param ref the reference to write
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogCompilationException failed to parse reference
     */
    public void sfUnparseReference(Writer w, Reference ref) throws SmartFrogCompilationException;

    /**
     * Unparses any value to a writer
     *
     * @param w writer to output the description
     * @param value the value to write
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogParseException failed to parse any value
     */
    public void sfUnparseValue(Writer w, Object value) throws SmartFrogCompilationException;
}
