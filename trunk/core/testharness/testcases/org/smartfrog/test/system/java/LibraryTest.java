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
import org.smartfrog.services.os.java.LibrariesImpl;
import org.smartfrog.services.os.java.LibraryArtifactImpl;

/**
 * created 04-Apr-2005 15:35:58
 */

public class LibraryTest extends SmartFrogTestBase {
    public static final String FILES = JavaPackageTest.FILES;

    public LibraryTest(String name) {
        super(name);
    }

    public void testSimplePatchIsNoop() {
        assertSame("", LibraryArtifactImpl.patchProject(""));
        assertSame("project-without-dots",
                LibraryArtifactImpl.patchProject("project-without-dots"));
        assertEquals("org/smartfrog/something",
                LibraryArtifactImpl.patchProject("org.smartfrog.something"));
    }

    public void testLibrariesCacheDirInvalid() throws Throwable {
        deployExpectingException(FILES+"testLibrariesCacheDirInvalid.sf",
                "testLibrariesCacheDirInvalid",
                "SmartFrogLifecycleException",
                null,
                "SmartFrogException",
                LibrariesImpl.ERROR_NOT_A_DIRECTORY);
    }


    public void testOrphanLibrary() throws Throwable {
        deployExpectingException(FILES + "testOrphanLibrary.sf",
                "testOrphanLibrary",
                "SmartFrogLifecycleException",
                null,
                "SmartFrogResolutionException",
                LibraryArtifactImpl.ERROR_NO_REPOSITORY);
    }

    public void testRepositoryBadType() throws Throwable {
        deployExpectingException(FILES + "testRepositoryBadType.sf",
                "testRepositoryBadType",
                "SmartFrogLifecycleException",
                null,
                "SmartFrogResolutionException",
                "repository"
                );
    }
}
