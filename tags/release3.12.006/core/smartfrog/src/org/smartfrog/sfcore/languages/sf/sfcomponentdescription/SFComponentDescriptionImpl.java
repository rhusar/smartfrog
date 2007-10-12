/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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
import java.util.Set;


import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.Phase;
import org.smartfrog.sfcore.languages.sf.PhaseNames;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.ReferencePhases;
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
   protected Vector phases = null;

   /**
    * Reference to sfConfig attribute
    */
   protected Reference sfConfigRef = new Reference(SmartFrogCoreKeys.SF_CONFIG);

   /**
    *  type of description. The type field is used as a prototype for this
    *  description. This means that the attributes of the (super)type are
    *  inherited by this component in a structural copy
    */
   public Vector types = new Vector();
    private static final String PHASE_LIST = "phaseList";


    /**
     *  Constuctor.
     *
     *@param  types    supertypes for component
     *@param  parent  parent component
     *@param  cxt     context for description
     *@param  eager   eager flag
     */
    public SFComponentDescriptionImpl(Vector types, SFComponentDescription parent, Context cxt, boolean eager) {
       super(parent, cxt, eager);
       if (types != null) this.types = types;
    }


   /**
    *  Get prototypes for this description. This is the component from which
    *  attributes get copied into this description.
    *
    * @return    types for this description
    *
    * @see #setTypes
    */
   public Vector getTypes() {
      return types;
   }


   /**
    *  Set new types for this component.
    *
    * @param  t  new prototype sfor description
    *
    * @return    old type
    *
    * @see #getTypes
    */
   public Vector setTypes(Vector t) {
      Vector ot = types;
      types = t;

      return ot;
   }


   /**
    *  add a new type for this component.
    *
    * @param  type  new prototype for description
    *
    */
   public void addType(Reference type) {
      types.add(type);
   }

   /**
    *  add a new set of attributes for this component.
    *
    * @param  type  new set of attributes for description
    *
    */
   public void addType(SFComponentDescription type) {
      types.add(type);
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
      res.setTypes((Vector) types.clone());
      res.setContext((Context) sfContext.copy());
      res.setParent(parent);
      res.setEager(eager);

      for (Enumeration e = sfContext.keys(); e.hasMoreElements(); ) {
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
     * @TODO why is this here? no longer seems necessary from the definiton
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
        /*
        if (!r.getEager() && (index == 0)) {
            return r;
        }
        */
        /*
        if (r.getData() && (index == 0)) {
            return r;
        }
        */
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
    * @throws  SmartFrogResolutionException failed to place resolve
    */
   public void placeResolve() throws SmartFrogResolutionException {
      ResolutionState resState = new ResolutionState();

      do {
         resState.clear();
         doPlaceResolve(resState);
      } while (resState.moreToResolve());
      if (resState.unresolved().size() > 0) {
         throw new SmartFrogPlaceResolutionException(null, null, sfCompleteName(),
                 resState.unresolved());
      }
   }


   /**
    *  Internal method to do recursion of place resolve.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogResolutionException failed to place resolve
    */
   public void doPlaceResolve(ResolutionState resState) throws SmartFrogResolutionException {
      // Hold removals. NOT creating the vector here saves memory at the
      // expense of two null checks below.
      Vector removals = null;

      // Resolve attributes
      for (Enumeration e = sfContext.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Object value = sfContext.get(key);
         Set tags = null;
         try {
            tags = sfContext.sfGetTags(key);
         } catch (SmartFrogException e1) {
            //shouldn't happen
         }

         try {
           // Get attribute and if key a reference try to place it in the
           // right component. Don't resolve value since it ain't mine
           if (key instanceof Reference) {
             // allocate removals if not already there
             if (removals == null) {
               removals = new Vector(5);
             }

             if (place( (Reference) key, value, tags, resState)) {
                removals.addElement(key);
             }
           } else if (value instanceof ComponentResolver) {
             // Attribute value is resolvable, ask it to resolve itself
             ( (ComponentResolver) value).doPlaceResolve(resState);
           }
         } catch (SmartFrogPlaceResolutionException pre){
            throw pre;   // some handled error such as overriding a final attriute
         } catch (Throwable thr){ // some other error - such as cyclic reference
           StringBuffer msg = new StringBuffer("Failed to resolve '");
           msg.append(key.toString());
           msg.append(" ");
           msg.append(value.toString());
           msg.append(";'");
           if (thr instanceof java.lang.StackOverflowError) {
             msg.append(". Possible cause: cyclic reference.");
           }
           throw new SmartFrogPlaceResolutionException(msg.toString(), thr, null, sfCompleteName());
         }
      }

      if (removals != null) {
         for (Enumeration e = removals.elements(); e.hasMoreElements(); ) {
            sfContext.remove(e.nextElement());
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
    * @return tye if the placement worked
    * @throws SmartFrogPlaceResolutionException if there was a failure to place (including final attributes)
    */
   protected boolean place(Reference key, Object value, Set tags, ResolutionState resState) throws SmartFrogPlaceResolutionException {
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
         resState.addUnresolved(key, sfCompleteName(), null, rex);

         return false;
      }

      boolean attrFinal = false;
      try {
         attrFinal = destDescription.sfContext().sfContainsTag(nam, SmartFrogCoreKeys.SF_FINAL);
      } catch (SmartFrogException ignored) {
         // leave false
      }

      if (attrFinal) {
            throw new SmartFrogPlaceResolutionException(
                 MessageUtil.formatMessage(CANNOT_OVERRIDE_FINAL, sfCompleteName(), key)
            );
      }

      // Found destination
      resState.haveResolved(true);

      // place value under simple name in destination
      destDescription.sfContext().put(nam, value);
      try {
         if (tags != null) destDescription.sfContext().sfAddTags(nam, tags);
      } catch (SmartFrogException e) {
         // shouldn't happen
      }

      // Set new parent if value is component
      if (value instanceof ComponentDescription) {
         ((ComponentDescription) value).setParent(destDescription);

         // Remember to go and resolve the newly placed component
         resState.addUnresolved(value, destDescription.sfCompleteName(), null, null);
      }

      return true;
   }


   /**
    *  Type resolves the component. This means iterating a number of times over
    *  the description tree, and doing type resolution. Type resolution finds
    *  the supertype and subtypes it into this component.
    *
    * @throws  SmartFrogResolutionException failed to type resolve
    */
   public void typeResolve() throws SmartFrogResolutionException {
      ResolutionState resState = new ResolutionState();

      do {
         resState.clear();
         doTypeResolve(resState);
      } while (resState.moreToResolve());

      if (resState.unresolved().size() > 0) {
         throw new SmartFrogTypeResolutionException(
                 MessageUtil.formatMessage(MSG_UNRESOLVED_REFERENCE),
                 null,
                 sfCompleteName(),
                 resState.unresolved());
      }
   }


   /**
    *  Internal method to do recursion of type resolve.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogResolutionException failed to type resolve
    */
   public void doTypeResolve(ResolutionState resState) throws SmartFrogResolutionException {
/*
      System.out.println("-----------------------");
      System.out.println("resolving " + sfCompleteName());
      System.out.println("context: " + sfContext());
      System.out.println("types: " + types);
      System.out.println("parent " + sfParent());
      System.out.println("parent parent " + sfParent().sfParent());
*/
      if (!resolveTypes(resState)) return;
      for (Enumeration e = sfContext.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Object value = sfContext.get(key);
         try {
           if (value instanceof ComponentResolver) {
             // Attribute value is resolvable, ask it to resolve itself
             ( (ComponentResolver) value).doTypeResolve(resState);
           }
         } catch (Throwable thr) {
           if (thr instanceof SmartFrogTypeResolutionException)  {
             throw (SmartFrogResolutionException)thr;
           }

           StringBuffer msg = new StringBuffer ( "Failed to resolve '");
           msg.append (key.toString());
           msg.append (" ");
           msg.append (value.toString());
           msg.append("'");
           if (thr instanceof java.lang.StackOverflowError) {
              msg.append (". Possible cause: cyclic reference.");
           }
           throw new SmartFrogTypeResolutionException(msg.toString(), thr,
               sfCompleteName(), resState.unresolved());
         }
      }
   }

   /**
    *  Does the type resolution for all the types. Looks for super type relative to this
    *  description if the first element of the type reference is a
    *  HereReferencePart (ie. not a PARENT or ROOT). Subtypes supertype into
    *  this description if found.
    *
    *@param  resState  resolution state to maintain unresolved types
    *@return true if all types resolved
    *@throws SmartFrogTypeResolutionException an error in the subtyping process, such as
    * the override of a final attribute
    */
   protected boolean resolveTypes(ResolutionState resState) throws SmartFrogTypeResolutionException {
      boolean ok = true;
      for (int i = types.size() - 1; ok && i>=0;  i--) {
         Object o = types.elementAt(i);
         if (o instanceof Reference)
           ok &= resolveType(resState, (Reference) o);
         else
           ok &= resolveType(resState, (SFComponentDescription) o);
         if (ok) types.remove(i);
      }
      return ok;
   }


   /**
    *  Does the actual type resolution. Looks for super type relative to this
    *  description if the first element of the type reference is a
    *  HereReferencePart (ie. not a PARENT or ROOT). Subtypes supertype into
    *  this description if found.
    *
    * @param  resState  resolution state to maintain unresolved types
    * @param type reference to the supertype
    * @return whther the resolution succeeded
    * @throws SmartFrogTypeResolutionException an error in the subtyping process, such as
    * the override of a final attribute
    */
   protected boolean resolveType(ResolutionState resState, Reference type) throws SmartFrogTypeResolutionException {
      try {
         SFComponentDescription superType = (SFComponentDescription) sfResolve(type);
         superType.doTypeResolve(resState);

         if (!resState.moreToResolve()) {
            subtype(superType);
            resState.haveResolved(true);
         }
      } catch (SmartFrogTypeResolutionException te) {
         throw te; // this was from the sutyping - possibly a override of sfFinal
      } catch (Exception excpt) {
         resState.addUnresolved(type, sfCompleteName(), null, excpt);
         return false;
      }
      return true;
   }

   /**
    *  Does the actual type resolution. Looks for super type relative to this
    *  description if the first element of the type reference is a
    *  HereReferencePart (ie. not a PARENT or ROOT). Subtypes supertype into
    *  this description if found.
    *
    * @param  resState  resolution state to maintain unresolved types
    * @param type super type to copy from.
    * @return whther the resolution succeeded
    * @throws SmartFrogTypeResolutionException an error in the subtyping process, such as
    * the override of a final attribute
    */

   protected boolean resolveType(ResolutionState resState, SFComponentDescription type) throws SmartFrogTypeResolutionException {
      try {
         //type.doTypeResolve(resState);

         if (!resState.moreToResolve()) {
            subtype(type);
            resState.haveResolved(true);
         }
      } catch (SmartFrogTypeResolutionException te) {
         throw te; // this was from the sutyping - possibly a override of sfFinal
      } catch (Exception excpt) {
         throw new SmartFrogTypeResolutionException("error type resolving component in " + sfCompleteName() + " data: " + type);
      }
      return true;
   }

   /**
    *  Subtypes a supertype ino this component. Copies the type of the superType
    *  into this description. Also copies any attributes which are not in this
    *  description into it.
    *
    *@param  superType  super type to copy from
    *@throws SmartFrogTypeResolutionException an error in the subtyping process, such as
    * the override of a final attribute
    */
   protected void subtype(SFComponentDescription superType) throws SmartFrogTypeResolutionException {
      // First copy the sfContext of the supertype
      Context sContext = (Context) superType.sfContext().copy();
      Object key;
      Object value;
      Set tags;

      // re-parent any descriptions in super sfContext
      for (Enumeration e = sContext.keys(); e.hasMoreElements(); ) {
         key = e.nextElement();
         value = sContext.get(key);
         if (value instanceof SFComponentDescription) {
            ((SFComponentDescription) value).setParent(this);
         }
      }

      // add my sfContext
      for (Enumeration e = sfContext.keys(); e.hasMoreElements(); ) {
         key = e.nextElement();
         value = sfContext.get(key);
         boolean finalAttr = false;
         try {
            finalAttr = sContext.sfContainsTag(key, "sfFinal");
         } catch (SmartFrogException e1) {
            // shouldn't happen
         }
         if (finalAttr) {
            throw new SmartFrogTypeResolutionException(
                 MessageUtil.formatMessage(CANNOT_OVERRIDE_FINAL, sfCompleteName(), key)
            );
         }
         sContext.put(key, value);
         try {
            tags = sfContext.sfGetTags(key);
            sContext.sfAddTags(key, tags);
         } catch (SmartFrogException e1) {
            //shouldn't happen
         }
      }

      // set sfContext
      sfContext = sContext;
   }


   /**
    *  'Link' resolve a description. This involves iterating over the
    *  description tree a number of times and looking at the attribute values.
    *  If the value is an eager reference, it is resolved. The resulting value
    *  replaces the reference as the attribute value
    *
    * @throws  SmartFrogResolutionException failed to deploy resolve
    */
   public void linkResolve() throws SmartFrogResolutionException {
      ResolutionState resState = new ResolutionState();

      do {
         resState.clear();
         doLinkResolve(resState);
      } while (resState.moreToResolve());

      if (resState.unresolved().size() > 0) {
         throw new SmartFrogLinkResolutionException (
                 MessageUtil.formatMessage(MSG_UNRESOLVED_REFERENCE), null,
                 sfCompleteName(), resState.unresolved());
      }
   }


   /**
    *  Internal method to do recursion on link resolution.
    *
    * @param  resState  resolution state
    *
    * @throws  SmartFrogResolutionException failed to deploy resolve
    */
   public void doLinkResolve(ResolutionState resState) throws
       SmartFrogResolutionException {
       Object key = null;
       Object value = null;
       Object result = null;

       for (Enumeration e = sfContext.keys(); e.hasMoreElements(); ) {
           // Get next attribute key and value
           key = e.nextElement();
           value = sfContext.get(key);

           // If value is reference resolve and place result in its place
           if (value instanceof ComponentResolver) {
                   // value is deploy resolvable
                try {
                    ((ComponentResolver)value).doLinkResolve(resState);
                } catch (SmartFrogResolutionException ex) {
                    throw ex;
                } catch (Throwable thr) {
                   StringBuffer msg = new StringBuffer("Failed to resolve '");
                   msg.append(key);
                   msg.append(" ");
                   try {
                       msg.append(value);
                   } catch (java.lang.StackOverflowError thrx) {
                       msg.append("[unprintable cyclic value]");
                   }
                   msg.append("'");
                   if (thr instanceof java.lang.StackOverflowError) {
                       msg.append(". Possible cause: cyclic reference.");
                   }
                   throw new SmartFrogLinkResolutionException(msg.toString(),
                       thr, sfCompleteName(), resState.unresolved());
               }

           } else if (value instanceof Reference) {
               Reference rv = (Reference)value;
               if (!rv.getData()) {
                   try {
                       result = sfResolve((Reference) value);
                       sfContext.put(key, result);
                       if (result instanceof SFComponentDescription) {
                           // need to do this as it may link to the file root!
                          if (((SFComponentDescription) result).sfParent() == null) {
                               ((SFComponentDescription) result).setParent(this);
                          }
                          ((SFComponentDescription) result).doLinkResolve(resState);
                       }
                   } catch (SmartFrogLazyResolutionException slrex) {
                       rv.setEager(false);
                   } catch (Exception resex) {
                       resState.addUnresolved(value, sfCompleteName(), key.toString(), resex);
                   } catch (Throwable thr) {
                       StringBuffer msg = new StringBuffer("Failed to resolve '");
                       msg.append(key);
                       msg.append(" ");
                       msg.append(value);
                       msg.append("'");
                       if (thr instanceof StackOverflowError) {
                           msg.append(". Possible cause: cyclic reference.");
                       }
                       throw new SmartFrogLinkResolutionException(msg.toString(),
                               thr, sfCompleteName(), resState.unresolved());
                   }
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
         writeOn(ps, indent, true);
    }

    /**
     * Writes this component description on a writer with or without "extends".
     *
     * @param ps writer to write on
     * @param indent the indent to use for printing offset
     * @param includeExtends print the "extends [DATA]" bit
     *
     * @throws IOException failure while writing
     */
    protected void writeOn(Writer ps, int indent, boolean includeExtends) throws IOException {
        if (includeExtends) ps.write("extends "+ (getEager()?"":"DATA "));
        boolean first = true;
        for (Enumeration e = getTypes().elements(); e.hasMoreElements(); ) {
           Object elem = e.nextElement();
           if (!first) ps.write(", ");
           if (elem instanceof SFComponentDescriptionImpl) {
              ((SFComponentDescriptionImpl) elem).writeOn(ps, indent+1, false);
           } else {
              ps.write(elem.toString());
           }
           first = false;
        }

        if (sfContext.size()>0) {
            ps.write("{\n");
            sfContext.writeOn(ps, indent+1);
            tabPad(ps, indent); ps.write('}');
        } else {
            ps.write(';');
        }
    }

    /**
     * Subclasses can override this method to return alternative componentDescription implementations
     * 
     */
    protected ComponentDescription createComponentDescription(final ComponentDescription parentCD, final Context context, final boolean isEager) {
    	return new ComponentDescriptionImpl(parentCD, context, isEager);
    }
    
   /**
    *  Return a component description as required by the deployer.
    *  Works by side-effect on the SFComponentDescription for efficiency.
    *  This becomes usable after the conversion.
    *
    *@return    the equivalent component description
    */
   public ComponentDescription sfAsComponentDescription() throws SmartFrogCompilationException {
       ComponentDescription res = null;
       // parent only necessary for the root - gets overwritten below
       ContextImpl newContext = new ContextImpl();
       res = createComponentDescription(null, newContext, eager);

       for (Enumeration e = sfContext.keys(); e.hasMoreElements(); ) {
           Object key = e.nextElement();
           Object value = sfContext.get(key);
           Set tags = null;

          try {
             if (value instanceof SFTempValue || sfContext.sfContainsTag(key, "sfTemp")) {
                  //nothing - attribute is to be removed...
              } else if (value instanceof Phases) {
                  value = ((Phases) value).sfAsComponentDescription();
                  ((ComponentDescription) value).setParent(res);
                  newContext.put(key, value);
             } else if (value instanceof ReferencePhases) {
                  value = ((ReferencePhases) value).sfAsReference();
                  newContext.put(key, value);
             } else
                  newContext.put(key, copyValue(value));
          } catch (SmartFrogException e1) {
              throw  ((SmartFrogCompilationException)SmartFrogCompilationException.forward(e1));
          }

          try {
             if (newContext.sfContainsAttribute(key)){
                 tags  = sfContext.sfGetTags(key);
                 newContext.sfAddTags(key, tags);
             }
          } catch (SmartFrogException e1) {
                 e1.printStackTrace();
                //It shouldn't happen
          }
       }

       return res;
   }


    protected Object copyValue(Object v) throws SmartFrogCompilationException {
        if (v instanceof Number) return v;
        if (v instanceof Boolean) return v;
        if (v instanceof SFNull) return v;
        if (v instanceof SFTempValue) return v;
        if (v instanceof String) return v;
        if (v instanceof Reference) return v;
        if (v instanceof SFByteArray) return v;
        if (v instanceof Vector) {
             return copyVector((Vector)v);
        }
       /*
        if (v instanceof ComponentDescription) {
            throw new SmartFrogCompilationException("illegal value in context during conversion to ComponentDescription. ComponentDescription cannot be used; use SFComponentDescription. Context: " +
                                                v.toString() + " (Class: "+v.getClass().getName()+") in component " + sfCompleteName());
        }
        */
        throw new SmartFrogCompilationException("Non-primitive value found during conversion to ComponentDescription " +
                                                v.toString() + " (Class: "+v.getClass().getName()+") in component " + sfCompleteName());
    }

    protected Object copyVector(Vector v) throws SmartFrogCompilationException {
        Vector res = new Vector();
        for (int i = 0; i < v.size(); i++) {
           try {
               res.add(copyValue(v.elementAt(i)));
           } catch (Exception e) {
               throw new SmartFrogCompilationException("Error in vector during conversion to ComponentDescription. Vector: " +
                                                v.toString() + " in component " + sfCompleteName(), e);
           }
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
    *@param  phaseList               a vector of strings defining the names of the
    *      phases
    *@return                      the resultant Phases object, ready for the
    *      next phase action or conversion into the core ComponentDescription
    *@throws  SmartFrogException  In case of SmartFrog system error
    */
   public Phases sfResolvePhases(Vector phaseList)
          throws SmartFrogException {
      SFComponentDescription actOn = this;

      for (Enumeration e = phaseList.elements(); e.hasMoreElements(); ) {
         String name = (String) e.nextElement();
         try {
           if (name.equals(PhaseNames.TYPE)) {
             actOn.typeResolve();
           }
           else if (name.equals(PhaseNames.PLACE)) {
             actOn.placeResolve();
           }
           else if (name.equals(PhaseNames.SFCONFIG)) {
             Object sfc = sfResolve(sfConfigRef);
             if (sfc instanceof SFComponentDescription) {
                actOn = (SFComponentDescription)sfc ;
             } else {
                 throw new SmartFrogResolutionException(MessageUtil.formatMessage(ROOT_MUST_BE_COMPONENT, sfc.getClass().toString()));
             }
           }
           else if (name.equals(PhaseNames.LINK)) {
             actOn.linkResolve();
           }
           else if (name.equals(PhaseNames.PRINT)) {
             //org.smartfrog.sfcore.common.Logger.log(actOn.toString());
             org.smartfrog.SFSystem.sfLog().out(actOn.toString());
           }
           else {
             actOn.visit(new Phase(name), false);
           }
         } catch (Throwable thr) {
           throw SmartFrogResolutionException.forward(name, thr);
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
         phases = (Vector) sfContext.get(PHASE_LIST);

         if (phases == null) {
            phases = new Vector();
            phases.add(PhaseNames.TYPE);
            phases.add(PhaseNames.PLACE);
            phases.add(PhaseNames.FUNCTION);
            phases.add(PhaseNames.SFCONFIG);
            phases.add(PhaseNames.LINK);
         } else {
            sfContext.remove(PHASE_LIST);
         }
      }

      return phases;
   }
}
