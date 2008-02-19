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
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;


/**
 * Model a liveness page. <p/> This is not quite a SmartFrog component, but rather a general purpose class for importing
 * into other things, as needed. <p/> Created 20-Apr-2004 16:11:51
 */
public class LivenessPageChecker implements LivenessPage {

    /**
     * size to download packets
     */
    protected static final int BLOCKSIZE = 8192;

    /**
     * path attribute
     */
    protected String path = null;

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
     * user name
     */
    protected String username;

    /**
     * password
     */
    protected String password;

    /**
     * The URL
     */
    protected URL targetURL = null;

    private String urlAsString;

    private URI uri;

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
     * max response code; anything above 299 is an error. That includes not-modified
     */
    protected int maximumResponseCode = 299;

    /**
     * flag to set if you want to fetch the remote error message
     */
    protected boolean fetchErrorText = true;

    /**
     * string of queries at the end of the url
     */
    protected String queries = null;

    /**
     * our log
     */
    protected LogSF log;

    private boolean logResponse;

    /**
     * Owner: may be null
     */
    private Prim owner;

    /**
    * Mime types
    */
    protected HashMap<String, String> mimeTypeMap;
    private String errorMessage;

    /** headers */
    private Vector<Vector<String>> headers;
    private int connectTimeout;

    /** regexp or empty string */
    private String responseRegexp="";
    private Pattern responsePattern;
    public static final String ERROR_NO_CONNECTION = "unable to connect to URL";
    public static final String ERROR_NO_MATCH = "Response body does not match regular expression ";
    public static final String ERROR_UNABLE_TO_COMPILE = "Unable to compile ";
    private static final String FAILED_TO_REPLACE_ATTRIBUTE = "failed to replace attribute ";
    private static final String BAD_URL = "Bad URL: ";

    /**
     * create a new liveness page
     *
     * @param protocol protocol http or https
     * @param host     hostname/ip address
     * @param port     port to use
     * @param page     page on the web site
     * @throws RemoteException              for RMI/Networking problems
     * @throws SmartFrogDeploymentException deployment problems
     */
    public LivenessPageChecker(
            String protocol,
            String host,
            int port,
            String page) throws RemoteException, SmartFrogDeploymentException {
        this.page = page;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        makeURL();
    }

    /**
     * Creates a new LivenessPageChecker object.
     *
     * @param url A URL to check
     * @throws SmartFrogDeploymentException if the url generated a {@link MalformedURLException}
     * @throws SmartFrogLogException        log setup problems
     */
    public LivenessPageChecker(String url)
            throws SmartFrogLogException, SmartFrogDeploymentException {
        bind(null);
        bindToURL(url);
    }

    /**
     * Creates a new LivenessPageChecker object.
     *
     * @param owner owner component
     * @param url   A URL to check
     * @throws SmartFrogLogException log setup problems
     */
    public LivenessPageChecker(Prim owner, URL url) throws SmartFrogLogException {
        bind(owner);
        targetURL = url;
    }

    /**
     * Creates a new LivenessPageChecker object.
     *
     * @param owner onwner component
     * @throws SmartFrogLogException log setup problems
     */
    public LivenessPageChecker(Prim owner) throws SmartFrogLogException {
        bind(owner);
    }

    /**
     * bind to the owner, includes log setup
     *
     * @param owner owner class
     * @throws SmartFrogLogException log setup problems
     */
    private void bind(Prim owner) throws SmartFrogLogException {
        this.owner=owner;
        if (owner != null) {
            log = LogFactory.getLog(owner);
        }
    }


    /**
     * bind to a url string
     *
     * @param target URL to bind to
     * @throws SmartFrogDeploymentException if the url generated a {@link MalformedURLException}
     */
    public synchronized void bindToURL(String target) throws SmartFrogDeploymentException {
        bindToURL(target,target);
    }

    /**
     * bind to a url string
     *
     * @param target URL to bind to
     * @param sanitizedTarget the same URL without any password strings
     * @throws SmartFrogDeploymentException if the url generated a {@link MalformedURLException}
     */
    public synchronized void bindToURL(String target,String sanitizedTarget) throws SmartFrogDeploymentException {
        try {
            targetURL = new URL(target);
            uri = targetURL.toURI();
            urlAsString = sanitizedTarget;
        } catch (MalformedURLException e) {
            throw new SmartFrogDeploymentException(BAD_URL + target, e);
        } catch (URISyntaxException e) {
            throw new SmartFrogDeploymentException(BAD_URL + target, e);
        }
    }

    /**
     *  concatenate paths, ensuring only one / between them.
     * @param first first string, or ""
     * @param second second string or ""
     * @return first/second, even if first has a trailing / and second a leading /
     */
    protected String concatPaths(String first,String second) {
        String f, s;
        f = first != null ? first : "";
        int fl = first.length();
        if (fl > 0 && first.charAt(fl - 1) == '/') {
            f = first.substring(0, fl - 2);
        }

        s = second != null ? second : "";
        if (second.length() > 0 && second.charAt(0) == '/') {
            s = second.substring(1);
        }
        return f + '/' + s;
    }

    /**
    * make a URL from the various things
    *
    * @throws SmartFrogDeploymentException if the url generated a {@link MalformedURLException}
    */
    protected void makeURL() throws SmartFrogDeploymentException {
        StringBuilder url= new StringBuilder();
        url.append(protocol);
        url.append("://");
        StringBuilder safeURL=new StringBuilder();
        safeURL.append(url);
        if (username != null) {
            url.append(username);
            url.append(':');
            url.append(password);
            url.append('@');
        }

        StringBuilder target = new StringBuilder();
        target.append(host);
        target.append(':');
        target.append(port);
        String fullpath=concatPaths("",path);
        fullpath = concatPaths(fullpath, page);
        target.append(fullpath);

        if (queries != null) {
            target.append(queries);
        }
        url.append(target);
        safeURL.append(target);
        //bindToURL(url.toString(),safeURL.toString());
        bindToURL(url.toString());
    }

    /**
     * call this after configuring the class; does any preparation and turns any {@link MalformedURLException} into a
     * {@link SmartFrogDeploymentException}. If the target URL is already defined, does nothing.
     *
     * @throws SmartFrogDeploymentException if the URL is bad
     */
    public synchronized void onStart() throws SmartFrogDeploymentException {
        demandCreateURL();
    }

    /**
     * Create the URL on demand
     *
     * @throws SmartFrogDeploymentException the URL is bad
     */
    private void demandCreateURL() throws SmartFrogDeploymentException {
        if (targetURL == null) {
            makeURL();
        }
    }

    /**
     * try and retrieve the liveness page.
     *
     * @throws SmartFrogLivenessException if the check fails
     */
    public void onPing() throws SmartFrogLivenessException {
        if (!isEnabled()) {
            return;
        }
        checkPage();
    }

    /**
     * check the page
     *
     * @throws SmartFrogLivenessException if the check fails
     */
    public void checkPage() throws SmartFrogLivenessException {
        //set up the connection
        HttpURLConnection connection = null;
        boolean logDebug = log != null && log.isDebugEnabled();
        try {
            if (logDebug) {
                log.debug("Connecting to " + urlAsString);
            }
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setInstanceFollowRedirects(followRedirects);
            //set the headers
            if (headers != null) {
                for (Vector<String> tuple : headers) {
                    connection.addRequestProperty(tuple.get(0), tuple.get(1));
                }
            }
            if (connectTimeout >= 0) {
                connection.setConnectTimeout(connectTimeout);
            }

            //this call actually triggers sending the request
            int responseCode = connection.getResponseCode();

            if (responseCode <= 0) {
                logAndRaise("Endpoint " + toString()
                        + " is not returning HTTP responses");
            }

            String response = connection.getResponseMessage();
            if (logDebug) {
                log.debug("response=\n" + response);
            }

            if (isStatusOutOfRange(responseCode)) {
                String text = maybeGetErrorText(connection);
                String message = "Endpoint " + toString()
                        + " returned error: "+ responseCode
                        + '\n' + response
                        + '\n' + text;
                logAndRaise(message);
            }

            //now fetch the file
            String body = getInputOrErrorText(connection);

            if (mimeTypeMap != null) {
                String mimeType = connection.getContentType();
                if (!isMimeTypeInRange(mimeType)) {
                    logAndRaise("Unexpected mimetype: " + mimeType);
                }

            }

            postProcess(responseCode, response, body);

        } catch (IOException exception) {
            //String text = maybeGetErrorText(connection);
            String message = "Failed to read " + urlAsString + '\n'
                    + '\n' + exception.getMessage();
            logAndRaise(message);
        }
    }

    /**
     * Tests for the mime type being in range
     *
     * @param mimeType the supplied mime type
     * @return true if there are no mime types specified for this checker, or the type is in the list of supported types
     *         (no regexp matching yet)
     */
    public boolean isMimeTypeInRange(String mimeType) {
        return mimeTypeMap == null || lookupMimeType(mimeType) != null;
    }

    /**
     * Look up the mime type in the mime type map.
     *
     * @param mimeType the mime type
     * @return null if there is no match (or no mime map), the actual value if there is one
     */
    public String lookupMimeType(String mimeType) {
        return mimeTypeMap != null ? mimeTypeMap.get(mimeType) : null;
    }

    /**
     * Compare the supplied error code with the min/max codes for this checker
     *
     * @param responseCode the response code to check
     * @return true iff it is out of range.
     */
    public boolean isStatusOutOfRange(int responseCode) {
        return (responseCode < minimumResponseCode)
                || (responseCode > maximumResponseCode);
    }

    /**
     * Log the error message and raise an exception. The error text is also saved to {@link #errorMessage}
     *
     * @param message message to report
     * @throws SmartFrogLivenessException always
     */
    private void logAndRaise(String message) throws SmartFrogLivenessException {
        errorMessage = message;
        log.error(message);
        throw new SmartFrogLivenessException(message);
    }

    /**
     * Override point.
     * Default implementation checks the regular expression and adds its first group as the group1 attribute 
     * @param responseCode response http code
     * @param response     response line
     * @param body         body of the response
     * @throws SmartFrogLivenessException if need be
     */
    private void postProcess(int responseCode, String response, String body)
            throws SmartFrogLivenessException {
        if (logResponse) {
            log.info(body);
        }
        if (responsePattern != null) {
            Matcher matcher = responsePattern.matcher(body);
            if (!matcher.matches()) {
                throw new SmartFrogLivenessException(ERROR_NO_MATCH + responseRegexp
                        + "\n" + body);
            }
            try {
                if (owner != null) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        String group = matcher.group(1);
                        log.info("Matched response group" + i + ": " + group);
                        owner.sfReplaceAttribute("group" + i, group);
                    }
                }
            } catch (SmartFrogRuntimeException e) {
                log.ignore(FAILED_TO_REPLACE_ATTRIBUTE, e);
            } catch (RemoteException e) {
                log.ignore(FAILED_TO_REPLACE_ATTRIBUTE, e);
            }
        }
    }

/**
* fetch error text if configured to do so, otherwise return an empty string
*
* @param connection a connection that can be null if it so chooses.
* @return "" or remote error text
*/
    protected String maybeGetErrorText(HttpURLConnection connection) {
        if (connection == null) {
            return ERROR_NO_CONNECTION;
        }
        if (fetchErrorText) {
            return getInputOrErrorText(connection);
        } else {
            return "";
        }
    }

    /**
     * this call assumes that the connection is open, we now go to get the text from the connection, be it good text or
     * error text. If something goes wrong partway through a fetch, we return all that we had.
     *
     * @param connection current connection
     * @return null if there was no input from either stream, or something went wrong with the read.
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
        return urlAsString + " ["
                + minimumResponseCode + "< response <" + maximumResponseCode+"]"
                + (enabled ? "" : "(disabled)");
    }

    /**
     * set this to track redirects
     *
     * @param followRedirects true to follow redirects
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
     * Get the page attribute
     *
     * @return the page to fetch
     */
    public String getPage() {
        return page;
    }

    /**
     * the page under the host to probe
     *
     * @param page page to fetch
     */
    public void setPage(String page) {
        this.page = page;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
     * @param protocol protocol, e,g http
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
     * @param host hostname or IP addr
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
     * @param port port number
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
     * Get any error message raised by the last poll
     *
     * @return the error message, which may be null
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * query the enabled flag. Overrides other options
     *
     * @return current value
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * set the enabled flag. overrides other options
     *
     * @param enabled new value
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getUrlAsString() {
        return urlAsString;
    }

    /**
     * Get the URL as a URI
     * @return the active URL as a URI; null if we havent bound yet.
     */
    public URI getUri() {
        return uri;
    }


    public String getResponseRegexp() {
        return responseRegexp;
    }

    /**
     * Set the response regular expression
     * @param responseRegexp the regular expression
     * @throws SmartFrogDeploymentException if the syntax would not compile
     */
    public void setResponseRegexp(String responseRegexp) throws SmartFrogDeploymentException {
        this.responseRegexp = responseRegexp;
        if(responseRegexp!=null && responseRegexp.length()>0) {
            try {
                responsePattern=Pattern.compile(responseRegexp);
            } catch (PatternSyntaxException e) {
                throw new SmartFrogDeploymentException(ERROR_UNABLE_TO_COMPILE +responseRegexp,e);
            }
        } else {
            responsePattern=null;
        }
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }

    /**
     * extract a query string from the parameters
     *
     * @param params vectore of parameters for the query
     */
    public void buildQueryString(Vector params) {

        StringBuilder query = null;
        if (params != null) {
            for (Object param : params) {
                if (query != null) {
                    query.append('&');
                } else {
                    query = new StringBuilder();
                }
                if (param instanceof Vector && ((Vector) param).size() > 1) {
                    Vector nested = (Vector) param;
                    String name = nested.elementAt(0).toString();
                    String value = nested.elementAt(1).toString();
                    query.append(name);
                    query.append('=');
                    query.append(value);
                } else {
                    String term = param.toString();
                    query.append(term);
                }
            }
        }
        if (query == null) {
            queries = null;
        } else {
            queries = "?" + query;
        }

    }

    /**
     * set the mime types of this component
     *
     * @param mimeTypes vector of supported mime types
     */
    public void setMimeTypes(Vector<String> mimeTypes) {
        if (mimeTypes == null || mimeTypes.isEmpty()) {
            mimeTypeMap = null;
        } else {
            mimeTypeMap = new HashMap<String, String>(mimeTypes.size());
            for (String mimeType : mimeTypes) {
                mimeTypeMap.put(mimeType, mimeType);
            }
        }
    }

    /**
     * Set the headers
     *
     * @param headers headers
     */
    public void setHeaders(Vector<Vector<String>> headers) {
        this.headers = headers;
    }

    /**
     * Set the timeout in milliseconds for opening a connection
     *
     * @param connectTimeout the new value in millis
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
