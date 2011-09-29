/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.test.system.sfcore.languages.cdl.execute;

import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.prim.Prim;

/**

 */
public class CdlExecTest extends XmlTestBase {

    public static final String FILES = "files/sfcdl/";
    public static final String VALID = FILES + "valid/";
    public static final String INVALID = FILES + "invalid/";
    public static final String EXEC_CDL = VALID + "exec.cdl";
    public static final String JBOSS_CDL = VALID + "jboss.cdl";
    public static final String SOUND_CDL = VALID + "fun.cdl";

    public CdlExecTest(String name) {
        super(name);
    }


    public void NotestDeployExec() throws Throwable {
        application = deployExpectingSuccess(EXEC_CDL, "testExec");
    }

    public void NotestDeployJBoss() throws Throwable {
        application = deployExpectingSuccess(JBOSS_CDL, "testJboss");
    }

    public void testDeploySound() throws Throwable {
        application = deployExpectingSuccess(SOUND_CDL, "testSound");
        Thread.sleep(15000);
    }

}
