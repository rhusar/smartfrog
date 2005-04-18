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

package org.smartfrog.sfcore.componentdescription;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.Properties;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.PrettyPrinting;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

//For utility methods
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.reference.ReferenceResolverHelperImpl;
import java.rmi.RemoteException;


/**
 * Defines the context class used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 */
public class ComponentDescriptionImpl extends ReferenceResolverHelperImpl implements Serializable, Cloneable,
    ComponentDescription, MessageKeys, PrettyPrinting {


    /** Context of attributes (key value pairs). */
    public Context context;

    /** Parent of this description. */
    public ComponentDescription parent;

    /** PrimParent of this description. */
    public Prim primParent;

    /** Whether this description is eager or lazy. */
    public boolean eager;

    /** Log: it cannot be initialized before LogImpl is ready
     * LogImpl uses ComponentDescription.sfResolve to read its initial
     * configuration */
    private static  LogSF sflog= null;


    /**
     * Constucts ComponentDescriptionImpl with parent component and context.
     *
     * @param parent parent component
     * @param cxt context for description
     * @param eager eager flag
     */
    public ComponentDescriptionImpl(ComponentDescription parent, Context cxt,
        boolean eager) {
        this.context = cxt;
        this.parent = parent;
        this.eager = eager;
    }


    /**
     * Returns the complete name for this ComponentDescription and does not throw
     * any exception. If an exception is thrown it will return a new empty reference.
     *
     * @return reference of attribute names to this component or an empty reference
     *
     */
    public Reference sfCompleteNameSafe(){
       return sfCompleteName();
    }



    /**
     * Gets the complete name for this description. This gives a reference from
     * the root component to this description. If the parent does not know
     * about this component, the parents complete name is returned.
     *
     * @return complete name for this component
     */
    public Reference sfCompleteName() {
        if (parent == null) {
            return new Reference();
        }

        Reference r = parent.sfCompleteName();
        Object name = parent.sfAttributeKeyFor(this);

        if (name != null) {
            r.addElement(ReferencePart.here(name));
        }

        return r;
    }

    /**
     * Adds an attribute to this component description under given name.
     *
     * @param name name of attribute
     * @param value value of attribute
     *
     * @return added attribute if non-existent or null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public synchronized Object sfAddAttribute(Object name, Object value)
        throws SmartFrogRuntimeException {
        return context.sfAddAttribute(name, value);
    }

    /**
     * Removes an attribute from this component.
     *
     * @param name of attribute to be removed
     *
     * @return removed attribute value if successfull or null if not
     *
     * @throws SmartFrogRuntimeException when name is null
     */
    public synchronized Object sfRemoveAttribute(Object name)
        throws SmartFrogRuntimeException {
        return context.sfRemoveAttribute(name);
    }

    /**
     * Replace named attribute in component context. If attribute is not
     * present it is added to the context.
     *
     * @param name of attribute to replace
     * @param value value to add or replace
     *
     * @return the old value if present, null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public synchronized Object sfReplaceAttribute(Object name, Object value)
        throws SmartFrogRuntimeException {
        return context.sfReplaceAttribute(name, value);
    }


    /**
     * Returns the attribute key given a value.
     *
     * @param value value to look up key for
     *
     * @return key for attribute value or null if none
     */

    // perhaps this should be synchronized... but causes problems with sfCompleteName if it is
    public Object sfAttributeKeyFor(Object value) {
        return context.sfAttributeKeyFor(value);
    }

    /**
     * Returns true if the context contains value.
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     */
    public boolean sfContainsValue(Object value) {
       return context.contains(value);
    }


    /**
     * Returns true if the context contains attribute.
     * @param attribute to check
     *
     * @return true if context contains key, false otherwise
     */
    public boolean sfContainsAttribute(Object attribute) {
       return context.containsKey(attribute);
    }


    /**
     * Returns an ordered iterator over the attribute names in the context.
     * The remove operation of this Iterator won't affect
     * the contents of ConponentDescription
     * @return iterator
     */
    public  Iterator sfAttributes() {
        return context.sfAttributes();
    }

    /**
     * Returns an ordered iterator over the values in the context.
     * The remove operation of this Iterator won't affect
     * the contents of ConponentDescription
     *
     * @return iterator
     */
    public  Iterator sfValues() {
      return context.sfValues();
    }


    /**
     * Gets the context for this description.
     *
     * @return context
     *
     * @see #setContext
     */
    public Context sfContext() {
        return context;
    }

    /**
     * Sets the context for this description.
     *
     * @param cxt new context
     *
     * @return old context
     *
     * @see #sfContext
     */
    public Context setContext(Context cxt) {
        Context oc = context;
        context = cxt;

        return oc;
    }

    /**
     * Gets the parent for this description.
     *
     * @return component parent description
     *
     * @see #setParent
     */
    public ComponentDescription sfParent() {
        return parent;
    }

    /**
     * Sets parent for this component.
     *
     * @param p new parent component
     *
     * @return old parent for description
     *
     * @see #getParent
     */
    public ComponentDescription setParent(ComponentDescription p) {
        ComponentDescription op = parent;
	primParent = null; // cannot have both!
        parent = p;

        return op;
    }


    /**
     * When a component description is held as an attribute in a Prim, the
     * parent is no longer a ComponentDescription, but the Prim itself.
     * This is so that attribute resolution works through the component description
     * in to the Prim hierarchy. For typenig reasons, the PrimParent has to be handled
     * specially and not through the normal interface (due to RemoteExceptions in the interface).
     *
     * Gets the parent for this description.
     *
     * @return component parent description
     *
     * @see #setPrimParent
     */
    public Prim sfPrimParent() {
	return primParent;
    }

    /**
     * When a component description is held as an attribute in a Prim, the
     * parent is no longer a ComponentDescription, but the Prim itself.
     * This is so that attribute resolution works through the component description
     * in to the Prim hierarchy. For typenig reasons, the PrimParent has to be handled
     * specially and not through the normal interface (due to RemoteExceptions in the interface).
     *
     * Sets parent for this component.
     *
     * @param parent new parent component
     *
     * @return old parent for description
     *
     * @see #sfPrimParent
     */
    public Prim setPrimParent(Prim p) {
        Prim op = primParent;
	parent = null; // cannot have both!
        primParent = p;

        return op;
    }

    /**
     * Gets the eager flag for description.
     *
     * @return true if description eager, false if lazy
     *
     * @see #setEager
     */
    public boolean getEager() {
        return eager;
    }

    /**
     * Sets eager flag for description.
     *
     * @param e new eager flag
     *
     * @return old eager flag
     *
     * @see #getEager
     */
    public boolean setEager(boolean e) {
        boolean oe = eager;
        eager = e;

        return oe;
    }

    //
    // ReferenceResolver
    //

    /**
     * Find an attribute in this component context.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHere(Object name)
        throws SmartFrogResolutionException {
        Object result = null;
        try {
            result=context.sfResolveAttribute(name);
        } catch (SmartFrogContextException ex) {
            throw SmartFrogResolutionException.notFound(new Reference(name)
                , sfCompleteNameSafe());
        }
        return result;
    }

    /**
     * Find an attribute in this context.
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
    public Object sfResolveHere(Object name, boolean mandatory)
        throws SmartFrogResolutionException {
        try {
            return sfResolveHere(name);
        } catch (SmartFrogResolutionException e) {
            if (mandatory) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Resolves to the parent of this description.
     *
     * @return parent or null if no parent
     */
    public Object sfResolveParent() {
	if (sfParent() == null)
	    if (sfPrimParent() == null)
		return null;
	    else
		return sfPrimParent();
	else
	    return sfParent();
    }


    /**
     * Resolve a given reference. Forwards to indexed resolve with index 0.
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException occurred while resolving
     */
    public Object sfResolve(Reference r) throws SmartFrogResolutionException {
        Object obj = sfResolve(r, 0);
        try {
            if ((sflog()!= null) && sflog().isTraceEnabled()) {
                sflog().trace("sfResolved: "+r.toString()+" to "+obj.toString());
            }
        } catch (Exception ex) {ex.printStackTrace();}//ignore}
        return obj;
    }

    /**
     * Resolves a refererence starting at given index.
     *
     * @param r reference to resolve
     * @param index index in reference to start to resolve
     *
     * @return Object refernce
     *
     * @throws SmartFrogResolutionException failure while resolving reference
     */
    public Object sfResolve(Reference r, int index)
        throws SmartFrogResolutionException {
        return r.resolve(this, index);
    }

    /**
     * Creates a deep copy of the compiled component.  Parent, eager flag are
     * the same in the copy. Resolvers are blanked in the copy to avoid
     * resolution confusion. Resolution data object reference is copied if it
     * implements the Copying interface, otherwise the pointer is shared with
     * the copy.
     * Note: Use "copy", not clone, to deploy more than one component using the same ComponentDescription
     * @return copy of component
     */
    public Object copy() {
        ComponentDescription res = null;
        res = (ComponentDescription) clone();
        res.setContext((Context) context.copy());
        res.setParent(parent);
        res.setEager(eager);

        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            Object value = res.sfContext().get(e.nextElement());

            if (value instanceof ComponentDescription) {
                ((ComponentDescription) value).setParent(res);
            }
        }

        return res;
    }

    /**
     * Gets the clone of the ComponentDescription.
     * Note: Use "copy" to deploy more than one component using the same ComponentDescription
     * @return cloned object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cex) {
            // should never happen
        }

        return null;
    }

    /**
     * Returns a string representation of the component. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. Large description trees should be written out using
     * writeOn since memory for large strings runs out quick!
     *
     * @return string representation of component
     */
    public String toString() {
        StringWriter sw = new StringWriter();

        try {
            context.writeOn(sw);
        } catch (IOException ioex) {
            // ignore should not happen
        }

        return sw.toString();
    }

    /**
     * Writes this component description on a writer. Used by toString. Should
     * be used instead of toString to write large descriptions to file, since
     * memory can become a problem given the LONG strings created
     *
     * @param ps writer to write on
     *
     * @throws IOException failure while writing
     */
    public void writeOn(Writer ps) throws IOException {
	writeOn(ps, 0);
    }

    /**
     * Writes this component description on a writer. Used by toString. Should
     * be used instead of toString to write large descriptions to file, since
     * memory can become a problem given the LONG strings created
     *
     * @param ps writer to write on
     * @param indent the indent to use for printing offset
     *
     * @throws IOException failure while writing
     */
    public void writeOn(Writer ps, int indent) throws IOException {
	ps.write("extends " + (getEager() ? "" : "LAZY "));

	if (context.size() > 0) {
	    ps.write(" {\n");
	    context.writeOn(ps, indent+1);
	    tabPad(ps, indent); ps.write('}');
	} else {
	    ps.write(';');
	}
    }


    /**
     * Internal method to pad out a writer.
     *
     * @param ps writer to tab to
     * @param amount amount to tab
     *
     * @throws IOException failure while writing
     */
    protected void tabPad(Writer ps, int amount) throws IOException {
        for (int i = 0; i < amount; i++)
            ps.write("  ");
    }

    /**
     * Visit every node in the tree, applying an action to that node. The nodes
     * may be visited top-down or bottom-up
     *
     * @param action the action to apply
     * @param topDown true if top-down, false if bottom-up
     *
     * @throws Exception error during applying an action
     */
    public void visit(CDVisitor action, boolean topDown)
        throws Exception {
        Object name;
        Object value;

        if (topDown) {
            action.actOn(this);
        }

        for (Enumeration e = ((Context) context.clone()).keys();
                e.hasMoreElements();) {
            name = e.nextElement();

            if ((value = context.get(name)) instanceof ComponentDescription) {
                ((ComponentDescription) value).visit(action, topDown);
            }
        }

        if (!topDown) {
            action.actOn(this);
        }
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * default parser phases
     *
     * @param String url to convert to ComponentDescription
     *
     * @return process compound description default phases Resolved
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url)
        throws SmartFrogException, RemoteException {
        return sfComponentDescription(url,null,null);
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * some parser phases
     *
     * @param String url to convert to ComponentDescription. The url is used to
     *              select the parser selecting any ending after the last '.'
     * @param Vector parser phases to apply. If the vector is null, then all
     *    the default phases are applied
     * @param Rererence ref reference to resolve in Description.
     *
     * @return process compound description 'phases' Resolved
     *
     * @throws SmartFrogRuntimeException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url,
                  Vector phases, Reference ref)
        throws SmartFrogException{
        String language = url;
        return sfComponentDescription(url,language, phases, ref);
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * some parser phases
     *
     * @param String url to convert to ComponentDescription
     * @param String language to select appropriate parser
     * @param Vector parser phases to apply. If the vector is null, then all
     *    the default phases are applied
     * @param Rererence ref reference to resolve in ComponentDescription.
     *        If ref is null the whole result ComponentDescription is returned.
     *
     * @return process the selected ComponentDescription after compound
     *         description 'phases' are resolved
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url,
                  String language, Vector phases, Reference ref)
        throws SmartFrogException {
        Phases descr = null;
        try {
            descr = (new SFParser(language)).sfParseResource(url);
        } catch (Exception thr) {
            throw new SmartFrogException("Error creating parser for '"+url+"'. "
                + MessageUtil.formatMessage(MSG_ERR_PARSE)
                +"["+thr.toString()+"]", thr);
        }
        try {
            if (phases==null) {
               descr = descr.sfResolvePhases();
            } else {
               descr = descr.sfResolvePhases(phases);
            }
        } catch (Exception thr) {
            throw new SmartFrogException ("Error during parsing of '"+url+"'. "
                +MessageUtil.formatMessage(MSG_ERR_RESOLVE_PHASE)
                +"["+thr.toString()+"]", thr);
        }
        Object obj=null;
        if (ref !=null) {
            obj = descr.sfAsComponentDescription().sfResolve(ref);
        } else {
            obj = descr.sfAsComponentDescription();
        }
        if (!(obj instanceof ComponentDescription)){
           throw new SmartFrogResolutionException(null,null,"Error resolving '"
                 +ref.toString()+"' in "+ url
                 + ". The result is not a ComponentDescription, resolved to: "
                 + obj.toString()
                 +" ("+obj.getClass().getName()+")" );
        }
        return (ComponentDescription) obj;
    }



    /**
      * Utility method that gets Component Description for a String after applying
      * some parser phases
      *
      * @param String description to parse into ComponentDescription
      * @param String language to select appropriate parser ('sf')
      * @param Vector parser phases to apply. If the vector is null, then all
      *    the default phases are applied
      * @param Rererence ref reference to resolve in ComponentDescription.
      *        If ref is null the whole result ComponentDescription is returned.
      *
      * @return process the selected ComponentDescription after compound
      *         description 'phases' are resolved
      *
      * @throws RemoteException In case of network/rmi error
      * @throws SmartFrogRuntimeException In case of SmartFrog system error
      */
     public static ComponentDescription sfComponentDescriptionFromStr(String description,
                   String language, Vector phases, Reference ref)
         throws SmartFrogException {
         Phases descr = null;
         try {
             descr = (new SFParser(language)).sfParse(description);
         } catch (Throwable thr) {
             throw SmartFrogException.forward(MessageUtil.formatMessage(MSG_ERR_PARSE), thr);
         }
         try {
             if (phases==null) {
                descr = descr.sfResolvePhases();

             } else {
                descr = descr.sfResolvePhases(phases);
             }
         } catch (Throwable thr) {
             throw SmartFrogException.forward(MessageUtil.formatMessage(MSG_ERR_RESOLVE_PHASE), thr);
         }
         Object obj=null;
         if (ref !=null) {
             obj = descr.sfAsComponentDescription().sfResolve(ref);
         } else {
             obj = descr.sfAsComponentDescription();
         }
         if (!(obj instanceof ComponentDescription)){
            throw new SmartFrogResolutionException(null,null,"Error resolving '"
                  +ref.toString()+"' in description \n"+ description + "\n"
                  + ". The result is not a ComponentDescription, resolved to: "
                  + obj.toString()
                  +" ("+obj.getClass().getName()+")" );
         }
         return (ComponentDescription) obj;
     }


    /**
     * Adds system properties to component description.
     * Uses startWith parameter as filter.
     *
     * @param startWith system property label (ex. org.smartfrog)
     * @param compDesc configuration description where to add system properties.
     * @return this configuration description with system properties added.
     */
    public static ComponentDescription addSystemProperties(String startWith,
        ComponentDescription compDesc) {
        Properties props = System.getProperties();
        for (Enumeration e = props.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement().toString();
            //  System.out.println("- Read:    "+key+
            //                    "\n, Filter: "+startWith);
            if (key.startsWith(startWith)) {
                Object value = props.get(key);
                try {
                    // convert to number
                    value = Double.valueOf((String)value);
                } catch (Exception ex) {
                // ignore, value is not a number
                }
                if ((value.toString().equals("true"))||
                    (value.toString().equals("false"))){
                  try {
                    // convert to boolean
                    value = Boolean.valueOf( (String) value);
                  } catch (Exception ex) {
                  // ignore, value is not a number
                  }
                }
                String cxtKey = key.substring(startWith.length());
                try {
                  compDesc.sfReplaceAttribute(cxtKey, value);
                  //System.out.println("*** Added: "+cxtKey.toString()+", "+value.toString());
                } catch (SmartFrogRuntimeException ex1) {
                   //System.err.println(ex1);
                }
            }
        }
        return compDesc;
    }

    /**
     *  Gets configuration description for Obj class. The short class name will
     *  be used to locate its Reference. Name of the description file will be in lower case.
     *  System properties that start with same package name as obj  are added to
     * ComponentDescription if addSystemProperties is true.
     * @param obj which class Component description has to be read
     * @param addSystemProperties to select if to add system properties
     * @param newPhases parser phases to apply to component description
     *  Takes default when vector is null. Default: type, link, function, predicate.
     * @return Component Description
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public static ComponentDescription getClassComponentDescription (Object obj,
          boolean addSystemProperties, Vector newPhases) throws SmartFrogException {
        //Get Component description for this log class
        String className = obj.getClass().toString();
        className = className.substring(6).replace('.','/');
        String urlDescription = className+".sf";
        Reference selectedRef = new Reference (className.substring(className.lastIndexOf("/")+1));
        Vector phases = new Vector();
        if (newPhases!=null){
            phases = newPhases;
        } else {
            phases.add("type");
            phases.add("link");
            phases.add("function");
            phases.add("predicate");
        }
        // Get componentDescription and
        ComponentDescription cmpDesc = ComponentDescriptionImpl.sfComponentDescription(
                                                                   urlDescription.toLowerCase()
                                                                 , phases
                                                                 , selectedRef);
        if (addSystemProperties){
            //add properties that start with package name.
            cmpDesc = ComponentDescriptionImpl.addSystemProperties(
                       obj.getClass().toString().substring(6)+"."
                     , cmpDesc);
        }

        return cmpDesc;
    }


    /** Special method to be used only by LogFactory to initialize log in
     *  ComponentDescription.
     *  This is because LogImpl uses ComponentDescription to initialize itself.
     *
     */
    static public void initLog(LogSF newlog){
        if (sflog==null) sflog = newlog;
    }

    /**
     *  To log into sfCore logger. This method should be used to log Core messages
     * @return Logger implementing LogSF and Log
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public LogSF sflog() {
       return sflog;
    }



}
