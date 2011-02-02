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


import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.test.SmartFrogTestBase;


/**
 * JUnit class for negative test cases using attribute resolution functionality provided by SmartFrog framework.
 */
public class CyclicReferenceTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/reference/";
    private static final String POSSIBLE_CAUSE_CYCLIC_REFERENCE = SFComponentDescriptionImpl.POSSIBLE_CAUSE_CYCLIC_REFERENCE;

    public CyclicReferenceTest(String s) {
        super(s);
    }

    public void testCaseDummy()  {
	;
    }


    /**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseTCN96() throws Throwable {
        deployExpectingException(FILES + "tcn96.sf",
                "tcn96",
                EXCEPTION_DEPLOYMENT,
                "Failed to resolve 'link link'.",
                EXCEPTION_LINKRESOLUTION,
                POSSIBLE_CAUSE_CYCLIC_REFERENCE);
		}

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCN97() throws Throwable {
        deployExpectingException(FILES + "tcn97.sf",
                "tcn97",
                EXCEPTION_DEPLOYMENT,
                "Failed to resolve 'link PARENT:sfConfig:link'.",
                EXCEPTION_LINKRESOLUTION,
                POSSIBLE_CAUSE_CYCLIC_REFERENCE);
		}

    //Tests 98 and 99 -- no longer applicable since new link resolution...
    //That is, new link resolution will not cause a stack overflow, rather will run out of heap space... 
    ///**
    // * test case
    // * @throws Throwable on failure
    // */
    /*public void testCaseTCN98() throws Throwable {
        deployExpectingException(FILES + "tcn98.sf",
                "tcn98",
                EXCEPTION_DEPLOYMENT,
                "[unprintable cyclic value]",
                EXCEPTION_LINKRESOLUTION,
                POSSIBLE_CAUSE_CYCLIC_REFERENCE);
		}
    */
    ///**
    // * test case
    // * @throws Throwable on failure
    // */
    /*public void testCaseTCN99() throws Throwable {
        deployExpectingException(FILES + "tcn99.sf",
                "tcn99",
                EXCEPTION_DEPLOYMENT,
                "",
                EXCEPTION_TYPERESOLUTION,
                POSSIBLE_CAUSE_CYCLIC_REFERENCE);
		}
    */

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCN19() throws Throwable {
        deployExpectingException("org/smartfrog/test/system/reference/" + "tcn19.sf",
                "tcn19",
                EXCEPTION_DEPLOYMENT,
                "Failed to resolve 'attr ",
                EXCEPTION_LINKRESOLUTION,
                POSSIBLE_CAUSE_CYCLIC_REFERENCE);
		}
}