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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.PrintWriter;
import java.util.Iterator;





/**
 * A top level component describing a DNS configuration.
 *
 * 
 * 
 */
public class DNSDataImpl extends DNSComponentImpl implements DNSData {


    /** An options component for the name server. */
    DNSOptions options = null;

    /** An alias for the default view. */
    String defaultViewName = null;

    /** the host name that will appear in the NS records as 
     *  master of the zone. */
    String nameServerHostName = "localhost.";
 
   // all the attribute names in the description...
    public static final String NAME_SERVER_HOST_NAME = "nameServerHostName";
 
    /**
     * Creates a new <code>DNSDataImpl</code> instance.
     *
     */
    public DNSDataImpl() {
   
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
        if (!isData(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " Data type " + ctx);
        }
        nameServerHostName = getString(ctx, NAME_SERVER_HOST_NAME,
                                       nameServerHostName);
        // always an absolute name
        nameServerHostName = (nameServerHostName.endsWith(".") 
                              ? nameServerHostName
                              : nameServerHostName + ".");
        for (Iterator iter = ctx.sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof ComponentDescription) {
                ComponentDescription cd = (ComponentDescription) obj;
                 if (isOptions(cd.sfContext())) {
                    setOptions(cd);
                } else if (isView(cd.sfContext())) {
                    replaceViewFromDescription(cd);
                }
            }
        }
    }
    
    /**
     * Sets the options data object for this configuration.
     *
     * @param cd A component description for the options data object
     * @exception SmartFrogDeploymentException if an error occurs 
     * while deploying options.
     */
    void setOptions(ComponentDescription cd) 
        throws SmartFrogDeploymentException {

        options =  (DNSOptions) deployComponent(this, cd);
     }

    /**
     * Constructs from description and replaces a new view as child of this top
     * level component. If it is not present it is just added. We gave it
     * an attribute equal to its name, and if it is a "default" view, we
     * also bound it with the DEFAULT_VIEW attribute.
     *
     * @param cd  A component description for the view data object.
     * @return The old view with the same name or null.
     * @exception SmartFrogDeploymentException if an error occurs
     * while deploying view.
     */
    public DNSView replaceViewFromDescription(ComponentDescription cd) 
        throws SmartFrogDeploymentException {

        DNSView view = (DNSView) deployComponent(this, cd);
        return replaceView(view);
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

        if (root instanceof DNSDataImpl) {
            // just "take-over" all the relevant fields
            DNSDataImpl rootData = (DNSDataImpl) root;
            rootData.options = options;
            rootData.defaultViewName = defaultViewName;
            rootData.nameServerHostName = nameServerHostName;
            rootData.sfContext = sfContext;
        } else {
            throw new DNSModifierException("Incompatible replacement type"
                                           + root);
        }
    }


    /**
     * Replaces a new view object as child of this top level component.
     * If it is not present it is just added. We gave it
     * an attribute equal to its name, and if it is a "default" view, we
     * also bound it with the DEFAULT_VIEW attribute.
     *
     * @param view  A view data object that replaces/adds.
     * @return The old view with the same name or null.
     */
    public synchronized DNSView replaceView(DNSView view) {

        view.setParent(this);
        DNSView old = (DNSView) sfContext().put(view.getName(), view);
        if ((old !=null) && (old.isDefault())) {
            // get rid of the default pointer too...
            defaultViewName = null;
        }
        if (view.isDefault()) {
            defaultViewName =  view.getName();           
        }
        return old;
    }

    /**
     * Returns the "default" view or if none has been specified
     * as "default" one at random.
     *
     * @return The "default" view or if none has been specified
     * as "default" one at random.
     */
    public DNSView getMainView() {
        
        DNSView result = null;
        // try first the default.
        if (defaultViewName != null) {
            try {
                result = (DNSView) sfResolveHere(defaultViewName);
            } catch (Exception e) {
                // continue
            }
        }
        // any view will do.
        if (result == null) {
            for (Iterator iter = sfContext.sfValues(); iter.hasNext(); ) {
                Object obj = iter.next();
                if (obj instanceof DNSView) {
                    return (DNSView) obj;
                }
            }
        }
        return result;
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
     * for a "default" view.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHere(Object name)
        throws SmartFrogResolutionException {

        if (DEFAULT_VIEW.equals(name) 
            && (defaultViewName != null)) {
            return super.sfResolveHere(defaultViewName);
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

        if (options != null) {
            options.printNamedConfig(out);
        }
        // write all the views...
        super.printNamedConfig(out);
    }

   /**
     * Gets the global options component associated with this hierarchy.
     *
     * @return The global options component associated with this hierarchy.
     */
    public DNSOptions getOptions() {

        return options;
    }

    /**
     * Gets the host name that will appear in the NS records as 
     *  master of the zone.
     *
     * @return The host name that will appear in the NS records as 
     *  master of the zone.
     *
     */
    public String getNameServerHostName() {
        
        return nameServerHostName;
    }


}
