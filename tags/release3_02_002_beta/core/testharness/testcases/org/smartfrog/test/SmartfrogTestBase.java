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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Vector;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

import org.smartfrog.SFSystem;
import org.smartfrog.SFParse;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;

/**
 * A base class for smartfrog tests
 * @author steve loughran
 * created 17-Feb-2004 17:08:35
 */

public abstract class SmartfrogTestBase extends TestCase {
    /**
     * cached directory of classes
     */
    protected File classesDir;
    protected String hostname;

    /**
     * Construct the base class, extract hostname and test classes directory from the JVM
     * paramaters -but do not complain if they are missing
     * @param name
     */
    public SmartfrogTestBase(String name) {
        super(name);
        hostname = TestHelper.getTestProperty(TestHelper.HOSTNAME,"localhost");
        String classesdirname = TestHelper.getTestProperty(TestHelper.CLASSESDIR,null);
        if(classesdirname!=null) {
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
    protected void deployExpectingException(String testURL,
                                            String appName,
                                            String exceptionName,
                                            String searchString) throws RemoteException,
            SmartFrogException, SFGeneralSecurityException,
            UnknownHostException {
        deployExpectingException(testURL,
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
     * Ignored if the containedExceptionClass parametere is null.
     * @throws RemoteException in the event of remote trouble.
     */
    protected void deployExpectingException(String testURL,
                                            String appName,
                                            String exceptionName,
                                            String searchString,
                                            String containedExceptionName,
                                            String containedExceptionText) throws SmartFrogException,
            RemoteException, UnknownHostException, SFGeneralSecurityException {
        startSmartFrog();
        ConfigurationDescriptor cfgDesc =
             new ConfigurationDescriptor(appName
                                       , testURL,
                 ConfigurationDescriptor.Action.DEPLOY
                                       , hostname
                                       , null);
        Object deployedApp = null;
        try {
            //Deploy and don't throw exception. Exception will be contained
            // in a ConfigurationDescriptor.
            deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,false);
            if ((deployedApp instanceof ConfigurationDescriptor) &&
                (((ConfigurationDescriptor)deployedApp).resultException!=null)){
                Throwable thr = ((ConfigurationDescriptor)deployedApp).resultException;
                assertContains(cfgDesc.statusString(), searchString);
                if (containedExceptionName!=null) {
                    Throwable cause = thr.getCause();
                    assertNotNull("expected throwable of type "
                                   +containedExceptionName,
                                   cause);
                    //verify the name
//                    assertThrowableNamed(cause,
//                                         containedExceptionName,
//                                         cfgDesc.statusString());
                    assertContains(cause.toString(),
                                         containedExceptionName,
                                         cfgDesc.statusString());

                    if (containedExceptionText!=null) {
                        assertContains(cause.toString(),
                                       containedExceptionText,
                                       cfgDesc.statusString());
                    }
                }

            } else {
                fail("We expected an exception here:"+exceptionName
                     +" but got this result "+deployedApp.toString());
            }
         } catch (Exception fault) {
            fail(fault.toString());
         }
    }

    /**
     * assert that a throwable's classname is of a given type/substring
     * @param thrown
     * @param name
     */
    public void assertThrowableNamed(Throwable thrown,String name, String cfgDescMsg) {
        assertContains(thrown.getClass().getName(),name, cfgDescMsg);
        //assertContains(thrown.toString(),name, cfgDescMsg);
    }

    /**
     * assert that a string contains a substring
     * @param source
     * @param substring
     * @param cfgDescMsg
     */
    public void assertContains(String source, String substring, String cfgDescMsg) {
        assertNotNull("No string to look for ["+substring+"]",source);
        assertTrue("Did not find ["+substring+"] in ["+source+"]"+"\n, Result:"+cfgDescMsg,
                source.indexOf(substring)>=0);
    }


    /**
     * assert that a string contains a substring
     * @param source
     * @param substring
     */
    public void assertContains(String source, String substring) {
       assertContains(source,substring,"");
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

    public void startSmartFrog() throws SmartFrogException, RemoteException,
            SFGeneralSecurityException, UnknownHostException {
        SFSystem.runSmartFrog();
    }
    /**
     * Deploys an application and returns the refence to deployed application.
     * @param testURL  URL to test
     * @param appName  Application name
     * @return Reference to deployed application
     * @throws RemoteException in the event of remote trouble.
     */
    protected Prim deployExpectingSuccess(String testURL, String appName)
                                                    throws Exception,Throwable {
        ConfigurationDescriptor cfgDesc =
                new ConfigurationDescriptor(appName,
                                            testURL,
                    ConfigurationDescriptor.Action.DEPLOY,
                                            hostname,
                                            null);
        try {
            startSmartFrog();
            Object deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,true);

            if (deployedApp instanceof Prim) {
                return ((Prim) deployedApp);
            } else if (deployedApp instanceof ConfigurationDescriptor) {
                Throwable exception = ((ConfigurationDescriptor)deployedApp).
                        resultException;
                if (exception!=null); {
                    throw exception;
                }
            }
        } catch (Throwable throwable) {
            logChainedException(throwable);
            throw throwable;
        }
        fail("something odd came back");
        //fail throws a fault; this is here to keep the compiler happy.
        return null;
    }

    /**
     * a Java1.4 log
     */
    private Logger log=Logger.getLogger(this.getClass().getName());

    /**
     * log a chained exception if there is one; do nothing if not.
     * There because JUnit 3.8.1 is not aware of chaining (yet), presumably
     * through a need to work with pre1.4 stuff
     * @param throwable
     */
    protected void logChainedException(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if(cause!=null) {
            log.log(Level.SEVERE,"nested fault in "+throwable,cause);
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


}
