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
package org.smartfrog.projects.alpine.xmlutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.faults.ValidationException;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * This class handles entity resolution problems. When used with XSD, file paths
 * come in as absolute paths, not relative ones, and they are absolute relative
 * to where xerces is working, at least when importing resource files. So, this
 * catalog does something special: it matches by the short name of a file.
 * <p/>
 * Any path to xml-cdl.xsd will match to our copy, regardless of the rest of the
 * URL. without this, everything breaks. This is very ugly, and makes a mockery
 * of the notion of using full length urls to qualify things. But for some reason
 * XSD handling breaks without it. Sigh.
 * <p/>
 * created Jul 15, 2004 3:58:11 PM
 */

public class CatalogHandler implements URIResolver, EntityResolver, Validatable {


    private static final Log log = LogFactory.getLog(CatalogHandler.class);

    ResourceLoader loader;

    /**
     * map table
     */
    private HashMap<String, String> mappings;
    public static final String ERROR_NO_RESOURCE_FOUND = "No resource: ";

    /**
     * Constructor sets up an empty map, and calls
     * {@link #loadInitialMappings()} to fill in the table.
     */
    public CatalogHandler(ResourceLoader loader) {
        this.loader = loader;
        resetMap();
        loadInitialMappings();
    }

    /**
     * reset the resolution table
     */
    public void resetMap() {
        mappings = new HashMap<String, String>();
    }


    /**
     * load in the initial mappings.
     * This is an override point, one that is called in the constructor of the ClasspathCatalog
     */
    public void loadInitialMappings() {
    }

    /**
     * load a set of mappings in, where a set is an array of strings, name followed by path
     *
     * @param map array of name,value pairs to load
     */
    public void loadMappings(String map[]) {
        assert map.length % 2 == 0;
        for (int i = 0; i < map.length; i += 2) {
            String schema = map[i];
            String filename = map[i + 1];
            //schema to filename mapping
            mappings.put(schema, filename);
            //filename to filename
            mappings.put(filename, filename);
            //and file in path to filename mapping.
            mappings.put(extractLastPathElement(filename), filename);
        }
    }

    /**
     * look up a mapping
     *
     * @param uri
     * @return the resolved path
     */
    public String lookup(String uri) {
        return mappings.get(uri);
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
     *          if an error occurs when trying to resolve
     *          the URI.
     */
    public Source resolve(String href, String base)
            throws TransformerException {
        String resource = lookup(href);
        if (resource == null) {
            return null;
        }
        try {
            return loadStreamSource(resource, href);
        } catch (IOException e) {
            throw new TransformerException(e);
        }
    }

    /**
     * Create a new stream source from a resource in the classloader
     *
     * @param resource resource to load
     * @param href     href for relative URI resolution
     * @return source
     * @throws java.io.IOException on trouble
     */
    private Source loadStreamSource(String resource, String href)
            throws IOException {
        InputStream in = loader.loadResource(resource);
        StreamSource source = new StreamSource(in, href);
        return source;
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
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another
     *                                  exception.
     * @throws java.io.IOException      A Java-specific IO exception, possibly the result of
     *                                  creating a new InputStream or Reader for the
     *                                  InputSource.
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
        StringBuffer buffer = new StringBuffer();
        for (String schema : mappings.keySet()) {
            String filename = mappings.get(schema);
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
     * @throws java.io.FileNotFoundException
     */
    public void validateImportPaths()
            throws FileNotFoundException {
        for (String schema : mappings.keySet()) {
            String filename = mappings.get(schema);
            try {
                loadStreamSource(filename, schema);
            } catch (IOException e) {
                throw new FileNotFoundException(ERROR_NO_RESOURCE_FOUND +
                        filename
                        + " for " + schema);
            }
        }
    }

    /**
     * validate an instance.
     * Return if the object is valid, thrown an exception if not.
     * It is imperative that this call has <i>No side effects</i>.
     *
     * @return true unless an exception is thrown
     * @throws org.smartfrog.projects.alpine.faults.ValidationException
     *          with text if not valid
     */
    public boolean validate() throws ValidationException {
        try {
            validateImportPaths();
        } catch (FileNotFoundException e) {
            throw new ValidationException(e);
        }
        return true;
    }

    /**
     * bind an XML reader to this catalog
     *
     * @param parser
     */
    public void bind(XMLReader parser) throws SAXNotSupportedException,
            SAXNotRecognizedException, IOException {
        setImportPaths(parser);
        parser.setEntityResolver(this);
    }


}
