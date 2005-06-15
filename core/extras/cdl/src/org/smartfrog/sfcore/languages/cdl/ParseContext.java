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

import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.importing.ClasspathResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportedDocument;
import org.smartfrog.sfcore.languages.cdl.importing.ImportedDocumentMap;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.io.IOException;
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

    /**
     * Create a parse context
     *
     * @param importResolver instance of whatever resolves imports
     */
    public ParseContext(ImportResolver importResolver) {
        this.importResolver = importResolver;
    }

    /**
     * create a parse context with the default resolver
     *
     * @see ClasspathResolver
     */
    public ParseContext() {
        this.importResolver = new ClasspathResolver();
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

    public HashMap getOptions() {
        return options;
    }

    public Properties getProperties() {
        return properties;
    }

    public ImportResolver getImportResolver() {
        return importResolver;
    }

    public void setImportResolver(ImportResolver importResolver) {
        this.importResolver = importResolver;
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
        QName prototypeName = prototype.getName();
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

        QName prototypeName = prototype.getName();
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
        prototype.setToplevel(true);
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
