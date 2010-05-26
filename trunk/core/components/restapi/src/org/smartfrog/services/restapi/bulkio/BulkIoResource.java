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
package org.smartfrog.services.restapi.bulkio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created 20-May-2010 17:08:30
 */

/**
 *  The size path entry controls how much you expect to put/get
 */
@Path("/bulkio/{size}")


/**
 * 
 */
public class BulkIoResource {

    public static final int BLOCK_SIZE = 1024;
    /**
     * There is wrongness with Java and signs of things like byte.
     */
    private static byte[] block;

    static {
        block = new byte[BLOCK_SIZE];
        for (int i=0; i<BLOCK_SIZE; i++) {
            block[i]=(byte)(i%256);
        }
    }

    @GET
    @Produces("application/octet-stream")
    public String doGet(@PathParam("size") String size) {
        
        return "undefined";
    }
    
}
