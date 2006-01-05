/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.unit.sfcore.languages.cdl.references;

import junit.framework.TestCase;
import org.smartfrog.sfcore.languages.cdl.references.ReferencePath;
import org.smartfrog.sfcore.languages.cdl.references.Step;
import org.smartfrog.sfcore.languages.cdl.references.StepHere;
import org.smartfrog.sfcore.languages.cdl.references.StepRoot;
import org.smartfrog.sfcore.languages.cdl.references.StepUp;
import org.smartfrog.sfcore.languages.cdl.references.StepDown;

import java.util.List;

/**
 * This is the test case for reference building
 */
public class ReferencePathTest extends TestCase {

    public ReferencePath path;
    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        path=new ReferencePath();
    }

    public void testBuildHere() throws Exception {
        path.build(".");
        List<Step> steps = path.getSteps();
        assertEquals(1,steps.size());
        Step s1 = steps.get(0);
        assertTrue(s1 instanceof StepHere);
    }

    public void testBuildRoot() throws Exception {
        path.build("/");
        List<Step> steps = path.getSteps();
        assertEquals(1, steps.size());
        Step s1 = steps.get(0);
        assertTrue(s1 instanceof StepRoot);
    }

    public void testBuildUp() throws Exception {
        path.build("..");
        List<Step> steps = path.getSteps();
        assertEquals(1, steps.size());
        Step s1 = steps.get(0);
        assertTrue(s1 instanceof StepUp);
    }

    public void testBuildDownLocal() throws Exception {
        path.build("child");
        List<Step> steps = path.getSteps();
        assertEquals(1, steps.size());
        Step s1 = steps.get(0);
        assertTrue(s1 instanceof StepDown);
        StepDown sd=(StepDown) s1;
        assertEquals("child",sd.getLocalname());
    }

    public void testBuildDownPrefix() throws Exception {
        path.build("../tns:child");
        List<Step> steps = path.getSteps();
        assertEquals(2, steps.size());
        Step s1 = steps.get(1);
        assertTrue(s1 instanceof StepDown);
        StepDown sd = (StepDown) s1;
        assertEquals("child", sd.getLocalname());
        assertEquals("tns", sd.getPrefix());
    }

    public void testComplexPath() throws Exception {
        path.build("/../tns:child/.././ns2:something/../local/.");
        List<Step> steps = path.getSteps();
        assertEquals(9, steps.size());
    }
}
