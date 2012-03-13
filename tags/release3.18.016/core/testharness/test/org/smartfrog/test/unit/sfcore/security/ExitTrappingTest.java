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
package org.smartfrog.test.unit.sfcore.security;

import junit.framework.TestCase;
import org.smartfrog.sfcore.security.ExitTrappingSecurityManager;

/**
 * Created 28-Oct-2008 15:17:11
 */

public class ExitTrappingTest extends TestCase {
    private ExitTrappingSecurityManager manager;

    /**
     * Constructs a test case with the given name.
     */
    public ExitTrappingTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        manager = new ExitTrappingSecurityManager();
    }

    public void testManagerConstruct() throws Throwable {
        ExitTrappingSecurityManager.setSystemExitPermitted(true);
        assertTrue(ExitTrappingSecurityManager.isSystemExitPermitted());
        ExitTrappingSecurityManager.setSystemExitPermitted(false);
        assertFalse(ExitTrappingSecurityManager.isSystemExitPermitted());
    }

    public void testManagerCheckEnabled() throws Throwable {
        ExitTrappingSecurityManager.setSystemExitPermitted(true);
        manager.checkExit(1);
        //we should get here OK
    }

    public void testManagerCheckThrowsExceptionWhenBlocking() throws Throwable {
        ExitTrappingSecurityManager.setSystemExitPermitted(false);
        try {
            manager.checkExit(1);
            fail("expected failure");
        } catch (ExitTrappingSecurityManager.SystemExitException expected) {
            //all is well
            assertEquals(1, expected.getStatus());
        }
    }

    public void testSystemExit() throws Throwable {
        if (System.getSecurityManager() != null) {
            fail("There is a security manager already");
        }
        try {
            assertFalse(ExitTrappingSecurityManager.isSecurityManagerRunning());
            assertTrue(ExitTrappingSecurityManager.registerSecurityManager());
            System.exit(1);
        } catch (ExitTrappingSecurityManager.SystemExitException expected) {
            //all is well
            assertEquals(1, expected.getStatus());
        } finally {
            System.setSecurityManager(null);
        }
    }
    public void testRuntimeExit() throws Throwable {
        if (System.getSecurityManager() != null) {
            fail("There is a security manager already");
        }
        try {
            assertFalse(ExitTrappingSecurityManager.isSecurityManagerRunning());
            assertTrue(ExitTrappingSecurityManager.registerSecurityManager());
            Runtime.getRuntime().exit(-1);
        } catch (ExitTrappingSecurityManager.SystemExitException expected) {
            //all is well
            assertEquals(-1, expected.getStatus());
        } finally {
            System.setSecurityManager(null);
        }
    }

}
