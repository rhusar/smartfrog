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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.cddlm.cdl.demo.Echo;

/**
 * Test that the underlying CDL components work.
 */

/**
 *
  #include "/org/smartfrog/services/cddlm/cdl/components.sf";
 configuration extends CmpComponent {
 _sf_echo extends CmpComponent {
 a_sf_message "";
 _cmp_CommandPath extends CmpComponent {
 }
 }
 }
 sfConfig extends CmpComponent {
 __echo extends CmpComponent {
 a_sf_message "test";
 _cmp_CommandPath extends CmpComponent {
 value "org.smartfrog.services.cddlm.cdl.EchoImpl";
 }
 }
 }
 */ 
public class CdlComponentTest extends CdlDeployingTestBase {

    
    public static final String VALID_CDL_FILES = "files/sfcdl/valid/";
    public static final String SIMPLE = "simple.cdl";
    public static final String COMPOUND = "compound.cdl";
    public static final String MESSAGE = "message.cdl";
    public static final String MESSAGEBOX = "message-box.cdl";
    public static final String ECHO_SYSTEM = "echo-system.cdl";
    public static final String CDL_OPEN_MP3 = "open-mp3.cdl";

    public CdlComponentTest(String name) {
        super(name);
    }

    public void testSimple() throws Throwable {
        deployAndTerminate(SIMPLE);
    }

    public void testCompound() throws Throwable {
        deployAndTerminate(COMPOUND);
    }

    public void NotestOpenMP3() throws Throwable {
        deployAndTerminate(CDL_OPEN_MP3);
    }
    
    public void testMessage() throws Throwable {
        deployAndTerminate(MESSAGE);
    }

    public void testMessageBox() throws Throwable {
        deployAndTerminate(MESSAGEBOX);
    }

    public void testEchoSystem() throws Throwable {
        deployAndTerminate(ECHO_SYSTEM);
    }

    public void testMessageBoxWait() throws Throwable {
        Prim prim = deployExpectingSuccess(VALID_CDL_FILES + MESSAGEBOX, MESSAGEBOX);
        Prim echoPrim = prim.sfResolve("echo", (Prim) null, true);
        //cast it
        Echo echo = (Echo) echoPrim;
        try {
            boolean terminated = spinUntilTerminated(echoPrim, 5);
            assertTrue("successful termination", terminated);
        } finally {
            terminateApplication(prim);
        }
    }
    public void testEchoSystemWait() throws Throwable {
        Prim prim = deployExpectingSuccess(VALID_CDL_FILES + ECHO_SYSTEM, ECHO_SYSTEM+"2");
        Prim echoPrim =prim;
        boolean terminated = false;
        try {
            terminated = spinUntilTerminated(echoPrim, 30);
            assertTrue("successful termination",terminated);
        } finally {
            if(!terminated) {
                try {
                    terminateApplication(prim);
                } catch (java.rmi.NoSuchObjectException ignore) {
                    //already terminated, do nothing
                }
            }
        }
    }
}
