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
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.GenericAttribute;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.URIAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsResolver;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.utils.IteratorRelay;
import org.smartfrog.sfcore.languages.cdl.utils.NodeIterator;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * This represents a parsed CDL document, or an error caused during parsing.
 */

public class CdlDocument extends DocumentedNode {

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * Original Xom Document
     */
    private Document document;

    /**
     * the target namespace of this document (may be null)
     */
    private URI targetNamespace;
    private URIAttribute targetNamespaceAttr;

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

    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_NAMESPACE = "The element is not in CDL namespace";
    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_ELEMENT = "Expected an element named ";

    public CdlDocument() {
        super();
        owner = this;
    }

    public CdlDocument(ParseContext parseContext) {
        this();
        this.parseContext = parseContext;
    }

    public CdlDocument(Document doc) throws CdlException {
        super(doc.getRootElement());
        owner = this;
        bind(doc);
    }

    /**
     * string info for debugging
     *
     * @return
     */
    public String toString() {
        return "CDL document namespace=" +
                (targetNamespace == null ?
                "local" :
                targetNamespace.toString());
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

    public void setSystem(ToplevelList system) {
        this.system = system;
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
     * @see ToplevelList#lookup(QName)
     */
    public PropertyList lookup(QName name) {
        return configuration.lookup(name);
    }


    /**
     * Verify that we are in the CDL namespace
     *
     * @param element
     * @throws CdlXmlParsingException
     */
    private void verifyInCdlNamespace(Element element)
            throws CdlXmlParsingException {
        CdlXmlParsingException.assertValid(CddlmConstants.XML_CDL_NAMESPACE.equals(
                element.getNamespaceURI()),
                ERROR_WRONG_NAMESPACE);

    }

    /**
     * verify the name of a node
     *
     * @param element
     * @param name
     * @throws CdlXmlParsingException
     */
    private void verifyNodeName(Element element, String name)
            throws CdlXmlParsingException {
        CdlXmlParsingException.assertValid(
                CddlmConstants.ELEMENT_NAME_ROOT.equals(element.getLocalName()),
                ERROR_WRONG_ELEMENT +
                name
                + " but got " + element);
    }

    /**
     * call our superclass binding. This does not do any parsing of the XML
     *
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException
     *
     */
    public void bind(Element element) throws CdlXmlParsingException {
        super.bind(element);
    }

    /**
     * bind the document. This does not do any parsing of the XML
     *
     * @param doc
     * @throws CdlException
     */
    public void bind(Document doc) throws CdlException {
        assert doc != null;
        this.document = doc;
    }

    /**
     * Get the root node
     *
     * @return
     */
    Element getRoot() {
        return getNode();
    }


    /**
     * This is the complete parse process. The parse context is bound, and the
     * different phases are invoked
     *
     * @param context the parsing context
     * @throws CdlXmlParsingException
     */
    public void parse(ParseContext context) throws CdlException {
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
        Element root = getRoot();
        verifyInCdlNamespace(root);
        verifyNodeName(root, CddlmConstants.ELEMENT_NAME_ROOT);

        //get our target namespace.
        targetNamespaceAttr = GenericAttribute.findAndBind(
                ATTR_TARGET_NAMESPACE,
                URIAttribute.class,
                getNode(),
                false,
                false);
        if (targetNamespaceAttr != null) {
            targetNamespace = targetNamespaceAttr.getUri();
        }

        //now process our children
        for (Node childnode : nodes(root)) {
            if (!(childnode instanceof Element)) {
                continue;
            }
            Element child = (Element) childnode;
            //imports come first
            if (Import.isA(child)) {
                imports.add(new Import(child));
                continue;
            }

            //type declarations
            //what to do with these?
            if (Type.isA(child)) {
                types = new Type(child);
                continue;
            }

            //<configuration> element
            if (ToplevelList.isConfigurationElement(child)) {
                configuration = new ToplevelList(this, child);
                continue;
            }

            //<system> element
            if (ToplevelList.isSystemElement(child)) {
                system = new ToplevelList(this, child);
                continue;
            }

            //add a doc node
            if (Documentation.isA(child)) {
                Documentation documentation = new Documentation(child);
                log.info("Ignoring documentation " + child);
                continue;
            }

            //if we get here, then either there is stuff that we don't recognise
            //or its in another namespace
            if (DocNode.inCdlNamespace(child)) {
                //strange stuff here
                throw new CdlXmlParsingException("Unknown element " + child);
            } else {
                //do nothing
                log.info("Ignoring unknown element " + child);
            }
        }
        //at this point, we are mapped into custom classes to represent stuff
        registerPrototypes();
    }

    /**
     * All imports are processed here
     *
     * @throws CdlException
     * @todo: implement
     */
    public void parsePhaseProcessImports() throws CdlException {
        log.debug("Import processing not implemented");
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

}
