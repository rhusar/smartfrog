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
package org.smartfrog.services.junit.junit3;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.smartfrog.services.testcontext.TestContextInjector;
import org.smartfrog.services.xunit.base.AbstractTestSuite;
import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.log.TestListenerLog;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.utils.Utils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Implementation of the Junit3.8.x test suite component. This is where tests are actually run; we bring up Junit
 * internally and run it here.
 */

public class JUnit3TestSuiteImpl extends AbstractTestSuite implements JUnitTestSuite,
        junit.framework.TestListener {

    /** List of classes to test */
    private List<String> classes;

    /** Is the if= set? */
    private boolean ifValue = true;

    /** is the unless= test clear? */
    private boolean unlessValue = false;

    /** The package to prefix all classnames with */
    private String packageValue;

    /** the name of the suite */
    private String suitename;

    /** test class list we build up */
    private Map<String, String> testClasses;

    /** track the active tests; used to build up full statistics on what a test does. */
    private Map<String, Long> startedTests;

    /** listener for tests; set at binding time */
    private TestListener listener;

    /** System properties */
    private Properties sysproperties;

    /** Single test to run */
    private String singleTest;

    /** Error if sysproperties are uneven. {@value} */
    public static final String ERROR_UNEVEN_PROPERTIES = "There is an unbalanced number of properties in "
            + ATTR_SYSPROPS;
    /** {@value } */
    private static final String SUITE_METHOD_NAME = "suite";
    /** {@value } */
    public static final String WARN_IGNORING_REMOTE_FAULT = "Ignoring remote fault";

    public JUnit3TestSuiteImpl() throws RemoteException {

    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        readConfiguration();
    }


    /**
     * read in our configuration
     *
     * @throws SmartFrogException on trouble
     * @throws RemoteException    on trouble
     */
    @SuppressWarnings("unchecked")
    protected void readConfiguration() throws SmartFrogException,
            RemoteException {
        ifValue = sfResolve(ATTR_IF, ifValue, false);
        unlessValue = sfResolve(ATTR_UNLESS, unlessValue, false);

        List nestedClasses;
        nestedClasses = (List) sfResolve(ATTR_CLASSES,
                (List) null,
                false);
        classes = ListUtils.flattenStringList(nestedClasses,
                ATTR_CLASSES);

        singleTest = sfResolve(ATTR_SINGLE_TEST, singleTest, false);

        //properties. extract the list, flatten it and bind to sysproperties
        sysproperties = ListUtils.resolveProperties(this, new Reference(ATTR_SYSPROPS), true);

        //now pull in the propertySet
        ComponentDescription propSet = null;
        propSet = sfResolve(ATTR_PROPERTY_SET, propSet, true);
        CDtoProperties(sysproperties, propSet);

        //package attribute names a package
        packageValue = sfResolve(ATTR_PACKAGE, packageValue, false);
        if (packageValue == null) {
            packageValue = "";
        } else {
            //add a dot at the end if we need it
            if (packageValue.length() > 0 &&
                    packageValue.charAt(packageValue.length() - 1) != '.') {
                packageValue = packageValue + '.';
            }
        }
        buildClassList();
        //use our short name
        suitename = getHelper().shortName();
        //then look for an override, which is mandatory if we do not know who
        //we are right now.
        suitename = sfResolve(ATTR_NAME, suitename, suitename == null);
        log("Running JUnit3 test suite " + suitename + " on host " + getHostname());
    }


    public static Properties CDtoProperties(ComponentDescription cd) throws SmartFrogResolutionException {
        Properties props = new Properties();
        CDtoProperties(props, cd);
        return props;
    }

    private static void CDtoProperties(Properties props, ComponentDescription cd) throws SmartFrogResolutionException {
        Iterator iterator = cd.sfAttributes();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = cd.sfResolveHere(key);
            props.put(key.toString(), value.toString());
        }
    }


    /**
     * build the list of classes to run. At this point the list is already flat. If the user has asked for a single
     * test, so {@link #singleTest} is not null, then only tests that end with that pattern are tested.
     */
    protected void buildClassList() {
        testClasses = new HashMap<String, String>();
        for (String testclass : classes) {
            addTest(testclass);
        }
    }

    /**
     * add a test to the list , prepending the package If {@link #singleTest} is not null, only tests whose classnameor
     * fullname match will be allowed.
     *
     * @param classname test class
     */
    private void addTest(String classname) {
        String fullname = getFullClassname(classname);
        boolean add;
        //should we add this?
        add = singleTest == null
                || singleTest.length() == 0
                || singleTest.equals(classname)
                || singleTest.equals(fullname);

        if (add && testClasses.get(fullname) == null) {
            log("adding test " + fullname);
            testClasses.put(fullname, fullname);
        }
    }

    /**
     * Get the full classname of a class
     *
     * @param classname the name of the class
     * @return the full classname with package prepended
     */
    private String getFullClassname(String classname) {
        return packageValue + classname;
    }


    /**
     * Evaluate the condition by delegating to the underlying condition, and pull in the if/unless values.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    @Override
    public synchronized boolean evaluate() throws RemoteException, SmartFrogException {
        return super.evaluate() && ifValue && !unlessValue;
    }

    /**
     * run the test bail out if we were interrupted.
     *
     * @return true iff all tests passed
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    @Override
    public boolean runTests() throws RemoteException, SmartFrogException {


        log("Running junit3 test suite " + suitename);
        checkConfigured();
        //bind to our listener
        TestListenerLog testLog = null;
        listener = listen(suitename);
        try {
            if (maybeSkipTestSuite()) {
                //exit early
                sfLog().info("Skipping test suite " + suitename);
                listener.endTest(TestInfo.skipped(suitename));
                return true;
            }

            //set up the log
            testLog = getConfiguration().getTestLog();
            if (testLog != null) {
                testLog.addLogListener(listener);
            }

            //copy any system properties over
            Utils.applySysProperties(sysproperties);

            // set up our context
            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put(TestContextInjector.ATTR_PRIM, this);
            context.put(TestContextInjector.ATTR_LISTENER, listener);
            context.put(TestContextInjector.ATTR_PROPERTIES, sysproperties);
            //reset the logs
            startedTests = new HashMap<String, Long>();
            //now run all the tests

            boolean failed = false;
            for (String classname : testClasses.keySet()) {
                try {
                    testSingleClass(classname, context);
                } catch (Exception e) {
                    //something got thrown here
                    throw SmartFrogException.forward(e);
                }
                if (Thread.currentThread().isInterrupted()) {
                    sfLog().error("Interrupted test thread");
                    failed = true;
                }
                updateResultAttributes(false);
                failed |= !getStats().isSuccessful();
                if (failed && !getConfiguration().getKeepGoing()) {
                    return false;
                }
                updateResultAttributes(true);
            }
            return !failed;
        } finally {
            stopListening(testLog);
        }

    }

    /**
     * as we exit, we mark the listener as null and end the suite.
     *
     * @param testLog log to unlisten
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    private void stopListening(TestListenerLog testLog) throws SmartFrogException, RemoteException {

        TestListener oldlistener = getListener();
        if (oldlistener != null) {
            //mark the listener as null first, so that any failure to end the
            //suite doesnt affect our deref
            listener = null;
            startedTests = null;
            //unsubscribe
            if (testLog != null) {
                testLog.removeLogListener(oldlistener);
            }

            //end the suite.
            oldlistener.endSuite();
        }
    }

    /**
     * check we have been configured before running the tests
     *
     * @throws SmartFrogException if we are not configured in this thread
     */
    protected void checkConfigured() throws SmartFrogException {
        if (getConfiguration() == null) {
            throw new SmartFrogException(
                    "TestSuite has not been configured yet");
        }
    }

    /**
     * test a single class
     *
     * @param classname test class to load and run
     * @param context   name-object mapping context
     * @return true if the tests were set up
     */
    private boolean testSingleClass(String classname, HashMap<String, Object> context) {
        log("testing " + classname);
        Test tests;
        TestResult result;
        result = new TestResult();
        result.addListener(this);
        boolean testsSetUp;
        try {
            Class clazz = loadTestClass(classname);
            tests = extractTest(clazz);
            injectTestContext(tests, context);
            testsSetUp = true;
        } catch (Throwable e) {
            //couldn't set up the tests, so we catch the exception and create a failure
            //test that reports the outcome
            tests = new Warning(classname, e);
            testsSetUp = false;
        }
        //run the tests
        tests.run(result);
        return testsSetUp;
    }


    /**
     * load the test class, using the secure classloader framework.
     *
     * @param classname class
     * @return the loaded class
     * @throws SmartFrogResolutionException on a load failure
     * @throws RemoteException              on network trouble
     */
    private Class loadTestClass(String classname)
            throws SmartFrogResolutionException, RemoteException {
        return getHelper().loadClass(classname);
    }

    /**
     * get the tests from the class, either as a suite or as introspected tests. There is no verification here that a
     * class is a test suite!
     *
     * @param clazz class with the test
     * @return the test
     * @throws SmartFrogInitException if the test suite setup failed
     */
    private Test extractTest(Class clazz) throws SmartFrogInitException {
        //todo: verify that the class implements test or testsuite
        try {
            // check if there is a suite method
            Method method = clazz.getMethod(SUITE_METHOD_NAME);
            return (Test) method.invoke(null);
        } catch (NoSuchMethodException e) {
            //if not, assume that it is a testclass and do it that way
            return new TestSuite(clazz);
        } catch (IllegalAccessException e) {
            throw new SmartFrogInitException("No access to the method " + SUITE_METHOD_NAME
                    + " in class " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new SmartFrogInitException("Exception in " + SUITE_METHOD_NAME
                    + " in class " + clazz
                    + " : " + e.toString(),
                    e.getCause());
        }
    }


    /**
     * inject the test context into every test that expects it
     *
     * @param tests   test suite
     * @param context context to inject
     */
    private void injectTestContext(TestSuite tests, HashMap<String, Object> context) {
        Enumeration<?> t = tests.tests();
        while (t.hasMoreElements()) {
            Test test = (Test) t.nextElement();
            injectTestContext(test, context);
        }
    }

    /**
     * inject test context into a test
     *
     * @param test    test to run
     * @param context to inject if the test is injectable
     */
    private void injectTestContext(Test test, HashMap<String, Object> context) {
        if (test instanceof TestContextInjector) {
            TestContextInjector tci = (TestContextInjector) test;
            tci.setTestContext((HashMap<String, Object>) context.clone());
        } else if (test instanceof TestSuite) {
            injectTestContext((TestSuite) test, context);
        }
    }


    /**
     * get our listener
     *
     * @return the listener
     */
    public TestListener getListener() {
        return listener;
    }


    /**
     * Implement {@link junit.framework.TestListener#addError(Test, Throwable)}
     * @param test test that errored
     * @param throwable what went wrong
     */
    @Override
    public void addError(Test test, Throwable throwable) {
        getStats().incErrors();
        TestInfo info = onEnd(test, throwable);
        info.setOutcome(TestInfo.OUTCOME_ERROR);
        try {
            getListener().addError(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        } catch (SmartFrogException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * ignore a remote fault by logging it
     *
     * @param e exception
     */
    private void IgnoreRemoteFault(Exception e) {
        sfLog().warn(WARN_IGNORING_REMOTE_FAULT, e);
    }

    /**
     * A failure occurred.
     * Implement JUnit's {@link junit.framework.TestListener#addFailure(Test, AssertionFailedError)}
     * @param test  test
     * @param error error
     */
    @Override
    public void addFailure(Test test, AssertionFailedError error) {
        getStats().incFailures();
        TestInfo info = onEnd(test, error);
        info.setOutcome(TestInfo.OUTCOME_FAILURE);
        try {
            getListener().addFailure(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        } catch (SmartFrogException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * A test ended.
     * Implement JUnit's {@link junit.framework.TestListener#endTest(Test)}
     * @param test test
     */
    @Override
    public void endTest(Test test) {
        getStats().incTestsRun();
        TestInfo info = onEnd(test, null);
        info.setOutcome(TestInfo.OUTCOME_SUCCESS);
        try {
            getListener().endTest(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        } catch (SmartFrogException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * A message from {@link junit.framework.TestListener#startTest(Test)} that a test started.
     * Note that if a test throws an exception in {@link TestCase#setUp()} then this callback is
     * <i>not</i> invoked by Junit. We need to be aware of this fact.
     *
     * @param test test
     */
    @Override
    public void startTest(Test test) {
        TestInfo info = onStart(test);
        try {
            getListener().startTest(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        } catch (SmartFrogException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * note that a test has started by adding the current time to the {@link #startedTests} hashtable if it is not
     * already there, and incrementing the started count in the statistics. If it is there, do nothing.
     *
     * @param test test
     * @return test informatation
     */
    public TestInfo onStart(Test test) {
        TestInfo info = createTestInfo(test);
        String testname = info.getText();
        long start = registerStartTime(testname);
        info.setStartTime(start);
        return info;
    }


    /**
     * file the start time;  if there already is one, then take that one instead. Will increment the started count in
     * the statistics if needed.
     *
     * @param testname name of the test
     * @return timestamp of test start.
     */
    private synchronized long registerStartTime(String testname) {
        if (!startedTests.containsKey(testname)) {
            long start = System.currentTimeMillis();
            startedTests.put(testname, start);
            getStats().incTestsStarted();
            return start;
        } else {
            return lookupStartTime(testname);
        }
    }

    /**
     * look up the start time
     *
     * @param testname test name
     * @return start time
     * @throws RuntimeException if there is no key of that name
     */
    private long lookupStartTime(String testname) {
        return startedTests.get(testname);
    }

    /**
     * call this when a test ends to set up the start and end times right.
     *
     * @param test  the test
     * @param fault optional fault detail
     * @return the test information including start and end time.
     */
    public synchronized TestInfo onEnd(Test test, Throwable fault) {
        long endTime = System.currentTimeMillis();
        TestInfo testInfo = createTestInfo(test, fault);
        //force a start entry in there
        long startTime = registerStartTime(testInfo.getText());
        testInfo.setStartTime(startTime);
        testInfo.setEndTime(endTime);
        return testInfo;
    }

    /**
     * Create a test info report from a test
     * @param test test that ran
     * @return a test information struct
     */
    public static TestInfo createTestInfo(Test test) {
        return createTestInfo(test, null);
    }


    /**
     * Create a test info report from a test
     * @param test test that ran
     * @param fault fault information -can be null
     * @return a test information struct
     */
    public static TestInfo createTestInfo(Test test, Throwable fault) {
        TestInfo testInfo = new TestInfo(fault);
        String testname = test.getClass().getName();
        String text;
        if (test instanceof TestCase) {
            //TestCase information is extracted specially
            TestCase testCase = (TestCase) test;
            text = testCase.getName();
            testname = text;
        } else {
            //any other kind of test has no name, just
            //a string value
            text = test.toString();
        }
        testInfo.setName(testname);
        testInfo.setText(text);
        return testInfo;
    }

}
