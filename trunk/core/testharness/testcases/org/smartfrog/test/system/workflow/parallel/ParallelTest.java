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
package org.smartfrog.test.system.workflow.parallel;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.sfcore.prim.Prim;

/**
 * @author Ashish Awasthi
 * Date: 02-Jun-2004
 */
public class ParallelTest extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/parallel/";
    private TestBlock block;

    public ParallelTest(String s) {
        super(s);
    }

    public void setBlock(Prim prim) {
        setBlock((TestBlock) prim);
    }

    public void setBlock(TestBlock block) {
        this.block = block;
    }

    //FIXFIX: RACE CONDITION
    public void testEmptyParallel() throws Throwable {
        application=deployExpectingSuccess(FILES +"testEmptyParallel.sf","testEmptyParallel");
        setBlock(application);
        expectSuccessfulTermination(block);
        assertAttributeEquals(application, TestBlock.ATTR_FORCEDTIMEOUT, true);
    }

    public void testSimpleParallel() throws Throwable {
        application = deployExpectingSuccess(FILES + "testSimpleParallel.sf", "testSimpleParallel");
        setBlock(application);
        expectSuccessfulTermination(block);
        //Prim toggle=(Prim) (application.sfResolve("toggle"));
        assertAttributeEquals(application,"value",true);
    }

    public void testEmptyParallelTerminating() throws Throwable {
        application = deployExpectingSuccess(FILES + "testEmptyParallelTerminating.sf",
                "testEmptyParallelTerminating");
        block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }
}
