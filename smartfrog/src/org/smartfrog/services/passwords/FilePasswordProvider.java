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

package org.smartfrog.services.passwords;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;

/**
 * Implementation of PasswordProvider which reads the password from a text 
 * file.
 * @author Ashish Awasthi
 */ 
public class FilePasswordProvider extends PrimImpl implements Prim, 
                                                    PasswordProvider     {

    /**
     * The attribute of the password file: "{@value}"
     */
    public static final String PASSWORD_FILE="passwordFile";
    public static final String ERROR_MISSING_PASSWORD_FILE = "Missing password file: ";

    /**
     * Constructs FilePasswordProvider object.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public FilePasswordProvider() throws RemoteException {
    }


    /**
     * {@inheritDoc}
     * @throws SmartFrogException If unable to get the password
     * @throws RemoteException in case of network or RMI error
     * @throws SmartFrogDeploymentException if there is no password file
     * @return a password
     */
    public String getPassword() throws SmartFrogException, RemoteException {
        File file =
                FileSystem.lookupAbsoluteFile(this,
                        PASSWORD_FILE,
                        null,
                        null,
                        true,
                        null);
        if(!file.exists()) {
            throw new SmartFrogDeploymentException(ERROR_MISSING_PASSWORD_FILE +file.getAbsolutePath(),this);
        }
        try {
            String contents =
                    FileSystem.readFile(file, Charset.defaultCharset()).toString();
            return contents.trim();
        } catch (IOException ioex) {
            throw new SmartFrogException("Failed to read "+file.getAbsolutePath(), ioex, this);
        }
    }
}
