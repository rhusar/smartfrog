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


import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;


/**
 * Implements a "master" or "forward" DNS zone.
 *
 * 
 * 
 */
public class DNSZoneImpl extends DNSComponentImpl implements DNSZone {

    /**  The name of the zone.*/
    String zoneName = null;
    
    /**  The name of the view this zone is in.*/
    String viewName = null;

    /** the host name that will appear in the NS records as 
     *  master of the zone. */
    String nameServerHostName = null;

    /** Whether we allow dynamic updates for this zone.*/
    boolean allowUpdate = true;

    /** Whether we have a forward instead of master zone.*/
    boolean isForwarding = false;

    /**  A vector of addresses we forward to.*/
    Vector forwarders = null;

    /**  Whether this a reverse mapping zone.*/
    boolean isReverse = false;
    
    /** Whether this zone should be considered "default".*/
    boolean isDefault = false;

    /** Standard SOA record time limit. */
    int refresh = 28800;

    /** Standard SOA record time limit. */
    int retry = 7200;

   /** Standard SOA record time limit. */
    int expire = 604800;

    /** Standard SOA record time limit. */
    int ttl = 86400;

    /** A name server that has not been set. */
    public static final String DEFAULT_NAME_SERVER_HOST_NAME = "default."; 

    // all the attribute names in the description...
    public static final String ZONE_NAME = "zoneName";
    public static final String VIEW_NAME = "viewName";
    public static final String NAME_SERVER_HOST_NAME = "nameServerHostName";
    public static final String ALLOW_UPDATE = "allowUpdate";
    public static final String IS_FORWARDING = "isForwarding";
    public static final String FORWARDERS = "forwarders";
    public static final String IS_REVERSE = "isReverse";
    public static final String SET_AS_DEFAULT_ZONE = "setAsDefaultZone";
    public static final String REFRESH = "refresh";
    public static final String RETRY = "retry";
    public static final String EXPIRE = "expire";
    public static final String TTL = "ttl";


    public DNSZoneImpl() {
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
        if (!isZone(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " Zone type " + ctx);
        }

        zoneName = getString(ctx, ZONE_NAME, null);
        if (zoneName == null) {
            throw new SmartFrogDeploymentException("No zone name");
        }
        viewName = getString(ctx, VIEW_NAME, null);
        if (viewName == null) {
            throw new SmartFrogDeploymentException("No view name");
        }
   
        nameServerHostName = getString(ctx, NAME_SERVER_HOST_NAME,
                                       DEFAULT_NAME_SERVER_HOST_NAME);
        // always an absolute name
        nameServerHostName = (nameServerHostName.endsWith(".") 
                              ? nameServerHostName
                              : nameServerHostName + ".");
        
        allowUpdate = getBoolean(ctx, ALLOW_UPDATE, allowUpdate);
        isForwarding = getBoolean(ctx, IS_FORWARDING, isForwarding);
        if (isForwarding) {
            forwarders = getVector(ctx, FORWARDERS, null);
            if (forwarders == null) {
                throw new SmartFrogDeploymentException("No forwarders");
            }
        }
        isReverse = getBoolean(ctx, IS_REVERSE, isReverse);
        isDefault =  getBoolean(ctx, SET_AS_DEFAULT_ZONE, isDefault);
        refresh = getInteger(ctx, REFRESH, refresh);
        retry = getInteger(ctx, RETRY, retry);
        expire = getInteger(ctx, EXPIRE, expire);        
        ttl = getInteger(ctx, TTL, ttl);

        // add the bindings...
        for (Iterator iter = ctx.sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof ComponentDescription) {
                ComponentDescription cd = (ComponentDescription) obj;
                if (isBinding(cd.sfContext())) {
                    replaceBindingFromDescription(cd);
                }
            }
        }
    }


     /**
     * Constructs from description and replaces a new binding as child of this
     * zone  component. If it is not present it is just added. We gave it
     * an attribute equal to its name.
     *
     * @param cd  A component description for the binding data object.
     * @return The old binding with the same name or null.
     * @exception SmartFrogDeploymentException if an error occurs
     * while deploying binding.
     */
    public DNSBindingIP replaceBindingFromDescription(ComponentDescription cd) 
        throws SmartFrogDeploymentException {

        DNSBindingIP binding = (DNSBindingIP) deployComponent(this, cd);
        return replaceBinding(binding);
    }

    /**
     * Replaces a new binding object as child of this component.
     * If it is not present it is just added.  We gave it
     * an attribute equal to its name.
     *
     * @param binding  A binding data object that replaces/adds.
     * @return The old binding with the same name or null.
     */
    public synchronized DNSBindingIP replaceBinding(DNSBindingIP binding) {

        binding.setParent(this);
        DNSBindingIP old = (DNSBindingIP) sfContext().put(binding.getName(),
                                                         binding);
        return old;
    }
   
    /**
     * Replaces the equivalent component in a hierarchy defined
     * by the given root by this component. 
     *
     * @param root A top level component of the hierarchy that we
     * want to modify.
     * @exception DNSModifierException if an error occurs while 
     * modifying the hierarchy.
     */
    public void replace(DNSComponent root)
        throws DNSModifierException {

        try {
            Reference ref = new Reference();
            ref.addElement(ReferencePart.here(getViewName()));
            DNSView rootView = (DNSView) root.sfResolve(ref);
            rootView.replaceZone(this);
        } catch (Exception e) {
            throw new DNSModifierException("can't replace zone", e);
        }
    }

    /**
     * Writes the named.conf contents to the print writer. It propagates 
     * to its sub-components.
     *
     * @param out A print writer to dump the named.conf that bind needs.
     */
    public void printNamedConfig(PrintWriter out) {
        
        out.println("zone \"" + zoneName + "\" {");
        if (isForwarding) {
            out.println("  type forward;");
            out.println("  forwarders "
                       + printAddresses(forwarders)  + ";");
            out.println("  forward only;");
         } else {
            
            out.println("  type master;");
            if (allowUpdate) {
                out.println("  allow-update {any;};");
            }
            out.println("  file \""+ getFileName() + "\";");
        }
        out.println("};"); 
    }

    /**
     * Returns a pointer to the parent view or null if it has not 
     *  been inserted into a view yet.
     *
     * @return A pointer to the parent view or null if it has not 
     *  been inserted into a view yet.
     */
    public DNSView getEnclosingView() {

        if (sfParent() instanceof DNSView) {
            return (DNSView) sfParent();
        }
        return null;
    }


    /**
     * A file name for the SOA record.
     *
     * @return A file name for the SOA record.
     */
    public String getFileName() {

        return viewName + "-" + zoneName + ".zone";
    }

    /**
     * Gets the name of the zone.
     *
     * @return The name of the zone.
     */
    public String getName() {
        
        return zoneName;
    }

    /**
     * Gets the name of the view this zone is in.
     *
     * @return The name of the view this zone is in.
     */
    public String getViewName() {

        return viewName;
    }

    /**
     * Gets the host name that will appear in the NS records as 
     *  master of the zone. If it has not been set we default to 
     * the one common to all the views/zones.
     *
     * @return The host name that will appear in the NS records as 
     *  master of the zone.
     *
     */
    public String getNameServerHostName() {
        
        if (DEFAULT_NAME_SERVER_HOST_NAME.equals(nameServerHostName)) {
            try {
                DNSData top =  getEnclosingView().getEnclosingData();
                nameServerHostName = top.getNameServerHostName();
            } catch (Exception e) {
                System.out.println("cannot getNameServerHostName" + e);
                return null;
            }
        }
        return nameServerHostName;
    }

    /**
     * Whether we allow dynamic updates for this zone.
     *
     * @return True if  we allow dynamic updates for this zone.
     */
    public boolean getAllowUpdate() {

        return allowUpdate;
    }

    /**
     * Whether we have a forward instead of master zone.
     *
     * @return True if we have a forward instead of master zone.
     */
    public boolean isForwarding() {

        return isForwarding;
    }

    /**
     * Gets a vector of addresses we forward to (if we don't master).
     *
     * @return A vector of addresses we forward to.
     */
    public Vector getForwarders() {

        return forwarders;
    }

    /**
     * Whether this a reverse mapping zone.
     *
     * @return True if this is a reverse mapping zone.
     */
    public boolean isReverse() {
        
        return isReverse;
    }

    
    /**
     * Whether this zone should be considered "default".
     *
     * @return True if  this zone should be considered "default".
     */
    public boolean isDefault() {

        return isDefault;
    }


    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
    public int getRefresh() {

        return refresh;
    }

    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
    public int getRetry() {

        return retry;
    }

    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
    public int getExpire() {
        
        return expire;
    }


    /**
     * Gets a standard SOA record time limit.
     *
     * @return A standard SOA record time limit.
     */
    public int getTTL() {
        
        return ttl;
    }


    /**
     * Returns an object that implements a visitor pattern to write
     * all the SOA records  of zones in a hierarchy.
     *
     * @param dir A directory where to write the record.
     * @param overwrite True if we overwrite existing files.
     * @return An object that implements a visitor pattern to write
     * all the SOA records  of zones in a hierarchy.
     */
    public static CDVisitor getWriteSOARecordVisitor(final File dir, 
                                                     final boolean overwrite) {

        return new DNSVisitor () {
                public void actOn(ComponentDescription node, Stack stack)  {
                    try {
                        if (node instanceof DNSZone) {
                            ((DNSZone) node).writeSOARecord(dir, overwrite);
                        }
                    } catch (DNSException e) {
                        throw new RuntimeException("can't write SOA", e);
                    }
                }
            };
    }

    /**
     * Dumps to a file the corresponding SOA record with no bindings.
     *
     * @param dir A directory where to write the record.
     * @param overwrite True if we overwrite existing files.
     * @exception DNSException if an error occurs
     */
    public void writeSOARecord(File dir, boolean overwrite)
        throws DNSException {

        try {
            if (isForwarding()) {
                // forward zones don't have SOA records...
                return;
            }
            File outFile = new File(dir, getFileName());
            if (outFile.exists()) {
                if (overwrite) {
                    outFile.delete();
                } else {
                    // do nothing
                    return;
                }
            }
            PrintWriter out = 
                new PrintWriter(new FileOutputStream(outFile));
            out.println("$ORIGIN .");
            out.println("$TTL " + Integer.toString(getTTL()));
            out.println(getName() + " IN SOA " 
                        + getNameServerHostName() 
                        + " root.localhost. ( \n"
                        + "16         ; serial\n"
                        + Integer.toString(getRefresh()) 
                        + "      ; refresh (8 hours)\n"
                        + Integer.toString(getRetry())    
                        + "       ; retry (2 hours)\n"
                        + Integer.toString(getExpire())    
                        + "     ; expire (1 week)\n"
                        + Integer.toString(getTTL())    
                        + "      ; minimum (1 day)\n"
                        + ")\n" 
                        + "    NS    " + getNameServerHostName());
            out.println("$ORIGIN " + getName() + ".");
            out.flush();
            if (out.checkError()) {
                throw new  DNSException("error while writing a zone file");
            }            
            out.close();
        } catch (IOException e) {
            throw new DNSException("cannot write zone file", e);
        }
    }
}
