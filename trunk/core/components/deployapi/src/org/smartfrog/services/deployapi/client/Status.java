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
package org.smartfrog.services.deployapi.client;

import org.ggf.xbeans.cddlm.api.DescriptorType;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.sfcore.languages.cdl.utils.ElementsIterator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import nu.xom.ParsingException;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Node;
import nu.xom.Serializer;
import nu.xom.Document;

/**
 * created Sep 1, 2004 5:34:02 PM
 */

public class Status extends ConsoleOperation {
    public static final String XPATH_STATUS = "api:StaticPortalStatus";


    public static final String ERROR_NO_FILE_ARGUMENT = "No file specified";
    public static final String ERROR_NO_FILE_FOUND = "File not found: ";

    public Status(PortalEndpointer endpointer, PrintWriter out) {
        super(endpointer, out);
    }

    public Status(PortalEndpointer endpointer, PrintWriter out, String[] args) {
        super(endpointer, out);
        bindToCommandLine(args);
    }

    /**
     * get uri and reason from the command line
     *
     * @param args
     */
    public void bindToCommandLine(String[] args) {

    }


    public void execute() throws IOException {
        Element status = getPortalPropertyXom(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        File tempFile=File.createTempFile("status",".xml");
        FileOutputStream os = new FileOutputStream(tempFile);
        Serializer ser=new Serializer(os);
        ser.setIndent(2);
        ser.setMaxLength(80);
        Document doc=new Document(status);
        ser.write(doc);
        ser.flush();
        os.close();
        String contents = Utils.loadFile(tempFile, Constants.CHARSET_UTF8);
        out.println(contents);
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
        PortalEndpointer server;
        ConsoleOperation operation;
        boolean success = false;
        final PrintWriter pw = new PrintWriter(System.out);
        try {
            server = extractBindingFromCommandLine(args);
            operation = new Status(server, pw, args);
            success = operation.doExecute();
        } catch (Throwable e) {
            processThrowableInMain(e, pw);
            success = false;
        }
        pw.flush();
        return success;
    }

}
