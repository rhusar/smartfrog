/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.transport.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class to pass java proxy settings down to the http client runtime.
 * <p/>
 * created 19-Apr-2006 16:27:21
 */


public class ProxySettings {

    private static final Log log= LogFactory.getLog(ProxySettings.class);
    private String proxyHost;
    private String proxyUser;
    private int proxyPort;
    private String proxyPassword;
    private String proxyRealm;

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
    public boolean bindToSystemSettings() {
        proxyHost = System.getProperty("http.proxyHost");
        if (proxyHost != null) {
            if (proxyHost.length() == 0) {
                proxyHost = null;
            }
        }
        if (!isEnabled()) {
            return false;
        }
        proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"));
        proxyUser = System.getProperty("http.proxyUser");
        proxyPassword = System.getProperty("proxyPassword");
        log.debug("Binding to proxy "+proxyHost+":"+proxyPort);
        return true;
    }

    /**
     * configure an HTTP client from the settinsg
     *
     * @param client
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

}
