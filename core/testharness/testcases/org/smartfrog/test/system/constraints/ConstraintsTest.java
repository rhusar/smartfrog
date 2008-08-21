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
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.EclipseSolver;
import org.smartfrog.test.DeployingTestBase;

public class ConstraintsTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/constraints/";
    private boolean succ=false;
    private boolean first=true;
    
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
    		System.out.println("No Constraint Solver Present.  Aborting test with (degenerative) success");
        	return true;
    	} else return false;
    }
    
    /**
     * test case CTN1
     * @throws Throwable on failure
     */
    public void testCaseCTN1() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn1.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	assertEquals(cd.sfContext().size(), 2);
    	
    	Object foo1 = cxt.get("foo1"); assertNotNull(foo1);
    		assertTrue(foo1 instanceof ComponentDescription);
    	
    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2);
    		assertTrue(foo2 instanceof ComponentDescription);
    	
    	Context f1c = ((ComponentDescription) foo1).sfContext();
    	Context f2c = ((ComponentDescription) foo2).sfContext();
    	
    	Object f1x = f1c.get("x"); assertNotNull(f1x); assertTrue(f1x instanceof Integer);
    		assertEquals(((Integer)f1x).intValue(), 1);
    	Object f1y = f1c.get("y"); assertNotNull(f1y); assertTrue(f1y instanceof Integer);
    		assertEquals(((Integer)f1y).intValue(), 2);
    	Object f1z = f1c.get("z"); assertNotNull(f1z); assertTrue(f1z instanceof Integer);
    		assertEquals(((Integer)f1z).intValue(), 2);
    	Object f2x = f2c.get("x"); assertNotNull(f2x); assertTrue(f2x instanceof Integer);
    		assertEquals(((Integer)f2x).intValue(), 1);	
    }
    
    /**
     * test case CTN2
     * @throws Throwable on failure
     */
    public void testCaseCTN2() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn2.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	
    	Object elements = cxt.get("elements"); assertNotNull(elements);
    		assertTrue(elements instanceof ComponentDescription);
    	
    	Context elc = ((ComponentDescription) elements).sfContext();
    	
    	Object x = elc.get("x"); assertNotNull(x); assertTrue(x instanceof String);
    		assertEquals((String)x, "one");
    	Object y = elc.get("y"); assertNotNull(y); assertTrue(y instanceof String);
    		assertEquals((String)y, "two");
    	Object z = elc.get("z"); assertNotNull(z); assertTrue(z instanceof String);
    		assertEquals((String)z, "three");
    }

    /**
     * test case CTN3
     * @throws Throwable on failure
     */
    public void testCaseCTN3() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn3.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo = cxt.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	Object fool = fooc.get("theList"); assertNotNull(fool); assertTrue(fool instanceof Vector); Vector foov = (Vector)fool;
    	assertEquals(((Integer) foov.get(0)).intValue(), 1); assertEquals(((Integer) foov.get(1)).intValue(), 2); assertEquals(((Integer) foov.get(2)).intValue(), 3);

    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2); assertTrue(foo2 instanceof ComponentDescription);
    	Context foo2c = ((ComponentDescription)foo2).sfContext();
    	Object foo2l = foo2c.get("theList"); assertNotNull(foo2l); assertTrue(foo2l instanceof Vector); Vector foo2v = (Vector)foo2l;
    	assertEquals(((Integer) foo2v.get(0)).intValue(), 1); assertEquals(((Integer) foo2v.get(1)).intValue(), 2); assertEquals(((Integer) foo2v.get(2)).intValue(), 3);	
    	
    	Object foo3 = cxt.get("foo3"); assertNotNull(foo3); assertTrue(foo3 instanceof ComponentDescription);
    	Context foo3c = ((ComponentDescription)foo3).sfContext();
    	Object foo3l = foo3c.get("theList"); assertNotNull(foo3l); assertTrue(foo3l instanceof Vector); Vector foo3v = (Vector)foo3l;
    	assertEquals(((Integer) foo3v.get(0)).intValue(), 1); assertEquals(((Integer) foo3v.get(1)).intValue(), 2); assertEquals(((Integer) foo3v.get(2)).intValue(), 3);

    	
    }  
    
    /**
     * test case CTN4
     * @throws Throwable on failure
     */
    public void testCaseCTN4() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn4.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo1 = cxt.get("foo1"); assertNotNull(foo1); assertTrue(foo1 instanceof ComponentDescription);
    	Context foo1c = ((ComponentDescription)foo1).sfContext();
    	
    	Object foo = foo1c.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	
    	Object bar = fooc.get("bar"); assertNotNull(bar); assertTrue(bar instanceof String); assertEquals((String)bar, "32");
    	Object bar2 = fooc.get("bar2"); assertNotNull(bar2); assertTrue(bar2 instanceof String); assertEquals((String)bar2, "48");
    	Object bar3 = fooc.get("bar3"); assertNotNull(bar3); assertTrue(bar3 instanceof String); assertEquals((String)bar3, "51");
    	
    }    

    
    /**
     * test case CTN5
     * @throws Throwable on failure
     */
    public void testCaseCTN5() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn5.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo1 = cxt.get("foo1"); assertNotNull(foo1); assertTrue(foo1 instanceof ComponentDescription);
    	Context foo1c = ((ComponentDescription)foo1).sfContext();
    	
    	Object foo = foo1c.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	
    	Object bar = fooc.get("bar4"); assertNotNull(bar); assertTrue(bar instanceof String); assertEquals((String)bar, "73");
    	
    }    


    
    /**
     * test case CTN6
     * @throws Throwable on failure
     */
    public void testCaseCTN6() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn6.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo1 = cxt.get("foo1"); assertNotNull(foo1); assertTrue(foo1 instanceof ComponentDescription);
    	Context foo1c = ((ComponentDescription)foo1).sfContext();
    	
    	Object foo = foo1c.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	
    	Object bar = fooc.get("bar"); assertNotNull(bar); assertTrue(bar instanceof String); assertEquals((String)bar, "32");
    	Object bar2 = fooc.get("bar2"); assertNotNull(bar2); assertTrue(bar2 instanceof String); assertEquals((String)bar2, "48");
    	Object bar3 = fooc.get("bar3"); assertNotNull(bar3); assertTrue(bar3 instanceof String); assertEquals((String)bar3, "51");
    	
    }    

    
    /**
     * test case CTN7
     * @throws Throwable on failure
     */
    public void testCaseCTN7() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn7.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo1 = cxt.get("foo1"); assertNotNull(foo1); assertTrue(foo1 instanceof ComponentDescription);
    	Context foo1c = ((ComponentDescription)foo1).sfContext();
    	
    	Object y = foo1c.get("y"); assertNotNull(y); assertTrue(y instanceof String); assertEquals((String)y, "one");
    	
    }    

    
    /**
     * test case CTN9
     * @throws Throwable on failure
     */
    public void testCaseCTN9() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn9.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object baz = cxt.get("baz"); assertNotNull(baz); assertTrue(baz instanceof ComponentDescription);
    	Context bazc = ((ComponentDescription)baz).sfContext();
    	
    	Object foof = bazc.get("foofred"); assertNotNull(foof); assertTrue(foof instanceof ComponentDescription); 
    	Context foofc = ((ComponentDescription)foof).sfContext();
    	
    	Object barf = foofc.get("bar"); assertNotNull(barf); assertTrue(barf instanceof String); assertEquals((String)barf, "hello world");
    	Object aif = foofc.get("sfArrayIndex"); assertNotNull(aif); assertTrue(aif instanceof String); assertEquals((String)aif, "fred");
    	Object atf = foofc.get("sfArrayTag"); assertNotNull(atf); assertTrue(atf instanceof String); assertEquals((String)atf, "foofred");
    	
    	Object fooc = bazc.get("fooclive"); assertNotNull(fooc); assertTrue(fooc instanceof ComponentDescription); 
    	Context foocc = ((ComponentDescription)fooc).sfContext();
    	
    	Object barc = foocc.get("bar"); assertNotNull(barc); assertTrue(barc instanceof String); assertEquals((String)barc, "hello world");
    	Object aic = foocc.get("sfArrayIndex"); assertNotNull(aic); assertTrue(aic instanceof String); assertEquals((String)aic, "clive");
    	Object atc = foocc.get("sfArrayTag"); assertNotNull(atc); assertTrue(atc instanceof String); assertEquals((String)atc, "fooclive");
    	
    	Object fooj = bazc.get("foojoe"); assertNotNull(fooj); assertTrue(fooj instanceof ComponentDescription); 
    	Context foojc = ((ComponentDescription)fooj).sfContext();
    	
    	Object barj = foojc.get("bar"); assertNotNull(barj); assertTrue(barj instanceof String); assertEquals((String)barj, "hello world");
    	Object aij = foojc.get("sfArrayIndex"); assertNotNull(aij); assertTrue(aij instanceof String); assertEquals((String)aij, "joe");
    	Object atj = foojc.get("sfArrayTag"); assertNotNull(atj); assertTrue(atj instanceof String); assertEquals((String)atj, "foojoe");
    	
    }    


    
    /**
     * test case CTN10
     * @throws Throwable on failure
     */
    public void testCaseCTN10() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn10.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2); assertTrue(foo2 instanceof ComponentDescription);
    	Context foo2c = ((ComponentDescription)foo2).sfContext();
    	
    	Object foo3 = foo2c.get("foo3"); assertNotNull(foo3); assertTrue(foo3 instanceof String); assertEquals((String)foo3, "011");
    	
    }    

    /**
     * test case CTN11
     * @throws Throwable on failure
     */
    public void testCaseCTN11() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn11.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2); assertTrue(foo2 instanceof ComponentDescription);
    	Context foo2c = ((ComponentDescription)foo2).sfContext();
    	
    	Object foo3 = foo2c.get("foo3"); assertNotNull(foo3); assertTrue(foo3 instanceof Boolean); assertEquals(((Boolean)foo3).booleanValue(), false);
    	
    }    

    /**
     * test case CTN12
     * @throws Throwable on failure
     */
    public void testCaseCTN12() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn12.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2); assertTrue(foo2 instanceof ComponentDescription);
    	Context foo2c = ((ComponentDescription)foo2).sfContext();
    	
    	Object foo3 = foo2c.get("foo3"); assertNotNull(foo3); assertTrue(foo3 instanceof Integer); assertEquals(((Integer)foo3).intValue(), 512);
    	
    }    

    /**
     * test case CTN13
     * @throws Throwable on failure
     */
    public void testCaseCTN13() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn13.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo = cxt.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	
    	Object bar = fooc.get("bar"); assertNotNull(bar); assertTrue(bar instanceof Integer); assertEquals(((Integer)bar).intValue(), 3);
    	
    }

    /**
     * test case CTN14
     * @throws Throwable on failure
     */
    public void testCaseCTN14() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn14.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();
    	   	
    	Object foo2 = cxt.get("foo2"); assertNotNull(foo2); assertTrue(foo2 instanceof ComponentDescription);
    	Context foo2c = ((ComponentDescription)foo2).sfContext();
    	
    	Object foo3 = foo2c.get("foo3"); assertNotNull(foo3); assertTrue(foo3 instanceof String); assertEquals((String)foo3, "011");
    	
    }

    
    /**
     * test case CTN15
     * @throws Throwable on failure
     */
    public void testCaseCTN15() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn15.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();   	
    	Object alloc = cxt.get("allocation"); assertNotNull(alloc); assertTrue(alloc instanceof Vector); Vector allocv = (Vector)alloc;
    	
    	assertEquals((String) allocv.get(0), "host0"); assertEquals((String) allocv.get(1), "host0"); 
    	assertEquals((String) allocv.get(2), "host1"); assertEquals((String) allocv.get(3), "host2"); 
    }


    /**
     * test case CTN16
     * @throws Throwable on failure
     */
    public void testCaseCTN16() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn16.sf");
    	assertNotNull(cd);
    	//It parses...

    	Context cxt = cd.sfContext();
	   	
    	Object foo = cxt.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	   	
    	Object alloc = fooc.get("allocation"); assertNotNull(alloc); assertTrue(alloc instanceof Vector); Vector allocv = (Vector)alloc;
    	
    	assertEquals((String) allocv.get(0), "host0"); assertEquals((String) allocv.get(1), "host0"); 
    	assertEquals((String) allocv.get(2), "host1"); assertEquals((String) allocv.get(3), "host2"); 
    }


    /**
     * test case CTN17
     * @throws Throwable on failure
     */
    public void testCaseCTN17() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn17.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();  
    	Object foo = cxt.get("foo"); assertNotNull(foo); assertTrue(foo instanceof ComponentDescription);
    	Context fooc = ((ComponentDescription)foo).sfContext();
    	
    	Object test = fooc.get("test"); assertNotNull(test); assertTrue(test instanceof String); assertEquals((String)test, "the the");
    	
    }

    
    /**
     * test case CTN19
     * @throws Throwable on failure
     */
    public void testCaseCTN19() throws Throwable {
    	if (failedSolver()) return;
    	
        ComponentDescription cd = SFParse.parseFileToDescription(FILES+"ctn19.sf");
    	assertNotNull(cd);
    	//It parses...
    	
    	Context cxt = cd.sfContext();   	
    	Object diff = cxt.get("diff"); assertNotNull(diff); assertTrue(diff instanceof ComponentDescription); 
    	Context diffc = ((ComponentDescription)diff).sfContext();
    	
    	Object elvals = diffc.get("element_vals"); assertNotNull(elvals); assertTrue(elvals instanceof Vector); Vector elvalsv = (Vector)elvals;
    	assertEquals((String) elvalsv.get(0), "one"); assertEquals((String) elvalsv.get(1), "two"); assertEquals((String) elvalsv.get(2), "three"); 
    }


}
