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

import nu.xom.ParsingException;
import org.apache.axis.AxisFault;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.cddlm.client.common.Constants;
import org.cddlm.client.common.ServerBinding;
import org.smartfrog.services.cddlm.api.CallbackInfo;
import org.smartfrog.services.cddlm.cdl.CdlDocument;
import org.smartfrog.services.cddlm.cdl.CdlParser;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;
import org.smartfrog.services.cddlm.cdl.XomAxisHelper;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.endpoint.CddlmSoapBindingStub;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationReferenceListType;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.CreateRequest;
import org.smartfrog.services.cddlm.generated.api.types.CreateResponse;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorTypeBody;
import org.smartfrog.services.cddlm.generated.api.types.EmptyElementType;
import org.smartfrog.services.cddlm.generated.api.types.JsdlType;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListTypeLanguage;
import org.smartfrog.services.cddlm.generated.api.types.LookupApplicationRequest;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;
import org.smartfrog.services.cddlm.generated.api.types.OptionMapType;
import org.smartfrog.services.cddlm.generated.api.types.OptionType;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.SetNotificationRequest;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.TerminateRequest;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    protected URI uri;
    public static final String NO_URI_FOUND = "No application URI";
    public static final String INVALID_URI = "Invalid URI:";

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
    public abstract void execute() throws IOException;

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
        } catch (Exception e) {
            //logThrowable(e);
            processThrowableInMain(e, out);
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
        ApplicationStatusRequest request = new ApplicationStatusRequest();
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
        ServerStatusRequest request = new ServerStatusRequest();
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
            NotificationInformationType callbackInfo)
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
        CreateRequest request = new CreateRequest(jsdl,
                descriptor,
                callbackInfo,
                map);
        CreateResponse response = getStub().create(request);
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
        DeploymentDescriptorType descriptor = createDescriptorWithXML(element,
                new URI(DeployApiConstants.SMARTFROG_NAMESPACE),
                null);
        return descriptor;
    }


    /**
     * wrap the element parameter in a MessageElement[] array and then hand off
     * to {@link #createDescriptorWithXML(MessageElement[], URI, String)}
     *
     * @param element
     * @return a deployment descriptor for use in a request
     */
    public DeploymentDescriptorType createDescriptorWithXML(
            MessageElement element,
            URI language,
            String version) {
        MessageElement any[] = new MessageElement[1];
        any[0] = element;
        DeploymentDescriptorType descriptor = createDescriptorWithXML(any,
                language,
                version);
        return descriptor;
    }

    /**
     * fill the descriptor element with some attached XML
     *
     * @param any an array of data. The size of the array should be 1 for
     *            correct operation.
     * @return a deployment descriptor for use in a request
     */
    public DeploymentDescriptorType createDescriptorWithXML(
            MessageElement[] any,
            URI language,
            String version) {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        DeploymentDescriptorTypeBody data = new DeploymentDescriptorTypeBody();
        data.set_any(any);
        descriptor.setBody(data);
        descriptor.setLanguage(language);
        descriptor.setVersion(version);
        return descriptor;
    }


    /**
     * jump through hoops to turn a Xom document into a descriptor Caller is
     * left to set the language and version attributes
     *
     * @param xom
     * @return
     * @throws ParserConfigurationException
     */
    public DeploymentDescriptorType createDescriptorWithXom(
            nu.xom.Document xom)
            throws ParserConfigurationException {
        DOMImplementation impl = XomAxisHelper.loadDomImplementation();
        MessageElement messageElement = XomAxisHelper.convert(xom, impl);
        return createDescriptorWithXML(messageElement, null, null);
    }

    /**
     * wrap a smartfrog text file into a message element and process it
     *
     * @param source
     * @return
     */
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
     * load a resource, make a CDL descriptor from it. The file can be validated
     * before sending
     *
     * @param resource
     * @return
     */
    public DeploymentDescriptorType createDescriptorFromCdlResource(
            String resource,
            boolean validate) throws SAXException, IOException,
            ParsingException, ParserConfigurationException {
        ResourceLoader loader = new ResourceLoader(this.getClass());
        CdlParser parser = new CdlParser(loader, validate);
        CdlDocument cdlDoc = parser.parseResource(resource);
        if (validate) {
            cdlDoc.validate();
        }
        return createDescriptorWithXom(cdlDoc.getDocument());
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
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param file file to load into the descriptor
     * @return a deployment descriptor for smartfrog
     * @throws IOException
     */
    public DeploymentDescriptorType createSmartFrogDescriptor(File file)
            throws IOException {
        String source = readIntoString(file);
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
        StringWriter dest = new StringWriter();
        char[] block = new char[1024];
        int read;
        while (((read = reader.read(block)) >= 0)) {
            dest.write(block, 0, read);
        }
        dest.flush();
        return dest.toString();
    }

    /**
     * helper to read into a string
     *
     * @param file file to read
     * @return
     * @throws IOException
     */
    public static String readIntoString(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            String source = readIntoString(in);
            return source;
        } finally {
            try {
                in.close();
            } catch (IOException e) {

            }
        }
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
        LookupApplicationRequest request = new LookupApplicationRequest(ncname);
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
     * @param application
     * @param reason
     * @return true if the process has commenced. Undeployment is asynchronous
     * @throws RemoteException
     */
    public boolean terminate(URI application, String reason)
            throws RemoteException {
        TerminateRequest undeploy = new TerminateRequest(application,
                reason);
        return getStub().terminate(undeploy);
    }

    /**
     * set any callback
     *
     * @param request
     * @return true
     * @throws RemoteException
     */
    public boolean setNotification(SetNotificationRequest request)
            throws RemoteException {
        return getStub().setNotification(request);
    }

    /**
     * set a CDDLM callback to a given endpoint
     *
     * @param application app identifier
     * @param url         endpoint for return messages
     * @param identifier  optional identifier
     * @return
     */
    public boolean setCddlmNotification(URI application, String url,
            String identifier)
            throws RemoteException {
        CallbackInfo info = new CallbackInfo();
        info.setAddress(url);
        info.setIdentifier(identifier);
        NotificationInformationType notification;
        notification = info.createCallback();
        SetNotificationRequest request = new SetNotificationRequest(
                application,
                notification);
        return setNotification(request);
    }

    /**
     * unsubscribe from any callback
     *
     * @param application app identifier
     * @return
     */
    public boolean setUnsubscribeCallback(URI application)
            throws RemoteException {
        SetNotificationRequest request = new SetNotificationRequest(
                application,
                null);
        try {
            return setNotification(request);
        } catch (AxisFault e) {
            if (DeployApiConstants.FAULT_NO_SUCH_APPLICATION.equals(
                    e.getFaultCode())) {
                //do nothing, as this is a common event
                return false;
            } else {
                //anything else
                throw e;
            }
        }
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
        LanguageListTypeLanguage[] languages = staticStatus.getLanguages()
                .getLanguage();
        for (int i = 0; i < languages.length; i++) {
            LanguageListTypeLanguage l = languages[i];
            org.apache.axis.types.URI nsURI = l.getUri();
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

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * assume first non-empty command line (after the server binding) is a URI;
     * extract it and set our URI value
     *
     * @param args
     */
    protected void bindUriToCommandLine(String[] args) {
        String appURI = getFirstNonNullElement(args);
        if (appURI == null) {
            throw new BadCommandLineException(NO_URI_FOUND);
        }
        try {
            uri = new URI(appURI);
        } catch (URI.MalformedURIException e) {
            throw new BadCommandLineException(INVALID_URI + appURI);
        }
    }
}
