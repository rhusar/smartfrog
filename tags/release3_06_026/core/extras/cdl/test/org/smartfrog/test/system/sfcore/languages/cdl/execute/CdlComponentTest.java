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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;

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
public class CdlComponentTest extends SmartFrogTestBase {

    
    public static final String FILES = "files/sfcdl/valid/";
    
    public CdlComponentTest(String name) {
        super(name);
    }
    
    public void testSfComponentsWork() throws Throwable {
        Prim prim=deployExpectingSuccess(FILES+"echo.sf","echo");
        terminateApplication(prim);
    }
    
}
