/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.partition.comms.nonblocking;

import java.io.IOException;

import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import org.smartfrog.services.anubis.partition.wire.WireFormException;
import org.smartfrog.services.anubis.partition.wire.msg.HeartbeatMsg;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

public class NonBlockingConnectionInitiator {

    private MessageConnection connection = null;
//    private byte[] heartbeat = null;   // SECURITY
    private HeartbeatMsg heartbeat = null;
    private LogSF  log       = LogFactory.getLog(this.getClass().toString());

    public NonBlockingConnectionInitiator(MessageConnection con, HeartbeatMsg hb) throws IOException,
        WireFormException {
        connection = con;
//        heartbeat = hb.toWire();  // SECURITY
        heartbeat = hb;
    }


    public void finishNioConnect(MessageNioHandler impl) {

        if ( (impl.isReadyForWriting()) && (impl.connected())) {
            impl.send(heartbeat);
        }
        else {
            if( log.isErrorEnabled() )
                log.error("MCI: can't send first heartbeat!!!");
        }

        /**
         * If the implementation is successfully assigned then start its thread
         * - otherwise call terminate() to shutdown the connection. The impl
         * will not be accepted if the heartbeat protocol has terminated the
         * connection during the time it took to establish it.
         */
        if (connection.assignImpl(impl)) {
            impl.start();
        }
        else {
            impl.terminate();
        }
    }

}
