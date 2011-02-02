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
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.www.HttpAttributes;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

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

    public String format = "application/binary";

    public boolean parseResults;

    public long expectedChecksumFromGet;
    
    private Log log = LogFactory.getLog(AbstractBulkIOClient.class);

    protected AbstractBulkIOClient(Log log) {
        this.log = log;
    }

    protected AbstractBulkIOClient() {
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL createFullURL(long size, String format) throws IOException {
        validateURL();
        StringBuilder fullPath = new StringBuilder(url.getFile());
        fullPath.append('?');
        fullPath.append("size=");
        fullPath.append(Long.toString(size));
        fullPath.append("&format=");
        fullPath.append(format);
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), fullPath.toString());
    }

    public long execute() throws IOException, InterruptedException {
        if (HttpAttributes.METHOD_POST.equals(operation)
                || HttpAttributes.METHOD_PUT.equals(operation)) {
            return doUpload(operation, size);
        } else if (HttpAttributes.METHOD_GET.equals(operation)) {
            return doDownload(size);
        } else {
            throw new IOException("Unsupported operation: \"" + operation + "\"");
        }
    }

    public long doUpload(String method, long ioSize) throws IOException, InterruptedException {
        return notImplemented();
    }

    private long notImplemented() throws IOException {
        throw new IOException("Not Implemented");
    }

    public long doUpload(String method, File f) throws IOException, InterruptedException {
        return notImplemented();
    }

    public long doDownload(long ioSize) throws IOException, InterruptedException {
        return notImplemented();
    }

    public long doDownload(long ioSize, File f) throws IOException, InterruptedException {
        return notImplemented();
    }

    /**
     * Interrupt the operation
     */
    public void interrupt() {

    }

    /**
     * Validate the URL we are connecting to
     *
     * @throws IOException if there is no valid URL
     */
    protected void validateURL() throws IOException {
        if (getUrl() == null) {
            throw new IOException("No URL to connect to");
        }
    }

    /**
     * Open a connection. The connection is not yet "connected" -you can do some last minute tuning
     *
     * @return an HTTP connection.
     * @throws IOException
     */
    protected HttpURLConnection openConnection() throws IOException {
        HttpURLConnection connection;
        validateURL();
        URL targetURL = getUrl();
        return openConnection(targetURL);
    }

    /**
     * Open a connection. The connection is not yet "connected" -you can do some last minute tuning
     *
     * @return an HTTP connection.
     * @throws IOException
     */
    protected HttpURLConnection openConnection(URL targetURL) throws IOException {
        HttpURLConnection connection;
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
        return connection;
    }

    /**
     * Set chunking. <i>DO NOT DO THIS ON A GET AS the JDK CANNOT HANDLE IT</i>
     *
     * @param connection
     */
    protected void maybeSetChunking(HttpURLConnection connection) {
        if (chunked) {
            connection.setChunkedStreamingMode(chunkLength);
        }
    }

    public String getName() {
        return "IOClient";
    }

    @Override
    public String toString() {
        return getName() + " connected to \"" + getUrl() + "\"; Operation is " + operation;
    }

    /**
     * Close quietly, log on an exception, do nothing on null
     *
     * @param c thing to close
     */
    protected void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                log.warn(e);
            }
        }
    }

    /**
     * Get a long property value
     * @param props property instance
     * @param key key
     * @return the long value
     * @throws IOException if the 
     */
    long getLongPropValue(Properties props, String key) throws IOException {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IOException("Failed to find property key " + key);
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to parse property "+ key + " = \"" + value + "\": " + e, e);
        }
    }
}
