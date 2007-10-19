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
import nu.xom.Serializer;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.xml.java5.iterators.IteratorRelay;
import org.smartfrog.services.xml.java5.iterators.NodeIterator;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.GenericAttribute;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.URIAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.generate.DescriptorSource;
import org.smartfrog.sfcore.languages.cdl.importing.ImportProcessor;
import org.smartfrog.sfcore.languages.cdl.process.ProcessingPhase;
import org.smartfrog.sfcore.languages.cdl.references.EarlyReferenceProcessor;
import org.smartfrog.sfcore.languages.cdl.references.ExtractReferenceOperation;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsProcessor;
import org.smartfrog.sfcore.languages.cdl.resolving.RegisterPrototypesProcessor;
import org.smartfrog.sfcore.languages.cdl.resolving.VerifyExtendsComplete;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


/**
 * This represents a parsed CDL document, or an error caused during parsing.
 */

public class CdlDocument implements Names, DescriptorSource {

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * URL of the document; may be null
     */
    private URL documentURL;

    /**
     * the name of the resource used to source the document
     */
    private String documentResource;

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
    private SystemElement system;

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

    public CdlDocument(Document doc) throws CdlException {
        bind((DocumentNode) doc);
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
    public SystemElement getSystem() {
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
    protected void setSystem(SystemElement system) {
        this.system = system;
    }

    /**
     * replace the system element in our cache and in the document itself.
     *
     * @param newsystem
     */
    public void replaceSystem(SystemElement newsystem) {

        if (system != null ) {
            root.replaceChild(system, newsystem);
        }
        setSystem(newsystem);
    }

    public void replaceConfiguration(ToplevelList newconfiguration) {
        if(configuration != null) {
            root.replaceChild(configuration,newconfiguration);
        }
        setConfiguration(newconfiguration);
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
     * The url from where the doc came; may be null
     *
     * @return a URL, possibly including the classpath: type
     */
    public URL getDocumentURL() {
        return documentURL;
    }

    public void setDocumentURL(URL documentURL) {
        this.documentURL = documentURL;
    }

    /**
     * Get the (often null) resource path of a doc
     *
     * @return string value or null
     */
    public String getDocumentResource() {
        return documentResource;
    }

    /**
     * Set the resource where this document came from.
     *
     * @param documentResource
     */
    public void setDocumentResource(String documentResource) {
        this.documentResource = documentResource;
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
     * @return the node or null for nothing of that name (or even, no configuration)
     */
    public PropertyList lookup(QName name) {
        if(configuration==null) {
            return null;
        }
        return configuration.getChildTemplateMatching(name);
    }

    /**
     * bind the document. This does not do any parsing of the XML
     *
     * @param graph the document graph
     * @throws CdlException
     */
    public void bind(DocumentNode graph) throws CdlException {
        assert graph != null;
        this.document = graph;
        graph.setOwner(this);
        //extract the URL From the doc
        String uri = graph.getBaseURI();
        if (uri != null && uri.length() > 0) {
            try {
                URL url = new URL(uri);
                setDocumentURL(url);
            } catch (MalformedURLException e) {
                log.info("Could not make a URL of " + uri, e);
            }
        } else {
            log.debug("Unknown origin of document");
        }

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
        List<ProcessingPhase> phases = createPhaseList();
        parseAndApplyPhases(context, phases);
    }

    public void parseImportedDocument(ParseContext context,String namespace) throws CdlException, IOException,
            ParsingException {
        List<ProcessingPhase> phases = createImportPhaseList(namespace);
        parseAndApplyPhases(context, phases);
    }

    /**
     * Create the phase list for processing an import. In a debug build, this
     * includes checking that extends was finished.
     * @param namespace
     * @return
     */
    private List<ProcessingPhase> createImportPhaseList(String namespace) {
        List<ProcessingPhase> phases = new ArrayList<ProcessingPhase>(8);
        //register the protos
        phases.add(new RegisterPrototypesProcessor(namespace));
        //imports
        phases.add(new ImportProcessor());
        //extract all reference bindings
        phases.add(ExtractReferenceOperation.createPhase());
        //do the extends processing
        phases.add(new ExtendsProcessor());
        //debug builds and a sanity check
        if (Constants.POLICY_DEBUG_RELEASE) {
            phases.add(VerifyExtendsComplete.createPhase());
        }
        //now do early references
        phases.add(new EarlyReferenceProcessor());
        return phases;
    }

    private List<ProcessingPhase> createPhaseList() {
        List<ProcessingPhase> phases = new ArrayList<ProcessingPhase>(8);
        //register the protos
        phases.add(new RegisterPrototypesProcessor());
        //imports
        phases.add(new ImportProcessor());
        //extract all reference bindings
        phases.add(ExtractReferenceOperation.createPhase());
        //do the extends processing
        phases.add(new ExtendsProcessor());
        //debug builds and a sanity check
        if (Constants.POLICY_DEBUG_RELEASE) {
            phases.add(VerifyExtendsComplete.createPhase());
        }
        //now do early references
        phases.add(new EarlyReferenceProcessor());
        return phases;
    }

    private void parseAndApplyPhases(ParseContext context, List<ProcessingPhase> phases) throws CdlException,
            IOException, ParsingException {
        setParseContext(context);
        parsePhaseBuildDom();

        //list is full, so execute.
        String currentPhase = "";
        try {
            for (ProcessingPhase phase : phases) {
                currentPhase = phase.toString();
                phase.process(this);
            }
        } catch (CdlException e) {
            e.addDetailText(Constants.QNAME_DETAIL_PHASE, currentPhase);
            e.addDetail(getRoot(), true);
            throw e;
        }
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
        for (Node node : root) {
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
                configuration = toplevelList;
                continue;
            }
            if (child instanceof SystemElement) {
                SystemElement toplevelList = (SystemElement) child;
                system = toplevelList;
                continue;
            }

            //add a doc node
            if (child instanceof Documentation) {
                Documentation documentation = (Documentation) child;
                //TODO: do something
                continue;
            }

            //if we get here, then either there is stuff that we don't recognise
            //or its in another namespace
            if (CDL_NAMESPACE.equals(child.getNamespaceURI())) {
                //strange stuff in the doc
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
        ProcessingPhase processor=new ImportProcessor();
        processor.process(this);
    }

    /**
     * Apply extends logic to the document
     *
     * @throws CdlResolutionException
     */
    public void parsePhaseExtendProcessing() throws CdlException, IOException, ParsingException {
        //extends our extendendables
        ProcessingPhase processor=new ExtendsProcessor();
        processor.process(this);
    }


    /**
     * register all our prototypes
     *
     * @throws CdlDuplicatePrototypeException if there is one in use already
     * @param namespace
     */
    public void registerPrototypes(String namespace) throws CdlDuplicatePrototypeException {
        if (configuration != null) {
            configuration.registerPrototypes(namespace);
        }
    }

    /**
     * Add a new description
     *
     * @param parent node: add attribute or children
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *
     */
    public void exportDescription(CdlComponentDescription parent) throws RemoteException, SmartFrogException {

        if (getSystem() != null) {
            getSystem().exportDescription(parent);
        }

    }

    /**
     * convert a doc to a CD graph
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public CdlComponentDescription convertToComponentDescription() throws SmartFrogException, RemoteException {
        //create a stub parent
        CdlComponentDescription root=new CdlComponentDescriptionImpl(null,null);
        //export everything
        exportDescription(root);
        //then extract the system from underneath
        final QName name = getSystem().getQName();
        final CdlComponentDescription system = (CdlComponentDescription) root.resolve(name, true);
        return system;
    }


    /**
     * Save a doc to a stream.
     * does not close the stream afterwards.
     * @param out
     * @throws IOException
     */

    public void print(PrintStream out) throws IOException {
        Serializer ser = new Serializer(out);
        ser.write(this.getDocument());
    }


    /**
     * Save the document to a text file in UTF-8 format,
     * prettily where appropriate.
     * @param file destination
     * @throws IOException
     */
    public void printToFile(File file) throws IOException {
        PrintStream print = null;
        try {
            print = new PrintStream(file, "UTF-8");
            print(print);
        } finally {
            FileSystem.close(print);
        }
    }

    public String printToString() throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        print(new PrintStream(out));
        return out.toString("UTF-8");
    }

}
