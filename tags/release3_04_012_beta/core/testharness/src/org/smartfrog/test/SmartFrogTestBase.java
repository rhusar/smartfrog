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
package org.smartfrog.test;

import junit.framework.TestCase;
import org.smartfrog.SFSystem;
import org.smartfrog.services.assertions.SmartFrogAssertionException;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * A base class for smartfrog tests
 * @author steve loughran
 * created 17-Feb-2004 17:08:35
 */

public abstract class SmartFrogTestBase extends TestCase {
    /**
     * cached directory of classes
     */
    protected File classesDir;
    protected String hostname;
    private static final String LIFECYCLE_EXCEPTION = "SmartFrogLifecycleException";
    private static final String ASSERTION_EXCEPTION = "SmartFrogAssertionException";

    /**
     * Construct the base class, extract hostname and test classes directory from the JVM
     * paramaters -but do not complain if they are missing
     * @param name
     */
    public SmartFrogTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        hostname = TestHelper.getTestProperty(TestHelper.HOSTNAME, "localhost");
        String classesdirname = TestHelper.getTestProperty(TestHelper.CLASSESDIR, null);
        if (classesdirname != null) {
            classesDir = new File(classesdirname);
        }
    }

    /**
     * get a file name relative to the classes dir directory
     * @param filename
     * @return
     */
    protected File getRelativeFile(String filename) {
        if(classesDir!=null) {
            return new File(classesDir,filename);
        } else {
            return new File(filename);
        }
    }

    /**
     * Deploy a component, expecting a smartfrog exception.
     * @param testURL   URL to test
     * @param appName   name of test app
     * @param exceptionName name of the exception thrown
     * @param searchString string which must be found in the exception message
     * @throws RemoteException in the event of remote trouble.
     */
    protected Throwable deployExpectingException(String testURL,
                                            String appName,
                                            String exceptionName,
                                            String searchString) throws RemoteException,
            SmartFrogException, SFGeneralSecurityException,
            UnknownHostException {
        return deployExpectingException(testURL,
                appName,
                exceptionName,
                searchString,
                null,
                null);
    }


    /**
     * Deploy a component, expecting a smartfrog exception. You can
     * also specify the classname of a contained fault -which, if specified,
     * must be contained, and some text to be searched for in this exception.
     * @param testURL   URL to test
     * @param appName   name of test app
     * @param exceptionName name of the exception thrown
     * @param searchString string which must be found in the exception message
     * @param containedExceptionName optional classname of a contained
     * exception; does not have to be the full name; a fraction will suffice.
     * @param containedExceptionText optional text in the contained fault.
     * @throws RemoteException in the event of remote trouble.
     * @returns the exception that was returned
     */
    protected Throwable deployExpectingException(String testURL,
                                            String appName,
                                            String exceptionName,
                                            String searchString,
                                            String containedExceptionName,
                                            String containedExceptionText) throws SmartFrogException,
            RemoteException, UnknownHostException, SFGeneralSecurityException {

        startSmartFrog();
        ConfigurationDescriptor cfgDesc =
                createDeploymentConfigurationDescriptor(appName, testURL);

        Object deployedApp = null;
        Throwable resultException = null;
        try {
            //Deploy and don't throw exception. Exception will be contained
            // in a ConfigurationDescriptor.
            deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,false);
            if ((deployedApp instanceof ConfigurationDescriptor) &&
                    (((ConfigurationDescriptor) deployedApp).resultException != null)) {
                searchForExpectedExceptions(deployedApp, cfgDesc, exceptionName,
                        searchString, containedExceptionName,containedExceptionText);
                resultException = ((ConfigurationDescriptor) deployedApp).resultException;
                return resultException;
            } else {
                //clean up
                if(deployedApp instanceof Prim) {
                    terminateApplication((Prim)deployedApp);
                }
                //then fail
                fail("We expected an exception here:"+exceptionName
                     +" but got this result "+deployedApp.toString());
            }
         } catch (Exception fault) {
            fail(fault.toString());
         }
        return null;
    }

    private void searchForExpectedExceptions(Object deployedApp, ConfigurationDescriptor cfgDesc, String exceptionName, String searchString,
                                             String containedExceptionName,
                                             String containedExceptionText) {
        //we got an exception. let's take a look.
        Throwable returnedFault;
        returnedFault = ((ConfigurationDescriptor) deployedApp).resultException;
        assertFaultCauseAndTextContains(returnedFault, exceptionName, searchString, cfgDesc);
        //get any underlying cause
        Throwable cause = returnedFault.getCause();
        assertFaultCauseAndTextContains(cause, containedExceptionName, containedExceptionText, cfgDesc);
    }

    /**
     * assert that something we deployed contained the name and text we wanted.
     * @param cause root cause. Can be null, if faultName and faultText are also null. It is an error if they are defined
     * and the cause is null
     * @param faultName substring that must be in the classname of the fault
     * @param faultText substring that must be in the text of the fault
     * @param cfgDesc what we were deploying; the status string is extracted for reporting purposes
     */
    private void assertFaultCauseAndTextContains(Throwable cause, String faultName,
                                                 String faultText, ConfigurationDescriptor cfgDesc) {
        String details = cfgDesc.statusString();
        assertFaultCauseAndTextContains(cause, faultName, faultText, details);
    }

    /**
     *
     /**
     * assert that something we deployed contained the name and text we wanted.
     * @param cause root cause. Can be null, if faultName and faultText are also null. It is an error if they are defined
     * and the cause is null
     * @param faultName substring that must be in the classname of the fault
     * @param faultText substring that must be in the text of the fault
     * @param details status string for reporting purposes
     */
    private void assertFaultCauseAndTextContains(Throwable cause, String faultName,
                                                 String faultText, String details) {
        //if we wanted the name of a fault
        if (faultName != null) {
            //then look for the name of contained exception and see it matches what was
            // asked for
            assertNotNull("expected throwable of type "
                    + faultName,
                    cause);
            //verify the name
            assertThrowableNamed(cause,
                    faultName,
                    details);
        }
        //look for the exception text
        if (faultText != null) {
            assertNotNull("expected throwable containing text "
                    + faultText,
                    cause);

            assertContains(cause.toString(),
                    faultText,
                    details,
                    extractDiagnosticsInfo(cause));
        }
    }



    private ConfigurationDescriptor createDeploymentConfigurationDescriptor(String appName, String testURL)
            throws SmartFrogInitException {
        return new ConfigurationDescriptor(appName
                                               , testURL,
                         ConfigurationDescriptor.Action.DEPLOY
                                               , hostname
                                               , null);
    }

    /**
     * assert that a throwable's classname is of a given type/substring
     * @param thrown
     * @param name
     */
    public void assertThrowableNamed(Throwable thrown,String name, String cfgDescMsg) {
        assertContains(thrown.getClass().getName(),name, cfgDescMsg, extractDiagnosticsInfo(thrown));
    }

    /**
     * extract as much info as we can from a throwable.
     * @param thrown
     * @return a string describing the throwable; includes a stack trace
     */
    protected String extractDiagnosticsInfo(Throwable thrown) {
        StringBuffer buffer=new StringBuffer();
        thrown.getStackTrace();
        buffer.append("Message:  ");
        buffer.append(thrown.getMessage());
        buffer.append('\n');
        buffer.append("Class:    ");
        buffer.append(thrown.getClass().getName());
        buffer.append('\n');
        buffer.append("Stack:    ");
        final StackTraceElement[] stackTrace = thrown.getStackTrace();
        for(int i=0;i<stackTrace.length;i++) {
            StackTraceElement frame=stackTrace[i];
            buffer.append(frame.toString());
            buffer.append('\n');
        }
        return buffer.toString();
    }

    /**
     * assert that a string contains a substring
     * @param source
     * @param substring
     * @param cfgDescMsg
     * @param extraText any extra text, can be null
     */
    public void assertContains(String source, String substring, String cfgDescMsg,String extraText) {
        assertNotNull("No string to look for ["+substring+"]",source);
        assertNotNull("No substring ", substring);
        final boolean contained = source.indexOf(substring)>=0;
        if(!contained)
        {
            String message = "Did not find ["+substring+"] in ["+source+"]"+"\n, Result:"+cfgDescMsg;
            System.out.println(message);
            if (extraText != null) {
                System.out.println(extraText);
            }
            fail(message);
        }
    }


    /**
     * assert that a string contains a substring
     * @param source
     * @param substring
     */
    public void assertContains(String source, String substring) {
       assertContains(source,substring,"",null);
    }


    public File getClassesDir() {
        return classesDir;
    }

    public void setClassesDir(File classesDir) {
        this.classesDir = classesDir;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * start smartfrog if it isnt already
     *
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws SFGeneralSecurityException
     * @throws UnknownHostException
     */
    public void startSmartFrog() throws SmartFrogException, RemoteException,
            SFGeneralSecurityException, UnknownHostException {
        if(!SFSystem.isSmartfrogInit()) {
            new SFSystem().runSmartFrog();
        }
    }

    /**
     * Deploys an application and returns the refence to deployed application.
     * @param testURL  URL to test
     * @param appName  Application name
     * @return Reference to deployed application
     * @throws RemoteException in the event of remote trouble.
     */
    protected Prim deployExpectingSuccess(String testURL, String appName)
                                                    throws Throwable {

        try {
            Object deployedApp = deployApplication(appName, testURL);

            if (deployedApp instanceof Prim) {
                return ((Prim) deployedApp);
            } else if (deployedApp instanceof ConfigurationDescriptor) {
                System.out.println("\n    "
                            +"* ERROR IN: Test success in description: \n" +
                             "        "
                            +((ConfigurationDescriptor)deployedApp).toString("\n        ")
                               );
                Throwable exception = ((ConfigurationDescriptor)deployedApp).
                        resultException;
                if (exception!=null) {
                    throw exception;
                }
            }
        } catch (Throwable throwable) {
            logThrowable("thrown during deployment",throwable);
            throw throwable;
        }
        fail("something odd came back");
        //fail throws a fault; this is here to keep the compiler happy.
        return null;
    }


    /**
     * internal helper to test stuff
     * @param appName
     * @param testURL
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws SFGeneralSecurityException
     * @throws UnknownHostException
     */
    private Object deployApplication(String appName, String testURL) throws SmartFrogException, RemoteException,
            SFGeneralSecurityException, UnknownHostException {
        startSmartFrog();
        ConfigurationDescriptor cfgDesc =
                new ConfigurationDescriptor(appName,
                        testURL,
                        ConfigurationDescriptor.Action.DEPLOY,
                        hostname,
                        null);
        System.out.println("\n"+"* Test success in description: \n    "+cfgDesc.toString("\n    ")
                               );

        Object deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,true);
        return deployedApp;
    }

    /**
     * log a chained exception if there is one; do nothing if not.
     * There because JUnit 3.8.1 is not aware of chaining (yet), presumably
     * through a need to work with pre1.4 stuff
     * @param throwable
     */
    protected void logChainedException(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if(cause!=null) {
            logThrowable("nested fault in "+throwable,cause);
        }
    }

    /**
     * log a throwable
     *
     * @param thrown
     */
    public void logThrowable(String message, Throwable thrown) {
        String info = extractDiagnosticsInfo(thrown);
        System.err.println(message);
        System.err.println(info);
        Throwable nested = thrown.getCause();
        if (nested != null) {
            logThrowable("nested fault:" , nested);
        }
    }

    /**
     * parse a file.
     * @param filename the name of a file, relative to the classes.dir passed in
     * to the test JVM.
     * @throws SmartFrogException
     */
    protected Phases parseLocalFile(String filename) throws SmartFrogException {
        File file=getRelativeFile(filename);
        return parse(file);
    }

    /**
     * parse a smartfrog file; throw an exception if something went wrong
     * @param file
     * @throws SmartFrogException
     */
    protected Phases parse(File file) throws SmartFrogException {
        String fileUrl;
        try {
            fileUrl = file.toURL().toString();
        } catch (MalformedURLException e) {
            String msg = MessageUtil.
                    formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND,
                            file.toString());
            throw new SmartFrogParseException(msg);
        }


        Phases phases=null;
        InputStream is=null;
        try {
            is = SFClassLoader.getResourceAsStream(fileUrl);
            if (is == null) {
                String msg = MessageUtil.
                        formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND, fileUrl);
                throw new SmartFrogParseException(msg);
            }
            phases = (new SFParser("sf")).sfParse(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException swallowed) {

                }
            }
        }
        return phases;

    }

     /**
     * parse a smartfrog file; throw an exception if something went wrong
     * @param fileUrl
     * @throws SmartFrogException
     */
    protected Phases parse(String fileUrl) throws SmartFrogException {
        Phases phases=null;
        InputStream is=null;
        try {
            is = SFClassLoader.getResourceAsStream(fileUrl);
            if (is == null) {
                String msg = MessageUtil.
                        formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND, fileUrl);
                throw new SmartFrogParseException(msg);
            }
            phases = (new SFParser("sf")).sfParse(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException swallowed) {

                }
            }
        }
        return phases;
    }

    /**
     * deploy something from this directory; expect an exception
     * @param filename
     * @param appname
     * @throws Throwable
     */
    public Throwable deployExpectingAssertionFailure(String filename, String appname) throws Throwable {
        startSmartFrog();
        ConfigurationDescriptor cfgDesc =
                createDeploymentConfigurationDescriptor(appname, filename);
        Object deployedApp = null;
        Throwable resultException = null;
        try {
            //Deploy and don't throw exception. Exception will be contained
            // in a ConfigurationDescriptor.
            deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,false);
            if ((deployedApp instanceof ConfigurationDescriptor) &&
                    (((ConfigurationDescriptor) deployedApp).resultException != null)) {
                searchForExpectedExceptions(deployedApp, cfgDesc, LIFECYCLE_EXCEPTION,
                        null, ASSERTION_EXCEPTION,null);
                resultException = ((ConfigurationDescriptor) deployedApp).resultException;
                return resultException;
            } else {
                //here we deploy the application
                Prim application=(Prim)deployedApp;
                try {
                    application.sfPing(null);
                    application.sfPing(null);
                    application.sfPing(null);
                    application.sfPing(null);
                    application.sfPing(null);
                } catch (SmartFrogLivenessException liveness) {
                    assertFaultCauseAndTextContains(liveness,LIFECYCLE_EXCEPTION,null,"expected lifecycle failure");
                    assertFaultCauseAndTextContains(liveness.getCause(), ASSERTION_EXCEPTION, null,
                            "expected nested assertion failure");
                }
            }

         } catch (Exception fault) {
            fail(fault.toString());
         }
        return null;
    }

    /**
     * recursive search for the root cause
     *
     * @param throwable
     * @return the assertion or null
     */
    public SmartFrogAssertionException extractAssertionException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        if (throwable instanceof SmartFrogAssertionException) {
            return (SmartFrogAssertionException) throwable;
        }
        return extractAssertionException(throwable.getCause());
    }

    /**
     * terminate a named application
     * @param application
     * @throws java.rmi.RemoteException on network trouble
     */
    public void terminateApplication(Prim application) throws RemoteException {
        if(application==null) {
            return ;
        }
        Reference name;
        try {
            name = application.sfCompleteName();
        } catch (RemoteException e) {
            name = null;

        }
        application.sfDetachAndTerminate(TerminationRecord.normal(name));
    }

    /**
     * get an  attribute from an application
     *
     * @param application
     * @param attribute
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public Object resolveAttribute(Prim application, String attribute)
            throws SmartFrogResolutionException, RemoteException {
        return application.sfResolve(attribute, true);
    }

    /**
     * get a string attribute from an application
     * @param application
     * @param attribute
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public String resolveStringAttribute(Prim application,String attribute)
            throws SmartFrogResolutionException, RemoteException {
        String value=(String)application.sfResolve(attribute,true);
        return value;
    }

    /**
     * assert that an attribute exists
     *
     * @param app
     * @param attribute
     * @throws Exception
     */
    public void assertAttributeExists(Prim app, String attribute)
            throws Exception {
        resolveAttribute(app, attribute);
    }

    /**
     * assert that an attribute exists
     * @param app
     * @param attribute
     * @throws Exception
     */
    public void assertStringAttributeExists(Prim app,String attribute) throws Exception {
        resolveStringAttribute(app,attribute);
    }

    /**
     * assert that an attribute exists and equals a specified value
     * @param app
     * @param attribute
     * @param mustEqual
     * @throws Exception
     */
    public void assertAttributeEquals(Prim app, String attribute,String mustEqual)
            throws Exception {
        String value=resolveStringAttribute(app, attribute);
        assertEquals(mustEqual,value);
    }

    /**
     * assert that an attribute exists and equals a specified value
     *
     * @param app
     * @param attribute
     * @param mustEqual
     * @throws Exception
     */
    public void assertAttributeEquals(Prim app, String attribute,
            Object mustEqual)
            throws Exception {
        Object value = resolveAttribute(app, attribute);
        assertEquals(mustEqual, value);
    }

    public static void assertEquals(String o1, String o2) {
      System.out.println("       - AssertEquals: \n"+
                         "          > "+ o1+"\n"+
                         "          > "+ o2+"\n");
      TestCase.assertEquals(o1,o2);
    }
    public static void assertEquals(Object o1, Object o2) {
      System.out.println("       - AssertEquals(Obj): \n"+
                         "          > "+ o1.toString()+"\n"+
                         "          > "+ o2.toString()+"\n");
      TestCase.assertEquals(o1,o2);
    }
}
