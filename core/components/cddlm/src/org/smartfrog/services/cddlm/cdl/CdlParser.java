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
package org.smartfrog.services.cddlm.cdl;



import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ValidityException;
import nu.xom.ParsingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

/**
 * JDom based utility to parse CDL files. created Jul 1, 2004 1:49:31 PM
 */

public class CdlParser {

    /**
     * logic for resource loading
     */
    private ResourceLoader resourceLoader;

    /**
     * builder class
      */
    private Builder builder;

    /**
     * create a parser;
     *
     * @param loader   resource loader algorithm
     * @param validate validation logic.
     */
    public CdlParser(ResourceLoader loader, boolean validate)
            throws SAXException {
        resourceLoader = loader;
        //we mandate Xerces, as the others cannot handle schema so well
        XMLReader xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        xerces.setFeature("http://apache.org/xml/features/validation/schema",
                    validate);
        /*
        CatalogResolver resolver=new CatalogResolver();
        resolver.namespaceAware=true;
        xerces.setEntityResolver(resolver);
        */
        builder = new Builder(xerces,validate);
    }


    /**
     * parse the file, and throw an exception if we couldnt
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public CdlDocument parseFile(String filename) throws IOException,  ParsingException {
        File f = new File(filename);
        return new CdlDocument(builder.build(f));
    }

    /**
     * parse a stream that we are provided
     * @param instream
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public CdlDocument parseStream(InputStream instream) throws IOException, ParsingException
             {
        return new CdlDocument(builder.build(instream));
    }

    /**
     * load and parse a resoure through our current resource loader
     * @param resource
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public CdlDocument parseResource(String resource) throws IOException, ParsingException {
        InputStream in = resourceLoader.loadResource(resource);
        return parseStream(in);
    }

}
