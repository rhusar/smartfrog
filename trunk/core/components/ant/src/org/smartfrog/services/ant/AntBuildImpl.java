/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ant;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;
import org.apache.tools.ant.Project;

import java.rmi.RemoteException;
import java.util.Vector;
import java.io.File;

/**
 *
 * Created 31-Oct-2007 14:24:05
 *
 */

public class AntBuildImpl extends PrimImpl implements AntBuild {

    public static final String ERROR_NO_DIRS = "no build directories specified: one of "+ATTR_BASEDIR+" or "+ATTR_DIRECTORIES+" must be set";

    private Project rootProject = null;
    private File baseDir;

    private String buildfile;
    private File genericantfile;
    private Vector<File> directories;
    private Vector targets;
    private AntThread workerAnt;
    public static final String ERROR_MISSING_BUILD_FILE = "Missing build file: ";

    //private File

    public AntBuildImpl() throws RemoteException {
    }


    /**
     * read in state and start the component
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        baseDir = FileSystem.lookupAbsoluteFile(this, ATTR_BASEDIR, null, null, false, null);
        directories = FileSystem.resolveFileList(this, new Reference(ATTR_DIRECTORIES), baseDir, false);
        if (directories == null) {
            if (baseDir == null) {
                throw new SmartFrogResolutionException(ERROR_NO_DIRS);
            } else {
                directories = new Vector<File>(1);
                directories.add(baseDir);
            }
        }
        genericantfile = FileSystem.lookupAbsoluteFile(this, ATTR_GENERICANTFILE, null, null, false, null);
        //you need a build file if there is no generic ant file.
        if(genericantfile == null) {
            buildfile = sfResolve(ATTR_BUILDFILE, buildfile, true);
        }
        targets = sfResolve(ATTR_TARGETS,targets,true);


        //do some validation.
        for(File dir:directories) {
            if(!dir.exists()) {
                throw new SmartFrogDeploymentException("No directory: "+dir);
            }
            if (!dir.isDirectory()) {
                throw new SmartFrogDeploymentException("Not a directory: " + dir);
            }
            File build=new File(dir,buildfile);
            if(!build.exists()) {
                throw new SmartFrogDeploymentException(ERROR_MISSING_BUILD_FILE + build);
            }
        }

        if(genericantfile!=null && !genericantfile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_MISSING_BUILD_FILE + genericantfile);
        }
        workerAnt=new AntThread();
        //to get here. all is well.
        workerAnt.start();
    }


    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }

    private class AntThread extends SmartFrogThread {

        /**
         * do the work
         */
        public void execute() throws Throwable {
        }
    }
}
