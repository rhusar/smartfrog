/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.services.scripting.javax;

import junit.framework.TestCase;
import org.smartfrog.services.scripting.javax.ScriptHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;

import javax.script.ScriptException;

/**
 * Created 20-Feb-2009 13:44:51
 */

public class ScriptHelperTest extends TestCase {


    ScriptHelper helper;
    ScriptException base;
    private SmartFrogLifecycleException sfException;
    private RuntimeException rte;
    private static final int COLUMN_NUMBER = 4;
    private static final int LINE_NUMBER = 23;
    private static final String FILENAME = "filename";
    private RuntimeException noChild;


    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper=new ScriptHelper(null);
        base = new ScriptException("base", FILENAME, LINE_NUMBER, COLUMN_NUMBER);
        rte = new RuntimeException("inner");
        base.initCause(rte);
        sfException = new SmartFrogLifecycleException("bottom");
        rte.initCause(sfException);
        noChild = new RuntimeException("No child");
    }

    public void testExtractOne() throws Throwable {
        assertExtracted(sfException);
    }

    public void testExtractTwo() throws Throwable {
        assertExtracted(rte);
    }

    public void testExtractThree() throws Throwable {
        SmartFrogException sfe = assertExtracted(base);
        assertScriptLocationPropagated(sfe);
    }


    public void testNullExtract() throws Throwable {
        assertNull(extractNestedSFE(null));
    }

    public void testNoChild() throws Throwable {
        assertNull(extractNestedSFE(noChild));
    }


    public void testConvertSFE() throws Throwable {
        SmartFrogException sfe = helper.convert(sfException);
        assertIsLiveness(sfe);
    }

    public void testConvertInner() throws Throwable {
        SmartFrogException sfe = helper.convert(rte);
    }


    public void testConvert() throws Throwable {
        SmartFrogException sfe = helper.convert(base);
        assertIsLiveness(sfe);
        assertScriptLocationPropagated(sfe);
    }

    private void assertScriptLocationPropagated(SmartFrogException sfe) {
        assertEquals(FILENAME, sfe.get(ScriptHelper.FILENAME));
        assertEquals(LINE_NUMBER, sfe.get(ScriptHelper.LINE));
        assertEquals(COLUMN_NUMBER, sfe.get(ScriptHelper.COLUMN));
    }

    private void assertIsLiveness(SmartFrogException sfe) {
        assertSame(sfException,sfe);
    }


    private SmartFrogException assertExtracted(Throwable in) {
        SmartFrogException sfe = extractNestedSFE(in);
        assertNotNull(sfe);
        assertIsLiveness(sfe);
        return sfe;
    }

    private SmartFrogException extractNestedSFE(Throwable in) {
        return helper.extractNestedSFE(in);
    }

}
