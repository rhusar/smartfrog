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

import nu.xom.Element;
import nu.xom.Nodes;
import org.apache.axis2.addressing.EndpointReference;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.services.deployapi.system.Constants;

import javax.xml.namespace.QName;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


/** base class for console operations created Aug 31, 2004 4:44:30 PM */

public abstract class ConsoleOperation {

    /** our output stream */
    protected PrintWriter out;

    /** our server binding */
    protected PortalEndpointer portal;


    public static final String SMARTFROG_VERSION = "1.0";
    protected URI uri;
    public static final String NO_URI_FOUND = "No application URI";
    public static final String INVALID_URI = "Invalid URI:";


    public ConsoleOperation(PortalEndpointer endpointer, PrintWriter out) {
        this.out = out;
        this.portal = endpointer;
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws java.rmi.RemoteException
     */
    public abstract void execute() throws IOException;

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
        } catch (Exception e) {
            //logThrowable(e);
            processThrowableInMain(e, out);
            return false;
        }
    }

    
    
    public PortalEndpointer getPortal() {
        return portal;
    }

    /**
     * @param args command line arguments; look for -url url
     * @return
     */
    public static PortalEndpointer extractBindingFromCommandLine(String[] args)
            throws IOException {
        PortalEndpointer extractedEndpointer = PortalEndpointer.fromCommandLine(
                args);
        if (extractedEndpointer == null) {
            extractedEndpointer = PortalEndpointer.createDefaultBinding();
        }
        return extractedEndpointer;
    }

    /**
     * list all applications
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public List<EndpointReference> listSystems() throws RemoteException {
        Element graph = getPortalPropertyXom(Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);
        Nodes systems = graph.query("api:ActiveSystems/api:system",
                Constants.XOM_CONTEXT);
        List<EndpointReference> apps = new ArrayList<EndpointReference>(systems.size());
        for (int i = 0; i < systems.size(); i++) {
            Element job = (Element) systems.get(i);
            EndpointReference epr = EprHelper.XomWsa2003ToEpr(job);
            apps.add(epr);
        }
        return apps;
    }




    /**
     * create an application
     *
     * @return info about a destination
     * @throws java.rmi.RemoteException
     */
    public SystemEndpointer create(String hostname)
            throws IOException {
        return portal.create(hostname);
    }


    /**
     * add a name option to our options
     *
     * @param options
     * @param name
     * @throws RuntimeException if we cannot turn
     *                          into a URI
     */
/*
    public static void addNameOption(OptionType options, String name) {
        try {
            OptionType o = options.createNamedOption(
                    new URI(DeployApiConstants.OPTION_NAME), true);
            o.setString(name);
        } catch (URI.MalformedURIException e) {
            throw new RuntimeException(e);
        }
    }
*/


    /**
     * helper to read into a string
     *
     * @param in
     * @return
     * @throws java.io.IOException
     */
    public static String readIntoString(InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        StringWriter dest = new StringWriter();
        char[] block = new char[1024];
        int read;
        while (((read = reader.read(block)) >= 0)) {
            dest.write(block, 0, read);
        }
        dest.flush();
        return dest.toString();
    }

    /**
     * helper to read into a string
     *
     * @param file file to read
     * @return
     * @throws java.io.IOException
     */
    public static String readIntoString(File file) throws IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            String source = readIntoString(in);
            return source;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                ///ignore
            }
        }
    }



    /**
     * look up an application against the server
     *
     * @param id id of app
     * @return URI of the app
     */

    public SystemEndpointer lookupSystem(String id) throws RemoteException {
        return portal.lookupSystem(id);
    }


    /**
     * Get a property from the destination
     *
     * @return a Xom graph of the result
     * @throws RemoteException
     */
    public Element getPortalPropertyXom(QName property)
            throws RemoteException {
        return getPortal().getPropertyXom(property);
    }

    /**
     * exit, use success flag to choose the return time. This method does not
     * return
     *
     * @param success success flag
     */
    protected static void exit(boolean success) {
        Runtime.getRuntime().exit(success ? 0 : -1);
    }


    /**
     * print out a fault
     *
     * @param exception
     */
    public static void processThrowableInMain(Throwable exception,
                                              PrintWriter out) {
        if (exception instanceof BadCommandLineException) {
            out.println(exception.getMessage());
        } else {
            exception.printStackTrace(out);
        }
    }

    /**
     * get the first non null element; set it to null
     *
     * @param args
     * @return null for no match
     */
    public static String getFirstNonNullElement(final String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                String elt = args[i];
                args[i] = null;
                return elt;
            }
        }
        return null;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * assume first non-empty command line (after the server binding) is a URI;
     * extract it and set our URI value
     *
     * @param args
     */
    protected void bindUriToCommandLine(String[] args) {
        String appURI = getFirstNonNullElement(args);
        if (appURI == null) {
            throw new BadCommandLineException(NO_URI_FOUND);
        }
        try {
            uri = new URI(appURI);
        } catch (URISyntaxException e) {
            throw new BadCommandLineException(INVALID_URI + appURI);
        }
    }

}
