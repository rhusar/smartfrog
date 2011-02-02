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
package org.smartfrog.services.filesystem.csvfiles;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;

/**
 * Base class for processing CSV content; has a worker thread that is terminated during shutdown.
 *
 * Created 25-Feb-2008 12:11:32
 *
 */

public class AbstractCSVProcessor extends PrimImpl {

    public AbstractCSVProcessor() throws RemoteException {
    }

    /**
     * Source component
     */
    public static final String ATTR_SOURCE = "source";
    private volatile WorkflowThread reader;

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread.requestThreadTermination(getReader());
    }

    public WorkflowThread getReader() {
        return reader;
    }

    protected void setReader(WorkflowThread reader) {
        this.reader = reader;
    }
}
