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
package org.smartfrog.test.unit.java;

import junit.framework.TestCase;
import org.smartfrog.services.os.java.RunJavaUtils;

import java.util.Vector;

/**
 * created Oct 1, 2004 12:22:14 PM
 */

public class RunJavaUtilsTest extends TestCase {
    public void testCrackEmptyString() {
        assertCracked("", 0);
    }

    public void testCrackWhitespace() {
        assertCracked(" \t", 0);
    }

    public void testCrackNull() {
        assertCracked(null, 0);
    }

    public void testCrackWord() {
        Vector v=assertCracked("word", 1);
        assertEquals("word",(String)v.get(0));
    }

    public void testCrackWordPair() {
        Vector v = assertCracked(" abc def ", 2);
        assertEquals("abc", (String) v.get(0));
        assertEquals("def", (String) v.get(1));
    }

    public void testCrackUrl() {
        final String url1="http://www.google.com/search?q=piglet";
        final String url2="http://images.google.com/images?q=piglet";
        Vector v = assertCracked(" abc def "+url1+"\t"+url2, 4);
        assertEquals(url1, (String) v.get(2));
        assertEquals(url2, (String) v.get(3));
    }

    public Vector assertCracked(String source, int size) {
        Vector v = RunJavaUtils.crack(source);
        assertEquals(size, v.size());
        return v;
    }

    public void testnotMerged() {
        Vector v = assertCracked(" abc def ", 2);
        Vector v2 = RunJavaUtils.mergeDuplicates(v);
        assertEquals("abc", (String) v2.get(0));
        assertEquals("def", (String) v2.get(1));
    }

    public void testMerged() {
        Vector v = assertCracked(" abc def abc def ghi def", 6);
        Vector v2 = RunJavaUtils.mergeDuplicates(v);
        assertEquals(3,v2.size());
        assertEquals("abc", (String) v2.get(0));
        assertEquals("def", (String) v2.get(1));
        assertEquals("ghi", (String) v2.get(2));
    }

    public void testMakeString() {
        Vector v = assertCracked(" abc def ", 2);
        String s=RunJavaUtils.makeSpaceSeparatedString(v);
        assertEquals("abc def",s);
    }

    public void testMakeResourceSimple() {
        String classname = "simple";
        String resource=RunJavaUtils.makeResource(classname);
        assertEquals("/"+classname+".class",resource);
    }

    public void testMakeResourceComplex() {
        String classname = "complex.example.Class";
        String resource = RunJavaUtils.makeResource(classname);
        assertEquals("/complex/example/Class.class", resource);
    }

}
