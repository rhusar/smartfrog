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
package org.smartfrog.test.system.sfcore.languages.cdl.execute;

import org.smartfrog.sfcore.prim.Prim;

/**
 * created 24-Jun-2005 15:02:17
 */

public class Lazy1Test extends CdlDeployingTestBase {

    public Lazy1Test(String name) {
        super(name);
    }

    public static final String LAZY1 = Lazy1Test.VALID + "lazy1.cdl";


    public void testLazyReference() throws Throwable {
        application = deployExpectingSuccess(Lazy1Test.LAZY1, "lazy1");
        Prim app = (Prim) resolveAttribute(application, "app");
        String message = resolveStringAttribute(app, "user");
        assertTrue("Empty message attribute", message.length() > 0);
        assertEquals("lazy-value", message);
    }


}
