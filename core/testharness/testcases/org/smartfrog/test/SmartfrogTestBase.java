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
import java.rmi.RemoteException;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.prim.Prim;

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
                                            String searchString) throws RemoteException {
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
                        String containedExceptionText) throws RemoteException {
        try {
            SFSystem.deployAComponent(hostname,
                    testURL, appName,
                    false);
        } catch (SmartFrogException fault) {
            String message = fault.getMessage();
            assertContains(message,searchString);
            if(containedExceptionName!=null) {
                Throwable cause=fault.getCause();
                assertNotNull("expected throwable of type "
                        +containedExceptionName,
                        cause);
                //verify the name
                assertThrowableNamed(cause,containedExceptionName);
                //verify the contained text
                if(containedExceptionText!=null) {
                    String m2 = cause.getMessage();
                    assertContains(m2,containedExceptionText);
                }
            }

        }
    }

    /**
     * assert that a throwable's classname is of a given type/substring
     * @param thrown
     * @param name
     */
    public void assertThrowableNamed(Throwable thrown,String name) {
        assertContains(thrown.getClass().getName(),name);
    }

    /**
     * assert that a string contains a substring
     * @param source
     * @param substring
     */
    public void assertContains(String source, String substring) {
        assertNotNull("No string to look for ["+substring+"]",source);
        assertTrue("Did not find ["+substring+"] in ["+source+"]",
                source.indexOf(substring)>=0);
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
     * Deploys an application and returns the refence to deployed application.
     * TODO: deployAComponent should return reference to deployed application
     * @param testURL  URL to test
     * @param appName  Application name
     * @return Reference to deployed application
     * @throws RemoteException in the event of remote trouble. 
     */
    protected Prim deployExpectingSuccess(String testURL, String appName) 
                                                    throws RemoteException {
        Prim app = null;                                                    
        try {
            SFSystem.deployAComponent(hostname, testURL, appName, false);
            app = getLocalApplicationReference(appName);
        }catch (SmartFrogException sfEx) {
            // should never throw exception
            fail("Unable to deploy component, Error:"+ sfEx.getMessage());
        }
        return app;
    }
    
    /**
     * Gets reference to application deployed in local process compound.
     * @param appName Application name deployed in process compound
     * @return Reference to deployed application
     * @throws SmartFrogException in the event of any trouble
     * @throws RemoteException in the event of remote trouble. 
     */
    private Prim getLocalApplicationReference (String appName) 
                                    throws SmartFrogException, RemoteException{
        // get reference to process compound
        ProcessCompound pc = SFProcess.getProcessCompound();
        // get reference to application
        Prim comp = (Prim) pc.sfResolveHere(appName);
        return comp;
    } 
}
