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
import org.smartfrog.services.www.HttpAttributes;
import org.smartfrog.services.www.LivenessPageChecker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class uses the Sun Java APIs to do bulk upload.
 */

public class SunJavaBulkIOClient extends AbstractBulkIOClient {

    private volatile boolean interrupted;

    public SunJavaBulkIOClient(Log log) {
        super(log);
    }

    public SunJavaBulkIOClient() {
    }

    @Override
    public long doUpload(long size) throws IOException, InterruptedException {
        validateURL();
        getLog().info("Uploading " + size + " bytes to " + getUrl());
        HttpURLConnection connection = openConnection();
        connection.setRequestProperty(HttpAttributes.HEADER_CONTENT_LENGTH, Long.toString(size));
        connection.setDoOutput(true);
        connection.connect();
        OutputStream stream = connection.getOutputStream();
        try {
            for (long bytes = 0; bytes < size; bytes++) {
                stream.write(32);
                if (interrupted) {
                    throw new InterruptedException("Interrupted after sending " + bytes + " bytes");
                }
            }
        } finally {
            closeQuietly(stream);
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
    public long doDownload(long size) throws IOException, InterruptedException {
        validateURL();
        URL target = createFullURL(size, format);
        getLog().info("Downloading " + size + " bytes from " + target);
        HttpURLConnection connection = openConnection(target);
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            String errorText = LivenessPageChecker.getInputOrErrorText(connection);
            throw new IOException("Wrong status code " + status + " from " + target + ":\n" + errorText);
        }
        String contentLengthHeader = connection.getHeaderField(HttpAttributes.HEADER_CONTENT_LENGTH);
        long contentLength = Long.parseLong(contentLengthHeader);
        if (contentLength != size) {
            throw new IOException("Wrong content length returned from " + target
                    + " - expected " + size + " but got " + contentLength);
        }
        String formatHeader = connection.getHeaderField(HttpAttributes.HEADER_CONTENT_TYPE);
        if (!format.equals(formatHeader)) {
            throw new IOException("Wrong content type returned from " + target
                    + " - expected " + format + " but got " + formatHeader);
        }
        InputStream stream = connection.getInputStream();
        long bytes = 0;
        try {
            for (bytes = 0; bytes < size; bytes++) {
                stream.read();
                if (interrupted) {
                    throw new InterruptedException("Interrupted after reading" + bytes + " bytes");
                }
            }
        } finally {
            getLog().info("Download finished after " + bytes + " bytes ");
            closeQuietly(stream);
        }
        if (bytes != size) {
            throw new IOException("Wrong content length downloaded from " + target
                    + " - requested " + size + " but got " + bytes);
        }
        return bytes;
    }

    @Override
    public void interrupt() {
        interrupted = true;
    }

    @Override
    public String getName() {
        return "IOClient using Sun API";
    }
}
