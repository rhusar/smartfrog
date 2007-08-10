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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * created 13-Oct-2006 16:41:10
 */


public interface TestBlock extends Remote {


    /**
     * {@value}
     */
    String ATTR_FINISHED = "finished";

    /**
     * {@value}
     */
    String ATTR_STATUS = "status";
    /**
     * {@value}
     */
    String ATTR_FAILED = "failed";
    /**
     * {@value}
     */
    String ATTR_SUCCEEDED = "succeeded";


    /**
     * Timeout time in millis.
     * {@value}
     */
    String ATTR_TIMEOUT = "timeout";

    /**
     * Is timeout expected?
     * {@value}
     */
    String ATTR_EXPECTTIMEOUT = "expectTimeout";

    /**
     * Flag to set if timeout was forced.
     * {@value}
     */
    String ATTR_FORCEDTIMEOUT = "forcedTimeout";

    /**
     * Optional description attribute.
     * {@value}
     */
    String ATTR_DESCRIPTION = "description";
    /**
     * Name that a deployed action goes by
     * {@value}
     */
    String ACTION = "_action";

    /**
     * Return true iff the component is finished.
     * Spin on this, with a (delay) between calls
     *
     * @return true if the test has finished
     * @throws RemoteException on network trouble
     * @throws SmartFrogException on other problems
     */
    boolean isFinished() throws RemoteException, SmartFrogException;

    /**
     * @return true only if the test has finished and failed
     * @throws RemoteException on network trouble
     * @throws SmartFrogException on other problems
     */
    boolean isFailed() throws RemoteException, SmartFrogException;

    /**
     * @return true iff the test succeeded
     * @throws RemoteException on network trouble
     * @throws SmartFrogException on other problems
     */

    boolean isSucceeded() throws RemoteException, SmartFrogException;

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     * @throws RemoteException on network trouble
     * @throws SmartFrogException on other problems
     */
    TerminationRecord getStatus() throws RemoteException, SmartFrogException;


    /**
     * return the current action
     * @return the child component. this will be null after termination.
     * @throws RemoteException on network trouble
     * @throws SmartFrogException on other problems
     */
    Prim getAction() throws RemoteException, SmartFrogException;

    /**
     * turn true if a test is skipped; if some condition caused
     * it not to run
     * @return whether or not the test block skipped deployment of children.
     */
    boolean isSkipped() throws RemoteException, SmartFrogException;
}
