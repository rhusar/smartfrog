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


package org.smartfrog.test.system.constraints;

import java.util.Vector;

import org.smartfrog.SFParse;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.test.DeployingTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConstraintsTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/constraints/";
    private boolean succ=false;
    private boolean first=true;
    private static Log log= LogFactory.getLog(ConstraintsTest.class);

    public ConstraintsTest(String name) {
        super(name);
    }

    private boolean failedSolver(){
    	//This is a temporary guard on running this test or degeneratively skipping it until we have the
    	//issue of including Eclipse in the release sorted out.  For now, only ADHF runs the constraints tests
    	//and whether they run or not is simply determined by the presence of the ECLIPSEDIRECTORY env variable.
    	//This whole method will go eventually.
    	if (first){
    		first=false;
    		succ=(System.getenv("ECLIPSEDIRECTORY")!=null);
    	}

    	if (!succ) {
    		log.warn("No Constraint Solver Present.  Aborting test with (degenerative) success");
        	return true;
    	} else return false;
    }

    /**
     * test case CTN1
     * @throws Throwable on failure
     */
    public void testCaseCTN1() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn1.sf");
    	assertEquals(cxt.size(), 2);

    	Object foo1 = cxt.get("foo1"); assertNotNull(foo1);
    		assertTrue(foo1 instanceof ComponentDescription);

    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2);
    		assertTrue(foo2 instanceof ComponentDescription);

    	Context f1c = ((ComponentDescription) foo1).sfContext();
    	Context f2c = ((ComponentDescription) foo2).sfContext();
        assertContextResolves(f1c, "x", 1);
        assertContextResolves(f1c, "y", 2);
        assertContextResolves(f1c, "z", 2);
        assertContextResolves(f2c, "x", 1);
    }

    private void assertContextResolves(Context ctx, String key, int value) {
        Object result = ctx.get("key");
        assertNotNull("No value for key "+key,result);
        assertTrue("Not an integer: "+result, result instanceof Integer);
        assertEquals(value,((Integer) result).intValue());
    }

    private void assertContextResolves(Context ctx, String key, boolean value) {
        Object result = ctx.get("key");
        assertNotNull("No value for key " + key, result);
        assertTrue("Not a Boolean: " + result, result instanceof Boolean);
        assertEquals(value,((Boolean) result).booleanValue());
    }

    private void assertContextResolves(Context ctx, String key, String value) {
        Object result = ctx.get("key");
        assertNotNull("No value for key " + key, result);
        assertTrue("Not a String: " + result, result instanceof String);
        assertEquals(value,(String) result);
    }

    /**
     * test case CTN2
     * @throws Throwable on failure
     */
    public void testCaseCTN2() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn2.sf");
        Context elc = resolveCD(cxt, "elements");

        assertContextResolves(elc, "x", "one");
        assertContextResolves(elc, "y", "two");
        assertContextResolves(elc, "x", "three");
    }

    /**
     * test case CTN3
     * @throws Throwable on failure
     */
    public void testCaseCTN3() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn3.sf");

        Context fooc = resolveCD(cxt, "foo");
        Vector foov = resolveVector(fooc, "theList");
        assertEquals(((Integer) foov.get(0)).intValue(), 1);
        assertEquals(((Integer) foov.get(1)).intValue(), 2);
        assertEquals(((Integer) foov.get(2)).intValue(), 3);

        Context foo2c = resolveCD(cxt, "foo2");
        Vector foo2v = resolveVector(foo2c, "theList");
        assertEquals(((Integer) foo2v.get(0)).intValue(), 1);
        assertEquals(((Integer) foo2v.get(1)).intValue(), 2);
        assertEquals(((Integer) foo2v.get(2)).intValue(), 3);

        Context foo3c = resolveCD(cxt, "foo3");
        Vector foo3v = resolveVector(foo3c, "theList");
        assertEquals(((Integer) foo3v.get(0)).intValue(), 1);
        assertEquals(((Integer) foo3v.get(1)).intValue(), 2);
        assertEquals(((Integer) foo3v.get(2)).intValue(), 3);


    }

    /**
     * test case CTN4
     * @throws Throwable on failure
     */
    public void testCaseCTN4() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn4.sf");

        Context foo1c = resolveCD(cxt, "foo1");

        Context fooc = resolveCD(foo1c, "foo");

        assertContextResolves(fooc, "bar", "32");
        assertContextResolves(fooc, "bar2", "48");
        assertContextResolves(fooc, "bar3", "51");

    }

    private Context resolveCD(Context foo1c, String key) {
        Object foo = foo1c.get(key);
        assertNotNull(foo);
        assertTrue(foo instanceof ComponentDescription);
        Context fooc = ((ComponentDescription)foo).sfContext();
        return fooc;
    }


    /**
     * test case CTN5
     * @throws Throwable on failure
     */
    public void testCaseCTN5() throws Throwable {
    	if (failedSolver()) return;

        Context cxt = parseToContext("ctn5.sf");
        Context foo1c = resolveCD(cxt, "foo1");
        Context fooc = resolveCD(foo1c, "foo");
    	assertContextResolves(fooc,"bar4","73");
    }



    /**
     * test case CTN6
     * @throws Throwable on failure
     */
    public void testCaseCTN6() throws Throwable {
    	if (failedSolver()) return;

        Context cxt = parseToContext("ctn6.sf");

        Context foo1c = resolveCD(cxt, "foo1");

        Context fooc = resolveCD(foo1c, "foo");

        assertContextResolves(fooc, "bar", "32");
        assertContextResolves(fooc, "bar2", "48");
        assertContextResolves(fooc, "bar3", "51");
    }

    private Context parseToContext(String filename) {
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+ filename);
        assertNotNull(cd);
        //It parses...

        Context cxt = cd.sfContext();
        return cxt;
    }


    /**
     * test case CTN7
     * @throws Throwable on failure
     */
    public void testCaseCTN7() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn7.sf");

        Context foo1c = resolveCD(cxt, "foo1");
        assertContextResolves(foo1c, "y", "one");
    }


    /**
     * test case CTN9
     * @throws Throwable on failure
     */
    public void testCaseCTN9() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn9.sf");
        Context bazc = resolveCD(cxt, "baz");

        Context foofc = resolveCD(bazc, "foofred");

        assertContextResolves(foofc, "bar", "hello world");
        assertContextResolves(foofc, "sfArrayIndex", "fred");
        assertContextResolves(foofc, "sfArrayTag", "foofred");
        Context foocc = resolveCD(bazc, "fooclive");

        assertContextResolves(foocc, "bar", "hello world");
        assertContextResolves(foocc, "sfArrayIndex", "clive");
        assertContextResolves(foocc, "sfArrayTag", "fooclive");

        Context foojc = resolveCD(bazc, "foojoe");
        assertContextResolves(foojc, "bar", "hello world");
        assertContextResolves(foojc, "sfArrayIndex", "joe");
        assertContextResolves(foojc, "sfArrayTag", "foojoe");
    }




    /**
     * test case CTN10
     * @throws Throwable on failure
     */
    public void testCaseCTN10() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn10.sf");
        Context foo2c = resolveCD(cxt, "foo2");
        assertContextResolves(foo2c, "foo3", "011");
    }

    /**
     * test case CTN11
     * @throws Throwable on failure
     */
    public void testCaseCTN11() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn11.sf");
        Context foo2c = resolveCD(cxt, "foo2");
        assertContextResolves(foo2c, "foo3",false);
    }

    /**
     * test case CTN12
     * @throws Throwable on failure
     */
    public void testCaseCTN12() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn12.sf");
        Context foo2c = resolveCD(cxt, "foo2");
        assertContextResolves(foo2c, "foo3", 512);
    }

    /**
     * test case CTN13
     * @throws Throwable on failure
     */
    public void testCaseCTN13() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn13.sf");
        Context fooc = resolveCD(cxt, "foo");
        assertContextResolves(fooc, "bar", 3);
    }

    /**
     * test case CTN14
     * @throws Throwable on failure
     */
    public void testCaseCTN14() throws Throwable {
    	if (failedSolver()) return;
        Context cxt = parseToContext("ctn14.sf");
        Context foo2c = resolveCD(cxt, "foo2");
        assertContextResolves(foo2c, "foo3", "011");
    }


    /**
     * test case CTN15
     * @throws Throwable on failure
     */
    public void testCaseCTN15() throws Throwable {
    	if (failedSolver()) return;

        Context cxt = parseToContext("ctn15.sf");
        Vector allocv = resolveVector(cxt, "allocation");
        assertElementEquals(allocv, 0, "host0");
        assertElementEquals(allocv, 1, "host0");
        assertElementEquals(allocv, 2, "host1");
        assertElementEquals(allocv, 3, "host2");
    }

    private void assertElementEquals(Vector allocv, int index, String value) {
        assertTrue("Vector has no element " + index, allocv.size() >= index);
        assertEquals("Vector element "+index,
                value, (String) allocv.get(index));
    }


    /**
     * test case CTN16
     * @throws Throwable on failure
     */
    public void testCaseCTN16() throws Throwable {
    	if (failedSolver()) return;

        Context cxt = parseToContext("ctn16.sf");

        Context fooc = resolveCD(cxt, "foo");

        Vector allocv = resolveVector(fooc, "allocation");

        assertElementEquals(allocv, 0, "host0");
        assertElementEquals(allocv, 1, "host0");
        assertElementEquals(allocv, 2, "host1");
        assertElementEquals(allocv, 3, "host2");
    }


    private Vector resolveVector(Context fooc, String key) {
        Object alloc = fooc.get(key);
        assertNotNull(alloc);
        assertTrue(alloc instanceof Vector);
        Vector allocv = (Vector)alloc;
        return allocv;
    }


    /**
     * test case CTN17
     * @throws Throwable on failure
     */
    public void testCaseCTN17() throws Throwable {
    	if (failedSolver()) return;

        Context cxt = parseToContext("ctn17.sf");
        Context fooc = resolveCD(cxt, "foo");

    	assertContextResolves(fooc, "test","the the");
    }


    /**
     * test case CTN19
     * @throws Throwable on failure
     */
    public void testCaseCTN19() throws Throwable {
    	if (failedSolver()) return;

        Context cxt = parseToContext("ctn19.sf");
        Context diffc = resolveCD(cxt, "diff");

        Vector elvalsv = resolveVector(diffc, "element_vals");
        assertElementEquals(elvalsv, 0, "one");
        assertElementEquals(elvalsv, 1, "two");
        assertElementEquals(elvalsv, 2, "three");
    }


}
