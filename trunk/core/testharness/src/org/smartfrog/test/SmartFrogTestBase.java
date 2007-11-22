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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;

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
    private static final Log log= LogFactory.getLog(SmartFrogTestBase.class);

    /**
     * Smartfrog assertion.
     * Value: {@value}
     */
    private static final String EXCEPTION_SMARTFROG_ASSERTION = "org.smartfrog.services.assertions.SmartFrogAssertionException";

    /**
     * Text to look for in classname when seeking a resolution exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_RESOLUTION = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogResolutionException";

    /**
     * Text to look for in classname when seeking a type resolution exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_TYPERESOLUTION = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogTypeResolutionException";

    /**
     * Text to look for in classname when seeking a place resolution exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_PLACERESOLUTION = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogPlaceResolutionException";

    /**
     * Text to look for in classname when seeking a link resolution exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_LINKRESOLUTION = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogLinkResolutionException";

    /**
     * Text to look for in classname when seeking a function  resolution exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_FUNCTIONRESOLUTION = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogFunctionResolutionException";

    /**
     * Text to look for in classname when seeking a assertion resolution exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_ASSERTIONRESOLUTION = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogAssertionResolutionException";


    /**
     * Text to look for in classname when seeking a lifecycle exception.
     * Value: {@value}
     */
    public static final String EXCEPTION_LIFECYCLE = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogLifecycleException";
    /**
     * Text to look for in classname when seeking a SmartFrogException.
     * Value: {@value}
     */
    public static final String EXCEPTION_SMARTFROG = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogException";
    /**
     * Text to look for in classname when seeking a liveness exception.
     * Value: {@value}
     */

    public static final String EXCEPTION_LIVENESS = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogLivenessException";

    /**
     * Text to look for in classname when seeking a SmartFrogDeploymentException.
     * Value: {@value}
     */

    public static final String EXCEPTION_DEPLOYMENT = "org.smartfrog.sfcore.common.SmartFrog.SmartFrogDeploymentException";

    /**
     * Text to look for in classname when seeking a ClassCastException.
     * Value: {@value}
     */
    public static final String EXCEPTION_CLASSCAST = "java.lang.ClassCastException";

    /**
     * Text to look for in classname when seeking a ClassCastException.
     * Value: {@value}
     */
    public static final String EXCEPTION_CLASSNOTFOUND = "java.lang.ClassNotFoundException";


    /**
     * Text to look for in classname when seeking a SmartFrogParseException.
     * Value: {@value}
     */
    public static final String EXCEPTION_PARSE = "org.smartfrog.sfcore.common.SmartFrogParseException";

    /**
     * Text to look for in classname when seeking a
     * SmartFrogCompileResolutionException.
     * Value: {@value}
     */
    public static final String EXCEPTION_COMPILE_RESOLUTION = "SmartFrogCompileResolutionException";

    /**
     * Text to look for in classname when seeking a
     * SmartFrogLazyResolutionException.
     * Value: {@value}
     */
    public static final String EXCEPTION_SMARTFROG_LAZY_RESOLUTION_EXCEPTION = "org.smartfrog.sfcore.common.SmartFrogLazyResolutionException";

    /**
     * This is an application that will be undeployed at teardown time
     */
    protected Prim application;
    protected static final String ERROR_UNRESOLVED_REFERENCE_LINK_RESOLUTION
            = "Unresolved Reference during phase link resolution";
    protected static final String ERROR_UNRESOLVED_REFERENCE_TYPE_RESOLUTION
            = "Unresolved Reference during phase type resolution";

    /**
     * Construct the base class, extract hostname and test classes directory from the JVM
     * parameters -but do not complain if they are missing
     * @param name test case name
     */
    protected SmartFrogTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture,by extracting the hostname and classes dir 
     */
    protected void setUp() throws Exception {
        //super.setUp();

        hostname = TestHelper.getTestProperty(TestHelper.HOSTNAME, "localhost");
        String classesdirname = TestHelper.getTestProperty(TestHelper.CLASSESDIR, null);
        if (classesdirname != null) {
            classesDir = new File(classesdirname);
        }
    }

    /**
     * Teardown tears down the application in {@link #application} if it is not null
     *
     * @throws Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        terminateApplication(application);
    }


    /**
     * Get the log of this test class
     * @return the log (which will never be null)
     */
    protected static Log getLog() {
        return log;
    }


    /**
     * Get the current application or null
     * @return the current application
     */
    public Prim getApplication() {
        return application;
    }

    /**
     * set the application
     * @param application new application
     */
    public void setApplication(Prim application) {
        this.application = application;
    }

    /**
     * get a file name relative to the classes dir directory
     * @param filename short name of the file
     * @return the resolved file
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
     * @return the exception that was raised
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong
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
     * @param exceptionName name of the exception thrown (can be null)
     * @param searchString string which must be found in the exception message (can be null)
     * @param containedExceptionName optional classname of a contained
     * exception; does not have to be the full name; a fraction will suffice.
     * @param containedExceptionText optional text in the contained fault.
     * @return the exception that was returned
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong
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
            ConfigurationDescriptor deployedCD;
            if ((deployedApp instanceof ConfigurationDescriptor) &&
                    (deployedCD=(ConfigurationDescriptor) deployedApp).getResultException() != null) {
                searchForExpectedExceptions(deployedCD, cfgDesc, exceptionName,
                        searchString, containedExceptionName,containedExceptionText);
                resultException = ((ConfigurationDescriptor) deployedApp).getResultException();
                return resultException;
            } else {
                //clean up
                if(deployedApp instanceof Prim) {
                    terminateApplication((Prim)deployedApp);
                }
                //then fail
                fail("We expected an exception here:"
                        +(exceptionName!=null?exceptionName:"(anonymous)")
                     +" but got this deployment "+deployedApp.toString());
            }
         } catch (Exception fault) {
            fail(fault.toString());
         }
        return null;
    }

    /**
     * A deployment failed, and a CD containing exceptions was returned instead of an application.
     * This method scans through looking for the expected exceptions and
     * failing if the type or messages do not match
     * @param deployedApp what was deployed
     * @param cfgDesc the original configuration descriptor.
     * @param exceptionName optional substring to find in the outermost exception
     * @param searchString optional text to find in the outermost exception
     * @param containedExceptionName optional substring to find in any nested exception
     * @param containedExceptionText optional text to find in any nested  exception
     */
    private void searchForExpectedExceptions(ConfigurationDescriptor deployedApp, ConfigurationDescriptor cfgDesc,
                                             String exceptionName, String searchString,
                                             String containedExceptionName,
                                             String containedExceptionText) {
        //we got an exception. let's take a look.
        Throwable returnedFault;
        returnedFault =  deployedApp.getResultException();
        assertFaultCauseAndCDContains(returnedFault, exceptionName, searchString, cfgDesc);
        //get any underlying cause
        Throwable cause = returnedFault.getCause();
        assertFaultCauseAndCDContains(cause, containedExceptionName, containedExceptionText, cfgDesc);
    }

    /**
     * assert that something we deployed contained the name and text we wanted.
     * @param cause root cause. Can be null, if faultName and faultText are also null. It is an error if they are defined
     * and the cause is null
     * @param faultName substring that must be in the classname of the fault (can be null)
     * @param faultText substring that must be in the text of the fault (can be null)
     * @param cfgDesc what we were deploying; the status string is extracted for reporting purposes
     */
    private void assertFaultCauseAndCDContains(Throwable cause, String faultName,
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
     * @param faultName substring that must be in the classname of the fault (can be null)
     * @param faultText substring that must be in the text of the fault  (can be null)
     * @param details status string for reporting purposes
     */
    protected static void assertFaultCauseAndTextContains(Throwable cause, String faultName,
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
     * @param thrown what was thrown
     * @param name the name of the class
     * @param cfgDescMsg description (can be null)
     */
    public static void assertThrowableNamed(Throwable thrown,String name, String cfgDescMsg) {
        assertContains(thrown.getClass().getName(),
                name, cfgDescMsg, thrown);
    }

    /**
     * Do a recursive dump of what is going wrong
     * @param thrown exception to dump
     * @return a dumped exception
     */
    private String recursiveDump(Throwable thrown) {
        StringBuffer dump=new StringBuffer();
        String info = extractDiagnosticsInfo(thrown);
        dump.append(info);
        Throwable nested = thrown.getCause();
        if (nested != null && nested!=thrown) {
            dump.append(recursiveDump(nested));
        }
        return dump.toString();
    }


    /**
    * extract as much info as we can from a throwable.
    * @param thrown what was thrown
    * @return a string describing the throwable; includes a stack trace
    */
    private static String extractDiagnosticsInfo(Throwable thrown) {
        StringBuffer buffer=new StringBuffer();
        thrown.getStackTrace();
        buffer.append("Message:  ");
        buffer.append(thrown.getMessage());
        buffer.append('\n');
        buffer.append("Class:    ");
        buffer.append(thrown.getClass().getName());
        buffer.append('\n');
        buffer.append("Stack:    ");
        StackTraceElement[] stackTrace = thrown.getStackTrace();
        for (StackTraceElement frame : stackTrace) {
            buffer.append(frame.toString());
            buffer.append('\n');
        }
        return buffer.toString();
    }

    /**
     * assert that a string contains a substring
     * @param source source to scan
     * @param substring string to look for
     * @param cfgDescMsg configuration description
     * @param extraText any extra text, can be null
     */
    public static void assertContains(String source, String substring, String cfgDescMsg,String extraText) {
        assertNotNull("No string to look for ["+substring+"]",source);
        assertNotNull("No substring ", substring);
        final boolean contained = source.contains(substring);

        if(!contained) {
            String message = "- Did not find \n["+substring+"]\nin \n["+source+"]"+
                (cfgDescMsg!=null?("\n, Result:\n"+cfgDescMsg):"");
            log.error(message+ extraText != null?("\n"+extraText):"");
            fail(message);
        }
    }

    /**
     * assert that a string contains a substring
     *
     * @param source     source to scan
     * @param substring  string to look for
     * @param cfgDescMsg configuration description
     * @param exception  any exception to look at can be null
     */
    public static void assertContains(String source, String substring, String cfgDescMsg, Throwable exception) {
        assertNotNull("No string to look for [" + substring + "]", source);
        assertNotNull("No substring ", substring);
        final boolean contained = source.contains(substring);

        if (!contained) {
            String message = "- Did not find \n[" + substring + "] \nin \n[" + source + "]" +
                    (cfgDescMsg != null ? ("\n, Result:\n" + cfgDescMsg) : "");
            if(exception!=null) {
                log.error(message , exception);
            } else {
                log.error(message);
            }
            fail(message);
        }
    }

    /**
     * assert that a string contains a substring
     * @param source source to scan
     * @param substring string to look for
     */
    public static void assertContains(String source, String substring) {
       assertContains(source,substring,"",(String)null);
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
     * start smartfrog if it isn't already live
     *
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong

     */
    public void startSmartFrog() throws SmartFrogException, RemoteException, SFGeneralSecurityException, UnknownHostException {
        if(!SFSystem.isSmartfrogInit()) {
            SFSystem.runSmartFrog();
        }
    }

    /**
     * Shut down smartfrog on the local host
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong
     */
    public void terminateSmartFrog() throws SmartFrogException, RemoteException, SFGeneralSecurityException, UnknownHostException {
       SFSystem.runConfigurationDescriptor(new ConfigurationDescriptor(":TERMINATE:::localhost:"));
    }

    /**
     * Deploys an application and returns the reference to deployed application.
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
            } else lookForThrowableInDeployment(deployedApp);
        } catch (Throwable throwable) {
            logThrowable("thrown during deployment",throwable);
            throw throwable;
        }
        fail("something odd came back");
        //fail throws a fault; this is here to keep the compiler happy.
        return null;
    }

    /**
     * Look through what we got back from deployment; if it is a CD containing
     * an exception then it is checked for an exception, which is then thrown if not null
     * @param deployedApp the application
     * @throws Throwable any exception raised during deployment
     */
    protected void lookForThrowableInDeployment(Object deployedApp) throws Throwable {
        if (deployedApp instanceof ConfigurationDescriptor) {
            ConfigurationDescriptor cd = (ConfigurationDescriptor) deployedApp;
            log.error("* ERROR IN: Test success in description: \n      "
                        + cd.toString("\n        "));
            if (cd.getResultException() !=null) {
                throw cd.getResultException();
            }
        }
    }


    /**
     * internal helper to test stuff
     * @param appName application name
     * @param testURL URL of the application
     * @return whatever was deployed
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong

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
        log.info("* Testing for successful deployment of: \n    "
                +cfgDesc.toString("\n    ")
                               );

        Object deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,true);
        return deployedApp;
    }



    /**
     * log a throwable
     * @param message the text to log
     * @param thrown what was thrown
     */
    public void logThrowable(String message, Throwable thrown) {
        log.error(message,thrown);
    }

    /**
     * parse a file whose filename is resolved before parsing parsed.
     * @param filename the name of a file, relative to the classes.dir passed in
     * to the test JVM.
     * @throws SmartFrogException any parse time exception
     * @return a parsed file
     */
    protected Phases parseLocalFile(String filename) throws SmartFrogException {
        File file=getRelativeFile(filename);
        return parse(file);
    }

    /**
     * parse a smartfrog file; throw an exception if something went wrong
     * @param file file to parse
     * @throws SmartFrogException  any parse time exception
     * @return the parsed file
     */
    protected Phases parse(File file) throws SmartFrogException {
        String fileUrl;
        try {
            fileUrl = file.toURI().toURL().toString();
        } catch (MalformedURLException ignore) {
            String msg = MessageUtil.
                    formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND,
                            file.toString());
            throw new SmartFrogParseException(msg);
        }

        return parse(fileUrl);
    }

    /**
     * parse a smartfrog file; throw an exception if something went wrong
     * The language of the parser is determined from the file extension; if there
     * is none, then 'sf' is assumed.
     * @param resource resource to parse
     * @throws SmartFrogException  any parse time exception
     * @return the parsed file
     */
    protected Phases parse(String resource) throws SmartFrogException {
        Phases phases=null;
        InputStream is=null;
        try {
            is = SFClassLoader.getResourceAsStream(resource);
            if (is == null) {
                String msg = MessageUtil.
                        formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND, resource);
                throw new SmartFrogParseException(msg);
            }
            String extension = determineLanguage(resource);
            phases = (new SFParser(extension)).sfParse(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                    //ignore
                }
            }
        }
        return phases;
    }

    /**
     * work out the language of a file by getting the extension
     * @param filename file whose extension will be examined
     * @return the extension; default is .sf
     */
    protected String determineLanguage(String filename) {
        String extension="sf";
        int index=filename.lastIndexOf('.');
        if(index>=0) {
            extension=filename.substring(index+1);
        }
        return extension;
    }

    /**
     * deploy something from this directory; expect an exception
     * @param filename name of the file
     * @param appname application name to use
     * @return the expected failure
     * @throws Throwable if something goes wrong before deployment
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
            ConfigurationDescriptor deployedCD;
            if ((deployedApp instanceof ConfigurationDescriptor) &&
                    ((deployedCD=(ConfigurationDescriptor) deployedApp).getResultException() != null)) {
                searchForExpectedExceptions(deployedCD, cfgDesc, EXCEPTION_LIFECYCLE,
                        null, EXCEPTION_SMARTFROG_ASSERTION,null);
                resultException = ((ConfigurationDescriptor) deployedApp).getResultException();
                return resultException;
            } else {
                //here we deploy the application
                Prim prim =(Prim)deployedApp;
                try {
                    prim.sfPing(null);
                    prim.sfPing(null);
                    prim.sfPing(null);
                    prim.sfPing(null);
                    prim.sfPing(null);
                } catch (SmartFrogLivenessException liveness) {
                    assertFaultCauseAndTextContains(liveness, EXCEPTION_LIFECYCLE,
                            null,"expected lifecycle failure");
                    assertFaultCauseAndTextContains(liveness.getCause(),
                            EXCEPTION_SMARTFROG_ASSERTION, null,
                            "expected nested assertion failure");
                } finally {
                    terminateApplication(prim);
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
     * @param throwable fault to examine
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
     * terminate a named application.
     * If the application parameter is null or refers to a nonexistent node, nothing happens.
     *
     * @param target application; can be null
     * @throws RemoteException on network trouble other than an already terminated app
     */
    public void terminateApplication(Prim target) throws RemoteException {
        if(target ==null) {
            return ;
        }
        Reference name;
        try {
            name = target.sfCompleteName();
        } catch (RemoteException ignore) {
            name = null;
        }
        try {
            target.sfDetachAndTerminate(TerminationRecord.normal(name));
        } catch (NoSuchObjectException ignore) {
            //app is already terminated, do not fail a test.
        }
    }

    public synchronized void terminateApplication() throws RemoteException {
        try {
            terminateApplication(application);
        } finally {
            application=null;
        }
    }

    /**
     * get an  attribute from an application
     *
     * @param target prim to work with
     * @param attribute attribute string to use
     * @return the resolved object
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static Object resolveAttribute(Prim target, String attribute)
            throws SmartFrogResolutionException, RemoteException {
        assertNotNull("Target parameter is null", target);
        assertNotNull("Attribute parameter is null",attribute);
        Object value = target.sfResolve(attribute, false);
        assertNotNull("Expected non-null "+describe(target, attribute),
                value);
        return value;
    }

    /**
     * Create a description of a target/attribute pair
     * @param target prim to work with
     * @param attribute attribute string to use
     * @return some text about the values
     */
    private static String describe(Prim target,String attribute) {
        try {
            return "attribute " + attribute + " on " + target.sfCompleteName();
        } catch (RemoteException ignore) {
            return "attribute '" + attribute + "' on an unresponsive component";
        }
    }

    /**
     * get a string attribute from an application
     * @param prim prim to work with
     * @param attribute attribute string to use
     * @return the attribute as a string
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static String resolveStringAttribute(Prim prim,String attribute)
            throws SmartFrogResolutionException, RemoteException {
        Object value = resolveAttributeWithTypeAssertion(prim,
                attribute,
                String.class);
        return (String) value;
    }

    /**
     * Resolve an attribute and make an assertion about the return type
     * @param prim app to resolve against
     * @param attribute attribute to resolve
     * @param expectedClass class that is required
     * @return the object
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static Object resolveAttributeWithTypeAssertion(Prim prim,
                                                    String attribute,
                                                    Class expectedClass)
            throws SmartFrogResolutionException, RemoteException {
        assertNotNull(expectedClass);
        Object value = resolveAttribute(prim,attribute);
        Class valueClass = value.getClass();
        assertEquals("Expected "+ describe(prim, attribute)
            +" to be of type "+expectedClass+" but instead it is an instance of class "
            +valueClass.toString()+" with value "+value.toString(),
                expectedClass,valueClass);
        return value;
    }

    /**
     * assert that an attribute exists
     *
     * @param prim app to resolve against
     * @param attribute attribute to resolve
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static void assertAttributeExists(Prim prim, String attribute)
            throws SmartFrogResolutionException, RemoteException {
        resolveAttribute(prim,attribute);
    }

    /**
     * assert that an attribute exists
     * @param prim app to resolve against
     * @param attribute attribute to resolve
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static void assertStringAttributeExists(Prim prim,String attribute)
            throws SmartFrogResolutionException, RemoteException {
        assertAttributeExists(prim,attribute);
    }

    /**
     * assert that an attribute exists and equals a specified value
     * @param mustEqual string that it must equal
     * @param prim app to resolve against
     * @param attribute attribute to resolve
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static void assertAttributeEquals(Prim prim, String attribute,String mustEqual)
            throws SmartFrogResolutionException, RemoteException {
        String value=resolveStringAttribute(prim, attribute);
        assertEquals("Unequal "+ describe(prim, attribute),mustEqual,value);
    }

    /**
     * assert that an attribute exists and equals a specified value
     *
     * @param mustEqual object which must equal
     * @param prim app to resolve against
     * @param attribute attribute to resolve
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static void assertAttributeEquals(Prim prim, String attribute,
                                      Object mustEqual) throws SmartFrogResolutionException,
            RemoteException {
        Object value = resolveAttribute(prim, attribute);
        assertEquals("Unequal " + describe(prim, attribute), mustEqual, value);
    }

    /**
     * Assert the value of a boolean attribute
     * @param expected expected value
     * @param prim app to resolve against
     * @param attribute attribute to resolve
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     * @throws AssertionError if a condition is not met
     */
    public static void assertAttributeEquals(Prim prim, String attribute,
                                      boolean expected) throws SmartFrogResolutionException,
            RemoteException {
        Object value = resolveAttributeWithTypeAssertion(prim, attribute,Boolean.class);
        assertEquals("Unequal " + describe(prim, attribute),expected,((Boolean)value).booleanValue());
    }

    /**
     * assert that a property is defined
     * @param property system property
     */
    public static void assertSystemPropertySet(String property) {
        String value=TestHelper.getTestProperty(property,null);
        assertNotNull("Property not set: "+property, value);
    }

    /**
     * assert that an application is alive;
     * @param application target application
     * @throws AssertionError if something failed, wrapping the underlying exception
     */
    protected static void assertLivenessSuccess(Prim application) {
        assertNotNull("Application is null",application);
        try {
            application.sfPing(null);
        } catch (SmartFrogLivenessException e) {
            throw new AssertionError(e);
        } catch (RemoteException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * assert that an application is alive;
     * @param application application
     * @param count number of times to check
     * @throws AssertionError if something failed, wrapping the underlying exception
     */
    protected static void assertLivenessSuccess(Prim application,int count) {
        for(int i=0;i<count;i++) {
            assertLivenessSuccess(application);
        }
    }

    /**
     * Assert that an object is an instance of a specific class.
     * @param instance object to examine
     * @param clazz class. subclasses are OK.
     */
    protected static void assertInstanceOf(Object instance,Class clazz) {
        assertNotNull("Null class argument",clazz);
        assertNotNull("Expected instance of "+clazz+" but got null",instance);
        if(!clazz.isInstance(instance)) {
            fail("Object "+instance+" is not an instance of "+clazz);
        }
    }

    /**
     * Assert that a termination record contains the expected values.
     * If either the throwableClass or throwableText attributes are non-null, then the record
     * must contain a fault
     * @param record termination record
     * @param descriptionText text to look for in the description (optional; can be null)
     * @param throwableClass fragment of the class name/package of the exception. (optional; can be null)
     * @param throwableText text to look for in the fault text. (optional; can be null)
     */
    public static void assertTerminationRecordContains(TerminationRecord record,
                                        String descriptionText,
                                        String throwableClass,
                                        String throwableText) {
        assertNotNull("Null termination record",record);
        if(descriptionText!=null) {
            assertContains(record.description,descriptionText);
        }
        if(throwableClass !=null || throwableText !=null) {
            if(record.getCause()!=null) {
                assertFaultCauseAndTextContains(record.getCause(),
                        throwableClass, throwableText, null);
            } else {
                fail("Expected Termination record "+record+" to contain "
                +" a throwable "+(throwableClass!=null?throwableClass:"")
                + (throwableText!=null?(" with text"+throwableText):""));
            }
        }
    }
}
