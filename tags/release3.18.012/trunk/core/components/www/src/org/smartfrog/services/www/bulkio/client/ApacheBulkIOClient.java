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

import org.apache.commons.logging.Log;

/**
 * Created 17-May-2010 16:46:34
 */

public class ApacheBulkIOClient extends AbstractBulkIOClient {
    
    public ApacheBulkIOClient(Log log) {
        super(log);
    }

    public ApacheBulkIOClient() {
    }

    /*
    
        @Override
        public long doUpload(long size) throws IOException, InterruptedException {
            return 0;
        }
    
        @Override
        public long doUpload(File f) throws IOException, InterruptedException {
            return 0;
        }
    
        @Override
        public long doDownload(long size) throws IOException, InterruptedException {
            return 0;
        }
    
        @Override
        public long doDownload(long size, File f) throws IOException, InterruptedException {
            return 0;
        }
    */

    @Override
    public void interrupt() {

    }

    @Override
    public String getName() {
        return "IOClient using Apache Httpclient library";
    }

}
