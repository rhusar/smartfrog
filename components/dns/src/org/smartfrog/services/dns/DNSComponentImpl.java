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

import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Context;
import java.net.InetAddress;
import java.util.Vector;
import java.util.Iterator;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import java.io.PrintWriter;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import java.util.Stack;





/**
 * An abstract class that implements the component description interface
 * for dns components.
 *
 * 
 */
public abstract class DNSComponentImpl extends ComponentDescriptionImpl
    implements DNSComponent {

     /** A reference for the dns data type. */
    static final String DNSTYPE = "dnsType";


    /** The current state of the component. */
    private int state = NOT_INIT;
 
    /**
     * Creates a new <code>DNSComponentImpl</code> instance.
     *
     */
    public DNSComponentImpl() {

        // parent set in sfDeployWith.
        super(null, new ContextImpl(), true);
    }

    /**
     * The "real" constructor of this component. It will recursively
     * try to instantiate and configure  all the components passed in
     * its context.
     *
     * @param parent A parent in the description hierarchy.
     * @param cxt A context from which to obtain arguments to configure
     * this object.
     * @exception SmartFrogException if an error occurs while initializing
     * this component or any of its sub-components.
     */
    public void sfDeployWith(ComponentDescription parent, Context cxt)
        throws SmartFrogDeploymentException {

        this.parent = parent;
        state = NORMAL;
    }


    /**
     * Creates a dns component from a description setting the parentage for
     * it.
     *
     * @param parentComponent  A parent component for this dns object.
     * @param component A description for this component.
     * @return A DNS component with the correct parentage.
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public static DNSComponent deployComponent(ComponentDescription
                                               parentComponent,
                                               ComponentDescription component)
        throws SmartFrogDeploymentException {

        // we could make the deployer customizable here...
        return new DNSComponentDeployerImpl(component).
            deployComponent(parentComponent);
    }

    /**
     * Writes the named.conf contents to the print writer. It propagates 
     * to its sub-components.
     *
     * @param out A print writer to dump the named.conf that bind needs.
     */
    public void printNamedConfig(PrintWriter out) {

        for (Iterator iter = sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof DNSComponent) {
                DNSComponent comp = (DNSComponent) obj;
                comp.printNamedConfig(out);
            }
        }
    }
    /**
     * A name unique for the enclosing component that identifies this 
     * object.
     *
     * @return A name unique for the enclosing component that identifies this 
     * object.
     */
    public String getName() {
        
        return null;
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

        throw new DNSModifierException("Cannot replace objects of type "
                                       + this);
    }


    /**
     * Returns an object that implements a visitor pattern to change
     * all the components of a hierarchy to a given state.
     *
     * @param newState The new state of this components.
     * @return An object that implements a visitor pattern to change
     * all the components of a hierarchy to a given state.
     */
    public static CDVisitor getStateChangeVisitor(final int newState) {

        return new DNSVisitor () {
                public void actOn(ComponentDescription node, Stack stack) {
                    if (node instanceof DNSComponent) {
                        ((DNSComponent) node).setState(newState);
                    }
                }
            };
    }

    /**
     *  Returns an object that implements a visitor pattern to check
     * if a hierarchy contains no zone, view or data components.
     *
     *
     * @param res An array allocated by the caller where the result
     * of this check will be returned (in result[0])
     * @return A visitor pattern to check
     * if a hierarchy contains no zone, view or data components.
     *
     */
    public static CDVisitor getOnlyBindingsVisitor(Boolean[] res) {

        res[0] = Boolean.TRUE;
        return new DNSVisitor (res) {
                public void actOn(ComponentDescription node, Stack stack) {
                    if ((node instanceof DNSView) 
                        || (node instanceof DNSZone)
                        || (node instanceof DNSData)) {
                        // this is really res[0] 
                        getResult()[0] = Boolean.FALSE;
                    }
                }
            };
    }

    


    /**
     * Returns an object that implements a visitor pattern to remove
     * all the components of a hierarchy in deleted or to be deleted state.
     *
     * @return An object  that implements a visitor pattern to remove
     * all the components of a hierarchy in to be deleted state.
     */
    public static CDVisitor getCleanerVisitor() {

        return new DNSVisitor () {
                public void actOn(ComponentDescription node, Stack stack) {
                    if (node instanceof DNSComponent) {
                        DNSComponent comp = (DNSComponent) node;
                        if ((comp.getState() == TO_DELETE)
                            ||(comp.getState() == DELETED)) {
                            comp.delete();
                        }
                    }
                }
            };
    }

    /**
     * Detaches itself from the parent and sets state to deleted.
     *
     */
     public void delete() {

         if (parent != null) {
             Object obj = parent.sfAttributeKeyFor(this);
             if (obj != null) {
                 try {
                     parent.sfRemoveAttribute(obj);
                 } catch (SmartFrogRuntimeException e) {
                     // ignore, we just want to know it is not there
                 }
             }
         }
         setState(DELETED);
         parent = null;
    }

    /**
     * Whether the context represents a DNS component.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS component.
     */
    public static boolean isComponent(Context ctx) {
        
        Object type = ctx.get(DNSTYPE);
        if (type instanceof String) {
            return ((String)type).startsWith("dns.");
        }
        return false;
    }



    /**
     * Whether the context represents a DNS data object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS data object.
     */
    public static boolean isData(Context ctx) {
        
        return "dns.data".equals(ctx.get(DNSTYPE));
    }

    /**
     * Whether the context represents a DNS collection object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS collection object.
     */
    public static boolean isCollection(Context ctx) {
        
        return "dns.collection".equals(ctx.get(DNSTYPE));
    }

    /**
     * Whether the context represents a DNS view object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS view object.
     */
    public static boolean isView(Context ctx) {

        return "dns.view".equals(ctx.get(DNSTYPE));
    }

    /**
     * Whether the context represents a DNS binding object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS binding object.
     */
    public static boolean isBinding(Context ctx) {

        return "dns.binding".equals(ctx.get(DNSTYPE));
    }

    /**
     * Whether the context represents a DNS zone object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS zone object.
     */
    public static boolean isZone(Context ctx) {

        return "dns.zone".equals(ctx.get(DNSTYPE));
    }

    /**
     * Whether the context represents a DNS options object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS options object.
     */
    public static boolean isOptions(Context ctx) {

        return "dns.options".equals(ctx.get(DNSTYPE));
    }

    /**
     * Whether the context represents a DNS opaque object.
     *
     * @param ctx A context.
     * @return True if the context represents a DNS opaque object.
     */
    public static boolean isOpaque(Context ctx) {
     
        return "dns.opaque".equals(ctx.get(DNSTYPE));
    }


    /**
     * Gets a boolean value from a context indexed by a key 
     * or a default value if it is not in the context.
     *
     * @param ctx A context to do the look-up
     * @param key A key in the context
     * @param defaultValue A default value if it is not in the context.
     * @return a boolean value from a context indexed by a key 
     * or a default value if it is not in the context.
     */
    public static boolean getBoolean(Context ctx, Object key,
                                     boolean defaultValue) {

        Object obj = ctx.get(key);
        if (obj instanceof Boolean) {
            return (((Boolean) obj).booleanValue());
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets a string value from a context indexed by a key 
     * or a default value if it is not in the context.
     *
     * @param ctx A context to do the look-up
     * @param key A key in the context
     * @param defaultValue A default value if it is not in the context.
     * @return a string value from a context indexed by a key 
     * or a default value if it is not in the context.
     */
    public static String getString(Context ctx, Object key,
                                   String defaultValue) {

        Object obj = ctx.get(key);
        if (obj instanceof String) {
            return (String) obj;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets a vector value from a context indexed by a key 
     * or a default value if it is not in the context.
     *
     * @param ctx A context to do the look-up
     * @param key A key in the context
     * @param defaultValue A default value if it is not in the context.
     * @return a vector value from a context indexed by a key 
     * or a default value if it is not in the context.
     */
    public static Vector getVector(Context ctx, Object key,
                                   Vector defaultValue) {

        Object obj = ctx.get(key);
        if (obj instanceof Vector) {
            return (Vector) obj;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets a componentDescription value from a context indexed by a key 
     * or a default value if it is not in the context.
     *
     * @param ctx A context to do the look-up
     * @param key A key in the context
     * @param defaultValue A default value if it is not in the context.
     * @return a componentDescription value from a context indexed by a key 
     * or a default value if it is not in the context.
     */
    public static ComponentDescription getCD(Context ctx, Object key,
                                             ComponentDescription 
                                             defaultValue) {

        Object obj = ctx.get(key);
        if (obj instanceof ComponentDescription) {
            return (ComponentDescription) obj;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets a int value from a context indexed by a key 
     * or a default value if it is not in the context.
     *
     * @param ctx A context to do the look-up
     * @param key A key in the context
     * @param defaultValue A default value if it is not in the context.
     * @return a int value from a context indexed by a key 
     * or a default value if it is not in the context.
     */
    public static int getInteger(Context ctx, Object key,
                                 int defaultValue) {

        Object obj = ctx.get(key);
        if (obj instanceof Integer) {
            return (((Integer) obj).intValue());
        } else {
            return defaultValue;
        }
    }

    /**
     * Formats a vector of addresses for inserting in a named config.
     *
     * @param all A vector of IP addresses (possibly in string format)
     * @return A string with  addresses for inserting in a named config.
     */
    public static String printAddresses(Vector all) {

        StringBuffer  strBuf = new StringBuffer();
        strBuf.append("{");
        for (Iterator iter = all.iterator() ; iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof String) {
                strBuf.append(obj);
            } else if (obj instanceof InetAddress) {
                strBuf.append(((InetAddress) obj).getHostAddress());
            } else {
                throw new IllegalArgumentException("not string/Inetaddress"
                                                   + " type " + obj);
            }
            strBuf.append("; ");
        }
        strBuf.append("}");
        return strBuf.toString();
    }

    /**
     * Gets the current state of the component.
     *
     * @return  The current state of the component.
     */
    public synchronized int getState() {

        return state;
    }


    /**
     * Sets the current state of the component.
     *
     * @param newState The new state of the component.
     * @return The old state of the component.
     */
    public synchronized int setState(int newState) {

        int old = state;
        state = newState;
        return old;
    }

} 
