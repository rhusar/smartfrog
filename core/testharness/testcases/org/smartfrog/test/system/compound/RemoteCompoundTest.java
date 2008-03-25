/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.compound;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Liveness;

import java.util.Iterator;
import java.util.Enumeration;

/**
 *
 * Created 19-Mar-2008 13:31:17
 *
 */

public class RemoteCompoundTest extends DeployingTestBase {

    public RemoteCompoundTest(String name) {
        super(name);
    }


    /**
     * Sets up the fixture,by extracting the hostname and classes dir
     */
    protected void setUp() throws Exception {
        super.setUp();
    }


    private Compound deployCompound() throws Throwable {
        application = deployExpectingSuccess("/org/smartfrog/test/system/compound/components.sf", "compounds");
        return (Compound) application;
    }

    private Compound getCompound() {
        return (Compound) application;
    }

    public void testAttributes() throws Throwable {
        Compound comp=deployCompound();
        Iterator iterator = comp.sfAttributes();
        while (iterator.hasNext()) {
            Object o = iterator.next();
        }
    }

    public void testValues() throws Throwable {
        Compound comp = deployCompound();
        Iterator iterator = comp.sfValues();
        while (iterator.hasNext()) {
            Object o = iterator.next();
        }
    }

    public void testChildren() throws Throwable {
        Compound comp = deployCompound();
        Enumeration<Liveness> children = comp.sfChildren();
        while (children.hasMoreElements()) {
            Liveness child = children.nextElement();
            //pretend we are the parent
            child.sfPing(comp);
        }
    }

}
