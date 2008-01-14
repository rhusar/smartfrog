/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.java.libraries;

import junit.framework.AssertionFailedError;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.os.java.LibraryArtifact;
import org.smartfrog.services.os.java.LibraryArtifactImpl;
import org.smartfrog.services.os.java.LibraryHelper;
import org.smartfrog.services.os.java.LibraryImpl;
import org.smartfrog.test.DeployingTestBase;

import java.io.File;
import java.net.ConnectException;

/**
 * Test Ivy library work
 */

public class IvyLibraryTest extends DeployingTestBase {
    public static final String FILES = "org/smartfrog/test/system/java/libraries/";
    private static final String CONNECTION_REFUSED = "refused";
    private static final String EXCEPTION_JUNIT_ASSERTION_FAILED = "junit.framework.AssertionFailedError";


    public IvyLibraryTest(String name) {
        super(name);
    }


    public void testLocalSmartFrog() throws Throwable {
        expectSuccessfulTestRun(FILES, "testLocalSmartFrog");
    }
    
    
}
