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


package org.smartfrog.test.system.components.scripting;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;


/**
 * JUnit test class for test cases related to "scripting" component
 */
public class ScriptingTest
   
	extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/slp/";

    public ScriptingTest(String s) {
        super(s);
    }


public void testCaseTCP50() throws Throwable 
{
		Prim applicationtcp50 = deployExpectingSuccess("org/smartfrog/test/system/components/scripting/TCP50.sf", "ScriptingTCP50");
		assertNotNull(applicationtcp50);
		Prim p = (Prim)applicationtcp50.sfResolveHere("PoorVictim");
		String actualPSfClass = (String)p.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.counter.CounterImpl", actualPSfClass);
}
public void testCaseTCN80()  throws Exception
	{
		deployExpectingException("org/smartfrog/test/system/components/scripting/TCN80.sf",
                                 "ScriptingTCN80",
                                 "SmartFrogException",
                                 null,
                                 "SmartFrogException",
                                 "null");
	
}
public void testCaseTCN81()  throws Exception
	{
		deployExpectingException("org/smartfrog/test/system/components/scripting/TCN81.sf",
                                 "ScriptingTCN81",
                                 "SmartFrogDeploymentException",
                                 null,
                                 "SmartFrogCompileResolutionException",
                                 "Unresolved Reference");
	
}




}

