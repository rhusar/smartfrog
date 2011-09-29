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
package org.smartfrog.test.unit.projects.alpine.wsa;

import nu.xom.Element;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import static org.smartfrog.test.unit.projects.alpine.TestHelper.assertInvalid;
import static org.smartfrog.test.unit.projects.alpine.TestHelper.assertValid;

/**
 * created 05-Apr-2006 11:33:51
 */

public class EprTest extends AddressingTestBase {

    /**
     * Constructs a test case with the given name.
     */
    public EprTest(String name) {
        super(name);
    }

    public void testEmptyInvalid() throws Exception {
        assertInvalid(emptyEPR);
    }

    public void testEmptyEqual() throws Exception {
        AlpineEPR epr2 = new AlpineEPR();
        assertEquals(emptyEPR, epr2);
    }

    public void testDifferentInequal() throws Exception {
        assertFalse(emptyEPR.equals(epr));
    }

    public void testEmptyCopy() throws Exception {
        AlpineEPR epr2 = new AlpineEPR(emptyEPR);
        assertEquals(epr2, emptyEPR);
        assertInvalid(epr2);
    }

    public void testSimpleEPR() throws Exception {
        assertValid(epr);
        AlpineEPR epr2 = new AlpineEPR(epr);
        assertEquals(epr2, epr);
    }

    public void testRoundTrip() throws Exception {
        Element xom = epr.toXom();
        AlpineEPR epr2 = new AlpineEPR(xom, null);
        assertEquals(epr, epr2);
    }

}
