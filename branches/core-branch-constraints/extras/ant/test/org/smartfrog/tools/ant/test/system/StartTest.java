/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.tools.ant.test.system;

import org.smartfrog.tools.ant.DeployingTaskBase;
import org.smartfrog.tools.ant.PropertyFile;
import org.smartfrog.tools.ant.test.TaskTestBase;

/**
 * @author steve loughran
 *         created 27-Feb-2004 16:37:55
 */

public class StartTest extends TaskTestBase {

    public StartTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "start.xml";
    }

   public void testIncompatiblePort() {
        expectBuildExceptionContaining("testIncompatiblePort", "Incompatible Value",
                "failed to start the smartfrog daemon");
    }

    /*
    public void testIncompatibleLivenessDelay() {
        expectBuildExceptionContaining("testIncompatibleLivenessDelay", "Incompatible Value",
                "failed to start the smartfrog daemon");
    }
     public void testIncompatibleLivenessFactor() {
        expectBuildExceptionContaining("testIncompatibleLivenessFactor", "Incompatible Value",
                "failed to start the smartfrog daemon");
    }
     public void testIncompatibleProcessAllow() {
        expectBuildExceptionContaining("testIncompatibleProcessAllow", "Incompatible Value",
                "failed to start the smartfrog daemon");
    }

    public void testIncompatibleProcessTimeOut() {
           expectBuildExceptionContaining("testIncompatibleProcessTimeOut", "Incompatible Value",
                   "failed to start the smartfrog daemon");
       }
    public void testIncompatibleLogLevel() {
              expectBuildExceptionContaining("testIncompatibleLogLevel", "Incompatible Value",
                      "failed to start the smartfrog daemon");
          }

      */
}
