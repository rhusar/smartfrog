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
package org.smartfrog.sfcore.languages.cdl;

import nu.xom.ParsingException;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.Import;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.importing.ClasspathResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportedDocument;
import org.smartfrog.sfcore.languages.cdl.importing.ImportedDocumentMap;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class represents the context of the execution; the environment in which
 * a CDL file is parsed. This includes <ol> <li>import resolver
 * implementation</li> <li>live property binding</li> <li>deployment
 * options</li> <li>JSDL for the deployment</li> <li>a cache of resolved
 * imports</li> </ol> The rest of the doc context should go in here too.
 * <p/>
 * created 08-Jun-2005 13:21:27
 */

public class ParseContext {


    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * import resolution class. This is mandatory (in the constructor) and so
     * can never be null
     */
    private ImportResolver importResolver;

    private ImportedDocumentMap imports = new ImportedDocumentMap();

    private ImportedDocumentMap localImports = new ImportedDocumentMap();

    /**
     * option lookup for remote deployment
     */
    private HashMap options = new HashMap();

    /**
     * properties are any extra properties set at deploy time
     */

    private Properties properties;

    /**
     * The hashmap containing any and all resolvable prototypes accessible in
     * the current document.
     */
    private HashMap<QName, PropertyList> resolvablePrototypes = new HashMap<QName, PropertyList>();

    /**
     * base document. may be null
     */
    private CdlDocument document;
    public static final String ERROR_NO_PROTOTYPE_NAME = "Prototype has no qname ";
    public static final String ERROR_DUPLICATE_PROTOTYPE = "Duplicate prototype :";
    private CdlParser parser;
    public static final String ERROR_RECURSIVE_IMPORT = "Recursive import of (%s,%s)";
    public static final String ERROR_DIFFERENT_LOCATION = "Cannot import %s into %s because %s is there already";
    public static final String ERROR_RECURSIVE_LOCAL_IMPORT = "Recursive import of ";

    /**
     * Create a parse context
     *
     * @param importResolver instance of whatever resolves imports
     * @param loader
     */
    public ParseContext(ImportResolver importResolver, ResourceLoader loader) {
        if (importResolver == null) {
            importResolver = new ClasspathResolver();
        }
        this.importResolver = importResolver;
        if (loader == null) {
            loader = new ResourceLoader(this.getClass());
        }
        try {
            parser = new CdlParser(loader, true);
        } catch (SAXException e) {
            throw new CdlRuntimeException("when creating parser", e);
        }
    }

    /**
     * create a parse context with the default resolver
     *
     * @see ClasspathResolver
     */
    public ParseContext() {
        this(null, null);
    }

    /**
     * get our import map
     *
     * @return
     */
    public ImportedDocumentMap getImports() {
        return imports;
    }

    /**
     * Map from a namespace to an imported document
     *
     * @param namespace
     * @return
     */
    public ImportedDocument lookupImportByNamespace(String namespace) {
        return getImports().get(namespace);
    }

    /**
     * look up imports by path, not name
     *
     * @param path
     * @return
     */
    public ImportedDocument lookupImportByPath(String path) {
        return getImports().get(path);
    }

    public HashMap getOptions() {
        return options;
    }

    public Properties getProperties() {
        return properties;
    }

    public ImportResolver getImportResolver() {
        return importResolver;
    }

    /**
     * set the resolver; bind if needed.
     *
     * @param importResolver
     */
    public void setImportResolver(ImportResolver importResolver) {
        this.importResolver = importResolver;
        if (importResolver != null) {
            importResolver.bind(this);
        }
    }

    public CdlDocument importDocument(Import imp) throws IOException, CdlException,
            ParsingException {
        assert imp != null;
        String namespace = imp.getNamespace();
        String path = imp.getLocation();
        if (namespace == null) {
            return importLocalDocument(path);
        } else {
            return importDocument(namespace, path);
        }
    }

    /**
     * do the full document import, to the extent of including and parsing the
     * doc
     *
     * @param path
     * @return the imported document
     * @throws IOException
     * @throws CdlException
     */
    public CdlDocument importLocalDocument(String path)
            throws IOException, CdlException,
            ParsingException {
        assert path != null;
        //first, check for it already being present.
        //no namespace, check in local paths
        ImportedDocument importedDocument = localImports.get(path);
        if (importedDocument != null) {
            if (importedDocument.getDocument() == null) {
                throw new CdlException(ERROR_RECURSIVE_LOCAL_IMPORT + path);
            }
            return null;
        }

        ImportedDocument imported = doImport(path, null);
        localImports.put(path, imported);
        return imported.getDocument();
    }

    /**
     * do the full document import, to the extent of including and parsing the
     * doc
     *
     * @param namespace namespace
     * @param path
     * @return the imported document
     * @throws IOException
     * @throws CdlException
     */
    public CdlDocument importDocument(String namespace, String path)
            throws IOException, CdlException,
            ParsingException {
        assert namespace != null;
        assert path != null;
        //first, check for it already being present.
        ImportedDocument importedDocument = lookupImportByNamespace(namespace);
        if (importedDocument != null) {
            CdlDocument document = importedDocument.getDocument();
            if (document == null) {
                throw new CdlException(String.format(ERROR_RECURSIVE_IMPORT,
                        namespace,
                        path));
            }
            if (importedDocument.getLocation() != path) {
                throw new CdlException(String.format(ERROR_DIFFERENT_LOCATION,
                        path,
                        namespace,
                        importedDocument.getLocation()));
            }
            //we have already been imported, skip it.
            return null;
        }

        ImportedDocument imported = doImport(path, namespace);
        imports.put(namespace, imported);
        return imported.getDocument();
    }

    private ImportedDocument doImport(String path, String namespace)
            throws IOException, ParsingException, CdlException {
        URL location = getImportResolver().resolveToURL(path);
        if (log.isDebugEnabled()) {
            log.debug("Importing " + namespace + " url" + location);
        }
        //we now have a location; lets load it.
        InputStream inputStream = location.openStream();
        CdlDocument doc;
        try {
            doc = parser.parseStream(inputStream);
            //recursive parse
            doc.parse(this);
        } finally {
            inputStream.close();
        }
        ImportedDocument imported = new ImportedDocument();
        imported.setDocument(doc);
        imported.setNamespace(namespace);
        imported.setLocation(path);
        return imported;
    }

    /**
     * Get the base document (may be null)
     *
     * @return
     */
    public CdlDocument getDocument() {
        return document;
    }


    /**
     * creates a new document, with the parse context set up. The doc is not
     * registered in any way.
     *
     * @return a new document
     * @throws AssertionError if getDocument()!=null, that is, a reused context
     */
    public CdlDocument createRootDocument() {
        assert document == null;
        document = new CdlDocument(this);
        return document;
    }

    /**
     * map the path to a URI. Hands off to the importResolver
     *
     * @param path
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    public URL resolveToURL(String path) throws IOException {
        return importResolver.resolveToURL(path);
    }


    /**
     * Resolve a prototype
     *
     * @param prototypeName
     * @return any known prototype of that name, or null for none
     */
    public PropertyList prototypeResolve(QName prototypeName) {
        return resolvablePrototypes.get(prototypeName);
    }

    /**
     * Add a new prototype to our list of known prototypes. If the prototype
     * exists, then it is replaced.
     *
     * @param prototype
     */
    public void prototypeUpdate(PropertyList prototype) {
        QName prototypeName = prototype.getQName();
        if (prototypeName == null) {
            throw new CdlRuntimeException(ERROR_NO_PROTOTYPE_NAME + prototype);
        }
        resolvablePrototypes.put(prototypeName, prototype);
    }

    /**
     * Add a new prototype to our list of known prototypes. If the prototype
     * exists, then we fault
     *
     * @param prototype
     * @throws CdlDuplicatePrototypeException for duplicates
     */
    public void prototypeAddNew(PropertyList prototype)
            throws CdlDuplicatePrototypeException {

        QName prototypeName = prototype.getQName();
        if (prototypeName == null) {
            throw new CdlRuntimeException(ERROR_NO_PROTOTYPE_NAME + prototype);
        }
        if (prototypeResolve(prototypeName) != null) {
            throw new CdlDuplicatePrototypeException(
                    ERROR_DUPLICATE_PROTOTYPE + prototypeName);
        }
        log.debug("Adding prototype " + prototypeName);
        resolvablePrototypes.put(prototypeName, prototype);
        //mark the proto as toplevel
        prototype.setTemplate(true);
    }

    /**
     * Get all the prototypes in the current system
     *
     * @return
     */
    public Collection<PropertyList> prototypes() {
        return resolvablePrototypes.values();
    }

    public Log getLog() {
        return log;
    }
}
