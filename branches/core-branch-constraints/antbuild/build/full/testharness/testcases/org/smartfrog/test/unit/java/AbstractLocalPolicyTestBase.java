/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */

package org.smartfrog.test.unit.java;

import org.smartfrog.services.os.java.LocalCachePolicy;
import org.smartfrog.services.os.java.RemoteCachePolicy;
import org.smartfrog.services.os.java.SerializedArtifact;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.File;

public abstract class AbstractLocalPolicyTestBase extends AbstractPolicyTestBase {
    protected LocalCachePolicy policy;

    protected void setUp() throws Exception {
        super.setUp();
        policy = createPolicy();
    }
    
    abstract LocalCachePolicy createPolicy() throws Exception;

    /**
     * Create a path for the logging artifact
     *
     * @return String logging path
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *
     */
    protected String createLoggingPath() throws Exception {
        return policy.createLocalPath(logging);
    }

    public void testNullArtifact() throws Exception {
        try {
            policy.createLocalPath(null);
            fail("Should have failed ");
        } catch (SmartFrogRuntimeException e) {
            assertErrorMessageContains(e,
                    SerializedArtifact.ERROR_NULL_ARTIFACT);
        }
    }


    public void testEmptyArtifact() throws Exception {
        SerializedArtifact artifact = new SerializedArtifact();
        try {
            policy.createLocalPath(artifact);
            fail("Should have failed ");
        } catch (SmartFrogRuntimeException e) {
            assertErrorMessageContains(e,
                    SerializedArtifact.ERROR_INVALID_LIBRARY);
        }
    }


    public void testNullProject() throws Exception {
        logging.project = null;
        try {
            createLoggingPath();
            fail("Should have failed ");
        } catch (SmartFrogRuntimeException e) {
            assertErrorMessageContains(e,
                    SerializedArtifact.ERROR_INVALID_LIBRARY);
        }
    }



    public void testSimpleProject() throws Exception {
        String path = createLoggingPath();
        assertTrue("expected path " + path + " to end with " + logging.extension,
                path.endsWith(logging.extension));
        assertTrue("expected path " + path + " to start with " + logging.project,
                path.indexOf(logging.project)==0);
        assertTrue("version in the file",path.indexOf("-" + logging.version) > 0);
//        assertTrue("path not in unix form "+path,path.startsWith("/"));
    }


    public void testNullExtension() throws Exception {
        logging.extension = null;
        String path = createLoggingPath();
        assertTrue("expected path " + path + " to end with " + logging.version,
                path.endsWith(logging.version));
    }

}
