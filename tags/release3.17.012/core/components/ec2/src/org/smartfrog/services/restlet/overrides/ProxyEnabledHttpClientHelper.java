/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.restlet.overrides;

import com.noelios.restlet.ext.httpclient.HttpClientHelper;
import com.noelios.restlet.http.HttpClientCall;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.restlet.Client;
import org.restlet.data.Request;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.services.www.HttpProxyProperties;

/**
 *
 * Created 29-Jan-2008 12:58:36
 *
 */

public class ProxyEnabledHttpClientHelper extends HttpClientHelper {
    private static final Log log = LogFactory.getLog(ProxyEnabledHttpClientHelper.class);
    private String proxyHost;
    private String proxyUser;
    private int proxyPort;
    private String proxyPassword;
    private String proxyRealm;

    public ProxyEnabledHttpClientHelper(Client client) {
        super(client);
    }

    /**
     * Creates a low-level HTTP client call from a high-level uniform call.
     *
     * @param request The high-level request.
     * @return A low-level HTTP client call.
     */
    public HttpClientCall create(Request request) {
        HttpClientCall clientCall = super.create(request);
        return clientCall;
    }

    /**
     * {@inheritDoc}
     * @throws Exception if it doesnt start
     */
    @Override
    public void start() throws Exception {
        super.start();
        //now configure the client
        configureClient(getHttpClient());
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getProxyRealm() {
        return proxyRealm;
    }

    public void setProxyRealm(String proxyRealm) {
        this.proxyRealm = proxyRealm;
    }

    public boolean isEnabled() {
        return proxyHost != null;
    }

    public boolean isAuthenticating() {
        return proxyUser != null;
    }

    /**
     * Extract proxy settings from the system settings.
     *
     * @return true iff the proxy is enabled
     */
    public boolean bindToSystemProxySettings() {
        proxyHost = System.getProperty(HttpProxyProperties.HTTP_PROXY_HOST);
        if (proxyHost != null) {
            if (proxyHost.length() == 0) {
                proxyHost = null;
            }
        }
        if (!isEnabled()) {
            return false;
        }
        proxyPort = Integer.parseInt(System.getProperty(HttpProxyProperties.HTTP_PROXY_PORT, "80"));
        proxyUser = System.getProperty(HttpProxyProperties.HTTP_PROXY_USER);
        proxyPassword = System.getProperty(HttpProxyProperties.HTTP_PROXY_PASSWORD);
        if(log.isDebugEnabled()) {
            log.debug("Binding to proxy " + proxyHost + ':' + proxyPort);
        }
        return true;
    }

    /**
     * configure an HTTP client from the settings
     *
     * @param client client to configure
     */
    public void configureClient(HttpClient client) {
        if (isEnabled()) {
            HostConfiguration hostConfiguration = client.getHostConfiguration();
            hostConfiguration.setProxy(proxyHost, proxyPort);
            if (isAuthenticating()) {
                client.getState().setProxyCredentials(proxyRealm, proxyHost,
                        new UsernamePasswordCredentials(proxyUser, proxyPassword));
            }
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return isEnabled()?("proxy :"+proxyHost+':'+proxyPort):"no proxy";
    }
}
