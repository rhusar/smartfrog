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

import java.util.*;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.PrettyPrinting;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

//For utility methods
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.reference.ReferenceResolverHelperImpl;

import org.smartfrog.sfcore.common.*;
import java.rmi.*;

import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.languages.sf.PhaseNames;

import java.io.*;


/**
 * Defines the context class used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 */
public class ComponentDescriptionImpl extends ReferenceResolverHelperImpl implements Serializable, Cloneable,
    ComponentDescription, MessageKeys, PrettyPrinting {


    /** Context of attributes (key value pairs). */
    public Context sfContext;

    /** Parent of this description. */
    public ComponentDescription parent;

    /** PrimParent of this description. */
    public Prim primParent;

    /** Whether this description is eager or lazy. */
    public boolean eager;

    /** Log: it cannot be initialized before LogImpl is ready
     * LogImpl uses ComponentDescription.sfResolve to read its initial
     * configuration */
    private static  LogSF sfLog= null;



    /**
     * Constucts ComponentDescriptionImpl with parent component and context.
     *
     * @param parent parent component
     * @param cxt context for description
     * @param eager eager flag
     */
    public ComponentDescriptionImpl(ComponentDescription parent, Context cxt, boolean eager) {
        if (cxt == null)  //@todo remove all the new ContextImpl() throughout the code, replace by null!
           this.sfContext = new ContextImpl();
        else
           this.sfContext = cxt;
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

    /** Reference that caches cannonical name. */
//    protected Reference sfCompleteName = null; //No cache. Conflict with Schema error report

    /**
     * Returns the complete name for this component from the root of the
     * application.
     *
     * @return reference of attribute names to this component
     */
    public Reference sfCompleteName() {
            Object cdParent = sfResolveParent();
            if (cdParent == null) {
                return new Reference();
            }

            Reference r = null;
            Object key = null;
            if (cdParent instanceof ComponentDescription){
                r = ((ComponentDescription)cdParent).sfCompleteName();
                key = ((ComponentDescription)cdParent).sfAttributeKeyFor(this);
            } else if (cdParent instanceof Prim){
                try {
                    r = ((Prim)cdParent).sfCompleteName();
                    key = ((Prim)cdParent).sfAttributeKeyFor(this);
                } catch (RemoteException ex) {
                    if ((sfLog()!= null) && sfLog().isErrorEnabled()) sfLog().err(ex.getMessage(),ex);
                    else ex.printStackTrace();
                    //If problem when resolving remote parent, report it in the name
                    if (r==null){
                        r = new Reference();
                        r.addElement(new HereReferencePart("*CDPrimParentError*"));
                    }
                }
            } else {
                return new Reference();
            }

            Reference sfCompleteName= (Reference)r.clone();

            if (key!=null) {
                sfCompleteName.addElement(ReferencePart.here(key));
            } else {
                //NOTE: this is a best effort name. It will not work if there are two attributes with equal
                // descriptions because it will always use the name of the first one. Not so relevant because
                // we cannot make changes in a copy that would have effect in the remote component.
                String name = "";
                try {
                    // we only obtain a copy when going through a remote object
                    if (cdParent instanceof Prim){
                        Object keyName = ((Prim)cdParent).sfContext().sfAttributeKeyForEqual(this);
                        if (keyName!=null) name=keyName.toString();
                    }
                } catch (Throwable e) {
                    if (sfLog().isIgnoreEnabled()) { sfLog().ignore("Problem trying to get the real name of a copied CD",e);}
                }
                sfCompleteName.addElement(new HereReferencePart(name+"*copy*"));
               if (((sfLog()!= null) && sfLog().isTraceEnabled())){
                    sfLog().trace("Internal error generating CD complete name - CD is a copy: "+sfCompleteName); //or child not named in parent yet
               }
            }
        return sfCompleteName;
    }

    /**
     * Parentage changed in component hierachy.
     * Actions: sfCompleteName cache is cleaned
     */
    public void sfParentageChanged() {
        ////       sfCompleteName=null;
    }

    /**
     * Adds an attribute to this component description under given name. If the attribute
     * value is a component description, then its prim parent is
     * set to this.

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
        ComponentDescription valueParent = null;
        try {
            if (value instanceof ComponentDescription) {
                //Set right parentage for ComponentDescription
                valueParent = ((ComponentDescription)value).sfParent();
                ((ComponentDescription)value).setParent(this);
            }
            return sfContext.sfAddAttribute(name, value);
        } catch (SmartFrogContextException ex) {
            if (valueParent!=null){
                ((ComponentDescription)value).setParent(valueParent);
            }
            //ex.init(this);
            throw ex;
        }
    }

    /**
     * Removes an attribute from this component.
     *
     * @param name of attribute to be removed
     *
     * @return removed attribute value if successfull or null if not. If the attribute
     * value removed is a component description, then its parent is
     * removed as well.

     *
     * @throws SmartFrogRuntimeException when name is null
     */
    public synchronized Object sfRemoveAttribute(Object name)
        throws SmartFrogRuntimeException {
        try {
            Object value = sfContext.sfRemoveAttribute(name);
            if (value instanceof ComponentDescription) {
                ((ComponentDescription)value).setParent(null);
            }
            return value;
        } catch (SmartFrogContextException ex) {
            //ex.init(this);
            throw ex;
        }
    }

    /**
     * Replace named attribute in component context. If attribute is not
     * present it is added to the context. If the attribute
     * value added is a component description, then its parent is
     * set to this and/or if the one removed is a component description then
     * its parent is reset.
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
        ComponentDescription valueParent = null;
        try {
            if (value instanceof ComponentDescription) {
                //Set right parentage for ComponentDescription
                valueParent = ((ComponentDescription)value).sfParent();
                ((ComponentDescription)value).setParent(this);
            }
            Object oldValue = sfContext.sfReplaceAttribute(name, value);
            if ((oldValue!=null) && (oldValue instanceof ComponentDescription) && (oldValue != value)) {
               ((ComponentDescription)oldValue).setParent(null);
            }
            return oldValue;
        } catch (SmartFrogContextException ex) {
            if (valueParent!=null){
                ((ComponentDescription)value).setParent(valueParent);
            }
            //ex.init(this);
            throw ex;
        }
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
        if (value instanceof Remote) {
            //When using stubs we need to compare equality
            return sfContext.sfAttributeKeyForEqual(value);
        } else {
            // == for non remote objects.
            return sfContext.sfAttributeKeyFor(value);
        }
    }

    /**
     * Returns true if the context contains value.
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     */
    public boolean sfContainsValue(Object value) {
        if (value instanceof Remote) {
            //When using stubs we need to compare equality
            return sfContext.sfContainsValue(value);
        } else {
            // == for non remote objects.
            return sfContext.sfContainsRefValue(value);
        }
    }


    /**
     * Returns true if the context contains attribute.
     * @param attribute to check
     *
     * @return true if context contains key, false otherwise
     */
    public boolean sfContainsAttribute(Object attribute) {
       return sfContext.containsKey(attribute);
    }


    /**
     * Returns an ordered iterator over the attribute names in the context.
     * The remove operation of this Iterator won't affect
     * the contents of ConponentDescription
     * @return iterator
     */
    public  Iterator sfAttributes() {
        return sfContext.sfAttributes();
    }

    /**
     * Returns an ordered iterator over the values in the context.
     * The remove operation of this Iterator won't affect
     * the contents of the ComponentDescription
     *
     * @return iterator
     */
    public  Iterator sfValues() {
      return sfContext.sfValues();
    }


    /**
     * Gets the context for this description.
     *
     * @return context
     *
     * @see #setContext
     */
    public Context sfContext() {
        return sfContext;
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
        Context oc = sfContext;
        sfContext = cxt;

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
     * @param newparent new parent component
     *
     * @return old parent for description
     *
     * @see #sfParent()
     */
    public ComponentDescription setParent(ComponentDescription newparent) {
        ComponentDescription op = parent;
	    primParent = null; // cannot have both!
        parent = newparent;

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
     * @see #setPrimParent(Prim)
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
     * @param newparent new parent component
     *
     * @return old parent for description
     *
     * @see #sfPrimParent()
     */
    public Prim setPrimParent(Prim newparent) {
        Prim op = primParent;
        parent = null; // cannot have both!
        primParent = newparent;

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
            result=sfContext.sfResolveAttribute(name);
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
       try {
          if (sfGetTags(name).contains("sfLocal")) {
             if (mandatory) {
                throw new SmartFrogResolutionException("Accessing local attribute " + name);
             }
             return null;
          }
       } catch (SmartFrogException e) {
          if (mandatory) {
             throw new SmartFrogResolutionException("Error accessing attribute tags " + name, e);
          }
          return null;
       }
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
        if (sfParent() == null) {
            if (sfPrimParent() == null) {
                return null;
            } else {
                return sfPrimParent();
            }
        } else {
            return sfParent();
        }
    }


    /**
     * Resolves a (copy of) a given reference. Forwards to indexed resolve with index 0
     * after making sure that the DATA flag is unset if necessary.
     * Method returns the resulting attribute value.
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException occurred while resolving
     */
    public Object sfResolve(Reference r) throws SmartFrogResolutionException {
        Reference rn = (Reference) r.copy();
        rn.setData(false);
        Object obj = sfResolve(r, 0);
        if (obj instanceof SFMarshalledObject){
            //  Unmarshall!Obj.
            try {
                obj = ((SFMarshalledObject)obj).get();
            } catch (IOException e) {
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(e.getMessage(),e);
            } catch (ClassNotFoundException e) {
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(e.getMessage(),e);
            }
        }
        try {
            if ((sfLog()!= null) && sfLog().isTraceEnabled()) {
                sfLog().trace("sfResolved: "+r.toString()+" to "+obj.toString());
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
        res.setContext((Context) sfContext.copy());
        res.setParent(parent);
        res.setEager(eager);

        for (Enumeration e = sfContext.keys(); e.hasMoreElements();) {
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
            sfContext.writeOn(sw);
        } catch (IOException ioex) {
            // ignore should not happen
        }

        return sw.toString();
    }

    /**
     * Writes this component description on a stream encoded as utf-8.
     *
     * @param ps utf-8 encoded stream to write on
     *
     * @throws IOException failure while writing
     */
    public void writeOn(OutputStream ps) throws IOException {
        writeOn(new OutputStreamWriter(ps, "utf-8"), 0);
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
        ps.write("extends " + (getEager() ? "" : "DATA "));

        if (sfContext.size() > 0) {
            ps.write(" {\n");
            sfContext.writeOn(ps, indent + 1);
            tabPad(ps, indent);
            ps.write('}');
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
        for (int i = 0; i < amount; i++) {
            ps.write("  ");
        }
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
    public void visit(CDVisitor action, boolean topDown) throws Exception {
       visit(action, topDown, true, new Stack());
    }

    /**
     * Visit every node in the tree, applying an action to that node. The nodes
     * may be visited top-down or bottom-up
     *
     * @param action the action to apply
     * @param topDown true if top-down, false if bottom-up
     * @param includeLazy  whether to visit into sub-nodes tagged LAZY
     *
     * @throws Exception error during applying an action
     */
    public void visit(CDVisitor action, boolean topDown, boolean includeLazy) throws Exception {
       visit(action, topDown, includeLazy, new Stack());
    }

     /**
     * Visit every node in the tree from path downwards, applying an action to that node. The nodes
     * may be visited top-down or bottom-up
     *
     * @param action the action to apply
     * @param topDown true if top-down, false if bottom-up
     * @param path the path through the CD hierarchy taken to get here
     *
     * @throws Exception error during applying an action
     */
     public void visit(CDVisitor action, boolean topDown, Stack path) throws Exception {
        visit(action, topDown, true, new Stack());
    }

         /**
     * Visit every node in the tree from path downwards, applying an action to that node. The nodes
     * may be visited top-down or bottom-up
     *
     * @param action the action to apply
     * @param topDown true if top-down, false if bottom-up
     * @param includeLazy  whether to visit into sub-nodes tagged LAZY
     * @param path the path through the CD hierarchy taken to get here
     *
     * @throws Exception error during applying an action
     */
     public void visit(CDVisitor action, boolean topDown, boolean includeLazy, Stack path)
        throws Exception {
        Object name;
        Object value;

        if (topDown) {
            action.actOn(this, path);
        }

         path.push(this);
         for (Enumeration e = ((Context) sfContext.clone()).keys();
                e.hasMoreElements();) {
            name = e.nextElement();

            if ((value = sfContext.get(name)) instanceof ComponentDescription) {
                if (includeLazy || ((ComponentDescription)value).getEager()) {
                    ((ComponentDescription) value).visit(action, topDown, includeLazy, path);
                }
            }
        }
        path.pop();

        if (!topDown) {
            action.actOn(this, path);
        }
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * default parser phases
     *
     * @param url URL to convert to ComponentDescription
     *
     * @return process compound description default phases Resolved
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url)
        throws SmartFrogException {
        return sfComponentDescription(url,null,null);
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * some parser phases
     *
     * @param url to convert to ComponentDescription. The url is used to
     *              select the parser selecting any ending after the last '.'
     * @param phases parser phases to apply. If the vector is null, then all
     *    the default phases are applied
     * @param ref reference to resolve in Description.
     *
     * @return process compound description 'phases' Resolved
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url,
                  Vector phases, Reference ref)
        throws SmartFrogException{
        String language = url;
        return sfComponentDescription(url, language, phases, ref);
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * some parser phases
     *
     * @param url URL to convert to ComponentDescription
     * @param language language to select appropriate parser
     * @param phases phases to apply. If the vector is null, then all
     *    the default phases are applied
     * @param ref reference to resolve in ComponentDescription.
     *        If ref is null the whole result ComponentDescription is returned.
     *
     * @return process the selected ComponentDescription after compound
     *         description 'phases' are resolved
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url,
                  String language, Vector phases, Reference ref)
        throws SmartFrogException {
        return sfComponentDescription(url,language, phases, ref, null);
    }

    /**
     * Utility method that gets Component Description for URL after applying
     * some parser phases
     *
     * @param url URL to convert to ComponentDescription
     * @param language language to select appropriate parser
     * @param phases phases to apply. If the vector is null, then all
     *    the default phases are applied
     * @param ref reference to resolve in ComponentDescription.
     *        If ref is null the whole result ComponentDescription is returned.
     * @param codebase suggested codebase for the classloader
     *
     * @return process the selected ComponentDescription after compound
     *         description 'phases' are resolved
     *
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ComponentDescription sfComponentDescription(String url,
                  String language, Vector phases, Reference ref, String codebase)
        throws SmartFrogException {
        Phases descr = null;
        try {
            descr = (new SFParser(language)).sfParseResource(url,codebase);
        } catch (Exception thr) {
            throw new SmartFrogResolutionException("Error creating parser for '"+url+"'. "
                + MessageUtil.formatMessage(MSG_ERR_PARSE)
                +" ["+ thr.toString()+"]", thr);
        }
        try {
            if (phases==null) {
               descr = descr.sfResolvePhases();
            } else {
               descr = descr.sfResolvePhases(phases);
            }
        } catch (Exception thr) {
            throw new SmartFrogResolutionException ("Error during parsing of '"+url+"'. "
                +MessageUtil.formatMessage(MSG_ERR_RESOLVE_PHASE)
                +" ["+ thr.toString()+"]", thr);
        }
        Object obj=null;
        if (ref !=null) {
            obj = descr.sfAsComponentDescription().sfResolve(ref);
        } else {
            obj = descr.sfAsComponentDescription();
        }
        if (!(obj instanceof ComponentDescription)){
           throw new SmartFrogResolutionException(null,null,"Error resolving '"
                 +ref+"' in "+ url
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
      * @param description to parse into ComponentDescription
      * @param language to select appropriate parser ('sf')
      * @param phases phases to apply. If the vector is null, then all
      *    the default phases are applied
      * @param ref reference to resolve in ComponentDescription.
      *        If ref is null the whole result ComponentDescription is returned.
      *
      * @return process the selected ComponentDescription after compound
      *         description 'phases' are resolved
      *
      * @throws SmartFrogException In case of SmartFrog system error
      */
     public static ComponentDescription sfComponentDescriptionFromStr(InputStream description,
                   String language, Vector phases, Reference ref)
         throws SmartFrogException {
         return sfComponentDescriptionFromStr(description, language, phases, ref, null);
     }
    /**
      * Utility method that gets Component Description for a String after applying
      * some parser phases
      *
      * @param description to parse into ComponentDescription
      * @param language to select appropriate parser ('sf')
      * @param phases phases to apply. If the vector is null, then all
      *    the default phases are applied
      * @param ref reference to resolve in ComponentDescription.
      *        If ref is null the whole result ComponentDescription is returned.
     * @param codebase suggested codebase for the classloader
      *
      * @return process the selected ComponentDescription after compound
      *         description 'phases' are resolved
      *
      * @throws SmartFrogException In case of SmartFrog system error
      */
     public static ComponentDescription sfComponentDescriptionFromStr(InputStream description,
                   String language, Vector phases, Reference ref, String codebase)
         throws SmartFrogException {
         Phases descr = null;
         try {
             descr = (new SFParser(language)).sfParse(description, codebase);
         } catch (Throwable thr) {
             throw SmartFrogResolutionException.forward(MessageUtil.formatMessage(MSG_ERR_PARSE), thr);
         }

         Object obj = resolveCDfromPhases(phases, ref, descr);

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
      * Utility method that gets Component Description for a String after applying
      * some parser phases
      *
      * @param description to parse into ComponentDescription
      * @param language to select appropriate parser ('sf')
      * @param phases phases to apply. If the vector is null, then all
      *    the default phases are applied
      * @param ref reference to resolve in ComponentDescription.
      *        If ref is null the whole result ComponentDescription is returned.
      *
      * @return process the selected ComponentDescription after compound
      *         description 'phases' are resolved
      *
      * @throws SmartFrogException In case of SmartFrog system error
      */
     public static ComponentDescription sfComponentDescriptionFromStr(String description,
                   String language, Vector phases, Reference ref)
         throws SmartFrogException {
         return sfComponentDescriptionFromStr(description, language, phases, ref, null);
     }
    /**
      * Utility method that gets Component Description for a String after applying
      * some parser phases
      *
      * @param description to parse into ComponentDescription
      * @param language to select appropriate parser ('sf')
      * @param phases phases to apply. If the vector is null, then all
      *    the default phases are applied
      * @param ref reference to resolve in ComponentDescription.
      *        If ref is null the whole result ComponentDescription is returned.
     * @param codebase suggested codebase for the classloader
      *
      * @return process the selected ComponentDescription after compound
      *         description 'phases' are resolved
      *
      * @throws SmartFrogException In case of SmartFrog system error
      */
     public static ComponentDescription sfComponentDescriptionFromStr(String description,
                   String language, Vector phases, Reference ref, String codebase)
         throws SmartFrogException {
         Phases descr = null;
         try {
             descr = (new SFParser(language)).sfParse(description, codebase);
         } catch (Throwable thr) {
             throw SmartFrogResolutionException.forward(MessageUtil.formatMessage(MSG_ERR_PARSE), thr);
         }

         Object obj = resolveCDfromPhases(phases, ref, descr);

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
       * Private Utility method that gets Component Description from the parser parser phases
       * after resolving the "phases" set and extracting "ref"
       * @param phases phases to apply. If the vector is null, then all
       *    the default phases are applied
       * @param ref reference to resolve in ComponentDescription.
       *        If ref is null the whole result ComponentDescription is returned.
       * @param descr description to parse into ComponentDescription
       *
       * @return process the selected ComponentDescription after compound
       *         description 'phases' are resolved
       *
       * @throws SmartFrogException In case of SmartFrog system error
       */

    private static Object resolveCDfromPhases(Vector phases, Reference ref, Phases descr) throws
        SmartFrogException {
        try {
            if (phases==null) {
               descr = descr.sfResolvePhases();

            } else {
               descr = descr.sfResolvePhases(phases);
            }
        } catch (Throwable thr) {
            throw SmartFrogResolutionException.forward(MessageUtil.formatMessage(MSG_ERR_RESOLVE_PHASE), thr);
        }
        Object obj=null;
        if (ref !=null) {
            obj = descr.sfAsComponentDescription().sfResolve(ref);
        } else {
            obj = descr.sfAsComponentDescription();
        }
        return obj;
    }


     /**
      * Adds system properties to component description using parser.sfParsePrimitiveValue()
      * for conversion using 'sf' language.
      * Uses startWith parameter as filter.
      *
      * @param startWith system property label (ex. org.smartfrog)
      * @param compDesc configuration description where to add system properties.
      * @return this configuration description with system properties added.
      */
     public static ComponentDescription addSystemProperties(String startWith,
        ComponentDescription compDesc){
        return addSystemProperties(startWith, compDesc, "sf");
    }

    /**
     * Adds system properties to component description using parser.sfParsePrimitiveValue()
     * for conversion.
     * Uses startWith parameter as filter.
     *
     * @param startWith system property label (ex. org.smartfrog)
     * @param compDesc configuration description where to add system properties.
     * @param language language extension to use.
     * @return this configuration description with system properties added.
     */
    public static ComponentDescription addSystemProperties(String startWith,
        ComponentDescription compDesc, String language) {
        SFParser parser = null;
        Properties props = null;
        try {
            try {
              props = System.getProperties();
            } catch (SecurityException e) {
              throw SmartFrogException.forward(
                  "Access to System.getProperties() blocked " +
                  "by a security manager", e);
            }
            parser = new SFParser(language);
            for (Enumeration e = props.keys(); e.hasMoreElements(); ) {
                String key = e.nextElement().toString();
                //  System.out.println("- Read:    "+key+
                //                    "\n, Filter: "+startWith);
                if (key.startsWith(startWith)) {
                    Object value = props.get(key);
                    value = value.toString();
                    try {
                       value = parser.sfParsePrimitiveValue(value.toString());
                    } catch (SmartFrogParseException ex) {
                       // ignore, value is not a PrimValue it is a String
                    } catch (SecurityException se) {
                      if ((sfLog!=null)&&(sfLog.isErrorEnabled())){
                          sfLog.error("Reading system property '"+ key +"' with string value '"+value+"', but access to this property blocked by a security manager");
                      }
                    } catch (Throwable thr) {
                        // ignore, value is not a PrimValue it is a String
                    }

                    String cxtKey = key.substring(startWith.length());
                    try {
                        compDesc.sfReplaceAttribute(cxtKey, value);
                        if ((sfLog!=null)&&(sfLog.isTraceEnabled())){
                            sfLog.trace("Adding to CompDesc system property '"+ cxtKey +"' with value = "+value+", "+ value.getClass().getName());
                        }
                    } catch (SmartFrogRuntimeException ex1) {
                        //System.err.println(ex1);
                    }
                }
            }
//        } catch (SmartFrogException ex2) {
        } catch (Exception ex2) {
          if (sfLog != null) {
            if (sfLog.isErrorEnabled()) {
              sfLog.err("Error adding system properties to ComponentDescription", ex2);
            }
          } else {
            System.err.println("Error adding system properties to ComponentDescription: " + ex2.getMessage());
            if (org.smartfrog.sfcore.common.Logger.logStackTrace) {
              ex2.printStackTrace();
            }
          }
        }
        return compDesc;
    }

    /**
         *  Gets configuration description for Obj class using "sf" language. The short class name will
         *  be used to locate its Reference. Name of the description file will be in lower case.
         *  System properties that start with same package name as obj  are added to
         * ComponentDescription if addSystemProperties is true.
         *  Note: If obj is java.lang.string, then the string is used as the class name. Useful for static classes
         * @param obj which class Component description has to be read (or java.lang.String)
         * @param addSystemProperties to select if to add system properties
         * @param newPhases parser phases to apply to component description
         * languageExtension provide extenstion for the language used (ex. sf by default)
         *  Takes default when vector is null. Default: type, link, function, predicate.
         * @return Component Description
         * @throws SmartFrogException In case of SmartFrog system error
         */
        public static ComponentDescription getClassComponentDescription (Object obj,
          boolean addSystemProperties, Vector newPhases) throws SmartFrogException {
          return getClassComponentDescription (obj,addSystemProperties,newPhases,"sf");
      }

    /**
     *  Gets configuration description for Obj class using the right parser for language. The short class name will
     *  be used to locate its Reference. Name of the description file will be in lower case.
     *  System properties that start with same package name as obj  are added to
     * ComponentDescription if addSystemProperties is true.
     *  Note: If obj is java.lang.string, then the string is used as the class name. Useful for static classes
     * @param obj which class Component description has to be read (or java.lang.String)
     * @param addSystemProperties to select if to add system properties
     * @param newPhases parser phases to apply to component description
     * @param languageExtension provide extenstion for the language used (ex. sf by default)
     *  Takes default when vector is null. Default: type, link, function, predicate.
     * @return Component Description
     * @throws SmartFrogException In case of SmartFrog system error
     */
    public static ComponentDescription getClassComponentDescription (Object obj,
          boolean addSystemProperties, Vector newPhases,String languageExtension) throws SmartFrogException {
        //Get Component description for this log class
        String className = obj.getClass().toString();
        if (obj instanceof java.lang.String) className = obj.toString();
        if (className.startsWith("class ")) {
            className = className.substring(6);
        }
        String tempClassName = className.replace('.','/');
        String urlDescription = tempClassName+"."+languageExtension;
        Reference selectedRef = new Reference (tempClassName.substring(tempClassName.lastIndexOf("/")+1));

        Vector phases = null;
        if (newPhases!=null){
            phases = newPhases;
        } else {
            Phases top = null;
            top = new SFParser(languageExtension).sfParseResource( urlDescription.toLowerCase());
            phases = top.sfGetPhases();
            //This only works for SF 1 language. This should be more generic.
            if ((languageExtension.equals("sf"))&&(phases.contains(PhaseNames.SFCONFIG))){
                phases.remove(PhaseNames.SFCONFIG);
            }
        }
        // Get componentDescription and Entry
        
        ComponentDescription cmpDesc = ComponentDescriptionImpl.sfComponentDescription(
                                                                   urlDescription.toLowerCase()
                                                                 , phases
                                                                 , selectedRef);
        if (addSystemProperties){
            //add properties that start with package name.
            cmpDesc = ComponentDescriptionImpl.addSystemProperties( className+".", cmpDesc, languageExtension);
        }

        return cmpDesc;
    }


    /** Special method to be used only by LogFactory to initialize log in
     *  ComponentDescription.
     *  This is because LogImpl uses ComponentDescription to initialize itself.
     *  @param newlog new log
     */
    static public void initLog(LogSF newlog){
        if (sfLog == null) {
            sfLog = newlog;
        }
    }

    /**
     *  To log into sfCore logger. This method should be used to log Core messages
     * @return Logger implementing LogSF and Log
     */
    public LogSF sfLog() {
       return sfLog;
    }

    /**
     * Creates diagnostics report
     * @return Component description
     */
    public ComponentDescription sfDiagnosticsReport() {
      ComponentDescription cd = null;
      try {
        cd = new ComponentDescriptionImpl(null,new ContextImpl(), false);
        cd.setParent(this);
        StringBuffer report = new StringBuffer();
        Diagnostics.doReport(report,this);
        cd.sfReplaceAttribute(SmartFrogCoreKeys.SF_DIAGNOSTICS_REPORT, report );
      } catch (Throwable thr){
        //ignore
        if (sfLog().isWarnEnabled()){ sfLog().warn(thr);}
      }
      return cd;
    }

   // implementation of the TAGS interface


    /**
     * Set the TAGS for an attribute. TAGS are simply uninterpreted strings associated
     * with each attribute.
     *
     * @param name attribute key for tags.
     * @param tags a set of tags
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public void sfSetTags(Object name, Set tags) throws SmartFrogContextException {
         sfContext.sfSetTags(name, tags);
    }

    /**
     * Get the TAGS for an attribute. TAGS are simply uninterpreted strings associated
     * with each attribute.
     *
     * @param name attribute key for tags.
     * @return the set of tags
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public Set sfGetTags(Object name) throws SmartFrogContextException {
          return sfContext.sfGetTags(name);
    }

    /**
     * add a tag to the tag set of an attribute
     *
     * @param name attribute key for tags.
     * @param tag  a tag to add to the set
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public void sfAddTag(Object name, String tag) throws SmartFrogContextException {
         sfContext.sfAddTag(name, tag);
    }

    /**
     * remove a tag from the tag set of an attribute if it exists
     *
     * @param name attribute key for tags.
     * @param tag  a tag to remove from the set
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public void sfRemoveTag(Object name, String tag) throws SmartFrogContextException {
         sfContext.sfRemoveTag(name, tag);
    }

    /**
     * add a tag to the tag set of an attribute
     *
     * @param name attribute key for tags.
     * @param tags a set of tags to add to the set
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
     public void sfAddTags(Object name, Set tags) throws SmartFrogContextException {
         sfContext.sfAddTags(name, tags);
    }

    /**
     * remove a tag from the tag set of an attribute if it exists
     *
     * @param name attribute key for tags.
     * @param tags a set of tags to remove from the set
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public void sfRemoveTags(Object name, Set tags) throws SmartFrogContextException {
          sfContext.sfRemoveTags(name, tags);
    }

    /**
     * Return an iterator over the tags for an attribute
     *
     * @param name the name of the attribute.
     * @return an iterator over the tags
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public Iterator sfTags(Object name) throws SmartFrogContextException {
          return sfContext.sfTags(name);
    }

    /**
     * Return whether or not a tag is in the list of tags for an attribute
     *
     * @param name the name of the attribute.
     * @param tag  the tag to chack
     * @return whether or not the attribute has that tag
     * @throws SmartFrogException
     *          the attribute does not exist
     */
    public boolean sfContainsTag(Object name, String tag) throws SmartFrogContextException {
          return sfContext.sfContainsTag(name, tag);
    }

     // implementation of the TAGSComponent interface


    /**
     * Set the TAGS for this component. TAGS are simply uninterpreted strings associated
     * with each attribute.
     *
     * @param tags a set of tags
     * @throws SmartFrogException
     *          the attribute does not exist;
     */
    public void sfSetTags(Set tags) throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                parent.sfSetTags(key, tags);
            } else {
                Object key = primParent.sfAttributeKeyFor(this);
                primParent.sfSetTags(key, tags);
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }

    /**
     * Get the TAGS for this component. TAGS are simply uninterpreted strings
     * associated with each attribute.
     * 
     * @return the set of tags
     * @throws SmartFrogContextException the attribute does not exist;
     */
    public Set sfGetTags() throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                return parent.sfGetTags(key);
            } else if (primParent != null) {
                Object key = primParent.sfAttributeKeyFor(this);
                if (key == null) {
                    throw new SmartFrogContextException("No name found for " + sfCompleteNameSafe() + " in "
                            + primParent.sfCompleteName() + ", impossible to get its Tags");
                }
                return primParent.sfGetTags(key);
            } else {
                return new HashSet();
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }

    /**
     * add a tag to the tag set of this component
     *
     * @param tag  a tag to add to the set
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    public void sfAddTag(String tag) throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                parent.sfAddTag(key, tag);
            } else {
                Object key = primParent.sfAttributeKeyFor(this);
                primParent.sfAddTag(key, tag);
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }

    /**
     * remove a tag from the tag set of this component if it exists
     * 
     * @param tag
     *            a tag to remove from the set
     * @throws SmartFrogContextException
     *             the attribute does not exist;
     */
    public void sfRemoveTag(String tag) throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                parent.sfRemoveTag(key, tag);
            } else {
                Object key = primParent.sfAttributeKeyFor(this);
                primParent.sfRemoveTag(key, tag);
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }

    /**
     * add a tag to the tag set of this component
     * 
     * @param tags
     *            a set of tags to add to the set
     * @throws SmartFrogContextException
     *             the attribute does not exist;
     */
     public void sfAddTags(Set tags) throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                parent.sfAddTags(key, tags);
            } else {
                Object key = primParent.sfAttributeKeyFor(this);
                primParent.sfAddTags(key, tags);
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }

    /**
     * remove a tag from the tag set of this component if it exists
     * 
     * @param tags
     *            a set of tags to remove from the set
     * @throws SmartFrogContextException
     *             the attribute does not exist;
     */
    public void sfRemoveTags(Set tags) throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                parent.sfRemoveTags(key, tags);
            } else {
                Object key = primParent.sfAttributeKeyFor(this);
                primParent.sfRemoveTags(key, tags);
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }

    /**
     * Return whether or not a tag is in the list of tags for this component
     * 
     * @param tag
     *            the tag to chack
     * @return whether or not the attribute has that tag
     * @throws SmartFrogContextException
     *             the attribute does not exist
     */
    public boolean sfContainsTag(String tag) throws SmartFrogContextException {
        try {
            if (parent != null) {
                Object key = parent.sfAttributeKeyFor(this);
                return parent.sfContainsTag(key, tag);
            } else {
                Object key = primParent.sfAttributeKeyFor(this);
                return primParent.sfContainsTag(key, tag);
            }
        } catch (RemoteException e) {
            throw (SmartFrogContextException) SmartFrogContextException.forward(e);
        } catch (SmartFrogRuntimeException e) {
            // can not use forward method to down cast an exception
            throw new SmartFrogContextException(e);
        }
    }


    // hash code and equals
     /**
      * Compares the specified Object with this ComponentDescription for equality
      * Does not compare parentage but it compares LAZY.
      *
      * @param  o object to be compared for equality with this ComponentDescription
      * @return true if the specified Object is equal to this ComponentDescription
      */
     public synchronized boolean equals(Object o) {
         if (o == this)
             return true;

         if (!(o instanceof ComponentDescription))
             return false;

         if (eager != ((ComponentDescription)o).getEager() ){
             return false;
         }

         if (!((sfContext).equals((((ComponentDescription)o).sfContext())))){
             return false;
         }

         return true;
     }

     /**
      * Checks component description for same parentage
      * @param o parent to compare with
      * @return true if they share a parent
      */
     public boolean hasSameParent(ComponentDescription o) {
         if (primParent==null) {
           if (!(parent == o.sfParent())){
               return true;
           }
         } else {
           if (!(primParent == o.sfPrimParent())){
               return true;
           }
         }
         return false;
     }

     /**
      * Returns the hash code value for this ComponentDescription
      * Parentage is not included but LAZY is.
      */
     public synchronized int hashCode() {
         // Simple hashcode using Joshua Bloch's recommendation
         int result = 17;
         result = 37 * result + sfContext.hashCode();
         result = 37 * result + (eager ? 0 :1 );
         return result;
     }

}
