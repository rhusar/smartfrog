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
package org.cddlm.client.common;

import org.cddlm.client.generated.api.endpoint.CddlmSoapBindingStub;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * created Aug 31, 2004 4:27:08 PM represents a server binding.
 */

public class ServerBinding implements Serializable {

    /**
     * url
     */
    private URL url;

    private String username;
    private String password;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setUrl(String urlValue) throws MalformedURLException {
        url = new URL(urlValue);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * create a new stub from this binding
     *
     * @return
     */
    public CddlmSoapBindingStub createStub() throws RemoteException {
        assert url != null;

        //create a new bound stub
        CddlmSoapBindingStub stub = new CddlmSoapBindingStub(url, null);
        return stub;
    }


    public static ServerBinding createDefaultBinding() throws IOException {
        ServerBinding binding = new ServerBinding();
        URL defURL = new URL("http", Constants.DEFAULT_HOST,
                Constants.DEFAULT_SERVICE_PORT,
                Constants.DEFAULT_PATH);
        binding.setUrl(defURL);
        return binding;
    }

}
