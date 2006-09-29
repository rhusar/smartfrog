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
import org.smartfrog.sfcore.languages.cdl.references.StepRefRoot;
import org.smartfrog.sfcore.languages.cdl.references.StepStart;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.ElementEx;
import org.smartfrog.sfcore.languages.cdl.dom.SystemElement;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * This is the test case for reference building
 */
@SuppressWarnings({"ProhibitedExceptionDeclared"})
public class ReferencePathTest extends TestCase {

    private ReferencePath path;

    private PropertyList root;
    private PropertyList child1;
    private PropertyList child2;

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        path = new ReferencePath();
        root = new SystemElement("root");

        child1 = new PropertyList("child1");
        root.appendChild(child1);
        child2 = new PropertyList("child2");
        child1.appendChild(child2);
    }

    private void assertStepType(int position, Class type) {
        List<Step> steps = path.getSteps();
        Step s1 = steps.get(position);
        assertEquals(type, s1.getClass());
    }

    private void assertStepRoot(int position) {
        assertStepType(position, StepRoot.class);
    }

    private void assertStepRefRoot(int position, String localname) {
        assertStepRefRoot(position, localname, "");

    }

    private void assertStepRefRoot(int position, String localname, String uri) {
        assertStepType(position, StepRefRoot.class);
        StepRefRoot s1 = (StepRefRoot) path.getStep(position);
        QName r = s1.getRefroot();
        assertEquals(localname, r.getLocalPart());
        assertEquals(uri, r.getNamespaceURI());
    }

    private void assertStepUp(int position) {
        assertStepType(position, StepUp.class);
    }

    private void assertStepHere(int position) {
        assertStepType(position, StepHere.class);
    }

    private void assertStepStart(int position) {
        assertStepType(position, StepStart.class);
    }

    private void assertStepDown(int position, String localname) {
        assertStepDown(position, localname, null);
    }

    private void assertStepDown(int position, String localname, String prefix) {
        assertStepType(position, StepDown.class);
        StepDown s1 = (StepDown) path.getStep(position);
        assertEquals(localname, s1.getLocalname());
        assertEquals(prefix, s1.getPrefix());
    }

    private void assertStepSize(int size) {
        int actual = path.size();
        if (size != actual) {
            String text;
            text = "Expected " + size + " elements in path " + path + " but found " + actual;
            fail(text);
        }
    }

    public void testBuildHere() throws Exception {
        path.build(".", null);
        assertStepSize(2);
        assertStepStart(0);
        assertStepHere(1);
    }

    public void testBuildRoot() throws Exception {
        path.build("/", null);
        assertStepSize(1);
        assertStepRoot(0);
    }

    public void testBuildUp() throws Exception {
        path.build("..", null);
        assertStepSize(2);
        assertStepStart(0);
        assertStepUp(1);
    }

    public void testBuildDownLocal() throws Exception {
        path.build("child", null);
        assertStepSize(2);
        assertStepStart(0);
        assertStepDown(1, "child");
    }

    public void testBuildDownPrefix() throws Exception {
        path.build("../tns:child", null);
        assertStepSize(3);
        assertStepStart(0);
        assertStepUp(1);
        assertStepDown(2, "child", "tns");
    }

    public void testComplexPath() throws Exception {
        path.build("/../tns:child/.././ns2:something/../local/.", null);
        assertStepSize(9);
        assertStepRoot(0);
        assertStepUp(1);
        assertStepDown(2, "child", "tns");
        assertStepUp(3);
        assertStepHere(4);
        assertStepDown(5, "something", "ns2");
        assertStepUp(6);
        assertStepDown(7, "local");
        assertStepHere(8);
    }


    public void testExtractHere() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REF, ".");
        path = new ReferencePath(child2);
        assertStepSize(2);
        assertStepStart(0);
        assertStepHere(1);
    }

    public void testExtractUp() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REF, ".././child2");
        path = new ReferencePath(child2);
        assertStepSize(4);
        assertStepStart(0);
        assertStepUp(1);
        assertStepHere(2);
        assertStepDown(3, "child2");
    }

    public void testExtractRoot() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REF, "/");
        path = new ReferencePath(child2);
        assertStepSize(2);
        assertStepUp(0);
        assertStepUp(1);
    }

    public void testExtractRootChild1() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REF, "/child1");
        path = new ReferencePath(child2);
        assertStepSize(3);
        assertStepUp(0);
        assertStepUp(1);
        assertStepDown(2, "child1");
    }

    public void testNoParentBreaks() throws Exception {
        PropertyList orphan = new PropertyList("orphan");
        orphan.addNewAttribute(Constants.QNAME_CDL_REF, "/child1");
        try {
            path = new ReferencePath(orphan);
            fail("expected an assertion");
        } catch (CdlRuntimeException e) {
            assertEquals(ReferencePath.ERROR_NO_TOPLEVEL, e.getMessage());
        }
    }

    public void testExtractRefRoot() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REFROOT, "application");
        child2.addNewAttribute(Constants.QNAME_CDL_REF, "/something/else");
        path = new ReferencePath(child2);
        assertStepSize(4);
        assertStepRefRoot(0, "application");
        assertStepRoot(1);
        assertStepDown(2, "something");
        assertStepDown(3, "else");
    }

    public void testExtractRefRootPrefix() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REFROOT, "cdl:application");
        child2.addNewAttribute(Constants.QNAME_CDL_REF, "/something/else");
        path = new ReferencePath(child2);
        assertStepSize(4);
        assertStepRefRoot(0, "application", Constants.XMLNS_CDL);
        assertStepRoot(1);
        assertStepDown(2, "something");
        assertStepDown(3, "else");
    }

    public void testRefRootBadPrefixBreaks() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REFROOT, "cdl4:application");
        child2.addNewAttribute(Constants.QNAME_CDL_REF, "/something/else");
        try {
            path = new ReferencePath(child2);
            fail("expected an assertion");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith(ElementEx.ERROR_NON_RESOLVABLE_QNAME_PREFIX));
        }
    }

    public void testLazyPathMerge() throws Exception {
        child2.addNewAttribute(Constants.QNAME_CDL_REFROOT, "cdl:application");
        child2.addNewAttribute(Constants.QNAME_CDL_REF, "/something/else");
        path = new ReferencePath(child2);
        assertFalse(path.isLazy());
        //child2.addNewAttribute(Constants.QNAME_CDL_LAZY,"true");
        child2.setLazy(true);
        ReferencePath path2=new ReferencePath(child2);
        path.appendPath(path2);
        assertTrue(path.isLazy());
        assertStepSize(8);
        assertStepRefRoot(4, "application", Constants.XMLNS_CDL);
        assertStepRoot(5);
        assertStepDown(6, "something");
        assertStepDown(7, "else");
    }

}
