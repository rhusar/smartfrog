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

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;



/**
 * Implementation of the Junit test suite component.
 * This is where tests are actually run; we bring up Junit internally and run it here
 * created 14-May-2004 15:14:23
 */

public class JUnitTestSuiteImpl extends PrimImpl implements JUnitTestSuite, junit.framework.TestListener {

    private Log log;

    private ComponentHelper helper;

    private List classes;

    private boolean ifValue;

    private boolean unlessValue;


    private String packageValue;

    private List excludesList;

    private HashMap excludesMap;

    private RunnerConfiguration configuration;

    private int errors = 0;
    private int failures = 0;
    private int testsRun = 0;


    /**
     * test class list we build up
     */
    private HashMap testClasses;

    public JUnitTestSuiteImpl() throws RemoteException {
        helper = new ComponentHelper(this);
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
        log = helper.getLogger();
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
        readConfiguration();
        //runTests();
    }

    protected void readConfiguration() throws SmartFrogException, RemoteException {
        ifValue=sfResolve(ATTRIBUTE_IF,ifValue,false);
        unlessValue=sfResolve(ATTRIBUTE_UNLESS,unlessValue,false);
        classes = flattenStringList((List)sfResolve(ATTR_CLASSES,classes,false),ATTR_CLASSES);

        //package attribute names a package
        packageValue = sfResolve(ATTR_PACKAGE,packageValue,false);
        if(packageValue==null) {
            packageValue="";
        } else {
            //add a dot at the end if we need it
            if(packageValue.length()>0 && packageValue.charAt(packageValue.length()-1)!='.') {
                packageValue+='.';
            }
        }
        buildClassList();
    }

    /**
     * build the list of classes to run
     */
    protected void buildClassList() {
        testClasses = new HashMap();
        Iterator it=classes.iterator();
        while (it.hasNext()) {
            String testclass = (String) it.next();
            addTest(testclass);
        }
    }

    /**
     * add a test to the list , prepending the package
     * @param classname
     */
    private void addTest(String classname) {
        String fullname=packageValue+classname;

        if(testClasses.get(fullname)==null) {
            log.debug("adding test "+ fullname);
            testClasses.put(fullname, fullname);
        }
    }




    /**
     * flatten a string list, validating type as we go. recurses as much as we need to.
     * At its most efficient if no flattening is needed.
     * @param src
     * @param name
     * @return
     * @throws SmartFrogInitException
     */
    private List flattenStringList(final List src,String name) throws SmartFrogInitException {
        List dest=new ArrayList(src.size());
        Iterator index = src.iterator();
        while (index.hasNext()) {
            Object element = (Object) index.next();
            if (element instanceof List) {
                dest.addAll(flattenStringList((List)element,name));
            } else if (!(element instanceof String)) {
                throw new SmartFrogInitException("An element in "
                        + name + " is not string: " + element.toString());
            }
            dest.add(element);
        }
        return dest;
    }

    /**
     * run the test
     * bail out if we were interrupted.
     * @return true iff all tests passed
     * @throws java.rmi.RemoteException
     */
    public boolean runTests() throws RemoteException, SmartFrogException {
        if(configuration==null || getListener()==null) {
            throw new SmartFrogException("TestSuite has not been configured yet");
        }

        if(!getIf() || getUnless()) {
            log.debug("Skipping test as conditions preclude it");
            return true;
        }

        boolean failed=false;
        Iterator it= testClasses.keySet().iterator();
        while (it.hasNext()) {
            String classname = (String) it.next();
            try {
                testSingleClass(classname);
            } catch (ClassNotFoundException e) {
                throw SmartFrogException.forward(e);
            } catch (IllegalAccessException e) {
                throw SmartFrogException.forward(e);
            } catch (InvocationTargetException e) {
                throw SmartFrogException.forward(e);
            }
            if(Thread.currentThread().isInterrupted()) {
                log.debug("Interrupted test thread");
                return false;
            }
            failed = failures > 0 || errors > 0;
            if(failed && !configuration.getKeepGoing()) {
                return false;
            }
        }
        return !failed;
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
        log.debug("testing "+classname);
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
        log.warn("ignoring",e);
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
