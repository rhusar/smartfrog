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


package org.smartfrog.test.system.dump;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.Dumper;
import org.smartfrog.sfcore.common.DumperCDImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * JUnit test class for test cases related to Subprocess Example
 */
public class SubProcessExampleDumpTest
        extends DeployingTestBase {

    // In this particular case we use the examples without screens
    //private static final String FILES = "org/smartfrog/examples/subprocesses/";
    //#include "org/smartfrog/test/system/deploy/subprocessTestHarness.sf"
    private static final String FILES = "org/smartfrog/test/system/deploy/";
    private static final Log log = LogFactory.getLog(SubProcessExampleDumpTest.class);

    /**
     * Constructor
     * @param s name
     */
    public SubProcessExampleDumpTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseSubProcessExDump01() throws Throwable {

        application = deployExpectingSuccess(FILES + "subprocessTestHarness.sf", "tcSPEDump01");
        assertNotNull(application);

        String actualSfClass = (String) application.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.sfcore.compound.CompoundImpl", actualSfClass);

        //Some basic check
        Prim sys = (Prim) application.sfResolveHere("system");
        assertEquals("first", sys.sfDeployedProcessName());

        Prim foo = (Prim) sys.sfResolveHere("foo");
        assertEquals("test", foo.sfDeployedProcessName());

        Prim bar = (Prim) foo.sfResolveHere("bar");
        assertEquals("test2", bar.sfDeployedProcessName());

        ComponentDescription cd = application.sfDiagnosticsReport();
        assertNotNull("No Diagnostics report", cd);
        log.info("Diagnostics report: \n" + cd);
        //Testing Dump now
        log.info(dumpState(application));


    }


    /**
     * Cast the parameter toa prim and dump it
     * @param node node to dump
     * @return the dup
     */
    public String dumpState(Object node) {
        StringBuffer message = new StringBuffer();
        String name = "error";
        //Only works for Prims.
        if (node instanceof Prim) {
            try {
                Prim objPrim = ((Prim) node);
                message.append("\n*************** State *****************\n");
                Dumper dumper = new DumperCDImpl(objPrim);
                objPrim.sfDumpState(dumper.getDumpVisitor());
                message.append(dumper.toString());
                name = (objPrim).sfCompleteName().toString();
            } catch (Exception ex) {
                log.error(ex);
                StringWriter sw = new StringWriter();
                PrintWriter pr = new PrintWriter(sw, true);
                ex.printStackTrace(pr);
                pr.close();
                message.append("\n Error: " + ex.toString() + "\n" + sw.toString());
                fail(message.toString());
                return null;
            }
        }
        return ("State for " + name + "\n" + message.toString());

    }

}
