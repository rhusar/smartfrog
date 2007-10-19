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


package org.smartfrog.test.system.examples;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;


/**
 * JUnit test class for test cases related to HelloWorld Example 
 *  */

public class HelloWorldExampleTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/examples/helloworld/";

    public HelloWorldExampleTest(String s) {
        super(s);
    }

    public void testCaseHWE01() throws Throwable 
	{
            application = deployExpectingSuccess(FILES+"example1.sf", "tcHWE01");
		    assertNotNull(application);

			Prim generator = (Prim)application.sfResolve("g");
			
			int actualGeneratorFrq = 0;
			int expectedGeneratorFrq= 10;
			actualGeneratorFrq = generator.sfResolve("frequency",actualGeneratorFrq,true);
			assertEquals(expectedGeneratorFrq, actualGeneratorFrq);

			Prim printer = (Prim)application.sfResolve("p");
			
			String actualPrinterName = "";
			String expectedPrinterName= "myPrinter";
			actualPrinterName = printer.sfResolve("name",actualPrinterName,true);
			assertEquals(expectedPrinterName, actualPrinterName);
    }

	public void testCaseHWE01a() throws Throwable 
	{
			application = deployExpectingSuccess(FILES+"example1a.sf", "tcHWE01a");
			assertNotNull(application);

			Prim generator = (Prim)application.sfResolve("g");
			
			int actualGeneratorFrq = 0;
			int expectedGeneratorFrq= 10;
			actualGeneratorFrq = generator.sfResolve("frequency",actualGeneratorFrq,true);
			assertEquals(expectedGeneratorFrq, actualGeneratorFrq);

			Prim printer = (Prim)application.sfResolve("p");
			
			String actualPrinterName = "";
			String expectedPrinterName= "printerA";
			actualPrinterName = printer.sfResolve("name",actualPrinterName,true);
			assertEquals(expectedPrinterName, actualPrinterName);
	}
	
	public void testCaseHWE01b() throws Throwable 
	{
			application = deployExpectingSuccess(FILES+"example1b.sf", "tcHWE01b");
			assertNotNull(application);

			Prim generator = (Prim)application.sfResolve("g");
			
			int actualGeneratorFrq = 0;
			int expectedGeneratorFrq= 10;
			actualGeneratorFrq = generator.sfResolve("frequency",actualGeneratorFrq,true);
			assertEquals(expectedGeneratorFrq, actualGeneratorFrq);

			Prim printer = (Prim)application.sfResolve("p");
			
			String actualPrinterName = "";
			String expectedPrinterName= "printerB";
			actualPrinterName = printer.sfResolve("name",actualPrinterName,true);
			assertEquals(expectedPrinterName, actualPrinterName);
	}

    public void testCaseHWE01c() throws Throwable
	{
			application = deployExpectingSuccess(FILES+"example1c.sf", "tcHWE01c");
			assertNotNull(application);

			Prim generator = (Prim)application.sfResolve("g");
			
			int actualGeneratorFrq = 0;
			int expectedGeneratorFrq= 10;
			actualGeneratorFrq = generator.sfResolve("frequency",actualGeneratorFrq,true);
			assertEquals(expectedGeneratorFrq, actualGeneratorFrq);

			Prim printer = (Prim)application.sfResolve("p");
			
			String actualPrinterName = "";
			String expectedPrinterName= "printerC";
			actualPrinterName = printer.sfResolve("name",actualPrinterName,true);
			assertEquals(expectedPrinterName, actualPrinterName);
	}
	public void testCaseHWE01dist() throws Throwable 
	{
			application = deployExpectingSuccess(FILES+"example1dist.sf", "tcHWE01Dist");
			assertNotNull(application);

			Prim generator = (Prim)application.sfResolve("g");
			
			int actualGeneratorFrq = 0;
			int expectedGeneratorFrq= 10;
			actualGeneratorFrq = generator.sfResolve("frequency",actualGeneratorFrq,true);
			assertEquals(expectedGeneratorFrq, actualGeneratorFrq);

			Prim printer = (Prim)application.sfResolve("p");
			
			String actualPrinterName = "";
			String expectedPrinterName= "myPrinter";
			actualPrinterName = printer.sfResolve("name",actualPrinterName,true);
			assertEquals(expectedPrinterName, actualPrinterName);
		
	}
	public void testCaseHWE02() throws Throwable 
	{
			application = deployExpectingSuccess(FILES+"example2.sf", "tcHWE2");
			assertNotNull(application);

			Prim generator1 = (Prim)application.sfResolve("g1");
			
			int actualGenerator1Frq = 0;
			int expectedGenerator1Frq= 10;
			actualGenerator1Frq = generator1.sfResolve("frequency",actualGenerator1Frq,true);
			assertEquals(expectedGenerator1Frq, actualGenerator1Frq);

			Prim generator2 = (Prim)application.sfResolve("g2");
			
			int actualGenerator2Frq = 0;
			int expectedGenerator2Frq= 5;
			actualGenerator2Frq = generator2.sfResolve("frequency",actualGenerator2Frq,true);
			assertEquals(expectedGenerator2Frq, actualGenerator2Frq);

			Prim printer = (Prim)application.sfResolve("p");
			
			String actualPrinterName = "";
			String expectedPrinterName= "myPrinter";
			actualPrinterName = printer.sfResolve("name",actualPrinterName,true);
			assertEquals(expectedPrinterName, actualPrinterName);
	}
	public void testCaseHWE03() throws Throwable 
	{
			application = deployExpectingSuccess(FILES+"example3.sf", "tcHWE3");
			assertNotNull(application);
			
			Prim pair1 = (Prim)application.sfResolve("pair1");
			
			Prim generator1 = (Prim)pair1.sfResolve("g");
				int actualGenerator1Frq = 0;
				int expectedGenerator1Frq= 10;
				actualGenerator1Frq = generator1.sfResolve("frequency",actualGenerator1Frq,true);
				assertEquals(expectedGenerator1Frq, actualGenerator1Frq);
			
			Prim printer1 = (Prim)pair1.sfResolve("p");
			assertNotNull(printer1);

			Prim pair2 = (Prim)application.sfResolve("pair2");

			Prim generator2 = (Prim)pair2.sfResolve("g");
				int actualGenerator2Frq = 0;
				int expectedGenerator2Frq= 10;
				actualGenerator2Frq = generator2.sfResolve("frequency",actualGenerator2Frq,true);
				assertEquals(expectedGenerator2Frq, actualGenerator2Frq);
			
			Prim printer2 = (Prim)pair2.sfResolve("p");
				assertNotNull(printer2);
			
	}

/*	DetachingCompound
 *	public void testCaseHWE04() throws Throwable 
	{
			Prim applicationHWE4 = deployExpectingSuccess(FILES+"example4.sf", "tcHWE4");
			assertNotNull(applicationHWE4);
			
		//	Prim pair1 = (Prim)applicationHWE4.sfResolve("pair1");
			
			Prim generator1 = (Prim)applicationHWE4.sfResolve("g");
				int actualGenerator1Frq = 0;
				int expectedGenerator1Frq= 10;
				actualGenerator1Frq = generator1.sfResolve("frequency",actualGenerator1Frq,true);
				assertEquals(expectedGenerator1Frq, actualGenerator1Frq);
			
			Prim printer1 = (Prim)applicationHWE4.sfResolve("p");
			assertNotNull(printer1);


	}*/
	public void testCaseHWE05() throws Throwable 
	{
		//DetachingCompound
	}

}

