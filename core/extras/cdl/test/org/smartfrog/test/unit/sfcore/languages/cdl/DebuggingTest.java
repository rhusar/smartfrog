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
package org.smartfrog.test.unit.sfcore.languages.cdl;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ggf.cddlm.cdl.test.SingleDocumentTestCase;
import org.ggf.cddlm.cdl.test.CDLProcessorFactory;
import org.ggf.cddlm.cdl.test.TestSource;
import org.ggf.cddlm.cdl.test.PatternParser;
import org.ggf.cddlm.cdl.test.TestPattern;
import org.ggf.cddlm.cdl.test.CDLTestUtils;
import org.ggf.cddlm.cdl.test.Tester;
import org.ggf.cddlm.cdl.test.ResourceTestSource;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.test.unit.sfcore.languages.cdl.standard.CdlSmartFrogProcessorFactory;

/**
 * Code to run explicit tests on specific cdl test files, to track down bugs in the handling
 * better
 * created 09-Feb-2006 11:14:36
 */

@SuppressWarnings({"ProhibitedExceptionDeclared"})
public class DebuggingTest extends TestCase {

    private CDLProcessorFactory factory;



    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        factory=new CdlSmartFrogProcessorFactory();
    }

    public CDLProcessorFactory getFactory() {
        return factory;
    }

    protected void runTestOnResource(String resource) throws Throwable {
        //run a test
        TestSource source = new ResourceTestSource(resource);
        PatternParser parser = new PatternParser();
        TestPattern testPattern = CDLTestUtils.parseOneDocument(parser, source);
        Tester tester = new Tester(getFactory());
        //this is just here for easy breakpoint setting
        String name = source.getName();
        //run the test
        tester.test(testPattern);
    }

    public void testBadReference() throws Throwable {
        String resource = CddlmConstants.TEST_PACKAGE_CDL_SET_02 + "/cddlm-cdl-2005-02-0015.xml";
        runTestOnResource(resource);
    }
}
