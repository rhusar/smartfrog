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
 * To reduce GPL dependencies and to work with other drivers that are pingable, we use
 * reflection to invoke a method called 'vind ping
 * for mysql, this requires a database > 3.22, and a compatible mysql driver.
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
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        return super.evaluate();
    }

    /**
     * Introspect to make a ping on the remote system. Any error during the ping is logged at debug level and
     * the method returns false. A successful ping is turned into success; there's no timing checks or anything
     * @param connection connection to ping
     * @return true if the ping succeeds, false if something went wrong.
     * @throws SmartFrogException if the connection does not support a public void ping() method
     */
    protected boolean ping(Connection connection) throws SmartFrogException {
        Class clazz = connection.getClass();
        try {
            Method method = clazz.getMethod("ping", EMPTY_PARAM_CLASSES);
            method.invoke(connection, EMPTY_PARAM_OBJECTS);
            return true;
        } catch (NoSuchMethodException e) {
            throw new SmartFrogException("Connection " + connection + "  of type " +clazz
                +" does not have a method called ping()",e);
        } catch (IllegalAccessException e) {
            throw new SmartFrogException("Connection " + connection + "  of type " + clazz
                    + " does allow access to the ping() method",e);
        } catch (InvocationTargetException e) {
            //something went wrong with the Ping operation. log it, and return false
            //as the database connection is clearly not working.
            getLog().debug("When pinging ", e.getCause());
            return false;
        }
    }


}
