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

package org.smartfrog.sfcore.prim;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.util.Vector;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SecureRemoteObject;

import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import java.rmi.*;


/**
 * Defines the base class for all deployed components. A deployed component
 * knows how to react to termination, deployment requests and heart beats.
 * This implementation allows subclasses to define how a deployed component is
 * communicated with. The sfExport attribute is examined. If set to "true" the
 * sfExportRef method is called, which by default uses RMI to export the
 * component. Subclasses may choose to export a component another way.
 *
 * <p>
 * A liveness thread is started for a primitive if it has no parent, but is
 * expected to have children (like Compound), or if the parent is remote.
 * Liveness attribute sfLivenessDelay dictates how often (in seconds) sfPing
 * is supposed to be called. sfLivenessFactor is the multiplier for
 * sfLivenessDelay to wait until the primitive declares that there is a
 * liveness failure. Both attributes are looked up using ATTRIB in order to
 * find out whether a parent has set liveness on.
 * </p>
 *
 */
public class PrimImpl extends Object implements Prim, MessageKeys {

    /** Static attribute that hold the lifecycle hooks for sfDeploy. */
    public static PrimHookSet sfDeployHooks = new PrimHookSet();

    /** Static attribute that hold the lifecycle hooks for sfStart. */
    public static PrimHookSet sfStartHooks = new PrimHookSet();

    /** Static attribute that hold the lifecycle hooks for sfDeployWith. */
    public static PrimHookSet sfDeployWithHooks = new PrimHookSet();

    /** Static attribute that hold the lifecycle hooks for sfTerminateWith. */
    public static PrimHookSet sfTerminateWithHooks = new PrimHookSet();

    /** Reference used to look up sfLivenessDelay attributes. */
    protected static final Reference refLivenessDelay = new Reference(ReferencePart.attrib(
                SmartFrogCoreKeys.SF_LIVENESS_DELAY));

    /** Reference used to look up sfLivenessFactor attributes. */
    protected static final Reference refLivenessFactor = new Reference(ReferencePart.attrib(
                SmartFrogCoreKeys.SF_LIVENESS_FACTOR));

    /** Flag indicating that this component has been terminated. */
    protected boolean sfIsTerminated = false;

    /** Flag indicating that this component has been deployed. */
    protected boolean sfIsDeployed = false;

    /** Flag indicating that this component has been started. */
    protected boolean sfIsStarted = false;

    /** Parent. */
    protected Prim sfParent = null;

    /** Attribute context. */
    protected Context sfContext = null;

    /** Timer for initiating heartbeats. */
    protected LivenessSender sfLivenessSender;

    /** Current count for liveness, updated through sfPing. */
    protected int sfLivenessCount;

    /**
     * Livneess factor. Initializer for liveness count. How many multiples of
     * livenss delay to wait till a liveness failure of the parent is
     * declared, Defaults to 2.
     */
    protected int sfLivenessFactor = 2;

    /**
     * How often to send liveness in seconds. Defaults to 0 (off, ie. no
     * liveness).
     */
    protected long sfLivenessDelay = 0;

    /** Reference to export form of self if sfExport is true. */
    protected Object sfExportRef = null;

    /** Reference that caches cannonical name. */
    protected Reference sfCompleteName = null;

    /**
     * Used in conjunction with sfDeployWith to set parent and context after
     * creation.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public PrimImpl() throws RemoteException {
    }

    //
    // ReferenceResolver
    //

    /**
     * Resolves a single attribute id in this component.
     *
     * @param id key to resolve
     *
     * @return value for key or null if none
     */
    public Object sfResolveId(Object id) {
        return sfContext.get(id);
    }

    /**
     * Returns the parent for this component.
     *
     * @return parent for this component or null if none
     */
    public RemoteReferenceResolver sfResolveParent() {
        return sfParent;
    }

    /**
     * Resolves a given reference. Forwards to indexed resolve with index 0
     * and return resulting attribute value.
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException occurred while resolving
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(Reference r)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(r, 0);
    }


    /**
     * Resolves given reference starting at given index. This is forwarded to
     * the reference (and on to each reference part).
     *
     * @param r reference to resolve
     * @param index index in reference to start resolving
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException error occurred while resolving
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(Reference r, int index)
        throws SmartFrogResolutionException, RemoteException {
        Object obj = null;

        try {
            obj = r.resolve(this, index);
        } catch (SmartFrogResolutionException rex) {
            if ((!(rex.containsKey(SmartFrogRuntimeException.SOURCE)))
                    || (rex.get(SmartFrogRuntimeException.SOURCE)== null)) {
                rex.put(SmartFrogRuntimeException.SOURCE, this.sfCompleteNameSafe());
                rex.put(SmartFrogResolutionException.DEPTH, new Integer(index));
            }
            rex.appendPath(this.sfCompleteName().toString() + " ");
            throw rex;
        } catch (java.lang.StackOverflowError st){
            throw new SmartFrogResolutionException(r,this.sfCompleteNameSafe(),
               st.toString() +". Possible cause: cyclic reference",null,st,this);
        } catch (Throwable thr){
             throw new SmartFrogResolutionException(r,this.sfCompleteNameSafe(),
              null,null,thr,this);
        }

        return obj;
    }

    /**
     * Request the host on which this component is deployed.
     *
     * @return the host InetAddress
     *
     * @throws RemoteException In case of network/rmi error
     */
    public InetAddress sfDeployedHost() throws RemoteException {
        try {
            return java.net.InetAddress.getLocalHost();
        } catch (Exception ex) {
        }

        return null;
    }

    /**
     * Request the process in which this component is deployed, the name being
     * that defined in the sfProcessName attribute or the string ROOT if in
     * the root process compound.
     *
     * @return the name of the process
     *
     * @throws RemoteException In case of network/rmi error
     */
    public String sfDeployedProcessName() throws RemoteException {
        String value = (String) System.getProperty(
                "org.smartfrog.sfcore.processcompound.sfProcessName");

        if (value == null) {
            return SmartFrogCoreKeys.SF_ROOT;
        } else {
            return value;
        }
    }

    /**
     * Adds an attribute to this component under given name.
     *
     * @param name name of attribute
     * @param value value of attribute
     *
     * @return added attribute if non-existent or null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public synchronized Object sfAddAttribute(Object name, Object value)
        throws SmartFrogRuntimeException, RemoteException {
        if ((name == null) || (value == null)) {
          if (name == null) {
              throw new SmartFrogRuntimeException(
              MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                                        "sfAddAttribute"), this);
          }
          if (value == null) {
              throw new SmartFrogRuntimeException(
              MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'value'",
                                        "sfAddAttribute"), this);
          }

            return null;
        }

        if (sfContext.containsKey(name)) {
            return null;
        }

        sfContext.put(name, value);

        return value;
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
        throws SmartFrogRuntimeException, RemoteException {
        if (name == null) {
          if (name == null) {
              throw new SmartFrogRuntimeException(
              MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                                        "sfRemoveAttribute"), this);
          }

            return null;
        }

        return sfContext.remove(name);
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
        throws SmartFrogRuntimeException, RemoteException {
        if ((name == null) || (value == null)) {
            if (name == null) {
                throw new SmartFrogRuntimeException(
                MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                                          "sfReplaceAttribute"), this);
            }
            if (value == null) {
                throw new SmartFrogRuntimeException(
                MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'value'",
                                          "sfReplaceAttribute"), this);
            }

            return null;
        }

        return sfContext.put(name, value);
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
        return sfContext.keyFor(value);
    }


    /**
     * Returns the context of this component.
     *
     * @return component context
     */
    public Context sfContext() {
        return sfContext;
    }


    /**
     * Resolves a referencePart given a string. Utility method to auto-convert
     * from string to reference.
     *
     * NOTE: To resolve a reference from a String using a reference cannonical
     * representation it is neccesary to do:
     * "return sfResolve(Reference.fromString(reference));"so that the parser
     * is invoked.
     *
     * @param referencePart stringified reference
     *
     * @return java Object for attribute value
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(String referencePart) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart));
    }


    /**
     * Resolves a reference given a string. Utility method to auto-convert from
     * string to reference. It can use cannonical representations that are
     * resolved by the parser (parse = true).
     *
     * @param reference string field reference
     *
     * @return java Object for attribute value
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolveWithParser(String reference) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(Reference.fromString(reference));
    }


    /**
     * Resolves given reference and gets a java Object.
     * Utility method to resolve an attribute with a java Object value.
     *
     * @param reference reference
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java Object for attribute value or null if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(Reference reference, boolean mandatory)
            throws SmartFrogResolutionException, RemoteException{
        try {
            Object referenceObj = sfResolve(reference, 0);
            return (referenceObj);
        } catch (SmartFrogResolutionException e) {
            if (mandatory) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Resolves a referencePart given a string and gets a java Object.
     *
     * @param referencePart string field reference

     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or null if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(String referencePart, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), mandatory);
    }

    /**
     * Resolves given reference and gets a boolean. Utility method to resolve
     * an attribute with a boolean value.
     *
     * @param reference reference
     * @param defaultValue boolean default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a ResolutionException
     *
     * @return boolean for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public boolean sfResolve(Reference reference, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Boolean) {
                return (((Boolean) referenceObj).booleanValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets an int. Utility method to resolve an
     * attribute with an int value.
     *
     * @param reference reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(Reference reference, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Integer) {
                return (((Integer) referenceObj).intValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets an int. Utility method to resolve an
     * attribute with an int value.
     *
     * @param reference reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(Reference reference, int defaultValue,Integer minValue, Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        int value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.intValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved int '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.intValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved int '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }

    /**
     * Resolves given reference and gets an long. Utility method to resolve an
     * attribute with an long value. Int values are "upcasted" to long.
     *
     * @param reference reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(Reference reference, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if ((referenceObj instanceof Long) ||
                    (referenceObj instanceof Integer)) {
                return (((Number) referenceObj).longValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    }

    /**
     * Resolves given reference and gets an long. Utility method to resolve an
     * attribute with an long value. Int values are "upcasted" to long.
     *
     * @param reference reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(Reference reference, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        long value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.longValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved long '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.longValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved long '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }
    /**
     * Resolves given reference and gets an float. Utility method to resolve an
     * attribute with an float value. Integer values are "upcasted" to float.
     *
     * @param reference reference
     * @param defaultValue float default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(Reference reference, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if ((referenceObj instanceof Float) ||
                    (referenceObj instanceof Integer)) {
                return (((Number) referenceObj).floatValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    }

    /**
     * Resolves given reference and gets an float. Utility method to resolve an
     * attribute with an float value. Int values are "upcasted" to float.
     *
     * @param reference reference
     * @param defaultValue float default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(Reference reference, float defaultValue, Float minValue, Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        float value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.floatValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved float '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.floatValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved float '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }

    /**
     * Resolves given reference and gets an double. Utility method to resolve an
     * attribute with an double value. Integer values are "upcasted" to double.
     *
     * @param reference reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(Reference reference, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if ((referenceObj instanceof Float) ||
                    (referenceObj instanceof Integer)
                    || (referenceObj instanceof Long)
                    || (referenceObj instanceof Double)) {
                return (((Number) referenceObj).doubleValue());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    }

    /**
     * Resolves given reference and gets an double. Utility method to resolve an
     * attribute with an double value. Int values are "upcasted" to double.
     *
     * @param reference reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(Reference reference, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        double value = sfResolve(reference, defaultValue, mandatory);
        if ((minValue!=null)&&(value<minValue.doubleValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved double '"+value+"' < '"+minValue+"'(minValue)");
        else if ((maxValue!=null)&&(value>maxValue.doubleValue()))
            throw new SmartFrogResolutionException(reference, this.sfCompleteNameSafe(), "Error: sfResolved double '"+value+"' > '"+maxValue+"'(maxValue)");
        else return value;
    }

    /**
     * Resolves given reference. Utility method to resolve an attribute with a
     * String value.
     *
     * @param reference reference
     * @param defaultValue String default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String sfResolve(Reference reference, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof String) {
                return (((String) referenceObj).toString());
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a Vector. Utility method to resolve an
     * attribute with a Vector value.
     *
     * @param reference reference
     * @param defaultValue Vector default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Vector for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Vector sfResolve(Reference reference, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Vector) {
                return (((Vector) referenceObj));
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a String[]. Utility method to resolve
     * an attribute with a Vector value and returns a String[].
     *
     * @param reference reference
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String[] for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String[] sfResolve(Reference reference, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Vector) {
                String[] array = null;

                if (!(((Vector) referenceObj).isEmpty())) {
                    ((Vector) referenceObj).trimToSize();
                    array = new String[((Vector) referenceObj).size()];
                    ((Vector) referenceObj).copyInto(array);

                    return (array);
                }
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog ComponentDescription.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog ComponentDescription default value that is
     *        returned when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog ComponentDescription for attribute value or
     *         defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public ComponentDescription sfResolve(Reference reference,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof ComponentDescription) {
                return ((ComponentDescription) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a SmartFrog
     * Reference value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog Reference default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog Reference for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Reference sfResolve(Reference reference, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Reference) {
                return ((Reference) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog Prim.
     * Utility method to resolve an attribute with a SmartFrog
     * Prim value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog Prim default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog Prim for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Prim sfResolve(Reference reference, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Prim) {
                return ((Prim) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a SmartFrog Compound.
     * Utility method to resolve an attribute with a SmartFrog
     * Compound value.
     *
     * @param reference reference
     * @param defaultValue SmartFrog Compound default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return SmartFrog Compound for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Compound sfResolve(Reference reference, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof Compound) {
                return ((Compound) referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a java.net.InetAddress.
     * Utility method to resolve an attribute with a SmartFrog
     * java.net.InetAddress value.
     *
     * @param reference reference
     * @param defaultValue java.net.InetAddress default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java.net.InetAddress for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public java.net.InetAddress sfResolve(Reference reference,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        boolean illegalClassType = false;

        try {
            Object referenceObj = sfResolve(reference, 0);

            if (referenceObj instanceof java.net.InetAddress) {
                return ((java.net.InetAddress) referenceObj);
            } else if (referenceObj instanceof String) {
                try {
                    return (java.net.InetAddress.getByName((String) referenceObj));
                } catch (Exception ex) {
                    SmartFrogResolutionException resEx = SmartFrogResolutionException.generic(reference,
                            this.sfCompleteNameSafe(), ex.toString());
                    resEx.put(SmartFrogException.DATA, ex);
                    throw resEx;
                }
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }

        return defaultValue;
    }

    /**
     * Resolves given reference and gets a java Object.
     * Utility method to resolve an attribute with a java Object value.
     *
     * @param reference reference
     * @param defaultValue java Object default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return java Object for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(Reference reference, Object defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException{
        boolean illegalClassType = false;
        try {
            Object referenceObj = sfResolve(reference, 0);
            if ((defaultValue==null) || ( defaultValue.getClass().isAssignableFrom(referenceObj.getClass()))) {
                return (referenceObj);
            } else {
                illegalClassType = true;
                throw SmartFrogResolutionException.illegalClassType(reference,
                    this.sfCompleteNameSafe(),referenceObj.getClass().toString(),defaultValue.getClass().toString());
            }
        } catch (SmartFrogResolutionException e) {
            if ((mandatory) || (illegalClassType)) {
                throw e;
            }
        }
        return defaultValue;
    };

    /**
     * Resolves a referencePart given a string and gets a boolean. Utility
     * method to resolve an attribute with a boolean value.
     *
     * @param referencePart string field reference
     * @param defaultValue boolean default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return boolean for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public boolean sfResolve(String referencePart, boolean defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param referencePart string field reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(String referencePart, int defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a int. Utility method to
     * resolve an attribute with an int value.
     *
     * @param referencePart string field reference
     * @param defaultValue int default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return int for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public int sfResolve(String referencePart, int defaultValue,Integer minValue,Integer maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a long. Utility method
     * to resolve an attribute with an long value. Int values are upcastted to
     * long.
     *
     * @param referencePart string field reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(String referencePart, long defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a long. Utility method
     * to resolve an attribute with an long value. Int values are upcastted to
     * long.
     *
     * @param referencePart string field reference
     * @param defaultValue long default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return long for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public long sfResolve(String referencePart, long defaultValue, Long minValue, Long maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }
    /**
     * Resolves a referencePart given a string and gets a float. Utility method
     * to resolve an attribute with an float value.
     *
     * @param referencePart string field reference
     * @param defaultValue float default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(String referencePart, float defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a float. Utility method
     * to resolve an attribute with an float value.
     *
     * @param referencePart string field reference
     * @param defaultValue float default value that is returned when reference is
     *        not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return float for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public float sfResolve(String referencePart, float defaultValue,Float minValue,Float maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a double. Utility method
     * to resolve an attribute with an double value. Int values are upcasted to
     * double.
     *
     * @param referencePart string field reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(String referencePart, double defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a double. Utility method
     * to resolve an attribute with an double value. Integer, Long and Float
     * values are upcasted to double.
     *
     * @param referencePart string field reference
     * @param defaultValue double default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     * @param minValue allowed (included)
     * @param maxValue allowed (included)
     *
     * @return double for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable or resolved value &lt; minValue or &gt; maxValue
     * @throws RemoteException In case of network/rmi error
     */
    public double sfResolve(String referencePart, double defaultValue, Double minValue, Double maxValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, minValue, maxValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a String. Utility method
     * to resolve an attribute with a String value.
     *
     * @param referencePart string field reference
     * @param defaultValue String default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String sfResolve(String referencePart, String defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a Vector. Utility method     * to resolve an attribute with a Vector value.
     *
     * @param referencePart string field reference
     * @param defaultValue Vector default value that is returned when reference
     *        is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Vector for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Vector sfResolve(String referencePart, Vector defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a String[]. Utility
     * method to resolve an attribute with a Vector value and returns a String[]
     *
     *
     * @param referencePart string field reference
     * @param defaultValue String[] default value that is returned when
     *        reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return String[] for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public String[] sfResolve(String referencePart, String[] defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog
     * ComponentDescription. Utility method to resolve an attribute with a
     * SmartFrog ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog ComponentDescription default value that is
     *        returned when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return ComponentDescription for attribute value or defaultValue if not
     *         found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public ComponentDescription sfResolve(String referencePart,
        ComponentDescription defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog Reference default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Reference sfResolve(String referencePart, Reference defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Prim.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog Prim default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Prim for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Prim sfResolve(String referencePart, Prim defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }


    /**
     * Resolves a referencePart given a string and gets a SmartFrog Compound.
     * Utility method to resolve an attribute with a SmartFrog
     * ComponentDescription value.
     *
     * @param referencePart string field reference
     * @param defaultValue SmartFrog Compound default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Compound for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Compound sfResolve(String referencePart, Compound defaultValue,
        boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Resolves a referencePart given a string and gets a SmartFrog Reference.
     * Utility method to resolve an attribute with a java.net.InetAddress
     * value.
     *
     * @param referencePart string field reference
     * @param defaultValue java.net.InetAddress default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public java.net.InetAddress sfResolve(String referencePart,
        java.net.InetAddress defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }



    /**
     * Resolves a referencePart given a string and gets a java Object.
     *
     * @param referencePart string field reference
     * @param defaultValue java Object default value that is returned
     *        when reference is not found and it is not mandatory
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        triggers a SmartFrogResolutionException
     *
     * @return Reference for attribute value or defaultValue if not found
     *
     * @throws SmartFrogResolutionException illegal reference or reference
     * not resolvable
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolve(String referencePart,
        Object defaultValue, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException {
        return sfResolve(new Reference(referencePart), defaultValue, mandatory);
    }

    /**
     * Returns the complete name for this component from the root of the
     * application.
     *
     * @return reference of attribute names to this component
     *
     * @throws RemoteException In case of network/rmi error
     */
     //sfCompleteName is cached. @TODO: clean cache when re-parenting
    public Reference sfCompleteName() throws RemoteException {
        if (sfCompleteName==null) {
            Reference r;
            Object key;
            if (sfParent==null) {
                r = SFProcess.getProcessCompound().sfCompleteName();
                key = SFProcess.getProcessCompound().sfAttributeKeyFor(this);
            } else {
                r = sfParent.sfCompleteName();
                key = sfParent.sfAttributeKeyFor(this);
            }
            sfCompleteName= (Reference)r.clone();

            if (key!=null) {
                sfCompleteName.addElement(ReferencePart.here(key));
            }
        }
        return sfCompleteName;
    }

    /**
     * Returns the complete name for this component from the root of the
     * application and does not throw any exception. If an exception is
     * thrown it will return null.
     *
     * @return reference of attribute names to this component or null
     *
     */
    public Reference sfCompleteNameSafe() {
        try {
            return this.sfCompleteName();
        } catch (Throwable thr){
            // TODO: log a message to indicate that sfCompleteName failed!
            return new Reference();
        }
    }

    /**
     * Returns the parent of this component.
     *
     * @return parent component
     */
    public Prim sfParent() {
        return sfParent;
    }

    /**
     * Find an attribute in this component context.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public synchronized Object sfResolveHere(Object name)
        throws SmartFrogResolutionException {
        Object result = null;

        if ((result = sfResolveId(name)) == null) {
             throw SmartFrogResolutionException.notFound(new Reference(
                        name), sfCompleteNameSafe());
        }

        return result;
    }

    /**
     * Private method to set up newly created component. Primitives should only
     * override sfDeploy since this is the one which does the actual work
     *
     * @param parent parent of component
     * @param cxt context for component
     *
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     * @throws RemoteException In case of network/rmi error
     */
    public void sfDeployWith(Prim parent, Context cxt)
        throws SmartFrogDeploymentException, RemoteException {
        try {
            sfParent = parent;
            sfContext = cxt;

        /* @TODO Would like to do this, but requires Prim to be a ComponentDescription
         * which is a good idea in anycase, but requires a refactoring of the interfaces
         *
         * in the mean time reference resolution from a CD is bounded to that CD hierarchy
         * and cannot move between this and the Component hierarchy
         *
         * Could do this by providing a proxy which implements CD and forwards the resovlve
         * requests to the Prim...
         *

        // set the parent link of any contained component description to this Prim
        // so that references work
        for (Enumeration e = sfContext.keys(); e.hasMoreElements();) {
        Object value = sfContext.get(e.nextElement());

        if (value instanceof ComponentDescription) {
            ((ComponentDescription) value).setParent(this);
        }
        }

        */


            boolean es; // allow exportRef to be defined by string (backward compatability) or boolean
            Object eso = sfResolveId(SmartFrogCoreKeys.SF_EXPORT);

            if (eso == null) {
                es = true;
            } else if (eso instanceof String) {
                es = Boolean.valueOf((String) eso).booleanValue();
            } else {
                es = ((Boolean) eso).booleanValue();
            }

            if (es) {
                sfExportRef();
            }

            if (sfParent != null) {
                ((ChildMinder) sfParent).sfAddChild(this);
            }

            try {
                // Look up delay, if not there never mind looking up factor
                sfLivenessDelay = ((Number) sfResolve(refLivenessDelay)).intValue();
                sfLivenessFactor = ((Number) sfResolve(refLivenessFactor)).intValue();
                // copy in local description for efficiency when subcomponents looking up
                sfReplaceAttribute(SmartFrogCoreKeys.SF_LIVENESS_DELAY, new Long (sfLivenessDelay) );
                // copy in local description for efficiency when subcomponents looking up
                sfReplaceAttribute(SmartFrogCoreKeys.SF_LIVENESS_FACTOR, new Integer (sfLivenessFactor));
            } catch (SmartFrogResolutionException resex) {
                // ignore, leave default
            }

            sfLivenessCount = sfLivenessFactor;

            // start the liveness thread
            sfStartLivenessSender();


            sfReplaceAttribute(SmartFrogCoreKeys.SF_HOST, sfDeployedHost());
            sfReplaceAttribute(SmartFrogCoreKeys.SF_PROCESS, sfDeployedProcessName());

            sfDeployWithHooks.applyHooks(this, null);

        } catch (Exception sfex){
        new TerminatorThread(this, sfex, null).quietly().run();
            throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward (sfex);
        }
    }

    /**
     * Export this primitive to accept remote method calls. Default
     * implementation is to use UnicastRemoteObject. Check is done if
     * sfExportRef is already set, in which case this is returned. <b>Note</b>
     * that for remote methods to work equals, hashCode and toString must be
     * implemented. This is done for PrimImpl in which case those requests are
     * forwarded to the sfExportRef.
     *
     * @return exported primitive
     *
     * @throws SmartFrogException failed to export primitive
     */
    public Object sfExportRef() throws SmartFrogException {
        if (sfExportRef == null) {
            try {
                sfExportRef = SecureRemoteObject.exportObject(this);
            } catch (RemoteException rex) {
                throw new SmartFrogLifecycleException(MSG_OBJECT_REGISTRATION_FAILED,
                    rex, this);
            } catch (SFGeneralSecurityException sfgsex) {
                throw new SmartFrogLifecycleException(MSG_OBJECT_REGISTRATION_FAILED,
                    sfgsex, this);
            }
        }

        return sfExportRef;
    }

    /**
     * initializes and starts the liveness sender for this primitive. A
     * component needs a liveness sender if it has no parent, but is has
     * children (ie. is a ChildMinder) or if the parent is remote.
     *
     * @throws SmartFrogLivenessException failed to initialize or start sender
     */
    protected void sfStartLivenessSender() throws SmartFrogLivenessException {
        if ((sfLivenessSender != null) || (sfLivenessDelay == 0)) {
            return;
        }

        if (((sfParent == null) && (this instanceof ChildMinder)) ||
                sfIsRemote(sfParent)) {
            try {
                sfLivenessSender = new LivenessSender(this,
                        sfLivenessDelay * 1000);
                sfLivenessSender.start();
            } catch (Throwable t) {
                throw new SmartFrogLivenessException(MSG_LIVENESS_START_FAILED,
                    t, this);
            }
        }
    }

    /**
     * Stops the liveness sender if this component has one.
     */
    protected void sfStopLivenessSender() {
        if (sfLivenessSender != null) {
            sfLivenessSender.stop();
        }
    }

    /**
     * Checks whether given object is a non-local object. This implementation
     * is RMI oriented, so checks whether the given object is a RemoteStub,
     * which would indicate that this object is remote. Subclasses can
     * override this (and associated sfExport method) if the underlying remote
     * infrastructure is not RMI.
     *
     * @param o object to check remoteness on
     *
     * @return true if object a remote reference, false if local
     */
    protected boolean sfIsRemote(Object o) {
        return (o instanceof RemoteStub);
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
        throws SmartFrogException, RemoteException {

         Reference componentId = sfCompleteName();
        if (sfIsTerminated) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED, componentId.toString()),
                this);
        }
    sfIsDeployed = true;
        sfDeployHooks.applyHooks(this, null);
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        if (sfIsTerminated) {
            throw new SmartFrogLifecycleException(MessageUtil.formatMessage(
                    MSG_START_COMP_TERMINATED, this.sfCompleteNameSafe().toString()),
                this);
        }
    sfIsStarted = true;
        sfStartHooks.applyHooks(this, null);
    }

    /**
     * Default dump state behavior is to send the context to the target.
     *
     * @param target dump interface to dump to
     */
    public void sfDumpState(Dump target) {
        try {
            target.dumpState(sfContext, this);
        } catch (Exception ex) {
            // ignore
        }
    }

    /**
     * Request this component to terminate. Termination causes the object to
     * set the sfIsTerminated flag (so a component can not be terminated
     * twice). Need some way of stopping this server object, so that remote
     * stubs will fail, but does not solve the local reference problem (local
     * objects can still call this object)
     *
     * @param status termination status record
     */
    public void sfTerminate(TerminationRecord status) {
        terminateNotifying(status, sfParent);
    }

    /**
     * Provides hook for subclasses to implement usefull termination behavior.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        try {
            org.smartfrog.sfcore.security.SecureRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            // @TODO: Log. Ignore.
        }
        org.smartfrog.sfcore.common.Logger.log (this.sfCompleteNameSafe().toString(),status);
        try {
            sfTerminateWithHooks.applyHooks(this, status);
        } catch (Exception e) {
        }
    }

    /**
     * Notifies a component that this component has terminated through
     * sfTerminatedWith and calls the local hook (sfTerminateWith).
     *
     * @param status termination status
     * @param comp component to notify of termination
     */
    protected void terminateNotifying(TerminationRecord status, Prim comp) {
        // Provide ID to termination record
        // Note that it uses the name of the first component terminated not the actual caller id.
        if (status.id == null) {
            try {
                status.id = this.sfCompleteNameSafe();
            } catch (Exception ex) {
                //Usually when disconnected from network and Parent not reachable
                try {
                    status.id = SFProcess.getProcessCompound().sfCompleteName();
                    status.id.addElement(ReferencePart.here(
                    SFProcess.getProcessCompound().sfAttributeKeyFor(this)));
                } catch (Exception ex2) {
                    //ignore
                }
            }
        }

        synchronized (this) {
            if (sfIsTerminated) {
                return;
            }
            sfStopLivenessSender();
            sfIsTerminated = true;
        }

        try {
            sfTerminateWith(status);
        } catch (Exception ex) {
            // ignore
            Logger.logQuietly(ex);
        }

        if (comp != null) {
            try {
                comp.sfTerminatedWith(status, this);
            } catch (Exception ex) {
                Logger.logQuietly(ex);
            }
        }
    }

    /**
     * Get this object to detach from its parent. Calls sfStartLivenessSender
     * since this component might now be eligible for a liveness sender.
     *
     * @throws SmartFrogException detachment failed
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDetach()
        throws SmartFrogException, RemoteException {
        if (sfParent == null) {
            return;
        }
        try {
            if (!(SFProcess.getProcessCompound().sfContainsChild(this))) {
                //Registers with local process compound!
               SFProcess.getProcessCompound().sfRegister(this.sfResolveId(
                   SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME), this);
            }
            ((ChildMinder) sfParent).sfRemoveChild(this);
            sfParent = null;
            sfStartLivenessSender();
            sfParentageChanged();
        } catch (SmartFrogResolutionException ex){
            //@Todo: log
            //ignore
        }

    }


    /**
     * Get this object to terminate, after detaching itself from its parent.
     *
     * @param status termination status
     */
    public synchronized void sfDetachAndTerminate(TerminationRecord status) {
        try {
            if (sfParent != null) {
                ((ChildMinder) sfParent).sfRemoveChild(this);
            }
            sfParent = null;
            terminateNotifying(status, null);
        } catch (Exception ex) {
            Logger.logQuietly(ex);
        }
    }

    /**
     * Called from sub-component (normally) to indicate termination. Subclasses
     * should implement useful behavior.
     *
     * @param status termination status
     * @param comp component that has terminated
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
    }

    /**
     * Called from up the containment tree to terminate. This is to avoid the
     * upcall to the parent which sfTerminate causes.  Will only cause
     * termination if the sender component is the parent of this component.
     *
     * @param status termination status
     */
    public void sfTerminateQuietlyWith(TerminationRecord status) {
        terminateNotifying(status, null);
    }

    /**
     * Liveness call in to check if this component is still alive. This method
     * can be overriden to check other state of a component. An example is
     * Compound where all children of the compound are checked. This basic
     * check updates the liveness count if the ping came from its parent.
     * Otherwise (if source non-null) the liveness count is decreased by the
     * sfLivenessFactor attribute. If the count ever reaches 0 liveness
     * failure on tha parent has occurred and sfLivenessFailure is called with
     * source this, and target parent. Note: the sfLivenessCount must be
     * decreased AFTER doing the test to correctly count the number of ping
     * opportunities that remain before invoking sfLivenessFailure. If done
     * before then the number of missing pings is reduced by one. E.g. if
     * sfLivenessFactor is 1 then a sfPing from the parent sets
     * sfLivenessCount to 1. The sfPing from a non-parent would reduce the
     * count to 0 and immediately fail.
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        if (sfIsTerminated) {
            throw new SmartFrogLivenessException("Component Terminated");
        }

        if ((source == null) || (sfParent == null)) {
            return;
        }

        if (source.equals(sfParent)) {
            sfLivenessCount = sfLivenessFactor;
        } else if ((sfLivenessSender != null) &&
                source.equals(sfLivenessSender) && (sfLivenessCount-- <= 0)) { // ptm: must decrement *AFTER* test
            sfLivenessFailure(this, sfParent, null);
        }
    }

    /**
     * Handle ping failures. Default behavior is to terminate with a liveness
     * send failure record storing the name of the target of the ping (which
     * generally is one of the children or the parent of this component).
     *
     * @param source source of update
     * @param target target that update was trying to reach
     * @param failure error that occurred
     */
    protected void sfLivenessFailure(Object source, Object target,
        Throwable failure) {
        // Failed to send liveness to children, terminate by default
        Reference targetName = null;
        Reference myName = null;

        try {
            targetName = ((Prim) target).sfCompleteName();
        } catch (Exception ex) {
            // ignore, leave null
        }

        try {
            myName = this.sfCompleteNameSafe();
        } catch (Exception ex) {
            //Usually when disconnected from network and Parent not reachable
            if (myName == null) {
                try {
                    myName = SFProcess.getProcessCompound().sfCompleteName();
                    myName.addElement(ReferencePart.here(
                            SFProcess.getProcessCompound().sfAttributeKeyFor(this)));
                } catch (Exception ex2) {
                    //ignore
                }
            }
        }

        if (myName != null) {
            sfTerminate(TerminationRecord.abnormal("Liveness Send Failure in " +
                    myName, targetName));
        } else {
            sfTerminate(TerminationRecord.abnormal("Liveness Send Failure",
                    targetName));
        }
    }

    /**
     * Implemented to provide for remote equality checking. If the primitive
     * was export the sfExportRef is used to compare objects, otherwise the
     * super class (Object) is requested to check equality.
     *
     * @param o object to compare with
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object o) {
        if (sfExportRef == null) {
            return super.equals(o);
        } else {
            return sfExportRef.equals(o);
        }
    }

    /**
     * Returns the hashcode for the exported object if it was exported, the
     * object itself if not.
     *
     * @return hashcode for this object or its remote reference
     */
    public int hashCode() {
        if (sfExportRef == null) {
            return super.hashCode();
        } else {
            return sfExportRef.hashCode();
        }
    }

    /**
     * Returns the string of the remote reference if this primitive was
     * exported, the superclass toString if not.
     *
     * @return string form for this component
     */
    public String toString() {
        if (sfExportRef == null) {
            return super.toString();
        } else {
            return sfExportRef.toString();
        }
    }

    /**
     * Terminates the component in a seperate thread. A component should call
     * this method when it encounters any fatal exception that requires
     * termination of the component.
     *
     * @param targetComponent The component that encountered the error
     * @param excp The error that caused the termination
     * @param componentId Component Identifier
     */
    protected void terminateComponent(Prim targetComponent, Throwable excp,
        Reference componentId) {
        TerminatorThread terminator = new TerminatorThread(targetComponent,
                excp, componentId);//.quietly();
        // start the thead
        terminator.start();
    }


    /**
     *  To get the sfCore logger
     * @return Logger implementing LogSF and Log
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public LogSF sfGetProcessLog() throws SmartFrogException, RemoteException {
       return sfGetLog(SmartFrogCoreKeys.SF_CORE_LOG);
    }

    /**
     * To get a logger
     * @param name logger name
     * @return Logger implementing LogSF and Log
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public LogSF sfGetLog(String name) throws SmartFrogException, RemoteException {
       return LogFactory.getLog(name);
    }

    /**
     *  To get application logger using ROOT name.
     *  The name used is cached in attritube @see SmartFrogCoreKeys.SF_APP_LOG_NAME
     *  If this attribute has been pre-set then it is used to get the applition logger,
     *  otherwise ROOT cannonical name is used.
     *
     * @return Logger implementing LogSF and Log
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public LogSF sfGetApplicationLog() throws SmartFrogException, RemoteException{
        //@todo should we use prim name and get a hierarchy of logs?
         //this.sfResolveHere(SmartFrogCoreKeys.SF_APP_LOG_NAME);
        try {
            return sfGetLog(sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, "", true));
        } catch (SmartFrogResolutionException ex) {
            //Get root Log name
            String sfLogName = ((Prim)sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT)).sfCompleteName().toString();
            // add attribute
            sfAddAttribute(SmartFrogCoreKeys.SF_APP_LOG_NAME,sfLogName);
            return sfGetLog(sfLogName);
        }

    }

    /**
     * Parentage changed in component hierachy.
     * Actions: sfCompleteName cache is cleaned
     */
    public void sfParentageChanged() throws RemoteException{
       sfCompleteName=null;
    }
}
