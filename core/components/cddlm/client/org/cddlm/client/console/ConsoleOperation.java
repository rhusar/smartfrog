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
package org.cddlm.client.console;

import org.apache.axis.types.URI;
import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.generated.api.endpoint.CddlmSoapBindingStub;
import org.cddlm.client.generated.api.types.ApplicationReferenceListType;
import org.cddlm.client.generated.api.types.ApplicationStatusType;
import org.cddlm.client.generated.api.types.EmptyElementType;
import org.cddlm.client.generated.api.types.ServerStatusType;
import org.cddlm.client.generated.api.types._applicationStatusRequest;
import org.cddlm.client.generated.api.types._serverStatusRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * base class for console operations created Aug 31, 2004 4:44:30 PM
 */

public abstract class ConsoleOperation {

    /**
     * our output stream
     */
    protected PrintWriter out;

    /**
     * our server binding
     */
    protected ServerBinding binding;


    /**
     * stub. this is only valid w
     */
    private CddlmSoapBindingStub stub;

    /**
     * demand create our stub. retain it afterwards for reuse.
     *
     * @return
     * @throws RemoteException
     */
    public CddlmSoapBindingStub getStub() throws RemoteException {
        if (stub == null) {
            out.println("Connecting to " + binding.toString());
            stub = binding.createStub();
        }
        return stub;
    }

    public ConsoleOperation(ServerBinding binding, PrintWriter out) {
        this.out = out;
        this.binding = binding;
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws RemoteException
     */
    public abstract void execute() throws RemoteException;

    /**
     * log a throwable to the output stream
     *
     * @param t
     */
    public void logThrowable(Throwable t) {
        t.printStackTrace(out);
        out.flush();
    }

    /**
     * execute; log exceptions to the stream
     *
     * @return true if it worked, false if not
     */
    public boolean doExecute() {
        try {
            execute();
            out.flush();
            return true;
        } catch (RemoteException e) {
            logThrowable(e);
            return false;
        }
    }

    /**
     * TODO: parse command line looking for values
     *
     * @param args command line arguments; look for -url url
     * @return
     */
    public static ServerBinding extractBindingFromCommandLine(String[] args)
            throws IOException {
        ServerBinding extractedBinding = ServerBinding.fromCommandLine(args);
        if (extractedBinding == null) {
            extractedBinding = ServerBinding.createDefaultBinding();
        }
        return extractedBinding;
    }


    /**
     * list all applications
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public URI[] listApplications() throws RemoteException {
        EmptyElementType empty = new EmptyElementType();
        ApplicationReferenceListType list = getStub().listApplications(empty);
        URI apps[] = list.getApplication();
        if (apps == null) {
            apps = new URI[0];
        }
        return apps;
    }

    /**
     * get the status of an application
     *
     * @param app
     * @return
     * @throws java.rmi.RemoteException
     */
    ApplicationStatusType queryApplicationStatus(URI app)
            throws RemoteException {
        _applicationStatusRequest request = new _applicationStatusRequest();
        request.setApplication(app);
        ApplicationStatusType status = getStub().applicationStatus(request);
        return status;
    }

    /**
     * get the status
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public ServerStatusType getStatus() throws RemoteException {
        _serverStatusRequest request = new _serverStatusRequest();
        ServerStatusType status = getStub().serverStatus(request);
        return status;
    }

    /**
     * exit, use success flag to choose the return time. This method does not
     * return
     *
     * @param success success flag
     */
    static void exit(boolean success) {
        Runtime.getRuntime().exit(success ? 0 : -1);
    }
}
