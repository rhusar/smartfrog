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

import java.io.PrintWriter;
import java.io.File;
import java.rmi.RemoteException;

/**
 * created Sep 1, 2004 5:34:02 PM
 */

public class Deploy extends ConsoleOperation {

    String name;

    File sourceFile;

    public Deploy(ServerBinding binding, PrintWriter out) {
        super(binding, out);
    }

    public void execute() throws RemoteException {

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
            operation = new ListApplications(server, pw);
            success = operation.doExecute();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            success = false;
        }
        pw.flush();
        return success;
    }
}
