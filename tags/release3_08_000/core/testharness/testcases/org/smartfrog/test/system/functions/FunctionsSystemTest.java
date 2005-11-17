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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;


/**
 * JUnit test class for functions provided by SmartFrog framework.
 */
public class FunctionsSystemTest extends SmartFrogTestBase {

    private static final String FILES="org/smartfrog/test/system/functions/";

    public FunctionsSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN29() throws Exception {
        deployExpectingException(FILES+"tcn29.sf",
                "tcn29",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "Unresolved Reference, data: [True in: HERE sfConfig:throw1, True in: HERE sfConfig:throw2], source: HERE sfConfig, resolutionPhase: link]");
    }

    public void testCaseTCN32() throws Exception {
        deployExpectingException(FILES + "tcn32.sf",
                "tcn32",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "Unresolved Reference, data: [nonExistentConcat in: HERE sfConfig:message], resolutionPhase: type]");
    }

    public void testCaseTCN33() throws Throwable {
        String expected = "Unable to connect to host remoteHost.india.hp.com"+
                "at port 50002 This would not work";
        Prim appl = deployExpectingSuccess(FILES + "tcn33.sf", "tcn33");
        assertNotNull(appl);
        String actual = (String) (appl.sfResolve("message"));
        assertFalse(expected.equals(actual));
    }
    /**
     * Tests "cancat" function.
     */
    public void testCaseTCP9() throws Throwable {
        Prim appl = deployExpectingSuccess(FILES + "tcp9.sf", "tcp9");
        assertNotNull(appl);
        String message = (String) (appl.sfResolve("message"));
        assertContains(message, "Hello World");
    }
    /**
     * Tests "vector" function.
     */
    public void testCaseTCP10() throws Throwable {
        Vector expected = new Vector();
        expected.add("Macgrath");
        expected.add("Hayden");
        expected.add("Ponting");
        Prim appl = deployExpectingSuccess(FILES + "tcp10.sf", "tcp10");
        assertNotNull(appl);
        Vector actual = (Vector) (appl.sfResolve("administrators"));
        assertNotNull("Did not find the value", actual);
        assertEquals(expected, actual);
    }
    /**
     * Tests "append" function.
     */
    public void testCaseTCP11() throws Throwable {
        Vector expected = new Vector();
        expected.add("This is a test for the append function");
        expected.add("provided by");
        expected.add("smartfrog framework");
        Prim appl = deployExpectingSuccess(FILES + "tcp11.sf", "tcp11");
        assertNotNull(appl);
        Vector actual = (Vector) (appl.sfResolve("logMessage"));
        assertEquals(expected, actual);
    }
    /**
     * Tests "formatString" function.
     */
    public void testCaseTCP12() throws Throwable {
        String expected = "Unable to connect to host remoteHost.india.hp.com "+
                "at port 50002";
        Prim appl = deployExpectingSuccess(FILES + "tcp12.sf", "tcp12");
        assertNotNull(appl);
        String actual = (String) (appl.sfResolve("message"));
        assertContains(expected, actual);
    }
    /**
     * Tests "sum" function.
     */
    public void testCaseTCP13() throws Throwable {
        String expected = "value is 99\n";
        Prim appl = deployExpectingSuccess(FILES + "tcp13.sf", "tcp13");
        assertNotNull(appl);
        String actual = (String) (appl.sfResolve("base"));
        assertContains(expected, actual);
    }
    /**
     * Tests "product" function.
     */
    public void testCaseTCP14() throws Throwable {
        Integer expected = new Integer(1000);
        Prim appl = deployExpectingSuccess(FILES + "tcp14.sf", "tcp14");
        assertNotNull(appl);
        Integer actual = (Integer) (appl.sfResolve("test"));
        assertEquals(expected, actual);
    }
    /**
     * Tests "random" function.
     */
    public void testCaseTCP15() throws Throwable {
        int min = 1;
        int max = 6;
        Prim appl = deployExpectingSuccess(FILES + "tcp15.sf", "tcp15");
        assertNotNull(appl);
        Integer actual = (Integer) (appl.sfResolve("throw1"));
        assertNotNull(actual);
        assertTrue((min<=actual.intValue()) && (actual.intValue()<=max));
    }
    /**
     * Tests "random" function.
     */
    public void testCaseTCP16() throws Throwable {
        Prim appl = deployExpectingSuccess(FILES + "tcp16.sf", "tcp16");
        assertNotNull(appl);
        Integer seq1 = (Integer) (appl.sfResolve("sequence1"));
        Integer seq2 = (Integer) (appl.sfResolve("sequence2"));
        assertNotNull(seq1);
        assertNotNull(seq2);
        assertTrue( (seq2.intValue()- seq1.intValue()) == 1);
    }
    /**
     * Tests "date" function.
     */
    public void testCaseTCP17() throws Throwable {
        Calendar dt = Calendar.getInstance();
        String expected = dt.getTime().toString();
        Prim appl = deployExpectingSuccess(FILES + "tcp17.sf", "tcp17");
        assertNotNull(appl);
        String actual = (String)(appl.sfResolve("today"));
        assertContains(expected, actual.substring(1, 8));
    }
}
