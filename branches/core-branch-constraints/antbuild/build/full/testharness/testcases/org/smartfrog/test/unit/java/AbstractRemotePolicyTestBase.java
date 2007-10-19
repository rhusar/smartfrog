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

import org.smartfrog.services.os.java.RemoteCachePolicy;
import org.smartfrog.services.os.java.SerializedArtifact;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogException;

public abstract class AbstractRemotePolicyTestBase extends AbstractPolicyTestBase {

    protected RemoteCachePolicy policy;
    

    protected void setUp() throws Exception {
        super.setUp();
        policy = createPolicy();
    }
    
    abstract RemoteCachePolicy createPolicy() throws Exception;

    /**
     * Create a path for the logging artifact
     *
     * @return  String logging path
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *
     */
    protected String createLoggingPath() throws Exception {
        return policy.createRemotePath(logging);
    }

    public void testNullArtifact() throws Exception {
        try {
            policy.createRemotePath(null);
            fail("Should have failed ");
        } catch (SmartFrogRuntimeException e) {
            assertErrorMessageContains(e,SerializedArtifact.ERROR_NULL_ARTIFACT);
        }
    }
    
    public void testEmptyArtifact() throws Exception {
        SerializedArtifact artifact=new SerializedArtifact();
        try {
            policy.createRemotePath(artifact);
            fail("Should have failed ");
        } catch (SmartFrogRuntimeException e) {
            assertErrorMessageContains(e,SerializedArtifact.ERROR_INVALID_LIBRARY);
        }
    }


    public void testNullProject() throws Exception {
        logging.project=null;
        try {
            createLoggingPath();
            fail("Should have failed ");
        } catch (SmartFrogRuntimeException e) {
            assertErrorMessageContains(e,SerializedArtifact.ERROR_INVALID_LIBRARY);
        }
    }


    public void testSimpleProject() throws Exception {
        String path=policy.createRemotePath(logging);
        assertTrue("expected path "+path+" to end with "+logging.extension,
                path.endsWith(logging.extension));
        assertTrue("expected path "+path+" to start with /"+logging.project,
                path.startsWith(logging.project));
        assertTrue(path.indexOf("-"+logging.version)>0);
    }


    public void testNullExtension() throws Exception {
        logging.extension=null;
        String path= createLoggingPath();
        assertTrue("expected path "+path+" to end with "+logging.version,
                path.endsWith(logging.version));
    }
    
}
