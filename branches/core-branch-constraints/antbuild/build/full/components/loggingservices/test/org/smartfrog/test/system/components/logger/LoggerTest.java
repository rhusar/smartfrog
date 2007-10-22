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


package org.smartfrog.test.system.components.logger;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * JUnit test class for test cases related to "logger" component
 */
public class LoggerTest
    extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/logger/";

    public LoggerTest(String s) {
        super(s);
    }

    public void testCaseTCP35() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp35.sf", "tcp35");
        String logsDir;
        String logsFile;
        try {
            assertNotNull(application);
            Prim logger = (Prim) application.sfResolve("logger");
            logsDir = logger.sfResolve("logsDir", (String) null, false);
            logsFile = logger.sfResolve("logFile", (String) null, false);
        } finally {
            terminateApplication();
        }
        File file = new File(logsDir + "\\" + logsFile);

        assertFalse(file.exists());
    }

    public void testCaseTCP36() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp36.sf", "tcp36");
        String logsDir;
        String logsFile;
        try {
            assertNotNull(application);
            Prim logger = (Prim) application.sfResolve("logger");
            logsDir = logger.sfResolve("logsDir", (String) null, false);
            logsFile = logger.sfResolve("logFile", (String) null, false);
        } finally {
            terminateApplication();
        }

        File file = new File(logsDir + "\\" + logsFile);

        assertTrue(file.exists());

        BufferedReader br = new BufferedReader(new FileReader(file));
        String ln = null;
        String[] expected
                = {"Foo:localhost:==>Info msg1", "Foo:localhost:==>Warning msg1", "Foo:localhost:==>Info msg1", "Foo:localhost:==>Log Error1"};

        int i = 0;
        do {
            ln = br.readLine();
            assertContains(ln, expected[i]);
            i++;
        } while (i != 4);
    }

    public void testCaseTCP37() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp37.sf", "tcp37");
        assertNotNull(application);
        String logsDir;
        String logsFile;
        try {
            Prim logger = (Prim) application.sfResolve("logger");
            logsDir = logger.sfResolve("logsDir", (String) null, false);
            logsFile = logger.sfResolve("logFile", (String) null, false);
        } finally {
            terminateApplication();
        }

        File file1 = new File(logsDir);
        File file2 = new File(logsDir + "\\" + logsFile);

        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertTrue(file1.isDirectory());
        assertFalse(file2.isDirectory());

        file2.delete();
        assertFalse(file2.exists());

    }

    public void testCaseTCP38() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp38.sf", "tcp38");
        String logsDir;
        String logsFile;
        try {
            assertNotNull(application);
            Prim logger = (Prim) application.sfResolve("logger");
            logsDir = logger.sfResolve("logsDir", (String) null, false);
            logsFile = logger.sfResolve("logFile", (String) null, false);
        } finally {
            terminateApplication();
        }

        File file = new File(logsDir + "\\" + logsFile);

        assertTrue(file.exists());

        BufferedReader br=null;
        try {
            br = new BufferedReader(new FileReader(file));
            String ln = null;
            ln = br.readLine();
            assertEquals(ln, null);
        } finally {
            FileSystem.close(br);;
        }
    }
}

