/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.bulkio.client;

import org.smartfrog.services.www.HttpAttributes;


/**
 * Created 18-May-2010 12:08:38
 */

public interface BulkIOClient extends HttpAttributes {

    String ATTR_USE_FORM_UPLOAD = "useFormUpload";
    String ATTR_CHUNKED = "chunked";
    String ATTR_CHUNK_LENGTH = "chunkLength";
    String ATTR_IOCLASS = "ioClass";
    String ATTR_SIZE = "size";
    String ATTR_OPERATION = "operation";
    //String ATTR_ = "";
}
