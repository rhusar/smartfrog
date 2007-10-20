/** (C) Copyright 2004-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl;


import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.smartfrog.services.xml.utils.ParserHelper;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.DomToXom;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.ExtendedNodeFactory;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Xom based utility to parse CDL files. created Jul 1, 2004 1:49:31 PM
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

    private ParseContext context;
    /**
     * create a parser;
     * This includes creating an {@link nu.xom.Builder} with
     * {@link ExtendedNodeFactory} as the node factory
     * @param context loading context
     * @param validate validation logic.
     */
    public CdlParser(ParseContext context, boolean validate)
            throws SAXException {
        this.context=context;
        resourceLoader = context.getLoader();
        //we mandate Xerces, as the others cannot handle schema so well
        XMLReader xerces = ParserHelper.createXmlParser(validate, true, true);

        if (validate) {
            CdlCatalog resolver = new CdlCatalog(resourceLoader);
            try {
                resolver.bind(xerces);
            } catch (IOException e) {
                throw new SAXException(e.getMessage(), e);
            }
        }
        builder = new Builder(xerces, validate, new ExtendedNodeFactory());
    }


    /**
     * parse the file, and throw an exception if we couldnt
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public CdlDocument parseFile(String filename) throws IOException,
            ParsingException, CdlException {
        File f = new File(filename);
        Document doc = builder.build(f);
        return new CdlDocument(doc);
    }

    /**
     * parse a stream that we are provided
     *
     * @param instream
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public CdlDocument parseStream(InputStream instream) throws IOException,
            ParsingException, CdlException {
        Document doc = builder.build(instream);
        return new CdlDocument(doc);
    }

    /**
     * load and parse a resource through our current resource loader
     *
     * @param resource
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public CdlDocument parseResource(String resource) throws IOException,
            ParsingException, CdlException {
        InputStream in = resourceLoader.loadResource(resource);
        CdlDocument cdlDocument = parseStream(in);
        URL docURL=context.getUrlFactory().createClasspathUrl(resource);
        cdlDocument.setDocumentURL(docURL);
        return cdlDocument;
    }

    /**
     * Convert a Dom document to a CDL one.
     * @param dom
     * @return a CdlDocument from a Dom
     * @throws ParsingException
     * @throws CdlException
     */
    public CdlDocument parseDom(org.w3c.dom.Document dom) throws
            ParsingException, CdlException {
        DomToXom converter = new DomToXom(builder);
        //this converts it to a Dom Document, but not to
        Document document = converter.convert(dom);
        return new CdlDocument(document);
    }


    /**
     * Get our resource loader
     *
     * @return
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

}
