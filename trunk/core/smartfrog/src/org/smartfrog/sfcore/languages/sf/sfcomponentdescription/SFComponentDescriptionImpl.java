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
package org.smartfrog.sfcore.languages.sf.sfcomponentdescription;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;


import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.Phase;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the context class used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 */
public class SFComponentDescriptionImpl extends ComponentDescriptionImpl
    implements Serializable, Cloneable, SFComponentDescription, MessageKeys {

   /**
    *  cache the resoltion phases for this component - obtained by calls to
    *  sfGetPhases().
    */
   Vector phases = null;

   /**
    * Reference to sfConfig attribute
    */
   Reference sfConfigRef = new Reference(SmartFrogCoreKeys.SF_CONFIG);

   /**
    *  type of description. The type field is used as a prototype for this
    *  description. This means that the attributes of the (super)type are
    *  inherited by this component in a structural copy
    */
   public Reference type;


   /**
    *  Constuctor.
    *
    *@param  type    supertype for component
    *@param  parent  parent component
    *@param  cxt     context for description
    *@param  eager   eager flag
    */
   public SFComponentDescriptionImpl(Reference type, SFComponentDescription parent, Context cxt, boolean eager) {
      super(parent, cxt, eager);
      this.type = type;
   }


   /**
    *  Get prototype for this description. This is the component from which
    *  attributes get copied into this description.
    *
    * @return    type for this description
    *
    * @see #setType
    */
   public Reference getType() {
      return type;
   }


   /**
    *  Set new type for this component.
    *
    * @param  t  new prototype for description
    *
    * @return    old type
    *
    * @see #getType
    */
   public Reference setType(Reference t) {
      Reference ot = type;
      type = t;

      return ot;
   }


   /**
    *  Creates a deep copy of the compiled component. Parent, type and eager
    *  flag are the same in the copy. Resolvers are blanked in the copy to avoid
    *  resolution confusion. Resolution data object reference is copied if it
    *  implements the Copying interface, otherwise the pointer is shared with
    *  the copy.
    *
    *@return    copy of component
    */
   public Object copy() {
      SFComponentDescription res = null;
      res = (SFComponentDescription) clone();
      res.setType(type);
      res.setContext((Context) context.copy());
      res.setParent(parent);
      res.setEager(eager);

      for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
         Object value = res.sfContext().get(e.nextElement());

         if (value instanceof SFComponentDescription) {
            ((SFComponentDescription) value).setParent(res);
         }
      }

      return res;
   }


   /**
    *  Returns the clone.
    *
    * @return an Object clone
    */
   public Object clone() {
      return super.clone();
   }


    //
    // ReferenceResolver
    //

    /**
     * Resolves a refererence starting at given index. If the reference is
     * lazy, it is returned since component descriptions are not supposed to
     * chain on lazy references.
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
        if (!r.getEager() && (index == 0)) {
            return r;
        }
        return r.resolve(this, index);
    }



   //
   // ComponentResolver
   //

   /**
    *  Place resolves the component. This means iterating a number of times over
    *  the description tree, and doing place resolution. Places attributes which
    *  have a reference as a key in the right place.
    *
    * @throws  SmartFrogCompileResolutionException failed to place resolve
    */
   public void placeResolve() throws SmartFrogCompileResolutionException {
      ResolutionState resState = new ResolutionState();

      do {
         resState.clear();
         doPlaceResolve(resState);
      } while (resState.moreToResolve());
      if (resState.unresolved().size() > 0) {
         throw SmartFrogCompileResolutionException.placeResolution(null,sfCompleteName(),
               resState.unresolved(),null);
      }
   }


   /**
    *  Internal method to do recursion of place resolve.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogCompileResolutionException failed to place resolve
    */
   public void doPlaceResolve(ResolutionState resState) throws SmartFrogCompileResolutionException {
      // Hold removals. NOT creating the vector here saves memory at the
      // expense of two null checks below.
      Vector removals = null;

      // Resolve attributes
      for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Object value = context.get(key);
         try {
           // Get attribute and if key a reference try to place it in the
           // right component. Don't resolve value since it ain't mine
           if (key instanceof Reference) {
             // allocate removals if not already there
             if (removals == null) {
               removals = new Vector(5);
             }

             if (place( (Reference) key, value, resState)) {
         removals.addElement(key);
         }
           } else if (value instanceof ComponentResolver) {
             // Attribute value is resolvable, ask it to resolve itself
             ( (ComponentResolver) value).doPlaceResolve(resState);
           }
         } catch (Throwable thr){
           StringBuffer msg = new StringBuffer("Failed to resolve '");
           msg.append(key.toString());
           msg.append(" ");
           msg.append(value.toString());
           msg.append(";'");
           if (thr instanceof java.lang.StackOverflowError) {
             msg.append(". Possible cause: cyclic reference.");
           }
           throw new SmartFrogCompileResolutionException(msg.toString(), thr,
               sfCompleteName(), null, resState.unresolved());
         }
      }

      if (removals != null) {
         for (Enumeration e = removals.elements(); e.hasMoreElements(); ) {
            context.remove(e.nextElement());
         }
      }
   }


   /**
    *  Places an attribute key which is a reference. Finds the destination
    *  component indicated by the reference and moves the value to the
    *  destination (using last reference part in reference). If value is
    *  description, it is re-parented, and remembered in the state as needing
    *  resolution. Failure to locate the destination description is remembered
    *  in the state as well.
    *
    *@param  key       attribute key
    *@param  value     attribute value
    *@param  resState  resolution state
    */
   protected boolean place(Reference key, Object value, ResolutionState resState) {
      Object nam = ((HereReferencePart) key.lastElement()).getValue();
      ComponentDescription destDescription = null;

      try {
         // Try to find destination component
         Reference destKey = (Reference) key.clone();
         destKey.removeElement(destKey.lastElement());
         destDescription = (ComponentDescription) sfResolve(destKey);
      } catch (Exception rex) {
         // If we can't find the target component we leave the attribute
         // as unresolved
         resState.addUnresolved(key, sfCompleteName());

         return false;
      }

      // Found destination
      resState.haveResolved(true);

      // place value under simple name in destination
      destDescription.sfContext().put(nam, value);

      // Set new parent if value is component
      if (value instanceof ComponentDescription) {
         ((ComponentDescription) value).setParent(destDescription);

         // Remember to go and resolve the newly placed component
         resState.addUnresolved(value, destDescription.sfCompleteName());
      }

      return true;
   }


   /**
    *  Type resolves the component. This means iterating a number of times over
    *  the description tree, and doing type resolution. Type resolution finds
    *  the supertype and subtypes it into this component.
    *
    * @throws  SmartFrogCompileResolutionException failed to type resolve
    */
   public void typeResolve() throws SmartFrogCompileResolutionException {
      ResolutionState resState = new ResolutionState();

      do {
         resState.clear();
         doTypeResolve(resState);
      } while (resState.moreToResolve());

      if (resState.unresolved().size() > 0) {
         throw SmartFrogCompileResolutionException.typeResolution(
                 MessageUtil.formatMessage(MSG_UNRESOLVED_REFERENCE),
                 sfCompleteName(),
                 resState.unresolved(),null);
      }
   }


   /**
    *  Internal method to do recursion of type resolve.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogCompileResolutionException failed to type resolve
    */
   public void doTypeResolve(ResolutionState resState) throws SmartFrogCompileResolutionException {
      if (!resolveType(resState)) {
         return;
      }

      for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Object value = context.get(key);
         try {
           if (value instanceof ComponentResolver) {
             // Attribute value is resolvable, ask it to resolve itself
             ( (ComponentResolver) value).doTypeResolve(resState);
           }
         } catch (Throwable thr) {
           StringBuffer msg = new StringBuffer ( "Failed to resolve '");
           msg.append (key.toString());
           msg.append (" ");
           msg.append (value.toString());
           msg.append(";'");
           if (thr instanceof java.lang.StackOverflowError) {
              msg.append (". Possible cause: cyclic reference.");
           }
           throw new SmartFrogCompileResolutionException(msg.toString(), thr,
               sfCompleteName(), null, resState.unresolved());
         }
      }
   }


   /**
    *  Does the actual type resolution. Looks for super type relative to this
    *  description if the first element of the type reference is a
    *  HereReferencePart (ie. not a PARENT or ROOT). Subtypes supertype into
    *  this description if found.
    *
    *@param  resState  resolution state to maintain unresolved types
    *@return           true if resolved, false if not
    */
   protected boolean resolveType(ResolutionState resState) {
      Reference superTypeRef = type;

      if (superTypeRef == null) {
         return true;
      }

      try {
         SFComponentDescription superType = (SFComponentDescription) sfResolve(superTypeRef);
         superType.doTypeResolve(resState);

         if (!resState.moreToResolve()) {
            subtype(superType);
            resState.haveResolved(true);
            superTypeRef = type;
         }
      } catch (Exception excpt) {
         resState.addUnresolved(superTypeRef, sfCompleteName());
      }

      return superTypeRef == null;
   }


   /**
    *  Subtypes a supertype ino this component. Copies the type of the superType
    *  into this description. Also copies any attributes which are not in this
    *  description into it.
    *
    *@param  superType  super type to copy from
    */
   protected void subtype(SFComponentDescription superType) {
      // First copy the context of the supertype
      Context sContext = (Context) superType.sfContext().copy();
      Object key;
      Object value;

      // re-parent any descriptions in super context
      for (Enumeration e = sContext.keys(); e.hasMoreElements(); ) {
         key = e.nextElement();
         value = sContext.get(key);

         if (value instanceof SFComponentDescription) {
            ((SFComponentDescription) value).setParent(this);
         }
      }

      // add my context
      for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
         key = e.nextElement();
         value = context.get(key);
         sContext.put(key, value);
      }

      // set context
      context = sContext;

      // set supertype
      type = superType.getType();
   }


   /**
    *  'Link' resolve a description. This involves iterating over the
    *  description tree a number of times and looking at the attribute values.
    *  If the value is an eager reference, it is resolved. The resulting value
    *  replaces the reference as the attribute value
    *
    * @throws  SmartFrogCompileResolutionException failed to deploy resolve
    */
   public void linkResolve() throws SmartFrogCompileResolutionException {
      ResolutionState resState = new ResolutionState();

      do {
         resState.clear();
         doLinkResolve(resState);
      } while (resState.moreToResolve());

      if (resState.unresolved().size() > 0) {
         throw SmartFrogCompileResolutionException.linkResolution(
                 MessageUtil.formatMessage(MSG_UNRESOLVED_REFERENCE),
                 sfCompleteName(), resState.unresolved(),null);
      }
   }


   /**
    *  Internal method to do recursion on link resolution.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogCompileResolutionException failed to deploy resolve
    */
   public void doLinkResolve(ResolutionState resState) throws SmartFrogCompileResolutionException {
      Object key = null;
      Object value = null;
      Object result = null;

      for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
         // Get next attribute key and value
         key = e.nextElement();
         value = context.get(key);

         // If value is reference resolve and place result in its place
         if (value instanceof ComponentResolver) {
            // value is deploy resolvable
            ((ComponentResolver) value).doLinkResolve(resState);
         } else if ((value instanceof Reference) &&
               (((Reference) value).getEager())) {
            try {
               result = sfResolve((Reference) value);
               context.put(key, result);
               if (result instanceof SFComponentDescription) {
                   // need to do this as it may link to the file root!
                   ((SFComponentDescription)result).doLinkResolve(resState);
               }
/*
               if (result instanceof Copying) {
                  result = ((Copying) result).copy();

                  if (result instanceof SFComponentDescription) {
                     ((ComponentDescription) result).setParent(this);
                     resState.haveResolved(true);
                     resState.addUnresolved(((SFComponentDescription) result).sfCompleteName());
                  }
               }
 */

            } catch (Exception resex) {
               resState.addUnresolved(value, sfCompleteName());
            } catch (Throwable thr){
              StringBuffer msg = new StringBuffer("Failed to resolve '");
              msg.append(key.toString());
              msg.append(" ");
              msg.append(value.toString());
              msg.append(";'");
              if (thr instanceof java.lang.StackOverflowError) {
                msg.append(". Possible cause: cyclic reference.");
              }
              throw new SmartFrogCompileResolutionException(msg.toString(), thr,
                  sfCompleteName(), null, resState.unresolved());

            }
         }
      }
   }


   /**
    *  Returns a string representation of the component. This will give a
    *  description of the component which is parseable, and deployable again...
    *  Unless someone removed attributes which are essential to startup that is.
    *  Large description trees should be written out using writeOn since memory
    *  for large strings runs out quick!
    *
    *@return    string representation of component
    */
   public String toString() {
      StringWriter sw = new StringWriter();

      try {
         writeOn(sw);
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
     * @param indent the indent to use for printing offset
     *
     * @throws IOException failure while writing
     */
    public void writeOn(Writer ps, int indent) throws IOException {
	ps.write("extends " + (getEager() ? "" : "LAZY ") +
               ((getType() == null) ? "" : getType().toString()));

	if (context.size() > 0) {
	    ps.write(" {\n");
	    context.writeOn(ps, indent+1);
	    tabPad(ps, indent); ps.write('}');
	} else {
	    ps.write(';');
	}
    }

   /**
    *  Return a component description as required by the deployer.
    *  Works by side-effect on the SFComponentDescription for efficiency.
    *  This becomes uniseable after the conversion.
    *
    *@return    the equivalent component descriptino
    */
   public ComponentDescription sfAsComponentDescription() throws SmartFrogCompilationException {
       ComponentDescription res = null;
       // parent only necessary for the root - gets overwritten below
       ContextImpl newContext = new ContextImpl();
       res = new ComponentDescriptionImpl(null, newContext, eager);

       for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
           Object key = e.nextElement();
           Object value = context.get(key);

           if (value instanceof SFComponentDescription) {
               value = ((SFComponentDescription) value).sfAsComponentDescription();
               ((ComponentDescription) value).setParent(res);
               newContext.put(key, value);
           } else
               newContext.put(key, copyValue(value));
       }
       return res;
   }


    protected Object copyValue(Object v) throws SmartFrogCompilationException {
        if (v instanceof Number) return v;
        if (v instanceof Boolean) return v;
        if (v instanceof SFNull) return v;
        if (v instanceof String) return v;
        if (v instanceof Reference) return v;
        if (v instanceof SFByteArray) return v;
        if (v instanceof Vector) {
             return copyVector((Vector)v);
        }
        throw new SmartFrogCompilationException("illegal value in context during conversion to ComponentDesscription " +
                                                v.toString() + " in component " + sfCompleteName());
    }

    protected Object copyVector(Vector v) throws SmartFrogCompilationException {
        Vector res = new Vector();
        for (int i = 0; i < v.size(); i++) {
            res.add(copyValue(v.elementAt(i)));
        }
        return res;
    }

   /**
    *  Public method to carry out specific resolution actions as defined by the
    *  phaseList attribute. This attribute is removed as part of the resolution.
    *  The phases are defined by the sfGetPhases method.
    *
    *@return                      the resultant Phases object, ready for the
    *      next phase action or convertion into the core ComponentDescription
    *@throws  SmartFrogException  In case of SmartFrog system error
    */
   public Phases sfResolvePhases() throws SmartFrogException {
      return sfResolvePhases(sfGetPhases());
   }


   /**
    *  Public method to carry out specific phase.
    *
    *@param  phase                The phase to carry out
    *@return                      the resultant Phases object, ready for the
    *      next phase action or convertion into the core ComponentDescription
    *@throws  SmartFrogException  In case of SmartFrog system error
    */
   public Phases sfResolvePhase(String phase)
          throws SmartFrogException {
      Vector v = new Vector();
      v.add(phase);

      return sfResolvePhases(v);
   }


   /**
    *  Public method to carry out specific resolution actions as defined by the
    *  phases provided.
    *
    *@param  phases               a vector of strings defining the names of the
    *      phases
    *@return                      the resultant Phases object, ready for the
    *      next phase action or convertion into the core ComponentDescription
    *@throws  SmartFrogException  In case of SmartFrog system error
    */
   public Phases sfResolvePhases(Vector phases)
          throws SmartFrogException {
      SFComponentDescription actOn = this;

      for (Enumeration e = phases.elements(); e.hasMoreElements(); ) {
         String name = (String) e.nextElement();
         try {
           if (name.equals("type")) {
             actOn.typeResolve();
           }
           else if (name.equals("place")) {
             actOn.placeResolve();
           }
           else if (name.equals("sfConfig")) {
             actOn = (SFComponentDescription) sfResolve(sfConfigRef);
           }
           else if (name.equals("link")) {
             actOn.linkResolve();
           }
           else if (name.equals("print")) {
             //org.smartfrog.sfcore.common.Logger.log(actOn.toString());
             org.smartfrog.SFSystem.sfLog().out(actOn.toString());
           }
           else {
             actOn.visit(new Phase(name), false);
           }
         } catch (Throwable thr) {
           throw SmartFrogCompileResolutionException.forward(thr, name);
         }

      }

      return actOn;
   }


   /**
    *  Public method to get the set of phases defined in the component. This
    *  will either be the phaseList attribute, or the standard set defined as
    *  though the phaseList attribute had been defined phaseList ["type",
    *  "place", "sfConfig", "link", "function"]; The attribute is removed to
    *  tidy the definition, but the result is cached for later use
    *
    *@return    Vector of Phases
    */
   public Vector sfGetPhases() {
      if (phases == null) {
         phases = (Vector) context.get("phaseList");

         if (phases == null) {
            phases = new Vector();
            phases.add("type");
            phases.add("place");
            phases.add("sfConfig");
            phases.add("link");
            phases.add("function");
            phases.add("predicate");
         } else {
            context.remove("phaseList");
         }
      }

      return phases;
   }
}
