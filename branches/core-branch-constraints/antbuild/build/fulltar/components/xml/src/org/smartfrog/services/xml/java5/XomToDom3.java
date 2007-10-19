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
package org.smartfrog.services.xml.java5;

import nu.xom.Document;
import nu.xom.converters.DOMConverter;
import org.smartfrog.services.xml.utils.ParserHelper;
import org.smartfrog.services.xml.utils.XmlConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * created 30-Nov-2005 11:03:43
 */

public class XomToDom3 {
    /**
     * How to look for a dom3 parser
     * {@value}
     */
    private static final String DOM3 = "XML 3.0";


    private static void configureDomRegistry() {
        String current=System.getProperty(DOMImplementationRegistry.PROPERTY);
        if(current==null) {
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                    XmlConstants.DOM3_PARSER_LIST);
        }
    }

    /**
     * Get a Dom3 impl
     * @return the implementation
     * @throws RuntimeException if things go wrong
     */
    public static DOMImplementation getDom3Implementation() {
        try {
            //configureDomRegistry();
            // get an instance of the DOMImplementation registry
            DOMImplementationRegistry registry =
                    DOMImplementationRegistry.newInstance();
            // get a DOM implementation the Level 3 XML module
            DOMImplementation domImpl =
                    registry.getDOMImplementation(DOM3);
            if(domImpl==null) {
                //fallback
                domImpl= ParserHelper.loadDomImplementation();
            }
            if(domImpl==null) {
                //crisis
                throw new RuntimeException("Failed to create a parser with the attributes:"+DOM3);
            }
            return domImpl;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert from a Xom document to a W3C Dom Document
     * @param xom xom document
     * @return the Dom equivalent
     * @throws RuntimeException for dom instantiation problems
     */
    public static org.w3c.dom.Document fromXom(Document xom) {
        DOMImplementation domImpl=getDom3Implementation();
        assert domImpl!=null;
        return DOMConverter.convert(xom,domImpl);
    }

}
