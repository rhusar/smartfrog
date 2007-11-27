/** (C) Copyright 2004-2007 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.system.reference;

import org.smartfrog.test.SmartFrogTestBase;


/**
 * JUnit class for negative test cases using attribute resolution functionality provided by SmartFrog framework.
 */
public class ReferenceSystemTest extends SmartFrogTestBase {

    private static final String POSSIBLE_CAUSE_CYCLIC_REFERENCE = "Possible cause: cyclic reference.";
    
    public ReferenceSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN19() throws Exception {
        deployExpectingException("org/smartfrog/test/system/reference/" + "tcn19.sf",
                "tcn19",
                EXCEPTION_DEPLOYMENT,
                "Failed to resolve 'attr ",
                EXCEPTION_LINKRESOLUTION,
                POSSIBLE_CAUSE_CYCLIC_REFERENCE);
    }

}
