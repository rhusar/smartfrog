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
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;

import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * created Sep 1, 2004 4:40:07 PM
 */

public class ListApplications extends ConsoleOperation {
    public static final String DEPLOYED_TEXT = "Applications deployed = ";


    public ListApplications(ServerBinding binding, PrintWriter out) {
        super(binding, out);
    }

    public void execute() throws RemoteException {

        URI[] apps = listApplications();

        int length = 0;

        length = apps == null ? 0 : apps.length;

        out.println(DEPLOYED_TEXT + length);
        for (int i = 0; i < length; i++) {
            final URI app = apps[i];
            out.println(" " + app.toString());
            ApplicationStatusType status = lookupApplicationStatus(app);
            out.println("   name: " + status.getName().toString());
            out.println("   state: " + status.getState().toString());
        }
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
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }
}
