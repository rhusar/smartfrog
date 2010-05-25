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
import org.smartfrog.services.www.HttpHeaders;
import org.smartfrog.services.www.LivenessPageChecker;
import org.smartfrog.services.www.bulkio.IoAttributes;
import org.smartfrog.services.www.bulkio.server.AbstractBulkioServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.zip.CRC32;

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
    public long doUpload(String method, long ioSize) throws IOException, InterruptedException {
        validateURL();
        CRC32 checksum = new CRC32();
        URL targetUrl = getUrl();
        getLog().info("Uploading " + ioSize + " bytes to " + targetUrl);
        HttpURLConnection connection = openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, Long.toString(ioSize));
        connection.setDoOutput(true);
        maybeSetChunking(connection);
        connection.connect();
        OutputStream stream = connection.getOutputStream();
        long bytes = 0;
        try {
            for (bytes = 0; bytes < ioSize; bytes++) {
                int octet = (AbstractBulkioServlet.getByteFromCounter(bytes));
                stream.write(octet);
                checksum.update(octet);
                if (interrupted) {
                    throw new InterruptedException("Interrupted after sending " + bytes + " bytes");
                }
            }
        } finally {
            closeQuietly(stream);
        }
        getLog().info("Upload complete, checking results");
        checkStatusCode(targetUrl, connection, HttpURLConnection.HTTP_OK);
        long expectedChecksum = checksum.getValue();
        getLog().info("Uploaded " + bytes + " bytes to " + targetUrl + " checksum=" + expectedChecksum);
        if (bytes != ioSize) {
            throw new IOException("Wrong content length uploaded from "
                    + " - put " + ioSize + " but got " + bytes);
        }
        if (parseResults) {
            InputStream inStream = null;
            Properties props = new Properties();
            try {
                inStream = connection.getInputStream();
                props.load(inStream);
            } finally {
                closeQuietly(inStream);
            }

            long actualChecksum = getLongPropValue(props, IoAttributes.CHECKSUM);
            if (actualChecksum != expectedChecksum) {
                throw new IOException("Expected checksum from upload of " + ioSize + " bytes "
                        + "was " + expectedChecksum + " but got " + actualChecksum
                        + "\n Properties: " + props.toString());
            }

        }
        return ioSize;
    }

    /*
    
        @Override
        public long doUpload(File f) throws IOException, InterruptedException {
            return 0;
        }
    
    
    */

    @Override
    public long doDownload(long ioSize) throws IOException, InterruptedException {
        validateURL();
        URL target = createFullURL(ioSize, format);
        getLog().info("Downloading " + ioSize + " bytes from " + target);
        CRC32 checksum = new CRC32();
        HttpURLConnection connection = openConnection(target);
        connection.setRequestMethod(HttpAttributes.METHOD_GET);
        connection.setDoOutput(false);
        connection.connect();
        checkStatusCode(target, connection, HttpURLConnection.HTTP_OK);
        String contentLengthHeader = connection.getHeaderField(HttpHeaders.CONTENT_LENGTH);
        long contentLength = Long.parseLong(contentLengthHeader);
        if (contentLength != ioSize) {
            throw new IOException("Wrong content length returned from " + target
                    + " - expected " + ioSize + " but got " + contentLength);
        }
        String formatHeader = connection.getHeaderField(HttpHeaders.CONTENT_TYPE);
        if (!format.equals(formatHeader)) {
            throw new IOException("Wrong content type returned from " + target
                    + " - expected " + format + " but got " + formatHeader);
        }
        InputStream stream = connection.getInputStream();
        long bytes = 0;
        try {
            for (bytes = 0; bytes < ioSize; bytes++) {
                int octet = stream.read();
                checksum.update(octet);
                if (interrupted) {
                    throw new InterruptedException("Interrupted after reading" + bytes + " bytes");
                }
            }
        } finally {
            closeQuietly(stream);
        }
        long actualChecksum = checksum.getValue();
        getLog().info("Download finished after " + bytes + " bytes, checksum=" + actualChecksum);
        if (bytes != ioSize) {
            throw new IOException("Wrong content length downloaded from " + target
                    + " - requested " + ioSize + " but got " + bytes);
        }
        if (expectedChecksumFromGet >= 0 && expectedChecksumFromGet != actualChecksum) {
            throw new IOException("Expected checksum from download of " + ioSize + " bytes "
                    + "was " + expectedChecksumFromGet + " but got " + actualChecksum);
        }
        return bytes;
    }

    private void checkStatusCode(URL target, HttpURLConnection connection, int expectedStatus) throws IOException {
        int status = connection.getResponseCode();
        if (status != expectedStatus) {
            String errorText = LivenessPageChecker.getInputOrErrorText(connection);
            throw new IOException("Wrong status code " + status + " from " + target + ":\n" + errorText);
        }
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
