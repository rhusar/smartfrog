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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;
import java.sql.Connection;

/**
 * This is a condition that asserts that a connection can be opened. There is also scope for a subclass to
 * apply an operation to the condition, so that the mysql condition can do a ping.
 * created 05-Dec-2006 15:03:57
 */

public class ConnectionOpenCondition extends AbstractJdbcOperation implements Condition {


    public ConnectionOpenCondition() throws RemoteException {

    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        //do a quick connect to see that we are ok
        Connection connection = null;
        try {
            connection = connect();
            ping(connection);
            return true;
        } catch(SmartFrogException e) {
            if(getLog().isDebugEnabled()) getLog().debug("Could not connect",e);
            return false;
        } finally {
            close(connection);
        }
    }

    protected boolean ping(Connection connection) throws SmartFrogException,
            RemoteException {
        return true;
    }
}
