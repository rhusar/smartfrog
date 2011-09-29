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


package org.smartfrog.services.persistence.test.testcases;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.SFSystem;
import java.util.Vector;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;

import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.locator.Locator;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.smartfrog.sfcore.common.Dumper;
import org.smartfrog.sfcore.common.DumperCDImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.services.persistence.framework.activator.Activator;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.services.persistence.test.hsqldb.TestInterface;

/**
 * JUnit test class for test cases related to "Solidwoodfrog" component
 */
public class PersistenceTest
    extends SmartFrogTestBase {

    private static final String HSQL_PATH = "org/smartfrog/services/persistence/test/hsqldb/";
	private static final String TESTCASES_PATH = "org/smartfrog/services/persistence/test/testcases/";
	private Log log = null;
    public PersistenceTest(String s) {
		 super(s);
		 try{
			log = LogFactory.getLog(this);
		  }catch(Exception e){
		  }
	}

    
	public void testCaseLockCollision() throws Throwable {    
			application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");			
			Prim tempPrim = deployExpectingSuccess(TESTCASES_PATH+"lockCollisionTest.sf", "tcLockCollision");	
			terminateApplication(tempPrim);
			terminateApplication(application);						
    }


	public void testCasePendingTermination() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}
							
			Prim tempPrim = deployExpectingSuccess(TESTCASES_PATH+"pendingTerminationTest.sf", "tcpendingTermination");	
			terminateApplication(tempPrim);
			terminateApplication(application);
		
	}
	
	
	public void testCasecreateNewChild() throws Throwable {
		boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}
			
			Prim tempPrim = deployExpectingSuccess(TESTCASES_PATH+"createNewChildTest.sf", "tccreatenewchildtest");	
			terminateApplication(tempPrim);
			terminateApplication(application);
	}

	
	public void testTranasactions() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}
			deployExpectingException(TESTCASES_PATH+"xactTest.sf", "tctransactions","SmartFrogLifecycleException","ConcurrentTransactionException adding ",
						"ConcurrentTransactionException","ConcurrentTransactionException adding attr");
			terminateApplication(application);
	}
	
	
	public void testTranasactionDeadLock() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}	
			deployExpectingException(TESTCASES_PATH+"transactionDeadlockTest.sf", "tctranasactiondeadlock","Exception","Failed to get next transaction");
			terminateApplication(application);
	}

	
	/************************************** update does not work ********************************
	public void testUpdate() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}		
			Prim tempPrim = deployExpectingSuccess(TESTCASES_PATH+"update1.sf", "tcupdate1");
			updateExpectingException(TESTCASES_PATH+"update2.sf","tcupdate2","SmartFrogUpdateException","abandoning");	
			terminateApplication(tempPrim);
			terminateApplication(application);
			//Thread.sleep(50);
	}
	
	
	public Throwable updateExpectingException(String testURL, String appName,String exceptionName,String searchString ) throws Exception{
		try{
			ConfigurationDescriptor cfgDesc =
					new ConfigurationDescriptor(appName,
							testURL,
							ConfigurationDescriptor.Action.UPDATE,
							"localhost",
							null);
			Object deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc, true);

		}catch(Exception e){
			
			if(log!=null)
				log.info("Exception as Expected -- can not update");
				terminateApplication(application);
				Thread.sleep(50);
			
		}
			return null;
	}
	******************************* update does not work *********************************/
	

	public void testCaseDeployEmpty() throws Throwable {
		boolean found = false;
		while(!found){
			try{
				Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
			}catch(SmartFrogResolutionException e){
				application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
				found = true;
			}catch(Exception e ){
				found = false;
			}
		}	
		/**
		 * deploy empty.sf - contains volatile and cached attributes
		 */
		deployExpectingSuccess(TESTCASES_PATH+"empty.sf", "tcEmpty");	

		/**
		 * failover
		 */
		TestInterface t = (TestInterface) application.sfResolveHere("test");
		t.simulateFailover();

		/**
		 * check for existence (or non-existence) of tagged attributes
		 */
		Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("tcEmpty");
		if( p.sfContainsAttribute("volatile") ) {
			fail("The \'volatile\' values should not exist");
		}
		if( p.sfContainsAttribute("both") ) {
			fail("The \'both\' (marked \'volatile\' and \'cached\') values should not exist");
		}
		if( !p.sfContainsAttribute("cached") ) {
			fail("The \'cached\' values should exist");
		}
		terminateApplication(p);
		terminateApplication(application);
		Thread.sleep(50);
	}

	
	public void testAlternateRebind() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}
			/**
			 * deploy hello server (rebinding interface)
			 */
			Prim tempPrim = deployExpectingSuccess(TESTCASES_PATH+"alternateRebind.sf", "altRebind");
			TestInterface t = (TestInterface) application.sfResolveHere("test");
			
			/**
			 * get the server hello interface and invoke it
			 */
			Vector hosts = new Vector();
			hosts.add("localhost");
			Hello helloServer = (Hello)Locator.multiHostSfResolve(hosts, "altRebind");
			helloServer.hello("test harness before failover");

			/**
			 * failover
			 */
			t.simulateFailover();
			
			/**
			 * invoke the hello server interface again - should rebind and call successfully
			 */
			helloServer.hello("test harness after failover");
			
			terminateApplication(tempPrim);
			terminateApplication(application);
			Thread.sleep(50);
					
	}	

	
	public void testCaseDeployFailDeployWith() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}	

			/**
			 * Deploy component expected to failure during deployWith phase
			 */
			deployExpectingException(TESTCASES_PATH+"deployFailDeployWith.sf", "tcdeployFailDeployWith","SmartFrogDeploymentException","child2 failed to deploy");

			/**
			 * clean up is asynchronous so wait more than 500 millis to make sure its been done.
			 */
			Thread.sleep(1000);	
			
			/**
			 * try deploying a component that would clash on unique component name to check cleanup
			 */
			Prim sample = deployExpectingSuccess(TESTCASES_PATH+"deployFailTest.sf", "tcdeployFail");
			
			terminateApplication(sample);
			terminateApplication(application);
			Thread.sleep(100);
											
    }
	
	public void testCaseDeployFailStart() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}	
			
			/**
			 * Deploy component expected to failure during start phase
			 */
			deployExpectingException(TESTCASES_PATH+"deployFailStart.sf", "tcdeployFailStart","SmartFrogLifecycleException","child2");
			
			/**
			 * clean up is asynchronous so wait more than 500 millis to make sure its been done.
			 */
			Thread.sleep(1000);	
			
			/**
			 * try deploying a component that would clash on unique component name to check cleanup
			 */
			Prim sample = deployExpectingSuccess(TESTCASES_PATH+"deployFailTest.sf", "tcdeployFail");
			
			terminateApplication(sample);
			terminateApplication(application);
			Thread.sleep(100);								
    }
	
	
	public void testCaseDeployRecoveryStart() throws Throwable {
			boolean found = false;
			while(!found){
				try{
					Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
				}catch(SmartFrogResolutionException e){
					application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
					found = true;
				}catch(Exception e ){
					found = false;
				}
			}
			
			/**
			 * deploy component expected to fail during start phase of recovery (should succeed on initial deployment)
			 */
			Prim pt = deployExpectingSuccess(TESTCASES_PATH+"deployFailRecoveryStart.sf", "tcdeployFail");
			
			/**
			 * Simulate failover - component should fail during recovery start phase
			 */
			TestInterface t = (TestInterface) application.sfResolveHere("test");
			t.simulateFailover();
			
			/**
			 * clean up is asynchronous so wait more than 500 millis to make sure its been done.
			 */
			Thread.sleep(1000);	
			
			/**
			 * try deploying a component that would clash on unique component name to check cleanup
			 */
			Prim sample = deployExpectingSuccess(TESTCASES_PATH+"deployFailTest.sf", "deployAgain");
			
			terminateApplication(sample);
			terminateApplication(application);
			Thread.sleep(100);
    }
	
	public void testDump() throws Throwable {
		boolean found = false;
		while(!found){
			try{
				Prim p =(Prim)SFProcess.getRootLocator().getRootProcessCompound(null).sfResolveHere("InterfaceManager");
			}catch(SmartFrogResolutionException e){
				application = deployExpectingSuccess(HSQL_PATH+"components.sf", "tcnHSQLDB");	
				found = true;
			}catch(Exception e ){
				found = false;
			}
		}
		StringBuffer message = new StringBuffer();
		String name = "error";
		try {
			Prim objPrim = application;				
			message.append ("\nDump\n");
			Dumper dumper = new DumperCDImpl(objPrim);				
			objPrim.sfDumpState(dumper.getDumpVisitor());				
			message.append (dumper.toString(30*1000L));				
			name = (objPrim).sfCompleteName().toString();
		} catch (Exception ex) {

			StringWriter sw = new StringWriter();
			PrintWriter pr = new PrintWriter(sw,true);
			ex.printStackTrace(pr);
			pr.close();
			message.append("\n Error: "+ex.toString()+"\n"+sw.toString());
		}
	}


}

