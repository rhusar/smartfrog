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

import java.io.IOException;
import java.io.PrintWriter;

/**
 * created Sep 15, 2004 1:41:46 PM
 */

public class DeployXMLFile extends Deploy {


    public DeployXMLFile(ServerBinding binding, PrintWriter out, String[] args) {
        super(binding, out, args);
    }


    public void createDeploymentDescriptor() throws IOException {

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
            operation = new DeploySmartFrogFile(server, pw, args);
            success = operation.doExecute();
        } catch (Throwable e) {
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }

}
