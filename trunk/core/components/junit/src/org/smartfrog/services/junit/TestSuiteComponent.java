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
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.AssertionFailedError;



/**
 * Implementation of the test suite component.
 * This is where tests are actually run; we bring up Junit internally and run it here
 * created 14-May-2004 15:14:23
 */

public class TestSuiteComponent extends PrimImpl implements TestSuite, junit.framework.TestListener {

    private Logger log;

    private ComponentHelper helper;

    private String testClass;

    private boolean ifValue;

    private boolean unlessValue;

    private boolean subPackages;

    private List excludes;

    RunnerConfiguration configuration;

    private int errors = 0;
    private int failures = 0;
    private int testsRun = 0;



    public TestSuiteComponent() throws RemoteException {
        helper = new ComponentHelper(this);
        log = helper.getLogger();
    }

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
     * bind to the configuration. A null parameter means 'stop binding'
     *
     * @param configuration
     * @throws java.rmi.RemoteException
     */
    public void bind(RunnerConfiguration configuration) throws RemoteException {
        this.configuration = configuration;
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


    /**
     * run the test
     *
     * @return true iff all tests passed
     * @throws java.rmi.RemoteException
     */
    public boolean runTests() throws RemoteException, SmartFrogException {
        if(configuration==null || getListener()==null) {
            throw new SmartFrogException("TestSuite has not been configured yet");
        }

        return false;
    }

    /**
     * test a single class 
     * @param classname
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    void testSingleClass(String classname) throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        Test tests;
        Class clazz=loadTestClass(classname);
        tests=extractTest(clazz);
        TestResult result=new TestResult();
        result.addListener(this);
        tests.run(result);

    }


    /**
     * load the test class, using the secure classloader framework.
     *
     * @param classname
     * @return
     * @throws ClassNotFoundException
     */
    private Class loadTestClass(String classname) throws ClassNotFoundException {
        //Class.forName(classname);
        Class clazz=SFClassLoader.forName(classname);
        return clazz;
    }

    /**
     * get the tests from the class, either as a suite or as introspected tests
     * @param clazz
     * @return the test
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Test extractTest(Class clazz) throws IllegalAccessException, InvocationTargetException {
        //todo: verify that the class implements test or testsuite
        try {
            // check if there is a suite method
            Method method = clazz.getMethod("suite", new Class[0]);
            return (Test) method.invoke(null, new Class[0]);
        } catch (NoSuchMethodException e) {
            //if not, assume that it is a testclass and do it that way
            return new junit.framework.TestSuite(clazz);
        }
    }

    /**
     * get our listener
     * @return
     */
    TestListener getListener() {
        return configuration.getListener();
    }

    /**
     * An error occurred.
     */
    public void addError(Test test, Throwable throwable) {
        errors++;
        TestInfo info=new TestInfo(test,throwable);
        try {
            getListener().addError(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * ignore a remote fault by logging it
     * @param e
     */
    private void IgnoreRemoteFault(RemoteException e) {
        log.log(Level.WARNING,"",e);
    }

    /**
     * A failure occurred.
     */
    public void addFailure(Test test, AssertionFailedError error) {
        failures++;
        TestInfo info = new TestInfo(test, error);
        try {
            getListener().addFailure(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * A test ended.
     */
    public void endTest(Test test) {
        testsRun++;
        TestInfo info = new TestInfo(test);
        try {
            getListener().endTest(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        }
    }

    /**
     * A test started.
     */
    public void startTest(Test test) {
        TestInfo info = new TestInfo(test);
        try {
            getListener().startTest(info);
        } catch (RemoteException e) {
            IgnoreRemoteFault(e);
        }
    }


}
