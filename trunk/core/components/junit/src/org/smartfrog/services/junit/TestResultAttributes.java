/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.junit;

/**
 * nothing but results
 * Date: 07-Jul-2004
 * Time: 20:42:05
 */

public interface TestResultAttributes {
    /**
     * true if the test has finished
     */
    public String ATTR_FINISHED = "finished";

    /**
     * number of tests
     */
    public String ATTR_TESTS = "tests";
    /**
     * number of failures
     */
    public String ATTR_FAILURES = "failures";

    /**
     * number of errors
     */
    public String ATTR_ERRORS = "errors";
    /**
     * boolean set to true iff all tests passed
     */
    public String ATTR_SUCCESSFUL = "successful";


}
