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
package org.smartfrog.sfcore.languages.cdl.importing;

import org.smartfrog.sfcore.languages.cdl.process.ProcessingPhase;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.Import;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.ParseContext;

import java.io.IOException;

import nu.xom.ParsingException;

/**
 * created 04-Jan-2006 15:44:47
 */

public class ImportProcessor implements ProcessingPhase {



    /**
     * Process a document.
     *
     * @param document the document to work on
     * @throws java.io.IOException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     * @throws nu.xom.ParsingException
     */
    public void process(CdlDocument document) throws IOException, CdlException, ParsingException {

            ParseContext context = document.getParseContext();
            for (Import imp : document.getImports()) {
                CdlDocument imported = context.importDocument(document, imp);
                //TODO: do we need to do anything with the imported doc?
            }

    }


    /**
     * @return a string representation of the phase
     */
    public String toString() {
        return "Document Importer";
    }

}
