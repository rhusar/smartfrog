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

/**
 * A base class for smartfrog tests
 * @author steve loughran
 * created 17-Feb-2004 17:08:35
 */

public class SmartfrogTestBase extends TestCase {
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
     * Deploy a component, expecting a smartfrog exception
     * @param testURL   URL to test
     * @param appName   name of test app
     * @param searchString string which must be found in the exception message
     * @throws RemoteException in the event of remote trouble.
     */
    protected void deployExpectingExceptionContaining(String testURL, String appName, String searchString) throws RemoteException {
        try {
            SFSystem.deployAComponent(hostname,
                    testURL, appName,
                        false);
        }catch (Exception e) {
            assertTrue(e.getMessage(),
                    e.getMessage().indexOf(searchString)>=0);
        }
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
}
