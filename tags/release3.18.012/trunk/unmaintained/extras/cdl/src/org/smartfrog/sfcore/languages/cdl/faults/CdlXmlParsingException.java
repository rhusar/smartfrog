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
package org.smartfrog.sfcore.languages.cdl.faults;

import nu.xom.Node;


/**
 * we add our own exception under
 * This is a runtime exception as it can be thrown on any getter that queries
 * an attribute of an element.
 * created Jul 15, 2004 4:57:59 PM
 */

public class CdlXmlParsingException extends CdlRuntimeException {

    /**
     * Xom nodes cannot be serialized.
     */
    private transient Node source;

    public CdlXmlParsingException() {
    }

    public CdlXmlParsingException(String message) {
        super(message);
    }

    public CdlXmlParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CdlXmlParsingException(Throwable cause) {
        super(cause);
    }

    public CdlXmlParsingException(Node node) {
        setSource(node);
    }

    public CdlXmlParsingException(Node node,String message) {
        super(message);
        setSource(node);
    }

    public CdlXmlParsingException(Node node,String message, Throwable cause) {
        super(message, cause);
        setSource(node);
    }

    public CdlXmlParsingException(Node node,Throwable cause) {
        super(cause);
        setSource(node);
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    /**
     * Assert that a test holds, if not, throw an exception.
     * @param test test to verify
     * @param errorText text in exception
     * @throws CdlXmlParsingException iff test==false
     */
    public static void assertValid(boolean test,String errorText) throws CdlXmlParsingException {
        if(!test) {
            throw new CdlXmlParsingException(errorText);
        }
    }
}
