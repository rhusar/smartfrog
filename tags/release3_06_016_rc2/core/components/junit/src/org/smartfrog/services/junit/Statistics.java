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

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.io.Serializable;

/**
 * cache statistics for operations. thread safe add operations, and
 * the code to set the stats on a Prim
 * created Jul 14, 2004 4:21:51 PM
 */

public final class Statistics implements Serializable {

    private int errors = 0;
    private int failures = 0;
    private int testsStarted = 0;
    private int testsRun = 0;

    /**
     * reset everything to zero
     */
    public void reset() {
        errors=failures=testsStarted=testsRun=0;
    }

    /**
     * write all our state to the results order it so that we set the finished
     * last
     */
    public synchronized void updateResultAttributes(Prim node,boolean finished)
            throws SmartFrogRuntimeException, RemoteException {
        node.sfReplaceAttribute(TestResultAttributes.ATTR_ERRORS, new Integer(errors));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_FAILURES, new Integer(failures));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_TESTS, new Integer(testsRun));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_SUCCESSFUL,
                Boolean.valueOf(isSuccessful()));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_FINISHED, Boolean.valueOf(finished));
    }

    /**
     * extract test info from a Prim class
     *
     * @param node
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *
     * @throws RemoteException
     */
    public void retrieveResultAttributes(Prim node)
            throws SmartFrogResolutionException, RemoteException {
        errors = node.sfResolve(TestResultAttributes.ATTR_ERRORS, 0, false);
        failures = node.sfResolve(TestResultAttributes.ATTR_FAILURES, 0, false);
        testsRun = node.sfResolve(TestResultAttributes.ATTR_TESTS, 0, false);
        testsStarted =0;
    }

    /**
     * add one set of statistics to another
     * @param that
     */
    public synchronized void add(Statistics that) {
        addErrors(that.getErrors());
        addFailures(that.getFailures());
        addTestsRun(that.getTestsRun());
        addTestsStarted(that.getTestsStarted());
    }

    /**
     * extract test info from a prim class and add it to our state
     * @param node
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public void retrieveAndAdd(Prim node) throws SmartFrogResolutionException,
            RemoteException {
        Statistics that=new Statistics();
        that.retrieveResultAttributes(node);
        add(that);
    }

    public int getErrors() {
        return errors;
    }


    public int getFailures() {
        return failures;
    }

    public int getTestsStarted() {
        return testsStarted;
    }

    public int getTestsRun() {
        return testsRun;
    }

    public synchronized void addErrors(int count) {
        errors+=count;
    }

    public void incErrors() {
        addErrors(1);
    }

    public synchronized void addFailures(int count) {
        failures+=count;
    }

    public void incFailures() {
        addFailures(1);
    }

    public synchronized void addTestsStarted(int count) {
        testsStarted+=count;
    }

    public void incTestsStarted() {
        addTestsStarted(1);
    }
    public synchronized void addTestsRun(int count) {
        testsRun+=count;
    }

    public void incTestsRun() {
        addTestsRun(1);
    }

    /**
     * test for success, which means no errors or failures
     * @return
     */
    public synchronized boolean isSuccessful() {
        return errors==0 && failures==0;
    }
}
