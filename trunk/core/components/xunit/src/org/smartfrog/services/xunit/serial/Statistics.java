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
package org.smartfrog.services.xunit.serial;

import org.smartfrog.services.xunit.base.TestResultAttributes;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * cache statistics for operations. thread safe add operations, and
 * the code to set the stats on a Prim
 * created Jul 14, 2004 4:21:51 PM
 */

public final class Statistics implements Serializable, Cloneable {

    private int errors = 0;
    private int failures = 0;
    private int testsStarted = 0;
    private int testsRun = 0;
    private int loggedMessages = 0;


    /**
     * Hash table by outcome
     */
    private HashMap<String,Integer> outcomes=new HashMap<String, Integer>();


    /**
     * reset everything to zero
     */
    public void reset() {
        errors = failures = testsStarted = testsRun = loggedMessages = 0;
    }

    /**
     * write all our state to the results order it so that we set the finished
     * attribute last
     * @param node note to add to
     * @param finished flag if the run is finished
     * @throws SmartFrogRuntimeException any failure to set an attribute
     * @throws RemoteException networkin problems
     */
    public synchronized void updateResultAttributes(Prim node, boolean finished)
            throws SmartFrogRuntimeException, RemoteException {
        node.sfReplaceAttribute(TestResultAttributes.ATTR_ERRORS, new Integer(errors));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_FAILURES, new Integer(failures));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_TESTS, new Integer(testsRun));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_TESTS_STARTED, new Integer(testsStarted));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_LOGGED_MESSAGES,
                new Integer(loggedMessages));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_SUCCESSFUL,
                Boolean.valueOf(isSuccessful()));
        node.sfReplaceAttribute(TestResultAttributes.ATTR_FINISHED, Boolean.valueOf(finished));
    }

    /**
     * extract test info from a Prim class
     *
     * @param node node to work on
     * @return true if the prim thinks it has finished.
     * @throws SmartFrogResolutionException failure to resolve something
     * @throws RemoteException networking problems
     */
    public boolean retrieveResultAttributes(Prim node)
            throws SmartFrogResolutionException, RemoteException {
        errors = node.sfResolve(TestResultAttributes.ATTR_ERRORS, 0, false);
        failures = node.sfResolve(TestResultAttributes.ATTR_FAILURES, 0, false);
        testsRun = node.sfResolve(TestResultAttributes.ATTR_TESTS, 0, false);
        testsStarted = node.sfResolve(TestResultAttributes.ATTR_TESTS_STARTED, 0, false);
        loggedMessages = node.sfResolve(TestResultAttributes.ATTR_LOGGED_MESSAGES,
                0,
                false);
        boolean finished=
                node.sfResolve(TestResultAttributes.ATTR_FINISHED,
                        false,
                        false);
        return finished;
    }

    /**
     * add one set of statistics to another
     *
     * @param that the other statistics source
     */
    public synchronized void add(Statistics that) {
        addErrors(that.getErrors());
        addFailures(that.getFailures());
        addTestsRun(that.getTestsRun());
        addTestsStarted(that.getTestsStarted());
        addLoggedMessages(that.getLoggedMessages());
        //add all the outcomes
        for(String outcome:that.outcomes.keySet()) {
            increment(outcome,that.getOutcome(outcome));
        }
    }

    /**
     * extract test info from a prim class and add it to our state
     *
     * @param node node to work on
     * @throws SmartFrogResolutionException failure to resolve something
     * @throws RemoteException networking problems
     */
    public void retrieveAndAdd(Prim node) throws SmartFrogResolutionException,
            RemoteException {
        Statistics that = new Statistics();
        that.retrieveResultAttributes(node);
        add(that);
    }

    public synchronized void increment(String outcome,int count) {
        int value = getOutcome(outcome);
        value += count;
        outcomes.put(outcome, new Integer(value));
    }

    public void increment(String outcome) {
        increment(outcome, 1);
    }

    /**
     * Get the count for a specific outcome
     * @param outcome
     * @return the count or 0 for no events of that outcome found.
     */
    public int getOutcome(String outcome) {
        Integer current = outcomes.get(outcome);
        int value = 0;
        if (current != null) {
            value = current.intValue();
        }
        return value;
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
        errors += count;
    }

    public void incErrors() {
        addErrors(1);
    }

    public synchronized void addFailures(int count) {
        failures += count;
    }

    public void incFailures() {
        addFailures(1);
    }

    public synchronized void addTestsStarted(int count) {
        testsStarted += count;
    }

    public void incTestsStarted() {
        addTestsStarted(1);
    }

    public synchronized void addTestsRun(int count) {
        testsRun += count;
    }

    public void incTestsRun() {
        addTestsRun(1);
    }

    public synchronized void addLoggedMessages(int count) {
        loggedMessages += count;
    }

    public void incLoggedMessages() {
        addLoggedMessages(1);
    }

    public int getLoggedMessages() {
        return loggedMessages;
    }
    /**
     * get number of tests that did not succeed, the sum
     * of failures+errors. Synchronised.
     *
     * @return the number of unsuccessful tests
     */
    public synchronized int getUnsuccessfulTests() {
        return getFailures() + getErrors();
    }

    /**
     * test for success, which means no errors or failures
     *
     * @return true if the test was successful
     */
    public boolean isSuccessful() {
        return getUnsuccessfulTests() == 0;
    }

    /**
     * check that the statistics of this instance match that of another one.
     * We ignore logged messages as they can get a bit corrupted by irrelevant events.
     * Same for the number of testsStarted, which are vulnerable to race conditions
     * @param other the other statistics
     * @return true if all counts match
     */
    public boolean isEqual(Statistics other) {
        return failures == other.failures
                && testsRun == other.testsRun
                && errors == other.errors;
    }


    public String toString() {
        String s = "Statistics: testsRun=" + testsRun
                + " errors=" + errors
                + " failures=" + failures
                + " loggedMessages=" + loggedMessages
                + " testsStarted="+testsStarted;
        return s;
    }

    /**
     * {@inheritDoc}
     *
     */
    public Object clone() {
        try {
            Statistics that = (Statistics)super.clone();
            //there shouldn't be any need to deep clone the hash map, because
            //the keys and values are both immutable. When an outcome is updated,
            //a new value is assigned.
            return that;
        } catch (CloneNotSupportedException e) {
            //not possible except by a subclass, and, being final,
            //that is not possible.
            throw new RuntimeException(e);
        }
    }
}
