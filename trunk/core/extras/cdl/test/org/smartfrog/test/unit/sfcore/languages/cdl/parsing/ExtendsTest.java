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
package org.smartfrog.test.unit.sfcore.languages.cdl.parsing;

import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;

import java.io.IOException;

import nu.xom.ParsingException;

/**
 * created 10-Jun-2005 16:53:50
 */

public class ExtendsTest extends XmlTestBase {

    public ExtendsTest(String name) {
        super(name);
    }

    public void testExtendsIsExtracted() throws IOException, CdlException,
            ParsingException {
        CdlDocument cdlDocument = loadValidCDL(CDL_DOC_EXTENDS_1);
        
    }
}
