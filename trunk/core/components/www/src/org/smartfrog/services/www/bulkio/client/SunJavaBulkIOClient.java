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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * This class uses the Sun Java APIs to do bulk upload.
 */

public class SunJavaBulkIOClient extends AbstractBulkIOClient {

    private volatile boolean interrupted;


    @Override
    public long doUpload(long size) throws IOException, InterruptedException {
        HttpURLConnection connection = openConnection();
        connection.setRequestProperty(HttpAttributes.CONTENT_LENGTH, Long.toString(size));
        OutputStream outputStream = connection.getOutputStream();
        for (long bytes = 0; bytes < size; bytes++) {
            outputStream.write(32);
            if (interrupted) {
                throw new InterruptedException("Interrupted after sending " + bytes + " bytes");
            }
        }
        return size;
    }
/*

    @Override
    public long doUpload(File f) throws IOException, InterruptedException {
        return 0;
    }


*/

    @Override
    public void interrupt() {
        interrupted = true;
    }
}
