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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Class to upload files to a remote host over SSH Session.
 * @author Ashish Awasthi
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 * 
 */
public class ScpTo extends AbstractScpOperation {

    /**
     * Constucts ScpTo using log object.
     * @param sfLog log to use
     */
    public ScpTo(LogSF sfLog) {
        super(sfLog);
    }
    /**
     * Uploads files to a remote machine.
     * @param session to use
     * @param remoteFiles vector of remote file names
     * @param localFiles vector of corresponding local file names
     * @throws IOException in case not able to transfer files
     */
    public void doCopy (Session session, Vector remoteFiles, 
           Vector<File> localFiles) throws IOException, JSchException {
        String cmdPrefix = "scp -t ";
        for (int index = 0; index < remoteFiles.size(); index++) {
            if (haltOperation) {
                throw new InterruptedIOException();
            }
            Channel channel = null;
            try {
                File localFile = localFiles.elementAt(index);
                String remoteFile = (String) remoteFiles.elementAt(index);
                channel = session.openChannel("exec");
                String command = cmdPrefix + remoteFile.trim();
                log.info ("Scp command := " + command);
                ((ChannelExec) channel).setCommand(command);
                // get I/O streams from channel
                OutputStream out = channel.getOutputStream();
                InputStream in = channel.getInputStream();
                channel.connect();
                checkAck(in);
                doScpTo(in, out, localFile);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
        }
    }
    /**
     * Use scp to copy file to the remote host.
     * @param in Input Stream of the channel
     * @param out Output Stream of the channel
     * @param localFile local file name
     */
    private void doScpTo(InputStream in, OutputStream out, 
                            File localFile) throws IOException {
        int fileSize = (int) localFile.length();
        String lFilePart=localFile.getName();
        StringBuffer cmdBuff = new StringBuffer("C0644")
                                    .append(" ")
                                    .append(fileSize)
                                    .append(" ")
                                    .append(lFilePart)
                                    .append("\n");
        // send command over OutputStream
        String cmd = cmdBuff.toString();
        log.info ("Writing "+ cmd + " to output stream");
        
        out.write(cmd.getBytes());
        out.flush();
        checkAck(in);
        sendFile(localFile, in, out);
    }
    /**
     * Writes file content to output stream of the ssh channel.
     * The {@link #haltOperation} attribute is checked during the operation,
     * so that the operation can be interrupted -in which case a
     * @throws IOException in case of any error while writing
     * @throws InterruptedIOException if the operation was halted
     * @param file file to send
     * @param in input stream for acknowledgements
     * @param out output stream outpu stream
     */
    private void sendFile (File file, InputStream in, OutputStream out)
                                 throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            byte[] buf=new byte[BUFFER_SIZE];
            log.info ("Sending "+ file.getName() + " of size: "+ file.length());
            while(!haltOperation) {
                int bytesRead = fis.read(buf, 0, buf.length);
                if(bytesRead <= 0) {
                    break;
                }
                out.write(buf, 0, bytesRead); 
            }
            if (haltOperation) {
                throw new InterruptedIOException();
            }
            out.flush();
            writeAck(out);
            checkAck(in);
        } finally {
            FileSystem.close(fis);
        }
    }
}

