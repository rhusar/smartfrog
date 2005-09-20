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
package org.smartfrog.services.deployapi.client;


import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.smartfrog.services.deployapi.system.Constants;
import org.apache.axis2.clientapi.Call;
import org.apache.axis2.addressing.EndpointReference;

import javax.xml.namespace.QName;

/**
 * created Aug 31, 2004 4:27:08 PM represents a server binding.
 */

public class PortalBinding implements Serializable {


    /**
     * url
     */
    private URL url;

    private String username;
    private String password;
    private EndpointReference endpointer;

    private String listenerTransport=null;
    private boolean separateListenerTransport=false;
    /**
     * this is the prefix we look for on the command line
     */
    public static final String URL_COMMAND = "-url:";


    public PortalBinding() {
    }

    public PortalBinding(URL url) {
        setURL(url);
    }


    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
        endpointer = new EndpointReference(url.toExternalForm());
    }

    public void bindToURL(String urlValue) throws MalformedURLException {
        url = new URL(urlValue);
        setURL(url);
    }

    public EndpointReference getEndpointer() {
        return endpointer;
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
    public Call createStub() throws RemoteException {
        assert url != null;

        //create a new bound stub
        Call call = new Call();
        call.setExceptionToBeThrownOnSOAPFault(true);
        call.setTo(getEndpointer());
        call.setTransportInfo(getSenderTransport(),getListenerTransport(),isSeparateListenerTransport());
        //turn on addressing
        call.engageModule(new QName(org.apache.axis2.Constants.MODULE_ADDRESSING));

        return call;
    }

    public boolean isSeparateListenerTransport() {
        return separateListenerTransport;
    }

    public void setSeparateListenerTransport(boolean separateListenerTransport) {
        this.separateListenerTransport = separateListenerTransport;
    }

    public String getListenerTransport() {
        return listenerTransport;
    }

    public void setListenerTransport(String listenerTransport) {
        this.listenerTransport = listenerTransport;
    }


    public String getSenderTransport() {
        String protocol=url.getProtocol();
        if("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {
            return "http";
        } else {
            return protocol;
        }
    }

    public static PortalBinding createDefaultBinding() throws IOException {
        PortalBinding binding = new PortalBinding();
        URL defURL = new URL("http",
                Constants.DEFAULT_HOST,
                Constants.DEFAULT_SERVICE_PORT,
                Constants.DEFAULT_PATH);

        binding.setURL(defURL);
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
     * @throws java.net.MalformedURLException if there was anything wrong with the URL
     */
    public static PortalBinding fromCommandLineElement(
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
            PortalBinding binding = new PortalBinding(newurl);
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
     * @throws java.net.MalformedURLException if there was anything wrong with the URL
     */
    public static PortalBinding fromCommandLine(String[] commandLine)
            throws MalformedURLException {
        PortalBinding binding = null;
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



}
