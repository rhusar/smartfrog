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
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Implementation of the test suite component
 * created 14-May-2004 15:14:23
 */

public class TestSuiteComponent extends PrimImpl implements TestSuite {

    public TestSuiteComponent() throws RemoteException {
    }

    /*
    TestSuiteSchema extends Schema {
    class extends OptionalString;
    if extends OptionalBoolean;
    unless extends OptionalBoolean;
    subpackages extends OptionalBoolean;
    package extends String;
    excludes extends OptionalList;

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }
*/

    private String testClass;

    private boolean ifValue;

    private boolean unlessValue;

    private boolean subPackages;

    private List excludes;

    public String getTestClass() throws RemoteException {
        return testClass;
    }

    public void setTestClass(String testClass) throws RemoteException {
        this.testClass = testClass;
    }

    public boolean getIf() throws RemoteException {
        return ifValue;
    }

    public void setIf(boolean ifValue) throws RemoteException {
        this.ifValue = ifValue;
    }

    public boolean getUnless() throws RemoteException {
        return unlessValue;
    }

    public void setUnless(boolean unlessValue) throws RemoteException {
        this.unlessValue = unlessValue;
    }

    public boolean getSubPackages() throws RemoteException {
        return subPackages;
    }

    public void setSubPackages(boolean subPackages) throws RemoteException {
        this.subPackages = subPackages;
    }

    public List getExcludes() throws RemoteException {
        return excludes;
    }

    public void setExcludes(List excludes) throws RemoteException {
        this.excludes = excludes;
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }


}
