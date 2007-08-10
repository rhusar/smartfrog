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
package org.smartfrog.test.unit.projects.alpine;

import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.faults.ValidationException;
import junit.framework.Assert;

/**
 * created 05-Apr-2006 11:35:58
 */

public final class TestHelper {

    protected TestHelper() {
    }

    public static void assertValid(Validatable subject) {
        subject.validate();
    }

    public static void assertInvalid(String message, Validatable subject) {
        try {
            subject.validate();
            Assert.fail(message);
        } catch (ValidationException e) {
            //expected
        }

    }

    public static void assertInvalid(Validatable subject) {
        assertInvalid("Expected invalid object " + subject, subject);
    }

}
