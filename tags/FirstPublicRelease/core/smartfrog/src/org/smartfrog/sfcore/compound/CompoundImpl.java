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

package org.smartfrog.sfcore.compound;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Dump;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;


/**
 * Implements the compound component behavior. A compound deploys component
 * descriptions, and maintains them as its children. This includes liveness,
 * termination and location behavior
 *
 */
public class CompoundImpl extends PrimImpl implements Compound {
    /**
     * Property base for children vector properties. Uses SFSystem.propBase as
     * basis
     */
    public static final String propBase = SFSystem.propBase +
        "sfcore.compound.";

    /**
     * Initial capacity for child vector. Looks up Compound.childCap (offset by
     * SFSystem.propBase). Defaults to 5 if not there
     */
    public static int childCap = Integer.getInteger(propBase + "childCap", 5)
                                        .intValue();

    /**
     * Capacity increment for child vector. Looks up Compound.childInc (offset
     * by SFSystem.propBase). Defaults to 2 if not there
     */
    public static int childInc = Integer.getInteger(propBase + "childInc", 2)
                                        .intValue();

    /** Maintains children on which life of compound depends (and vice versa). */
    protected Vector sfChildren = new Vector(childCap, childInc);

    /**
     * Whether termination should be synchronous. Determined by the
     * sfSyncTerminate attribute "true" or "false"
     */
    protected boolean sfSyncTerminate;

    /**
     * Creates a compound implementation.
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public CompoundImpl() throws RemoteException {
    }

    /**
     * Deploys a compiled component. The deployed component is added to the
     * parent attribute table. This method does not forward deployment to the
     * parent if parent is not this, so if parent is remote it could be
     * expensive and exception sensitive. A check is made if the an attrbute
     * with given made exists in parent and is replaceable. An attribute is
     * only replaceable if its value is a component description.
     *
     * @param name name to name deployed component under in context
     * @param parent of deployer component
     * @param cmp compiled component to deploy
     * @param parms parameters for description
     *
     * @return newly deployed component
     *
     * @throws SmartFrogDeploymentException failed to deploy compiled component
     */
    public Prim sfDeployComponentDescription(Object name, Prim parent,
            ComponentDescription cmp, Context parms)
            throws SmartFrogDeploymentException {
        // check for attribute already named like given name
        try {
            Object res = ((parent == null) || (name == null)) ? null
            : sfResolveId(name);

            if ((res != null) && !(res instanceof ComponentDescription)) {
                throw new SmartFrogDeploymentException(null, parent.sfCompleteName() ,name, cmp, parms,MessageUtil.
                        formatMessage(MSG_NON_REP_ATTRIB, name), null,null);
            }
            // try to deploy
            Prim result = cmp.deploy(null, parent, parms);

            if ((parent != null) && (name != null)) {
                parent.sfReplaceAttribute(name, result);
            }
            return result;
        }  catch (SmartFrogDeploymentException dex) {
            // It will build source recursively
            Reference newRef =new Reference();
            if (name==null) {
                if (parms.containsKey("sfProcessComponentName"))
                    name =parms.get("sfProcessComponentName");
                try { newRef = parent.sfCompleteName();} catch (Exception ex){}  // LOG ex
            }
            if ((dex.get(dex.OBJECT_NAME))!=null)
                newRef.addElement (ReferencePart.here(name));
            else dex.add(dex.OBJECT_NAME, name);

            if (dex.get(dex.SOURCE)!=null)
                newRef.addElements((Reference)dex.get(dex.SOURCE));

            if (newRef.size()!=0) dex.put(dex.SOURCE, newRef);

            throw dex;
        } catch (Throwable thr) {
            throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(thr);
        }
    }

    //
    // ChildMinder
    //

    /**
     * Liveness interface to compound. A liveness target must be an attribute
     * off the compound.
     *
     * @param target target to heartbeat
     */
    //public synchronized void sfAddChild(Liveness target) {
    // if synchronized -> locks processCompound when it registers back!
    public void sfAddChild(Liveness target) {
        sfChildren.addElement(target);
    }

    /**
     * Removes a liveness interface from the heartbeat targets. The target is
     * also removed as an attribute of the compound.
     *
     * @param target object to remove from heartbeat
     *
     * @return true if child is removed successfully else false
     */
    public boolean sfRemoveChild(Liveness target) {
        boolean res = sfChildren.removeElement(target);
        sfRemoveAttribute(sfAttributeKeyFor(target));

        return res;
    }

    /**
     * Checks whether this compound contains given child.
     *
     * @param child child to check
     *
     * @return true if child in compound, false otherwise
     */
    public boolean sfContainsChild(Liveness child) {
        return sfChildren.contains(child);
    }

    /**
     * Returns an enumeration over the children of the compound.
     *
     * @return enumeration over children
     */
    public Enumeration sfChildren() {
        return ((Vector) sfChildren.clone()).elements();
    }

    //
    // Prim
    //

    /**
     * Override superclass behaviour.  If in heart beat then remove.
     *
     * @param key attribute key to remove
     *
     * @return Reference to removed object
     */
    public synchronized Object sfRemoveAttribute(Object key) {
        Object res = super.sfRemoveAttribute(key);

        if (res instanceof Liveness) {
            sfRemoveChild((Liveness) res);
        }

        return res;
    }

    /**
     * Primitive deploy call. Causes the compound to do initial deployment of
     * its contained eager component descriptions. Deployed results are then
     * placed in the compound context. It is the responsibility of the
     * deployed component to register with the heart beat of the compound.
     * PrimImpl takes care of that as part of the sfDeployWith call.
     *
     * @param parent parent component
     * @param cxt context for compound
     *
     * @exception SmartFrogDeploymentException failed to deploy sub-components
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeployWith(Prim parent, Context cxt)
                                throws SmartFrogDeploymentException, RemoteException {
        super.sfDeployWith(parent, cxt);

        for (Enumeration e = sfContext().keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object elem = sfContext.get(key);

                if ((elem instanceof ComponentDescription) &&
                        (((ComponentDescription) elem).getEager())) {
                    sfDeployComponentDescription(key, this,
                        (ComponentDescription) elem, null);
                }
        }
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws SmartFrogException failure deploying compound or
     *            sub-component
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            Object st = sfResolveId("sfSyncTerminate");

            if (st == null) {
                sfSyncTerminate = false;
            } else if (st instanceof String) {
                sfSyncTerminate = Boolean.valueOf((String) st).booleanValue();
            } else {
                sfSyncTerminate = ((Boolean) st).booleanValue();
            }

            super.sfDeploy();

            for (Enumeration e = sfChildren(); e.hasMoreElements();) {
                Object elem = e.nextElement();
                if (elem instanceof Prim) {
                    try{
                        ((Prim) elem).sfDeploy();
                    } catch (Throwable thr){
                        String name = "";
                        try {name =((Prim)elem).sfCompleteName().toString();} catch (Exception ex) {};
                        SmartFrogLifecycleException sflex = SmartFrogLifecycleException.sfDeploy(name ,thr,null);
                        sflex.add(sflex.DATA,"Failed object class: "+((Prim) elem).sfResolve("sfClass","",false));
                        throw sflex;
                    }
                }
            }
        } catch (SmartFrogException sfex){
            throw sfex;
        } catch (Throwable thr) {
            throw SmartFrogLifecycleException.sfDeploy(null,thr, (Prim)this);
        }
    }
    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        try {
            for (Enumeration e = sfChildren(); e.hasMoreElements();) {
                Object elem = e.nextElement();

                if (elem instanceof Prim) {
                    try {
                        ((Prim) elem).sfStart();
                    } catch (Throwable thr){
                        String name = "";
                        try {name =((Prim)elem).sfCompleteName().toString();} catch (Exception ex) {};
                        SmartFrogLifecycleException sflex = SmartFrogLifecycleException.sfStart(name ,thr,null);
                        sflex.add(sflex.DATA,"Failed object class: "+((Prim) elem).sfResolve("sfClass","",false));
                        throw sflex;
                    }
                }
            }
         } catch (Throwable thr) {
               // any exception causes termination
               Reference name = sfCompleteNameSafe();
               sfTerminate(TerminationRecord.abnormal("Compound sfStart failure: " + thr,
                                  name));
               throw SmartFrogLifecycleException.forward(thr);
         }
    }

    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if (sfSyncTerminate) {
            sfSyncTerminateWith(status);
        } else {
            sfASyncTerminateWith(status);
        }

        super.sfTerminateWith(status);
    }

    /**
     * Iterates over children telling each of them to terminate quietly with
     * given status.
     *
     * @param status status to terminate with
     */
    protected void sfSyncTerminateWith(TerminationRecord status) {
        for (Enumeration e = sfChildren(); e.hasMoreElements();)
            try {
                ((Prim) e.nextElement()).sfTerminateQuietlyWith(status);
            } catch (Exception ex) {
                // ignore
            }
    }

    /**
     * Terminate children asynchronously using a seperate thread for each call.
     *
     * @param status status to terminate with
     */
    protected void sfASyncTerminateWith(TerminationRecord status) {
        for (Enumeration e = sfChildren(); e.hasMoreElements();)
            try {
                new TerminateCall((Prim) e.nextElement(), status);
            } catch (Exception ex) {
                // ignore
            }
    }

    /**
     * Sent by sub-components on termination. Compound behaviour is to
     * terminate itself if the sending component is one if it's heart beat
     * targets.
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        // Compound dies if sub-components die
        if (sfContainsChild(comp)) {
            sfTerminate(status);
        }
    }

    /**
     * Override to forward call to all liveness targets.
     *
     * @param target target to dump to
     */
    public void sfDumpState(Dump target) {
        super.sfDumpState(target);

        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object elem = e.nextElement();

            if (elem instanceof Prim) {
                new DumpCall((Prim) elem, target);
            }
        }
    }

    /**
     * Implements ping for a compound. A compound extends prim functionality by
     * pinging each of its children, any failure to do so will call
     * sfLivenessFailure with the compound as source and the errored child as
     * target. The exception that ocurred is also passed in. This check is
     * only done if the source is non-null and if the source is the parent (if
     * parent exists). If there is no parent and the source is non-null the
     * check is still done.
     *
     * @param source source of ping
     *
     * @exception SmartFrogLivenessException liveness failed
     */
    public void sfPing(Object source) throws SmartFrogLivenessException {
        super.sfPing(source);

        if ((source == null) || (sfLivenessDelay == 0)) {
            return;
        }

        if ((sfLivenessSender != null) && source.equals(sfParent)) {
            return;
        }

        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
            Object child = e.nextElement();

            try {
                sfPingChild((Liveness) child);
            } catch (Exception ex) {
                sfLivenessFailure(this, child, ex);
            }
        }
    }

    /**
     * Called for each child by sfPing if liveness is to be passed on.
     *
     * @param child child to send to
     *
     * @throws SmartFrogLivenessException failed to ping child
     * @throws RemoteException In case of Remote/nework error
     */
    protected void sfPingChild(Liveness child) throws SmartFrogLivenessException, RemoteException {
        child.sfPing(this);
    }

    /**
     * Implements an asynchronous sfTerminateQuietlyWith call
     */
    private class TerminateCall implements Runnable {
        /**
         * Reference to component.
         */
        private Prim target;

        /**
         * Termination record.
         */
        private TerminationRecord record;
        
        /**
         * Constructs TerminateCall with component and termination record.
         */
        public TerminateCall(Prim target, TerminationRecord record) {
            this.target = target;
            this.record = record;
            (new Thread(this)).start();
        }
        /**
         * Runs the thread.
         */
        public void run() {
            try {
                target.sfTerminateQuietlyWith(record);
            } catch (Exception remex) {
                // ignore
            }
        }
    }

    /**
     * Implements an asynchronous dump call
     */
    private class DumpCall implements Runnable {
        /**
         * Reference to component.
         */
        private Prim prim;
        
        /**
         * Reference to dump.
         */
        private Dump dump;

        /**
         * Constructor taking the component to call and the dump interface to
         * dump to. Starts the dump call immediately as a thread
         *
         * @param callee source of dump call
         * @param caller destination of dump call
         */
        protected DumpCall(Prim callee, Dump caller) {
            this.prim = callee;
            this.dump = caller;
            (new Thread(this)).start();
        }

        /**
         * Run part, just do the call, ignoring exceptions
         */
        public void run() {
            try {
                prim.sfDumpState(dump);
            } catch (Exception remex) {
                // ignore
            }
        }
    }
}
