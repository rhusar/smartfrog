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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Diagnostics;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SFMarshalledObject;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.AssertReference;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.RemoteReferenceResolverHelperImpl;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SecureRemoteObject;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.SerializableIterator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Defines the base class for all deployed components. A deployed component
 * knows how to react to termination, deployment requests and heart beats. This
 * implementation allows subclasses to define how a deployed component is
 * communicated with. The sfExport attribute is examined. If set to "true" the
 * sfExportRef method is called, which by default uses RMI to export the
 * component. Subclasses may choose to export a component another way.
 * <p/>
 * <p> A liveness thread is started for a primitive if it has no parent, but is
 * expected to have children (like Compound), or if the parent is remote.
 * Liveness attribute sfLivenessDelay dictates how often (in seconds) sfPing is
 * supposed to be called. sfLivenessFactor is the multiplier for sfLivenessDelay
 * to wait until the primitive declares that there is a liveness failure. Both
 * attributes are looked up using ATTRIB in order to find out whether a parent
 * has set liveness on. </p>
 */
public class PrimImpl extends RemoteReferenceResolverHelperImpl
        implements Prim, MessageKeys {

    /**
     * Component Log. This log is used to from any component. It can be replaced using sfSetLog()
     */
    private LogSF sfLog;

    /**
     * Static attribute that hold the lifecycle hooks for sfDeploy.
     */
    public static final PrimHookSet sfDeployHooks = new PrimHookSet();

    /**
     * Static attribute that hold the lifecycle hooks for sfStart.
     */
    public static final PrimHookSet sfStartHooks = new PrimHookSet();

    /**
     * Static attribute that hold the lifecycle hooks for sfDeployWith.
     */
    public static final PrimHookSet sfDeployWithHooks = new PrimHookSet();

    /**
     * Static attribute that hold the lifecycle hooks for sfTerminateWith.
     */
    public static final PrimHookSet sfTerminateWithHooks = new PrimHookSet();

    /**
     * Reference used to look up sfLivenessDelay attributes.
     */
    protected static final Reference refLivenessDelay = new Reference( ReferencePart.attrib( SmartFrogCoreKeys.SF_LIVENESS_DELAY));

    /**
     * Reference used to look up sfLivenessFactor attributes.
     */
    protected static final Reference refLivenessFactor = new Reference( ReferencePart.attrib( SmartFrogCoreKeys.SF_LIVENESS_FACTOR));

    /**
     * Flag indicating that this component has been terminated.
     */
    protected volatile boolean sfIsTerminated = false;

    /**
     * Flag indicating that this component termination is initiated.
     */
    protected volatile boolean sfIsTerminating = false;

    /**
     * Flag indicating that this component has been deployed.
     */
    protected volatile boolean sfIsDeployed = false;

    /**
     * Flag indicating that this component has been started.
     */
    protected volatile boolean sfIsStarted = false;

    /**
     * Parent.
     */
    protected Prim sfParent = null;

    /**
     * Attribute context.
     */
    protected Context sfContext = null;

    /**
     * Timer for initiating heartbeats.
     */
    protected LivenessSender sfLivenessSender;

    /**
     * Current count for liveness, updated through sfPing.
     */
    protected int sfLivenessCount;

    /**
     * Livneess factor. Initializer for liveness count. How many multiples of
     * livenss delay to wait till a liveness failure of the parent is declared,
     * Defaults to 2.
     */
    protected int sfLivenessFactor = 2;

    /**
     * How often to send liveness in seconds. Defaults to 0 (off, ie. no
     * liveness).
     */
    protected long sfLivenessDelay = 0;

    /**
     * Reference to export form of self if sfExport is true.
     */
    protected Object sfExportRef = null;

    /**
     * Reference that caches cannonical name.
     */
    protected Reference sfCompleteName = null;


    /**
     * Name of tag for attribute injection
     */
    private final String injectionTag = "sfInject";

    /**
     * Used in conjunction with sfDeployWith to set parent and context after
     * creation.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public PrimImpl(){
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
    public Object sfResolveHere(Object name)
            throws SmartFrogResolutionException {
        Object result = null;
        try {
            result = sfContext.sfResolveAttribute(name);
            try {
                // if sfLog() is called then a new log is created and an upcall is triggered
                if ((sfLog != null) && sfLog().isTraceEnabled()) {
                    sfLog().trace("sfResolved HERE '" + name.toString() + "' to '" + result.toString() + "'");
                }
            } catch (Throwable thr) {
                thr.printStackTrace();
            } //ignore

        } catch (SmartFrogContextException ex) {
            //sfCompleteName() uses sfResolveHere() and therefore it can be a problem here :-)!
            // Using the sfCompleteName cache to provide as much info a it is available.
            throw SmartFrogResolutionException.notFound(new Reference(name), sfCompleteName, ex);

        }
        return result;
    }

    /**
     * Find an attribute in this context.
     *
     * @param name      attribute key to resolve
     * @param mandatory boolean that indicates if this attribute must be present
     *                  in the description. If it is mandatory and not found it
     *                  throws a SmartFrogResolutionException
     *
     * @return Object value for attribute
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHere(Object name, boolean mandatory)
            throws SmartFrogResolutionException {
        //the check for mandatory is done outside of the call, for
        //a very minor speedup when references do not resolve
        //and yet are needed. There are other costs (branch misprediction penalty),
        //of course
        if (mandatory) {
            return sfResolveHere(name);
        } else {
            try {
                return sfResolveHere(name);
            } catch (SmartFrogResolutionException ignored) {
                //failed to resolve a non-mandatory element
            }
        }
        return null;
    }

    /**
     * Find an attribute in this context, so long as it is visible anywhere.
     *
     * @param name      attribute key to resolve
     * @param mandatory boolean that indicates if this attribute must be present
     *                  in the description. If it is mandatory and not found it
     *                  throws a SmartFrogResolutionException
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
        } catch (Exception e) {
            if (mandatory) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("Error accessing attribute tags " + name, e);
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
     * Returns the parent for this component.
     *
     * @return parent for this component or null if none
     */
    public Object sfResolveParent() {
        return sfParent;
    }

    /**
     * Resolves a (copy of) a given reference. Forwards to indexed resolve with
     * index 0 after making sure that the DATA flag is unset if necessary.
     * Method returns the resulting attribute value.
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
        Reference rn = r;
        if (r.getData()) {
            //clone should be enough at this point.
            rn = (Reference) r.clone();
            rn.setData(false);
        }

        Object obj = sfResolve(rn, 0);
        if (obj instanceof SFMarshalledObject) {
            //  Unmarshall!Obj.
            try {
                obj = ((SFMarshalledObject) obj).get();
            } catch (IOException e) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(e.getMessage(), e);
            }
        }
        try {
            if ((sfLog!=null) && sfLog().isTraceEnabled()) {
                sfLog().trace(sfCompleteNameSafe() + " sfResolved '" + rn.toString() + "' to '" + obj.toString() + "'");
            }
        } catch (Throwable thr) {
            thr.printStackTrace();
        } //ignore
        return obj;
    }


    /**
     * Resolves given reference starting at given index. This is forwarded to
     * the reference (and on to each reference part).
     *
     * @param r     reference to resolve
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
                    || (rex.get(SmartFrogRuntimeException.SOURCE) == null)) {
                rex.put(SmartFrogRuntimeException.SOURCE, sfCompleteNameSafe());
                rex.put(SmartFrogResolutionException.DEPTH, new Integer(index));
            }
            if ((!(rex.containsKey(SmartFrogResolutionException.REFERENCE)))) {
                rex.put(SmartFrogResolutionException.REFERENCE, r);
            }
            rex.appendPath(this.sfCompleteName().toString() + " ");
            throw rex;
        } catch (StackOverflowError st) {
            throw new SmartFrogResolutionException(r, sfCompleteNameSafe(), st.toString() + ". Possible cause: cyclic reference", null, st, this);
        } catch (OutOfMemoryError st) {
            throw new SmartFrogResolutionException(r, sfCompleteNameSafe(), st.toString() + ". Possible cause: cyclic reference", null, st, this);
        } catch (Throwable thr) {
            throw new SmartFrogResolutionException(r, sfCompleteNameSafe(), null, null, thr, this);
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
            return SFProcess.sfDeployedHost();
        } catch (Exception ex) {
            String message = MessageUtil.formatMessage(
                    MSG_FAILED_INET_ADDRESS_LOOKUP);
            if (sfLog().isErrorEnabled()) {
                sfLog().error(message, ex);
            }
            throw new RemoteException(null,
                    SmartFrogException.forward(message, ex));
        }
    }

    /**
     * Request the process in which this component is deployed, the name being
     * that defined in the sfProcessName attribute or the string ROOT if in the
     * root process compound.
     *
     * @return the name of the process
     *
     * @throws RemoteException In case of Remote/network error
     */
    public String sfDeployedProcessName() throws RemoteException {
        String value = System.getProperty(
                "org.smartfrog.sfcore.processcompound.sfProcessName");

        if (value == null) {
            return SmartFrogCoreKeys.SF_ROOT;
        } else {
            return value;
        }
    }

    /**
     * Adds an attribute to this component under given name. If the attribute
     * value is a ComponentDescription  then this component is set as its
     * parent
     *
     * @param name  name of attribute
     * @param value value of attribute
     *
     * @return added attribute if non-existent or null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfAddAttribute(Object name, Object value)
            throws SmartFrogRuntimeException, RemoteException {
        Object valueParent = null;
        try {

            if (value instanceof ComponentDescription) {
                //Set right parentage for ComponentDescription
                valueParent = ((ComponentDescription) value).sfPrimParent();
                ((ComponentDescription) value).setPrimParent(this);
            }
            return sfContext.sfAddAttribute(name, value);

        } catch (SmartFrogContextException ex) {
            if (valueParent != null) {
                ((ComponentDescription) value).setPrimParent((Prim) valueParent);
            }
            ex.init(this);
            throw ex;
        }
    }

    /**
     * Removes an attribute from this component. If the attribute value removed
     * is a component description, then its prim parent is removed as well.
     *
     * @param name of attribute to be removed
     *
     * @return removed attribute value if successfull or null if not
     *
     * @throws SmartFrogRuntimeException when name is null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfRemoveAttribute(Object name)
            throws SmartFrogRuntimeException, RemoteException {
        try {
            Object value = sfContext.sfRemoveAttribute(name);
            if (value instanceof ComponentDescription) {
                ((ComponentDescription) value).setPrimParent(null);
            }
            return value;
        } catch (SmartFrogContextException ex) {
            ex.init(this);
            throw ex;
        }
    }

    /**
     * Replace named attribute in component context. If attribute is not present
     * it is added to the context. If the attribute value added is a component
     * description, then its parent is set to this and/or if the one removed is
     * a component description then its parent is reset. If the attribute is
     * defined as requiring injection - the attribute will be injected after the
     * value of the attribute has been set.
     *
     * @param name  of attribute to replace
     * @param value value to add or replace
     *
     * @return the old value if present, null otherwise. It old value was a
     *         component description, then its prim parent is reset.
     *
     * @throws SmartFrogRuntimeException when name or value are null, or
     * injection failed
     * @throws RemoteException In case of Remote/network error
     */
    public synchronized Object sfReplaceAttribute(Object name, Object value)
            throws SmartFrogRuntimeException, RemoteException {

        Prim valueParent = null;

        try {
            if (value instanceof ComponentDescription) {
                //Set right parentage for ComponentDescription
                valueParent = ((ComponentDescription) value).sfPrimParent();
                ((ComponentDescription) value).setPrimParent(this);
            }
            Object oldValue = sfContext.sfReplaceAttribute(name, value);
            if ((oldValue != null) && (oldValue instanceof ComponentDescription) && (oldValue != value)) {
                ((ComponentDescription) oldValue).setPrimParent(null);
            }

            if (sfContainsTag(name, injectionTag)) {
                injectAttribute(name, value);
            }
            return oldValue;
        } catch (SmartFrogContextException ex) {
            if (valueParent != null) {
                ((ComponentDescription) value).setPrimParent(valueParent);
            }
            ex.init(this);
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
     * Returns true if the context contains a ref to value.
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfContainsValue(Object value) throws RemoteException {
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
     *
     * @param attribute to check
     *
     * @return true if context contains attribute, false otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfContainsAttribute(Object attribute)
            throws RemoteException {
        return sfContext.sfContainsAttribute(attribute);
    }

    /**
     * Returns an ordered iterator over the attribute names in this component.
     * The remove operation of this Iterator won't affect the contents of this
     * component
     *
     * @return iterator
     *
     * @throws RemoteException In case of Remote/network error
     */
    public Iterator<Object> sfAttributes() throws RemoteException {
        return new SerializableIterator<Object>(sfContext.sfAttributes());
    }

    /**
     * Returns an ordered iterator over the attribute values in this component.
     * The remove operation of this Iterator won't affect the contents of this
     * component
     *
     * @return iterator
     *
     * @throws RemoteException In case of Remote/network error
     */
    public Iterator<Object> sfValues() throws RemoteException {
        return new SerializableIterator<Object>(sfContext.sfValues());
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
     * Returns the complete name for this component from the root of the
     * application.
     *
     * @return reference of attribute names to this component
     *
     * @throws RemoteException In case of network/rmi error
     */
    public Reference sfCompleteName() throws RemoteException {
        if (sfCompleteName == null) {
            Reference r;
            Object key;
            if (sfParent == null) {
                r = SFProcess.getProcessCompound().sfCompleteName();
                key = SFProcess.getProcessCompound().sfAttributeKeyFor(this);
            } else {
                r = sfParent.sfCompleteName();
                key = sfParent.sfAttributeKeyFor(this);
            }
            sfCompleteName = (Reference) r.clone();

            if (key != null) {
                sfCompleteName.addElement(ReferencePart.here(key));
            } else {
                // This will happen when sfCompleteName is called before Prim is registered with its parent
                // A call to sfParentageChanged after the registration happens will clean up the cache.
                //@todo we will replace unknown with its name once we modify the interface so that the component registers itself with its parent
                sfCompleteName.addElement(new HereReferencePart("*unknown*"));
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace(
                            "Internal error generating complete name - child not named in parent yet");
                }
            }
        }
        return sfCompleteName;
    }

    /**
     * Returns the complete name for this component from the root of the
     * application and does not throw any exception. If an exception is thrown
     * it will return a new empty reference.
     *
     * @return reference of attribute names to this component or an empty
     *         reference
     */
    public Reference sfCompleteNameSafe() {
        return ComponentHelper.completeNameSafe(this);
    }


    /**
     * Returns the parent of this component.
     *
     * @return parent component
     *
     * @throws RemoteException In case of Remote/network error
     */
    public Prim sfParent() throws RemoteException {
        return sfParent;
    }


    /**
     * Private method to set up newly created component. Primitives should only
     * override sfDeploy since this is the one which does the actual work
     *
     * @param parent parent of component
     * @param cxt    context for component
     *
     * @throws SmartFrogDeploymentException In case of any error while deploying
     * the component
     * @throws RemoteException In case of network/rmi error
     */
    public void sfDeployWith(Prim parent, Context cxt) throws SmartFrogDeploymentException, RemoteException {

        try {
            sfParent = parent;
            sfContext = cxt;

            sfLog=null;
            //Init Log
            sfLog();

            sfDeployWithHooks.applyHooks(this, null);

            // set the prim parent link of any contained component description to this Prim
            // so that references work
            for (Enumeration e = sfContext.keys(); e.hasMoreElements();) {
                Object value = sfContext.get(e.nextElement());
                // Only set prim parent for LAZY descriptions. Otherwise not needed because it will be deployed.
                if (value instanceof ComponentDescription) {
                    ((ComponentDescription) value).setPrimParent(this);
                }
            }

            boolean es; // allow exportRef to be defined by string (backward compatability) or boolean
            Object eso = sfResolveHere(SmartFrogCoreKeys.SF_EXPORT, false);

            if (eso == null) {
                es = true;
            } else if (eso instanceof String) {
                es = Boolean.valueOf((String) eso).booleanValue();
            } else {
                es = ((Boolean) eso).booleanValue();
            }

            if (es) {
                //Default value=0 = Anonymous port.
                int port = 0;
                Object portObj = sfResolveHere(SmartFrogCoreKeys.SF_EXPORT_PORT, false);
                Object exportRef = null;

                sfExport(portObj);
            }

            if (sfParent != null) {
                ((ChildMinder) sfParent).sfAddChild(this);
            }

            registerWithProcessCompound();

            // Look up delay, if not there never mind looking up factor
            sfLivenessDelay = sfResolve(refLivenessDelay, sfLivenessDelay, false);
            sfLivenessFactor = sfResolve(refLivenessFactor, sfLivenessFactor, false);

            // copy in local description for efficiency when subcomponents looking up
            sfReplaceAttribute(SmartFrogCoreKeys.SF_LIVENESS_DELAY, new Long(sfLivenessDelay));
            // copy in local description for efficiency when subcomponents looking up
            sfReplaceAttribute(SmartFrogCoreKeys.SF_LIVENESS_FACTOR, new Integer(sfLivenessFactor));

            sfLivenessCount = sfLivenessFactor;

            // start the liveness thread
            sfStartLivenessSender();

            // add location information attributes
            try {
                sfReplaceAttribute(SmartFrogCoreKeys.SF_HOST, sfDeployedHost());
            } catch (Exception e) {
                if (sfLog().isWarnEnabled()) {
                    sfLog().warn("Failed to get a valid value for " + SmartFrogCoreKeys.SF_HOST + " attribute.", e);
                }
            }
            sfReplaceAttribute(SmartFrogCoreKeys.SF_PROCESS, sfDeployedProcessName());

        } catch (Exception sfex) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error(sfex);
            }
            //Logger.log(sfex);
            new TerminatorThread(this, sfex, null).quietly().start();
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(
                    sfex);
        }
    }

    /**
     * Exports this  component using portObj. portObj can be a port or a vector
     * containing a set of valid ports. If a vector is used the component tries
     * to see if the port used by the local ProcessCompound is in the vector set
     * and use that if so. If not tries to use the first one avaible
     *
     * @param portObj Object
     *
     * @return Object Reference to exported object
     *
     * @throws RemoteException In case of Remote/network error
     * @throws SmartFrogException if failed to export
     */
    protected Object sfExport(Object portObj) throws
            RemoteException, SmartFrogException {
        Object exportRef = null;
        int port = 0; //default value
        if ((portObj != null)) {
            if (portObj instanceof Integer) {
                port = ((Integer) portObj).intValue();
                exportRef = sfExportRef(port);
                sfAddAttribute("sfPort", new Integer(port));
            } else if (portObj instanceof Vector) {
                //Get rootProcess port
                Object portObjPC = SFProcess.getProcessCompound()
                        .sfResolve("sfPort", false);
                //compare with range
                if ((portObjPC != null) && (((Vector) portObj).contains(
                        portObjPC))) {
                    port = ((Integer) portObjPC).intValue();
                    exportRef = sfExportRef(port);
                    sfAddAttribute("sfPort", new Integer(port));
                } else {
                    //if not in range use vector and try
                    int size = ((Vector) (portObj)).size();
                    for (int i = 0; i < size; i++) {
                        //get
                        try {
                            port = ((Integer) ((Vector) (portObj)).elementAt(i))
                                    .intValue();
                            exportRef = sfExportRef(port);
                            sfAddAttribute("sfPort", new Integer(port));
                            break;
                        } catch (SmartFrogException ex) {
                            if (i >= size - 1) {
                                throw ex;
                            }
                        }
                    } //for
                }
            }
        } else {
            //Get rootProcess port
            Object portObjPC = SFProcess.getProcessCompound()
                    .sfResolve("sfPort", false);
            if (portObjPC != null) {
                //use port used by ProcessCompound
                port = ((Integer) portObjPC).intValue();
                exportRef = sfExportRef(port);
                sfAddAttribute("sfPort", new Integer(port));
            } else {
                // use ramdom
                exportRef = sfExportRef(port);
            }
        }
        return exportRef;
    }

    /**
     * Registers component with local ProcessCompound only if it is root
     * component, its parent is remote or the attribute sfProcessComponentName
     * is defined.
     *
     * @throws RemoteException In case of Remote/network error
     * @throws SmartFrogException if failed to register
     */
    protected void registerWithProcessCompound()
            throws RemoteException, SmartFrogException {
        //Registers component with local ProcessCompound
        if ((sfParent == null) ||
                sfIsRemote(sfParent) ||
                sfContext.containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
            try {
                SFProcess.getProcessCompound().sfRegister(sfResolveHere(
                        SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false),
                        this);
            } catch (Exception ex) {
                throw (SmartFrogDeploymentException) SmartFrogDeploymentException
                        .forward(ex);
            }
        }
    }

    /**
     * Deregisters this component from local ProcessCompound (if ever
     * registered)
     */
    private void deregisterWithProcessCompound() {
        //
        try {
            if (SFProcess.getProcessCompound() != null) {
                SFProcess.getProcessCompound().sfDeRegister(this);
            }
        } catch (Exception ex) {
            // @TODO: Log. Ignore.
            //Logger.logQuietly(ex);
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }
    }

    /**
     * Export this primitive to accept remote method calls using port. If port
     * value is 0 it will be using an anonymous port. Default implementation is
     * to use UnicastRemoteObject. Check is done if sfExportRef is already set,
     * in which case this is returned. <b>Note</b> that for remote methods to
     * work equals, hashCode and toString must be implemented. This is done for
     * PrimImpl in which case those requests are forwarded to the sfExportRef.
     *
     * @param port where to export. If 0, it will use any port available.
     *
     * @return exported primitive
     *
     * @throws SmartFrogException failed to export primitive
     */
    public Object sfExportRef(int port) throws SmartFrogException {
        if (sfExportRef == null) {
            try {
                sfExportRef = SecureRemoteObject.exportObject(this, port);
            } catch (RemoteException rex) {
                throw new SmartFrogLifecycleException(MessageUtil.formatMessage(
                        MSG_OBJECT_REGISTRATION_FAILED), rex);
            } catch (SFGeneralSecurityException sfgsex) {
                throw new SmartFrogLifecycleException(MessageUtil.formatMessage(
                        MSG_OBJECT_REGISTRATION_FAILED), sfgsex);
            }
        }

        return sfExportRef;
    }

    /**
     * Export this primitive to accept remote method calls using an anonymous
     * port. Implementation is to use UnicastRemoteObject. Check is done if
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
        return sfExportRef(0);
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
                        sfLivenessDelay * 1000,
                        sfCompleteNameSafe().toString());
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
     * Checks whether given object is a non-local object. This implementation is
     * RMI oriented, so checks whether the given object is a RemoteStub, which
     * would indicate that this object is remote. Subclasses can override this
     * (and associated sfExport method) if the underlying remote infrastructure
     * is not RMI.
     *
     * @param o object to check remoteness on
     *
     * @return true if object a remote reference, false if local
     */
    protected boolean sfIsRemote(Object o) {
        return (o instanceof RemoteStub);
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior. Attributees that require injection are handled during
     * sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        sfSetLog(sfGetApplicationLog());
        sfCompleteName(); //prime the name cache...

        if (sfIsTerminated) {
            throw new SmartFrogDeploymentException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED,
                    sfCompleteName().toString()),
                    this);
        }
        injectAttributes();
        sfDeployHooks.applyHooks(this, null);
        sfIsDeployed = true;
    }

    /**
     * Iterate over all the attributes in the context to see if they require to
     * be injected (have the sfIntect tag) and if so, carry out the injection
     * using injectAttribute
     *
     * @throws SmartFrogException - an error occurred during one  of the
     * injections
     */
    private void injectAttributes() throws SmartFrogException {
        Iterator<Object> i = null;
        i = sfContext.sfAttributes();
        while (i.hasNext()) {
            Object name = i.next();

            boolean needsInjection = false;
            try {
                needsInjection = sfContainsTag(name, injectionTag);
            } catch (Exception e) {
            }
            if (needsInjection) {
                Object value = null;
                try {
                    value = sfResolve(new Reference(ReferencePart.here(name)));
                } catch (Exception e) {
                    throw new SmartFrogDeploymentException(
                            MessageUtil.formatMessage(MSG_INJECTION_VALUE_FAILED,
                                    sfCompleteNameSafe().toString(),
                                    name), e, this, null
                    );
                }
                injectAttribute(name, value);
            }
        }
    }

    /**
     * Inject an attribute into the component Injection consists of calling a
     * setter method or the direct assignment to a field of the component. The
     * setter method name is concatenation of "set" with the attribute name nad
     * must take an Object as a parameter (it may generate a class-caste
     * internally). If the setter is not defined, a field with the same name as
     * the attribute will be asssigned to the value. If neither the setter nor
     * the field exist, an exception is generated.
     *
     * @param name  the name of the attribute being injected
     * @param value the value of the attribute
     *
     * @throws SmartFrogDeploymentException most probably neither the setter nor
     * the field exists in the comopnent
     */
    private void injectAttribute(Object name, Object value)
            throws SmartFrogDeploymentException {
        try {
            String injectorName = "set" + name;

            Class[] p = null;
            try {
                p = new Class[]{Class.forName("java.lang.Object")};
            } catch (Exception e) {
            }

            Method m = getClass().getMethod(injectorName, p);
            Object[] values = new Object[]{value};
            try {
                m.invoke(this, values);
            } catch (Exception e) {
                throw new SmartFrogDeploymentException(
                        MessageUtil.formatMessage(MSG_INJECTION_SETMETHOD_FAILED,
                                sfCompleteNameSafe().toString(),
                                name,
                                value), e, this, null
                );
            }
        } catch (NoSuchMethodException nsm) {
            try {
                Field f = getClass().getField(name.toString());
                f.set(this, value);
            } catch (NoSuchFieldException nsf) {
                throw new SmartFrogDeploymentException(
                        MessageUtil.formatMessage(MSG_INJECTION_FAILED,
                                sfCompleteNameSafe().toString(),
                                name), this, null
                );
            } catch (Exception e) {
                throw new SmartFrogDeploymentException(
                        MessageUtil.formatMessage(MSG_INJECTION_SETFIELD_FAILED,
                                sfCompleteNameSafe().toString(),
                                name,
                                value), e, this, null
                );
            }
        }
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
                    MSG_START_COMP_TERMINATED,
                    sfCompleteNameSafe().toString()),
                    this);
        }
        sfStartHooks.applyHooks(this, null);
        sfIsStarted = true;
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
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }
    }

    /**
     * Request this component to terminate. Termination causes the object to set
     * the sfIsTerminated flag (so a component can not be terminated twice).
     * Need some way of stopping this server object, so that remote stubs will
     * fail, but does not solve the local reference problem (local objects can
     * still call this object)
     *
     * @param status termination status record
     */
    public void sfTerminate(TerminationRecord status) {
        terminateNotifying(status, sfParent);
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        //org.smartfrog.sfcore.common.Logger.log (this.sfCompleteNameSafe().toString(),status);
        if (sfLog().isTraceEnabled()) {
            sfLog().trace(sfCompleteNameSafe().toString(), null, status);
        }
        try {
            sfTerminateWithHooks.applyHooks(this, status);
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }

        deregisterWithProcessCompound();
    }


    protected final Object termLock = new Object();

    /**
     * Notifies a component that this component has terminated through
     * sfTerminatedWith and calls the local hook (sfTerminateWith).
     *
     * @param status termination status
     * @param comp   component to notify of termination
     */

    protected void terminateNotifying(TerminationRecord status, Prim comp) {
        // Provide ID to termination record
        // Note that it uses the name of the first component terminated not the actual caller id.

        // protect aganist two callers invoing this
        //  can't synchronize of "this" as it can cause deqdlock in sync termination
        synchronized (termLock) {
            if (sfIsTerminating || sfIsTerminated) {
                return;
            }
            sfIsTerminating = true;
        }

        if (status.id == null) {
            try {
                status.id = sfCompleteNameSafe();
            } catch (Exception ex) {
                //Usually when disconnected from network and Parent not reachable
                try {
                    status.id = SFProcess.getProcessCompound().sfCompleteName();
                    status.id.addElement(ReferencePart.here(SFProcess.getProcessCompound().sfAttributeKeyFor(this)));
                } catch (Exception ex2) {
                    if (sfLog().isIgnoreEnabled()) {
                        sfLog().ignore(ex2);
                    }
                }
            }
        }

        try {
            sfTerminateWith(status);
        } catch (Exception ex) {
            // ignore
            //Logger.logQuietly(ex);
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }

        if (comp != null) {
            try {
                comp.sfTerminatedWith(status, this);
            } catch (Exception ex) {
                // Logger.logQuietly(ex);
                if (sfLog().isIgnoreEnabled()) {
                    sfLog().ignore(ex);
                }
            }
        }

        //UnExports this component.
        // It has to be placed after comp.sfTerminatedWith(status, this);
        try {
            org.smartfrog.sfcore.security.SecureRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            // @TODO: Log. Ignore.
            //Logger.logQuietly(ex);
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }

        //synchronized (this) {
        sfIsTerminated = true;
        //}

    }

    /**
     * Get this object to detach from its parent. Calls sfStartLivenessSender
     * since this component might now be eligible for a liveness sender. Every
     * detached component will be re-parented with its local ProcessCompound.The
     * detached component will be registed using "sfProcessComponentName" value
     * or a random name if it does not exist. It sends sfParantageChanged()
     * notification.
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
            //First remove child
            ((ChildMinder) sfParent).sfRemoveChild(this);
            //Second register child with ProcessCompound if needed
            //(ProcessCompound will have a temporal parentage with application
            // root components and then it will detach!). Keep this order!)
            if (!(SFProcess.getProcessCompound().sfContainsChild(this))) {
                //Registers with local process compound!
                SFProcess.getProcessCompound().sfRegister(this.sfResolveHere(
                        SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, false),this);
            }
            sfParent = null;
            sfStartLivenessSender();
            sfParentageChanged();
        } catch (SmartFrogResolutionException ex) {
            sfLog().ignore("unknown at detach time", ex);
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
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(ex);
            }
        }
    }

    /**
     * Called from sub-component (normally) to indicate termination. Subclasses
     * should implement useful behavior.
     *
     * @param status termination status
     * @param comp   component that has terminated
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
     * Validate all ASSERTs in the context of the Prim, returning true if OK,
     * false if not.
     */
    public synchronized boolean sfValid() throws RemoteException {
        for (Enumeration e = sfContext.keys(); e.hasMoreElements();) {
            Object k = e.nextElement();
            if (sfContext.get(k) instanceof AssertReference) {
                try {
                    Object value = sfResolve(new Reference(ReferencePart.here(k)));
                    if (value instanceof Boolean) {
                        if (!((Boolean) value).booleanValue()) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } catch (SmartFrogResolutionException e1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Liveness call in to check if this component is still alive. This method
     * can be overriden to check other state of a component. An example is
     * Compound where all children of the compound are checked. This basic check
     * updates the liveness count if the ping came from its parent. Otherwise
     * (if source non-null) the liveness count is decreased by the
     * sfLivenessFactor attribute. If the count ever reaches 0 liveness failure
     * on tha parent has occurred and sfLivenessFailure is called with source
     * this, and target parent. Note: the sfLivenessCount must be decreased
     * AFTER doing the test to correctly count the number of ping opportunities
     * that remain before invoking sfLivenessFailure. If done before then the
     * number of missing pings is reduced by one. E.g. if sfLivenessFactor is 1
     * then a sfPing from the parent sets sfLivenessCount to 1. The sfPing from
     * a non-parent would reduce the count to 0 and immediately fail.
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness}
     * interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        if (Logger.logLiveness && (sfLog().isTraceEnabled())) {
            String sourceName = source.toString();
            if (source instanceof Prim) {
                try {
                    sourceName = ((Prim)source).sfCompleteName().toString();
                } catch (RemoteException e) {
                    if (sfLog().isIgnoreEnabled()) sfLog().ignore(e); 
                }
            }
            sfLog().trace("ping received from " + sourceName + ": in " + sfCompleteNameSafe() + ", counter " + sfLivenessCount);
        }

        if (sfIsTerminated) {
            if (Logger.logLiveness && (sfLog().isTraceEnabled())) {
                sfLog().trace("ping returning that I am terminated : in " + sfCompleteNameSafe());
            }
            throw new SmartFrogLivenessException(MessageUtil.formatMessage(
                    COMPONENT_TERMINATED));
        }

        /*
        try {
            System.out.println("validated " + sfCompleteNameSafe() + ": " + sfValid());
        } catch (Exception e) {
            System.out.println("validated " + sfCompleteNameSafe() + ": failed with " + e);
        }
        */

        if (source == null) {
            return;
        }

        if (sfParent == null) {
            // if am root - don't check counters...!
            return;
        }

        // memory model hack...
        boolean fail = false;
        synchronized (this) {
            if (source.equals(sfParent)) {
                if (Logger.logLiveness && (sfLog().isTraceEnabled())) {
                    sfLog().trace(
                            "parent ping received - resetting counter: in " + sfCompleteNameSafe());
                }
                sfLivenessCount = sfLivenessFactor;
            } else if ((sfLivenessSender != null) &&
                    source.equals(sfLivenessSender) &&
                    (sfLivenessCount-- <= 0)) {
                fail = true;
            }
        }

        if (fail) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug(
                        "failing as parent liveness checking had counted down: in " + sfCompleteNameSafe());
            }
            sfLivenessFailure(this, sfParent, null);
        }
    }

    /**
     * Handle ping failures. Default behavior is to terminate with a liveness
     * send failure record storing the name of the target of the ping (which
     * generally is one of the children or the parent of this component).
     *
     * @param source  source of update
     * @param target  target that update was trying to reach
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
            myName = sfCompleteNameSafe();
        } catch (Exception ex) {
            //Usually when disconnected from network and Parent not reachable
            if (myName == null) {
                try {
                    myName = SFProcess.getProcessCompound().sfCompleteName();
                    myName.addElement(ReferencePart.here(SFProcess.getProcessCompound().sfAttributeKeyFor(
                            this)));
                } catch (Exception ex2) {
                    //ignore
                }
            }
        }

        String failureMsg = "";
        if (failure != null) {
            failureMsg = " (Failure: " + failure.getMessage() + ")";
        }

        if (myName != null) {
            sfTerminate(TerminationRecord.abnormal(
                    MessageUtil.formatMessage(LIVENESS_SEND_FAILURE_IN
                            , myName, targetName + failureMsg),
                    targetName,
                    failure));
        } else {
            sfTerminate(TerminationRecord.abnormal(
                    MessageUtil.formatMessage(LIVENESS_SEND_FAILURE
                            , targetName + failureMsg), targetName, failure));
        }
    }

    /**
     * Implemented to provide for remote equality checking. If the primitive was
     * export the sfExportRef is used to compare objects, otherwise the super
     * class (Object) is requested to check equality.
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
     * @param excp            The error that caused the termination
     * @param componentId     Component Identifier
     */
    protected void terminateComponent(Prim targetComponent, Throwable excp,
                                      Reference componentId) {
        TerminatorThread terminator = new TerminatorThread(targetComponent,
                excp,
                componentId);//.quietly();
        // start the thead
        terminator.start();
    }

    /**
     * To log into sfCore logger. This method should be used to log Core
     * messages
     * <p/>
     * Types of possible logs in SF: - Core log - Application log - Component
     * log - Using sfSetLog() - Specific log - Using sfLog attribute in
     * component
     * <p/>
     * When initialized, sflog uses CoreLog, once the component enters in the
     * sfDeploy lifecycle phase, it is changed to use the specific log if sfLog
     * attribute is defined or if it is not defined it defaults to the core
     * log.
     *
     * @return Logger implementing LogSF and Log
     */
    public LogSF sfLog() {
        if (sfLog == null) {
            try {
                sfSetLog(sfGetApplicationLog());
            } catch (Exception ex) {
                sfSetLog(sfGetCoreLog());
            }
        }
        return sfLog;
    }


    /**
     * Method to replace logger used by components.
     *
     * @param newlog replacement for Prim core log
     *
     * @return oldlog
     */
    public synchronized LogSF sfSetLog(LogSF newlog) {
        LogSF oldlog = sfLog;
        sfLog = newlog;
        // add attribute
        try {
            sfReplaceAttribute(SmartFrogCoreKeys.SF_APP_LOG_NAME,newlog.getLogName());
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) {
                sfLog().err(ex);
            }
        }
        return oldlog;
    }


    /**
     * To get a logger with a particular name.
     *
     * @param name logger name
     *
     * @return Logger implementing LogSF and Log
     */
    public LogSF sfGetLog(String name) {
        return LogFactory.getLog(name);
    }

    /**
     * To get the sfCore logger
     *
     * @return Logger implementing LogSF and Log
     */
    public LogSF sfGetCoreLog() {
        return LogFactory.sfGetProcessLog();
    }

    /**
     * To get application logger using ROOT name. The name used is cached in
     * attritube {@link SmartFrogCoreKeys#SF_APP_LOG_NAME} If this attribute has
     * been pre-set then it is used to get the application logger, otherwise
     * ROOT cannonical name is used.
     *
     * @return Logger implementing LogSF and Log
     *
     * @throws SmartFrogException if failed
     * @throws RemoteException In case of Remote/network error
     */
    public LogSF sfGetApplicationLog() throws SmartFrogException, RemoteException {
        //@todo should we use prim name and get a hierarchy of logs?
        //this.sfResolveHere(SmartFrogCoreKeys.SF_APP_LOG_NAME,false);
        String sfLogName = null;
        try {
            // Check or sfLog attribute in component, if not initially defined
            // then it will be created during sfDeploy.
            Object obj = sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, true);
            if (obj instanceof Prim) {
                sfLogName = ((Prim) obj).sfCompleteName().toString();
            } else {
                sfLogName = obj.toString();
            }
            return sfGetLog(sfLogName);
        } catch (SmartFrogResolutionException ex) {
            //Get root Log name
            // Very expensive call
            //String sfLogName = ((Prim)sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT)).sfCompleteName().toString();
            if (sfParent() != null) {
                try {
                    // Check or sfLog attribute in parent
                    sfLogName = (sfParent().sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, "", true));
                } catch (SmartFrogResolutionException rex) {
                    sfLogName = this.sfCompleteName().toString();
                }
            } else {
                //I am the Root component for this application
                sfLogName = sfCompleteName().toString();
            }
            return sfGetLog(sfLogName);
        }

    }

    /**
     * Parentage changed in component hierachy. Actions: sfCompleteName cache is
     * cleaned
     *
     * @throws RemoteException In case of Remote/network error
     */
    public void sfParentageChanged() throws RemoteException {
        sfCompleteName = null;
    }

    /**
     * Returns value of flag indicating if this component has been terminated.
     *
     * @return boolean
     */
    public boolean sfIsTerminated() {
        return sfIsTerminated;
    }

    /**
     * Returns value of flag indicating if this component is terminating.
     *
     * @return boolean
     */
    public boolean sfIsTerminating() {
        return sfIsTerminating;
    }

    /**
     * Returns value of flag indicating if this component has been deployed.
     *
     * @return boolean
     */
    public boolean sfIsDeployed() {
        return sfIsDeployed;
    }

    /**
     * Returns value of flag indicating if this component has been started.
     *
     * @return boolean
     */
    public boolean sfIsStarted() {
        return sfIsStarted;
    }

    /**
     * Creates diagnostics report
     *
     * @return Component description
     */
    public ComponentDescription sfDiagnosticsReport() {
        ComponentDescription cd = null;
        try {
            cd = new ComponentDescriptionImpl(null,
                    new ContextImpl(),
                    false);
            //cd.setPrimParent(this);
            StringBuffer report = new StringBuffer();
            Diagnostics.doReport(report, this);
            cd.sfReplaceAttribute(SmartFrogCoreKeys.SF_DIAGNOSTICS_REPORT,
                    report);
        } catch (Throwable thr) {
            //ignore
            if (sfLog().isWarnEnabled()) {
                sfLog().warn(thr);
            }
        }
        return cd;
    }


    /* Update lifecycle */
    protected boolean updateAbandoned = false;

    /**
     * Inform component (and children, typically) that an update is about to
     * take place. Normally a component would quiesce its activity
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException  not OK to update
     */
    public synchronized void sfPrepareUpdate()
            throws RemoteException, SmartFrogException {
        Reference componentId = sfCompleteName();
        if (sfIsTerminated) {
            throw new SmartFrogUpdateException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED,
                    componentId.toString()));
        }
        boolean mayUpdate = sfResolve("sfUpdatable", false, false);
        if (!mayUpdate) {
            throw new SmartFrogUpdateException(
                    "Component not updatable - set sfUpdatable to true: " + componentId
                            .toString());
        }
        updateAbandoned = false;
    }

    protected Context newContext;

    /**
     * Validate whether the component (and its children) can be updated
     *
     * @param newCxt - the data that will replace the original context
     *
     * @return true - OK to update, false - OK to terminate and redeploy,
     *         exception - not OK to update
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException Failure not OK to update
     */
    public synchronized boolean sfUpdateWith(Context newCxt)
            throws RemoteException, SmartFrogException {
        Reference componentId = sfCompleteName();
        if (sfIsTerminated) {
            throw new SmartFrogUpdateException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED,
                    componentId.toString()));
        }
        if (updateAbandoned) {
            throw new SmartFrogUpdateException("update already abandoned " + componentId
                    .toString());
        }
        // validate the description, return false if it requires termination, exception to fail
        // cache context
        // check children that exist already
        //     identify those that should be terminated  (returned false)
        //     those to be updated (return true)
        // return true
        newContext = (Context) newCxt.copy();

        // check that all sf attributes are well defined...
        for (Iterator i = newContext.sfAttributes(); i.hasNext();) {
            String key = i.next().toString();
            if (key.startsWith("sf")) {
                try {
                    Object myValue = sfResolve(key, true);
                    if (!myValue.equals(newContext.get(key))) {
                        return false;  // non matching sf attribute
                    }
                } catch (SmartFrogResolutionException e) {
                    return false;  // there is a new sf attribute
                } catch (RemoteException e) {
                    sfAbandonUpdate();
                    throw new SmartFrogUpdateException(
                            "remote error during update",
                            e);
                }
            }
        }

        // if they are, then make sure that all sf attributes in the current comopnent are in the
        // new context
        for (Iterator i = sfContext.sfAttributes(); i.hasNext();) {
            String key = i.next().toString();
            if (key.startsWith("sf")) {
                newContext.put(key, sfContext.get(key));
            }
        }

        return true;
    }

    /**
     * Carry out the context update - no roll back from this point on.
     * Terminates children that need terminating, create and deployWith children
     * that need to be
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException Failure to update - to be
     * treated like a normal lifecycle error, by default with termination
     */
    public synchronized void sfUpdate()
            throws RemoteException, SmartFrogException {
        Reference componentId = sfCompleteName();
        if (sfIsTerminated) {
            throw new SmartFrogUpdateException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED,
                    componentId.toString()));
        }
        if (updateAbandoned) {
            throw new SmartFrogUpdateException("update already abandoned " + componentId
                    .toString());
        }
        // update context
        sfContext = newContext;
        // failure considered terminal
    }


    /**
     * Next phase of start-up after update - includes calling sfDeply on new
     * children Errors are considered terminal unless behaviour overridden.
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException Failure to update
     */

    public synchronized void sfUpdateDeploy()
            throws RemoteException, SmartFrogException {
        Reference componentId = sfCompleteName();
        if (sfIsTerminated) {
            throw new SmartFrogUpdateException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED,
                    componentId.toString()));
        }
        if (updateAbandoned) {
            throw new SmartFrogUpdateException("update already abandoned " + componentId
                    .toString());
        }
    }

    /**
     * Final phase of startup after update - includes calling sfStart on new
     * children Errors are considered terminal unless behaviour overridden.
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException Failure to update
     */
    public synchronized void sfUpdateStart()
            throws RemoteException, SmartFrogException {
        Reference componentId = sfCompleteName();
        if (sfIsTerminated) {
            throw new SmartFrogUpdateException(MessageUtil.formatMessage(
                    MSG_DEPLOY_COMP_TERMINATED,
                    componentId.toString()));
        }
        if (updateAbandoned) {
            throw new SmartFrogUpdateException("update already abandoned " + componentId
                    .toString());
        }
    }

    /**
     * Can occur after prepare and check, but not afterwards to roll back from
     * actual update process.
     *
     * @throws RemoteException network problems
     */
    public synchronized void sfAbandonUpdate() throws RemoteException {
        // notify all children of the abandon, ignoring all errors?
        // only occurs after failure of prepare or updatewith, future failure considered fatal
        if (updateAbandoned) {
            return;
        }
        updateAbandoned = true;
    }

    /**
     * Control of complete update process for a component, running through all
     * the above phases.
     *
     * @param desc component description to update
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException Failure to update

     */
    public void sfUpdateComponent(ComponentDescription desc)
            throws RemoteException, SmartFrogException {
        boolean ready;

        try {
            if (sfLog().isTraceEnabled()) {
                sfLog().trace("preparing");
            }
            sfPrepareUpdate();
            if (sfLog().isTraceEnabled()) {
                sfLog().trace("preparing done");
            }

            if (sfLog().isTraceEnabled()) {
                sfLog().trace("update with");
            }
            ready = sfUpdateWith(desc.sfContext());
            if (!ready) {
                throw new SmartFrogUpdateException(
                        "top level component must accept update",
                        null);
            }
            if (sfLog().isTraceEnabled()) {
                sfLog().trace("update with done");
            }
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error(e);
            }
            try {
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("abandoning");
                }
                sfAbandonUpdate();
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("abandoning done");
                }
            } catch (RemoteException e1) {
                // ignore?
            }

            if (e instanceof SmartFrogUpdateException) {
                throw (SmartFrogUpdateException) e;
            } else {
                String message = "error in update, abandoning";
                if (sfLog().isWarnEnabled()) {
                    sfLog().warn(message, e);
                }
                throw new SmartFrogUpdateException(message, e);
            }
        }

        if (ready) {
            try {
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("update");
                }
                sfUpdate();
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("update done");
                }
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("update deploy");
                }
                sfUpdateDeploy();
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("update deploy done");
                }
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("update start");
                }
                sfUpdateStart();
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("update start done");
                }
            } catch (Exception e) {
                String message = "fatal error in update - terminating";
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(message, e);
                }
                try {
                    sfTerminate(TerminationRecord.abnormal(message + " ",
                            sfCompleteNameSafe(),
                            e));
                } catch (Exception e1) {
                    // ignore?
                }
                throw new SmartFrogUpdateException(message + " application", e);
            }
        }
    }

    // implementation of the RemoteTags interface

    /**
     * Set the TAGS for an attribute. TAGS are simply uninterpreted strings
     * associated with each attribute.
     *
     * @param name attribute key for tags
     * @param tags a set of tags
     *
     * @throws SmartFrogException the attribute does not exist;
     */
    public void sfSetTags(Object name, Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        sfContext.sfSetTags(name, tags);
    }

    /**
     * Get the TAGS for an attribute. TAGS are simply uninterpreted strings
     * associated with each attribute.
     *
     * @param name attribute key for tags
     *
     * @return the set of tags
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public Set sfGetTags(Object name)
            throws RemoteException, SmartFrogRuntimeException {
        return sfContext.sfGetTags(name);
    }

    /**
     * add a tag to the tag set of an attribute
     *
     * @param name attribute key for tags
     * @param tag  a tag to add to the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfAddTag(Object name, String tag)
            throws RemoteException, SmartFrogRuntimeException {
        sfContext.sfAddTag(name, tag);
    }

    /**
     * remove a tag from the tag set of an attribute if it exists
     *
     * @param name attribute key for tags
     * @param tag  a tag to remove from the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfRemoveTag(Object name, String tag)
            throws RemoteException, SmartFrogRuntimeException {
        sfContext.sfRemoveTag(name, tag);
    }

    /**
     * add a tag to the tag set of an attribute
     *
     * @param name attribute key for tags
     * @param tags a set of tags to add to the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfAddTags(Object name, Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        sfContext.sfAddTags(name, tags);
    }

    /**
     * remove a tag from the tag set of an attribute if it exists
     *
     * @param name attribute key for tags
     * @param tags a set of tags to remove from the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfRemoveTags(Object name, Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        sfContext.sfRemoveTags(name, tags);
    }

    /**
     * Return an iterator over the tags for an attribute - not part of
     * RemoteTags, but is part of Tags
     *
     * @param name the name of the attribute
     *
     * @return an iterator over the tags
     *
     * @throws SmartFrogContextException the attribute does not exist;
     */
    public Iterator sfTags(Object name) throws SmartFrogException {
        return sfContext.sfTags(name);
    }

    /**
     * Return whether or not a tag is in the list of tags for an attribute
     *
     * @param name the name of the attribute
     * @param tag  the tag to chack
     *
     * @return whether or not the attribute has that tag
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public boolean sfContainsTag(Object name, String tag)
            throws RemoteException, SmartFrogRuntimeException {
        return sfContext.sfContainsTag(name, tag);
    }

    // implementation of the RemoteTagsComponent interface

    /**
     * Set the TAGS for this component. TAGS are simply uninterpreted strings
     * associated with each attribute.
     *
     * @param tags a set of tags
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfSetTags(Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            sfParent.sfSetTags(key, tags);
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            parent.sfSetTags(key, tags);
        }
    }

    /**
     * Get the TAGS for this component. TAGS are simply uninterpreted strings
     * associated with each attribute.
     *
     * @return the set of tags or null if no tags found
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public Set sfGetTags() throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            if (key == null) {
                return null;
            } else {
                return sfParent.sfGetTags(key);
            }
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            if (key == null) {
                return null;
            } else {
                return parent.sfGetTags(key);
            }
        }
    }

    /**
     * add a tag to the tag set of this component
     *
     * @param tag a tag to add to the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfAddTag(String tag)
            throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            sfParent.sfAddTag(key, tag);
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            parent.sfAddTag(key, tag);
        }
    }

    /**
     * remove a tag from the tag set of this component if it exists
     *
     * @param tag a tag to remove from the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfRemoveTag(String tag)
            throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            sfParent.sfRemoveTag(key, tag);
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            parent.sfRemoveTag(key, tag);
        }
    }

    /**
     * add a tag to the tag set of this component
     *
     * @param tags a set of tags to add to the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfAddTags(Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            sfParent.sfAddTags(key, tags);
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            parent.sfAddTags(key, tags);
        }
    }

    /**
     * remove a tag from the tag set of this component if it exists
     *
     * @param tags a set of tags to remove from the set
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public void sfRemoveTags(Set tags)
            throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            sfParent.sfRemoveTags(key, tags);
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            parent.sfRemoveTags(key, tags);
        }
    }


    /**
     * Return whether or not a tag is in the list of tags for this component
     *
     * @param tag the tag to chack
     *
     * @return whether or not the attribute has that tag
     *
     * @throws SmartFrogRuntimeException the attribute does not exist;
     * @throws RemoteException network problems
     */
    public boolean sfContainsTag(String tag)
            throws RemoteException, SmartFrogRuntimeException {
        Object key = null;
        if (sfParent != null) {
            key = sfParent.sfAttributeKeyFor(this);
            return sfParent.sfContainsTag(key, tag);
        } else {
            Prim parent = SFProcess.getProcessCompound();
            key = parent.sfAttributeKeyFor(this);
            return parent.sfContainsTag(key, tag);
        }
   }

}
