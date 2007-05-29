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
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * created Sep 1, 2004 5:34:02 PM
 */

public class Deploy extends ConsoleOperation {

    private String name;

    private File sourceFile;

    private DeploymentDescriptorType descriptor;

    public static final String ERROR_NO_FILE_ARGUMENT = "No file specified";
    public static final String ERROR_NO_FILE_FOUND = "File not found: ";

    public Deploy(ServerBinding binding, PrintWriter out) {
        super(binding, out);
    }

    public Deploy(ServerBinding binding, PrintWriter out, String[] args) {
        super(binding, out);
        bindToCommandLine(args);
    }

    public DeploymentDescriptorType getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(DeploymentDescriptorType descriptor) {
        this.descriptor = descriptor;
    }

    public void createDeploymentDescriptor() throws IOException {

    }

    /**
     * get uri and reason from the command line
     *
     * @param args
     */
    public void bindToCommandLine(String[] args) {
        String filename = getFirstNonNullElement(args);
        if (filename == null) {
            throw new BadCommandLineException(ERROR_NO_FILE_ARGUMENT);
        }
        sourceFile = new File(filename);
        if (!sourceFile.exists()) {
            throw new BadCommandLineException(ERROR_NO_FILE_FOUND + filename);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }


    public void execute() throws IOException {
        createDeploymentDescriptor();
        uri = deploy(name, descriptor, null, null);
        out.print("Deployed to uri: " + uri);
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
            operation = new Deploy(server, pw, args);
            success = operation.doExecute();
        } catch (Throwable e) {
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }

}
