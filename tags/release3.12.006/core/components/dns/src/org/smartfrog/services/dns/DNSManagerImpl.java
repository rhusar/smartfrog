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

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.reference.Reference;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.HashMap;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.ReferencePart;
import java.io.IOException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.CDVisitor;

/**
 * SmartFrog component to manage a DNS server
 */
public class DNSManagerImpl extends CompoundImpl implements DNSManager, 
                                                            Compound {
    /** A "backend" connector to the DNS server. */ 
    DNSNamed named = null;

    /** Configuration data for the named daemon. */
    DNSData data = null;

    /** A reference to the configuration data. */
    static final Reference REF_DATA =
        new Reference(ReferencePart.here("data"));

    /** A static reference for the named server. */
    static final Reference REF_NAMED =
        new Reference(ReferencePart.here("named"));
    

    /** default constructor as requried by SmartFrog */
    public DNSManagerImpl()
        throws RemoteException {
        
        data =  new DNSDataImpl();
    }

    /** sfDeploy lifecycle method as required by SmartFrog */
    public synchronized void sfDeploy()
        throws SmartFrogException, RemoteException {

        super.sfDeploy();
        named = (DNSNamed) sfResolve(REF_NAMED);
        ComponentDescription dataCD = (ComponentDescription)
            sfResolve(REF_DATA, 
                      new ComponentDescriptionImpl(null,null,true), // ignored
                      true);
        DNSComponent conf = DNSComponentImpl.deployComponent(null, dataCD);
        conf.replace(data);
        pushConfiguration();        
    }

    /**
     * Pushes the configuration data to the named server.
     *
     * @exception DNSException if an error occurs while pushing the
     * configuration.
     */
    void pushConfiguration() 
        throws DNSException {
       try { 
            named.updateConfigData(data);
       } catch (RemoteException e) {
           throw new DNSException("can't push configuration", e);
        }
    }


    /**
     * Updates the configuration of named and re-starts the daemon, adding
     * dynamic bindings after that.
     *
     * @exception DNSException if an error occurs while reseting the named.
     */
    void resetNamed() 
        throws DNSException {
        
        try {             
            pushConfiguration();        
            named.cleanUp();
            named.start();
            if (!named.status()) {
                throw new DNSException("Can't contact  named, not started");
            }
            updateBindings(data, true);
            // need to ensure that "forward" zones get the updates
            named.flush();
        } catch (RemoteException e) {
            throw new DNSException("can't reset named", e);
        }
    }

    /** sfStart lifecycle method as required by SmartFrog */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {

        super.sfStart();
        resetNamed();
    }

    /**
     * sfTerminateWith lifecycle method as required by SmartFrog
     * @param tr A reason for termination.
     */
    public void sfTerminateWith(TerminationRecord tr) {
        // Try to clean up all bindings (best effort...)
        unregisterAll();
        super.sfTerminateWith(tr);        
    }

    

    /**
     * Tries to unregister all the current bindings, zones or views,
     * ignoring exceptions when it cannot.
     *
     * @return true If it successfully unregistered all the bindings.
     */
    boolean unregisterAll() {

        try { 
            data = new DNSDataImpl();
            named.updateConfigData(data);
            named.cleanUp();
            return true;
        } catch (Exception e) {
            System.out.println("Cannot unregisterall " + e);
            return false;
        }
    }

    /**
     * Performs dynamic updates for all the binding components.
     *
     * @param comp A hierarchy with binding components.
     * @param addAll True if we register both NORMAL and TO_ADD
     * binding objects, false if we only register TO_ADD objects.
     * Only TO_DELETE objects are unregistered.
     * @exception DNSModifierException if an error occurs while traversing
     * the hierarchy.
     */
    public static void updateBindings(DNSComponent comp, boolean addAll)
        throws DNSModifierException {
        
        Boolean[] results = {Boolean.FALSE}; // default value irrelevant
        try {
            CDVisitor vis = DNSBindingIPImpl.getDynamicUpdateVisitor(addAll,
                                                                     results);
            comp.visit(vis, true);
            tidyUp(comp);
        } catch (Exception e) {
            throw new  DNSModifierException("Can't update bindings", e);
        }
        if (!results[0].booleanValue()) {
            throw new DNSModifierException("Can't update ALL bindings");
        }
    }
         
   /**
     * Performs a change of state for all components.
     *
     * @param comp A hierarchy with  components.
     * @param newState A new state for all the components.
     * @exception DNSModifierException if an error occurs while traversing
     * the hierarchy.
     */
    public static void updateState(DNSComponent comp, int newState)
        throws DNSModifierException {
        
        try {
            CDVisitor vis =  DNSComponentImpl.getStateChangeVisitor(newState);
            comp.visit(vis, true);
        } catch (Exception e) {
            throw new  DNSModifierException("Can't update state", e);
        }
    }


    /**
     * Removes all the deleted  or to be deleted components
     *
     * @param comp a <code>DNSComponent</code> value
     * @exception DNSModifierException if an error occurs
     */
    public static void tidyUp(DNSComponent comp) 
        throws DNSModifierException {

        try {
            CDVisitor vis =  DNSComponentImpl.getCleanerVisitor();
            comp.visit(vis, true);
        } catch (Exception e) {
            throw new  DNSModifierException("Can't clean hierarchy", e);
        }
    }


  
     /**
     * Registers a DNS update in the name server. 
     *
     * @param modif A DNS update to be added to the name server.
     * @exception DNSModifierException if an error occurs 
     * while registering the update.
     */
    public synchronized void register(DNSModifier modif)
        throws DNSModifierException {

        processModif(modif, DNSComponent.TO_ADD);
    }

    /**
     * Unregisters a DNS update in the name server. 
     *
     * @param modif A DNS update to be added to the name server.
     * @exception DNSModifierException if an error occurs 
     * while unregistering the update.
     */
    public synchronized void unregister(DNSModifier modif) 
        throws DNSModifierException {

        processModif(modif, DNSComponent.TO_DELETE);
    }
    
    /**
     * Process  a DNS update in the name server. 
     *
     * @param modif A DNS update to be added to the name server.
     * @param oper TO_ADD or TO_DELETE
     * @exception DNSModifierException if an error occurs 
     * while registering the update.
     */
    void processModif(DNSModifier modif, int oper)
        throws DNSModifierException {
       
        try {
            DNSComponent update = modif.getUpdate();
            updateState(update, oper);
            // I should copy "data" in case of failure...
            update.replace(data); 
           
            if (modif.isOnlyBindings()) {
                updateBindings(data, false);
                // need to ensure that "forward" zones get the updates
                named.flush();
            } else {
                tidyUp(data);                
                // no dynamic updates of zones, views...
                resetNamed();
            }
        } catch (Exception e) {
            if (e instanceof DNSModifierException) {
                throw (DNSModifierException) e;
            } else {
                throw new DNSModifierException("Can't register", e);
            }
        }

    }


    /**
     * Checks that all the bindings that are currently registered
     * can be looked up in the name server.
     *
     * @return True if all the NORMAL bindings are OK, false otherwise.
     * @exception DNSBindingException If I cannot check that  bindings are
     * OK.
     */
    public synchronized  boolean sanityCheck() 
        throws DNSModifierException {

        try {
            Boolean[] results = {Boolean.FALSE}; // default value irrelevant
            CDVisitor vis = DNSBindingIPImpl.getBindingCheckerVisitor(results);
            data.visit(vis, true);
            return results[0].booleanValue();
        } catch (Exception e) {
            throw new  DNSModifierException("Can't do sanity check", e);
        }
    }
}
