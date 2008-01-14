/* (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.test.unit;

import junit.framework.TestCase;
import org.smartfrog.services.junit.junit3.JUnit3TestSuiteImpl;
import org.smartfrog.sfcore.common.SmartFrogInitException;

import java.util.ArrayList;
import java.util.List;

/**
 * created Nov 23, 2004 11:33:33 AM
 */

public class ListFlatteningTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testListFlattening() throws Exception {
        JUnit3TestSuiteImpl junit = new JUnit3TestSuiteImpl();
        List l1 = new ArrayList();
        List l2 = new ArrayList();
        List l3 = new ArrayList();
        l2.add("1");
        l2.add("2");
        l3.add("3");
        l3.add("4");
        l2.add(l3);
        List flat = new ArrayList();
        flat.add("1");
        flat.add("2");
        flat.add("3");
        flat.add("4");
        List<String> flat2 = junit.flattenStringList(l2, "l2");
        assertEquals(flat, flat);
        assertEquals(flat, flat2);
        l2.add(new ArrayList());
        l1.add(l2);
        List<String> flat3 = junit.flattenStringList(l1, "l1");
        assertEquals(flat, flat3);
        List l4 = new ArrayList();
        l4.add(l1);
        l4.add(new Integer("3"));
        try {
            List<String> flat4 = junit.flattenStringList(l4, "l5");
            fail("should have thrown something");
        } catch (SmartFrogInitException e) {
            //expected
        }
        List<String> flat5 = junit.flattenStringList(null, "flat5");
        assertEquals(0, flat5.size());
    }

}
