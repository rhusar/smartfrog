/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.cddlm.client.console;

import org.apache.axis.message.MessageElement;
import org.cddlm.client.callbacks.CallbackServer;
import org.cddlm.client.common.ServerBinding;
import org.smartfrog.services.cddlm.generated.api.callbacks.DeploymentCallbackEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types._lifecycleEventCallbackRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Date: 16-Sep-2004 Time: 21:03:30
 */
public class Listen extends ConsoleOperation
        implements DeploymentCallbackEndpoint {

    _lifecycleEventCallbackRequest lastMessage;

    int messageCount = 0;

    int timeout = 0;

    public Listen(ServerBinding binding, PrintWriter out, String[] args) {
        super(binding, out);
        bindToCommandLine(args);
    }

    public Listen(ServerBinding binding, PrintWriter out) {
        super(binding, out);
    }


    /**
     * get uri from the command line
     *
     * @param args
     */
    public void bindToCommandLine(String[] args) {
        bindUriToCommandLine(args);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setTimeout(int timeoutSeconds) {
        this.timeout = timeoutSeconds;
    }

    public _lifecycleEventCallbackRequest getLastMessage() {
        return lastMessage;
    }

    /**
     * execute this operation, or throw a remote exception when we return, it is
     * because we timed out
     *
     * @throws java.rmi.RemoteException
     */
    public void execute() throws IOException {
        CallbackServer server = new CallbackServer();
        try {
            server.start();
        } catch (Exception e) {
            throw new WrappedException(null, e);
        }
        String identifier = null;

        try {
            //get our ident
            identifier = CallbackServer.addMapping(this);

            //send a set callback message
            String url = server.getCallbackURL();
            setCddlmCallback(getUri(), url, identifier);

            aboutToWait();
            //now ask for
            try {
                Thread.sleep(timeout * 1000);
            } catch (InterruptedException e) {

            }
            //now unsubscribe
            setUnsubscribeCallback(getUri());
        } finally {
            //shutdown code
            server.stop();
            if (identifier != null) {
                CallbackServer.removeMapping(identifier);
            }
        }
    }

    /**
     * useful little override point for testing the cddlm callback has already
     * been set at this point
     */

    protected void aboutToWait() {

    }

    /**
     * this is our callback event
     *
     * @param callback
     * @return
     * @throws RemoteException
     */
    public synchronized boolean callback(
            _lifecycleEventCallbackRequest callback) throws RemoteException {
        messageCount++;
        lastMessage = callback;
        processCallback(callback);
        this.notifyAll();
        return false;
    }

    /**
     * process a callback by printing it
     *
     * @param callback
     */
    protected void processCallback(_lifecycleEventCallbackRequest callback) {
        BigInteger timestamp = callback.getTimestamp();
        if (timestamp != null) {
            long utc = timestamp.longValue();
            Date date = new Date(utc * 1000);
            out.println("time:   " + date.toString());
        }
        ApplicationStatusType status = callback.getStatus();
        out.println("event:  " + status.getState());
        if (status.getStateInfo() != null) {
            out.println("info :" + status.getStateInfo());
        }
        if (status.getExtendedState() != null) {
            MessageElement[] any = status.getExtendedState().get_any();
            for (int i = 0; i < any.length; i++) {
                out.println(any[i].toString());
            }
        }
        out.println();
    }

    /**
     * if we have a message, return immediately, else suspend till a message
     * arrives
     *
     * @param timeout
     * @return true if there is a message
     * @throws InterruptedException
     */
    public synchronized boolean blockForMessages(long timeout)
            throws InterruptedException {
        if (messageCount <= 0) {
            wait(timeout);
        }
        return messageCount > 0;
    }

    /**
     * entry point for this command line
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        boolean success = innerMain(args);
        exit(success);
    }

    public static boolean innerMain(String[] args) {
        ServerBinding server;
        ConsoleOperation operation;
        boolean success = false;
        final PrintWriter pw = new PrintWriter(System.out);
        try {
            server = extractBindingFromCommandLine(args);
            operation = new Listen(server, pw, args);
            success = operation.doExecute();
        } catch (Throwable e) {
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }

    public static class WrappedException extends IOException {

        public WrappedException(String s) {
            super(s);
        }

        public WrappedException(String s, Throwable cause) {
            super(s);
            initCause(cause);
        }

    }
}
