/* (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.database.mysql;

import org.smartfrog.services.database.core.ConnectionOpenCondition;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.Connection;

/**
 * A subclass of {@link ConnectionOpenCondition} which issues a ping command against the database.
 *
 * To reduce GPL dependencies and to work with other drivers that are pingable, we use reflection to invoke a method
 * called void ping() We don't care what side effects this method has. For Mysql 3.22 it is some ping() operation which
 * checks the connection.
 *
 * For other databases, it implementation specific.
 *
 * created 05-Dec-2006 15:19:35
 */

public class IsMysqlLive extends ConnectionOpenCondition implements Condition {

    /**
     * Pre-created parameter class
     */
    private final Class[] EMPTY_PARAM_CLASSES = {};
    /**
     * Pre-created parameter class
     */
    private final Object[] EMPTY_PARAM_OBJECTS = {};


    public IsMysqlLive() throws RemoteException {
    }

    /**
     * Introspect to make a ping on the remote system. Any error during the ping is logged at debug level and the method
     * returns false. A successful ping is turned into success; there's no timing checks or anything
     *
     * @param connection connection to ping
     * @return true if the ping succeeds, false if something went wrong.
     * @throws SmartFrogException if the connection does not support a public void ping() method
     * @throws RemoteException for network problems
     */
    protected boolean ping(Connection connection) throws SmartFrogException, RemoteException {
        Method method;
        method = loadPingMethod(connection);
        try {
            method.invoke(connection, EMPTY_PARAM_OBJECTS);
            return true;
        } catch (IllegalAccessException e) {
            throw new SmartFrogException("Connection " + connection + "  of type " + connection.getClass()
                    + " does not allow access to the ping() method", e, this);
        } catch (InvocationTargetException e) {
            //something went wrong with the Ping operation. log it, and return false
            //as the database connection is clearly not working.
            getLog().debug("When pinging ", e.getCause());
            return false;
        }
    }

    /**
     * Load our ping method
     *
     * @param connection connection to ping
     * @return the ping method
     * @throws SmartFrogException  if the connection does not support a public void ping() method
     * @throws RemoteException for network problems
     */
    protected Method loadPingMethod(Connection connection) throws SmartFrogException, RemoteException {
        Method method;
        Class clazz = connection.getClass();
        try {
            method = clazz.getMethod("ping", EMPTY_PARAM_CLASSES);
        } catch (NoSuchMethodException e) {
            throw new SmartFrogException("Connection " + connection + "  of type " + clazz
                    + " does not have a method called ping()", e, this);
        }
        return method;
    }


}
