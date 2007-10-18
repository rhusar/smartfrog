/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.assertions.history;

import org.smartfrog.services.assertions.SmartFrogAssertionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A component to log things to the history log
 * <pre>
  HistoryLog extends Prim {
     log TBD;
     deployMessage "";
     startMessage "";
     terminateMessage "";
 }
 </pre>
 */

public class HistoryLogImpl extends AbstractHistoryPrimImpl implements Remote {


    public static final String ATTR_DEPLOY_MESSAGE="deployMessage";
    public static final String ATTR_START_MESSAGE="startMessage";
    public static final String ATTR_TERMINATE_MESSAGE="terminateMessage";

    public HistoryLogImpl() throws RemoteException {
    }


    /**
     * L
     * @param attribute
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws SmartFrogAssertionException
     */
    protected void log(String attribute) throws SmartFrogResolutionException, RemoteException, SmartFrogAssertionException {
        String message=sfResolve(attribute,"",false);
        logMessage(message);
    }

    /**
     * Log a non-null, non-empty message.
     * @param message message to log. Skipped if null or empty
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws SmartFrogAssertionException
     */
    private void logMessage(String message) throws SmartFrogResolutionException, RemoteException, SmartFrogAssertionException {
        if(message!=null && message.length()>0) {
            History history = resolveHistory();
            history.log(message);
            sfLog().info(message);
        }
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log(ATTR_DEPLOY_MESSAGE);
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        log(ATTR_START_MESSAGE);
        queueForTermination("HistoryLog");
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            log(ATTR_TERMINATE_MESSAGE);
        } catch (SmartFrogResolutionException e) {
            sfLog().ignore(e);
        } catch (RemoteException e) {
            sfLog().ignore(e);
        } catch (SmartFrogAssertionException e) {
            sfLog().ignore(e);
        }
    }
}
