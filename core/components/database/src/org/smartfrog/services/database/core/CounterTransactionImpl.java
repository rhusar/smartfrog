/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
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
