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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * created Jul 15, 2004 3:58:11 PM
 */

public class CdlCatalog implements URIResolver, EntityResolver {

    /**
     * how we load resources
     */
    private ResourceLoader loader;

    private static final Log log = LogFactory.getLog(CdlCatalog.class);

    private static final String XSD = "org/smartfrog/services/cddlm/xsd/";

    private static final String CDDLM_MAPPINGS[] = {
        DeployApiConstants.XML_CDL_NAMESPACE, Constants.CDDLM_XSD_FILENAME,
        DeployApiConstants.CDL_API_NAMESPACE, Constants.DEPLOY_API_SCHEMA_FILENAME,
        DeployApiConstants.WS_ADDRESSING_NAMESPACE, "ws-addressing.xsd",
    };

    /**
     * where all the files really live
     */
    private static final String packageBase = XSD;

    /**
     * property to set on the parser to fix a schema
     */
    public static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";

    /**
     * map table
     */
    private HashMap mappings;

    public CdlCatalog(ResourceLoader loader) {
        this.loader = loader;
        resetMap();
        loadCDDLMMappings();
    }

    /**
     * reset the resolution table
     */
    public void resetMap() {
        mappings = new HashMap();
    }


    /**
     * load in the standard CDDLM mappings
     */
    public void loadCDDLMMappings() {
        loadMappings(CDDLM_MAPPINGS);
    }

    /**
     * load a set of mappings in.
     *
     * @param map array of name,value pairs to load
     */
    public void loadMappings(String map[]) {
        assert map.length % 2 == 0;
        for (int i = 0; i < map.length; i += 2) {
            String schema = map[i];
            String filename = map[i + 1];
            mappings.put(schema, filename);
            mappings.put(filename, filename);
        }
    }

    /**
     * look up a mapping
     *
     * @param uri
     * @return
     */
    public String lookup(String uri) {
        Object value = mappings.get(uri);
        if (value != null) {
            return packageBase + (String) value;
        } else {
            return null;
        }
    }

    /**
     * Called by the processor when it encounters an xsl:include, xsl:import, or
     * document() function.
     *
     * @param href An href attribute, which may be relative or absolute.
     * @param base The base URI in effect when the href attribute was
     *             encountered.
     * @return A Source object, or null if the href cannot be resolved, and the
     *         processor should try to resolve the URI itself.
     * @throws javax.xml.transform.TransformerException
     *          if an error occurs when trying to resolve the URI.
     */
    public Source resolve(String href, String base)
            throws TransformerException {
        String resource = lookup(href);
        if (resource == null) {
            return null;
        }
        try {
            InputStream in = loader.loadResource(resource);
            StreamSource source = new StreamSource(in, href);
            return source;
        } catch (IOException e) {
            throw new TransformerException(e);
        }
    }


    /**
     * Allow the application to resolve external entities.
     * <p/>
     * <p>The Parser will call this method before opening any external entity
     * except the top-level document entity (including the external DTD subset,
     * external entities referenced within the DTD, and external entities
     * referenced within the document element): the application may request that
     * the parser resolve the entity itself, that it use an alternative URI, or
     * that it use an entirely different input source.</p>
     * <p/>
     * <p>Application writers can use this method to redirect external system
     * identifiers to secure and/or local URIs, to look up public identifiers in
     * a catalogue, or to read an entity from a database or other input source
     * (including, for example, a dialog box).</p>
     * <p/>
     * <p>If the system identifier is a URL, the SAX parser must resolve it
     * fully before reporting it to the application.</p>
     *
     * @param publicId The public identifier of the external entity being
     *                 referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity being
     *                 referenced.
     * @return An InputSource object describing the new input source, or null to
     *         request that the parser open a regular URI connection to the
     *         system identifier.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping
     *                                  another exception.
     * @throws java.io.IOException      A Java-specific IO exception, possibly
     *                                  the result of creating a new InputStream
     *                                  or Reader for the InputSource.
     * @see org.xml.sax.InputSource
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("resolving " + systemId);
        }
        String resource = lookup(systemId);
        if (resource == null) {
            String filename = getFilenameFromSystemID(systemId);
            if (filename != null) {
                return resolveEntity(publicId, filename);
            }
            log.debug("no match");
            return null;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("resolved to " + resource);
            }
            return new InputSource(loader.loadResource(resource));
        }
    }

    /**
     * extract any filename from this file.
     *
     * @param systemId
     * @return
     */
    String getFilenameFromSystemID(String systemId) {
        if (!systemId.startsWith("file://")) {
            return null;
        }
        return extractLastPathElement(systemId);
    }

    private String extractLastPathElement(String systemId) {
        int lastSlash = systemId.lastIndexOf('/');
        if (lastSlash == -1) {
            return null;
        }
        String endString = systemId.substring(lastSlash + 1);
        if (endString.length() > 0) {
            return endString;
        } else {
            return null;
        }
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
        parser.setProperty(SCHEMA_LOCATION, s);
    }

    /**
     * bind an XML reader to this bunny
     *
     * @param parser
     */
    public void bind(XMLReader parser) throws SAXNotSupportedException,
            SAXNotRecognizedException {
        setImportPaths(parser);
        parser.setEntityResolver(this);
    }
}
