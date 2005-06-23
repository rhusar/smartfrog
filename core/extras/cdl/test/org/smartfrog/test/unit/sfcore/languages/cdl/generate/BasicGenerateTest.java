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
package org.smartfrog.test.unit.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.generate.SmartFrogSourceGenerator;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

import java.io.File;
import java.io.IOException;

/**
 * created 22-Jun-2005 12:59:27
 */

public class BasicGenerateTest extends XmlTestBase {

    public BasicGenerateTest(String name) {
        super(name);
    }


    public void testFirstGenerate() throws Exception {
        CdlDocument cdlDocument = parseValidCDL(
                CDL_DOC_EXTENDS_CHILD_EXTENSION);
        saveToSmartFrog(cdlDocument);
    }

    private File saveToSmartFrog(CdlDocument cdlDocument) throws IOException,
            CdlException {
        SmartFrogSourceGenerator generator = new SmartFrogSourceGenerator(
                cdlDocument);
        File tempFile = File.createTempFile("cdl", ".sf");
        log.info("Tempfile=" + tempFile);
        generator.generate(tempFile);
        return tempFile;
    }


    public void testEcho() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_SF_ECHO);
        saveToSmartFrog(cdlDocument);
    }

}
