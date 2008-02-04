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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.rmi.RemoteException;

/**
 * An implementation of the files class.
 * Created 04-Feb-2008 14:13:14
 *
 */

public class FilesImpl extends PrimImpl implements Files {

    private FilenamePatternFilter filter;
    private File dir;


    public FilesImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
     public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        dir = FileSystem.lookupAbsoluteFile(this,ATTR_DIR,null,null,true,null);
        String pattern=sfResolve(ATTR_PATTERN,"",true);
        boolean caseSensitive= sfResolve(ATTR_CASESENSITIVE,true,true);
        boolean includeHiddenFiles = sfResolve(ATTR_INCLUDEHIDDENFILES, true, true);
        filter=new FilenamePatternFilter(pattern, includeHiddenFiles, caseSensitive);
    }

    /**
     * Return a list of files that match the current pattern. This may be a compute-intensive operation, so cache the
     * result. Note that filesystem race conditions do not guarantee all the files listed still exist...check before
     * acting
     *
     * @return a list of files that match the pattern, or an empty list for no match
     */

    public File[] listFiles() {
        return dir.listFiles(filter);
    }


}
