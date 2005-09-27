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

import org.apache.axis2.addressing.EndpointReference;
import org.ggf.xbeans.cddlm.api.CreateRequestDocument;
import org.ggf.xbeans.cddlm.api.CreateResponseDocument;
import org.ggf.xbeans.cddlm.api.DescriptorType;
import org.ggf.xbeans.cddlm.api.InitializeRequestDocument;
import org.ggf.xbeans.cddlm.api.LookupSystemRequestDocument;
import org.ggf.xbeans.cddlm.api.LookupSystemResponseDocument;
import org.ggf.xbeans.cddlm.api.OptionMapType;
import org.ggf.xbeans.cddlm.api.TerminateRequestDocument;
import org.ggf.xbeans.cddlm.api.InitializeResponseDocument;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyDocument;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyResponseDocument;
import org.ggf.cddlm.utils.QualifiedName;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.services.deployapi.binding.bindings.CreateBinding;
import org.smartfrog.services.deployapi.binding.bindings.InitializeBinding;
import org.smartfrog.services.deployapi.binding.bindings.LookupSystemBinding;
import org.smartfrog.services.deployapi.binding.bindings.TerminateBinding;
import org.smartfrog.services.deployapi.binding.bindings.GetResourcePropertyBinding;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;

import javax.xml.namespace.QName;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import nu.xom.Element;


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
    protected PortalEndpointer portal;


    public static final String SMARTFROG_VERSION = "1.0";
    protected URI uri;
    public static final String NO_URI_FOUND = "No application URI";
    public static final String INVALID_URI = "Invalid URI:";


    public ConsoleOperation(PortalEndpointer endpointer, PrintWriter out) {
        this.out = out;
        this.portal = endpointer;
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws java.rmi.RemoteException
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

    public PortalEndpointer getPortal() {
        return portal;
    }

    /**
     * @param args command line arguments; look for -url url
     * @return
     */
    public static PortalEndpointer extractBindingFromCommandLine(String[] args)
            throws IOException {
        PortalEndpointer extractedEndpointer = PortalEndpointer.fromCommandLine(args);
        if (extractedEndpointer == null) {
            extractedEndpointer = PortalEndpointer.createDefaultBinding();
        }
        return extractedEndpointer;
    }

    /**
     * list all applications
     *
     * @return
     * @throws java.rmi.RemoteException
     */
/*    public URI[] listApplications() throws RemoteException {
        EmptyElementType empty = new EmptyElementType();
        ApplicationReferenceListType list = getCall().listApplications(empty);
        URI apps[] = list.getApplication();
        if (apps == null) {
            apps = new URI[0];
        }
        return apps;
    }*/

    /**
     * get the status of an application
     *
     * @param app application identifier
     * @return
     * @throws java.rmi.RemoteException
     */
/*
    public ApplicationStatusType lookupApplicationStatus(URI app)
            throws RemoteException {
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setApplication(app);
        ApplicationStatusType status = getCall().applicationStatus(request);
        return status;
    }
*/

    /**
     * get the status of an application
     *
     * @param app application identifier
     * @return
     * @throws java.rmi.RemoteException
     */
/*    public ApplicationStatusType lookupApplicationStatus(String app)
            throws RemoteException {
        return lookupApplicationStatus(lookupApplication(app));
    }*/

    /**
     * get the status of an application
     *
     * @param app application identifier
     * @return
     * @throws java.rmi.RemoteException
     */
/*
    public ApplicationStatusType lookupApplicationStatus(NCName app)
            throws RemoteException {
        return lookupApplicationStatus(lookupApplication(app));
    }
*/

    /**
     * get the status
     *
     * @return
     * @throws java.rmi.RemoteException
     */
/*
    public ServerStatusType getStatus() throws RemoteException {
        ServerStatusRequest request = new ServerStatusRequest();
        ServerStatusType status = getCall().serverStatus(request);
        return status;
    }
*/


    /**
     * create an application
     *
     * @return info about a destination
     * @throws java.rmi.RemoteException
     */
    public SystemEndpointer create(String hostname)
            throws RemoteException {
        CreateBinding binding = new CreateBinding();
        CreateRequestDocument requestDoc = binding.createRequest();
        CreateRequestDocument.CreateRequest request = requestDoc.addNewCreateRequest();
        if (hostname != null) {
            request.setHostname(hostname);
        }
        CreateResponseDocument response =
                binding.invokeBlocking(portal, Constants.API_PORTAL_OPERATION_CREATE, requestDoc);
        SystemEndpointer createdSystem = new SystemEndpointer(response.getCreateResponse());
        return createdSystem;
    }

    /**
     * deploy a named application, or return an exception
     *
     * @param descriptor
     * @param options
     */
    public void initialize(SystemEndpointer system,
                           DescriptorType descriptor,
                           OptionMapType options)
            throws RemoteException {
        InitializeBinding binding = new InitializeBinding();
        if (options == null) {
            options = OptionMapType.Factory.newInstance();
        }
/*
            if (name != null) {
                //name processing
                //addNameOption(options, name);
            }
*/

        InitializeRequestDocument requestDoc = binding.createRequest();
        InitializeRequestDocument.InitializeRequest request = requestDoc.addNewInitializeRequest();
        request.setDescriptor(descriptor);
        request.setOptions(options);
        InitializeResponseDocument responseDoc = binding
                .invokeBlocking(system, Constants.API_ELEMENT_INITALIZE_REQUEST, requestDoc);
    }

    /**
     * Combine create and initialize into one operation
     *
     * @param hostname
     * @param descriptor
     * @param options
     * @return
     * @throws RemoteException
     */
    public SystemEndpointer deploy(String hostname, DescriptorType descriptor,
                                   OptionMapType options) throws RemoteException {
        SystemEndpointer systemEndpointer = create(hostname);
        initialize(systemEndpointer, descriptor, options);
        return systemEndpointer;
    }

    /**
     * add a name option to our options
     *
     * @param options
     * @param name
     * @throws RuntimeException if we cannot turn
     *                          into a URI
     */
/*
    public static void addNameOption(OptionType options, String name) {
        try {
            OptionType o = options.createNamedOption(
                    new URI(DeployApiConstants.OPTION_NAME), true);
            o.setString(name);
        } catch (URI.MalformedURIException e) {
            throw new RuntimeException(e);
        }
    }
*/

    /**
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param source
     * @return
     * @throws java.io.IOException
     */
/*
    public DeploymentDescriptorType createSmartFrogDescriptor(String source)
            throws IOException {
        MessageElement element = createSmartfrogMessageElement(source);
        DeploymentDescriptorType descriptor = createDescriptorWithXML(element,
                new URI(DeployApiConstants.SMARTFROG_NAMESPACE),
                null);
        return descriptor;
    }
*/

    /**
     * wrap the element parameter in a MessageElement[] array and then hand off
     * to #createDescriptorWithXML(MessageElement[], URI, String)
     *
     * @param element
     * @return a deployment descriptor for use in a request
     */
/*    public DeploymentDescriptorType createDescriptorWithXML(
            MessageElement element,
            URI language,
            String version) {
        MessageElement any[] = new MessageElement[1];
        any[0] = element;
        DeploymentDescriptorType descriptor = createDescriptorWithXML(any,
                language,
                version);
        return descriptor;
    }*/

    /**
     * fill the descriptor element with some attached XML
     *
     * @param any an array of data. The size of the array should be 1 for
     *            correct operation.
     * @return a deployment descriptor for use in a request
     */
/*
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
*/

    /**
     * jump through hoops to turn a Xom document into a descriptor Caller is
     * left to set the language and version attributes
     *
     * @param xom
     * @return
     * @throws javax.xml.parsers.ParserConfigurationException
     */
/*    public DeploymentDescriptorType createDescriptorWithXom(
            nu.xom.Document xom)
            throws ParserConfigurationException {
        DOMImplementation impl = XomAxisHelper.loadDomImplementation();
        MessageElement messageElement = XomAxisHelper.convert(xom, impl);
        return createDescriptorWithXML(messageElement, null, null);
    }*/

    /**
     * wrap a smartfrog text file into a message element and process it
     *
     * @param source
     * @return
     */
/*    public MessageElement createSmartfrogMessageElement(String source) {
        MessageElement element = new MessageElement(
                Constants.SMARTFROG_NAMESPACE,
                Constants.SMARTFROG_ELEMENT_NAME);
        element.addAttribute(Constants.SMARTFROG_NAMESPACE,
                Constants.SMARTFROG_ELEMENT_VERSION_ATTR,
                SMARTFROG_VERSION);
        Text text = new Text(source);
        element.appendChild(text);
        return element;
    }*/

    /**
     * load a resource, make a CDL descriptor from it. The file can be validated
     * before sending
     *
     * @param resource
     * @return
     */
/*    public DeploymentDescriptorType createDescriptorFromCdlResource(
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
    }*/

    /**
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param in input stream
     * @return a deployment descriptor for smartfrog
     * @throws java.io.IOException
     */
/*    public DeploymentDescriptorType createSmartFrogDescriptor(InputStream in)
            throws IOException {
        String source = readIntoString(in);
        return createSmartFrogDescriptor(source);
    }*/

    /**
     * wrap a string with a smartfrog deploy descriptor
     *
     * @param file file to load into the descriptor
     * @return a deployment descriptor for smartfrog
     * @throws java.io.IOException
     */
/*    public DeploymentDescriptorType createSmartFrogDescriptor(File file)
            throws IOException {
        String source = readIntoString(file);
        return createSmartFrogDescriptor(source);
    }*/

    /**
     * helper to read into a string
     *
     * @param in
     * @return
     * @throws java.io.IOException
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
     * @throws java.io.IOException
     */
    public static String readIntoString(File file) throws IOException {
        InputStream in =null;
        try {
            in= new BufferedInputStream(new FileInputStream(file));
            String source = readIntoString(in);
            return source;
        } finally {
            try {
                if(in!=null) { in.close(); }
            } catch (IOException e) {
                ///ignore
            }
        }
    }

    /**
     * turn a string into an NC name
     *
     * @param name
     * @return
     */
/*    public NCName makeName(String name) {
        return new NCName(name);
    }*/

    /**
     * look up an application against the server
     *
     * @param id id of app
     * @return URI of the app
     */

    public SystemEndpointer lookupSystem(String id) throws RemoteException {
        LookupSystemBinding binding = new LookupSystemBinding();
        LookupSystemRequestDocument requestDoc = binding.createRequest();
        LookupSystemRequestDocument.LookupSystemRequest request = requestDoc.addNewLookupSystemRequest();
        request.setResourceId(id);
        LookupSystemResponseDocument response = binding.invokeBlocking(portal,
                Constants.API_PORTAL_OPERATION_LOOKUPSYSTEM, requestDoc);
        EndpointReferenceType epr = response.getLookupSystemResponse();
        EndpointReference epr2 = EprHelper.Wsa2003ToEPR(epr);
        SystemEndpointer endpointer = new SystemEndpointer(epr2, id);
        return endpointer;
    }


    /**
     * Get a property from the destination
     * @return a Xom graph of the result
     * @throws RemoteException
     */
    public Element getPortalPropertyXom(QName property)
            throws RemoteException {
        GetResourcePropertyResponseDocument responseDoc = getPortalProperty(property);
        GetResourcePropertyResponseDocument.GetResourcePropertyResponse resp;
        resp = responseDoc.getGetResourcePropertyResponse();
        return Utils.BeanToXom(resp);
    }

    public GetResourcePropertyResponseDocument getPortalProperty(QName property) throws RemoteException {
        Endpointer endpoint = portal;
        return getPropertyResponse(endpoint, property);
    }

    public GetResourcePropertyResponseDocument getPropertyResponse(Endpointer endpoint, QName property) throws RemoteException {
        GetResourcePropertyBinding binding=new GetResourcePropertyBinding();
        GetResourcePropertyDocument request = binding.createRequest();
        request.setGetResourceProperty(property);
        GetResourcePropertyResponseDocument response;
        response = binding.invokeBlocking(endpoint, Constants.WSRF_OPERATION_GETRESOURCEPROPERTY, request);
        return response;
    }

    /**
     * Get a property from the destination
     * @return a Xom graph of the result
     * @throws RemoteException
     */
    public Element getPortalPropertyXom(QualifiedName property)
            throws RemoteException {
        return getPortalPropertyXom(Utils.convert(property));
    }

    public GetResourcePropertyResponseDocument getPortalProperty(QualifiedName property)
            throws RemoteException {
        return getPortalProperty(Utils.convert(property));
    }

    public GetResourcePropertyResponseDocument getResourceProperty(Endpointer endpoint,QualifiedName property)
            throws RemoteException {
        return getPropertyResponse(endpoint,Utils.convert(property));
    }
    /**
     * initiate an undeployment
     *
     * @param application
     * @param reason
     * @throws java.rmi.RemoteException
     */
    public void terminate(SystemEndpointer application, String reason)
            throws RemoteException {
        TerminateBinding binding = new TerminateBinding();
        TerminateRequestDocument request = binding.createRequest();
        TerminateRequestDocument.TerminateRequest terminateRequest = request.addNewTerminateRequest();
        if (reason != null) {
            terminateRequest.setReason(reason);
        }
        binding.invokeBlocking(application, Constants.API_SYSTEM_OPERATION_TERMINATE, request);
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
        } catch (URISyntaxException e) {
            throw new BadCommandLineException(INVALID_URI + appURI);
        }
    }
}
