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
package org.smartfrog.services.database.core;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This component subclasses {@link TransactionImpl} to run the transactions
 * during termination, in the main thread. It does nothing at startup.
 * It can be used as a counterpoint to any components that create tables and the like
 * created 28-Nov-2006 14:47:56
 */

public class TerminationTransactionImpl extends TransactionImpl {


    public TerminationTransactionImpl() throws RemoteException {
    }


    /**
     * do not run any commands on startup
     *
     * @throws SmartFrogDeploymentException for smartfrog problems
     * @throws SmartFrogResolutionException for smartfrog problems
     * @throws RemoteException for network problems.
     */
    @Override
    protected void executeStartupCommands()
            throws SmartFrogDeploymentException, SmartFrogResolutionException, RemoteException {
        //do nothing
    }

    /**
     * Override point: termination commands. All exceptions should be caught and printed here.
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     * @throws SQLException       SQL problems
     */
    @Override
    protected void runTerminationCommands() throws SmartFrogException, RemoteException, SQLException {
        Connection connection = null;
        try {
            connection = connect();
            executeCommands(connection, getCommands().iterator());
            performOperation(connection);
            commitAndClose(connection);
        } finally {
            closeQuietly(connection);
        }
    }

    /**
     * Override point: Return true if the component has termination time SQL commands to run
     *
     * @return true if we have commmands
     */
    @Override
    protected boolean hasTerminationCommands() {
        return getCommands() != null && !getCommands().isEmpty();
    }

}
