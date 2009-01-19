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
package org.smartfrog.services.deploydir;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created 07-Mar-2008 16:28:13
 */

public class DirectoryWatcherImpl extends PrimImpl implements DirectoryWatcher, DirectoryWatcherEvent {

    private File directory;
    private long interval;
    public static final String ERROR_NO_DIRECTORY = "No directory: ";
    public static final String ERROR_NOT_A_DIRECTORY = "Not a directory: ";
    private SmartFrogThread watcher;
    public static final String ERROR_NEGATIVE_INTERVAL = "Negative interval: ";

    public DirectoryWatcherImpl() throws RemoteException {
    }


    /**
     * Start watching
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        resolveAttributes();
        watcher = startWatcher();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread.requestThreadTermination(watcher);
    }

    /**
     * Resolve our attributes. Can be subclassed, in which case the parent should be called
     *
     * @throws SmartFrogException smartfrog trouble
     * @throws RemoteException    network trouble
     */
    protected void resolveAttributes()
            throws SmartFrogException, RemoteException {
        directory = FileSystem.lookupAbsoluteFile(this, new Reference(ATTR_DIRECTORY), null, null, true, null);
        if (!directory.exists()) {
            throw new SmartFrogDeploymentException(ERROR_NO_DIRECTORY + directory);
        }
        if (!directory.isDirectory()) {
            throw new SmartFrogDeploymentException(ERROR_NOT_A_DIRECTORY + directory);
        }
        interval = sfResolve(ATTR_INTERVAL, 0L, true);
        if (interval < 0) {
            throw new SmartFrogDeploymentException(ERROR_NEGATIVE_INTERVAL + interval);
        }
    }

    /**
     * Create the watcher. Override point
     *
     * @return the worker thread
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    protected SmartFrogThread startWatcher() throws SmartFrogException, RemoteException {
        return new DirectoryWatcherThread(directory, interval, this);
    }

    /**
     * Notify of a directory changed Base class just prints out a notice of what changeda
     *
     * @param current the current directory
     * @param added   added files
     * @param removed removed files
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    public void directoryChanged(List<File> current, List<File> added, List<File> removed)
        throws SmartFrogException,RemoteException {
        for (File file : added) {
            sfLog().info("Added " + file);
        }
        for (File file : removed) {
            sfLog().info("Removed " + file);
        }
    }

    /**
     * Thread to watch the directory
     */
    private static class DirectoryWatcherThread extends SmartFrogThread {
        private File directory;
        private long interval;
        private DirectoryWatcherEvent handler;
        private List<File> active = new ArrayList<File>();

        /**
         * start a watcher
         *
         * @param directory dir to watch
         * @param interval  sleep interval
         * @param handler   callback handler
         */
        private DirectoryWatcherThread(File directory, long interval, DirectoryWatcherEvent handler) {
            super(new Object());
            this.directory = directory;
            this.interval = interval;
            this.handler = handler;
        }

        /**
         * scan the directory, call our owner's
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            final boolean repeating = interval > 0;
            do {
                scanAndReport();
                if (repeating) {
                    try {
                        sleep(interval);
                    } catch (InterruptedException e) {
                        //end of the sleep.
                        break;
                    }
                }
            } while (repeating && !isTerminationRequested());
        }

        /**
         * Scan the directories, report changes
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException    network problems
         */
        private void scanAndReport() throws SmartFrogException, RemoteException {
            File[] currentFiles = directory.listFiles();
            List<File> current = new ArrayList<File>(currentFiles.length);
            List<File> added = new ArrayList<File>();
            List<File> removed = new ArrayList<File>();
            //work out what is added.
            for (File child : currentFiles) {
                if (child.isDirectory()) {
                    current.add(child);
                    if (active.lastIndexOf(child) == -1) {
                        //its arrived
                        added.add(child);
                        //add to the entry list
                        active.add(child);
                    }
                }
            }

            //work out what is removed
            for (File entry : active) {
                if (current.indexOf(entry) == 0) {
                    removed.add(entry);
                }
            }
            //copy over the new list of current entries
            Collections.sort(active);
            active = current;

            //sort them; this isolates us from file-system quirks
            Collections.sort(added);
            Collections.sort(removed);

            //now see if anything has changed
            if (added.size() > 0 || removed.size() > 0) {
                handler.directoryChanged(current, added, removed);
            }
        }
    }

}
