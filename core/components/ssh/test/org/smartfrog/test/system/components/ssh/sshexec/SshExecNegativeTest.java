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
package org.smartfrog.test.system.components.ssh.sshexec;

import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases related to "SSH" component
 */
public class SshExecNegativeTest
        extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/ssh/sshexec/";

    public SshExecNegativeTest(String s) {
        super(s);
    }

/*sshexec : improper host*/
    public void testCaseTCN91_missing_host() throws Exception {
        deployExpectingException(FILES +"tcn91.sf",
                                 "tcn91",
                                 EXCEPTION_DEPLOYMENT,
                                 null,
                                 EXCEPTION_LINKRESOLUTION,
                                 "error in schema: non-optional attribute 'host' is missing");
    }

	/*sshexec : userid missing*/
	public void testCaseTCN92_user_name_is_missing() throws Exception {
        deployExpectingException(FILES +"tcn92.sf",
                                 "tcn92",
                                 EXCEPTION_DEPLOYMENT,
                                 null,
                                 EXCEPTION_LINKRESOLUTION,
                                 "error in schema: non-optional attribute 'username' is missing");
    }
	/*sshexec : command missing*/
	public void NotestCaseTCN93_commands_is_missing() throws Exception {
        deployExpectingException(FILES +"tcn93.sf",
                                 "tcn93",
                                 null,
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "commands");
    }
	/*sshexec : password file missing*/
    public void testCaseTCN94_missing_password_file() throws Exception {
        deployExpectingException(FILES +"tcn94.sf",
                                 "tcn94",
                                EXCEPTION_LIFECYCLE,
                                "sfDeploy",
                                EXCEPTION_SMARTFROG,
                                "java.io.FileNotFoundException");
    }


    public void test_vm_commands() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES,"test_vm_commands");
    }
}
