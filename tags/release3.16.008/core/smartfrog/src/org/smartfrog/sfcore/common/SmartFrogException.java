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

import org.smartfrog.Version;
import org.smartfrog.sfcore.prim.Prim;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * Root of all SmartFrog-generated exceptions. All the exceptions in SmartFrog
 * system should extend this exception.
 *
 */
public class SmartFrogException extends Exception implements Serializable {
    /** Attribute name for Context. */
    public static final String CONTEXT = "Context";

    /** Attribute name for Exception. */
    public static final String EXCEPTION = "Exception";

    /** Additional informational data. */
    public static final String DATA = "data";

    /** Context associated with the exception. */
    protected Context cxt = null;


    /** Attribute name for primContect in exceptioncontext. */
    public static final String PRIM_CONTEXT = "primContext";

    /** Attribute name for primSFCompleteName in exceptioncontext. */
    public static final String PRIM_COMPLETE_NAME = "primSFCompleteName";

    /** SmarFrog version that created the exception */
    public static final String SFVERSION = Version.versionString();

    /**
     * Constructs a SmartFrogException with no message.
     */
    public SmartFrogException() {
        super();
    }

    /**
     * Constructs a SmartFrogException with specified message.
     *
     * @param message exception message
     */
    public SmartFrogException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogException(Throwable cause) {
        super(cause);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogException) {
            return (SmartFrogException)thr;
        } else {
            return new SmartFrogException(thr);
        }
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     * If thr is an instance of SmartFrogException then the exception is returned
     * without any modification, if not a new SmartFrogException is created
     * with message as a paramenter
     * @param message String message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogException) {
            if (message!=null){
                ((SmartFrogException)thr).add("msg: ",message);
            }
            return (SmartFrogException)thr;
        } else {
            return new SmartFrogException(message, thr);
        }
    }


    /**
     * Constructs a SmartFrogException with specified message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes
     * the exception context with component details.
     *
     * @param message exception message
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogException(String message, Prim sfObject) {
        super(message);
        init(sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified cause. Also initializes
     * the exception context with component details.
     *
     * @param cause cause of the exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogException(Throwable cause, Prim sfObject) {
        super(cause);
        init(sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes
     * the exception context with component details.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogException(String message, Throwable cause,Prim sfObject) {
        super(message, cause);
        init(sfObject);
    }


    /**
     * Gets the context associated with the exception.
     *
     * @return Exception context
     *
     * @see #setContext
     */
    public Context getContext() {
        return cxt;
    }

    /**
     * Sets the context associated with the exception.
     *
     * @param newContext The context associated with exception

     * @see #getContext
     */
    public void setContext(Context newContext) {
        this.cxt = newContext;
    }

    /**
     * Puts additional context in the exception context. This method can be used
     * to add more information as exception propagates in the call chain.
     *
     * @param params Additional Context
     */
    public void put(Context params){
        if (params != null) {
            if (cxt == null) {
                cxt = params;
                return;
            }
            for (Enumeration e = params.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                put(key, params.get(key));
            }
        }
    }

    /**
     * Converts a given context into a serializable one:
     *  Any value is converted toString().
     *
     * @param params Additional Context
     * @return Context
     */
    public Context serializableContext(Context params){
        Context newCxt = new ContextImpl();
        if (params != null) {
            for (Enumeration e = params.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                newCxt.put(key.toString(), params.get(key).toString());
            }
        }
        return newCxt;
    }

    /**
     * Gets the value of the attribute in the exception context.

     * @param key name of the attribute to be retrieved from the error context

     * @return value of the attribute
     */
    public Object get (Object key) {
        if (cxt == null || key == null ) {
            return null;
        }
        else return cxt.get(key);
    }

    /**
     * Puts an attribute in key-value form in exception context.
     *
     * @param key name of the attribute
     * @param value value of the attribute
     */
    public void put (Object key, Object value){
        if (key == null || value == null) {
            return;
        }
        if (cxt == null) cxt = new ContextImpl();
        cxt.put(key,value);
    }

    /**
     * Adds new attritute to exception context only if it doesn't exist in the
     * exception context.
     * @param key name of the attribute
     * @param value value of the attribute
     * @return boolean true if the attribute was added.
     */
    public boolean add (Object key, Object value){
        if (cxt == null) cxt = new ContextImpl();
        else if (cxt.containsKey(key)) return false;
        put(key,value);
        return true;
    }
    /**
     * Checks if some attribute exists in exception context.
     *
     * @param value value of the attribute
     * @return boolean true if the attribute exists
     */
    public boolean contains (Object value) {
        return cxt != null && cxt.contains(value);
    }
    /**
     * Checks if some attribute exists in exception context.
     *
     * @param value attribute name
     * @return boolean true if the attribute exists
     */
    public boolean containsKey (Object value) {
        return cxt != null && cxt.containsKey(value);
    }

    /**
     * Initializes the exception context.
     *
     * @param sfObject SmartFrog Component
     */
    public void init (Prim sfObject){
        if (sfObject == null) return;
        if (cxt == null) cxt = new ContextImpl();

        try {
            add(PRIM_CONTEXT, serializableContext(sfObject.sfContext()));
        } catch (Throwable  rex){
            //Ignore.
        }
        try {
            add(PRIM_COMPLETE_NAME, sfObject.sfCompleteName().toString());
        } catch (Throwable  thr){
            //Ignore.
        }
    }

    /**
     * Gets a string representation of the exception.
     *
     * @return string representation of the exception
     */
    public String toString() {
        return toString(", ");
    }

    /**
     * Gets a string representation of the exception.
     *
     * @param nm  Message separator (ex. "\n");
     *
     * @return String this object to String.
     */
    public String toString (String nm) {
        StringBuilder strb = new StringBuilder();
        strb.append(shortClassName()).append(":: ");
        if (getMessage()!=null){
            if ((getCause()!=null) && (getCause().toString().equals(getMessage()))) {
               strb.append (getCauseMessage(nm));
            } else {
                //Only print message when message != cause
                strb.append (getMessage());
                strb.append ((((getCause() == null) ) ? "" : (nm+"cause: " + getCauseMessage(nm))));
            }
        } else {
            strb.append ((((getCause() == null) ) ? "" : (getCauseMessage(nm))));
        }

        strb.append ((nm+ SFVERSION ));

        strb.append ((((containsKey(DATA))) ? (nm+DATA+  ": "
                                                    + get(DATA)) : "" ));
        strb.append ((((containsKey(PRIM_COMPLETE_NAME))) ? (nm+PRIM_COMPLETE_NAME+
                                                  ": " + get(PRIM_COMPLETE_NAME)) : "" ));
        strb.append ((((containsKey(PRIM_CONTEXT))) ? (nm+PRIM_CONTEXT+
                                                  ": " + "included") : "" ));

        return strb.toString();
    }

    /**
     * Get the cause message
     * @param nm Message separator (ex. "\n");
     * @return  cause message
     */
    public String getCauseMessage(String nm) {
        String causeText="";
        Throwable cause = getCause();
        if ((!(nm.startsWith(","))||(!nm.startsWith(":")))){
             nm=nm+"   ";
        }
        if (cause instanceof SmartFrogException){
           causeText = ((SmartFrogException)cause).toString(nm);
        } else {
            causeText=cause.toString();
        }

        return causeText;
    }


   /**
    * Gets a detailed string representation of the exception.
    *
    * @return string representation of the exception
    */
    public String toStringAll() {
        return toStringAll(", ");
    }

    /**
     * Gets a detailed string representation of the exception.
     *
     * @param nm Message separator (ex. "\n");
     *
     * @return string representation of the exception
     */
    public String toStringAll(String nm) {
        StringBuilder strb = new StringBuilder();
        strb.append("ALL: ").append(shortClassName()).append(": ");

        if ((getMessage()!=null) && (getCause()!=null) && (!((getCause().toString().equals(getMessage()))))) {
            //Only print message when message != cause
            strb.append (getMessage());
            strb.append ((((getCause() == null) ) ? "" : (nm+"cause: " + getCauseMessage(nm))));
        } else {
            strb.append ((((getCause() == null) ) ? "" : (getCauseMessage(nm))));
        }
        
        strb.append ((((cxt == null) ||
            (cxt.size() == 0)) ? "" : (nm+"  context: " + nm +cxt.toString()+nm)));
        return strb.toString();
    }

    /**
     * Gets class name.
     *
     * @return Class name
     */
    protected String shortClassName (){
      return getClass().getName().substring(
              getClass().getName().lastIndexOf('.')+1);
    }

}
