/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www;

import junit.framework.TestCase;

/**
 * Created 19-May-2010 14:49:13
 */

@SuppressWarnings({"ProhibitedExceptionDeclared"})
public class ConcatPathsTest extends TestCase {

    public ConcatPathsTest(String name) {
        super(name);
    }

    public void testConcatTwoPaths() throws Exception {
        expectConcat("/", "/", "/");
    }

    public void testConcatOnePathOneEmptyString() throws Exception {
        expectConcat("/", "", "/");
    }

    public void testConcatOnePathOneNullString() throws Exception {
        expectConcat("/", null, "/");
    }

    public void testConcatEmptyStrings() throws Exception {
        expectConcat("", "", "/");
    }

    public void testConcatOneLongPathLeadingSlashOneNullString() throws Exception {
        expectConcat("/upload", null, "/upload");
    }

    public void testConcatOneLongPathOneNullString() throws Exception {
        expectConcat("upload", null, "upload");
    }

    public void testConcatOneNullOneLongPathString() throws Exception {
        expectConcat(null, "upload", "upload");
    }

    public void testConcatOneNullOneLongPathSlashString() throws Exception {
        expectConcat(null, "/upload", "/upload");
    }

    public void testConcatTestWarAndPage() throws Exception {
        expectConcat("/testwar", "error", "/testwar/error");
    }

    public void testConcatTestWarSlashAndPage() throws Exception {
        expectConcat("/testwar", "error", "/testwar/error");
    }

    public void testPathAndPage() throws Exception {
        expectConcat("", "/page", "/page");
    }

    public void testExamplePathAndPage() throws Exception {
        expectConcat("/example/", "/page", "/example/page");
    }

    public void testAddLeadingSlashOnNull() throws Throwable {
        expectAddLeadingSlash(null, "/");
    }

    public void testAddLeadingSlashOnSlash() throws Throwable {
        expectAddLeadingSlash("/", "/");
    }

    public void testAddLeadingSlashOnEmptyString() throws Throwable {
        expectAddLeadingSlash("", "/");
    }

    public void testAddLeadingSlashOnDoubleSlash() throws Throwable {
        expectAddLeadingSlash("//", "//");
    }

    public void testAddLeadingSlashOnPath() throws Throwable {
        expectAddLeadingSlash("path", "/path");
    }

    /**
     * Expect that the result of leading slash edition will be as predicted
     * @param in input string
     * @param expected expected result
     */
    private void expectAddLeadingSlash(String in, String expected) {
        String path = LivenessPageChecker.addLeadingSlash(in);
        assertEquals("Expected leading slash on "
                        + quote(in)
                        + " to be " + quote(expected)
                        + " but got " + quote(path),
                expected, path);
    }

    /**
     * Check that two paths concatenate together as expected
     * @param first first path
     * @param second second path
     * @param expected expected result
     */
    private void expectConcat(String first, String second, String expected) {
        String merged = LivenessPageChecker.concatPaths(first, second);
        assertEquals("Expected concat of "
                        + quote(first)
                        + " and " + quote(second)
                        + " to be " + quote(expected)
                        + " but got " + quote(merged),
                expected,
                merged);
    }

    protected String quote(String s) {
        return s == null ? null : ('"' + s + '"');
    }
}
