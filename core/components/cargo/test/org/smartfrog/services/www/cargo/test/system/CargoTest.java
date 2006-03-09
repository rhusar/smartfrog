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
package org.smartfrog.services.www.cargo.test.system;


public class CargoTest extends CargoTestBase {
    public CargoTest(String name) {
        super(name);
    }

    public void testIncomplete() throws Exception {
        deployExpectingException(FILE_BASE + "testIncomplete.sf",
                "testIncomplete",
                EXCEPTION_DEPLOYMENT,
                "",
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'configurationClass' is missing");
    }

    public void testBadCargoClass() throws Throwable {
        deployExpectingException(FILE_BASE + "testBadCargoClass.sf",
                "testBadCargoClass",
                EXCEPTION_LIFECYCLE,
                "",
                EXCEPTION_SMARTFROG,
                "Cannot find org.codehaus.cargo.container.badConfigurationClass");
    }


}
