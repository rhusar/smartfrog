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

import org.apache.axis.message.MessageElement;
import org.cddlm.client.common.ServerBinding;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLAnyNamespace;

import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * created Sep 15, 2004 12:01:50 PM
 */

public class ApplicationStatus extends ConsoleOperation {

    public ApplicationStatus(ServerBinding binding, PrintWriter out,
            String[] args) {
        super(binding, out);
        bindUriToCommandLine(args);
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws RemoteException
     */
    public void execute() throws RemoteException {
        ApplicationStatusType status = lookupApplicationStatus(uri);
        out.println("uri:    " + status.getReference());
        out.println("name:   " + status.getName());
        out.println("status: " + status.getState());
        out.println("info: " + status.getStateInfo());
        NotificationInformationType notificationInfo = status.getNotification();
        if (notificationInfo != null) {
            out.println("callback: " + notificationInfo.getType());
            /*
            MesnotificationInfo.getSubscription().get_any();
            NotificationAddressType address = notificationInfo.getAddress();
            if (address != null) {
                if (address.getUri() != null) {
                    out.println("url:      " + address.getUri());
                }
            }
            */
            out.println("identifier:" + notificationInfo.getIdentifier());
        }
        UnboundedXMLAnyNamespace extendedState = status.getExtendedState();
        if (extendedState != null) {
            MessageElement[] any = extendedState.get_any();
            for (int i = 0; i < any.length; i++) {
                String s = any[i].toString();
                out.println(s);
            }
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
            operation = new ApplicationStatus(server, pw, args);
            success = operation.doExecute();
        } catch (Throwable e) {
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }

}
