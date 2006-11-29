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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This component subclasses {@link #TransactionImpl()} to run the transactions
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
     * @throws org.smartfrog.sfcore.common.SmartFrogDeploymentException
     *                                  for smartfrog problems
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *                                  for smartfrog problems
     * @throws java.rmi.RemoteException for network problems.
     */
    protected void executeStartupCommands()
            throws SmartFrogDeploymentException, SmartFrogResolutionException, RemoteException {
        //do nothing
    }


    /**
     * shut down the component by running the operations
     *
     * @param status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        Throwable caught = null;
        Connection connection = null;
        try {
            connection = connect();
            performOperation(connection);
            commitAndClose(connection);
        } catch (SQLException e) {
            caught = e;
        } catch (SmartFrogException e) {
            caught = e;
        } catch (RemoteException e) {
            caught = e;
        } finally {
            closeQuietly(connection);
        }
        if(caught!=null) {
            sfLog().ignore("Caught while terminating the application",caught);
        }
    }
}
