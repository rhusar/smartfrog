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

package org.smartfrog.services.deployapi.binding;

import static org.smartfrog.services.deployapi.system.Constants.*;
import org.ggf.xbeans.cddlm.api.DescriptorType;
import org.ggf.xbeans.cddlm.api.InitializeRequestDocument;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.system.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Serializer;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.ParsingException;

/**

 */
public class DescriptorHelper extends FaultRaiser {

    File tempDir = null;
    public static final String ERROR_NO_DESCRIPTOR = "No deployment descriptor";
    public static final String ERROR_UNSUPPORTED_PROTOCOL = "remote descriptors with protocol ";
    public static final String DESCRIPTOR = "descriptor";
    public static final String BODY = "body";
    public static final String LANGUAGE = "language";
    public static final String REFERENCE = "reference";
    public static final String API = "api:";
    public static final String ERROR_NO_FILE = "Missing/unreachable file ";
    private static final String TNS = CDL_API_TYPES_NAMESPACE;
    private static final String ERROR_NO_LANGUAGE_ATTR = "No language specified";
    private static final String ERROR_BOTH_OPTIONS = "Both reference and body elements supplied -only one is allowed";

    /**
     * descriptor helper
     *
     * @param tempDir can be null for system temp dir
     */
    public DescriptorHelper(File tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * create a temp file
     *
     * @param type "sf", "cdl" etc.
     * @return a temp file
     * @throws IOException
     */
    public File createTempFile(String type) throws IOException {
        return File.createTempFile("deploy", "." + type, tempDir);
    }

    /**
     * @deprecated 
     * @param request
     * @return
     * @throws IOException
     */
    public File extractBodyToFile(InitializeRequestDocument.InitializeRequest request)
            throws IOException {
        DescriptorType descriptorNode = request.getDescriptor();
        if (descriptorNode.isSetReference()) {
            return retrieveRemoteReference(descriptorNode.getReference());
        } else {
            return saveBodyToTempFile(descriptorNode);
        }
    }


    /**
     * @deprecated
     * @param descriptorNode
     * @return
     * @throws IOException
     */
    private File saveBodyToTempFile(DescriptorType descriptorNode) throws
            IOException {
        if (descriptorNode.isSetBody()) {
            File tempfile = createTempFile("xml");
            DescriptorType.Body body = descriptorNode.getBody();

            body.save(tempfile);
            return tempfile;
        } else {
            throw FaultRaiser.raiseBadArgumentFault(ERROR_NO_DESCRIPTOR);
        }
    }

    /**
     * Extract the body to a file
     * @param request
     * @return
     * @throws IOException
     */
    public File extractBodyToFile(Element request)
            throws IOException {
        Element descriptor = request.getFirstChildElement(DESCRIPTOR, TNS);
        Attribute language = descriptor.getAttribute(LANGUAGE, TNS);
        if (language == null) {
            throw raiseBadArgumentFault(ERROR_NO_LANGUAGE_ATTR);
        }
        Element reference = descriptor.getFirstChildElement(REFERENCE, TNS);
        Element body = descriptor.getFirstChildElement(BODY, TNS);
        if (reference != null) {
            if (body != null) {
                throw raiseBadArgumentFault(ERROR_BOTH_OPTIONS);
            }
            return retrieveRemoteReference(reference.getValue());
        } else {
            return saveBodyToTempFile(body, false);
        }
    }

    /**
     * Get the request as XML. 
     * This is always an descriptor with a language attr round the outside,
     * even if we have loaded from a file
     * @param request
     * @return the XML
     */
    public Element extractDescriptorAsXML(Element request) throws IOException,
            ParsingException {
        Element descriptor = request.getFirstChildElement(DESCRIPTOR, TNS);
        Attribute language = descriptor.getAttribute(LANGUAGE, TNS);
        if (language == null) {
            throw raiseBadArgumentFault(ERROR_NO_LANGUAGE_ATTR);
        }
        Element reference = descriptor.getFirstChildElement(REFERENCE, TNS);
        Element body = descriptor.getFirstChildElement(BODY, TNS);
        if (reference != null) {
            if (body != null) {
                throw raiseBadArgumentFault(ERROR_BOTH_OPTIONS);
            }
            //get the file
            File file = retrieveRemoteReference(reference.getValue());
            //then load it inline. slick :)
            return loadInlineDescriptor(file,language.getValue());

        } else {
            //no need to save and load, just hand back the descriptor
            return descriptor;
        }
    }

    /**
     * Save the body to a temp file
     * The body is detached in the process
     * @param body
     * @param savecopy
     * @return
     * @throws IOException
     */

    public File saveBodyToTempFile(Element body, boolean savecopy) throws
            IOException {
        File tempfile = createTempFile("xml");
        FileOutputStream fileout = new FileOutputStream(tempfile);
        OutputStream out;
        out = new BufferedOutputStream(fileout);
        try {
            Serializer serializer = new Serializer(out);
            if(savecopy) {
                body=(Element) body.copy();
            } else {
                body.detach();
            }
            Document doc = new Document(body);
            serializer.write(doc);
            serializer.flush();
        } finally {
            out.close();
        }
        return tempfile;
    }

    /**
     * Create a blank initialisation request
     *
     * @return
     */
    public Element createInitRequest() {
        Element request = apiElement(API_ELEMENT_INITALIZE_REQUEST);
        return request;
    }

    /**
     * Create an init request with a given descriptor
     * @param descriptor
     * @return
     */
    public Element createInitRequest(Element descriptor) {
        Element request = createInitRequest();
        request.appendChild(descriptor);
        return request;
    }


    public File retrieveRemoteReference(String reference) throws IOException {
        URL url = new URL(reference);
        String protocol = url.getProtocol();
        if (!("file".equals(protocol))) {
            throw FaultRaiser.raiseNotImplementedFault(
                    ERROR_UNSUPPORTED_PROTOCOL +
                            protocol);
        }
        File file = new File(url.getPath());
        if (!file.exists()) {
            throw FaultRaiser.raiseBadArgumentFault(ERROR_NO_FILE + file);
        }
        return file;
    }

    /**
     * Create an inline descriptor from an XML document
     * @param xml
     * @param language
     * @return
     */
    public Element createInlineDescriptor(
            Element xml,
            String language) {
        Element descriptor = createDescriptorElement(language);
        Element body = apiElement(BODY);
        descriptor.appendChild(body);
        xml.detach();
        body.appendChild(xml);
        return descriptor;
    }

    /**
     * Load a descriptor (inline) from a file containing XML. 
     * Loaded via a non-validating parser
     * @param file
     * @param language
     * @return api:descriptor/api:body/*
     * @throws ParsingException
     * @throws IOException
     */
    public Element loadInlineDescriptor(File file,
                                        String language) throws
            ParsingException, IOException {
        Document document = loadDocument(file);
        return inlineDescriptorFromDocument(document, language);
    }

    private Document loadDocument(File file) throws ParsingException,
            IOException {
        Builder builder=new Builder(false);
        Document document = builder.build(file);
        return document;
    }

    private Element inlineDescriptorFromDocument(Document document,
                                                 String language) {
        Element rootElement = Utils.detachRootElement(document);
        return createInlineDescriptor(rootElement, language);
    }

    /**
     * Load a descriptor from an input stream
     * @param in
     * @param language
     * @return api:descriptor/api:body/*
     * @throws ParsingException
     * @throws IOException
     */
    public Element loadInlineDescriptor(InputStream in,
                                        String language) throws
            ParsingException, IOException {
        try {
            Builder builder = new Builder(false);
            Document document = builder.build(in);
            return inlineDescriptorFromDocument(document, language);
        } finally {
            in.close();
        }
    }


    /**
     * Create a descriptor containing a reference
     * @param url
     * @param language
     * @return
     */
    public Element createReferenceXomDescriptor(String url, String language) {
        Element descriptor = createDescriptorElement(language);
        String name = "reference";
        Element reference = apiElement(name);
        reference.appendChild(url);
        descriptor.appendChild(reference);
        return descriptor;
    }

    private static Element apiElement(String name) {
        return new Element(API + name,
                TNS);
    }

    private Element createDescriptorElement(String language) {
        Element descriptor = apiElement(DESCRIPTOR);
        String name = LANGUAGE;
        addApiAttr(descriptor, name, language);
        return descriptor;
    }

    private void addApiAttr(Element element, String name, String value) {
        Attribute attribute = new Attribute(API + name,
                TNS,
                value);
        element.addAttribute(attribute);
    }

    public Element createSmartFrogReferenceDescriptor(String url) {
        return createReferenceXomDescriptor(url, SMARTFROG_NAMESPACE);
    }

    public Element createSmartFrogInlineDescriptor(File file)
            throws IOException,
            ParsingException {
        String contents = loadSmartFrogFile(file);
        return createSmartFrogInlineDescriptor(contents);
    }

    public Element createSmartFrogInlineDescriptor(String contents) {
        Element element = new Element(
                "sf:"+ SMARTFROG_ELEMENT_NAME,
                SMARTFROG_NAMESPACE);
        Attribute version= new Attribute(
                "sf:" +SMARTFROG_ELEMENT_VERSION_ATTR,
                SMARTFROG_NAMESPACE,
                SMARTFROG_VERSION);
        element.addAttribute(version);
        element.appendChild(contents);
        return createInlineDescriptor(element, SMARTFROG_NAMESPACE);
    }


    private String loadSmartFrogFile(File file) throws IOException {
        String contents=Utils.loadFile(file, CHARSET_SF_FILE);
        return contents;
    }
    
    public Element createCDLReferenceDescriptor(String url) {
        return createReferenceXomDescriptor(url, XML_CDL_NAMESPACE);
    }

    public Element createCDLInlineDescriptor(File file) throws IOException,
            ParsingException {
        return loadInlineDescriptor(file, XML_CDL_NAMESPACE);
    }


}

/**
 * wrap a string with a smartfrog deploy descriptor
 *
 * @param source
 * @return
 * @throws java.io.IOException
 */
/*

    public Element createSmartFrogDescriptor(String source)
            throws IOException {
        Element element = createSmartfrogMessageElement(source);
        DeploymentDescriptorType descriptor = createDescriptorWithXML(element,
                new URI(Constants.SMARTFROG_NAMESPACE),
                null);
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


