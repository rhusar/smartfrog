/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.cddlm.components;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;


/**
 * Model a liveness page. This is not quite a SmartFrog component, but rather a helper
 * component for importing into other things, as needed.
 * created 20-Apr-2004 16:11:51
 */
public class LivenessPage {
    /**
     * size to download packets
     */
    protected static final int BLOCKSIZE = 8192;
    protected String page = "/";
    protected String protocol = "http";
    protected String host = "127.0.0.1";
    protected int port = 80;
    protected URL targetURL = null;
    protected boolean followRedirects = true;

    /**
     * minumum value for a response code.
     * By default all 2XX responses are ok.
     */
    protected int minimumResponseCode = 200;

    /**
     * max response code; anything above 299 is an error. That includes not-modified
     */
    protected int maximumResponseCode = 299;

    /**
     * flag to set if you want to fetch the remote error message
     */
    protected boolean fetchErrorText = true;

    /**
     * create a new liveness page
     *
     * @param protocol
     * @param host
     * @param port
     * @param page
     * @throws RemoteException
     */
    public LivenessPage(String protocol, String host, int port, String page)
            throws RemoteException, SmartFrogDeploymentException {
        this.page = page;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        makeURL();
    }

    public LivenessPage(String url) throws MalformedURLException {
        targetURL = new URL(url);
    }

    public LivenessPage(URL url) {
        targetURL = url;
    }

    public LivenessPage() {
    }

    /**
     * bind to a url string
     *
     * @param target
     * @throws SmartFrogDeploymentException if the url generated a
     *                                      {@link MalformedURLException}
     */
    public void bindToURL(String target) throws SmartFrogDeploymentException {
        try {
            targetURL = new URL(target);
        } catch (MalformedURLException e) {
            throw new SmartFrogDeploymentException("bad URL" + target, e);
        }
    }

    protected void makeURL() throws SmartFrogDeploymentException {
        String target = protocol + "://" + host + ':' + port + '/' + page;
        bindToURL(target);
    }

    /**
     * call this after configuring the class; does any preparation and
     * turns any {@link java.net.MalformedURLException} into a {@link SmartFrogException}.
     * If the target URL is already defined, does nothing.
     *
     * @throws SmartFrogDeploymentException
     */
    public synchronized void onDeploy() throws SmartFrogDeploymentException {
        if (targetURL == null) {
            makeURL();
        }
    }

    /**
     * try and retrieve the liveness page.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *
     */
    public void onPing() throws SmartFrogLivenessException {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setInstanceFollowRedirects(followRedirects);

            //this call actually triggers sending the request
            int responseCode = connection.getResponseCode();

            if (responseCode <= 0) {
                throw new SmartFrogLivenessException("endpoint " + toString() +
                        " is not returning HTTP responses");
            }

            String response = connection.getResponseMessage();

            if ((responseCode < minimumResponseCode) ||
                    (responseCode > maximumResponseCode)) {
                throw new SmartFrogLivenessException("endpoint " + toString() +
                        " returned error " + response +
                        maybeGetErrorText(connection));
            }
        } catch (IOException exception) {
            String message = "Failed to read" + targetURL.toString() + "\n" +
                    maybeGetErrorText(connection);
            throw new SmartFrogLivenessException(message, exception);
        }
    }

    /**
     * fetch error text if configured to do so, otherwise return an empty string
     *
     * @param connection a connection that can be null if it so chooses.
     * @return "" or remote error text
     */
    protected String maybeGetErrorText(HttpURLConnection connection) {
        if (fetchErrorText && (connection != null)) {
            return getInputOrErrorText(connection);
        } else {
            return "";
        }
    }

    /**
     * this call assumes that the connection is open, we now go to get the
     * text from the connection, be it good text or error text. If something
     * goes wrong partway through a fetch, we return all that we had.
     *
     * @param connection
     * @return null if there was no input from either stream, or something
     *         went wrong with the read.
     */
    protected String getInputOrErrorText(HttpURLConnection connection) {
        InputStream instream = null;
        StringWriter text = null;

        try {
            try {
                instream = connection.getInputStream();
            } catch (IOException e) {
                instream = connection.getErrorStream();
            }

            if (instream == null) {
                return null;
            }

            text = new StringWriter(BLOCKSIZE);

            char[] buffer = new char[BLOCKSIZE];
            InputStreamReader reader = new InputStreamReader(instream);

            int length;

            while ((length = reader.read(buffer)) > 0) {
                text.write(buffer, 0, length);
            }

            reader.close();
            instream = null;

            return text.toString();
        } catch (IOException e) {
            //something failed when reading.
            if (text != null) {
                return text.toString();
            }

            return null;
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * @return a string representation of the object.
     */
    public String toString() {
        return protocol + ":" + port + "//" + host + "/" + page;
    }

    /**
     * set this to track redirects
     *
     * @param followRedirects
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean getFollowRedirects() {
        return followRedirects;
    }

    public String getPage() {
        return page;
    }

    /**
     * the page under the host to probe
     *
     * @param page
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * the protocol; should be one of http or https
     *
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * the protocol; should be one of http or https
     *
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * host to test
     *
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * host to test
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    /**
     * set the port at the destination
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    public URL getTargetURL() {
        return targetURL;
    }

    public int getMinimumResponseCode() {
        return minimumResponseCode;
    }

    public void setMinimumResponseCode(int minimumResponseCode) {
        this.minimumResponseCode = minimumResponseCode;
    }

    public int getMaximumResponseCode() {
        return maximumResponseCode;
    }

    public void setMaximumResponseCode(int maximumResponseCode) {
        this.maximumResponseCode = maximumResponseCode;
    }

    public boolean getFetchErrorText() {
        return fetchErrorText;
    }

    public void setFetchErrorText(boolean fetchErrorText) {
        this.fetchErrorText = fetchErrorText;
    }
}
