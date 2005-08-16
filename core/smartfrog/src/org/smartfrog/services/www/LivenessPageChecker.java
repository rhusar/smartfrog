/**
 * (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP This library
 * is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any
 * later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA For more information: www.smartfrog.org
 */
package org.smartfrog.services.www;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


/**
 * Model a liveness page. This is not quite a SmartFrog component, but rather a
 * helper component for importing into other things, as needed. created
 * 20-Apr-2004 16:11:51
 */
public class LivenessPageChecker implements LivenessPage {
    /**
     * who owns us
     */
    private Prim owner;

    /**
     * size to download packets
     */
    protected static final int BLOCKSIZE = 8192;

    /** 
     */
    protected String page = "/";

    /** 
     */
    protected String protocol = "http";

    /** 
     */
    protected String host = "127.0.0.1";

    /** 
     */
    protected int port = 80;

    /** 
     */
    protected URL targetURL = null;

    /** 
     */
    protected boolean followRedirects = true;

    /** 
     */
    protected boolean enabled = true;

    /**
     * minumum value for a response code. By default all 2XX responses are ok.
     */
    protected int minimumResponseCode = 200;

    /**
     * max response code; anything above 299 is an error. That includes
     * not-modified
     */
    protected int maximumResponseCode = 299;

    /**
     * flag to set if you want to fetch the remote error message
     */
    protected boolean fetchErrorText = true;

    /**
     * string of queries at the end of the url
     */
    protected String queries=null;

    /**
     * our log
     */
    protected Log log;

    protected HashMap mimeTypeMap;

    /**
     * create a new liveness page
     *
     * @param protocol
     * @param host
     * @param port
     * @param page
     * @throws RemoteException
     */
    public LivenessPageChecker(Prim owner, String protocol, String host, int port,
                               String page) throws RemoteException, SmartFrogDeploymentException {
        this.page = page;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.owner = owner;
        makeURL();
    }

    /**
     * Creates a new LivenessPageChecker object.
     *
     * @param url A URL to check
     * @throws MalformedURLException if the URL is invalid
     */
    public LivenessPageChecker(Prim owner, String url) throws MalformedURLException, SmartFrogLogException {
        bind(owner);
        targetURL = new URL(url);
    }

    /**
     * Creates a new LivenessPageChecker object.
     *
     * @param url A URL to check
     */
    public LivenessPageChecker(Prim owner, URL url) throws SmartFrogLogException {
        bind(owner);
        targetURL = url;
    }

    /**
     * Creates a new LivenessPageChecker object.
     */
    public LivenessPageChecker(Prim owner) throws SmartFrogLogException {
        bind(owner);
    }

    /**
     * bind to the owner, includes log setup
     *
     * @param owner
     */
    private void bind(Prim owner) throws SmartFrogLogException {
        this.owner = owner;
        log = LogFactory.getLog(owner);
    }


    /**
     * bind to a url string
     *
     * @param target
     * @throws SmartFrogDeploymentException if the url generated a {@link
     *                                      MalformedURLException}
     */
    public void bindToURL(String target) throws SmartFrogDeploymentException {
        try {
            targetURL = new URL(target);
        } catch (MalformedURLException e) {
            throw new SmartFrogDeploymentException("bad URL" + target, e);
        }
    }

    /**
     * make a URL from the various things
     *
     * @throws SmartFrogDeploymentException DOCUMENT ME!
     */
    protected void makeURL() throws SmartFrogDeploymentException {
        StringBuffer target=new StringBuffer();
        target.append(protocol);
        target.append("://" );
        target.append(host);
        target.append(':');
        target.append(port);
        if(!page.startsWith("/")) {
            target.append('/');
        }
        target.append(page);
                                
        if(queries!=null) {
            target.append(queries);
        }
        bindToURL(target.toString());
    }

    /**
     * call this after configuring the class; does any preparation and turns
     * any {@link java.net.MalformedURLException} into a {@link
     * SmartFrogException}. If the target URL is already defined, does
     * nothing.
     *
     * @throws SmartFrogDeploymentException
     */
    public synchronized void onDeploy() throws SmartFrogDeploymentException {
        demandCreateURL();

    }

    private void demandCreateURL() throws SmartFrogDeploymentException {
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
        if (!isEnabled()) {
            return;
        }

        //set up the connection
        HttpURLConnection connection = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("connecting to " + targetURL);
            }
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setInstanceFollowRedirects(followRedirects);

            //this call actually triggers sending the request
            int responseCode = connection.getResponseCode();

            if (responseCode <= 0) {
                throw new SmartFrogLivenessException("endpoint " + toString()
                        + " is not returning HTTP responses");
            }

            String response = connection.getResponseMessage();
            if (log.isDebugEnabled()) {
                log.debug("response=" + response);
            }

            if ((responseCode < minimumResponseCode)
                    || (responseCode > maximumResponseCode)) {
                throw new SmartFrogLivenessException("endpoint " + toString()
                        + " returned error " + response
                        + maybeGetErrorText(connection));
            }

            //now fetch the file
            String body=getInputOrErrorText(connection);

            if(mimeTypeMap!=null) {
                String mimeType = connection.getContentType();
                if(null== mimeTypeMap.get(mimeType)) {
                    throw new SmartFrogLivenessException("Unexpected mimetype: "+mimeType);
                }

            }

            postProcess(responseCode,response,body);

        } catch (IOException exception) {
            String message = "Failed to read " + targetURL.toString() + "\n"
                    + maybeGetErrorText(connection)+"\n"+exception.getMessage();
            log.error(message);
            throw new SmartFrogLivenessException(message, exception);
        }
    }

    /**
     * just a little something for subclassers out there
     * @param responseCode
     * @param response
     * @param body
     * @throws SmartFrogLivenessException
     */
    private void postProcess(int responseCode, String response, String body) throws SmartFrogLivenessException {
    }


    /**
     * fetch error text if configured to do so, otherwise return an empty
     * string
     *
     * @param connection a connection that can be null if it so chooses.
     * @return "" or remote error text
     */
    protected String maybeGetErrorText(HttpURLConnection connection) {
        if(connection==null) {
            return "unable to connect to URL";
        }
        if (fetchErrorText) {
            return getInputOrErrorText(connection);
        } else {
            return "";
        }
    }

    /**
     * this call assumes that the connection is open, we now go to get the text
     * from the connection, be it good text or error text. If something goes
     * wrong partway through a fetch, we return all that we had.
     *
     * @param connection
     * @return null if there was no input from either stream, or something went
     *         wrong with the read.
     */
    protected String getInputOrErrorText(HttpURLConnection connection) {
        InputStream instream = null;
        StringWriter text = null;

        InputStreamReader reader = null;
        try {
            try {
                instream = connection.getInputStream();
            } catch (IOException e) {
                instream = connection.getErrorStream();
            }

            if (instream == null) {
                return "";
            }

            text = new StringWriter(BLOCKSIZE);

            char[] buffer = new char[BLOCKSIZE];
            reader = new InputStreamReader(instream);

            int length;

            while ((length = reader.read(buffer)) > 0) {
                text.write(buffer, 0, length);
            }

            instream = null;

            return text.toString();
        } catch (IOException e) {
            //something failed when reading.
            if (text != null) {
                return text.toString();
            }

            return null;
        } finally {
            FileSystem.close(reader);
            FileSystem.close(instream);
        }
    }

    /**
     * @return a string representation of the object.
     */
    public String toString() {
        return "LivenessCheck " + targetURL.toString() + " " + minimumResponseCode
                + "< response <" + maximumResponseCode + "; enabled = " + enabled;
    }

    /**
     * set this to track redirects
     *
     * @param followRedirects
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * get the follow redirects flag
     *
     * @return true iff redirects should be followed 
     */
    public boolean getFollowRedirects() {
        return followRedirects;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
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
     * @return the protocol
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
     * @return hostname
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

    /**
     * get the port
     *
     * @return port #
     */
    public int getPort() {
        return port;
    }

    /**
     * set the port at the destination, if URL is not defined
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * get the target URL attribute
     *
     * @return target URL or null
     */
    public URL getTargetURL() {
        return targetURL;
    }

    /**
     * minimum response code for a successful get
     *
     * @return current value
     */
    public int getMinimumResponseCode() {
        return minimumResponseCode;
    }

    /**
     * minimum response code for a successful get
     *
     * @param minimumResponseCode new value
     */
    public void setMinimumResponseCode(int minimumResponseCode) {
        this.minimumResponseCode = minimumResponseCode;
    }

    /**
     * max response code for a successful get
     *
     * @return current value
     */
    public int getMaximumResponseCode() {
        return maximumResponseCode;
    }

    /**
     * max response code for a successful get
     *
     * @param maximumResponseCode new value
     */
    public void setMaximumResponseCode(int maximumResponseCode) {
        this.maximumResponseCode = maximumResponseCode;
    }

    /**
     * fetch the error text when things fail?
     *
     * @return current value
     */
    public boolean getFetchErrorText() {
        return fetchErrorText;
    }

    /**
     * fetch the error text when things fail?
     *
     * @param fetchErrorText new value
     */
    public void setFetchErrorText(boolean fetchErrorText) {
        this.fetchErrorText = fetchErrorText;
    }

    /**
     * enabled flag. overrides other options
     *
     * @return current value
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * enabled flag. overrides other options
     *
     * @param enabled new value
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * extract a query string from the parameters
     * @param params
     */
    public void buildQueryString(Vector params) {

        StringBuffer query=null;
        if(params!=null) {
            Iterator it=params.iterator();
            while ( it.hasNext() ) {
                if ( query != null ) {
                    query.append('&');
                } else {
                    query = new StringBuffer();
                }
                Object o = (Object) it.next();
                if(o instanceof Vector && ((Vector)o).size()>1) {
                    Vector nested=(Vector) o;
                    String name=nested.elementAt(0).toString();
                    String value=nested.elementAt(1).toString();
                    query.append(name);
                    query.append('=');
                    query.append(value);
                } else {
                    String term=o.toString();
                    query.append(term);
                }
            }
        }
        if(query==null) {
            queries=null;
        } else {
            queries="?"+query;
        }

    }

    /**
     * set the mime types of this component
     * @param mimeTypes
     */
    public void setMimeTypes(Vector mimeTypes) {
        if(mimeTypes==null || mimeTypes.size()==0) {
            mimeTypeMap=null;
        } else {
            Iterator it=mimeTypes.iterator();
            while (it.hasNext()) {
                String type = (it.next().toString()).intern();
                mimeTypeMap.put(type,type);
            }
        }
    }
}
