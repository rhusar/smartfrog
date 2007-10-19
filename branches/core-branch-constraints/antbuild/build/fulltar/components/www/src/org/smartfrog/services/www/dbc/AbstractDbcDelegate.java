/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.dbc;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import java.io.File;
import java.rmi.RemoteException;

/**
 * This is the base class for all deploy by copy delegates.
 * It deploys by copying in its start() operation. This is not a Prim derivative;
 * it is a simple POJO that implements {@link ApplicationServerContext}
 * Created 19-Jun-2006 16:43:01
 */

public abstract class AbstractDbcDelegate implements ApplicationServerContext {

    private DeployByCopyServerImpl server;
    private Prim declaration;
    private QueuedFile operation;

    /**
     * Bind to a server, taking in a reference to the (possibly remote) owner interface)
     * @param server owner server
     * @param owner owner as a Prim interface.
     */
    public AbstractDbcDelegate(DeployByCopyServerImpl server, Prim owner) {
        this.server = server;
        this.declaration = owner;
    }

    public Prim getDeclaration() {
        return declaration;
    }

    /**
     * Get the extension of this artifact.
     * @return something like ".ear"
     */
    public abstract String getExtension();

    /**
     * Base class deploy is a no-op.
     * @throws SmartFrogException subclasses may throw this
     * @throws RemoteException subclasses may throw this
     */
    public void deploy() throws SmartFrogException, RemoteException {
    }

    /**
     * Deploy by determining the source file, and doing a blocking copy.
     * If the source file is missing, A {@link SmartFrogDeploymentException} with
     * text {@link #ERROR_FILE_NOT_FOUND} is thrown. IOExceptions are passed on
     * nested inside a {@link SmartFrogDeploymentException}.
     * @throws SmartFrogException if the deployment failed
     * @throws RemoteException for network problems.
     */
    public void start() throws SmartFrogException, RemoteException {
        String source =
                FileSystem.lookupAbsolutePath(getDeclaration(),
                        ATTR_FILE,
                        null,
                        null,
                        true,
                        null);
        //sanity check
        File sourceFile = new File(source);
        String filename = sourceFile.getName();
        if (filename.endsWith(getExtension())) {
            filename = filename.substring(0, filename.length() - getExtension().length());
        }

        String contextPath = getDeclaration().sfResolve(ATTR_CONTEXT_PATH,
                filename,
                true);
        if (contextPath.startsWith("/")) {
            contextPath = contextPath.substring(1);
        }
        String absolutePath = '/' + contextPath;
        getDeclaration().sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);

        //create the full extension now.
        filename = contextPath + getExtension();

        //queue/execute the operation.
        operation = server.queueCopy(sourceFile, filename);
        pingOperation();
    }

    /**
     * Poll for the completed state of the operation
     * @throws SmartFrogException
     */
    protected void pingOperation() throws SmartFrogException {
        if (operation != null) {
            operation.ping();
        }
    }

    /**
     * Terminate by deleting the destination file. If it cannot be deleted
     * (i.e. the file is locked), we queue a {@link File#deleteOnExit()} operation,
     * which <i>may</i> delete it later.
     * @throws RemoteException  subclasses may throw this
     * @throws SmartFrogException subclasses may throw this
     */
    public void terminate() throws RemoteException, SmartFrogException {
        //delete the destination file
        server.sfLog().info("undeploying "+operation);
        operation.deleteDestFile();
    }

    /**
     * Ping operation checks that the destination file exists
     * @throws SmartFrogLivenessException if the destination file is absent
     * @throws RemoteException subclasses may throw this
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        try {
            pingOperation();
        } catch (SmartFrogException e) {
            throw (SmartFrogLivenessException)
                    SmartFrogLivenessException.forward(e);
        }
    }
}
