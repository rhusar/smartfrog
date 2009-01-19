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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.TupleDataSource;

import java.rmi.RemoteException;

/**
 *
 * Created 25-Feb-2008 12:27:35
 *
 */

public class TupleReaderThread extends WorkflowThread {


    /**
     * data source
     */
    protected TupleDataSource source;
    private volatile int currentLine;

    /**
     * Create a basic thread
     * @param owner who owns us
     * @param source the data source
     * @param workflowTermination should the thread plan for workflow termination
     */
    public TupleReaderThread(Prim owner, TupleDataSource source, boolean workflowTermination) {
        super(owner, workflowTermination);
        this.source = source;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    /**
     * read the thread in, validate the values, then maybe terminate the component
     *
     * @throws Throwable if anything went wrong
     */
    public void execute() throws Throwable {
        source.start();
        currentLine = 0;
        try {
            onStarted();
            String[] line;
            while ((line = source.getNextTuple()) != null) {
                if (isTerminationRequested()) {
                    onTerminationRequested();
                    //bail out completely
                    return;
                }
                processOneLine(line);
                currentLine++;
            }
            onFinished();
        } finally {
            source.close();
        }
    }

    /**
     * we've started. do any preparation
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    protected void onStarted() throws SmartFrogException, RemoteException {

    }
    /**
     * we've finished (successfully)
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    protected void onFinished() throws SmartFrogException, RemoteException {

    }

    /**
     * Process one line of the data source
     *
     * @param line line to process
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    protected void processOneLine(String[] line) throws SmartFrogException, RemoteException {

    }

    /**
         * Handle termination requested; after this call
     * the component will return
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    protected void onTerminationRequested() throws SmartFrogException, RemoteException {
    }
}
