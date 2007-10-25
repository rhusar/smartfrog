/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.components.ssh.scp;

import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases related to "SSH" component
 */
public class ScpNegativeTest
        extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/ssh/scp/";

    public ScpNegativeTest(String s) {
        super(s);
    }

    /*scp : password file missing*/
    public void testCaseTCN83() throws Exception {
        deployExpectingException(FILES + "tcn83.sf",
                "tcn83",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "attribute is still TBD");
    }

    /*scp :  host is missing*/
    public void testCaseTCN84() throws Exception {
        deployExpectingException(FILES + "tcn84.sf",
                "tcn84",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'host' is missing");
    }

    /*scp : userid missing*/
    public void testCaseTCN86() throws Exception {
        deployExpectingException(FILES + "tcn86.sf",
                "tcn86",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'username' is missing");
    }

    /*scp : local file missing*/
    public void testCaseTCN87() throws Exception {
        deployExpectingException(FILES + "tcn87.sf",
                "tcn87",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'localFiles' is missing");
    }

    /*scp : remote file missing*/
    public void testCaseTCN88() throws Exception {
        deployExpectingException(FILES + "tcn88.sf",
                "tcn88",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'remoteFiles' is missing");
    }

    /*scp : transferType  missing*/
    public void testCaseTCN89() throws Exception {
        deployExpectingException(FILES + "tcn89.sf",
                "tcn89",
                EXCEPTION_LIFECYCLE,
                "Unsupported action: \"post\"");
    }


    public void testCaseTCNNonexistentHost() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES , "tcn_nonexistent_host.sf");
    }

    public void testtcn_mismatched_file_listTest() throws Throwable {
        expectSuccessfulTestRun(FILES, "tcn_mismatched_file_list.sf");
    }
}
