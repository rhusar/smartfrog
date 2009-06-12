/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ssh;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

/**
 * Abstract parent class for ScpTo and ScpFrom.
 * @author Ashish Awasthi
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 * 
 */
public abstract class AbstractScpOperation implements ScpProgressCallback {

    protected final byte LINE_FEED = 0x0a;
    protected final int BUFFER_SIZE = 1024;
    protected LogSF log;
    protected ScpProgressCallback progress;
    

    /**
     * This flag is set to halt a child thread at work
     */
    protected volatile boolean haltOperation=false;

    /**
    * Constucts an instance.
     * @param log log a log of the owner
     * @param callback
    */
    protected AbstractScpOperation(LogSF log, ScpProgressCallback callback) {
        this.log = log;
        this.progress = callback;
    }
    /**
     * Write acknowlegement by writing char '0' to output stream of the channel.
     * @param out stream to write to.
     * @throws IOException if unable to write
     */
    protected void writeAck(OutputStream out) throws IOException {
        byte[] buf = new byte[1];
        buf[0] = 0;
        out.write(buf);
        out.flush();
    }


    /**
     * Called when a transfer begins
     *
     * @param localFile  local file name
     * @param remoteFile remote filename
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public void beginTransfer(File localFile, String remoteFile) throws
            SmartFrogException, RemoteException {
        if(progress!=null) {
            progress.beginTransfer(localFile,remoteFile);
        }
    }


    /**
     * Called when a transfer ends
     *
     * @param localFile  local file name
     * @param remoteFile remote filename
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public void endTransfer(File localFile, String remoteFile) throws
            SmartFrogException, RemoteException {
        if (progress != null) {
            progress.endTransfer(localFile, remoteFile);
        }

    }


    /**
     * halt the operation
     */
    public synchronized void haltOperation() {
        haltOperation=true;
    }

    /**
    * Reads server response from channel's input stream.
    * @param in stream to read from
    * @throws IOException if response is an error
    */
    protected void checkAck(InputStream in)throws IOException {
        int svrRsp = in.read();

        /*
         * svrRsp may be 0 for success,
         *          1 for error,
         *          2 for fatal error,
         *         -1 No Response from Server
         */  
        if (svrRsp == -1) {
            // didn't receive any response
            throw new IOException("No response from server");
        } else if (svrRsp != 0) {
            StringBuffer sb = new StringBuffer();
            int ch = in.read();
            while (ch > 0 && ch != '\n') {
                sb.append((char) ch);
                ch = in.read();
            }
            if (svrRsp == 1) {
                throw new IOException(
                        "error reported by server: "+ sb.toString());
            } else if (svrRsp == 2) {
                throw new IOException(
                        "fatal error reported by server: " + sb.toString());
            } else {
                throw new IOException("unknown response, code " + svrRsp
                                         + " message: " + sb.toString());
            }
        }
    }

}

