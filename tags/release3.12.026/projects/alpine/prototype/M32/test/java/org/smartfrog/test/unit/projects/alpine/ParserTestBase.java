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

package org.smartfrog.test.unit.projects.alpine;

import junit.framework.TestCase;
import nu.xom.ParsingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.xmlutils.CatalogHandler;
import org.smartfrog.projects.alpine.xmlutils.ResourceLoader;
import org.xml.sax.SAXException;

import java.io.IOException;

/**

 */
public abstract class ParserTestBase extends TestCase implements Filenames {


    protected static final Log log = LogFactory.getLog(this.getClass());
    protected SoapMessageParser parser;

    /**
     * Constructs a test case with the given name.
     */
    protected ParserTestBase(String name) {
        super(name);
    }

    /**
     * create a new catalog, using the local classloader for resolution
     *
     * @return a catalog loading resources from this classpath
     */
    protected CatalogHandler createCatalog() {
        ResourceLoader loader;
        loader = new ResourceLoader(this.getClass());
        return new CatalogHandler(loader);
    }

    private void logLoading(String resource) {
        log(resource);
    }

    protected void log(String message) {
        log.info(message);
    }

    /**
     * load a document
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws ParsingException
     * @throws AlpineRuntimeException
     */
    protected MessageDocument load(String filename) throws IOException,
            ParsingException {
        MessageDocument doc;
        logLoading(filename);
        doc = parser.parseResource(filename);
        return doc;
    }


    /**
     * configure the parser
     *
     * @throws SAXException
     */
    protected void initParser() throws SAXException {
        ResourceLoader loader = new ResourceLoader(this.getClass());
        parser = new SoapMessageParser(loader,
                SoapConstants.URI_SOAPAPI,
                isParserValidating(),
                null);
    }

    /**
     * override point: return true if the parser should be validating
     *
     * @return false, by default.
     */
    protected boolean isParserValidating() {
        return false;
    }

    /**
     * Sets up the fixture by initialising the parser
     */
    protected void setUp() throws Exception {
        super.setUp();
        initParser();
    }
}
