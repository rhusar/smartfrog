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
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMFactory;
import org.apache.ws.commons.om.impl.llom.builder.StAXOMBuilder;
import org.smartfrog.services.deployapi.binding.NuxStaxBuilder;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * created 20-Sep-2005 17:07:38
 */

public class Utils {

    protected Utils() {
    }


    /**
     * Turn a java qname into a ggf qualifiedname
     *
     * @param in
     * @return a converted qname
     */
    public static QName convert(QName in) {
        return in;
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

    /**
     * Create an OMElement from the QName
     * @param qname element qname
     * @return
     */
    public static OMElement createOmElement(QName qname) {
        return createOmElement(
                qname.getNamespaceURI(), qname.getLocalPart(),qname.getPrefix());
    }

    /**
     * Create an om element from the infividual elements of the qname.
     * @param namespace
     * @param local
     * @param prefix
     * @return
     */
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
        try {
            Document document;
            XMLStreamReader reader = em.getXMLStreamReader();
            NuxStaxBuilder builder=new NuxStaxBuilder();
            document = builder.build(reader);
            return document;
        } catch (XMLStreamException e) {
            throw FaultRaiser.raiseInternalError("converting object models",e);
        }
    }


    /**
     * Convert a Xom graph to Axiom. The Element is detached from its parents and placed
     * at the base of a new doc in the process; this is not a zero-side-effect operation
     * @param element
     * @return an Axom equivalent
     */
    public static OMElement xomToAxiom(Element element)  {
        element.detach();
        Document document=new Document(element);
        return xomToAxiom(document);
    }

    /**
     *
     * @param document
     * @return
     */
    public static OMElement xomToAxiom(Document document) {
        try {
            byte[] buffer = XomHelper.xomToBuffer(document);
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

    /**
     * Close any open stream; ignore any errors.
     * @param stream
     */
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
