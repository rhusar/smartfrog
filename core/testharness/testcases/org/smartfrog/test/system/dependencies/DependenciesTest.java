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


package org.smartfrog.test.system.dependencies;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.test.DeployingTestBase;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class DependenciesTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/dependencies/";

    public DependenciesTest(String name) {
        super(name);
    }

    public void testManagedEntity() throws Throwable {
    	//deploy
    	Prim threadpool=deployExpectingSuccess(FILES+"testManagedEntitiesTP.sf","testManagedEntitiesTP");
        application=deployExpectingSuccess(FILES+"testManagedEntities.sf","testManagedEntities");
        
        //test it
        String output = (String) application.sfResolve("output"); 
    	System.out.println("*****************************************************");
    	System.out.println(output);
    	assertEquals(output,"foo0cfoo1cfoo2cfoo2rfoo1rfoo0r");
        
        //clean up
        terminateApplication();
        threadpool.sfTerminate(TerminationRecord.normal(null));
    }
}
