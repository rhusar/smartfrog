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
package org.smartfrog.services.xunit.base;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.services.xunit.serial.Statistics;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

/**
 * This is an abstract base class for test suites.
 * Its key feature is that it provides a thread local context variable that can be used to retrieve the current
 * context of a running test.
 *
 * Because it extends ConditionCompound, it has the event workflow lifecycle
 * created 10-Oct-2006 11:39:29
 */

public abstract class AbstractTestSuite extends ConditionCompound implements TestSuite {


    private static ThreadLocal<RunnerConfiguration> configurationContext;

    private static ThreadLocal<Prim> testSuiteContext;
    /**
     * Statistics about this test
     */
    private Statistics stats = new Statistics();
    /**
     * our hostname
     */
    private String hostname;
    /**
     * assistance
     */
    protected ComponentHelper helper;

    /**
     * Error Text
     * </p>
     * {@value}
     */
    public static final String ERROR_OVERWRITING_CONTEXT = "Overwriting an existing thread-local configuration context.\n"
        +"Multiple thread runners may be active in the same thread\n"
        +"or the tests are themselves deploying tests.";
    /**
     * Error Text
     * {@value}
     */
    public static final String ERROR_OVERWRITING_SELF = "The component is overwriting its own configuration";

    protected AbstractTestSuite() throws RemoteException {
    }


    /**
     * {@inheritDoc}
     * @return false
     */
    @Override
    protected boolean isConditionRequired() {
        return false;
    }


    /**
     * Handle a missing condition by returning true
     *
     * @return true
     */
    @Override
    protected boolean onEvaluateNoCondition()  {
        return true;
    }

    /**
     * Registers components referenced in the SendTo sub-component registers itself with components referenced in the
     * RegisterWith sub-component.
     *
     * @throws RemoteException              In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while deploying the component
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        helper = new ComponentHelper(this);
    }

    /**
     * Starts the component by deploying the condition
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        InetAddress host = sfDeployedHost();
        setHostname(host.getHostName());
    }


    /**
     * Deregisters from all current registrations.
     *
     * @param status Termination  Record
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord status) {
        //reset the state in this thread
        getConfigurationContext().set(null);
        resetTestSuiteContext();
        super.sfTerminateWith(status);

    }

    /**
     * bind to the configuration. A null parameter means 'stop binding'
     *
     * @param configuration config to bind to
     * @throws RemoteException on network trouble
     * @throws SmartFrogException for other problems
     */
    public void bind(RunnerConfiguration configuration) throws RemoteException, SmartFrogException {
        boolean overwriting=false;
        boolean overwritingourselves=false;
        synchronized(getConfigurationContext()) {
            RunnerConfiguration current = getConfigurationContext().get();
            if(current !=null && configuration!=null) {
                overwriting=true;
                overwritingourselves= current == configuration;
            }
            getConfigurationContext().set(configuration);
            //set or reset the test suite context
            if(configuration!=null) {
                if (getTestSuiteContext().get() != null) {
                    overwriting = true;
                }

                getTestSuiteContext().set(this);
            } else {
                resetTestSuiteContext();
            }
        }
        if (overwriting) {
            //warn that something got overwritten. It is probably harmless, but can
            //cause interesting behaviour
            sfLog().info(ERROR_OVERWRITING_CONTEXT);
            if(overwritingourselves) {
                sfLog().info(ERROR_OVERWRITING_SELF);
            }
        }
    }

    /**
     * Reset our test suite context
     */
    private void resetTestSuiteContext() {
        getTestSuiteContext().set(null);
    }


    /**
     * Get the thread local configuration.
     * @return the configuration (may be null)
     */
    public static RunnerConfiguration getConfiguration() {
        return getConfigurationContext().get();
    }

    /**
     * Get the thread local test suite
     * @return the configuration (may be null)
     */
    public static Prim getTestSuite() {
        return getTestSuiteContext().get();
    }


    /**
     * Decide whether or not to skip the suite; report it if true
     * @see #reportSkippedTestSuite()
     * @return true if the test is to be skipped
     * @throws RemoteException network trouble
     * @throws SmartFrogException other problems
     */
    protected boolean maybeSkipTestSuite() throws RemoteException, SmartFrogException {
        if (!evaluate()) {
            sfLog().info("Skipping test suite as conditions preclude it");
            reportSkippedTestSuite();
            return true;
        }
        return false;
    }

    /**
     * Report a skipped test
     * @throws RemoteException network trouble
     * @throws SmartFrogException other problems
     */
    protected void reportSkippedTestSuite () throws RemoteException, SmartFrogException {
        //TODO: any reporting
    }

    /**
     * Get the test listener factory from this configuration
     * @return the test listener factory
     */
    protected TestListenerFactory getTestListenerFactory() {
        return getConfiguration().getListenerFactory();
    }

    protected Statistics getStats() {
        return stats;
    }

    protected void setStats(Statistics stats) {
        this.stats = stats;
    }

    protected String getHostname() {
        return hostname;
    }

    protected void setHostname(String hostname) {
        this.hostname = hostname;
    }

    protected ComponentHelper getHelper() {
        return helper;
    }

    /**
     * Create a new listener from the current factory.
     * @param suiteName name of the suite
     * @return a new listener
     * @throws SmartFrogException if needed
     * @throws RemoteException on network trouble
     */
    protected TestListener listen(String suiteName) throws RemoteException, SmartFrogException {
        TestListenerFactory listenerFactory = getTestListenerFactory();

        TestListener newlistener = listenerFactory.listen(this,
                getHostname(),
                sfDeployedProcessName(),
                suiteName,
                System.currentTimeMillis());
        return newlistener;
    }

    /**
     * write all our state to the results order it so that we set the finished
     * last
     * @param finished finished flag
     * @throws SmartFrogRuntimeException if needed
     * @throws RemoteException on network trouble
     */
    protected void updateResultAttributes(boolean finished)
            throws SmartFrogRuntimeException, RemoteException {
        getStats().updateResultAttributes(this, finished);
    }

    /**
     * Get the current configuration; create it if needed
     * @return the configuration context thread local
     */
    private static synchronized ThreadLocal<RunnerConfiguration> getConfigurationContext() {
        if(configurationContext==null) {
            configurationContext=new ThreadLocal<RunnerConfiguration>();
        }
        return configurationContext;
    }

    /**
     * Get the current test suite context; create it if needed
     * @return the test suite context thread local
     */
    private static synchronized ThreadLocal<Prim> getTestSuiteContext() {
        if (testSuiteContext == null) {
            testSuiteContext = new ThreadLocal<Prim>();
        }
        return testSuiteContext;
    }
}
