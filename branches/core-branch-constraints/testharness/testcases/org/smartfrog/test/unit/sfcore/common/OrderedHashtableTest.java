/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.unit.sfcore.common;

import junit.framework.TestCase;
import org.smartfrog.sfcore.common.OrderedHashtable;

import java.util.Enumeration;

/**
 * @author steve loughran
 * Date: 06-Feb-2004
 * Time: 11:24:46
 */
public class OrderedHashtableTest extends TestCase {

    public OrderedHashtableTest(String s) {
        super(s);
    }


    public void testConstruction() {
        OrderedHashtable table=null;
        try {
            table = new OrderedHashtable(0,0);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("Illegal Load")>=0);
            assertNull(table);
        }

    }

    private static Object testItems[] =
            {"1","3","2", new Integer(3), new Integer(1) };

    /**
     * test the ordering by saving some data then verifying the order
     * on the reload
     * @throws Exception
     */
    public void testOrdering() throws Exception {
        OrderedHashtable table = null;
        table = new OrderedHashtable(5,5);
        for(int i=0;i<testItems.length;i++) {
            table.put(testItems[i],testItems[i]);
        }

        Enumeration keys=table.keys();
        for (int i = 0; i < testItems.length; i++) {
            assertEquals(testItems[i],keys.nextElement());
        }
    }


    public void testIllegalCapacity() throws Exception {
        OrderedHashtable table = null;
        try {
            table = new OrderedHashtable(-4, -1);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("Illegal Capacity") >= 0);
        }

    }



}
