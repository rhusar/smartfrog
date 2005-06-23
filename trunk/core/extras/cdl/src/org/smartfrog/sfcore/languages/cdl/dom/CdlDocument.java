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
package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.GenericAttribute;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.URIAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.generate.GenerateContext;
import org.smartfrog.sfcore.languages.cdl.generate.ToSmartFrog;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsResolver;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.utils.IteratorRelay;
import org.smartfrog.sfcore.languages.cdl.utils.NodeIterator;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * This represents a parsed CDL document, or an error caused during parsing.
 */

public class CdlDocument implements Names, ToSmartFrog {

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * Original Xom Document
     */
    private DocumentNode document;

    /**
     * the target namespace of this document (may be null)
     */
    private URI targetNamespace;
    private URIAttribute targetNamespaceAttr;

    /**
     * the root document
     */
    private RootNode root;

    /**
     * configuration elements
     */
    private ToplevelList configuration;

    /**
     * System declaration
     */
    private ToplevelList system;

    /**
     * Our list of imports
     */
    private List<Import> imports = new ArrayList<Import>();

    /**
     * Our types
     */
    private Type types = null;

    /**
     * the parse context for this document. We may or may not be the primary
     * document in the context. Indeed, we wont be for any importation, but that
     * is mostly irrelevant.
     */
    private ParseContext parseContext = null;
    public static final String ERROR_WRONG_ROOT_ELT = "The root element is the wrong type";

    public CdlDocument() {
        super();
    }

    public CdlDocument(ParseContext parseContext) {
        this();
        this.parseContext = parseContext;
    }

    public CdlDocument(DocumentNode doc) throws CdlException {
        bind(doc);
    }

    /**
     * get the parse context
     *
     * @return
     */
    public ParseContext getParseContext() {
        return parseContext;
    }

    /**
     * set the parse context.
     *
     * @param parseContext
     */
    public void setParseContext(ParseContext parseContext) {
        this.parseContext = parseContext;
    }


    /**
     * validity test
     *
     * @return
     */
    public boolean isValid() {
        //TODO
        return document != null;
    }

    public Document getDocument() {
        return document;
    }

    /**
     * Get the configuration
     *
     * @return configuration; null for no none
     */
    public ToplevelList getConfiguration() {
        return configuration;
    }

    /**
     * Get the system declaration
     *
     * @return system or null for none defined
     */
    public ToplevelList getSystem() {
        return system;
    }

    public void setConfiguration(ToplevelList configuration) {
        this.configuration = configuration;
    }

    /**
     * set the system property. Protected as most callers should call {@link
     * #replaceSystem(ToplevelList)}
     *
     * @param system
     */
    protected void setSystem(ToplevelList system) {
        this.system = system;
    }

    /**
     * replace the system element in our cache and in the document itself.
     *
     * @param newsystem
     */
    public void replaceSystem(ToplevelList newsystem) {

        if (system != null) {
            root.replaceChild(system, newsystem);
        }
        setSystem(newsystem);
    }

    /**
     * Target namespace of the doc
     *
     * @return URI to the namespace, or null
     */
    public URI getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Get at the import list
     *
     * @return Potentially empty list of imports
     */
    public List<Import> getImports() {
        return imports;
    }

    /**
     * Get the type declaration
     *
     * @return type declaration
     */
    public Type getTypes() {
        return types;
    }


    /**
     * Iterate just over elements
     *
     * @param element
     * @return
     */
    public static NodeIterator nodeIterator(ParentNode element) {
        return new NodeIterator(element);
    }

    /**
     * Iteratable over the nodes
     *
     * @param element
     * @return
     */
    public static IteratorRelay<Node> nodes(ParentNode element) {
        return new IteratorRelay<Node>(nodeIterator(element));
    }

    /**
     * Look up a toplevel node
     *
     * @param name
     * @return
     */
    public PropertyList lookup(QName name) {
        return configuration.getChildTemplateMatching(name);
    }

    /**
     * bind the document. This does not do any parsing of the XML
     *
     * @param doc
     * @throws CdlException
     */
    public void bind(DocumentNode doc) throws CdlException {
        assert doc != null;
        this.document = doc;
        doc.setOwner(this);
    }

    /**
     * Get the root node
     *
     * @return the root node, null for an unparsed doc
     */
    public RootNode getRoot() {
        return root;
    }

    /**
     * This is the complete parse process. The parse context is bound, and the
     * different phases are invoked
     *
     * @param context the parsing context
     * @throws CdlXmlParsingException
     */
    public void parse(ParseContext context) throws CdlException, IOException,
            ParsingException {
        setParseContext(context);
        parsePhaseBuildDom();
        parsePhaseProcessImports();
        parsePhaseExtendProcessing();
        parsePhaseResolveVariables();
        parsePhaseEvaluateExpressions();
    }


    /**
     * This is the parse  phase where the DOM is extracted, and the root
     * prototypes are registered
     *
     * @throws CdlXmlParsingException
     */
    public void parsePhaseBuildDom() throws CdlException {

        Element rootElement = document.getRootElement();
        if (rootElement instanceof RootNode) {
            root = (RootNode) rootElement;
        } else {
            throw new CdlXmlParsingException(rootElement,
                    ERROR_WRONG_ROOT_ELT);
        }

        bind();

        //at this point, we are mapped into custom classes to represent stuff
        registerPrototypes();
    }

    /**
     * Bind ourselves; extract stuff from the root node
     *
     * @throws CdlXmlParsingException
     */
    private void bind() throws CdlXmlParsingException {
        //bind ourselves

        //get our target namespace.
        targetNamespaceAttr = GenericAttribute.findAndBind(
                ATTR_TARGET_NAMESPACE,
                URIAttribute.class,
                getRoot(),
                false,
                false);
        if (targetNamespaceAttr != null) {
            targetNamespace = targetNamespaceAttr.getUri();
        }



        //bind the root
        getRoot().bind();

        //now process our children
        for (Node node : root.nodes()) {
            if (!(node instanceof Element)) {
                continue;
            }
            ElementEx child = (ElementEx) node;

            if (child instanceof Import) {
                imports.add((Import) child);
                continue;
            }
            //type declarations
            //what to do with these?
            if (child instanceof Type) {
                types = (Type) child;
                continue;
            }

            //<configuration> and system elements
            if (child instanceof ToplevelList) {
                ToplevelList toplevelList = (ToplevelList) child;
                if (ELEMENT_CONFIGURATION.equals(toplevelList.getLocalName())) {
                    configuration = toplevelList;
                } else {
                    system = toplevelList;
                }
                continue;
            }

            //add a doc node
            if (child instanceof Documentation) {
                Documentation documentation = (Documentation) child;
                log.info("Ignoring documentation " + child);
                continue;
            }

            //if we get here, then either there is stuff that we don't recognise
            //or its in another namespace
            if (CDL_NAMESPACE.equals(child.getNamespaceURI())) {
                //strange stuff here
                throw new CdlXmlParsingException("Unknown element " + child);
            } else {
                //do nothing
                log.info("Ignoring unknown element " + child);
            }
        }

    }

    /**
     * All imports are processed here
     * <p/>
     * the way we import is that every import is inserted under the
     * configuration. this keeps everything under the tree.
     *
     * @throws CdlException TODOimplement
     */
    public void parsePhaseProcessImports() throws CdlException, IOException,
            ParsingException {
        log.debug("Import processing not implemented");
        for (Import imp : getImports()) {
            CdlDocument imported = getParseContext().importDocument(imp);
        }


    }

    /**
     * Apply extends logic to the document
     *
     * @throws CdlResolutionException
     */
    public void parsePhaseExtendProcessing() throws CdlException {
        //extends our extendendables
        ExtendsResolver extendsResolver = new ExtendsResolver(
                getParseContext());
        extendsResolver.resolveExtends(this);
    }

    public void parsePhaseResolveVariables() throws CdlException {

    }

    public void parsePhaseEvaluateExpressions() throws CdlException {

    }


    /**
     * register all our prototypes
     *
     * @throws CdlDuplicatePrototypeException if there is one in use already
     */
    private void registerPrototypes() throws CdlDuplicatePrototypeException {
        if (configuration != null) {
            configuration.registerPrototypes();
        }

        if (system != null) {
            system.registerPrototypes();
        }
    }

    /**
     * Write something to a smartfrog file. Parent elements should delegate to
     * their children as appropriate.
     * <p/>
     * The Base class delegates to children and otherwise does nothing
     *
     * @param out
     * @throws java.io.IOException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     */
    public void toSmartFrog(GenerateContext out) throws IOException,
            CdlException {
        if (getConfiguration() != null) {
            getConfiguration().toSmartFrog(out);
        }
        if (getSystem() != null) {
            out.enter(GenerateContext.COMPONENT_SFSYSTEM);
            getSystem().toSmartFrog(out);
            out.leave();
        }
    }
}
