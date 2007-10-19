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
package org.smartfrog.test.unit.sfcore.languages.cdl.generate;

import junit.framework.TestCase;
import org.smartfrog.sfcore.languages.cdl.generate.TypeMapper;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * created 02-Feb-2006 14:30:11
 */

public class TypeMapperTest extends TestCase {

    TypeMapper mapper;

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        mapper=new TypeMapper();
    }

    protected PropertyList mknod(String type,String value) {
        PropertyList node=new PropertyList("node");
        node.addNewAttribute(Constants.QNAME_SFI_TYPE,type);
        if(value!=null) {
            node.appendChild(value);
        }
        return node;
    }

    Object convert(String type, String value) throws SmartFrogException {
        PropertyList node = mknod(type,value);
        return mapper.map(node);
    }

    void assertConverts(String type, String value, Object expected) throws SmartFrogException {
        Object actual =convert(type,value);
        assertEquals(expected,actual);
    }

    void assertConvertFails(String type, String value) {
        try {
            Object actual = convert(type, value);
            fail("expected failure");
        } catch (SmartFrogException e) {
            //success!
        }

    }


    public void testInt() throws Exception {
        assertConverts("integer","7",7);
    }

    public void testMinusInt() throws Exception {
        assertConverts("integer", " -74", -74);
    }

    public void testIntTrim() throws Exception {
        assertConverts("integer", " 7\n", 7);
    }

    public void testBooleanT() throws Exception {
        assertConverts(TypeMapper.BOOLEAN, "true ", Boolean.TRUE );
    }

    public void testBooleanT1() throws Exception {
        assertConverts("boolean", " 1 ", Boolean.TRUE);
    }

    public void testBooleanF0() throws Exception {
        assertConverts("boolean", " 0 ", Boolean.FALSE);
    }

    public void testBooleanF() throws Exception {
        assertConverts("boolean", " false ", Boolean.FALSE);
    }

    public void testDouble() throws Exception {
        assertConverts("double", "7.3e2", (Double)7.3e2);
    }

    public void testFloat() throws Exception {
        assertConverts("float", "7.3e-3", (Float) 7.3e-3f);
    }

    public void testString() throws Exception {
        assertConverts("string", "7.3e-3", "7.3e-3");
    }

    public void testStringWhitespace() throws Exception {
        assertConverts("string", " \t\n7.3e-3 \r", " \t\n7.3e-3 \r");
    }

    public void testTrimWhitespace() throws Exception {
        assertConverts("trimmed", " \t\n7.3e-3 \r", "7.3e-3");
    }
    public void testLong() throws Exception {
        assertConverts("long", " 75", 75L);
    }

    public void testConstructor() throws Exception {
        final Object o = convert("java.lang.StringBuffer", "value");
        assertTrue(o instanceof StringBuffer);
        assertEquals("value",o.toString());
    }

}
