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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import org.smartfrog.services.deployapi.system.Constants;
import static org.smartfrog.services.deployapi.system.Constants.API_ELEMENT_INITALIZE_REQUEST;
import static org.smartfrog.services.deployapi.system.Constants.CHARSET_SF_FILE;
import static org.smartfrog.services.deployapi.system.Constants.SMARTFROG_ELEMENT_NAME;
import static org.smartfrog.services.deployapi.system.Constants.SMARTFROG_ELEMENT_VERSION_ATTR;
import static org.smartfrog.services.deployapi.system.Constants.SMARTFROG_NAMESPACE;
import static org.smartfrog.services.deployapi.system.Constants.SMARTFROG_XML_VERSION;
import static org.smartfrog.services.deployapi.system.Constants.XML_CDL_NAMESPACE;
import org.smartfrog.services.deployapi.system.DeploymentLanguage;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import static org.smartfrog.services.deployapi.transport.faults.FaultRaiser.checkArg;
import static org.smartfrog.services.deployapi.transport.faults.FaultRaiser.raiseBadArgumentFault;
import static org.smartfrog.services.deployapi.transport.faults.FaultRaiser.raiseUnsupportedLanguageFault;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**

 */
public class DescriptorHelper extends XomHelper {

    File tempDir = null;
    public static final String ERROR_NO_DESCRIPTOR = "No deployment descriptor";
    public static final String ERROR_UNSUPPORTED_PROTOCOL = "remote descriptors with protocol ";
    public static final String DESCRIPTOR = "descriptor";
    public static final String BODY = "body";
    public static final String LANGUAGE = "language";
    public static final String REFERENCE = "reference";
    public static final String ERROR_NO_FILE = "Missing/unreachable file ";
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
        if(type.charAt(0)!='.') {
            type= "." + type;
        }
        return File.createTempFile("deploy", type, tempDir);
    }


    /**
     * Extract the body to a file
     * @param request
     * @param extension
     * @return
     * @throws IOException
     */
    public File extractBodyToFile(Element request, String extension)
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
            return retrieveRemoteReference(reference.getValue(), extension);
        } else {
            Elements children=body.getChildElements();
            if(children.size()!=1) {
                throw raiseBadArgumentFault("Invalid content in the "
                        +BODY
                        +" element of the deployment request");
            }
            Element content=children.get(0);
            File file = saveElementToTempFile(content, false, extension);
            file.deleteOnExit();
            return file;
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
            File file = retrieveRemoteReference(reference.getValue(), "xml");
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
     * @param element
     * @param savecopy
     * @param extension
     * @return
     * @throws IOException
     */

    public File saveElementToTempFile(Element element,
                                      boolean savecopy,
                                      String extension) throws
            IOException {
        File tempfile = createTempFile(extension);
        FileOutputStream fileout = new FileOutputStream(tempfile);
        OutputStream out;
        out = new BufferedOutputStream(fileout);
        try {
            Serializer serializer = new Serializer(out);
            if(savecopy) {
                element =(Element) element.copy();
            } else {
                element.detach();
            }
            Document doc = new Document(element);
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


    public File retrieveRemoteReference(String reference, String extension) throws IOException {
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
    public SoapElement createInlineDescriptor(
            Element xml,
            String language) {
        SoapElement descriptor = createDescriptorElement(language);
        SoapElement body = apiElement(BODY);
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
    public SoapElement loadInlineDescriptor(File file,
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

    private SoapElement inlineDescriptorFromDocument(Document document,
                                                 String language) {
        Element rootElement = detachRootElement(document);
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
    public SoapElement createReferenceXomDescriptor(String url, String language) {
        SoapElement descriptor = createDescriptorElement(language);
        SoapElement reference = apiElement("reference",url);
        descriptor.appendChild(reference);
        return descriptor;
    }

    private SoapElement createDescriptorElement(String language) {
        SoapElement descriptor = apiElement(DESCRIPTOR);
        addApiAttr(descriptor, LANGUAGE, language);
        return descriptor;
    }

    public SoapElement createSmartFrogReferenceDescriptor(String url) {
        return createReferenceXomDescriptor(url, SMARTFROG_NAMESPACE);
    }



    public SoapElement createSmartFrogInlineDescriptor(File file)
            throws IOException {
        String contents = loadSmartFrogFile(file);
        return createSmartFrogInlineDescriptor(contents);
    }


    /**
     * Create an inline request.
     *
     * @param language   URI of the language
     * @param descriptor a descriptor which must not have any parent.
     * @param options    a list of options, can be null
     * @return
     */
    public SoapElement createInitRequestInline(
            String language,
            Element descriptor, List<Element> options) {
        SoapElement body = XomHelper.apiElement("body", descriptor);
        SoapElement dt = XomHelper.apiElement("descriptor", body);
        if(language!=null) {
            XomHelper.addApiAttr(dt, "language", language);
        }
        return completeInitRequest(dt, options);
    }

    /**
     * finish off an init requset
     *
     * @param dt      descriptor type
     * @param options list of options, can be null
     * @return
     */
    private SoapElement completeInitRequest(SoapElement dt, List<Element> options) {
        SoapElement request;
        request = XomHelper.apiElement(API_ELEMENT_INITALIZE_REQUEST, dt);
        if (options != null) {
            //add any options
            SoapElement ot = XomHelper.apiElement("options");
            for (Element e : options) {
                ot.appendChild(e);
            }
            request.appendChild(ot);
        }
        return request;
    }


    public SoapElement createInitRequestURL(String language,
                                            String descriptorURL, List<Element> options) {
        SoapElement ref = XomHelper.apiElement("reference", descriptorURL);
        SoapElement dt = XomHelper.apiElement("descriptor", ref);
        if(language!=null) {
            XomHelper.addApiAttr(dt, "language", language);
        }
        return completeInitRequest(dt, options);
    }

    public SoapElement createSmartFrogInlineDescriptor(String contents) {
        SoapElement element = new SoapElement(
                "sf:"+ SMARTFROG_ELEMENT_NAME,
                SMARTFROG_NAMESPACE);
        Attribute version= new Attribute(
                "sf:" +SMARTFROG_ELEMENT_VERSION_ATTR,
                SMARTFROG_NAMESPACE,
                SMARTFROG_XML_VERSION);
        element.addAttribute(version);
        element.appendChild(contents);
        return createInlineDescriptor(element, SMARTFROG_NAMESPACE);
    }


    private String loadSmartFrogFile(File file) throws IOException {
        String contents=Utils.loadFile(file, CHARSET_SF_FILE);
        return contents;
    }

    public SoapElement createCDLReferenceDescriptor(String url) {
        return createReferenceXomDescriptor(url, XML_CDL_NAMESPACE);
    }

    public SoapElement createCDLInlineDescriptor(File file) throws IOException,
            ParsingException {
        return loadInlineDescriptor(file, XML_CDL_NAMESPACE);
    }

    public void validateRequest(Element request) {
        checkArg(API_ELEMENT_INITALIZE_REQUEST.equals(request.getLocalName()),
                "wrong root element");

        Elements sprogs = request.getChildElements(DESCRIPTOR, TNS);
        checkArg(sprogs.size()==1,"wrong number of descriptor elements:"+sprogs.size());

    }


    /** process a smartfrog deployment of type smartfrog XML */
    public File saveInlineSmartFrog(Element request) throws IOException {
        Nodes n = request.query("api:descriptor/api:body/sf:smartfrog",
                Constants.XOM_CONTEXT);
        if (n.size() == 0) {
            throw raiseBadArgumentFault("no sf:smartfrog element");
        }
        if (n.size() > 1) {
            throw raiseBadArgumentFault("too many sf:smartfrog elements");
        }
        Element sfnode = (Element) n.get(0);
        String version=sfnode.getAttributeValue(SMARTFROG_ELEMENT_VERSION_ATTR,
                SMARTFROG_NAMESPACE);
        if (!Constants.SMARTFROG_XML_VERSION.equals(version)) {
            raiseUnsupportedLanguageFault("Unsupported SmartFrog version:"+version);
        }

        String text = sfnode.getValue();
        File descriptorFile = createTempFile(DeploymentLanguage.smartfrog.getExtension());
        Utils.saveToFile(descriptorFile, text, Constants.CHARSET_SF_FILE);
        return descriptorFile;
    }

    /**
     * Create a complete SF request
     * @param body body of the request
     * @return the request
     */
    public Element createSFrequest(String body) {
        Element descriptor = createSmartFrogInlineDescriptor(body);
        Element request = createInitRequest(descriptor);
        validateRequest(request);
        return request;
    }


    /**
     * Create a complete SF request
     *
     * @param filename file to the request
     * @return the request
     */
    public Element createSFrequest(File filename) throws IOException {
        Element descriptor = createSmartFrogInlineDescriptor(filename);
        Element request = createInitRequest(descriptor);
        validateRequest(request);
        return request;
    }
}




