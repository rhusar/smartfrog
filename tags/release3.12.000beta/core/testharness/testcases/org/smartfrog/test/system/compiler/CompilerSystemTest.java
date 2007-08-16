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


package org.smartfrog.test.system.compiler;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * JUnit test class for compiler/parser functional tests.
 */
public class CompilerSystemTest extends SmartFrogTestBase {

    private static final String FILES="org/smartfrog/test/system/compiler/";

    public CompilerSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN5() throws Exception {
        deployExpectingException(FILES+"tcn5.sf",
                "tcn5",
                EXCEPTION_DEPLOYMENT,
                "error in schema: wrong class found for attribute 'sfClass (class that implements component)', expected: java.lang.String, found: org.smartfrog.sfcore.common.SFNull",
                EXCEPTION_RESOLUTION,
                null);
    }

    public void testCaseTCN47() throws Exception {
        deployExpectingException(FILES+"tcn47.sf",
                "tcn47",
                EXCEPTION_DEPLOYMENT,
	        "The sfConfig attribute of a SmartFrog description must be a Component Description",
                EXCEPTION_RESOLUTION,
                null);
    }

// Changes in the SF Language made this test obsolete.
//    public void testCaseTCN6() throws Exception {
//        deployExpectingException(FILES + "tcn6.sf",
//                "tcn6",
//                "SmartFrogCompileResolutionException",
//                "Unresolved Reference");
//    }

    public void testCaseTCN7() throws Exception {
        deployExpectingException(FILES + "tcn7.sf",
                "tcn7",
                EXCEPTION_DEPLOYMENT,
                "failed to deploy 'p' component",
                EXCEPTION_CLASSNOTFOUND,
                "org.smartfrog.test.system.compiler.PrinterImpl");
    }

// Changes in the SF Language made this test obsolete.
//    public void testCaseTCN8() throws Exception {
//        deployExpectingException(FILES + "tcn8.sf",
//            "tcn8",
//            "SmartFrogCompileResolutionException",
//            "Unresolved Reference");
//    }

    public void testCaseTCN9() throws Exception {
        deployExpectingException(FILES + "tcn9.sf",
                "tcn9",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_CLASSNOTFOUND,
                "Cannot find org.smartfrog.test.system.compiler.PrinterImpl");
    }

    public void testCaseTCN10() throws Exception {
        deployExpectingException(FILES + "tcn10.sf",
                "tcn10",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "Encountered \"HOST\" ");
    }

    public void testCaseTCN23() throws Exception {
        deployExpectingException(FILES + "tcn23.sf",
                "tcn23",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "SmartFrogTypeResolutionException:: , data: [CounterCompound in: HERE sfConfig:c2 cause: Reference not found");
    }

    public void testCaseTCN24() throws Exception {
        deployExpectingException(FILES + "tcn24.sf",
                "tcn24",
                EXCEPTION_DEPLOYMENT,
                "failed to deploy 'data' component",
                EXCEPTION_CLASSNOTFOUND,
                "Cannot find kk.class");
    }
    public void testCaseTCN25() throws Exception {
        deployExpectingException(FILES + "tcn25.sf",
                "tcn25",
                EXCEPTION_DEPLOYMENT,
                "failed to deploy 'data' component",
                EXCEPTION_RESOLUTION,
                "Reference not found");
    }

    public void testCaseTCN26() throws Exception {
        deployExpectingException(FILES + "tcn26.sf",
                "tcn26",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "Include file: org/smartfrog//sfcore/components.sf not found");
    }
    public void testCaseTCN27() throws Exception {
        deployExpectingException(FILES + "tcn27.sf",
                "tcn27",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "dataLazy in: HERE sfConfig:c3 attribute: attributeMissingATTRIB cause: Reference not found");
    }
    public void testCaseTCN28() throws Exception {
        deployExpectingException(FILES + "tcn28.sf",
                "tcn28",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "Unresolved Reference: HERE sfClass");
    }

     public void testCaseTCN95() throws Exception {
       deployExpectingException(FILES + "tcn95.sf",
                "tcn95",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "Parsing include file test.sf : Include file: test.sf not found"); 
    }

}
