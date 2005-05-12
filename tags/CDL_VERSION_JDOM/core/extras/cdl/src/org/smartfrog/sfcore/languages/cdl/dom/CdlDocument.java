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
import static org.smartfrog.sfcore.languages.cdl.Constants.*;
import static org.ggf.cddlm.generated.api.CddlmConstants.*;
import org.smartfrog.sfcore.languages.cdl.utils.TypeFilter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.Content;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.filter.Filter;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * This represents a parsed CDL document, or an error caused during parsing.
 */

public class CdlDocument extends DocumentedNode {

    /**
     * Original Document
     */
    private Document document;

    /**
     * Any exception that ocurred during parsing.
     */

    private JDOMException exception;


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
    private ToplevelList system;




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

    public CdlDocument(Document doc) {
        this.document = doc;
    }

    public CdlDocument(JDOMException exception) {
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

    public JDOMException getException() {
        return exception;
    }

    public void throwAnyException() throws JDOMException {
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

        /*
        Attribute pathLangAttr = root.getAttribute("pathlanguage", Constants.CDL_NAMESPACE);
        if ( pathLangAttr != null ) {
            assertTrue(ERROR_NO_PATHLANGUAGE, pathLangAttr != null);
            String language = pathLangAttr.getValue();
            assertTrue(ERROR_BAD_PATHLANGUAGE, Constants.XPATH_URI.equals(language));
        }
        */

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
        for(Element element:elementIterator(document)) {
            verifyInCdlNamespace(element);
            verifyNodeName(element, ELEMENT_NAME_ROOT);
            processRootNode(element);
        }
    }

    private void verifyInCdlNamespace(Element element)
            throws CdlParsingException {
        CdlParsingException.assertValid(CDL_NAMESPACE.equals(element.getNamespaceURI()),
                ERROR_WRONG_NAMESPACE);

    }

    private void verifyNodeName(Element element,String name)
            throws CdlParsingException {
        CdlParsingException.assertValid(ELEMENT_NAME_ROOT.equals(element.getName()),
                ERROR_WRONG_ELEMENT +name
                    +" but got "+element);
    }

    /**
     * this is the root node; lets extract everything from it
     *
     * @param node
     * @throws CdlParsingException
     */
    private void processRootNode(Element node) throws CdlParsingException {
        root = node;
        for (Element element : elementIterator(root)) {
            verifyInCdlNamespace(element);


        }


    }

    /**
     * Get all content nodes of our document. Will be null if there is no
     * content
     *
     * @return
     */
    public List<Content> getContent() {
        assert document != null: "no document";
        return document.getContent();
    }

    /**
     * iterate over the list
     * @param element
     * @return
     */
    public List<Content> iterator(Parent element) {
        return element.getContent();
    }

    /**
     * iterate over the list
     *
     * @param element
     * @param filter
     * @return
     */
    public List<Content> iterator(Parent element,Filter filter) {
        return element.getContent(filter);
    }

    /**
     * Iterate just over elements
     * @param element
     * @return
     */
    public List<Element> elementIterator(Parent element) {
        return element.getContent(TypeFilter.elementFilter());
    }

}
