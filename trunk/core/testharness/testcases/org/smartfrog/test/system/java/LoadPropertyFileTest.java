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
package org.smartfrog.test.system.java;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.os.java.LoadPropertyFile;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * created Oct 1, 2004 10:54:11 AM
 */

public class LoadPropertyFileTest extends SmartFrogTestBase {

    public static final String FILES = "org/smartfrog/test/system/java/";

    public LoadPropertyFileTest(String name) {
        super(name);
    }

    public void testLoadProperty() throws Throwable {
        application = deployExpectingSuccess(LoadPropertyFileTest.FILES +
                "testLoadProperty.sf", "testLoadProperty");
    }

    public void testLoadPropertyVector() throws Throwable {
        application = deployExpectingSuccess(LoadPropertyFileTest.FILES +
                "testLoadProperty.sf", "testLoadProperty");
        LoadPropertyFile instance = (LoadPropertyFile) application.sfResolve("test1", true);
        Vector list = null;
        list = (Vector) ((Prim) instance).sfResolve(LoadPropertyFile.ATTR_PROPERTIES, true);
        assertEquals(4, list.size());
        assertTupleEquals(list, "prop1", "prop1");
        assertTupleEquals(list, "prop2", "prop2");
        assertTupleEquals(list, "prop.three", "prop3");
        assertTupleEquals(list, "4", "prop4");
    }

    private void assertTupleEquals(List list,String name,String expected) {
        Iterator it=list.iterator();
        for (Object aList : list) {
            List tuple = (List) aList;
            if (name.equals(tuple.get(0))) {
                assertEquals(expected, tuple.get(1));
                return;
            }
        }
        fail("Not found :"+name);
    }
}
