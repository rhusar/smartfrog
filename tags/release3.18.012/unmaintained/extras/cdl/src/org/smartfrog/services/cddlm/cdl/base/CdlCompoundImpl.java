/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.cdl.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * Base component for CDL components. It is a compound, obviously. Lifecycle events
 * can be relayed to a lifecycle listener
 * created 01-Feb-2006 11:19:17
 */

public class CdlCompoundImpl extends CompoundImpl
        implements CdlCompound, LifecycleListener {

    private URI jobURI;
    private static final Log log = LogFactory.getLog(CdlCompoundImpl.class);
    private LifecycleListener listener;

    public CdlCompoundImpl() throws RemoteException {
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure deploying compound or
     *                                  sub-component
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        Prim listenerPrim=sfResolve(ATTR_LISTENER,(Prim)null,false);
        if(listenerPrim!=null) {
            listener=(LifecycleListener) listenerPrim;
        }
        enterStateNotifying(LifecycleStateEnum.initialized, null);
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failed to start compound
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        enterStateNotifying(LifecycleStateEnum.running, null);
    }


    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            enterTerminatedStateNotifying(status);
        } catch (RemoteException e) {
            log.error(e);
        }
    }


    /**
     * Resolve a reference
     * @param name qname of the reference
     * @param mandatory flag to indicate a mandatory reference
     * @return whatever resolved
     * @throws SmartFrogResolutionException for resolution problems
     * @throws RemoteException for network problems
     */
    public Object resolve(QName name, boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        Reference r = new Reference(name);
        return sfResolve(r, mandatory);
    }

    /**
     * Resolve the sfText node under the named reference
     * @param name qname of the reference
     * @param mandatory flag to indicate a mandatory reference
     * @return whatever resolved
     * @throws SmartFrogResolutionException for resolution problems
     * @throws RemoteException for network problems
     */
    public String resolveText(QName name, boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        Reference r = new Reference(name);
        r.addElement(ReferencePart.attrib(ATTR_TEXT));
        return (String) sfResolve(r, mandatory);
    }

    /**
     * bind to something listening for lifecycle events. No events are raised at this point.
     *
     * @param uri job ID
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void subscribe(String uri, LifecycleListener target) throws SmartFrogException,
            RemoteException {
        try {
            jobURI = new URI(uri);
        } catch (URISyntaxException e) {
            throw SmartFrogException.forward(e);
        }
        listener = target;
        sfReplaceAttribute(ATTR_JOBURI, uri);
    }

    /**
     * unsubscribe. This is idempotent
     * @param subscriber
     * @throws SmartFrogException
     * @throws RemoteException
     * @return true if the listener was unsubscribed
     */
    public boolean unsubscribe(LifecycleListener subscriber)
            throws SmartFrogException, RemoteException {
        boolean match=listener==subscriber;
        if(match) {
            listener=null;
        }
        return match;
    }


    public LifecycleListener getListener() {
        return listener;
    }

    /**
     * enter a state, send notification if this is different from a state we
     * were in before This method is synchronous, you cannot enter a state till
     * the last one was processed.
     * <p/>
     * If you try and enter the current state, then nothing happens
     *
     * @param newState new state to enter
     * @param info     string to record in the stateInfo field.
     * @throws java.rmi.RemoteException for network problems
     */
    public void enterStateNotifying(LifecycleStateEnum newState, String info) throws RemoteException {
        if (listener != null) {
            listener.enterStateNotifying(newState, info);
        }
    }

    /**
     * terminate, send a message out
     *
     * @param record termination record
     * @throws java.rmi.RemoteException for network problems
     */
    public void enterTerminatedStateNotifying(TerminationRecord record) throws RemoteException {
        if (listener != null) {
            listener.enterTerminatedStateNotifying(record);
        }
    }

}
