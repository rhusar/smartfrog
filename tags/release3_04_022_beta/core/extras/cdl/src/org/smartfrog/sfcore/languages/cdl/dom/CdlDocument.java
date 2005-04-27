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
import nu.xom.ParsingException;
import nu.xom.Node;
import org.smartfrog.sfcore.languages.cdl.CdlParsingException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.utils.ParentNodeIterable;

import javax.xml.namespace.QName;

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

    private ParsingException exception;

    private ToplevelList configuration= new ToplevelList();

    private ToplevelList system;


    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_NAMESPACE = "The document is not in CDL namespace";
    /**
     * error message for tests {@value}
     */
    public static final String ERROR_WRONG_ROOT_ELEMENT = "Root element is not 'cdl'";
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
      * @return configuration; null for no none
     */
    public ToplevelList getConfiguration() {
        return configuration;
    }

    /**
     * Get the system declaration
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
        CdlParsingException.assertValid(Constants.CDL_NAMESPACE.equals(uri),ERROR_WRONG_NAMESPACE);
        CdlParsingException.assertValid(Constants.CDL_ELT_CDL.equals(root.getLocalName()),ERROR_WRONG_ROOT_ELEMENT);

        /*
        Attribute pathLangAttr = root.getAttribute("pathlanguage", Constants.CDL_NAMESPACE);
        if ( pathLangAttr != null ) {
            assertTrue(ERROR_NO_PATHLANGUAGE, pathLangAttr != null);
            String language = pathLangAttr.getValue();
            assertTrue(ERROR_BAD_PATHLANGUAGE, Constants.XPATH_URI.equals(language));
        }
        */

        if(configuration!=null) {
            configuration.validateToplevel();
        }
        if (system!= null) {
            system.validateToplevel();
        }

    }

    /**
     * Look up a toplevel node
     * @see ToplevelList#lookup(QName)
     * @param name
     * @return
     */
    public PropertyList lookup(QName name) {
        return configuration.lookup(name);
    }

    /**
     * parse the document
     * @throws CdlParsingException
     */
    protected void parse() throws CdlParsingException {
        for(Node element:ParentNodeIterable.iterateOver(document)) {
            

        }
    }

}
