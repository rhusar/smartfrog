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


package org.smartfrog.test.system.components.ssh;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;

/**
 * JUnit test class for test cases related to "SSH" component
 */
public class SSHTest
    extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/ssh/";

    public SSHTest(String s) {
        super(s);
    }
/*scp : password file missing*/
    public void testCaseTCN83() throws Exception {
        deployExpectingException(FILES+"tcn83.sf",
                                 "tcn83",
                                 "SmartFrogLifecycleException",
                                 "sfDeploy",
                                 "SmartFrogException",
                                 "java.io.FileNotFoundException");
    }
/*scp :  host is missing*/
    public void testCaseTCN84() throws Exception {
        deployExpectingException(FILES+"tcn84.sf",
                                 "tcn84",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'host' is missing");
    }

	/*scp : userid missing*/
	public void testCaseTCN86() throws Exception {
        deployExpectingException(FILES+"tcn86.sf",
                                 "tcn86",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'username' is missing");
    }
	/*scp : local file missing*/
	public void testCaseTCN87() throws Exception {
        deployExpectingException(FILES+"tcn87.sf",
                                 "tcn87",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'localFiles' is missing");
    }
		/*scp : remote file missing*/
		public void testCaseTCN88() throws Exception {
        deployExpectingException(FILES+"tcn88.sf",
                                 "tcn88",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'remoteFiles' is missing");
    }
		/*scp : transferType  missing*/
		public void testCaseTCN89() throws Exception {
        deployExpectingException(FILES+"tcn89.sf",
                                 "tcn89",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'transferType' is missing");
    }

/*scp : success manual test case
	public void testCaseTCP_scp() throws Throwable {

		Prim app = deployExpectingSuccess(FILES+"tcp_scp.sf", "tcp_scp");
    }*/

	// SSHExec
/*sshexec : improper host*/
    public void testCaseTCN91() throws Exception {
        deployExpectingException(FILES+"tcn91.sf",
                                 "tcn91",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'host' is missing");
    }
	
	/*sshexec : userid missing*/
	public void testCaseTCN92() throws Exception {
        deployExpectingException(FILES+"tcn92.sf",
                                 "tcn92",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'username' is missing");
    }
	/*sshexec : command missing*/
	public void testCaseTCN93() throws Exception {
        deployExpectingException(FILES+"tcn93.sf",
                                 "tcn93",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: non-optional attribute 'commands' is missing");
    }
	/*sshexec : password file missing*/
    public void testCaseTCN94() throws Exception {
        deployExpectingException(FILES+"tcn94.sf",
                                 "tcn94",
                                 "SmartFrogLifecycleException",
                                 "sfDeploy",
                                  "SmartFrogException",
                                 "java.io.FileNotFoundException");
    }
	

}

