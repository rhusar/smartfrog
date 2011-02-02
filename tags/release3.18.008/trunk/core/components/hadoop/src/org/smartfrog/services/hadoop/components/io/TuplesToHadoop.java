/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.components.io;

import java.rmi.Remote;

/**
 * Created 22-Sep-2008 15:51:22
 */


public interface TuplesToHadoop extends Remote {
    String ATTR_SOURCE = "source";
    String ATTR_DEST = "dest";

    String ATTR_LINEBEGIN = "lineBegin";
    String ATTR_LINEEND = "lineEnd";
    String ATTR_SEPARATOR = "separator";
    String ATTR_QUOTEBEGIN = "quoteBegin";
    String ATTR_QUOTEEND = "quoteEnd";

    String ATTR_BUFFERSIZE = "bufferSize";
    String ATTR_REPLICATION = "replication";
    String ATTR_BLOCKSIZE = "blockSize";
    String ATTR_OVERWRITE = "overwrite";
    String ATTR_ENCODING = "encoding";
}
