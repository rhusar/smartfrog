/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.Call;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.OperationDescription;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.soap.SOAP12Constants;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * created 21-Sep-2005 13:20:35
 */

public class Endpointer implements Serializable {
    /**
     * url
     */
    protected URL url;
    private String username;
    private String password;
    private EndpointReference endpointer;
    private String listenerTransport = null;
    private boolean separateListenerTransport = true;
    protected static org.apache.axis2.description.OperationDescription[] operations;
    protected static ServiceDescription serviceDescription;
    private ConfigurationContext configurationContext;
    private ServiceContext serviceContext;

    public Endpointer() {
    }

    public Endpointer(URL url) {
        setURL(url);
    }

    public Endpointer(EndpointReference endpointer) {
        this.endpointer = endpointer;
    }


    public Endpointer(String url) {
        bindToURL(url);
    }

    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
        endpointer = new EndpointReference(url.toExternalForm());
    }

    public void bindToURL(String urlValue) {
        try {
            url = new URL(urlValue);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        setURL(url);
    }


    public void bindToEndpointer(EndpointReference epr) {
        endpointer = epr;
        try {
            url = new URL(endpointer.getAddress());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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
    public Call createStub(String operationName) throws RemoteException {
        assert url != null;

        //create a new bound stub
        Call call = new Call(getServiceContext());
        call.setExceptionToBeThrownOnSOAPFault(true);
        call.setTo(getEndpointer());
        call.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        if (operationName != null) {
            call.setSoapAction(operationName);
            call.setWsaAction(operationName);
/*
            OperationDescription operation = serviceDescription.getOperation(operationName);
            if(operation!=null) {
            }
*/
        }
//        call.setTransportInfo(getSenderTransport(),getListenerTransport(),isSeparateListenerTransport());
        //turn on addressing
//        call.engageModule(new QName(org.apache.axis2.Constants.MODULE_ADDRESSING));

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
        String protocol = url.getProtocol();
        if ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {
            return "http";
        } else {
            return protocol;
        }
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Endpointer that = (Endpointer) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    public int hashCode() {
        return (url != null ? url.hashCode() : 0);
    }

    /**
     * get our axis2 home. currently null, meaning "read config data off the classpath, not the filesys"
     *
     * @return a string representing the home dir, in a platform-specific location.
     */
    protected String getAxis2Home() {
        return null;
    }

    /**
     * this does any late initialisation of the EPR, primarily setting up axis
     *
     * @throws org.apache.axis2.AxisFault
     */
    protected void init() throws AxisFault {
        configurationContext = new ConfigurationContextFactory()
                .buildClientConfigurationContext(getAxis2Home());
        configurationContext.getAxisConfiguration().addService(serviceDescription);
        serviceContext = serviceDescription.getParent()
                .getServiceGroupContext(configurationContext)
                .getServiceContext(serviceDescription.getName().getLocalPart());        
    }

    public URL getUrl() {
        return url;
    }

    public static OperationDescription[] getOperations() {
        return operations;
    }

    public static ServiceDescription getServiceDescription() {
        return serviceDescription;
    }

    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }
}
