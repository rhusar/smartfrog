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


package org.smartfrog.tools.ant;

/**
 * Test the parser
 * @author steve loughran
 *         Date: 25-Feb-2004
 *         Time: 23:48:51
 */
public class ParseTest extends TaskTestBase {
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
        executeTarget("testNoop");
    }

    public void testValid1() {
        executeTarget("testValid1");
    }

    public void testValid2() {
        executeTarget("testValid2");
    }

    public void testInvalid() {
        expectBuildException("testInvalid", "parse failure");
    }
    public void testMissingFile() {
        expectBuildException("testMissingFile", "not found");
    }
    public void testTwoFiles() {
        expectBuildException("testTwoFiles", "parse failure");
    }

    public void testVerbose() {
        executeTarget("testVerbose");
    }

    public void testQuiet() {
        executeTarget("testQuiet");
    }

    public void testVerboseQuiet() {
        executeTarget("testVerboseQuiet");
    }



}
