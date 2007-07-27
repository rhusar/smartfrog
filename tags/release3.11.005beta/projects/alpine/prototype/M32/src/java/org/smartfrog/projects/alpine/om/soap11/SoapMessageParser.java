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

package org.smartfrog.projects.alpine.om.soap11;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import org.smartfrog.projects.alpine.xmlutils.CatalogHandler;
import org.smartfrog.projects.alpine.xmlutils.ParserHelper;
import org.smartfrog.projects.alpine.xmlutils.ResourceLoader;
import org.smartfrog.projects.alpine.om.ExtendedNodeFactory;
import org.smartfrog.projects.alpine.om.FactoryChain;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**

 */
public class SoapMessageParser {

    /**
     * logic for resource loading
     */
    private ResourceLoader resourceLoader;

    /**
     * builder class
     */
    private Builder builder;

    /**
     * create a parser; This includes creating an {@link nu.xom.Builder} with
     * our custom node factory
     *
     * @param loader    resource loader algorithm
     * @param namespace for SOAP namespaces
     * @param validate  validation logic.
     * @param factory an optional factory
     */
    public SoapMessageParser(ResourceLoader loader,
                             String namespace,
                             boolean validate, ExtendedNodeFactory factory)
            throws SAXException {
        resourceLoader = loader;
        //we mandate Xerces, as the others cannot handle schema so well
        XMLReader xerces = ParserHelper.createXmlParser(validate, true, true);

        if (validate) {
            CatalogHandler resolver = new CatalogHandler(loader);
            try {
                resolver.bind(xerces);
            } catch (IOException e) {
                throw new SAXException(e.getMessage(), e);
            }
        }
        if (factory == null) {
            factory= FactoryChain.createSoapFactoryChain(true,true);
            //factory = new SoapFactory(namespace, null);
        }
        builder = new Builder(xerces, validate, factory);
    }


    /**
     * parse the file, and throw an exception if we couldnt
     *
     * @param filename the file to parse
     * @return a parsed messgae
     * @throws IOException IO problems
     * @throws ParsingException invalid XML problems
     */
    public MessageDocument parseFile(String filename) throws IOException,
            ParsingException {
        File f = new File(filename);
        return (MessageDocument) builder.build(f);
    }

    /**
     * parse a stream that we are provided
     *
     * @param instream input stream
     * @return a parsed message
     * @throws IOException IO problems
     * @throws ParsingException invalid XML problems
     */
    public MessageDocument parseStream(InputStream instream) throws IOException,
            ParsingException {
        Document doc = builder.build(instream);
        return (MessageDocument) doc;
    }

    /**
     * load and parse a resoure through our current resource loader
     *
     * @param resource resource to load
     * @return a parsed document
     * @throws IOException IO problems
     * @throws ParsingException invalid XML problems
     */
    public MessageDocument parseResource(String resource) throws IOException,
            ParsingException {
        InputStream in = resourceLoader.loadResource(resource);
        return parseStream(in);
    }

    /**
     * Parse from a string
     * @param xml XML to parse
     * @param uri base URI for the document
     * @return a parsed document
     * @throws IOException IO problems
     * @throws ParsingException invalid XML problems
     */
    public MessageDocument parseString(String xml, String uri) throws ParsingException, IOException {
        Document doc = builder.build(xml, uri);
        return (MessageDocument) doc;
    }

    /**
     * Get our resource loader
     *
     * @return the resource loader
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

}
