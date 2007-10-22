/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.ant.test.unit;

import org.smartfrog.tools.ant.PropertyFile;
import org.smartfrog.tools.ant.test.TaskTestBase;

/**
 * Test the parser
 *
 * @author steve loughran Date: 25-Feb-2004 Time: 23:48:51
 */
public class ParseTest extends TaskTestBase {
    public static final String SUCCESSFUL_PARSE = "SFParse: SUCCESSFUL";

    public ParseTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "parse.xml";
    }

    public void testNoop() {
        expectLogContaining("testNoop", "No source files");
    }

    public void testValid1() {
        expectLogContaining("testValid1", SUCCESSFUL_PARSE);
    }

    public void testValid2() {
        expectLogContaining("testValid2", SUCCESSFUL_PARSE);
    }


    public void testSubdir() {
        expectLogContaining("testSubdir", SUCCESSFUL_PARSE);
    }

    public void testInvalid() {
        expectExceptionWithLogContaining("testInvalid",
                "SmartFrogTypeResolutionException",
                "Reference not found");
    }

    public void testMissingFile() {
        expectBuildExceptionContaining("testMissingFile",
                "not found",
                "File not found :");
    }

    public void testTwoFiles() {
        expectBuildException("testTwoFiles", "parse failure");
    }

    public void testVerbose() {
        expectLogContaining("testVerbose",
                "sfClass \"org.smartfrog.sfcore.compound.CompoundImpl\";");
    }

    public void testQuiet() {
        expectLogContaining("testQuiet", SUCCESSFUL_PARSE);
        assertNotInLog("port 8080;");
    }

    public void testVerboseQuiet() {
        expectLogContaining("testVerboseQuiet", SUCCESSFUL_PARSE);
        assertNotInLog("port 8080;");
    }

    public void testEmptyPropertyFile() {
        expectBuildExceptionContaining("testEmptyPropertyFile",
                "empty propertyFile",
                PropertyFile.ERROR_NO_FILE_ATTRIBUTE);
    }

    public void testNoProperty() {
        expectBuildExceptionContaining("testNoProperty",
                "undefined property in .SF file",
                "parse failure");
    }

    public void testProperty() {
        expectLogContaining("testProperty", SUCCESSFUL_PARSE);
    }

    public void testValidPropertyFile() {
        expectLogContaining("testValidPropertyFile", SUCCESSFUL_PARSE);
    }

    public void testPropertySet() {
        expectLogContaining("testPropertySet", SUCCESSFUL_PARSE);
    }
}
