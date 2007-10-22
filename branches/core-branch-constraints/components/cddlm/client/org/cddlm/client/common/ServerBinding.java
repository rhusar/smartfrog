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

import org.smartfrog.services.cddlm.generated.api.endpoint.CddlmSoapBindingStub;

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
    /**
     * this is the prefix we look for on the command line
     */
    public static final String URL_COMMAND = "-url:";
    //public static final String URL_COMMAND2 = "-u:";


    public ServerBinding() {
    }

    public ServerBinding(URL url) {
        this.url = url;
    }


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

    public String toString() {
        if (url == null) {
            return "(unbound)";
        } else {
            return url.toExternalForm();
        }
    }

    /**
     * convert to an external form.
     *
     * @return
     */
    public String toCommandLineElement() {
        return URL_COMMAND + url.toExternalForm();
    }

    /**
     * get the binding of this element, null for no match,
     *
     * @param commandLineElement
     * @return
     * @throws MalformedURLException if there was anything wrong with the URL
     */
    public static ServerBinding fromCommandLineElement(
            String commandLineElement)
            throws MalformedURLException {
        boolean isOption = commandLineElement.indexOf(URL_COMMAND) == 0;
        //isOption |= commandLineElement.indexOf(URL_COMMAND2) == 0;
        if (isOption) {
            String urlBody = commandLineElement.substring(URL_COMMAND.length());
            if ("".equals(urlBody)) {
                throw new MalformedURLException(
                        "no URL in " + commandLineElement);
            }
            URL newurl = new URL(urlBody);
            ServerBinding binding = new ServerBinding(newurl);
            return binding;
        } else {
            return null;
        }
    }

    /**
     * get the binding of this element, null for no match,
     *
     * @param commandLine full command line args
     * @return
     * @throws MalformedURLException if there was anything wrong with the URL
     */
    public static ServerBinding fromCommandLine(String[] commandLine)
            throws MalformedURLException {
        ServerBinding binding = null;
        for (int i = 0; i < commandLine.length; i++) {
            binding = fromCommandLineElement(commandLine[i]);
            if (binding != null) {
                //mark that element as null
                commandLine[i] = null;
                break;
            }
        }
        return binding;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerBinding)) {
            return false;
        }

        final ServerBinding serverBinding = (ServerBinding) o;

        if (password != null ?
                !password.equals(serverBinding.password) :
                serverBinding.password != null) {
            return false;
        }
        if (!url.equals(serverBinding.url)) {
            return false;
        }
        if (username != null ?
                !username.equals(serverBinding.username) :
                serverBinding.username != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = url.hashCode();
        result = 29 * result + (username != null ? username.hashCode() : 0);
        result = 29 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

}
