/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.java;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.services.os.java.LibraryHelper;
import org.smartfrog.services.os.java.LibraryImpl;
import org.smartfrog.services.os.java.LibraryArtifactImpl;
import org.smartfrog.services.os.java.LibraryArtifact;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;

import java.io.File;
import java.rmi.RemoteException;
import java.net.UnknownHostException;

import junit.framework.AssertionFailedError;

/**
 * Test library work
 * created 04-Apr-2005 15:35:58
 */

public class LibraryTest extends SmartFrogTestBase {
    public static final String FILES = JavaPackageTest.FILES;
    private static final String EXCEPTION_JUNIT_ASSERTION_FAILED = "junit.framework.AssertionFailedError";


    public LibraryTest(String name) {
        super(name);
    }

    public void testSimplePatchIsNoop() {
        assertSame("", LibraryHelper.patchProject(""));
        assertSame("project-without-dots",
                LibraryHelper.patchProject("project-without-dots"));
        assertEquals("org/smartfrog/something",
                LibraryHelper.patchProject("org.smartfrog.something"));
    }

    public void testLibrariesCacheDirInvalid() throws Throwable {
        deployExpectingException(FILES + "testLibrariesCacheDirInvalid.sf",
                "testLibrariesCacheDirInvalid",
                EXCEPTION_LIFECYCLE,
                null,
                EXCEPTION_RESOLUTION,
                LibraryImpl.ERROR_NOT_A_DIRECTORY);
    }


    public void testOrphanLibrary() throws Throwable {
        deployExpectingException(FILES + "testOrphanLibrary.sf",
                "testOrphanLibrary",
                EXCEPTION_LIFECYCLE,
                null,
                EXCEPTION_RESOLUTION,
                LibraryArtifact.ATTR_LIBRARY);
    }

    public void testRepositoryBadType() throws Throwable {
        deployExpectingException(FILES + "testRepositoryBadType.sf",
                "testRepositoryBadType",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_RESOLUTION,
                null);
    }

    public void testMavenLibrary() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "testMavenLibrary.sf", "testMavenLibrary");
    }

    public void testSimpleLibrary() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "testSimpleLibrary.sf", "testSimpleLibrary");
    }

    public void testEmptyRepository() throws Throwable {
        deployExpectingException(FILES + "testEmptyRepository.sf",
                "testEmptyRepository",
                EXCEPTION_LIFECYCLE,
                null,
                EXCEPTION_SMARTFROG,
                LibraryArtifactImpl.ERROR_NO_REPOSITORIES);
    }


    public void testMaven2Download() throws Throwable {
        deploySuccessfulDownload("testMaven2Download");
    }

    /**
     * deploy a download, expect a file to go into absolutePath, a file which
     * must exist.
     *
     * @param appName
     *
     * @throws Throwable
     */
    private void deploySuccessfulDownload(String appName) throws Throwable {
        try {
            application = deployExpectingSuccess(FILES +
                    appName + ".sf", appName);
            String filename = application.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                    (String) null,
                    true);
            File file = new File(filename);
            assertTrue("not found " + filename, file.exists());
            file.delete();
        } catch(Throwable thrown) {
            //connection refused exceptions are a sign of being offline
            assertFaultCauseAndTextContains(thrown,null, "onnection refused",null);
            getLog().info("No connection to the remote server; ignoring result",thrown);
        }
    }

    /**
     * test that maven1 downloads work
     *
     * @throws Throwable
     */
    public void testMaven1Download() throws Throwable {
        deploySuccessfulDownload("testMaven1Download");
    }

    /**
     * test that maven1 downloads work
     *
     * @throws Throwable
     */
    public void testMaven2DownloadBadSha1() throws Throwable {
        try {
            deployExpectingException(FILES + "testMaven2DownloadBadSha1.sf",
                    "testMaven2DownloadBadSha1",
                    EXCEPTION_LIFECYCLE,
                    null,
                    EXCEPTION_SMARTFROG,
                    LibraryArtifactImpl.ERROR_CHECKSUM_FAILURE);
        } catch (AssertionFailedError thrown) {
            assertFaultCauseAndTextContains(thrown, EXCEPTION_JUNIT_ASSERTION_FAILED, "onnection refused", null);
        }
    }
}
