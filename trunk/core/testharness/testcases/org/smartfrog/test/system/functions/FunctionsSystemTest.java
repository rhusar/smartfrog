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
import java.util.GregorianCalendar;

import org.smartfrog.test.DeployingTestBase;


/**
 * JUnit test class for functions provided by SmartFrog framework.
 */
public class FunctionsSystemTest extends DeployingTestBase {

    private static final String FILES="org/smartfrog/test/system/functions/";

    public FunctionsSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN29() throws Exception {
        deployExpectingException(FILES+"tcn29.sf",
                "tcn29",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_LINKRESOLUTION,
	        "APPLY {sfFunctionClass \"org.smartfrog.sfcore.languages.sf.fu... in: HERE sfConfig attribute: throw1 cause: Unresolved Reference");
    }

    public void testCaseTCN32() throws Exception {
        deployExpectingException(FILES + "tcn32.sf",
                "tcn32",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_LINKRESOLUTION,
                "nonExistentConcat in: HERE sfConfig:message cause: Reference not found");
    }

    public void testCaseTCN33() throws Throwable {
        String expected = "Unable to connect to host remoteHost.india.hp.com"+
                "at port 50002 This would not work";
        application = deployExpectingSuccess(FILES + "tcn33.sf", "tcn33");
        assertNotNull(application);
        String actual = (String) (application.sfResolve("message"));
        assertFalse(expected.equals(actual));
    }
    /**
     * Tests "cancat" function.
     */
    public void testCaseTCP9() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp9.sf", "tcp9");
        assertNotNull(application);
        String message = (String) (application.sfResolve("message"));
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
        application = deployExpectingSuccess(FILES + "tcp10.sf", "tcp10");
        assertNotNull(application);
        Vector actual = (Vector) (application.sfResolve("administrators"));
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
        application = deployExpectingSuccess(FILES + "tcp11.sf", "tcp11");
        assertNotNull(application);
        Vector actual = (Vector) (application.sfResolve("logMessage"));
        assertEquals(expected, actual);
    }
    /**
     * Tests "formatString" function.
     */
    public void testCaseTCP12() throws Throwable {
        String expected = "Unable to connect to host remoteHost.india.hp.com "+
                "at port 50002";
        application = deployExpectingSuccess(FILES + "tcp12.sf", "tcp12");
        assertNotNull(application);
        String actual = (String) (application.sfResolve("message"));
        assertContains(actual, expected);
    }
    /**
     * Tests "sum" function.
     */
    public void testCaseTCP13() throws Throwable {
        String expected = "value is 99\n";
        application = deployExpectingSuccess(FILES + "tcp13.sf", "tcp13");
        assertNotNull(application);
        String actual = (String) (application.sfResolve("base"));
        assertContains(actual, expected);
    }
    /**
     * Tests "product" function.
     */
    public void testCaseTCP14() throws Throwable {
        Integer expected = new Integer(1000);
        application = deployExpectingSuccess(FILES + "tcp14.sf", "tcp14");
        assertNotNull(application);
        Integer actual = (Integer) (application.sfResolve("test"));
        assertEquals(expected, actual);
    }
    /**
     * Tests "random" function.
     */
    public void testCaseTCP15() throws Throwable {
        int min = 1;
        int max = 6;
        application = deployExpectingSuccess(FILES + "tcp15.sf", "tcp15");
        assertNotNull(application);
        Integer actual = (Integer) (application.sfResolve("throw1"));
        assertNotNull(actual);
        assertTrue((min<=actual.intValue()) && (actual.intValue()<=max));
    }
    /**
     * Tests "random" function.
     */
    public void testCaseTCP16() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp16.sf", "tcp16");
        assertNotNull(application);
        Integer seq1 = (Integer) (application.sfResolve("sequence1"));
        Integer seq2 = (Integer) (application.sfResolve("sequence2"));
        assertNotNull(seq1);
        assertNotNull(seq2);
        assertTrue( (seq2.intValue()- seq1.intValue()) == 1);
    }
    /**
     * Tests "date" function.
     */
    public void testCaseTCP17() throws Throwable {
        Calendar now = new GregorianCalendar();
        String year=Integer.toString(now.get(Calendar.YEAR));
        String day = Integer.toString(now.get(Calendar.DAY_OF_MONTH));
        application = deployExpectingSuccess(FILES + "tcp17.sf", "tcp17");
        assertNotNull(application);
        String result = (String)(application.sfResolve("today"));
        assertContains(result, year);
        assertContains(result, day);
    }

    /**
     * Test that lazy vectors still resolve, as do other elements in the list
     * @throws Throwable
     */
    public void testLazyVector() throws Throwable {
        application = deployExpectingSuccess(FILES + "lazyVector.sf", "lazyVector");
        assertLivenessSuccess(application,10);
    }
}
