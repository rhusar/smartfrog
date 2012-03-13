/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.exceptions;


import org.smartfrog.sfcore.common.LocalSmartFrogDescriptor;

import java.io.IOException;

/**
 * Created 04-Aug-2009 17:31:30
 */

public class ParseFailedException extends IOException {

    public LocalSmartFrogDescriptor descriptor;

    /**
     * Constructs an {@code IOException} with the specified detail message.
     *
     * @param message    The detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param descriptor the failing descriptor, including all errors
     */
    public ParseFailedException(String message, LocalSmartFrogDescriptor descriptor) {
        super(message);
        this.descriptor = descriptor;
    }
}
