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

import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.generated.api.endpoint.CddlmSoapBindingStub;

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
    }

    /**
     * execute; log exceptions to the stream
     *
     * @return true if it worked, false if not
     */
    public boolean doExecute() {
        try {
            execute();
            return true;
        } catch (RemoteException e) {
            logThrowable(e);
            return false;
        } 
    }
}
