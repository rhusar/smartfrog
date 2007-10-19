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
import org.smartfrog.services.cddlm.generated.api.types.DynamicServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListTypeLanguage;
import org.smartfrog.services.cddlm.generated.api.types.ServerInformationType;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;

import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * created Aug 31, 2004 4:42:42 PM
 */

public class ShowServerStatus extends ConsoleOperation {

    public ShowServerStatus(ServerBinding binding, PrintWriter out) {
        super(binding, out);
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws java.rmi.RemoteException
     */
    public void execute() throws RemoteException {
        ServerStatusType status = getStatus();
        StaticServerStatusType statInfo = status.get_static();
        DynamicServerStatusType dynInfo = status.getDynamic();
        ServerInformationType serverInfo = statInfo.getServer();
        out.println("server :" +
                serverInfo.getName() +
                " at " +
                serverInfo.getLocation());
        out.println("UTC offset " + serverInfo.getTimezoneUTCOffset());
        out.println("Build " + serverInfo.getBuild());
        String callbacks[] = statInfo.getNotifications().getItem();
        out.println("Callbacks: " + callbacks.length + " :-");
        for (int i = 0; i < callbacks.length; i++) {
            out.println("  " + callbacks[i]);
        }
        out.println();
        LanguageListTypeLanguage languages[] = statInfo.getLanguages()
                .getLanguage();
        out.println("Languages: " + languages.length + " :-");
        for (int i = 0; i < languages.length; i++) {
            final LanguageListTypeLanguage language = languages[i];
            out.println("  " +
                    language.getName()
                    +
                    "/" +
                    language.getVersion()
                    + " ::= " + language.getUri());
        }
        out.println();
        String options[] = statInfo.getOptions().getItem();
        out.println("Options: " + options.length + " :-");
        for (int i = 0; i < options.length; i++) {
            out.println("  " + options[i]);
        }
        out.println();

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
        ShowServerStatus operation;
        boolean success = false;
        final PrintWriter pw = new PrintWriter(System.out);
        try {
            server = extractBindingFromCommandLine(args);
            operation = new ShowServerStatus(server, pw);
            success = operation.doExecute();
        } catch (Throwable e) {
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }

}
