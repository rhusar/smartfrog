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
package org.smartfrog.services.deployapi.test.unit;

import junit.framework.TestCase;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

/**
 * created 28-Nov-2005 12:06:00
 */

public class XomHelperTest extends TestCase {


    public void testBoolTrue() throws Exception {
        assertTrue(XomHelper.getXsdBoolValue("true"));
        assertTrue(XomHelper.getXsdBoolValue("1"));
    }

    public void testBoolFalse() throws Exception {
        assertFalse(XomHelper.getXsdBoolValue("false"));
        assertFalse(XomHelper.getXsdBoolValue("0"));
    }

    public void testInvalid() throws Exception {
        assertInvalid("FALSE");
        assertInvalid(null);
        assertInvalid(" ");
        assertInvalid("2");
        assertInvalid("si");
    }

    private void assertInvalid(String string) {
        try {
            XomHelper.getXsdBoolValue(string);
            fail("expected failure from "+string);
        } catch (BaseException e) {
            //success
        }
    }

}
