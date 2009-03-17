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
package org.smartfrog.services.junit.junit3;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * A variant on how JUnit3 reports problems. This pseudo-test case class throws whichever fault is passed in created
 * 24-Oct-2006 13:38:22
 */

public class Warning extends TestCase {


    private Throwable thrown;

    public Warning(String name) {
        super(name);
    }

    public Throwable getThrown() {
        return thrown;
    }

    public void setThrown(Throwable thrown) {
        this.thrown = thrown;
    }

    /**
     * Constructs a test case with the given name.
     *
     * @param name   test name
     * @param thrown exception to raise later
     */
    public Warning(String name, Throwable thrown) {
        super(name);
        this.thrown = thrown;
    }

    /**
     * A constructor that creates a new {@link AssertionFailedError} with the specified message
     *
     * @param name    test name
     * @param message error message
     */
    public Warning(String name, String message) {
        super(name);
        thrown = new AssertionFailedError(message);
    }


    /**
     * Throws whatever is in the thrown attribute
     *
     * @throws Throwable whatever we were constructed with
     */
    public void testReportError() throws Throwable {
        if (thrown != null) {
            throw thrown;
        }
    }
}
