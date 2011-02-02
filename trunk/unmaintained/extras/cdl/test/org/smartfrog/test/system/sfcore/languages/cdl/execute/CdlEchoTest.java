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
package org.smartfrog.test.system.sfcore.languages.cdl.execute;

import org.smartfrog.services.cddlm.cdl.demo.Echo;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

/**
 * created 24-Jun-2005 15:02:17
 */

public class CdlEchoTest extends XmlTestBase {

    public CdlEchoTest(String name) {
        super(name);
    }

    public static final String FILES = "files/sfcdl/";
    public static final String VALID= FILES + "valid/";
    public static final String INVALID = FILES + "invalid/";
    public static final String ECHO_CDL = VALID + "echo.cdl";



    public void testEchoValid() throws Exception {
        assertValidCDL(ECHO_CDL);
    }

    public void testLanguageDetermination() throws Exception {
        assertEquals("sf",determineLanguage("test.sf"));
        assertEquals("cdl", determineLanguage("test.cdl"));
        assertEquals("cdl", determineLanguage(ECHO_CDL));
        assertEquals("xml", determineLanguage("test.xml"));
        assertEquals("", determineLanguage("test."));
        assertEquals("sf", determineLanguage("test"));
    }

    public void testParse() throws Exception {
        Phases phases=parse(ECHO_CDL);
    }

    public void NotestSimplePackage() throws Throwable {
        Prim application = null;
        try {
            application = deployExpectingSuccess(ECHO_CDL, "testSimplePackage");
            Object echo=resolveAttribute(application,"__echo");
            String message=resolveStringAttribute((Prim)echo, Echo.ATTR_MESSAGE);
            assertTrue("Empty message attribute",message.length()>0);

        } finally {
            terminateApplication(application);
        }
    }


}
