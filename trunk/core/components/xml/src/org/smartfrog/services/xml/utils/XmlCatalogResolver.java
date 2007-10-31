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
package org.smartfrog.services.xml.utils;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.smartfrog.sfcore.utils.ResourceLoader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * This class handles resolution for Sax and Dom Parsers.
 * It does some naughty "last-name-only" binding to deal with relative URL grief.
 */

public class XmlCatalogResolver implements URIResolver, EntityResolver {
    /**
     * how we load resources
     */
    protected ResourceLoader loader;
    /**
     * map table
     */
    protected HashMap mappings;

    public XmlCatalogResolver(ResourceLoader loader) {
        setLoader(loader);
        resetMap();
    }

    public ResourceLoader getLoader() {
        return loader;
    }

    protected void setLoader(ResourceLoader loader) {
        assert loader != null:"null ResourceLoader";
        this.loader = loader;
    }

    public HashMap getMappings() {
        return mappings;
    }

    protected void setMappings(HashMap mappings) {
        this.mappings = mappings;
    }

    /**
     * reset the resolution table
     */
    public void resetMap() {
        setMappings(new HashMap());
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
            //schema to filename mapping
            addMapping(schema, filename);
            //filename to filename
            addMapping(filename, filename);
            //and file in path to filename mapping.
            addMapping(extractLastPathElement(filename), filename);
        }
    }

    public void addMapping(String schema, String filename) {
        getMappings().put(schema, filename);
    }

    /**
     * look up a mapping
     *
     * @param uri
     * @return whatever is registered
     */
    public String lookup(String uri) {
        return (String) mappings.get(uri);
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
     * @throws javax.xml.transform.TransformerException if an error occurs when trying to resolve
     *                              the URI.
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
    protected Source loadStreamSource(String resource, String href)
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
     *                      exception.
     * @throws java.io.IOException  A Java-specific IO exception, possibly the result of
     *                      creating a new InputStream or Reader for the
     *                      InputSource.
     * @see org.xml.sax.InputSource
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        String resource = lookup(systemId);
        if (resource == null) {
            String filename = getFilenameFromSystemID(systemId);
            if (filename != null) {
                return resolveEntity(publicId, filename);
            }
            return null;
        } else {
            return new InputSource(loader.loadResource(resource));
        }
    }

    /**
     * extract any filename from this file.
     *
     * @param systemId the system ID/URL
     * @return the filename
     */
    String getFilenameFromSystemID(String systemId) {
        if (!systemId.startsWith("file://")) {
            return null;
        }
        return extractLastPathElement(systemId);
    }

    /**
     * strip off everything after the last forward /
     * @param systemId the system ID/URL
     * @return what is left at the end, or null if there is no / or nothing after it
     */
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
}
