/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.cdl;

import nu.xom.Document;
import nu.xom.ParsingException;

/**
 * This represents a parsed CDL document, or an error caused during parsing.
 * created Jul 15, 2004 3:52:57 PM
 */

public class CdlDocument {

    private Document doc;

    private ParsingException exception;


    public CdlDocument(Document doc) {
        this.doc = doc;
    }

    public CdlDocument(ParsingException exception) {
        this.exception = exception;
    }

    /**
     * validity test
     * @return
     */
    public boolean isValid() {
        //TODO
        return doc!=null;
    }

    public Document getDoc() {
        return doc;
    }

    public ParsingException getException() {
        return exception;
    }

    public int getErrorLine() {
        return exception == null ? 0 : exception.getLineNumber();
    }

    public int getErrorColumn() {
        return exception==null?0:exception.getColumnNumber();
    }

    public void throwAnyException() throws ParsingException {
        if (exception!=null) {
            throw exception;
        }
    }
}
