/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * A test suite is anything that is testable created 15-Apr-2004 11:51:51
 */


public interface TestSuite extends Prim, Remote, TestResultAttributes {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_IF = "if";
    public static final String ATTR_UNLESS = "unless";
    
    //list in name, value pairs [[name,value],[n2,v2]]
    public static final String ATTR_SYSPROPS="properties";

    //read only properties
    /**
     * bind to the configuration. A null parameter means 'stop binding'
     *
     * @param configuration
     * @throws RemoteException
     */
    void bind(RunnerConfiguration configuration) throws RemoteException,
            SmartFrogException;

    /**
     * run the test
     *
     * @throws RemoteException
     */
    boolean runTests() throws RemoteException, SmartFrogException;

}
