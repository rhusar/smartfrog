package org.smartfrog.services.database.core;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.Vector;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Execute a set of commands
 */
public class TransactionCommandsImpl extends JdbcOperationImpl
        implements TransactionCommands, Runnable {

    public TransactionCommandsImpl() throws RemoteException {
    }

    private boolean autocommit=false;
    private Vector commands;

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        autocommit=sfResolve(ATTR_AUTOCOMMIT,autocommit,true);
        commands=sfResolve(ATTR_COMMANDS,commands,true);
        checkConnection();
    }

    /**
     * Connect to the database; return a simple database connection bound to the
     * jdbc options.
     *
     * @return a new database connection
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogDeploymentException
     *
     */
    protected Connection connect() throws SmartFrogDeploymentException {
        Connection connection = super.connect();
        if(autocommit) {
            try {
                connection.setAutoCommit(autocommit);
            } catch (SQLException e) {
                throw translate("setting autocommit flag",e);
            }
        }
        return connection;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        Connection connection = null;
        try {
            connection = connect();

            if(!autocommit) {
                connection.commit();
            }
            connection.close();

        } catch (SQLException e) {
            queueFault("processing transactions",e);
        } catch (SmartFrogDeploymentException e) {
            queueFault(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    //we only get here at the end, when another fault was
                    //thrown, so dont bother logging it.
                }
            }

    }
}

}

