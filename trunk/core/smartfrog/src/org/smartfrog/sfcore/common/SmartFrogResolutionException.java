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

package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

/**
 * SmartFrogResolutionException is thrown system fails to resolve some
 * reference.
 *
 */
public class SmartFrogResolutionException extends SmartFrogRuntimeException
                                                     implements MessageKeys {
    /** Attribute name for path in exceptioncontext. */
    public final static String PATH="path";

    /** Attribute name for depth in exceptioncontext. */
    public final static String DEPTH="depth";

    /** Attribute name for default value classtype in exceptioncontext. */
    public final static String DEFAULT_OBJECT_CLASS_TYPE="defaultValueClassType";

    /** Attribute name for reference value classtype in exceptioncontext. */
    public final static String REFERENCE_OBJECT_CLASS_TYPE="referenceValueClassType";

    /** Attribute name for reference value classtype in exceptioncontext. */
    public final static String REFERENCE_OBJECT_RESOLVED="referenceValueResolved";


    /**
     * Constructs a SmartFrogResolutionException with message.
     *
     * @param message exception message
     */
    public SmartFrogResolutionException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogResolutionException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogResolutionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogResolutionException with cause. Also initializes
     * the exception context with component details.
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogResolutionException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Constructs a SmartFrogResolutionException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogResolutionException with message. Also initializes
     * the exception context with component details.
     *
     * @param message message
     * @param sfObject component that encountered exception
     */
    public SmartFrogResolutionException(String message,Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogResolutionException with message and cause. Also
     * initializes the exception context with component details
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogResolutionException(String message, Throwable cause,
        Prim sfObject) {
        super(message, cause, sfObject);
    }


    /**
     * Constructs a SmartFrogResolutionException with reference and reason.
     * @param ref reference causing the resolution exception
     * @param reason message for exception */
    public SmartFrogResolutionException(Reference ref, String reason) {
      this(ref, null , reason, null);
    }


    /**
     * Constructs a SmartFrogResolutionException with reference and reason.
     * @param ref reference causing the resolution exception
     * @param source source that raised the exception
     * @param reason message for exception
     */
    public SmartFrogResolutionException(Reference ref, Reference source,
                               String reason) {
      this(ref, source, reason, null);
    }

    /**
     * Constructs a SmartFrogResolutionException with reference and reason and
     * additional data.
     * @param ref reference causing the resolution exceptioh
     * @param source source that raised the exception
     * @param reason message for exception
     * @param data additional data for exception
     */
    public SmartFrogResolutionException (Reference ref, Reference source,
                               String reason, Object data) {
      super(reason);
      if ((ref!=null))put(REFERENCE,ref);
      if ((source!=null)) put(SOURCE,source);
      if (data!=null) put(DATA,data);
      //addCallerInfo(4);
    }

    /**
     * Constructs a SmartFrogResolutionException with reference and reason and
     * additional data.
     * @param ref reference causing the resolution exception
     * @param source source that raised the exception
     * @param reason message for exception
     * @param data additional data for exception
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception     *
     */
    public SmartFrogResolutionException (Reference ref, Reference source,
                                         String reason, Object data ,
                                         Throwable cause,Prim sfObject) {
      super(reason, cause, sfObject);
      if ((ref!=null))put(REFERENCE,ref);
      if ((source!=null)) put(SOURCE,source);
      if (data!=null) put(DATA,data);
      //addCallerInfo(4);
    }



    /**
     * Creates a generic SmartFrogResolutionException with reference and
     * message.
     *
     * @param ref reference leading to exception
     * @param message The message associated with the failure
     *
     * @return a SmartFrogResolution exception
     */
    public static SmartFrogResolutionException generic(Reference ref,
                                                            String message) {
        return new SmartFrogResolutionException(ref, message);

    }

    /**
     * Creates a generic resolution exception.
     *
     * @param ref reference leading to exception
     * @param source The source that was trying to resolve the reference
     * @param message The message associated with the failure
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException generic(Reference ref,
                            Reference source, String message) {
        return new SmartFrogResolutionException(ref ,source, message);
    }

    /**
     * Creates a not found resolution exception.
     *
     * @param ref reference leading to exception
     * @param source The source that was trying to resolve the reference
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException notFound(Reference ref,
                                                        Reference source) {
        return (new SmartFrogResolutionException(ref ,source,
                 MessageUtil.formatMessage(MSG_NOT_FOUND_REFERENCE)));
    }


    /**
     * Creates a not found resolution exception.
     *
     * @param ref reference leading to exception
     * @param source The source that was trying to resolve the reference
     * @param cause cause exception causing this exception
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException notFound(Reference ref,
                                                        Reference source, Throwable cause) {
        return (new SmartFrogResolutionException(ref ,source,
                 MessageUtil.formatMessage(MSG_NOT_FOUND_REFERENCE),null,cause,null));
    }


    /**
     * Creates a not a component reference resolution exception.
     *
     * @param ref reference leading to exception
     * @param source The source that was trying to resolve the reference
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException notComponent(Reference ref,
        Reference source) {
        return new SmartFrogResolutionException(ref, source,
                MessageUtil.formatMessage(MSG_NOT_COMPONENT_REFERENCE));
    }

    /**
     * Creates a not a component reference value resolution exception.
     *
     * @param ref reference leading to exception
     * @param source The source that was trying to resolve the reference
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException notValue(Reference ref,
              Reference source) {
        return new SmartFrogResolutionException(ref, source,
                MessageUtil.formatMessage(MSG_NOT_FOUND_REFERENCE));
    }

    /**
     * Creates an illegal reference resolution exception.
     *
     * @param ref ref causing the illegal reference
     * @param source The source that was trying to resolve the reference
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException illegalReference(Reference ref,
        Reference source) {
        return new SmartFrogResolutionException(ref, source,
                MessageUtil.formatMessage(MSG_ILLEGAL_REFERENCE));
    }

    /**
     * Creates an illegal reference resolution exception.
     *
     * @param ref ref causing the illegal reference
     * @param source The source that was trying to resolve the reference
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException illegalClassType(Reference ref,
        Reference source) {
        return new SmartFrogResolutionException (ref, source,
                MessageUtil.formatMessage(MSG_ILLEGAL_CLASS_TYPE));
    }

    /**
     * Creates an illegal reference resolution exception.
     *
     * @param ref ref causing the illegal reference
     * @param source The source that was trying to resolve the reference
     * @param foundValue Object found by sfResolve
     * @param referenceValueType The reference value type
     * @param defaultValueType The default value type
     *
     * @return a resolution exception
     */
    public static SmartFrogResolutionException illegalClassType(Reference ref,
        Reference source,Object resolvedValue, String referenceValueType, String defaultValueType) {
        SmartFrogResolutionException srex = new SmartFrogResolutionException (ref, source,
                MessageUtil.formatMessage(MSG_ILLEGAL_CLASS_TYPE));
                srex.put(REFERENCE_OBJECT_RESOLVED,resolvedValue);
                srex.put(REFERENCE_OBJECT_CLASS_TYPE,referenceValueType);
                srex.put(DEFAULT_OBJECT_CLASS_TYPE,defaultValueType);
        return srex;
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogResolutionException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogResolutionException) {
            return (SmartFrogResolutionException)thr;
        } else {
            return new SmartFrogResolutionException (thr);
        }
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param message to add to the exception
     * @param thr throwable object to be forwarded
     * @param r reference causing the resolution exception
     * @return SmartFrogException that is a SmartFrogResolutionException
     */
    public static SmartFrogException forward (String message, Reference r, Throwable thr){
        if (thr instanceof SmartFrogResolutionException) {
            // add message to data
            if (r !=null){
              //ADD: only added if not present.
              ((SmartFrogResolutionException)thr).add(SmartFrogResolutionException.REFERENCE,r);
            }
            return (SmartFrogResolutionException)thr;
        } else {
            return new SmartFrogResolutionException (r,null,message,null,thr,null);
        }
    }

    /**
     * Appends the path information.
     *
     * @param ref ref causing the illegal reference
     *
     * @return a resolution exception
     */
    public int appendPath (String ref){
        if (!(this.containsKey(PATH))||(this.get(PATH)==null)){
            this.put(PATH, new StringBuffer(ref));
        } else {
            ((StringBuffer)this.get(PATH)).append(ref);
        }
        return ((StringBuffer)this.get(PATH)).length();
    }


    /**
     * Appends the path information.
     *
     * @return a resolution exception
     */
    public int sizePath (){
        int length = -1;
        try {
          if ( (this.containsKey(PATH)) || (this.get(PATH) != null)) {
            return ((StringBuffer)this.get(PATH)).length();
          }
        } catch (Throwable thr){
        }
        return length;
    }


    /**
     * Returns a string representation of the resolution exception.
     *
     * @param nm  Message separator (ex. "\n");
     *
     * @return string representation of the resolution exception
     */
    public String toString(String nm) {
        StringBuffer strb = new StringBuffer();
        strb.append (shortClassName() +":: ");
        //strb.append ((((getMessage() == null) ? "" : getMessage())));
        if (getMessage()==null){
            strb.append ((getCause() == null)  ? "" : getCauseMessage(nm));
        } else {
            strb.append (getMessage());
//            strb.append ((((getCause() == null) ) ? "" : (nm+"  cause: " +
//                getCause().getMessage())));
        }
        strb.append(((this.containsKey(REFERENCE)&&(this.get(REFERENCE)!=null)
            &&(((Reference)this.get(REFERENCE)).size()!=0))) ? (nm+
                MessageUtil.formatMessage(MSG_UNRESOLVED_REFERENCE)+ ": " +
                    get(REFERENCE)) : "" );
        strb.append((((this.containsKey(SOURCE)&&(this.get(SOURCE)!=null)
                            &&(((Reference)this.get(SOURCE)).size()!=0)))
                               ? (nm+SOURCE+  ": " + get(SOURCE)) : "" ));
        strb.append((((this.containsKey(REFERENCE_OBJECT_RESOLVED))) ?
                    (nm+REFERENCE_OBJECT_RESOLVED+  ": '" + get(REFERENCE_OBJECT_RESOLVED)+"'") : "" ));
        strb.append((((this.containsKey(REFERENCE_OBJECT_CLASS_TYPE))) ?
                    (nm+REFERENCE_OBJECT_CLASS_TYPE+  ": " + get(REFERENCE_OBJECT_CLASS_TYPE)) : "" ));
        strb.append((((this.containsKey(DEFAULT_OBJECT_CLASS_TYPE))) ?
                    (nm+DEFAULT_OBJECT_CLASS_TYPE+  ": " + get(DEFAULT_OBJECT_CLASS_TYPE)) : "" ));
        if (Logger.logStackTrace){
            strb.append((((this.containsKey(PATH))) ?
                         (nm+PATH+ "("+ this.sizePath() + "): " + get(PATH)) : "" ));
            strb.append((((this.containsKey(DEPTH))) ?
                         (nm+DEPTH+  ": " + get(DEPTH)) : "" ));
        } else {
          if (this.sizePath()>0)
          strb.append((((this.containsKey(PATH))) ?
                       (nm+PATH+ "("+ this.sizePath() + ")") : "" ));
        }
        strb.append((((this.containsKey(PRIM_CONTEXT))) ?
                    (nm+PRIM_CONTEXT+  ": " + "included") : "" ));
        strb.append((((this.containsKey(DATA))) ?
                    (nm+DATA+  ": " + get(DATA)) : ""));

        return strb.toString();
    }



}
