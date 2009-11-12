/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This class can be used to issue SSH commands to a remote host; interpretation of the commands is
 * an exercise for the reader
 */

public class SshCommand extends AbstractSshOperation {

    public SshCommand(LogSF log, ScpProgressCallback callback) {
        super(log, callback);
    }
    

    private static final int SPIN_DELAY_MILLIS = 500;


    /**
     * execute a list of commands
     *
     * @param session      session to work with
     * @param commandsList commands to exec
     * @param outputStream an output stream to push work to. This is closed at the end of the operation
     * @param timeout      timeout in millis
     * @return the exit code of the operation
     * @throws JSchException          SSH problems
     * @throws IOException            other problems
     * @throws InterruptedIOException if the operation was interrupted
     */
    public int execute(Session session, List<String> commandsList,
                       OutputStream outputStream,
                       int timeout)
            throws JSchException, IOException {
        ChannelShell channel = null;
        String sessionInfo = getSessionInfo(session);
        try {
            // open ssh session

            // Execute commands

            StringBuilder buffer = new StringBuilder();
            for (Object aCommandsList : commandsList) {
                String cmd = aCommandsList.toString();
                buffer.append(cmd);
                buffer.append('\n');
            }

            byte[] bytes = buffer.toString().getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            channel = openShellChannel(session);
            channel.setOutputStream(outputStream);
            channel.setExtOutputStream(outputStream);
            channel.setInputStream(bais);
            channel.connect();

            if(log.isDebugEnabled()) {
                log.debug("Executing commands:" + buffer.toString());
            }

            // wait for it to finish. This is pretty ugly
            long timeLimit = System.currentTimeMillis() + timeout;
            while (!channel.isClosed() && !haltOperation) {
                long now = System.currentTimeMillis();
                if (timeout > 0 && now > timeLimit) {
                    //we have a timeout
                    String message = AbstractSSHComponent.TIMEOUT_MESSAGE + sessionInfo;
                    log.error(message);
                    throw new IOException(message);
                }
                try {
                    Thread.sleep(SPIN_DELAY_MILLIS);
                } catch (InterruptedException e) {
                    throw new InterruptedIOException("Interrupted while waiting for the end of the commands sent to "
                            + sessionInfo);

                }
            }
            //seee if we've been asked to die
            checkForHalted();

            return channel.getExitStatus();
        } finally {
            //clean up time
            if (channel != null) {
                closeChannel(channel);
            } else {
                //if there's no channel, we may not have closed the output stream
                FileSystem.close(outputStream);
            }
        }
    }

}
