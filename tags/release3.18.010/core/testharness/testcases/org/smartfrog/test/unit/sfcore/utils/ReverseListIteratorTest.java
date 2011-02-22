/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.unit.sfcore.utils;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

import org.smartfrog.sfcore.utils.ReverseListIterator;

/**
 *
 * Created 13-Sep-2007 14:05:36
 *
 */

public class ReverseListIteratorTest extends TestCase {
    private List<Integer> emptyList;
    private List<Integer> oneList;
    private List<Integer> longList;


    /**
     *
     * @param name classname
     */
    public ReverseListIteratorTest(String name) {
        super(name);
    }


    /** Sets up the fixture, for example, open a network connection. This method is called before a test is executed. */
    protected void setUp() throws Exception {
        super.setUp();
        longList = new ArrayList<Integer>();
        longList.add(1);
        longList.add(2);
        longList.add(3);
        longList.add(4);
        emptyList = new ArrayList<Integer>();
        oneList = new ArrayList<Integer>();
        oneList.add(0);
    }

    /**
     * iterate on a list
     * @param list list to iterate on
     * @return
     */
    ReverseListIterator<Integer> iterate(List<Integer> list) {
        return new ReverseListIterator<Integer>(list);
    }

    public void testCompleteWalk() throws Exception {
        int count=0;
        int number=0;
        for(int value:iterate(longList)) {
            count+=value;
            number++;
        }
        assertEquals(10,count);
        assertEquals(longList.size(), number);
    }

    public void testOrderedWalk() throws Exception {
        int previous = longList.size()+1;
        for (int value : iterate(longList)) {
            assertEquals(previous,value+1);
            previous=value;
        }
    }

    /**
     * Test that the iterator restarts from the top
     * @throws Exception
     */
    public void testFactoryWalk() throws Exception {
        int count = 0;
        ReverseListIterator<Integer> listIterator = iterate(longList);
        for (int value : listIterator) {
            count += value;
        }
        int previous = longList.size() + 1;
        for (int value : listIterator) {
            assertEquals(previous, value + 1);
            previous = value;
            count += value;
        }
        assertEquals(10, count);
    }

    public void testEmptyList() throws Exception {
        int count = 0;
        int number = 0;
        for (int value : iterate(emptyList)) {
            count += value;
            number++;
        }
        assertEquals(0, count);
        assertEquals(0, number);
    }

    public void testOneList() throws Exception {
        int count = 0;
        int number = 0;
        for (int value : iterate(oneList)) {
            count += value;
            number++;
        }
        assertEquals(1, count);
        assertEquals(1, number);
    }
}
