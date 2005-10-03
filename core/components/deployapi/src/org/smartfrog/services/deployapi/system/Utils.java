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
package org.smartfrog.services.deployapi.system;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.converters.DOMConverter;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMFactory;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ggf.cddlm.utils.QualifiedName;
import org.ggf.xbeans.cddlm.cmp.DeploymentFaultType;
import org.smartfrog.services.deployapi.binding.NuxStaxBuilder;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;
import java.io.StringReader;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * created 20-Sep-2005 17:07:38
 */

public class Utils {

    protected Utils() {
    }

    /**
     * Turn a ggf qualifiedname into a proper java one
     *
     * @param in
     * @return a converted qname
     */
    public static QName convert(QualifiedName in) {
        return new QName(in.getNamespaceURI(), in.getLocalPart());
    }

    /**
     * Turn a java qname into a ggf qualifiedname
     *
     * @param in
     * @return a converted qname
     */
    public static QualifiedName convert(QName in) {
        return new QualifiedName(in.getNamespaceURI(), in.getLocalPart());
    }

    /**
     * We are invalid.
     *
     * @param message
     * @throws org.smartfrog.services.deployapi.transport.faults.DeploymentException
     *
     * @returns true for use in conditional code
     */
    public static boolean validate(XmlObject message) {
        ArrayList validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);
        if (!message.validate(validationOptions)) {
            message.dump();
            DeploymentFaultType fault = DeploymentFaultType.Factory.newInstance();
            throw FaultRaiser.raiseBadArgumentFault("XML did not validate against the schema");
        }
        return true;
    }

    /**
     * validate documents iff assertions are enabled
     *
     * @param message
     */
    public static void maybeValidate(XmlObject message) {
        assert validate(message);
    }

    /**
     * Turn a bean (which must map to an element, unless you like runtime exceptions
     * @param bean to convert
     * @return the converted doc
     */
    public static Element beanToXom(XmlObject bean) {
        Node dom = bean.getDomNode();
        org.w3c.dom.Element elt=(org.w3c.dom.Element)dom;
        return DOMConverter.convert(elt);
    }



    /**
     * create a new uuid-style id
     * @return
     */
    public static String createNewID() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        s = s.replace("-", "_");
        return "uuid_" + s;
    }

    public static OMElement createOmElement(QualifiedName qname) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement element = factory.createOMElement(qname.getLocalPart(),
            qname.getNamespaceURI(), qname.getPrefix());
        return element;
    }

    public static OMElement createOmElement(String namespace,String local,String prefix) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement element = factory.createOMElement(local,namespace, prefix);
        return element;
    }

    /**
     * convert from an axiom graph to Xom. 
     * Very efficient as it uses the StAX stuff underneath
     * @param em
     * @return
     * @throws BaseException if needed 
     */
    public static Document axiomToXom(OMElement em)  {
        Document document = null;
        try {
            XMLStreamReader reader = em.getXMLStreamReader();
            NuxStaxBuilder builder=new NuxStaxBuilder();
            document = builder.build(reader);
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("converting object models",e);
        }
        return document;
    }

    /**
     * detatch the root element from the doc, so it
     * can be used elsewhere. The doc is left with a dummy element
     * to avoid it being malformed.
     * @param document
     * @return element that was the root
     */
    public static Element detachRootElement(Document document) {
        Element rootElement = document.getRootElement();
        document.setRootElement(new Element("dummy"));
        return rootElement;
    }


    public static byte[] xomToBuffer(Document document) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        Serializer serializer=new Serializer(out);
        serializer.write(document);
        serializer.flush();
        out.close();
        return out.toByteArray();
    }

    public static OMElement xomToAxiom(Element element)  {
        element.detach();
        Document document=new Document(element);
        return xomToAxiom(document);
    }

    public static OMElement xomToAxiom(Document document) {
        try {
            byte[] buffer = xomToBuffer(document);
            return loadAxiomFromBuffer(buffer);
        } catch (IOException e) {
            throw FaultRaiser.raiseInternalError("doc conversion error",e);
        }
    }

    public static OMElement loadAxiomFromBuffer(byte[] buffer) {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader parser = inputFactory.createXMLStreamReader(in);
            return parseAxiomDoc(parser);
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("Parse failure",e);
        } finally {
            close(in);
        }
    }

    public static void close(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            //ignore
        }
    }

    public static OMElement loadAxiomFromString(String textForm) {
        try {
            StringReader stringReader = new StringReader(textForm);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader parser = inputFactory.createXMLStreamReader(stringReader);
            return parseAxiomDoc(parser);
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("Parse failure:"+
            textForm, e);
        }
    }

    private static OMElement parseAxiomDoc(XMLStreamReader parser) {
        StAXOMBuilder builder = new StAXOMBuilder(parser);
        return builder.getDocumentElement();
    }

    /**
     * Save text to a file
     * @param dest destination file
     * @param contents text to save
     * @param cs charset
     * @throws IOException
     */
    public static void saveToFile(File dest, String contents,Charset cs) throws IOException {
        OutputStream out=new BufferedOutputStream(new FileOutputStream(dest));
        Writer writer=new OutputStreamWriter(out,cs);
        try {
            writer.write(contents);
        } finally {
            writer.close();
        }
    }

    /**
     * Load a file with a given charset into a buffer
     * @param file
     * @param cs
     * @return
     * @throws IOException
     */
    public static String loadFile(File file, Charset cs) throws IOException {
        InputStream in;
        in = new FileInputStream(file);
        return loadInputStream(in, cs);
    }


    /**
     * Load an input stream
     * @param in
     * @param cs
     * @return
     * @throws IOException
     */
    public static String loadInputStream(InputStream in, Charset cs) throws
            IOException {
        BufferedInputStream buffIn = null;
        InputStreamReader reader = null;
        try {
            buffIn = new BufferedInputStream(in);
            reader = new InputStreamReader(buffIn,cs);
            StringWriter dest=new StringWriter();
            int ch;
            while((ch = reader.read())>=0) {
                dest.write(ch);
            }
            return dest.toString();
        } finally {
            close(reader);
        }
    }

    

    
    public static String toIsoTime(Date timestamp) {
        DateFormat format= makeIsoDateFormatter();
        return format.format(timestamp);

    }

    public static DateFormat makeIsoDateFormatter() {
        return new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}
