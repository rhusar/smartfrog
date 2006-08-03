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
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * created 19-Jun-2006 16:43:01
 */

public abstract class AbstractDbcDelegate implements ApplicationServerContext {

    private File sourceFile;
    private File destDir;
    private File destFile;
    private DeployByCopyServerImpl server;
    private Prim declaration;

    public AbstractDbcDelegate(DeployByCopyServerImpl server, Prim owner) {
        this.server = server;
        this.declaration = owner;
    }

    public Prim getDeclaration() {
        return declaration;
    }

    public abstract String getExtension();

    public void deploy() throws SmartFrogException, RemoteException {
    }

    public void start() throws SmartFrogException, RemoteException {
        String source =
                FileSystem.lookupAbsolutePath(getDeclaration(),
                        ATTR_FILE,
                        null,
                        null,
                        true,
                        null);
        //sanity check
        sourceFile = new File(source);
        if (!sourceFile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_FILE_NOT_FOUND +
                    sourceFile);
        }
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
        String absolutePath = "/" + contextPath;
        getDeclaration().sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);

        //create the full extension now.
        filename = contextPath + getExtension();
        //do nothing
        destDir = server.getDestDir();
        destFile = new File(destDir, filename);
        try {
            FileSystem.fCopy(sourceFile, destFile);
        } catch (IOException e) {
            throw SmartFrogDeploymentException.forward(e);
        }
    }

    public void terminate() throws RemoteException, SmartFrogException {
        //delete the destination file
        if (destFile != null && destFile.exists() && !destFile.delete()) {
            destFile.deleteOnExit();
        }
    }

    public void ping() throws SmartFrogLivenessException, RemoteException {
        if (!destFile.exists()) {
            throw new SmartFrogLivenessException("Deployed File " + destFile + " has disappeared");
        }
    }
}
