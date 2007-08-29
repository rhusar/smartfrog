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
import static org.ggf.cddlm.generated.api.CddlmConstants.*;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.XmlConstants;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import nu.xom.XPathContext;

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

    public static final String PACKAGE_BASE = "";
    /**
     * where all the WSRF files really live {@value}
     */
    public  static final String WSRF_PACKAGE = PACKAGE_BASE
            + XML_FILENAME_WSRF_DIRECTORY;

    /**
     * where the API files really live {@value}
     */
    public static final String API_PACKAGE = PACKAGE_BASE
            + CDL_FILENAME_XML_DIRECTORY;


    public static final String RESOURCE_XML_CDL_XSD = API_PACKAGE +
       CDL_FILENAME_XML_CDL;
    public static final String RESOURCE_DEPLOYAPI_XSD = API_PACKAGE +
       Constants.DEPLOY_API_SCHEMA_FILENAME;
    public static final String RESOURCE_WS_ADDR_XSD = WSRF_PACKAGE +
       XML_FILENAME_WS_ADDRESSING;
    /**
     * This maps from namespaces to resources in our classpath {@value}
     */
    private static final String CDDLM_MAPPINGS[] = {
            XML_CDL_NAMESPACE, RESOURCE_XML_CDL_XSD,
            CDL_API_TYPES_NAMESPACE, RESOURCE_DEPLOYAPI_XSD,
            WS_ADDRESSING_NAMESPACE, RESOURCE_WS_ADDR_XSD,
            WS_ADDRESSING_2004_NAMESPACE,
                WSRF_PACKAGE +XML_FILENAME_WS_ADDRESSING_2004_08,
            WS_ADDRESSING_2005_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING_2005_08,
            SOAP11_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_SOAP_11,
            SOAP12_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_SOAP_12,
            SOAP11_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_SOAP_11,
            MUWS_P1_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_SOAP_11,
            MUWS_P2_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_WSDM_MUWS_P2,
    };

    private static final String[][] names = {
            {"xsd", "http://www.w3.org/2000/10/XMLSchema"},
            {"wsa", WS_ADDRESSING_NAMESPACE},
            {"wsa2003", WS_ADDRESSING_NAMESPACE},
            {"wsa2004", WS_ADDRESSING_2004_NAMESPACE},
            {"wsa2005", WS_ADDRESSING_2005_NAMESPACE},
            {"api", CDL_API_TYPES_NAMESPACE},
            {"apiw", CDL_API_WSDL_NAMESPACE},
            {"cdl", XML_CDL_NAMESPACE},
            {"cmp", CDL_CMP_TYPES_NAMESPACE},
            {"cmpw", CDL_CMP_WSDL_NAMESPACE},
            {"muws-p1-xs", MUWS_P2_NAMESPACE},
            {"muws-p2-xs", MUWS_P2_NAMESPACE},
            {"wsrf-bf", WSRF_WSBF_NAMESPACE},
            {"wsrf-rl", WSRF_WSRL_NAMESPACE},
            {"wsrf-rp", WSRF_WSRP_NAMESPACE},
            {"wsrf-top", WSN_WST_NAMESPACE},
            {"wsnt", WSRF_WSNT_NAMESPACE},
            {"s12", SOAP12_NAMESPACE},
            {"s11", SOAP11_NAMESPACE},
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


    /**
     * Create a new XPath context for resolving things
     * @return something that knows the standard names of the deploy api
     */
    public static XPathContext createXPathContext() {
        XPathContext context=new XPathContext();
        for(int i=0;i<names.length;i++) {
            context.addNamespace(names[i][0],names[i][1]);
        }
        return context;
    }

    /**
     * Get the prefix/namespace map table as a map, because
     * XPathContext doesnt have the public accessors.
     * @return a map that knows the standard names of the deploy api
     */
    public static Map<String,String> createPrefixMap() {
        Map<String, String> map=new HashMap<String,String>();
        for (int i = 0; i < names.length; i++) {
            map.put(names[i][0], names[i][1]);
        }
        return map;
    }
}
