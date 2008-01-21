package org.smartfrog.services.database.core;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This component subclasses {@link TransactionImpl} to run the transactions at startup, and the transactions listed in
 * counterCommands on termination It can be used as a counterpoint to any components that create tables and the like
 * created 28-Nov-2006 14:47:56
 */

public class CounterTransactionImpl extends TransactionImpl {

    public static final String ATTR_COUNTERCOMMANDS = "counterCommands";

    private static final Reference REF_COUNTERCOMMANDS = new Reference(ATTR_COUNTERCOMMANDS);

    /**
     * The list of counterCommands, extracted at startup
     */
    private ArrayList<String> counterCommands;

    public CounterTransactionImpl() throws RemoteException {
    }


    /**
     * The startup operation is to read the commands in then execute them by way of {@link #executeStartupCommands()}.
     * Subclasses may change this behaviour
     *
     * @throws SmartFrogException for smartfrog problems
     * @throws RemoteException    for network problems.
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Vector cc = null;
        cc = sfResolve(REF_COUNTERCOMMANDS, cc, true);
        counterCommands = new ArrayList<String>(cc.size());
        for (Object o : cc) {
            counterCommands.add(o.toString());
        }
    }

    /**
     * Override point: termination commands. All exceptions should be caught and printed here.
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     * @throws SQLException       SQL problems
     */
    protected void runTerminationCommands() throws SmartFrogException, RemoteException, SQLException {
        Connection connection = null;
        try {
            connection = connect();
            executeCommands(connection, counterCommands.iterator());
            performOperation(connection);
            commitAndClose(connection);
        } finally {
            closeQuietly(connection);
        }
    }

    /**
     * Override point: Return true if the component has termination time SQL commands to run
     *
     * @return true if there are counterCommands.
     */
    protected boolean hasTerminationCommands() {
        return counterCommands != null && counterCommands.size() > 0;
    }
}
