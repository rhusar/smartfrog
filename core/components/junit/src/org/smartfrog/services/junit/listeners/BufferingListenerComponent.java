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


package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestInfo;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class buffers received messages
 * Date: 27-Jun-2004
 * Time: 21:26:28
 */
public class BufferingListenerComponent  extends PrimImpl implements BufferingListener {

    public BufferingListenerComponent() throws RemoteException {
    }

    ArrayList errors,failures,starts,ends;

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        errors=new ArrayList();
        failures = new ArrayList();
        starts = new ArrayList();
        ends = new ArrayList();
    }

    /**
     * An error occurred.
     */
    public synchronized void addError(TestInfo test) throws RemoteException {
        TestInfo cloned = cloneTestInfo(test);
        errors.add(cloned);
    }

    /**
     * make a clone of any test info
     * @param test
     * @return
     */
    private TestInfo cloneTestInfo(TestInfo test) {
        TestInfo cloned =null;
        try {
            cloned= (TestInfo) test.clone();
        } catch (CloneNotSupportedException e) {
            //we should never get here
            assert false;
        }
        return cloned;
    }

    /**
     * A failure occurred.
     */
    public synchronized void addFailure(TestInfo test) throws RemoteException {
        TestInfo cloned = cloneTestInfo(test);
        failures.add(cloned);
    }

    /**
     * A test ended.
     */
    public synchronized void endTest(TestInfo test) throws RemoteException {
        TestInfo cloned = cloneTestInfo(test);
        ends.add(cloned);
    }

    /**
     * A test started.
     */
    public synchronized void startTest(TestInfo test) throws RemoteException {
        TestInfo cloned = cloneTestInfo(test);
        starts.add(cloned);
    }

    /**
     * get the number of errors
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public synchronized int getErrorCount() throws RemoteException {
        return errors.size();
    }

    /**
     * get the error at that point in the list
     *
     * @param entry
     * @return a copy of the error
     * @throws java.rmi.RemoteException
     * @throws IndexOutOfBoundsException if the entry is out of range
     */
    public synchronized TestInfo getErrorInfo(int entry) throws RemoteException, IndexOutOfBoundsException {
        return ((TestInfo) errors.get(entry)).duplicate();
    }

    /**
     * get the number of starts
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public synchronized int getStartCount() throws RemoteException {
        return starts.size();
    }

    /**
     * get the starts at that point in the list
     *
     * @param entry the list entry beginning at zero
     * @return a copy of the info
     * @throws java.rmi.RemoteException
     * @throws IndexOutOfBoundsException if the entry is out of range
     */
    public synchronized TestInfo getStartInfo(int entry) throws RemoteException, IndexOutOfBoundsException {
        return ((TestInfo)starts.get(entry)).duplicate();
    }

    /**
     * get the number of end
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public synchronized int getEndCount() throws RemoteException {
        return ends.size();
    }

    /**
     * get the end at that point in the list
     *
     * @param entry the list entry beginning at zero
     * @return a copy of the info
     * @throws java.rmi.RemoteException
     * @throws IndexOutOfBoundsException if the entry is out of range
     */
    public TestInfo getEndInfo(int entry) throws RemoteException, IndexOutOfBoundsException {
        return ((TestInfo) ends.get(entry)).duplicate();
    }

    /**
     * get the number of failures
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public int getFailureCount() throws RemoteException {
        return failures.size();
    }

    /**
     * get the failures at that point in the list
     *
     * @param entry the list entry beginning at zero
     * @return a copy of the info
     * @throws java.rmi.RemoteException
     * @throws IndexOutOfBoundsException if the entry is out of range
     */
    public TestInfo getFailureInfo(int entry) throws RemoteException, IndexOutOfBoundsException {
        return ((TestInfo) failures.get(entry)).duplicate();
    }

    /**
     * returns true iff all tests passed
     *
     * @return test success flag
     * @throws java.rmi.RemoteException
     */
    public boolean testsWereSuccessful() throws RemoteException {
        return getFailureCount()==0 && getErrorCount()==0;
    }


}
