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


package org.smartfrog.test.system.workflow;

import org.smartfrog.test.SmartFrogTestBase;

import java.util.Enumeration;

/**
 * @author Ashish Awasthi
 * Date: 02-Jun-2004
 */
public class SequenceTest extends SmartFrogTestBase {
    private static final String FILES = "org/smartfrog/test/system/workflow/";
    public SequenceTest(String s) {
        super(s);
    }
    public void testComponentFailureInSequence() throws Throwable {
        //This does not apply any more. It does not throw exception,
        // it does proper termination of the component. The termination record
        //should notify thre right reason class not found.
//        deployExpectingException(FILES+"testSequence.sf",
//                "sequencefail",
//                "SmartFrogDeploymentException",
//                "Class not found");
        this.deployExpectingSuccess(FILES+"testSequence.sf","sequencefail");
    }
    
    public void testComponentFailureInNewSequence() throws Throwable {
        //This does not apply any more. It does not throw exception,
        // it does proper termination of the component. The termination record
        //should notify thre right reason class not found.
//        deployExpectingException(FILES+"testSequence.sf",
//                "sequencefail",
//                "SmartFrogDeploymentException",
//                "Class not found");
        this.deployExpectingSuccess(FILES+"testNewSequence.sf","sequencenewfail");
    }    
}
