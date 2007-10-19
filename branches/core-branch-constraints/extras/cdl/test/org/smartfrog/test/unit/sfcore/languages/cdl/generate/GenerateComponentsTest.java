/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.unit.sfcore.languages.cdl.generate;

import org.smartfrog.services.cddlm.cdl.base.CdlCompoundImpl;
import org.smartfrog.services.cddlm.cdl.demo.EchoImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.test.system.sfcore.languages.cdl.execute.CdlComponentTest;
import org.smartfrog.test.system.sfcore.languages.cdl.execute.CdlExecTest;

/**
 * Use the CDL Files of the things that get deployed, and make some unit test
 * assertions about their generated descriptions. Written for defect tracking
 * created 07-Feb-2006 11:26:31
 */

public class GenerateComponentsTest extends SmartFrogTestBase {

    private static final String FILES= CdlComponentTest.VALID_CDL_FILES;

    private static final String MESSAGEBOX=FILES+CdlComponentTest.MESSAGEBOX;

    /**
     * Constructs a test case with the given name.
     */
    public GenerateComponentsTest(String name) {
        super(name);
    }

    public void testRootSfClassCorrect() throws Exception {
        Phases phases = parse(MESSAGEBOX);
        ComponentDescription rootCD = phases.sfAsComponentDescription();
        CdlComponentDescription rootCDL = (CdlComponentDescription) rootCD;
        String sfClass = rootCDL.sfResolve(SmartFrogCoreKeys.SF_CLASS, "", true);
        assertEquals(CdlCompoundImpl.class.getName(), sfClass);
    }

    public void testEchoSfClassCorrect() throws Exception {
        Phases phases=parse(MESSAGEBOX);
        ComponentDescription rootCD = phases.sfAsComponentDescription();
        CdlComponentDescription rootCDL = (CdlComponentDescription) rootCD;
        CdlComponentDescription echoCDL = (CdlComponentDescription) rootCD.sfResolve("echo",rootCD,true);
        String sfClass=echoCDL.sfResolve(SmartFrogCoreKeys.SF_CLASS,"",true);
        assertEquals(EchoImpl.class.getName(), sfClass);
    }

    public void testParseExec() throws Exception {
        Phases phases = parse(CdlExecTest.EXEC_CDL);
    }

    public void testParseJBoss() throws Exception {
        Phases phases = parse(CdlExecTest.JBOSS_CDL);
    }

    public void testParseSound() throws Exception {
        Phases phases = parse(CdlExecTest.SOUND_CDL);
    }

}
