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

package org.smartfrog.sfcore.workflow.eventbus;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * An extension of Compound providing the SmartFrog Component
 * event handling and the ability of the subclass to control which children get deployed and when.
 * This compound is a good starting block for implementing any Compound extension with complex lifecycles,
 * as most of the setup is handled, and there are override points to tweak behaviour.
 */
public class EventCompoundImpl extends CompoundImpl implements EventBus,
    EventRegistration, EventSink, EventCompound {
    private static final Reference receiveRef = new Reference(ATTR_REGISTER_WITH);
    private static final Reference sendRef = new Reference(ATTR_SEND_TO);
    private EventRegistrar registrar = new EventRegistrar(this);

    protected ComponentDescription action=null;
    protected Context actions=null;
    protected Enumeration actionKeys=null;

    protected Reference name=null;
    private boolean oldNotation = true;
    private static final Reference actionsRef = new Reference(ATTR_ACTIONS);
    private static final Reference actionRef =  new Reference(ATTR_ACTION);
    public static final String ERROR_NO_CHILDREN_TO_DEPLOY = "Found no children to deploy";

    /**
     * Constructs EventCompoundImpl.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public EventCompoundImpl() throws RemoteException {
    }

    /**
     * This is an override point. The original set of event components
     * suppored the 'old' notation, in which actions were listed in the {@link #ATTR_ACTIONS element}
     * New subclasses do not need to remain backwards compatible and should declare this fact by
     * returning false from this method
     * @return true
     */
    protected boolean isOldNotationSupported() {
        return true;
    }

    /**
    * Method that overwrites compoundImpl behavior and delays loading eager components to sfStart phase.
    * If action or actions atributes are present then it behaves like compound and loads all eager components.
     * @throws SmartFrogDeploymentException for deployment problems
    */
    @Override
    protected void sfDeployWithChildren() throws SmartFrogDeploymentException {
      if (isOldNotationSupported() && sfContext().containsKey(ATTR_ACTIONS)){
          oldNotation=true;
          //Old WF notation using actions
          // Here follows normal CompoundImpl deployment
          super.sfDeployWithChildren();

      } else {
          oldNotation=false;
          // New WF notation
          // Delays any child component deployment
          try { // if an exception is thrown in the super call - the termination is already handled
              Context childCtx = new ContextImpl();
              for (Enumeration e = sfContext().keys(); e.hasMoreElements();) {
                  Object key = e.nextElement();
                  Object elem = sfContext.get(key);
                  if ((elem instanceof ComponentDescription) && (((ComponentDescription) elem).getEager())) {
                      childCtx.sfAddAttribute(key, elem);
                      if (action == null) {
                          action = (ComponentDescription) elem;
                      }
                  }
              }

              actionKeys = childCtx.keys();
              actions = childCtx;

          } catch (Exception sfex) {
              new TerminatorThread(this, sfex, null).quietly().start();
              throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfex);
          }
      }
    }

    /**
     * Registers an EventSink for forwarding of events.
     *
     * @param sink the EventSink
     * @see EventRegistration
     */
    public synchronized void register(EventSink sink) {
        registrar.register(sink);
    }

    /**
     * Deregistera an EventSink for forwarding of events.
     *
     * @param sink the EventSink
     * @see EventRegistration
     */
    synchronized public void deregister(EventSink sink) {
        registrar.deregister(sink);    }

    /**
     * Handles the event locally then forward to all registered EventSinks.
     *
     * @param event java.lang.Object
     * @see EventSink
     */
    public synchronized void event(Object event) {
        handleEvent(event);
        sendEvent(event);
    }

    /**
     * Default implmentation of the event Handler hook to be overridden in
     * sub-classes. The default implementation does nothing.
     *
     * @param event The event
     */
    protected void handleEvent(Object event) {
        if (sfLog().isDebugEnabled()){
          sfLog().debug(sfCompleteNameSafe().toString() + " saw " + event);
        }
    }

    /**
     * Default implementation of the EventBus sendEvent method to forward all
     * events to registered EventSinks. Errors are ignored.
     *
     * @param event the event to send
     */
    public synchronized void sendEvent(Object event) {
        registrar.sendEvent(event);
    }

    /**
     * Registers components referenced in the SendTo sub-component registers
     * itself with components referenced in the RegisterWith sub-component.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        /* find local registrations and register them */
        ComponentDescription sends = (ComponentDescription) sfResolve(sendRef,false);
        if (sends != null) {
            Context scxt = sends.sfContext();
            for (Enumeration e = scxt.keys(); e.hasMoreElements();) {
                Object k = e.nextElement();
                Reference l = (Reference) scxt.get(k);
                EventSink s = (EventSink) sfResolve(l);
                registrar.register(s);
            }
        }

        /* find own registrations, and register remotely */
        ComponentDescription regs = (ComponentDescription)  sfResolve(receiveRef, false);
        if (regs!=null) {
            Context rcxt = regs.sfContext();

            for (Enumeration keys = rcxt.keys(); keys.hasMoreElements();) {
                Object key = keys.nextElement();
                Reference component = (Reference) rcxt.get(key);
                EventRegistration event = (EventRegistration) sfResolve(component);
                registrar.registerToReceiveFrom(event);
                event.register(this);
            }
        }

        if (oldNotation) {
            actions = ((ComponentDescription)sfResolve(actionsRef,true)).sfContext();
            actionKeys = actions.keys();
            sfLog().warn(" 'actions' workflow notation is deprecated");
        }

        action = (ComponentDescription)sfResolve(actionRef, false);
        name = sfCompleteNameSafe();
    }

    /**
     * Handle notifications of termination
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        boolean terminate;
        if (isWorkflowTerminating()) {
            try {
                //let subclasses decide what to do here
                terminate = onWorkflowTerminating(status, comp);
            } catch (Exception e) {
                //but if that fails, we terminate
                sfLog().error("Exception ", e);
                terminate = true;
            }
        } else {
            //check to see what the subclass wants
            try {
                if (sfContainsChild(comp)) {
                    terminate = onChildTerminated(status, comp);
                } else {
                    terminate = onNonChildTerminated(status, comp);
                }
            } catch (Exception e) {
                sfLog().error("Exception ",e);
                terminate =true;
            }
        }
        if (terminate) {
            new ComponentHelper(this).sfSelfDetachAndOrTerminate(status);
        }
    }

    /**
     * This is an override point to handle full component terminations.
     * It is only called during component termination, i.e. when {@link #isWorkflowTerminating()} is
     * true.
     * <p>
     * Normally this should return true. If it returns false, then the termination process may
     * not go through cleanly.
     * </p>
     * @param status exit record of the component
     * @param comp child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     * @throws SmartFrogRuntimeException for runtime exceptions
     * @throws RemoteException for network problems
     */

    protected boolean onWorkflowTerminating(TerminationRecord status, Prim comp)
            throws SmartFrogRuntimeException, RemoteException {
        return true;
    }

    /**
    * This is an override point; it is where subclasses get to change their workflow
    * depending on what happens underneath.
    * It is only called outside of component termination, i.e. when {@link #isWorkflowTerminating()} is
    * false, and when the comp parameter is a child, that is {@link #sfContainsChild(Liveness)} holds.
    * If the the method returns true, we terminate the component.
    * <p>
    * Always return false if you start new components from this method!
    * </p>
    * @param status exit record of the component
    * @param comp child component that is terminating
    * @return true if the termination event is to be forwarded up the chain.
    * @throws SmartFrogRuntimeException for runtime exceptions
    * @throws RemoteException for network problems
    */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp)
            throws SmartFrogRuntimeException, RemoteException {
        return true;
    }

    /**
     * This is an override point; it is where subclasses get to change their workflow
     * depending on what happens underneath.
     * It is only called outside of component termination, i.e. when {@link #isWorkflowTerminating()} is
     * false, and when {@link #sfContainsChild(Liveness)} is false.
     * It is not normally overridden, but is there to provide complete coverage.
     * @param status exit record of the component
     * @param comp   non-child component that is terminating
     * @return true if the component is to be terminated
     * @throws SmartFrogRuntimeException for runtime exceptions
     * @throws RemoteException for network problems
     */
    protected boolean onNonChildTerminated(TerminationRecord status, Prim comp)
            throws SmartFrogRuntimeException, RemoteException {
        return false;
    }

    /**
    * Deregisters from all current registrations.
    *
    * @param status Termination  Record
    */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        /* unregister from all remote registrations */
        registrar.deregisterFromReceivingAll();
        super.sfTerminateWith(status);
    }

    /**
     * force a check for the action in, though the schema should have caught it
     * @throws SmartFrogResolutionException if the action is not defined
     * @throws RemoteException for network problems
     */
    protected void checkActionDefined() throws SmartFrogResolutionException,
        RemoteException {

        sfResolve(actionRef, true);
    }

    /**
     * A synchronized check for termination; the termLock is used for the lock.
     * @return true iff the workflow component is terminating or is already terminated
     */
    protected boolean isWorkflowTerminating() {
        synchronized (termLock) {
            if (sfIsTerminating || sfIsTerminated) {
                return true;
            }
        }
        return false;
    }


    /**
     * Get the name; valid after {@link #sfDeploy()}
     * @return the reference to this component
     */
    public Reference getName() {
        return name;
    }

    /**
     * Get the component descriptions of all actions
     * @return all actions
     */
    public Context getActions() {
        return actions;
    }

    /**
     * Create the children synchronously.
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems
     */
    protected void synchCreateChildren() throws RemoteException, SmartFrogException {
        actionKeys = getActions().keys();
        try {
            while (actionKeys.hasMoreElements()) {
                Object key = actionKeys.nextElement();
                ComponentDescription act = (ComponentDescription) actions.get(key);
                sfDeployComponentDescription(key, this, act, null);
                if (sfLog().isDebugEnabled()) sfLog().debug("Creating "+key);
            }
        } catch (NoSuchElementException ignored){
           throw new SmartFrogRuntimeException (ERROR_NO_CHILDREN_TO_DEPLOY,this);
        }

        //Actions are now children of this component, they are deployed and
        //started
        for (Prim child:sfChildList()) {
            child.sfDeploy();
        }
        for (Prim child:sfChildList()) {
            child.sfStart();
        }
    }

    /**
     * Helper method to deploy and start any component of a given name. Its template is replaced in the graph
     * by the running component
     *
     * @param childname  attribute to look up
     * @param required flag to indicate the component is required
     * @return the component or null if there was no attribute and required was false.
     * @throws SmartFrogResolutionException unable to resolve the child and required==true
     * @throws RemoteException network problems
     * @throws SmartFrogDeploymentException unable to deploy the child
     */
    protected Prim deployChildCD(String childname, boolean required)
            throws SmartFrogResolutionException, RemoteException, SmartFrogDeploymentException {
        ComponentDescription cd = null;
        cd = sfResolve(childname, cd, required);
        if (cd != null) {
            return sfCreateNewChild(childname, cd, null);
        } else {
            return null;
        }
    }

    /**
     * Deploy but do not start a component. If something went wrong with the {@link Prim#sfDeploy()},
     * we start a termination of the errant component and raise a lifecycle exception.
     * @param childname the name of the component
     * @param description the component description to use (a copy is made before deployment)
     * @return the new child
     * @throws SmartFrogDeploymentException if it could not deploy
     * @throws SmartFrogLifecycleException if something got thrown during deployment
     */
    protected Prim deployComponentDescription(String childname, ComponentDescription description)
            throws SmartFrogDeploymentException, SmartFrogLifecycleException {
        Prim child = sfDeployComponentDescription(childname, this,
                (ComponentDescription) description.copy(), new ContextImpl());
        // it is now a child, so need to guard against double calling of lifecycle...
        try {
            child.sfDeploy();
        } catch (Throwable thrown) {
            //forget about the finally component as we did not deploy properly.
            ComponentHelper helper = new ComponentHelper(child);
            helper.sfSelfDetachAndOrTerminate(TerminationRecord.ABNORMAL,
                    "failed to create "+ childname, null, thrown);
            throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thrown);
        }
        return child;
    }
}
