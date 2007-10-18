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


import org.smartfrog.sfcore.reference.Reference;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Update;
import org.xbill.DNS.Rcode;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.reference.ReferencePart;
import java.util.Stack;

/**
 * A class implementing a DNS binding between host names and IP addresses. 
 *
 * 
 * 
 */
public class DNSBindingIPImpl extends DNSComponentImpl 
    implements DNSBindingIP {

    /**  The name of the zone this binding is in.*/
    String zoneName = null;
    
    /**  The name of the view this binding is in.*/
    String viewName = null;
    
    /** A internal record for this binding. */
    DNSRecord record = null;

    // all the attribute names in the description...
    public static final String ZONE_NAME = "zoneName";
    public static final String VIEW_NAME = "viewName";
    public static final String TTL = "ttl";
    public static final String REPLACE_ALL = "replaceAll";
    

    /**
     * Creates a new <code>DNSBindingIPImpl</code> instance.
     *
     */
    public DNSBindingIPImpl() {
        // real configuration in sfDeployWith      
    }


    /**
     *  Gets an object implementing a visitor pattern that performs
     * checks on all the currently active bindings.
     *
     * @param res A pre-allocated boolean array for the result
     * @return An object implementing a visitor pattern that performs
     * checks on all the currently active bindings.
     *
     */
    public static CDVisitor getBindingCheckerVisitor(Boolean[] res) {


        res[0] = Boolean.TRUE;
        return new  DNSVisitor (res) {
                public void actOn(ComponentDescription node, Stack stack) {
                    if (node instanceof DNSBindingIP) {
                        DNSBindingIP bind = (DNSBindingIP) node;
                        int state = bind.getState();
                        if (state == NORMAL) {
                            if (!bind.testBinding()) {
                                getResult()[0] = Boolean.FALSE;
                            }
                        }
                    }
                }
            };
    }

    /**
     * Gets an object implementing a visitor pattern that performs
     * all the dynamic updates of bindings in a component hierarchy.
     *
     * @param addAll True if we also register NORMAL bindings and not
     * only the ones scheduled to  be added (TO_ADD)
     * @param res A pre-allocated boolean array for the result, i.e.,
     * True if all are updated OK, false otherwise.
     * @return An object implementing a visitor pattern that performs
     * all the dynamic updates of bindings in a component hierarchy.
     *
     */
    public static CDVisitor getDynamicUpdateVisitor(final boolean addAll,
                                                    Boolean[] res) {

        res[0] = Boolean.TRUE;
         return new DNSVisitor (res) {
                public void actOn(ComponentDescription node, Stack stack) {
                    if (node instanceof DNSBindingIP) {
                        DNSBindingIP bind = (DNSBindingIP) node;
                        int state = bind.getState();
                        if (state == TO_ADD) {
                            try {
                                bind.register();
                                bind.setState(NORMAL);
                            } catch (Exception e) {
                                System.out.println("can't register" + bind);
                                bind.setState(DELETED);
                                getResult()[0] = Boolean.FALSE;
                                // continue with others
                            }
                        } else if (state == TO_DELETE) {
                            try {
                                bind.unregister();
                                bind.setState(DELETED);
                            } catch (Exception e) {
                                System.out.println("can't unregister" + bind);
                                bind.setState(DELETED);
                                getResult()[0] = Boolean.FALSE;
                                // continue with others
                                
                            }
                        } else if ((state == NORMAL) && addAll) {
                            try {
                            bind.register();
                            } catch (Exception e) {
                                System.out.println("can't register" + bind);
                                bind.setState(DELETED);
                                getResult()[0] = Boolean.FALSE;
                                // continue with others
                            }
                        }
                    }
                }
            };

    }

    /**
     * The "real" constructor of this component. It will recursively
     * try to instantiate and configure  all the components passed in
     * its context.
     *
     * @param parent A parent in the description hierarchy.
     * @param ctx a <code>Context</code> value
     * @exception SmartFrogDeploymentException if an error occurs while 
     * initializing
     * this component or any of its sub-components.
     */
    public void sfDeployWith(ComponentDescription parent, Context ctx)
        throws SmartFrogDeploymentException {

        super.sfDeployWith(parent, ctx);
        if (!isBinding(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " Binding type " + ctx);
        }

        zoneName = getString(ctx, ZONE_NAME, null);
        if (zoneName == null) {
            throw new SmartFrogDeploymentException("No zone name");
        }
        viewName = getString(ctx, VIEW_NAME, null);
        if (viewName == null) {
            throw new SmartFrogDeploymentException("No view name");
        }
        record = DNSRecordImpl.newRecord(ctx);
        
        int ttl = getInteger(ctx, TTL, 3600);
        record.setTTL(ttl);
        boolean replaceAll = getBoolean(ctx, REPLACE_ALL, false);
        record.setReplaceAll(replaceAll);
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
            Reference ref = new Reference ();
            ref.addElement(ReferencePart.here(getViewName()));
            ref.addElement(ReferencePart.here(getZoneName()));
            DNSZone rootZone = (DNSZone) root.sfResolve(ref);
            rootZone.replaceBinding(this);
        } catch (Exception e) {
            throw new DNSModifierException("can't replace binding", e);
        }
    }


    /**
     * A name unique for the zone that identifies this binding.
     *
     * @return A name unique for the zone that identifies this binding.
     */
    public String getName() {

        return record.getUniqueName();
    }


    /**
     * Transforms a string into an absolute zone (a "." is added if needed)
     *
     * @param str A zone name encoded as a string.
     * @return An absolute zone name.
     */
    public static Name toAbsoluteName(String str) {
        
        try {
            String input = (str.endsWith(".") ? str : str + ".");
            return Name.fromString(input);
        } catch (Exception e) {
            throw new IllegalArgumentException("toAbsoluteName: can't convert "
                                               + str);
        }
    }  

 
    /**
     * Gets the name of the zone.
     *
     * @return The name of the zone.
     */
    public String getZoneName() {
        
        return zoneName;
    }


    /**
     * Returns a pointer to the parent zone or null if it has not 
     *  been inserted into a zone yet.
     *
     * @return A pointer to the parent zone or null if it has not 
     *  been inserted into a zone yet.
     */
    public DNSZone getEnclosingZone() {

        if (sfParent() instanceof DNSZone) {
            return (DNSZone) sfParent();
        }
        return null;
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
     * Returns a pointer to the parent view or null if it has not 
     *  been inserted into a view yet.
     *
     * @return A pointer to the parent view or null if it has not 
     *  been inserted into a view yet.
     */
    public DNSView getEnclosingView() {

        DNSZone zone = getEnclosingZone();
        if (zone != null) {
            return zone.getEnclosingView();
        }
        return null;
    }

    /**
     * Gets the dns record associated with this binding.
     *
     * @return The dns record associated with this binding.
     */
    public DNSRecord  getRecord() {
        
        return record;
    }

    /**
     * Checks that this binding is compatible to where it has been
     * placed.
     *
     * @exception DNSModifierException if the binding environment is
     * incompatible with where it was placed.
     */
    void checkConsistency() 
        throws DNSModifierException {

        try {
            DNSZone zoneEnc = getEnclosingZone();
            // PARENT:PARENT:PARENT:<viewName>:<zoneName>:<bindName>
            Reference meRef = new Reference();
            meRef.addElement(ReferencePart.parent());
            meRef.addElement(ReferencePart.parent());
            meRef.addElement(ReferencePart.parent());
            meRef.addElement(ReferencePart.here(viewName));
            meRef.addElement(ReferencePart.here(zoneName));
            meRef.addElement(ReferencePart.here(getName()));
            if (!(this == sfResolve(meRef))) {
                throw new DNSModifierException("incompatible zone/view"
                                               + " name " + meRef);
            }
        } catch (Exception e) {
            if (e instanceof DNSModifierException) {
                throw (DNSModifierException) e;
            } else {
                throw new DNSModifierException("binding not consistent", e);
            }
        }
    }


    /**
     *  Registers a DNS binding update in the name server. It needs to 
     * have been inserted in the component hierarchy in order to work.
     *
     * @exception DNSModifierException if an error occurs 
     * while registering the update.
     */
    public void register()
        throws DNSModifierException {

        try { 
            checkConsistency();
            Name absoluteZone = toAbsoluteName(getEnclosingZone().getName());
            Update update = record.getRegisterUpdate(absoluteZone);
            sendUpdate(update);
        } catch (Exception e) {
            if (e instanceof DNSModifierException) {
                throw (DNSModifierException) e;
            } else {
                throw new DNSModifierException("can't register binding", e);
            }
        }
    }


    /**
     * Sends an update and analyzes the response,
     * throwing an exception if it notifies
     * an error while updating the DNS server.
     *
     * @param update An update message to be sent.
     * @exception DNSModifierException If the response
     * message notified an error.
     */
    public void sendUpdate(Update update) 
        throws DNSModifierException {
        
        try {
            Resolver res = getEnclosingView().getResolver();        
            Message msg = res.send(update);
            int errorCode = msg.getRcode();
            if (errorCode != Rcode.NOERROR) {
                throw new DNSModifierException("got reply from server with"
                                              + " error message " 
                                              + Rcode.string(errorCode)); 
            }
        } catch (Exception e) {
            if (e instanceof DNSModifierException) {
                throw (DNSModifierException) e;
            } else {
                throw new DNSModifierException("got IO exception from server",
                                               e);
            }
        }
    }   

    
    /**
     * Unregisters a DNS binding update in the name server.
     *
     * @exception DNSModifierException if an error occurs 
     * while unregistering the update.
     */
    public void unregister() throws DNSModifierException {

        try {
            checkConsistency();
            Name absoluteZone = toAbsoluteName(getEnclosingZone().getName());
            Update update = record.getUnregisterUpdate(absoluteZone);
            sendUpdate(update);
        } catch (Exception e) {
            if (e instanceof DNSModifierException) {
                throw (DNSModifierException) e;
            } else {
                throw new DNSModifierException("can't unregister binding", e);
            }
        }
    }

    /**
     * Tests whether the binding can be looked-up in the server.
     *
     * @return True if the binding can be looked-up in the server.
     */
    public boolean testBinding() {

        try { 
            Name absoluteZone = toAbsoluteName(getEnclosingZone().getName());
            Resolver resol = getEnclosingView().getResolver();
            return record.validLookup(absoluteZone, resol);
        } catch (Exception e) {
            return false;
        }
    }
}
