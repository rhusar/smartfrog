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

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class to copy securely from a remote machine. 
 * @author Ashish Awasthi
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 * 
 */
public class ScpFrom extends AbstractScpOperation {

    /**
     * Constucts ScpFrom using log object.
     * @param sfLog log
     */
    public ScpFrom(LogSF sfLog) {
        super(sfLog);
    }
    /**
     * Downloads files from a remote machine.
     * @param session current session
     * @param remoteFiles vector of remote file names
     * @param localFiles vector of corresponding local file names
     * @throws IOException in case not able to transfer files
     * @throws JSchException for JSCH problems.
     */
    public void doCopy (Session session, Vector remoteFiles,Vector<File> localFiles)
                                 throws IOException, JSchException {
        String cmdPrefix = "scp -f ";
        for (int index = 0; index < remoteFiles.size(); index++) {
            if (haltOperation) {
                throw new InterruptedIOException();
            }
            Channel channel = null;
            try {
                File localFile = localFiles.elementAt(index);
                String remoteFile = (String) remoteFiles.elementAt(index);
                channel = session.openChannel("exec");
                String command = cmdPrefix + remoteFile;
                ((ChannelExec) channel).setCommand(command);
                // get I/O streams from channel
                OutputStream out = channel.getOutputStream();
                InputStream in = channel.getInputStream();
                channel.connect();
                writeAck(out);
                doScpFrom(in, out, localFile);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
        }
    }
    
    /**
     * Copies file from the remote host.
     * @param in Input Stream of the channel
     * @param out Output Stream of the channel
     * @param localFile local file
     * @throws IOException for errors on the server, or in writing the file. An InterruptedIOException is thrown
     * if the operation was halted.
     */
    private void doScpFrom(InputStream in, OutputStream out, 
                        File localFile) throws IOException {
        while (!haltOperation) {
            ByteArrayOutputStream arr = new ByteArrayOutputStream();
            // read server response from input stream
            while (!haltOperation) {
                int read = in.read();
                if (read < 0) {
                    return;
                }
                if ((byte) read == LINE_FEED) {
                    break;
                }
                arr.write(read);
            }
            if(haltOperation) {
                throw new InterruptedIOException();
            }
            String serverResponse = arr.toString();
            log.info(serverResponse);
            // Server responds to channel connect 
            // as - <cmd code> <file size> <file name> 
            if (serverResponse.charAt(0) == 'C') {
                // Sucessful
                StringTokenizer token = new StringTokenizer(serverResponse);
                String cmd  = token.nextToken();
                int filesize = Integer.parseInt(token.nextToken());
                String filename = token.nextToken();
                log.info("Receiving " + filename + "of size : " + filesize);
                writeAck(out);
                getFile(localFile, filesize, out, in);
                checkAck(in);
                writeAck(out);
            } else if (serverResponse.charAt(0) == '\01'
                    || serverResponse.charAt(0) == '\02') {
                // error.
                throw new IOException(serverResponse.substring(1));
            }
        }
    }
    /**
     * Reads remote file from stream and writes to local file.
     * <p/>
     * After the operation, both the out and in streams are left open.
     * @param localFile destination
     * @param filesize size of file to read
     * @param out output stream of the communications
     * @param in input stream (which is used as the source)
     * @throws IOException if unable to read file or server reponse is an error
     */
    private void getFile(File localFile,
                            int filesize,
                            OutputStream out,
                            InputStream in) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        FileOutputStream fos = new FileOutputStream(localFile);
        int bytesRead;
        try {
            while (true) {
                // read file in the chunk of 1 kb
                bytesRead = in.read(buf, 0,
                        (buf.length < filesize) ? buf.length : filesize);
                if (bytesRead < 0) {
                    throw new EOFException("Unexpected end of stream.");
                }
                fos.write(buf, 0, bytesRead);
                filesize -= bytesRead;
                if (filesize == 0) {
                    break;
                }
            }
        } finally {
            FileSystem.close(fos);
        }
    }
}

