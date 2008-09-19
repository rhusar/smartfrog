/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.constraints;

import junit.framework.TestCase;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * test some of the core solver
 */

public class CoreSolverTest extends TestCase {

    private static Log log= LogFactory.getLog(CoreSolverTest.class);

    private CoreSolver getInstance() {
        CoreSolver instance = CoreSolver.getInstance();
        assertNotNull(instance);
        return instance;
    }

    private CoreSolver getInstance(String classname) {
        System.setProperty(CoreSolver.PROP_SOLVER_CLASSNAME,classname);
        CoreSolver.resetSolverInstance();
        return getInstance();
    }

    private void assertIsCoreSolver(CoreSolver instance) {
        assertEquals(CoreSolver.class,instance.getClass());
    }

    private void assertInstanceMessageContains(String text) {
        String message = CoreSolver.getInstanceMessage();
        assertNotNull("expected a string containing " + text + " but got an empty string", message);
        assertTrue("Did not find \"" + text + "\" in \"" + message + "\"",
                message.contains(text));
    }


    public void testGetInstance() throws Throwable {
        CoreSolver instance = getInstance();
        log.info("Default Solver is " + instance.toString());
        log.info("Default Solver classname is " + CoreSolver.getSolverClassname());
    }

    public void testTwoInstancesSame() throws Throwable {
        CoreSolver instance = getInstance();
        CoreSolver instance2 = getInstance();
        assertSame(instance,instance2);
    }

    public void testMissingClassFails() throws Throwable {
        CoreSolver instance = getInstance("org.example.not.a.solver.classname");
        assertIsCoreSolver(instance);
        assertInstanceMessageContains(CoreSolver.ERROR_NO_CLASS);
        assertFailureExceptionExists();
    }


    public void testWrongClassFails() throws Throwable {
        CoreSolver instance = getInstance("java.lang.String");
        assertIsCoreSolver(instance);
        assertInstanceMessageContains(CoreSolver.ERROR_NOT_A_CORE_SOLVER);
    }

    private void assertFailureExceptionExists() {
        assertNotNull("Expected to get a failure exception, but there is none",
                CoreSolver.getInstanceFailureCause());
    }

    public void testCoreSolverWorks() throws Throwable {
        CoreSolver instance = getInstance(CoreSolver.CORE_SOLVER);
        assertIsCoreSolver(instance);
    }

    public void testEclipseSolverMayOrMayNotLoad() throws Throwable {
        CoreSolver instance = getInstance(CoreSolver.ECLIPSE_SOLVER);
    }

    public void testMockSolver() throws Throwable {
        CoreSolver instance = getInstance(MockSolver.MOCK_SOLVER);
        MockSolver solver=(MockSolver) instance;
    }
}
