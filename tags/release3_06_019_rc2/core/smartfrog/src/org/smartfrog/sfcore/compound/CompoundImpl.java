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

import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.SFDeployer;
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
     * Initial capacity for child vector. Looks up Compound.childCap (offset by
     * SmartFrogCoreProperty.propBaseCompound). Defaults to 5 if not there
     */
    public static int childCap = Integer.getInteger(SmartFrogCoreProperty.compoundChildCap, 5)
                                        .intValue();

    /**
     * Capacity increment for child vector. Looks up Compound.childInc (offset
     * by SmartFrogCoreProperty.propBaseCompound). Defaults to 2 if not there
     */
    public static int childInc = Integer.getInteger(SmartFrogCoreProperty.compoundChildInc, 2)
                                        .intValue();

    /** Maintains children on which life of compound depends (and vice versa). */
    protected Vector sfChildren = new Vector(childCap, childInc);


    /** Maintains a temporal list of the children that have to be driven
     * through their sfDeploy and sfStart lifecycle methods.*/
    protected Vector lifecycleChildren = new Vector();

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
     * An internal SmartFrog method.
     * It deploys a compiled component and makes it an attribute of the
     * parent compound. Also start heartbeating the deployed component
     * if the component registers. Note that the remaining lifecycle methods must
     * still be invoked on the created component - namely sfDeploy() and sfStart().
     * This is primarily an internal method - the prefered method for end users is
     * #sfCreateNewChild.
     *
     * Note that the remaining lifecycle methods must
     * still be invoked on the created component - namely sfDeploy() and sfStart().
     * This is primarily an internal method - the preferred method for end users is
     * #sfCreateNewChild.
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
            ComponentDescription cmp, Context parms) throws SmartFrogDeploymentException {
        // check for attribute already named like given name
        try {
            Object res = ((parent == null) || (name == null)) ? null: sfResolveHere(name,false);

            if ((res != null) && !(res instanceof ComponentDescription)) {
                throw new SmartFrogDeploymentException(null, parent.sfCompleteName() ,
                            name, cmp, parms,MessageUtil.
                                formatMessage(MSG_NON_REP_ATTRIB, name), null,null);
            }

            if (sfLog().isTraceEnabled()){
              StringBuffer message = new StringBuffer();
              try {
                message.append(this.sfCompleteNameSafe());
                message.append(" is deploying: ");
                if (name != null) {
                  message.append(name);
                } else {
                  message.append("no-name");
                }
                if (parent != null) {
                  message.append(", Parent: ");
                  message.append(parent.sfCompleteName());
                }
                message.append(", Component description: ");
                message.append(cmp.toString());
                if (parms != null) {
                  message.append(", Params: ");
                  message.append(parms.toString());
                }
              } catch (Exception ex1) {
                sfLog().trace("",ex1);
              }
              sfLog().trace(message.toString());
            }

            // try to deploy
            Prim result = SFDeployer.deploy(cmp, null, parent, parms);

            /**
             *
             * @TODO don't like this, we need to make the attribute over-write atomic with child registration (Patrick).
             *
             */
            if (parent != null){
                if (name != null) {
                  parent.sfReplaceAttribute(name, result);
                  //result.sfParentageChanged(); // yuk.... see todo above!
                } else {
                    //@TODO - Review after refactoring ProcessCompound
                    //This should throw an excetion when a
                    //component is registered without a name
                    //in a processcompound, but compound should not know anything
                    //about processcompound
                }
            }
            return result;
        } catch (SmartFrogDeploymentException dex) {
            // It will build source recursively
            Reference newRef =new Reference();
            if (name==null) {
                //@todo review methods for compDesc
                if (cmp.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME))
                    name =cmp.sfContext().get(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME);
                try {
                    newRef = parent.sfCompleteName();
                } catch (Exception ex){
                    // LOG ex
                    ignoreThrowable("could not get complete name", ex);
                }
            }
            if ((dex.get(SmartFrogDeploymentException.OBJECT_NAME))!=null) {
                newRef.addElement (ReferencePart.here(name));
            } else {
                dex.add(SmartFrogDeploymentException.OBJECT_NAME, name);
            }
            if (dex.get(SmartFrogDeploymentException.SOURCE)!=null) {
                newRef.addElements((Reference)dex.get(SmartFrogDeploymentException.SOURCE));
            }

            if (newRef.size()!=0) {
                dex.put(SmartFrogDeploymentException.SOURCE, newRef);
            }
            throw dex;
        } catch (Throwable thr) {
            throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(thr);
        }
    }

    /**
     * A high-level component deployment method - creates a child of this
     * Compound, running it through its entire lifecycle. This is the preferred way
     * of creating new child components of a Compound. The method is safe against
     * multiple calls of lifecycle.
     *
     * @param name name of attribute which the deployed component should adopt
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successfull
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewChild(Object name, ComponentDescription cmp, Context parms)
        throws RemoteException, SmartFrogDeploymentException {
        return sfCreateNewChild( name, this, cmp, parms);
    }




    /**
     * A high-level component deployment method - creates an app
     * , running it through its entire lifecycle. This is the preferred way
     * of creating new app.
     *
     * @param name name for the new application
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successful
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewApp(String name, ComponentDescription cmp, Context parms)
        throws RemoteException, SmartFrogDeploymentException {
        return this.sfCreateNewChild(name, null, cmp, parms);
    }

    /**
     * A high-level component deployment method - creates a child of 'parent'
     * Compound, running it through its entire lifecycle. This is the preferred way
     * of creating new child components of a Compound.
     *
     * @param name name of attribute which the deployed component should adopt
     * @param parent of deployer component
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successfull
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewChild(Object name, Prim parent,
                                 ComponentDescription cmp, Context parms) throws
        RemoteException, SmartFrogDeploymentException {
        Prim comp = null;

        if (parms==null)parms = new ContextImpl();
        try {
            // This is needed so that the root component is properly named
            // when registering with the ProcessCompound
            if ((parent==null)&&(name!=null))parms.put(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, name);

            if (sfLog().isTraceEnabled()) {
                try {
                    if (parent!=null) {
                        sfLog().trace("Creating new child '"+name+"' for: "+
                                      parent.sfCompleteName()+
                                      ", with description: "+cmp.toString()+
                                      ", and parameters: "+parms);
                    } else {
                        sfLog().trace("Creating new application: "+name+
                                      ", with description: "+cmp.toString()+
                                      ", and parameters: "+parms);
                    }
                } catch (Exception ex1) {
                    sfLog().trace(ex1.toString());
                }
            }

            //Copies component description before deploying it!
            comp = sfDeployComponentDescription(name, parent, (ComponentDescription)cmp.copy(), parms);
            // it is now a child, so need to guard against double calling of lifecycle...
            try {
                comp.sfDeploy();
            } catch (Throwable thr) {
                if (thr instanceof SmartFrogLifecycleException) {
                    throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thr);
                }
                throw SmartFrogLifecycleException.sfDeploy("Failed to create a new child.", thr, this);
            }
            try {
                comp.sfStart(); // otherwise let the start of this component do it...
            } catch (Throwable thr) {
                if (thr instanceof SmartFrogLifecycleException) {
                    throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thr);
                }
                throw SmartFrogLifecycleException.sfStart("Failed to create a new child.", thr, this);
            }
        } catch (Exception e) {
            if (comp!=null) {
                Reference compName = null;
                try { compName = comp.sfCompleteName(); } catch (Throwable thr) { }
                try {
                    if (parent!=null) {
                        comp.sfDetachAndTerminate(TerminationRecord.abnormal(
                            "Deployment Failure: "+e.getMessage(), compName, e));
                    } else {
                        comp.sfTerminate(TerminationRecord.abnormal(
                            "Deployment Failure: "+e.getMessage(), compName, e));
                    }
                } catch (Exception ex) {
                    //log
                    ignoreThrowable("Could not terminate", ex);
                }
            }
            throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(e);
        }

        if (sfLog().isTraceEnabled()) {
            try {
                if (parent!=null) {
                    sfLog().trace("New child created: "+comp.sfCompleteName()+ " ");
                } else {
                    sfLog().trace("New application created: "+ comp.sfCompleteName()+" ");
                }
            } catch (Exception ex1) {
                sfLog().trace(ex1.toString());
            }
        }
        return comp;
    }

    //
    // ChildMinder interface
    //

    /**
     * Liveness interface to compound. A liveness target must be an attribute
     * off the compound.
     *
     * @param target target to heartbeat
     */
    //public synchronized void sfAddChild(Liveness target) {
    // if synchronized -> locks processCompound when it registers back!
    public void sfAddChild(Liveness target) throws RemoteException {
        sfChildren.addElement(target);
        ((Prim)target).sfParentageChanged();
    }

    /**
     * Removes a liveness interface from the heartbeat targets. The target is
     * also removed as an attribute of the compound.
     *
     * @param target object to remove from heartbeat
     *
     * @return true if child is removed successfully else false
     */
    public boolean sfRemoveChild(Liveness target) throws SmartFrogRuntimeException, RemoteException  {
        boolean res = sfChildren.removeElement(target);
        try {
          sfRemoveAttribute(sfAttributeKeyFor(target));
        } catch (SmartFrogRuntimeException ex) {
          //Ignore: it happens when attribute does not exist
        }
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
    public synchronized Object sfRemoveAttribute(Object key)
        throws SmartFrogRuntimeException, RemoteException {

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
    public synchronized void sfDeployWith(Prim parent, Context cxt) throws
        SmartFrogDeploymentException, RemoteException {

        super.sfDeployWith(parent, cxt);

        sfDeployWithChildren();
    }

    /**
     * Method that selects the children that compound will drive through their lifecycle
     * The children are stored in 'lifecycleChildren'
     * @throws SmartFrogDeploymentException
     */
    protected void sfDeployWithChildren() throws SmartFrogDeploymentException {
      try { // if an exception is thrown in the super call - the termination is already handled
          for (Enumeration e = sfContext().keys(); e.hasMoreElements(); ) {
              Object key = e.nextElement();
              Object elem = sfContext.get(key);

              if ((elem instanceof ComponentDescription)&&(((ComponentDescription)elem).getEager())) {
                  lifecycleChildren.add(sfDeployComponentDescription(key, this, (ComponentDescription)elem, null));
              }
          }
      } catch (Exception sfex) {
          new TerminatorThread(this, sfex, null).quietly().run();
          throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfex);
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
            //set our order. We do this before calling our super, in case we have to handle an exception at this point.
            sfSyncTerminate = sfResolve(SmartFrogCoreKeys.SF_SYNC_TERMINATE, false, false);
            //deploy Prim
            super.sfDeploy();
            //deploy our children.
            sfDeployChildren();
        } catch (Throwable thr) {
            Reference name = sfCompleteNameSafe();
            sfGetCoreLog().error("caught on deployment ("+name.toString()+")", thr);
            throw SmartFrogLifecycleException.forward(thr);
        }
    }

    /**
     * This is an override point.
     * It is called during {@link #sfDeploy()} <i>after</i>
     * Prim has deployed, and it instantiates all children.
     * It is not synchronized, but is called from a synchronized parent method.
     * If overridden, a subclass must call <tt>super.sfDeployChildren()</tt>
     * if they want to deploy any children.
     * @throws SmartFrogResolutionException if stuff cannot get resolved
     * @throws RemoteException if the network is playing up
     * @throws SmartFrogLifecycleException if any exception (or throwable) is
     * raised by a child component.
     */
    protected void sfDeployChildren() throws SmartFrogResolutionException, RemoteException, SmartFrogLifecycleException {
        for (Enumeration e = lifecycleChildren.elements(); e.hasMoreElements();) {
            Object elem = e.nextElement();
            if (elem instanceof Prim) {
                try{
                    ((Prim) elem).sfDeploy();
                } catch (Throwable thr){
                    String name = "";
                    try {name =((Prim)elem).sfCompleteName().toString();} catch (RemoteException ex) {};
                    SmartFrogLifecycleException sflex = SmartFrogLifecycleException.sfDeploy(name ,thr,this);
                    String classFailed = ((Prim) elem).sfResolve(SmartFrogCoreKeys.SF_CLASS,"",false);
                    sflex.add(SmartFrogLifecycleException.DATA,"Failed object class: "+ classFailed);
                    throw sflex;
                }
            }
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
        try {
            super.sfStart();
            sfStartChildren();
         } catch (Throwable thr) {
               // any exception causes termination
               Reference name = sfCompleteNameSafe();
               sfTerminate(TerminationRecord.abnormal("Compound sfStart failure: " + thr, name));
               sfGetCoreLog().error("caught on start ("+name.toString()+")", thr);
               throw SmartFrogLifecycleException.forward(thr);
         }
    }

    /**
     * This is an override point.
     * It is called during {@link #sfStart()} <i>after</i>
     * Prim has started, and it starts all children.
     * It is not synchronized, but is called from a synchronized parent method.
     * If overridden, a subclass must call <tt>super.sfStartChildren()</tt>
     * if they want to start any children.
     * @throws SmartFrogResolutionException if stuff cannot get resolved
     * @throws RemoteException if the network is playing up
     * @throws SmartFrogLifecycleException if any exception (or throwable) is
     * raised by a child component.
     */

    protected void sfStartChildren() throws SmartFrogLifecycleException,
        RemoteException, SmartFrogResolutionException {
        for (Enumeration e = lifecycleChildren.elements(); e.hasMoreElements();) {
            Object elem = e.nextElement();
            if (elem instanceof Prim) {
                try {
                    ((Prim) elem).sfStart();
                } catch (Throwable thr){
                    String name = "";
                    try {name =((Prim)elem).sfCompleteName().toString();} catch (Exception ex) {};
                    SmartFrogLifecycleException sflex = SmartFrogLifecycleException.sfStart(name ,thr,this);
                    sflex.add(SmartFrogLifecycleException.DATA,
                            "Failed object class: "+((Prim) elem).sfResolve(SmartFrogCoreKeys.SF_CLASS,"",false));
                    throw sflex;
                }
            }
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
        //Re-check of sfSynchTerminate to get runtime changes.
        try {
            sfSyncTerminate = sfResolve(SmartFrogCoreKeys.SF_SYNC_TERMINATE, sfSyncTerminate, false);
        } catch (Exception sfrex){
          //Ignore
        }
        if (sfSyncTerminate) {
            sfSyncTerminateWith(status);
        } else {
            sfASyncTerminateWith(status);
        }

        super.sfTerminateWith(status);
    }

    /**
     * Iterates over children telling each of them to terminate quietly with
     * given status. It iterates from the last one created to the first
     * one.
     *
     * @param status status to terminate with
     */
    protected void sfSyncTerminateWith(TerminationRecord status) {
        if (sfLog().isTraceEnabled()) {
           sfLog().trace("SYNCTermination: "+ sfCompleteNameSafe(),null,status);
        }
        for (int i = sfChildren.size()-1; i>=0; i--) {
            try {
                ((Prim)sfChildren.elementAt(i)).sfTerminateQuietlyWith(status);
            } catch (Exception ex) {
                // Log
                ignoreThrowable("ignoring during termination", ex);
                // ignore
            }
        }
    }

    /**
     * Terminate children asynchronously using a seperate thread for each call.
     * It iterates from the last one created to the first one.
     *
     * @param status status to terminate with
     */
    protected void sfASyncTerminateWith(TerminationRecord status) {
        if (sfLog().isTraceEnabled()) {
           sfLog().trace("ASYNCTermination: "+ sfCompleteNameSafe(),null,status);
        }
        for (int i = sfChildren.size()-1; i>=0; i--) {
            try {
                //Deprecated: new TerminateCall((Prim)(sfChildren.elementAt(i)), status);
                (new TerminatorThread((Prim)(sfChildren.elementAt(i)),status).quietly()).start();
            } catch (Exception ex) {
            //@TODO: Log
                ignoreThrowable("ignoring during termination", ex);
            // ignore
            }
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
    public void sfPing(Object source) throws SmartFrogLivenessException,
                                                            RemoteException {
	// check the timing of the parent pings
        super.sfPing(source);

	// return if children not to be checked
        if ((source == null) || (sfLivenessDelay == 0)) {
            return;
        }

        if (sfLivenessSender == null) {
	    // don't have my own liveness sender, so check if it is my parent
	    if (!source.equals(sfParent)) {
		return;
	    }
	} else {
	    // have own checker - its my responsibility from here on, so return if not me
	    if (!source.equals(sfLivenessSender)) {
		return;
	    }
	}


	// the following, checking children, should only happen if source is own livenes sender or
        // it is the parent checking and I don't have my own check

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
     * Parentage changed in component hierachy. A notification is sent to all
     * children.
     */
    public void sfParentageChanged() throws RemoteException {
        for (Enumeration e = sfChildren(); e.hasMoreElements();) {
             Prim p = (Prim)(e.nextElement());
             p.sfParentageChanged();
        }
        super.sfParentageChanged();
    }


    /**
     * handler for any throwable/exception whose throwing is being ignored
     * @param message
     * @param thrown
     */
    private void ignoreThrowable(String message,Throwable thrown) {
        sfGetCoreLog().ignore(message, thrown);
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
