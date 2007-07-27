/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.sfunit;

import org.smartfrog.services.xunit.base.RunnerConfiguration;
import org.smartfrog.services.xunit.base.AbstractTestSuite;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

/** created 08-Jan-2007 14:57:40 */

public class SFUnitTestSuiteImpl extends AbstractTestSuite
        implements SFUnitTestSuite {
    private volatile boolean finished = false;
    private volatile boolean failed = false;
    private volatile boolean succeeded = false;
    private volatile boolean forcedTimeout = false;
    private volatile boolean skipped = false;
    private volatile TerminationRecord status;
    private RunnerConfiguration configuration;
    private ComponentHelper helper;
    private List<TestBlock> testChildren;
    private Iterator<TestBlock> testChildrenIterator;
    private Prim activeTest;

    public SFUnitTestSuiteImpl() throws RemoteException {
    }

    /**
     * Return true iff the component is finished. Spin on this, with a (delay) between calls
     *
     * @return true if we have finished
     */
    public boolean isFinished() {
        return finished;
    }

    /** @return true only if the test has finished and failed */
    public boolean isFailed() {
        return failed;
    }

    /** @return true iff the test succeeded */

    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * {@inheritDoc}
     *
     * @return the skipped state
     */
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     */


    public TerminationRecord getStatus() {
        return status;
    }

    /**
     * return the tests prim
     *
     * @return null, always
     */
    public Prim getAction() {
        return null;
    }

    /**
     * Stub implementation
     *
     * @return null always
     */
    public TerminationRecord getActionTerminationRecord()  {
        return null;
    }

    /**
     * Stub implementation
     *
     * @return null always
     */
    public TerminationRecord getTestsTerminationRecord() throws RemoteException {
        return null;
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed components in the compound context. Any
     * failure will cause the compound to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException    In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        helper = new ComponentHelper(this);

        //deploy all children. but do not (yet) start them
        Iterator iterator = getActions().sfAttributes();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            ComponentDescription act = (ComponentDescription) actions.get(key);
            sfDeployComponentDescription(key, this, act, null);
            if (sfLog().isDebugEnabled()) sfLog().debug("Creating " + key);
        }

        //now start anything that is not a test
        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();

            if (elem instanceof Prim && !(elem instanceof TestBlock)) {
                ((Prim) elem).sfStart();
            }
        }

        //so here everything is deployed, ready to run.
    }

    /**
     * Run the tests.
     *
     * This is done by running through every child in turn, and deploying it.
     *
     * When it terminates, it is evaluated. No, that doesnt work. We need notification?
     *
     * @return true if they worked
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for other problems
     */
    public boolean runTests() throws RemoteException, SmartFrogException, InterruptedException {
        InetAddress host = sfDeployedHost();
        String hostname = host.getHostName();
        //use our short name
        String suitename = helper.shortName();
        //then look for an override, which is mandatory if we do not know who
        //we are right now.
        suitename = sfResolve(ATTR_NAME, suitename, suitename == null);
        sfLog().info("Running SFUnit test suite " + suitename + " on host " + hostname);

        if (getConfiguration() == null) {
            throw new SmartFrogException(
                    "TestSuite has not been configured yet");
        }
        if (maybeSkipTestSuite()) {
            skipped = true;
            return true;
        }

        boolean successful = true;

        testChildren = new ArrayList<TestBlock>(sfChildren.size());


        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();

            if (elem instanceof TestBlock) {
                TestBlock testBlock = (TestBlock) elem;
                testChildren.add(testBlock);
            }
        }
        //now create the iterator.
        testChildrenIterator = testChildren.iterator();
        deployNextChild();
        return successful;
    }

    /**
     * Test one testblock.
     *
     * TestC
     * <ol>
     * <li> Subscribing to lifecycle events</li>
     * <li> Starting it.</li>
     * <li> Waiting for it to finish </li>
     * <li> Logging whether it failed or not</li>
     * </ol>
     *
     * @param testBlock component to test
     * @return true if the test worked
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    private synchronized boolean testOneChild(TestBlock testBlock) throws SmartFrogException, RemoteException {
        activeTest = (Prim) testBlock;
        Exception caught;
        try {
            activeTest.sfStart();
            //now we wait for the child to terminate
            //TODO: block, without breaking synchronization
            return true;
        } catch (SmartFrogException e) {
            caught = e;
        } catch (RemoteException e) {
            caught = e;
        }
        sfLog().info(caught);
        return false;
    }


    /**
     * Deploy a child; return false if there were none left
     *
     * @return whether the test was run or not
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException network problems
     */
    private boolean deployNextChild() throws SmartFrogException, RemoteException {
        if (testChildrenIterator.hasNext()) {
            testOneChild(testChildrenIterator.next());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handle child termination. Sequence behaviour for a normal child termination is <ol> <li> to start the next
     * component.</li> <li> if it is the last - terminate normally. </li> <li> If starting the next component raised an
     * error, terminate abnormally</li> </ol> Abnormal child terminations are relayed up.
     *
     * @param record exit record of the component
     * @param comp   child component that is terminating
     * @return true whenever a child component is not started
     */
    protected boolean onChildTerminated(TerminationRecord record, Prim comp) {
        if (activeTest == comp) {
            //cast it
            TestBlock test = (TestBlock) activeTest;
            //get the results
            boolean success=false;
            boolean skipped=false;
            boolean failed=false;
            Exception caught=null;
            try {
                success = test.isSucceeded();
                skipped = test.isSkipped();
                failed = test.isFailed();
            } catch (RemoteException e) {
                caught = e;
            } catch (SmartFrogException e) {
                caught = e;
            }
            if(caught!=null) {
                sfLog().error("Unreachable or terminated child -assuming failure");
                success=false;
                failed = true;
            }
            succeeded &=success;
            failed |= failed;
            //if we failed, we didnt succeed. Just to make sure :)
            succeeded &=!failed;
            //now, report things
            if(getConfiguration().getKeepGoing()) {
                
            }
            //after the eval, remove it from the graph
            try {
                sfRemoveChild(comp);
                //deploy the next child
                boolean deployed = deployNextChild();
                //signal false if we do not want to terminate
                return !deployed;
            } catch (RemoteException e) {
                caught = e;
            } catch (SmartFrogException e) {
                caught = e;
            }
            //terminate with an error if we caught something
            TerminationRecord newRecord = TerminationRecord.abnormal("Failed to start next test", sfCompleteName, caught);
            sfTerminate(newRecord);
            //and notify the caller we are handling it ourselves
            return false;
        } else {
            //something else terminated
            //whatever it was, it signals the end of this run
            return true;
        }

    }
}
