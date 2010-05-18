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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created 17-May-2010 17:35:44
 */

public abstract class AbstractBulkIOClient {

    public URL url;

    public long size;

    public String operation = HttpAttributes.METHOD_POST;

    public boolean useFormUpload = false;

    public int connectTimeout = -1;

    public boolean chunked = false;

    public int chunkLength = 8192;


    protected AbstractBulkIOClient() {
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public long execute() throws IOException, InterruptedException {
        if ("POST".equals(operation)
                || "PUT".equals(operation)) {
            return doUpload(size);
        } else if ("GET".equals(operation)) {
            return doDownload(size);
        } else {
            throw new IOException("Unsupported operation: \"" + operation + "\"");
        }
    }

    public long doUpload(long size) throws IOException, InterruptedException {
        return notImplemented();
    }

    private long notImplemented() throws IOException {
        throw new IOException("Not Implemented");
    }

    public long doUpload(File f) throws IOException, InterruptedException {
        return notImplemented();
    }

    public long doDownload(long size) throws IOException, InterruptedException {
        return notImplemented();
    }

    public long doDownload(long size, File f) throws IOException, InterruptedException {
        return notImplemented();
    }

    /**
     * Interrupt the operation
     */
    public void interrupt() {

    }

    /**
     * Open a connection. The connection is not yet "connected" -you can do some last minute tuning
     *
     * @return an HTTP connection.
     * @throws IOException
     */
    protected HttpURLConnection openConnection() throws IOException {
        HttpURLConnection connection;
        URL targetURL = getUrl();
        if (targetURL == null) {
            throw new IOException("No URL to connect to");
        }
        URLConnection rawConnection = targetURL.openConnection();
        if (!(rawConnection instanceof HttpURLConnection)) {
            throw new IOException("Could not open an HTTP connection to " + targetURL);
        }
        connection = (HttpURLConnection) rawConnection;
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(operation);
        if (connectTimeout >= 0) {
            connection.setConnectTimeout(connectTimeout);
        }
        if (chunked) {
            connection.setChunkedStreamingMode(chunkLength);
        }

        return connection;
    }
}
