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


package org.smartfrog.test.system.functions;

import java.util.Vector;
import java.util.Calendar;

import org.smartfrog.test.SmartfrogTestBase;
import org.smartfrog.sfcore.prim.Prim;


/**
 * JUnit test class for functions provided by SmartFrog framework.
 */
public class FunctionsSystemTest extends SmartfrogTestBase {

    private static final String FILES="org/smartfrog/test/system/functions/";

    public FunctionsSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN29() throws Exception {
        deployExpectingException(FILES+"tcn29.sf",
                "tcn29",
                "SmartFrogCompileResolutionException",
                "Unresolved Reference");
    }

    public void testCaseTCN32() throws Exception {
        deployExpectingException(FILES + "tcn32.sf",
                "tcn32",
                "SmartFrogCompileResolutionException",
                "Unresolved Reference");
    }
    
    public void testCaseTCN33() throws Exception {
        String expected = "Unable to connect to host remoteHost.india.hp.com"+
           "at port 50002 This would not work";
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcn33.sf", "tcn33");
            assertNotNull(appl);
            String actual = (String) (appl.sfResolve("message"));
            assertFalse(expected.equals(actual));
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "cancat" function.
     */
    public void testCaseTCP9() throws Exception {
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp9.sf", "tcp9");
            assertNotNull(appl);
            String message = (String) (appl.sfResolve("message"));
            assertContains(message, "Hello World");
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "vector" function.
     */
    public void testCaseTCP10() throws Exception {
        Vector expected = new Vector();
        expected.add("Macgrath");
        expected.add("Hayden");
        expected.add("Ponting");
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp10.sf", "tcp10");
            assertNotNull(appl);
            Vector actual = (Vector) (appl.sfResolve("administrators"));
            assertNotNull("Did not find the value", actual);
            assertEquals(expected, actual);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "append" function.
     */
    public void testCaseTCP11() throws Exception {
        Vector expected = new Vector();
        expected.add("This is a test for the append function");
        expected.add("provided by");
        expected.add("smartfrog framework");
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp11.sf", "tcp11");
            assertNotNull(appl);
            Vector actual = (Vector) (appl.sfResolve("logMessage"));
            assertEquals(expected, actual);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "formatString" function.
     */
    public void testCaseTCP12() throws Exception {
        String expected = "Unable to connect to host remoteHost.india.hp.com "+
                                                    "at port 50002";
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp12.sf", "tcp12");
            assertNotNull(appl);
            String actual = (String) (appl.sfResolve("message"));
            assertContains(expected, actual);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "sum" function.
     */
    public void testCaseTCP13() throws Exception {
        String expected = "value is 99\n";
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp13.sf", "tcp13");
            assertNotNull(appl);
            String actual = (String) (appl.sfResolve("base"));
            assertContains(expected, actual);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "product" function.
     */
    public void testCaseTCP14() throws Exception {
        Integer expected = new Integer(1000);
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp14.sf", "tcp14");
            assertNotNull(appl);
            Integer actual = (Integer) (appl.sfResolve("test"));
            assertEquals(expected, actual);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "random" function.
     */
    public void testCaseTCP15() throws Exception {
        int min = 1;
        int max = 6;
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp15.sf", "tcp15");
            assertNotNull(appl);
            Integer actual = (Integer) (appl.sfResolve("throw1"));
            assertNotNull(actual);
            assertTrue((min<=actual.intValue()) && (actual.intValue()<=max));
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "random" function.
     */
    public void testCaseTCP16() throws Exception {
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp16.sf", "tcp16");
            assertNotNull(appl);
            Integer seq1 = (Integer) (appl.sfResolve("sequence1"));
            Integer seq2 = (Integer) (appl.sfResolve("sequence2"));
            assertNotNull(seq1);
            assertNotNull(seq2);
            assertTrue( (seq2.intValue()- seq1.intValue()) == 1);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
    /**
     * Tests "date" function.
     */
    public void testCaseTCP17() throws Exception {
        Calendar dt = Calendar.getInstance();
        String expected = dt.getTime().toString();
        try {
            Prim appl = deployExpectingSuccess(FILES + "tcp17.sf", "tcp17");
            assertNotNull(appl);
            String actual = (String)(appl.sfResolve("today"));
            assertContains(expected, actual);
        }catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
