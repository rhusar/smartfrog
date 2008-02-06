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
package org.smartfrog.services.filesystem.files;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.rmi.RemoteException;

/**
 * An implementation of the files class. Created 04-Feb-2008 14:13:14
 */

public class FilesImpl extends PrimImpl implements Files {

    private Fileset fileset=new Fileset();
    private FilenamePatternFilter filter;
    public static final String ERROR_NOT_STARTED = "The component must be started before it can list the files";
    private static final Reference FILES_ATTRIBUTE = new Reference(ATTR_FILES);
    private static final Reference DIR_ATTRIBUTE = new Reference(ATTR_DIR);
    private static final Reference PATTERN_ATTRIBUTE = new Reference(ATTR_PATTERN);
    private static final Reference CASE_ATTRIBUTE = new Reference(ATTR_CASESENSITIVE);
    private static final Reference HIDDEN_ATTRIBUTE = new Reference(ATTR_INCLUDEHIDDENFILES);


    public FilesImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        fileset= resolveFileset(this);
    }

    /**
     * Return a list of files that match the current pattern. This may be a
     * compute-intensive operation, so cache the result. Note that filesystem
     * race conditions do not guarantee all the files listed still exist...check
     * before acting
     *
     * @return a list of files that match the pattern, or an empty list for no
     *         match
     * @throws SmartFrogLifecycleException if it could  not do this
     */

    public File[] listFiles() throws SmartFrogLifecycleException {
        return fileset.listFiles();
    }

    /**
     * Get the base directory of these files (may be null)
     *
     * @return the base directory
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public File getBaseDir() throws RemoteException, SmartFrogException {
        return fileset.baseDir;
    }


    /**
     * Build a fileset from a Prim component with the Files attributes.
     * @param component component to work with
     * @return a newly filled in fileset
     * @throws SmartFrogException if there were problems resolving attributes
     * @throws RemoteException for networking problems
     */
    public static Fileset resolveFileset(Prim component)
            throws SmartFrogException, RemoteException {
        return Fileset.createFileset(component,
                FILES_ATTRIBUTE,
                DIR_ATTRIBUTE,
                PATTERN_ATTRIBUTE,
                CASE_ATTRIBUTE,
                HIDDEN_ATTRIBUTE);
    }
}
