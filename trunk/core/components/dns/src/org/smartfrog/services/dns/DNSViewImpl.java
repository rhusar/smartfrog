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

import java.util.Vector;
import java.net.InetAddress;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import java.util.Iterator;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import java.net.UnknownHostException;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.Context;
import java.io.PrintWriter;





/**
 * A class implementing a DNS view.
 *
 */
public class DNSViewImpl extends DNSComponentImpl implements DNSView {


    /** The name of this view. */
    String name = null;

    /** Whether this view is the "default" view. */
    boolean isDefault = false;

    /** The main IP address to contact this view.*/
    InetAddress address = null;

    /** A vector of (strings) IP addresses representing all the 
     * network interfaces the view listens to. */
    Vector interfacesAddress = null;

    /** A vector of IP addresses to forward queries that we cannot resolve
        locally. */
    Vector forwarders = null;

    /** An alias for the default zone. */
    String defaultZoneName = null;

    /** An alias for the default reverse zone. */
    String defaultReverseZoneName = null;

    // all the attribute names in the description...
    public static final String VIEW_NAME = "viewName";

    public static final String SET_AS_DEFAULT_VIEW = "setAsDefaultView";

    public static final String VIEW_INTERFACES_ADDRESS = "viewInterfacesAddress";

    public static final String VIEW_ADDRESS = "viewAddress";

    public static final String FORWARDERS = "forwarders";

    /**
     * Creates a new <code>DNSViewImpl</code> instance.
     *
     */
    public DNSViewImpl() {
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
        if (!isView(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " View type " + ctx);
        }
        
        name = getString(ctx, VIEW_NAME, null);
        if (name == null) {
            throw new SmartFrogDeploymentException("No name");
        }
        isDefault = getBoolean(ctx, SET_AS_DEFAULT_VIEW, isDefault);
        interfacesAddress = getVector(ctx, VIEW_INTERFACES_ADDRESS, null);
        if (interfacesAddress == null) {
            throw new SmartFrogDeploymentException("No interfacesAddress");
        }

        forwarders = getVector(ctx, FORWARDERS, null);

        String ipAddressStr = null;
        try {

            ipAddressStr = getString(ctx, VIEW_ADDRESS, null);
            if (ipAddressStr == null) {
                throw new SmartFrogDeploymentException("no address");
            }
            address = InetAddress.getByName(ipAddressStr);
        } catch (UnknownHostException e) {
            throw new SmartFrogDeploymentException("can't resolve "
                                                   + ipAddressStr, e);
        }

        // add the zones...
        for (Iterator iter = ctx.sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof ComponentDescription) {
                ComponentDescription cd = (ComponentDescription) obj;
                if (isZone(cd.sfContext())) {
                    replaceZoneFromDescription(cd);
                }
            }
        }
    }

    /**
     * Constructs from description and replaces a new zone as child of this top
     * level component. If it is not present it is just added. We gave it
     * an attribute equal to its name, and if it is a "default" (reverse) zone,
     * we also bound it with the DEFAULT_ZONE or DEFAULT_REVERSE_ZONE 
     * attributes.
     *
     * @param cd  A component description for the zone data object.
     * @return The old zone with the same name or null.
     * @exception SmartFrogDeploymentException if an error occurs
     * while deploying zone.
     */
    public DNSZone replaceZoneFromDescription(ComponentDescription cd) 
        throws SmartFrogDeploymentException {

        DNSZone zone = (DNSZone) deployComponent(this, cd);
        return replaceZone(zone);
    }

    /**
     * Replaces a new zone object as child of this top level component.
     * If it is not present it is just added.  We gave it
     * an attribute equal to its name, and if it is a "default" (reverse) zone,
     * we also bound it with the DEFAULT_ZONE or DEFAULT_REVERSE_ZONE 
     * attributes.
     *
     * @param zone  A zone data object that replaces/adds.
     * @return The old zone with the same name or null.
     */
    public synchronized DNSZone replaceZone(DNSZone zone) {

        zone.setParent(this);
        DNSZone old = (DNSZone) sfContext().put(zone.getName(), zone);
        if ((old !=null) && (old.isDefault())) {
            // get rid of the default pointer too...
            if (old.isReverse()) {
                defaultReverseZoneName = null;
            } else {
                defaultZoneName = null;
            }
        }
        if (zone.isDefault()) {
            if (zone.isReverse()) {
                defaultReverseZoneName = zone.getName();
            } else {
                defaultZoneName =  zone.getName();
            }         
        }
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

        if (root instanceof DNSData) {
            DNSData rootData = (DNSData) root;
            rootData.replaceView(this);
        } else {
            throw new DNSModifierException("Wrong root type");
        }
    }
    
    /**
     * Returns a pointer to the parent "data" or null if it has not 
     *  been inserted into a top level "data" yet.
     *
     * @return A pointer to the parent "data" or null if it has not 
     *  been inserted into a top level "data" yet.
     */
    public DNSData getEnclosingData() {

        if (sfParent() instanceof DNSData) {
            return (DNSData) sfParent();
        }
        return null;
    }
    /**
     * Find an attribute in this context, so long as it is visible anywhere.
     *
     * @param name attribute key to resolve
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        throws a SmartFrogResolutionException
     *
     * @return Object value for attribute
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHereNonlocal(Object name, boolean mandatory)
        throws SmartFrogResolutionException {

        return sfResolveHere(name,mandatory);
    }
   /**
     * Find an attribute in this component context. It creates an alias
     * for a "default" zone and a "defaultReverse" zone.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHere(Object name)
        throws SmartFrogResolutionException {

        if (DEFAULT_ZONE.equals(name) 
            && (defaultZoneName != null)) {
            return super.sfResolveHere(defaultZoneName);
        }

        if (DEFAULT_REVERSE_ZONE.equals(name) 
            && (defaultReverseZoneName != null)) {
            return super.sfResolveHere(defaultReverseZoneName);
        }

        return super.sfResolveHere(name);
    }
  
    /**
     * Writes the named.conf contents to the print writer. It propagates 
     * to its sub-components.
     *
     * @param out A print writer to dump the named.conf that bind needs.
     */
    public void printNamedConfig(PrintWriter out) {

        out.println("view \"" + getName() + "\" {");
        out.println("match-destinations " 
                    + printAddresses(getInterfacesAddress()) + ";");
        if (forwarders != null) {
            out.println("  forwarders "
                       + printAddresses(forwarders)  + ";");
            out.println("  forward only;");
        }
        // print all the zones here...
        super.printNamedConfig(out);
        out.println("};");
    }   


    /**
     * Gets a resolver to communicate with the name server in our view.
     *
     * @return A resolver to communicate with the name server in our view.
     * @exception DNSException if an error occurs while getting a resolver
     * to the  name server.
     */
    public Resolver  getResolver() 
        throws DNSException {

        try {
            SimpleResolver resolver = 
                new SimpleResolver(address.getHostAddress());
            resolver.setPort(DEFAULT_PORT);
            resolver.setTCP(true);        
            return resolver;
        } catch (Exception e) {
            throw new DNSException("Cannot get resolver", e);
        }
    }



    /**
     * Gets the name of this view.
     *
     * @return The name of this view.
     */
    public String getName() {

        return name;
    }


    /**
     * Whether this view should be considered "default".
     *
     * @return True if  this view should be considered "default".
     */
    public boolean isDefault() {

        return isDefault;
    }


    /**
     * Gets the main IP address to contact this view.
     *
     * @return The main IP address to contact this view.
     */
    public InetAddress getAddress() {

        return address;
    }

    /**
     * Gets a vector of IP addresses representing all the 
     * network interfaces the view listens to.
     *
     * @return  A vector of IP addresses representing all the 
     * network interfaces the view listens to.
     *
     */
    public Vector getInterfacesAddress() {
        
        return interfacesAddress;
    }

    /**
     * Gets the value of forwarders
     *
     * @return the value of forwarders
     */
    public Vector getForwarders()  {
        return this.forwarders;
    }

}
