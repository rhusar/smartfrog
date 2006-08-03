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
package org.smartfrog.services.deployapi.axis2.client;


import nu.xom.Document;
import nu.xom.Element;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.InOutAxisOperation;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_LOOKUPSYSTEM_REQUEST;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_PORTAL_OPERATION_CREATE;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_PORTAL_OPERATION_LOOKUPSYSTEM;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_PORTAL_OPERATION_RESOLVE;
import static org.ggf.cddlm.generated.api.CddlmConstants.WSRF_OPERATION_GETCURRENTMESSAGE;
import static org.ggf.cddlm.generated.api.CddlmConstants.WSRF_OPERATION_GETMULTIPLERESOURCEPROPERTIES;
import static org.ggf.cddlm.generated.api.CddlmConstants.WSRF_OPERATION_GETRESOURCEPROPERTY;
import static org.ggf.cddlm.generated.api.CddlmConstants.WSRF_OPERATION_SUBSCRIBE;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * created Aug 31, 2004 4:27:08 PM represents a server binding.
 */

public class PortalEndpointer extends Endpointer {


    protected static AxisOperation[] operations;
    protected static AxisService serviceDescription;

    /**
     * This is pasted in from generated axis code
     */
    static {

        //creating the Service
        serviceDescription = new AxisService(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "PortalEPR").toString());

        //creating the operations
        AxisOperation __operation;
        operations = new AxisOperation[7];

        __operation = new InOutAxisOperation();
        __operation.setName(new QName(Constants.CDL_API_WSDL_NAMESPACE,
                WSRF_OPERATION_GETMULTIPLERESOURCEPROPERTIES));
        operations[0] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, API_PORTAL_OPERATION_CREATE));
        operations[1] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, API_PORTAL_OPERATION_LOOKUPSYSTEM));
        operations[2] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(new QName(Constants.CDL_API_WSDL_NAMESPACE,
                WSRF_OPERATION_GETCURRENTMESSAGE));
        operations[3] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, API_PORTAL_OPERATION_RESOLVE));
        operations[4] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, WSRF_OPERATION_SUBSCRIBE));
        operations[5] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(new QName(Constants.CDL_API_WSDL_NAMESPACE,
                WSRF_OPERATION_GETRESOURCEPROPERTY));
        operations[6] = __operation;
        serviceDescription.addOperation(__operation);

    }


    public PortalEndpointer() throws AxisFault {
        init();
    }

    public PortalEndpointer(URL url) throws AxisFault {
        setURL(url);
        init();
    }

    public PortalEndpointer(String url) throws AxisFault {
        super(url);
        init();
    }

    public AxisService getServiceDescription() {
        return serviceDescription;
    }

    public static PortalEndpointer createDefaultBinding() throws IOException {
        PortalEndpointer endpointer = new PortalEndpointer();
        URL defURL = new URL("http",
                Constants.DEFAULT_HOST,
                Constants.DEFAULT_SERVICE_PORT,
                Constants.DEFAULT_PATH);

        endpointer.setURL(defURL);
        return endpointer;
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
     * will patch that entry in the cmd line to null as a side effect
     * @param commandLine full command line args
     * @return a portal
     * @throws java.net.MalformedURLException if there was anything wrong with the URL
     */
    public static PortalEndpointer fromCommandLine(String[] commandLine)
            throws MalformedURLException, AxisFault {
        URL url = UrlFromCommandLine(commandLine);
        if(url!=null) {
            return new PortalEndpointer(url);
        } else {
            return null;
        }
    }



    /**
     * create an application
     *
     * @return info about a destination
     * @throws java.rmi.RemoteException
     */
    public SystemEndpointer create(String hostname)
            throws IOException {
        Element request;
        request = XomHelper.apiElement(Constants.API_ELEMENT_CREATE_REQUEST);
        if (hostname != null) {
            Element child = XomHelper.apiElement("hostname");
            child.appendChild(hostname);
            request.appendChild(child);
        }

        Document response = invokeBlocking(Constants.API_PORTAL_OPERATION_CREATE,
                request);
        SystemEndpointer createdSystem;
        createdSystem=new SystemEndpointer(response);
        return createdSystem;
    }


    public SystemEndpointer lookupSystem(String id) throws RemoteException {
        Element resid=XomHelper.apiElement("ResourceId",id);
        Element request;
        request = XomHelper.apiElement(API_ELEMENT_LOOKUPSYSTEM_REQUEST,resid);
        Document response = invokeBlocking(Constants.API_PORTAL_OPERATION_LOOKUPSYSTEM,
                request);
        SystemEndpointer system;
        system = new SystemEndpointer(response);
        return system;
    }

}
