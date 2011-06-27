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

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.RemoteToString;
import org.smartfrog.test.DeployingTestBase;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created 19-Mar-2008 13:31:17.
 *
 * These tests used to count attributes as well as children, but that proved too brittle. Only the number of child nodes are counted
 */

public class RemoteCompoundTest extends DeployingTestBase {

    private static final int CHILDCOUNT = 3;
    private static final int ATTRCOUNT = 20;

    public RemoteCompoundTest(String name) {
        super(name);
    }

    /**
     * Deploy the application and cast to a compound
     *
     * @return the deployed application as a compound
     *
     * @throws Throwable on failure
     */
    private Compound deployCompound() throws Throwable {
        application = deployExpectingSuccess("/org/smartfrog/test/system/compound/components.sf", "compounds");
        return (Compound) application;
    }


    /**
     * test case found
     *
     * @throws Throwable on failure
     */
    public void testAttributes() throws Throwable {
        Compound comp = deployCompound();
        Thread.sleep(5000);
        Iterator<Object> iterator = comp.sfAttributes();
        int counter = 0;
        int childcount = 0;
        StringBuilder attributes = new StringBuilder();
        StringBuilder children = new StringBuilder();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = comp.sfResolveHere(key, true);

            String summary = key.toString() + "=" + value;
            attributes.append(summary).append('\n');
            counter++;
            if (value instanceof Liveness) {
                childcount++;
                children.append(summary).append('\n');
            }
        }
        String failureText = "Expected " + ATTRCOUNT
                + " attributes but found " + counter + "\n"
                + " and " + CHILDCOUNT + " children but found " + childcount
                + " \n" + attributes;
        assertEquals(failureText, CHILDCOUNT, childcount);
    }

    /**
     * test case
     *
     * @throws Throwable on failure
     */
    public void testValues() throws Throwable {
        Compound comp = deployCompound();
        Iterator<Object> iterator = comp.sfValues();
        int counter = 0;
        int childcount = 0;
        StringBuilder attributes = new StringBuilder();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            String summary = value.toString();
            attributes.append(summary).append('\n');
            counter++;
            if (value instanceof Liveness) {
                childcount++;
            }
        }
        String failureText = "Expected " + ATTRCOUNT
                + " attributes but found " + counter + "\n"
                + " and " + CHILDCOUNT + " children but found " + childcount
                + " \n" + attributes;
        assertEquals(failureText, CHILDCOUNT, childcount);
    }

    /**
     * test case
     *
     * @throws Throwable on failure
     */
    public void testChildren() throws Throwable {
        Compound comp = deployCompound();
        Enumeration<Liveness> children = comp.sfChildren();
        int childcount = 0;
        StringBuilder attributes = new StringBuilder();
        while (children.hasMoreElements()) {
            childcount++;
            Liveness child = children.nextElement();
            //pretend we are the parent
            child.sfPing(comp);
            RemoteToString rts = (RemoteToString) child;
            attributes.append(rts.sfRemoteToString()).append('\n');
        }
        assertEquals("Wrong child count:\n" + attributes, CHILDCOUNT, childcount);
    }

}
