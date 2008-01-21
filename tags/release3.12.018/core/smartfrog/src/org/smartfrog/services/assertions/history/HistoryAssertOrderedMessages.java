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

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** created 09-Jul-2007 14:34:19 */

public class HistoryAssertOrderedMessages extends AbstractHistoryPrimImpl implements Remote {

    public static final String ATTR_MESSAGE1 = "message1";
    public static final String ATTR_MESSAGE2 = "message2";

    /*
    HistoryAssertOrderedMessages extends Prim {
  log TBD;
  message1;
  message2;
  //Optional : errorText;
  //errorText
}
     */
    public HistoryAssertOrderedMessages() throws RemoteException {
    }


    /**
     * Assert that the  two events are in the history log, one before the other
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String message1 = sfResolve(ATTR_MESSAGE1, "", true);
        String message2 = sfResolve(ATTR_MESSAGE2, "", true);
        History history = resolveHistory();
        history.assertEventsOrdered(message1,message2);
        queueForTermination("HistoryAssertOrderedMessages");
    }
}
