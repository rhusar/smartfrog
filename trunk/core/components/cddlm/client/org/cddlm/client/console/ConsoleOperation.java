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
package org.cddlm.client.console;

import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.common.Constants;
import org.cddlm.client.generated.api.endpoint.CddlmSoapBindingStub;
import org.cddlm.client.generated.api.types.ApplicationReferenceListType;
import org.cddlm.client.generated.api.types.ApplicationStatusType;
import org.cddlm.client.generated.api.types.CallbackInformationType;
import org.cddlm.client.generated.api.types.DeploymentDescriptorType;
import org.cddlm.client.generated.api.types.EmptyElementType;
import org.cddlm.client.generated.api.types.JsdlType;
import org.cddlm.client.generated.api.types.OptionMapType;
import org.cddlm.client.generated.api.types.ServerStatusType;
import org.cddlm.client.generated.api.types._applicationStatusRequest;
import org.cddlm.client.generated.api.types._deployRequest;
import org.cddlm.client.generated.api.types._deployResponse;
import org.cddlm.client.generated.api.types._deploymentDescriptorType_data;
import org.cddlm.client.generated.api.types._serverStatusRequest;
import org.cddlm.client.generated.api.types._lookupApplicationRequest;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

/**
 * base class for console operations created Aug 31, 2004 4:44:30 PM
 */

public abstract class ConsoleOperation {

    /**
     * our output stream
     */
    protected PrintWriter out;

    /**
     * our server binding
     */
    protected ServerBinding binding;


    /**
     * stub. this is only valid w
     */
    private CddlmSoapBindingStub stub;
    public static final String SMARTFROG_VERSION = "1.0";

    /**
     * demand create our stub. retain it afterwards for reuse.
     *
     * @return
     * @throws RemoteException
     */
    public CddlmSoapBindingStub getStub() throws RemoteException {
        if (stub == null) {
            out.println("Connecting to " + binding.toString());
            stub = binding.createStub();
        }
        return stub;
    }

    public ConsoleOperation(ServerBinding binding, PrintWriter out) {
        this.out = out;
        this.binding = binding;
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws RemoteException
     */
    public abstract void execute() throws RemoteException;

    /**
     * log a throwable to the output stream
     *
     * @param t
     */
    public void logThrowable(Throwable t) {
        t.printStackTrace(out);
        out.flush();
    }

    /**
     * execute; log exceptions to the stream
     *
     * @return true if it worked, false if not
     */
    public boolean doExecute() {
        try {
            execute();
            out.flush();
            return true;
        } catch (RemoteException e) {
            logThrowable(e);
            return false;
        }
    }

    /**
     *
     * @param args command line arguments; look for -url url
     * @return
     */
    public static ServerBinding extractBindingFromCommandLine(String[] args)
            throws IOException {
        ServerBinding extractedBinding = ServerBinding.fromCommandLine(args);
        if (extractedBinding == null) {
            extractedBinding = ServerBinding.createDefaultBinding();
        }
        return extractedBinding;
    }


    /**
     * list all applications
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public URI[] listApplications() throws RemoteException {
        EmptyElementType empty = new EmptyElementType();
        ApplicationReferenceListType list = getStub().listApplications(empty);
        URI apps[] = list.getApplication();
        if (apps == null) {
            apps = new URI[0];
        }
        return apps;
    }

    /**
     * get the status of an application
     *
     * @param app application identifier
     * @return
     * @throws java.rmi.RemoteException
     */
    ApplicationStatusType lookupApplicationStatus(URI app)
            throws RemoteException {
        _applicationStatusRequest request = new _applicationStatusRequest();
        request.setApplication(app);
        ApplicationStatusType status = getStub().applicationStatus(request);
        return status;
    }

    /**
     * get the status of an application
     *
     * @param app application identifier
     * @return
     * @throws java.rmi.RemoteException
     */
    ApplicationStatusType lookupApplicationStatus(String app)
            throws RemoteException {
        return lookupApplicationStatus(lookupApplication(app));
    }

    /**
     * get the status of an application
     *
     * @param app application identifier
     * @return
     * @throws java.rmi.RemoteException
     */
    ApplicationStatusType lookupApplicationStatus(NCName app)
            throws RemoteException {
        return lookupApplicationStatus(lookupApplication(app));
    }

    /**
     * get the status
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public ServerStatusType getStatus() throws RemoteException {
        _serverStatusRequest request = new _serverStatusRequest();
        ServerStatusType status = getStub().serverStatus(request);
        return status;
    }


    /**
     * deploy a named application, or return an exception
     *
     * @param name
     * @param descriptor
     * @param options
     * @return
     * @throws RemoteException
     */
    public URI deploy(String name,
                      DeploymentDescriptorType descriptor,
                      OptionMapType options) throws RemoteException {
        JsdlType jsdl = new JsdlType();
        NCName ncname = makeName(name);
        CallbackInformationType callbackInfo = null;
        _deployRequest request = new _deployRequest(jsdl,
                ncname,
                descriptor,
                callbackInfo,
                options);
        _deployResponse response = getStub().deploy(request);
        return response.getApplicationReference();

    }

    /**
     * wrap a string with a smartfrog deploy descriptor
     * @param source
     * @return
     * @throws IOException
     */
    public DeploymentDescriptorType createSmartFrogDescriptor(
            String source) throws IOException {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        _deploymentDescriptorType_data data = new _deploymentDescriptorType_data();
        MessageElement element=new MessageElement(Constants.SMARTFROG_NAMESPACE,Constants.SMARTFROG_ELEMENT_NAME);
        element.addAttribute(Constants.SMARTFROG_NAMESPACE, Constants.SMARTFROG_ELEMENT_VERSION_ATTR, SMARTFROG_VERSION);
        Text text= new org.apache.axis.message.Text(source);
        element.appendChild(text);
        MessageElement any[]=new MessageElement[1];
        any[0]=element;
        data.set_any(any);
        descriptor.setData(data);
        return descriptor;
    }


    /**
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param in input stream
     * @return a deployment descriptor for smartfrog
     * @throws IOException
     */
    public DeploymentDescriptorType createSmartFrogDescriptor(InputStream in) throws IOException {
        String source=readIntoString(in);
        return createSmartFrogDescriptor(source);
    }

    /**
     * helper to read into a string
     * @param in
     * @return
     * @throws IOException
     */
    public static String readIntoString(InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        StringBuffer buffer = new StringBuffer();
        char[] block = new char[1024];
        int read;
        while ( ((read = reader.read(block)) >= 0) ) {
            buffer.append(block);
        }
        return buffer.toString();
    }

    /**
     * turn a string into an NC name
     * @param name
     * @return
     */
    public NCName makeName(String name) {
        return new NCName(name);
    }

    /**
     * look up an application against the server
     * @param ncname name of app
     * @return URI of the app
     */
    public URI lookupApplication(NCName ncname) throws RemoteException {
        _lookupApplicationRequest request=new _lookupApplicationRequest(ncname);
        return getStub().lookupApplication(request);
    }

    /**
     * look up an application against the server
     *
     * @param name name of app
     * @return URI of the app
     */
    public URI lookupApplication(String name) throws RemoteException {
        return lookupApplication(makeName(name));
    }


    /**
     * exit, use success flag to choose the return time. This method does not
     * return
     *
     * @param success success flag
     */
    protected static void exit(boolean success) {
        Runtime.getRuntime().exit(success ? 0 : -1);
    }
}
