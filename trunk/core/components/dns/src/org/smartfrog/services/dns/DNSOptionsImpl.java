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

package org.smartfrog.services.dns;


import java.util.Iterator;

import java.util.Vector;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import java.io.PrintWriter;
import org.smartfrog.sfcore.common.Context;




/**
 * A class implementing global options of named.
 *
 * 
 * 
 */
public class DNSOptionsImpl extends DNSComponentImpl implements DNSOptions {


    /** Extra information not "understood" by this component. */
    DNSOpaqueImpl opaque = null;

    /**  Directory where all the config files are.*/
    String directory = null;
    
    /** A vector of IP addresses to forward queries that we cannot resolve
        locally. */
    Vector forwarders = null;

    /** A vector of  interfaces to listen on for requests. */
    Vector listenOn = null;


    // attribute names in the description.
    public static final String DIRECTORY = "configDir";

    public static final String FORWARDERS = "forwarders";

    public static final String LISTEN_ON = "listen-on";

    public static final String OPAQUE = "opaque";

    /**
     * Creates a new <code>DNSOptionsImpl</code> instance.
     *
     */
    public DNSOptionsImpl() {

    // real configuration in sfDeployWith
    }

    /**
     * The "real" constructor of this component. It will recursively
     * try to instantiate and configure  all the components passed in
     * its context.
     *
     * @param parent A parent in the description hierarchy.
     * @param ctx A context from which to obtain arguments to configure
     * this object.
     * @exception SmartFrogException if an error occurs while initializing
     * this component or any of its sub-components.
     */
    public void sfDeployWith(ComponentDescription parent, Context ctx)
        throws SmartFrogDeploymentException {

        super.sfDeployWith(parent, ctx);
  
       if (!isOptions(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " Options type " + ctx);
       }

       directory = getString(ctx, DIRECTORY, null);
       if (directory == null) {
           throw new SmartFrogDeploymentException("No directory name");
       }

       forwarders = getVector(ctx, FORWARDERS, null);
       listenOn = getVector(ctx, LISTEN_ON, null);
       ComponentDescription opaqueCD = getCD(ctx, OPAQUE, null);
       if (opaqueCD != null) {
           opaque = (DNSOpaqueImpl)  deployComponent(this, opaqueCD);
       }
    }


    /**
     * Writes the named.conf contents to the print writer. It propagates 
     * to its sub-components.
     *
     * @param out A print writer to dump the named.conf that bind needs.
     */
    public void printNamedConfig(PrintWriter out) {

        out.println("options {");
        out.println("  directory \"" + directory + "\";");
        if (forwarders != null) {
            out.println("  forwarders "
                       + printAddresses(forwarders)  + ";");
            out.println("  forward only;");
        }

        if (listenOn != null) {
            out.println("  listen-on "
                       + printAddresses(listenOn)  + ";");
        } else {
            out.println("  listen-on  {any;};");
        }
        if (opaque != null) {
            opaque.printNamedConfig(out);
        }
        out.println("};");        
    }

    /**
     * Gets the value of opaque
     *
     * @return the value of opaque
     */
    public DNSOpaqueImpl getOpaque()  {
        return this.opaque;
    }


    /**
     * Gets the value of directory
     *
     * @return the value of directory
     */
    public String getDirectory()  {
        return this.directory;
    }


    /**
     * Gets the value of forwarders
     *
     * @return the value of forwarders
     */
    public Vector getForwarders()  {
        return this.forwarders;
    }

    /**
     * Gets the value of listenOn
     *
     * @return the value of listenOn
     */
    public Vector getListenOn()  {
        return this.listenOn;
    }

}

