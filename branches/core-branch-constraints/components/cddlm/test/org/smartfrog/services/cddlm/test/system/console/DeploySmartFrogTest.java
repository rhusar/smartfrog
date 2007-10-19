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


package org.smartfrog.services.cddlm.test.system.console;

import org.apache.axis.types.URI;
import org.cddlm.client.console.ConsoleOperation;
import org.cddlm.client.console.DeploySmartFrogFile;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;

import java.io.File;

/**
 * Date: 06-Sep-2004 Time: 21:57:39
 */
public class DeploySmartFrogTest extends DeployingTestBase {


    private File testDir;

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        String testDirName = System.getProperty("test.classes.dir");
        if (testDirName == null) {
            throw new Exception("Undefined: test.classes.dir");
        }
        testDir = new File(testDirName);
        if (!testDir.exists()) {
            throw new Exception("No dir " + testDir.getAbsolutePath());
        }
    }

    public void testDeployAndUndeploy() throws Exception {
        String name = "simple";
        URI uri = null;
        DeploymentDescriptorType dt = createSimpleDescriptor();
        uri = deploy(name, dt, null, null);
        //now test a lookup
        String stateName = DeployApiConstants.STATE_RUNNING;
        assertInState(uri, stateName);
        final boolean result = undeploy(uri);
        assertTrue("undeploy returned false",
                result);

    }

    public void testDeployTwice() throws Exception {
        DeploymentDescriptorType dt = createSimpleDescriptor();
        URI uri = null;
        uri = deploy(dt);
        URI uri2 = deploy(null, dt, null, null);
        //now test a lookup
        assertDeployed(uri);
        assertDeployed(uri2);
        boolean result = undeploy(uri);
        result = result & undeploy(uri2);
        assertTrue("undeloy returned false",
                result);

    }

    public void testReadFile() throws Exception {
        File testFile = new File(testDir,
                "files" + File.separator + "counter.sf");
        String source = ConsoleOperation.readIntoString(testFile);
        assertTrue("Read in the whole file", source.indexOf("counter") >= 0);
    }

    public void testDeployFile() throws Exception {
        String[] args;
        args = new String[1];
        File testFile = new File(testDir, "files" +
                File.separator +
                "counter.sf");
        args[0] = testFile.getAbsolutePath();
        operation = new DeploySmartFrogFile(getBinding(), getOut(), args);
        operation.execute();
        URI uri = operation.getUri();
        assertNotNull("operation.getUri()", uri);
        undeploy(uri);

    }
}
