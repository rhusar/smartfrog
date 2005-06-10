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

import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.sfcore.languages.cdl.utils.ElementIterator;
import org.smartfrog.sfcore.languages.cdl.utils.IteratorRelay;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.utils.NodeIterator;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;

import nu.xom.ParsingException;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParentNode;
import nu.xom.Node;

import java.util.ArrayList;
import java.util.List;


/**
 * This represents a parsed CDL document, or an error caused during parsing.
 */

public class CdlDocument extends DocumentedNode {

    /**
     * a log
     */
    private Log log=ClassLogger.getLog(this);

    /**
     * Original Xom Document
     */
    private Document document;



    /**
     * configuration elements
     *
     */
    private ToplevelList configuration = new ToplevelList();

    /**
     * System declaration
     */
    private ToplevelList system = new ToplevelList();

    /**
     * Our list of imports
     */
    private List<Import> imports=new ArrayList<Import>();


    /**
     * Our types
     */
    private Type types = null;

    /**
     * the parse context for this document. We may or may not be the primary
     * document in the context. Indeed, we wont be for any importation, but that
     * is mostly irrelevant.
     */
    private ParseContext parseContext= null;

    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_NAMESPACE = "The element is not in CDL namespace";
    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_ELEMENT = "Expected an element named " ;

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
        owner=this;
        bind(doc);
    }


    /**
     * get the parse context
     * @return
     */
    public ParseContext getParseContext() {
        return parseContext;
    }

    /**
     * set the parse context.
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

    /**
     * this routine encodes all the logic around the validity of the scham
     *
     * @throws CdlXmlParsingException
     */
    public void validate() throws CdlXmlParsingException {
        Element root = document.getRootElement();
        String uri = root.getNamespaceURI();

        if (configuration != null) {
            configuration.validateToplevel();
        }
        if (system != null) {
            system.validateToplevel();
        }

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


    private void verifyInCdlNamespace(Element element)
            throws CdlXmlParsingException {
        CdlXmlParsingException.assertValid(CddlmConstants.XML_CDL_NAMESPACE.equals(element.getNamespaceURI()),
                ERROR_WRONG_NAMESPACE);

    }

    private void verifyNodeName(Element element,String name)
            throws CdlXmlParsingException {
        CdlXmlParsingException.assertValid(CddlmConstants.ELEMENT_NAME_ROOT.equals(element.getLocalName()),
                ERROR_WRONG_ELEMENT +name
                    +" but got "+element);
    }

    /**
     * our binding here sets the root node
     *
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException
     *
     */
    public void bind(Element element) throws CdlXmlParsingException {
        super.bind(element);
    }

    public void bind(Document doc) throws CdlException {
        assert doc != null;
        this.document = doc;
        parse();
    }

    /**
     * Get the root node
     * @return
     */
    Element getRoot() {
        return getNode();
    }


    /**
     * we have the root node; lets extract everything from it
     *
     * @throws CdlXmlParsingException
     */
    private void parse() throws CdlXmlParsingException {
        Element root=getRoot();
        verifyInCdlNamespace(root);
        verifyNodeName(root, CddlmConstants.ELEMENT_NAME_ROOT);
        for (Node childnode: nodes(root)) {
            if(!(childnode instanceof Element)) {
                continue;
            }
            Element child=(Element)childnode;
            //imports come first
            if(Import.isA(child)) {
                imports.add(new Import(child));
                continue;
            }

            //type declarations
            //what to do with these?
            if(Type.isA(child)) {
                types=new Type(child);
                continue;
            }

            //<configuration> element
            if(ToplevelList.isConfigurationElement(child)) {
                configuration=new ToplevelList(child);
                continue;
            }

            //<system> element
            if (ToplevelList.isSystemElement(child)) {
                system = new ToplevelList(child);
                continue;
            }

            //add a doc node
            if(Documentation.isA(child)) {
                Documentation documentation = new Documentation(child);
                log.info("Ignoring documentation " + child);
                continue;
            }

            //if we get here, then either there is stuff that we don't recognise
            //or its in another namespace
            if(DocNode.inCdlNamespace(child)) {
                //strange stuff here
                throw new CdlXmlParsingException("Unknown element "+child);
            } else {
                //do nothing
                //TODO: log this?
                log.info("Ignoring unknown element "+child);
            }

        }

        //at this point, we are mapped into custom classes to represent stuff

    }

    /**
     * Iterate just over elements
     * @param element
     * @return
     */
    public NodeIterator nodeIterator(ParentNode element) {
        return new NodeIterator(element);
    }

    public IteratorRelay<Node> nodes(ParentNode element) {
        return new IteratorRelay<Node>(nodeIterator(element));
    }

}
