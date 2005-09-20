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

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMXMLParserWrapper;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMFactory;
import org.apache.axis2.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.apache.axis2.clientapi.StreamWrapper;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;

/**
 * Convert axis2 stuff into a an XMLBean
 *
 */
public class Axis2Beans<T extends XmlObject>  {

    /**
     * generation-time options
     */
    private XmlOptions options;

    /**
     * Error when things are of the wrong type
     */
    public static final String ERROR_WRONG_TYPE = "Cannot convert XML because it is of the incompatible type ";

    public Axis2Beans() {
    }

    public Axis2Beans(XmlOptions options) {
        this.options = options;
    }


    public T convert(OMElement element) {
        XMLStreamReader reader = element.getXMLStreamReader();
        try {
            XmlObject xmlObject = T.Factory.parse(reader);
            T t;
            try {
                t=(T)xmlObject;
            } catch (ClassCastException e) {
                throw new BaseException(ERROR_WRONG_TYPE +xmlObject);

            }

            return t;

        } catch (XmlException e) {
            throw new BaseException(e);
        }
    }


    /**
     * Create an instance of this document
     * @return
     */
    public T createInstance() {
        return (T)T.Factory.newInstance();
    }


    /**
     * Convert a message to the body of the response.
     * @param document
     * @return a converted file
     */
    public OMElement convert(T document)  {
        return convertIntermediateDoc(document);
        //return convertDirect(document);
    }

    private OMElement convertIntermediateDoc(T document) {
        try {
            StringWriter writer = new StringWriter();
            XmlOptions writeOptions = new XmlOptions();
            writeOptions.setSaveOuter();
            writeOptions.setSaveUseOpenFrag();
            document.save(writer,options);
            writer.close();
            String textForm=writer.toString();
            StringReader stringReader = new StringReader(textForm);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader parser = inputFactory.createXMLStreamReader(stringReader);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            return builder.getDocumentElement();
        } catch (IOException e) {
            throw new BaseException(e);
        } catch (XMLStreamException e) {
            throw new BaseException(e);
        }
    }

    /**
     * Convert a message to the body of the response.
     * @param document
     * @return a converted file
     */
    protected OMElement convertDirect(T document) {
        XmlOptions writeOptions = new XmlOptions();
        //writeOptions.setSaveOuter();
        writeOptions.setSaveUseOpenFrag();
        XMLStreamReader reader = document.newXMLStreamReader(writeOptions);
        StreamWrapper streamWrapper = new StreamWrapper(reader);
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createStAXOMBuilder(getOMFactory(), streamWrapper);
        //get the root element (in this case the envelope)
        OMElement elt = builder.getDocumentElement();
        elt.build();
        return elt;
    }

    private OMXMLParserWrapper createBuilder(XMLStreamReader reader) {
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createStAXOMBuilder(getOMFactory(), reader);
        return builder;
    }

    private OMFactory getOMFactory() {
        return OMAbstractFactory.getOMFactory();
    }

    public T loadBeansFromResource(String resource) throws XmlException, IOException {
        InputStream stream = loadResource(resource);
        return (T) T.Factory.parse(stream);
    }

    private InputStream loadResource(String resource) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new BaseException("Resource missing: " + resource);
        }
        return stream;
    }
}
