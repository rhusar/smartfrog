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

import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.XmlConstants;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class handles entity resolution problems. When used with XSD, file paths
 * come in as absolute paths, not relative ones, and they are absolute relative
 * to where xerces is working, at least when importing resource files. So, this
 * catalog does something special: it matches by the short name of a file.
 * <p/>
 * Any path to xml-cdl.xsd will match to our copy, regardless of the rest of the
 * URL. without this, everything breaks,
 * <p/>
 * created Jul 15, 2004 3:58:11 PM
 */

public class CdlCatalog extends XmlCatalogResolver {

    protected static final String PACKAGE_BASE = "org/ggf/cddlm/";
    /**
     * where all the WSRF files really live {@value}
     */
    private static final String WSRF_PACKAGE = PACKAGE_BASE
            + CddlmConstants.XML_FILENAME_WSRF_DIRECTORY;

    /**
     * where the API files really live {@value}
     */
    private static final String API_PACKAGE = PACKAGE_BASE
            + CddlmConstants.CDL_FILENAME_XML_DIRECTORY;


    /**
     * This maps from namespaces to resources in our classpath {@value}
     */
    private static final String CDDLM_MAPPINGS[] = {
        CddlmConstants.XML_CDL_NAMESPACE, API_PACKAGE +
            CddlmConstants.CDL_FILENAME_XML_CDL,
        CddlmConstants.CDL_API_TYPES_NAMESPACE, API_PACKAGE +
            Constants.DEPLOY_API_SCHEMA_FILENAME,
        CddlmConstants.WS_ADDRESSING_NAMESPACE, WSRF_PACKAGE +
            CddlmConstants.XML_FILENAME_WS_ADDRESSING,
    };



    public CdlCatalog(ResourceLoader loader) {
        super(loader);
        loadCDDLMMappings();
    }


    /**
     * load in the standard CDDLM mappings
     */
    public void loadCDDLMMappings() {
        loadMappings(CDDLM_MAPPINGS);
    }


    /**
     * parser.setProperty( "http://apache.org/xml/properties/schema/external-schemaLocation",
     * "http: //domain.com/mynamespace mySchema.xsd");
     *
     * @param parser
     */
    public void setImportPaths(XMLReader parser)
            throws SAXNotSupportedException, SAXNotRecognizedException {
        String[] map = CDDLM_MAPPINGS;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < map.length; i += 2) {
            String schema = map[i];
            String filename = map[i + 1];
            buffer.append(schema);
            buffer.append(' ');
            buffer.append(filename);
            buffer.append(' ');
        }
        String s = new String(buffer);
        parser.setProperty(XmlConstants.PROPERTY_XERCES_SCHEMA_LOCATION, s);
    }

    /**
     * verify that our import paths are all working in this component
     *
     * @throws IOException
     */
    public void validateImportPaths()
            throws IOException {
        String[] map = CDDLM_MAPPINGS;
        for (int i = 0; i < map.length; i += 2) {
            String schema = map[i];
            String filename = map[i + 1];
            try {
                loadStreamSource(filename, schema);
            } catch (IOException e) {
                throw new FileNotFoundException("No resource: " +
                        filename
                        + " for " + schema);
            }
        }
    }

    /**
     * bind an XML reader to this bunny
     *
     * @param parser
     */
    public void bind(XMLReader parser) throws SAXNotSupportedException,
            SAXNotRecognizedException, IOException {
        setImportPaths(parser);
        parser.setEntityResolver(this);
    }




}
