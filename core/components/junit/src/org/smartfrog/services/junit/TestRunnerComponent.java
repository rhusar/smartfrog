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
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.logging.Log;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * This is the test runner.
 * It runs multiple test suites;
 * It keeps all its public state in a configuration object that can be got/cloned and serialized to suites
 * created 15-Apr-2004 15:44:41
 */

public class TestRunnerComponent extends CompoundImpl implements TestRunner, Runnable
 {

    private Log log;
    private ComponentHelper helper;
    /**
     * a cached exception that is thrown on a liveness failure
     */
    private Throwable cachedException = null;
    /**
     * flag set when the tests are finished
     */
    private boolean finished=false;

    /**
     * should we fail messily if a test failed
     */
    private boolean failOnError=true;

    private int threadPriority=Thread.NORM_PRIORITY;

    /**
     * run tests on startup
     */

    /**
     * thread to run the tests
     */
    private Thread worker=null;

    /**
     * keeper of statistics
     */
    private Statistics statistics=new Statistics();

    /**
     * who listens to the tests? This is potentially remote
     */
    private RunnerConfiguration configuration = new RunnerConfiguration();

    public static final String ERROR_TESTS_IN_PROGRESS = "Component is already running tests";

    /**
     * constructor
     *
     * @throws RemoteException
     */
    public TestRunnerComponent() throws RemoteException {
        helper = new ComponentHelper(this);
    }

    private synchronized Thread getWorker() {
        return worker;
    }

    private synchronized void setWorker(Thread worker) {
        this.worker = worker;
    }

    /**
     * validate our settings, bail out if they are invalid
     *
     * @throws SmartFrogInitException
     */
    private void validate() throws SmartFrogInitException {
        if(threadPriority<Thread.MIN_PRIORITY || threadPriority>Thread.MAX_PRIORITY) {
            throw new SmartFrogInitException(ATTR_THREAD_PRIORITY+" is out of range -must be within "
                    +Thread.MIN_PRIORITY+" and " +Thread.MAX_PRIORITY);
        }
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure deploying compound or
     *                                  sub-component
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failed to start compound
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        //this will deploy all our children, including the test suites
        super.sfStart();
        Object o = sfResolve(ATTR_LISTENER, configuration.getListenerFactory(), true);
        if (!(o instanceof TestListenerFactory)) {
            throw new SmartFrogException("The attribute " + ATTR_LISTENER
                    + "must refer to an implementation of TestListenerFactory");
        }
        configuration.setListenerFactory((TestListenerFactory) o);
        configuration.setKeepGoing(sfResolve(ATTR_KEEPGOING, configuration.getKeepGoing(), false));
        failOnError=sfResolve(ATTR_FAILONERROR,failOnError,false);
        threadPriority=sfResolve(ATTR_THREAD_PRIORITY,threadPriority,false);
        validate();
        //execute the tests in all the suites attached to this class
        boolean runTests= sfResolve(ATTR_RUN_TESTS_ON_STARTUP, true, true);
        if(runTests) {
            startTests();
        } else {
            log.info("Tests will only start when directly invoked");
        }
    }

    /**
     * Liveness tests first delegates to the parent,
     * then considers itself live unless all of the following conditions are met
     * <ol>
     * <li>We are finished
     * <li>There was an exception
     * <li>failOnError is set
     * </ol>
     * In which case the cached exception gets thrown.
     *
     * @param source source of ping
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *          liveness failed
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        //check the substuff
        super.sfPing(source);
        //then look to see if we had a failure with our tests
        if (failOnError && isFinished() && getCachedException() != null) {
            SmartFrogLivenessException.forward(getCachedException());
        }
    }


    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        Thread thread = getWorker();
        if(thread!=null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * run the test
     *
     * @throws java.rmi.RemoteException
     */
    public synchronized boolean startTests() throws RemoteException, SmartFrogException {
        if(getWorker()!=null) {
            throw new SmartFrogException(ERROR_TESTS_IN_PROGRESS);
        }
        Thread thread = new Thread(this);
        thread.setName("tester");
        thread.setPriority(threadPriority);
        log.info("Starting new tester at priority "+threadPriority);
        setWorker(thread);
        thread.start();
        return true;
    }

    /**
     * this is the thread entry point; runs the tests in a new thread.
     *
     * @see Thread#run()
     */
    public void run() {
        setFinished(false);
        log.info("Beginning tests");
        try {
            if (!executeTests()) {
                throw new SmartFrogException("Tests Failed");
            }
        } catch (RemoteException e) {
            catchException(e);
        } catch (SmartFrogException e) {
            catchException(cachedException);
        } finally {
            log.info("Completed tests");
            //declare ourselves finished
            setFinished(true);
            //unset the worker field
            setWorker(null);
        }
    }



    /**
     * run all the tests; this is the routine run in the worker thread.
     * Break out (between suites) if we are interrupted.
     * Sets the {@link TestResultAttributes#ATTR_FINISHED} attribute
     * to true on completion.
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public boolean executeTests() throws SmartFrogException, RemoteException {

        try {
            boolean successful=true;
            Enumeration e=sfChildren();
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
                if(o instanceof TestSuite) {
                    TestSuite suiteComponent=(TestSuite) o;
                    suiteComponent.bind(getConfiguration());
                    successful &=suiteComponent.runTests();
                    updateResultAttributes((Prim)suiteComponent);
                    //break out if the thread is interrupted
                    if(Thread.currentThread().isInterrupted()) {
                        return false;
                    }
                }
            }
            return successful;
        } finally {
            //this is here as it can throw an exception
            sfReplaceAttribute(ATTR_FINISHED, Boolean.TRUE);
        }
    }


    /**
     * fetch the test results from the Test suite, then update our own values
     * @param testSuite
     */
    private void updateResultAttributes(Prim testSuite) throws SmartFrogRuntimeException, RemoteException {
        statistics.retrieveAndAdd(testSuite);
        statistics.updateResultAttributes(this,false);
    }


    public TestListenerFactory getListenerFactory() throws RemoteException {
        return configuration.getListenerFactory();
    }

    public void setListenerFactory(TestListenerFactory listener) {
        configuration.setListenerFactory(listener);
    }


    public boolean getKeepGoing() {
        return configuration.getKeepGoing();
    }

    public void setKeepGoing(boolean keepGoing) {
        configuration.setKeepGoing(keepGoing);
    }

    public RunnerConfiguration getConfiguration() {
        return configuration;
    }


    public synchronized Throwable getCachedException() {
        return cachedException;
    }

    public synchronized void catchException(Throwable cachedException) {
        if(cachedException !=null) {
            ThrowableTraceInfo tti=new ThrowableTraceInfo(cachedException);
            log.info("Caught exception in tests "+tti,cachedException);
        }
        this.cachedException = cachedException;
    }

    public synchronized boolean isFinished() throws RemoteException {
        return finished;
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Get test execution statistics
     *
     * @return stats
     *
     * @throws java.rmi.RemoteException
     */
    public Statistics getStatistics() throws RemoteException {
        return statistics;
    }
}
