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
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.ProcessCompoundImpl;
//import org.smartfrog.sfcore.processcompound.ProcessCompoundImpl_Stub;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.ProcessReferencePart;
import org.smartfrog.sfcore.common.SmartFrogException;
import java.net.*;



/**
 * JUnit test class for test cases related to "Scripting" component
 */
public class ScriptingTest
   
	extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/scripting/";

    public ScriptingTest(String s) {
        super(s);
    }


public void testCaseTCP50() throws Throwable 
{
		Prim applicationtcp50 = deployExpectingSuccess(FILES+"TCP50.sf", "TCP50");
		assertNotNull(applicationtcp50);
		Prim p = (Prim)applicationtcp50.sfResolveHere("PoorVictim");
		String actualPSfClass = (String)p.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.counter.CounterImpl", actualPSfClass);
}
/*public void testCaseTCP52() throws Throwable 
{
		Prim applicationtcp52 = deployExpectingSuccess(FILES+"TCP52.sf", "TCP52");
		assertNotNull(applicationtcp52);
		
		System.out.println("testCaseTCP52  process name :" +applicationtcp52.sfCompleteName());
	
		ProcessCompound pc= SFProcess.getRootLocator().getRootProcessCompound(SFProcess.sfDeployedHost());
		Prim count=(Prim)pc.sfResolve("exam");
		
		String actualPSfClass =(String) count.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.counter.CounterImpl", actualPSfClass);
}*/

public void testCaseTCP53() throws Throwable 
{
		Prim applicationtcp53 = deployExpectingSuccess(FILES+"TCP53.sf", "TCP52");
		assertNotNull(applicationtcp53);
		Integer actual_numericSum = (Integer)applicationtcp53.sfResolveHere("numericSum");
		String actual_stringSum = (String)applicationtcp53.sfResolveHere("stringSum");
	    assertEquals(11, actual_numericSum.intValue());
		assertEquals("Hello", actual_stringSum);
}



public void testCaseTCN80()  throws Throwable
	{
	/*	deployExpectingException(FILES+"TCN80.sf",
                                 "ScriptingTCN80",
                                 "SmartFrogException",
                                 null,
                                 "SmartFrogException",
                                 "null");
	*/
		Prim applicationtcn80 = deployExpectingSuccess(FILES+"TCN80.sf", "TCN80");
		assertNotNull(applicationtcn80);


	
}
public void testCaseTCN81()  throws Exception
	{
		deployExpectingException(FILES+"TCN81.sf",
                                 "ScriptingTCN81",
                                 "SmartFrogDeploymentException",
                                 null,
                                 EXCEPTION_LINKRESOLUTION,
                                 "Unresolved Reference");
	
	}


// Manual  test case to start the BSH server 
// Manual  test case to read and execute a bsh script file  









}

