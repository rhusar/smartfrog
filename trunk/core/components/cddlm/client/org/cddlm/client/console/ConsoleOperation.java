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

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.cddlm.client.common.Constants;
import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.generated.api.endpoint.CddlmSoapBindingStub;
import org.cddlm.client.generated.api.types.ApplicationReferenceListType;
import org.cddlm.client.generated.api.types.ApplicationStatusType;
import org.cddlm.client.generated.api.types.CallbackInformationType;
import org.cddlm.client.generated.api.types.DeploymentDescriptorType;
import org.cddlm.client.generated.api.types.EmptyElementType;
import org.cddlm.client.generated.api.types.JsdlType;
import org.cddlm.client.generated.api.types.OptionMapType;
import org.cddlm.client.generated.api.types.OptionType;
import org.cddlm.client.generated.api.types.ServerStatusType;
import org.cddlm.client.generated.api.types.StaticServerStatusType;
import org.cddlm.client.generated.api.types._applicationStatusRequest;
import org.cddlm.client.generated.api.types._deployRequest;
import org.cddlm.client.generated.api.types._deployResponse;
import org.cddlm.client.generated.api.types._deploymentDescriptorType_data;
import org.cddlm.client.generated.api.types._languageListType_language;
import org.cddlm.client.generated.api.types._lookupApplicationRequest;
import org.cddlm.client.generated.api.types._serverStatusRequest;
import org.cddlm.client.generated.api.types._undeployRequest;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    public ApplicationStatusType lookupApplicationStatus(URI app)
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
    public ApplicationStatusType lookupApplicationStatus(String app)
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
    public ApplicationStatusType lookupApplicationStatus(NCName app)
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
     * @param callbackInfo
     * @return
     * @throws RemoteException
     */
    public URI deploy(String name,
            DeploymentDescriptorType descriptor,
            Options options,
            CallbackInformationType callbackInfo)
            throws RemoteException {
        JsdlType jsdl = new JsdlType();
        if (name != null) {
            //name processing
            if (options == null) {
                options = new Options();
            }
            addNameOption(options, name);
        }
        OptionMapType map = null;
        if (options != null) {
            map = options.toOptionMap();
        }
        _deployRequest request = new _deployRequest(jsdl,
                descriptor,
                callbackInfo,
                map);
        _deployResponse response = getStub().deploy(request);
        return response.getApplicationReference();

    }

    /**
     * add a name option to our options
     *
     * @param options
     * @param name
     * @throws RuntimeException if we cannot turn {@link DeployApiConstants#OPTION_NAME}
     *                          into a URI
     */
    public static void addNameOption(Options options, String name) {
        try {
            OptionType o = options.createNamedOption(
                    new URI(DeployApiConstants.OPTION_NAME), true);
            o.setString(name);
        } catch (URI.MalformedURIException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param source
     * @return
     * @throws IOException
     */
    public DeploymentDescriptorType createSmartFrogDescriptor(String source)
            throws IOException {
        MessageElement element = createSmartfrogMessageElement(source);
        DeploymentDescriptorType descriptor = createDescriptorWithXML(element);
        return descriptor;
    }

    public DeploymentDescriptorType createDescriptorWithXML(
            MessageElement element) {
        MessageElement any[] = new MessageElement[1];
        any[0] = element;
        DeploymentDescriptorType descriptor = createDescriptorWithXML(any);
        return descriptor;
    }

    public DeploymentDescriptorType createDescriptorWithXML(
            MessageElement[] any) {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        _deploymentDescriptorType_data data = new _deploymentDescriptorType_data();
        data.set_any(any);
        descriptor.setData(data);
        return descriptor;
    }

    public MessageElement createSmartfrogMessageElement(String source) {
        MessageElement element = new MessageElement(
                Constants.SMARTFROG_NAMESPACE,
                Constants.SMARTFROG_ELEMENT_NAME);
        element.addAttribute(Constants.SMARTFROG_NAMESPACE,
                Constants.SMARTFROG_ELEMENT_VERSION_ATTR,
                SMARTFROG_VERSION);
        Text text = new Text(source);
        element.appendChild(text);
        return element;
    }


    /**
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param in input stream
     * @return a deployment descriptor for smartfrog
     * @throws IOException
     */
    public DeploymentDescriptorType createSmartFrogDescriptor(InputStream in)
            throws IOException {
        String source = readIntoString(in);
        return createSmartFrogDescriptor(source);
    }

    /**
     * helper to read into a string
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String readIntoString(InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        StringBuffer buffer = new StringBuffer();
        char[] block = new char[1024];
        int read;
        while (((read = reader.read(block)) >= 0)) {
            buffer.append(block);
        }
        return buffer.toString();
    }

    /**
     * turn a string into an NC name
     *
     * @param name
     * @return
     */
    public NCName makeName(String name) {
        return new NCName(name);
    }

    /**
     * look up an application against the server
     *
     * @param ncname name of app
     * @return URI of the app
     */
    public URI lookupApplication(NCName ncname) throws RemoteException {
        _lookupApplicationRequest request = new _lookupApplicationRequest(
                ncname);
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
     * initiate an undeployment
     *
     * @param uri
     * @param reason
     * @return true if the process has commenced. Undeployment is asynchronous
     * @throws RemoteException
     */
    public boolean undeploy(URI uri, String reason) throws RemoteException {
        _undeployRequest undeploy = new _undeployRequest(uri, reason);
        return getStub().undeploy(undeploy);
    }

    /**
     * test for a language being supported
     *
     * @param languageURI
     * @return true if the URI is in the list of known languages
     */
    public boolean supportsLanguage(String languageURI) throws RemoteException {
        ServerStatusType status = getStatus();
        return supportsLanguage(status, languageURI);
    }

    /**
     * test for a language being supported
     *
     * @param status      server status
     * @param languageURI
     * @return true if the URI is in the list of known languages
     */
    public boolean supportsLanguage(ServerStatusType status,
            String languageURI) {
        StaticServerStatusType staticStatus = status.get_static();
        _languageListType_language[] languages = staticStatus.getLanguages()
                .getLanguage();
        for (int i = 0; i < languages.length; i++) {
            _languageListType_language l = languages[i];
            org.apache.axis.types.URI nsURI = l.getNamespace();
            if (languageURI.equals(nsURI.toString())) {
                //positive match
                return true;
            }
        }
        //if we get here, no match
        return false;
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


    /**
     * print out a fault
     *
     * @param exception
     */
    public static void processThrowableInMain(Throwable exception,
            PrintWriter out) {
        if (exception instanceof BadCommandLineException) {
            out.println(exception.getMessage());
        } else {
            exception.printStackTrace(out);
        }
    }

    /**
     * get the first non null element; set it to null
     *
     * @param args
     * @return null for no match
     */
    public static String getFirstNonNullElement(final String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                String elt = args[i];
                args[i] = null;
                return elt;
            }
        }
        return null;
    }
}
