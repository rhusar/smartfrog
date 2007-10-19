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
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.importing.ClasspathResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportedDocument;
import org.smartfrog.sfcore.languages.cdl.importing.ImportedDocumentMap;
import org.smartfrog.sfcore.languages.cdl.importing.BaseImportResolver;
import org.smartfrog.sfcore.languages.cdl.importing.classpath.UrlFactory;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.generate.TypeMapper;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
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
    
    /**
     * resource loader
     */ 
    ResourceLoader loader;

    /**
     * Map of imported documents
     */
    private ImportedDocumentMap imports = new ImportedDocumentMap();

    /**
     * Local imports are things that are imported without a namespace;
     * these are effectively merged into the current templated
     */
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


    private UrlFactory urlFactory;

    private TypeMapper typeMapper;

    /**
     * base document. may be null
     */
    private CdlDocument document;
    public static final String ERROR_NO_PROTOTYPE_NAME = "Prototype has no qname ";
    public static final String ERROR_DUPLICATE_PROTOTYPE = "Duplicate prototype :";
    //private CdlParser parser;
    public static final String ERROR_RECURSIVE_IMPORT_PREFIX = "Recursive import of ";
    private static final String ERROR_RECURSIVE_IMPORT = ERROR_RECURSIVE_IMPORT_PREFIX +"(%s,%s)";
    public static final String ERROR_DIFFERENT_LOCATION = "Cannot import %s into %s because %s is there already";
    public static final String ERROR_RECURSIVE_LOCAL_IMPORT = "Recursive import of ";
    public static final String ERROR_PARSER_SAX_FAULT = "when creating parser";
    public static final String ERROR_RELATIVE_IMPORT_FAILED = "Unable to resolve relative import path ";

    /**
     * Create a parse context
     *
     * @param importResolver instance of whatever resolves imports
     * @param loader
     */
    public ParseContext(ImportResolver importResolver, ResourceLoader loader) {
        if (importResolver == null) {
            importResolver = new BaseImportResolver();
        }
        setImportResolver(importResolver);
        if (loader == null) {
            loader = new ResourceLoader(this.getClass());
        }
        this.loader=loader;
        //URL factory
        urlFactory= new UrlFactory(loader);
        try {
            typeMapper =new TypeMapper();
        } catch (SmartFrogException e) {
            //only if we are very incompetent and ignoring unit test results
            throw new CdlRuntimeException(e);
        }
    }
    
    /**
     * create a new parser
     * @throws CdlRuntimeException if a parser cannot be created
     * @return a new parser
     */ 
    CdlParser createParser() {
        try {
            CdlParser parser = new CdlParser(this, true);
            return parser;
        } catch (SAXException e) {
            throw new CdlRuntimeException(ERROR_PARSER_SAX_FAULT, e);
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

    private String resolveReference(URL base, String path) {
        //todo
        return path;
    }

    /**
     * Import a document
     * @param parent who is doing the import. This lets us do relative resolution
     * @param declaration the import declaration from the doc
     * @return the imported document or null if it was already imported.
     * @throws IOException IO errors
     * @throws CdlException CDL errors
     * @throws ParsingException parsing problems.
     */
    public CdlDocument importDocument(CdlDocument parent, Import declaration) throws IOException, CdlException,
            ParsingException {
        assert declaration != null;
        String namespace = declaration.getNamespace();
        String path = declaration.getLocation();
        //now, do some relative resolution stuff. This needs to happen early,
        //because the map tables need absolute references to work properly.
        String urlpath=resolveRelativePath(parent, path);
        if (namespace == null) {
            return importLocalDocument(urlpath);
        } else {
            return importDocument(namespace, urlpath);
        }
    }

    /**
     * try and turn a possibly relative path into an absolute URL
     * @param parent document to use as a basis
     * @param path the relative/absolute path
     * @return
     * @throws CdlResolutionException
     */
    public String resolveRelativePath(CdlDocument parent,String path) throws CdlResolutionException {
        URL documentURL = parent != null ? parent.getDocumentURL() : null;
        URL url=null;
        try {
            url=urlFactory.createUrl(path);
        } catch (MalformedURLException first) {
            String message = ERROR_RELATIVE_IMPORT_FAILED + path;
            try {
                if(documentURL == null) {
                    throw new CdlResolutionException(message +" - no base document/URL");
                }
                url= urlFactory.createUrl(documentURL, path);
            } catch (MalformedURLException second) {
                //double failure. Throw the first exception, as it may be the most meaningful.
                log.info("resolving "+path+" relative to "+documentURL,second);
                throw new CdlResolutionException(message,first);
            }
        }
        return url.toExternalForm();
    }

    /**
     * Import a a document into the current namespace.
     *
     * @param path the full path to the doc (not a relative path)
     * @return the imported document or null if the import has already taken place
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
            //check for being present
            if (importedDocument.getDocument() == null) {
                //if the doc is present, then throw an exception
                throw new CdlException(ERROR_RECURSIVE_LOCAL_IMPORT + path);
            }
            //else return null to say the import has already taken place
            return null;
        }

        //place a stub in to say it is being imported
        //to catch and trigger recursive things
        localImports.put(path, new ImportedDocument());
        //import it
        ImportedDocument imported = doImport(path, null);
        //then patch in the imported doc into our import list
        localImports.put(path, imported);
        //and return the document
        return imported.getDocument();
    }

    /**
     * do the full document import, to the extent of including and parsing the
     * doc.
     *
     * Catches importing the same namespace from multiple locations.
     * @param namespace namespace
     * @param path the full path to the doc (not a relative path)
     * @return the imported document or null for an already imported doc
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
        imports.put(namespace, new ImportedDocument());
        ImportedDocument imported = doImport(path, namespace);
        imports.put(namespace, imported);
        return imported.getDocument();
    }

    /**
     * do the actual import. this includes mapping from the import path to a
     * URL, fetching the contents thereof.
     * @param path unresolved import.
     * @param namespace xmlnamespace, can be null
     * @return the imported document
     * @throws IOException
     * @throws ParsingException
     * @throws CdlException
     */ 
    private ImportedDocument doImport(String path, String namespace)
            throws IOException, ParsingException, CdlException {
        if (log.isDebugEnabled()) {
            log.debug("Importing " + path);
        }
        URL referenceURL=importResolver.createReferenceURL(path);
        URL sourceURL = importResolver.convertToSourceURL(referenceURL);
        if (log.isDebugEnabled()) {
            log.debug("Importing ns=" + namespace + " url " +referenceURL+" from "+ sourceURL);
        }
        //we now have a location; let's load it.
        InputStream inputStream = getUrlFactory().openStream(sourceURL);
        CdlDocument cdlDocument;
        try {
            //open the document
            CdlParser parser = createParser();
            cdlDocument = parser.parseStream(inputStream);
            //patch the document's origin
            cdlDocument.setDocumentURL(referenceURL);
            //recursive parse
            cdlDocument.parseImportedDocument(this, namespace);
        } finally {
            inputStream.close();
        }
        //return the newly imported document
        ImportedDocument imported = new ImportedDocument();
        imported.setDocument(cdlDocument);
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
     * Resolve a prototype
     *
     * @param prototypeName
     * @return any known prototype of that name, or null for none
     */
    public PropertyList prototypeResolve(QName prototypeName) {
        return resolvablePrototypes.get(prototypeName);
    }


    /**
     * test for an name mapping to a prototype
     * @param name qname to look for
     * @return true iff there is a match in the current context
     */
    public boolean hasPrototypeNamed(QName name) {
        return prototypeResolve(name)!=null;
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
        prototypeAddNew(prototype, prototypeName);
    }

        /**
        * Add a new prototype to our list of known prototypes. If the prototype
        * exists, then we fault
        *
        * @param prototype
        * @param prototypeName name to register prototype under
        * @throws CdlDuplicatePrototypeException for duplicates
        */
    public void prototypeAddNew(PropertyList prototype, QName prototypeName)
            throws CdlDuplicatePrototypeException {
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

    public ResourceLoader getLoader() {
        return loader;
    }

    public UrlFactory getUrlFactory() {
        return urlFactory;
    }

    public TypeMapper getTypeMapper() {
        return typeMapper;
    }
}
