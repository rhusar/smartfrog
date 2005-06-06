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

import org.smartfrog.sfcore.languages.cdl.CdlParsingException;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.sfcore.languages.cdl.utils.ElementIterator;
import org.smartfrog.sfcore.languages.cdl.utils.IteratorRelay;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;

import nu.xom.ParsingException;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParentNode;

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
     * Original Document
     */
    private Document document;


    /**
     * our root node
     */
    private Element root;

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
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_NAMESPACE = "The element is not in CDL namespace";
    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_ELEMENT = "Expected an element named " ;
    /*
    public static final String ERROR_BAD_PATHLANGUAGE = "Attribute 'pathlanguage' is not allowed to appear in element 'cdl:cdl'";
    public static final String ERROR_NO_PATHLANGUAGE = "pathlanguage attribute not found";
*/

    public CdlDocument(Document doc) throws CdlParsingException {
        this.document = doc;
    }

    public CdlDocument(ParsingException exception) {
        this.exception = exception;
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

    private ParsingException exception;

    public ParsingException getException() {
        return exception;
    }

    public int getErrorLine() {
        return exception == null ? 0 : exception.getLineNumber();
    }

    public int getErrorColumn() {
        return exception == null ? 0 : exception.getColumnNumber();
    }

    public void throwAnyException() throws ParsingException {
        if (exception != null) {
            throw exception;
        }
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
     * @throws CdlParsingException
     */
    public void validate() throws CdlParsingException {
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

    /**
     * parse the document
     *
     * @throws CdlParsingException
     */
    protected void parse() throws CdlParsingException {
        for(Element element: elements(document)) {
            verifyInCdlNamespace(element);
            verifyNodeName(element, CddlmConstants.ELEMENT_NAME_ROOT);
            processRootNode(element);
        }
    }

    private void verifyInCdlNamespace(Element element)
            throws CdlParsingException {
        CdlParsingException.assertValid(CddlmConstants.XML_CDL_NAMESPACE.equals(element.getNamespaceURI()),
                ERROR_WRONG_NAMESPACE);

    }

    private void verifyNodeName(Element element,String name)
            throws CdlParsingException {
        CdlParsingException.assertValid(CddlmConstants.ELEMENT_NAME_ROOT.equals(element.getLocalName()),
                ERROR_WRONG_ELEMENT +name
                    +" but got "+element);
    }

    /**
     * from the document, parser ourselves
     */
    private void processDocument() throws CdlParsingException {
        assert document != null;
        processRootNode(document.getRootElement());

    }

    /**
     * this is the root node; lets extract everything from it
     *
     * @param node
     * @throws CdlParsingException
     */
    private void processRootNode(Element node) throws CdlParsingException {
        root = node;
        for (Element child : elements(root)) {

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
                //TODO
                continue;
            }

            //if we get here, then either there is stuff that we don't recognise
            //or its in another namespace
            if(!DocNode.inCdlNamespace(child)) {
                //strange stuff here
                throw new CdlParsingException("Unknown element "+child);
            } else {
                //do nothing
                //TODO: log this?
            }

        }

        //at this point, we are mapped into custom classes to represent stuff

    }

    /**
     * Iterate just over elements
     * @param element
     * @return
     */
    public ElementIterator elementIterator(ParentNode element) {
        return new ElementIterator(element);
    }

    public IteratorRelay<Element> elements(ParentNode element) {
        return new IteratorRelay(elementIterator(element));
    }

}
