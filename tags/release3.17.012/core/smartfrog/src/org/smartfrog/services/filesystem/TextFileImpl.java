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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.io.File;
import java.rmi.RemoteException;

/**
 * A text file. By extending {@link SelfDeletingFileImpl}, we ensure that
 * we can clean up or terminate after deployment.
 * created 30-Mar-2005 16:37:56
 */

public class TextFileImpl extends SelfDeletingFileImpl implements TextFile {

    /**
     * Constructor.
     * @throws RemoteException  In case of network/rmi error
     */
    public TextFileImpl() throws RemoteException {
    }



    /**
     * when we deploy, we write out our text stream
     * @throws SmartFrogException error while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        //now write the string value if needed.
        String text = buildText();
        String encoding = null;
        if (text != null) {
            encoding = sfResolve(ATTR_TEXT_ENCODING, (String)null, true);
            File textFile = getFile();
            File parentDir = textFile.getParentFile();
            if (!parentDir.exists()) {
                boolean createParentDirs = sfResolve(ATTR_CREATE_PARENT_DIRS, true, true);
                if (createParentDirs) {
                    parentDir.mkdirs();
                } else {
                    throw new SmartFrogException("No parent directory for " + textFile
                            + " and " + ATTR_CREATE_PARENT_DIRS + "is false");
                }
                if (!parentDir.exists()) {
                    throw new SmartFrogException("Unable to create the parent directory " + parentDir
                            + "for the text file " + textFile);
                }
            }
            FileSystem.writeTextFile(textFile, text, encoding);
        }
        //call the superclass. this may trigger deletion.
        super.sfStart();
    }

    /**
     * Build the text to output
     * @return a string
     * @throws SmartFrogException resolution problems
     * @throws RemoteException networking
     */
    protected String buildText() throws SmartFrogException, RemoteException {
        return sfResolve(ATTR_TEXT, (String) null, false);
    }

}
