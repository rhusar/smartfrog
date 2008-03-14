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

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;
import org.smartfrog.sfcore.reference.RemoteReferenceResolverHelper;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;


/**
 * Defines the basic interface for all deployed components. A deployed
 * component knows how to react to termination, liveness, etc.
 *
 */
public interface Prim extends Update, Liveness, RemoteReferenceResolver, RemoteReferenceResolverHelper, RemoteTags, RemoteTagsComponent, Diagnostics, Remote {
    /**
     * Add an attribute to the component's context. Values should be
     * marshallable types if they are to be referenced remotely at run-time.
     * If an attribute with this name already exists it is <em>not</em>
     * replaced.
     *
     * @param name name of attribute
     * @param value object to be added in context
     *
     * @return value if successfull, null otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public Object sfAddAttribute(Object name, Object value)
        throws SmartFrogRuntimeException, RemoteException;

    /**
     * Remove named attribute from component context. Non present attribute
     * names are ignored.
     *
     * @param name name of attribute to be removed
     *
     * @return the removed value if successfull, null otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     * @throws SmartFrogRuntimeException when name is null
     */
    public Object sfRemoveAttribute(Object name)
        throws SmartFrogRuntimeException, RemoteException;


    /**
     * Returns the attribute key for a given value.
     *
     * @param value value to look up the key for
     *
     * @return key for given value or null if not found
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public Object sfAttributeKeyFor(Object value) throws RemoteException;

    /**
     * Returns true if the context contains value.
     *
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfContainsValue(Object value) throws RemoteException;


    /**
     * Returns true if the context contains attribute.
     * @param attribute to check
     *
     * @return true if context contains key, false otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfContainsAttribute(Object attribute) throws RemoteException;



    /**
     * Replace named attribute in component context. If attribute is not
     * present it is added to the context.
     *
     * @param name of attribute to replace
     * @param value attribute value to replace or add
     *
     * @return the old value if present, null otherwise
     *
     * @throws RemoteException In case of Remote/nework error
     * @throws SmartFrogRuntimeException when name or value are null
     */
    public Object sfReplaceAttribute(Object name, Object value)
        throws SmartFrogRuntimeException, RemoteException;


    /**
     * Returns an ordered iterator over the attribute names in the context.
     * The remove operation of this Iterator won't affect
     * the contents of this component
     *
     * @return iterator
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public  Iterator sfAttributes() throws RemoteException;

    /**
     * Returns an ordered iterator over the values in the context.
     * The remove operation of this Iterator won't affect
     * the contents of this component
     *
     * @return iterator
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public  Iterator sfValues() throws RemoteException;


    /**
     * Returns the context of this component.
     *
     * @return component context
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public Context sfContext() throws RemoteException;


    /**
     * Returns a reference to the component from the root of the containment
     * tree. If the given component is an attribute of this component, the
     * complete name of this compoonent is returned.
     *
     * @return reference to this object
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public Reference sfCompleteName() throws RemoteException;

    /**
     * Gets the parent of the component.
     *
     * @return a component or null this component is root
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public Prim sfParent() throws RemoteException;

    /**
     * Private method to set up a freshly deployed component. New primitives
     * should only override sfDeploy.
     *
     * @param parent parent of component
     * @param cxt context for component
     *
     * @throws SmartFrogException failed to deploy primitive
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfDeployWith(Prim parent, Context cxt)
        throws SmartFrogException, RemoteException;

    /**
     * Deploy the component. The component can not assume that other parts of
     * the application are already deployed.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException;

    /**
     * Start the component's main processing thread. Implementations should
     * <em>not</em> block in this call, but spawn off another thread.
     *
     * @throws SmartFrogException sfStart failure
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfStart() throws SmartFrogException, RemoteException;

    /**
     * Request component to "dump it's state" to a target object which
     * implements the Dump interface.
     *
     * @param target object to send dumpState to
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfDumpState(Dump target) throws RemoteException;

    /**
     * Request component to terminate with a given termination status. This
     * will cause the component to notify termination to its container.
     * Implementations don't generally implement this. They override the
     * sfTerminateWith hook.
     *
     * @param status termination status
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfTerminate(TerminationRecord status) throws RemoteException;

    /**
     * Request component to detach itself from its parent, and becoming a root
     * component.
     *
     * @throws SmartFrogException failed to detach
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfDetach() throws SmartFrogException, RemoteException;

    /**
     * Request component to detach itself from its container and terminate with
     * a given termination status. This will not notify the container of
     * termination.
     *
     * @param status termination status
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfDetachAndTerminate(TerminationRecord status)
        throws RemoteException;

    /**
     * Notification of other component that it has been terminated. This would
     * normally be called from sub-components which are terminated.
     *
     * @param comp terminated component
     * @param status termination record for component
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp)
        throws RemoteException;

    /**
     * Request this component to terminate quietly without telling anyone else.
     *
     * @param status status indicating termination type
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfTerminateQuietlyWith(TerminationRecord status)
        throws RemoteException;

    /**
     * Request the host on which this component is deployed.
     *
     * @return the host InetAddress
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public InetAddress sfDeployedHost() throws RemoteException;

    /**
     * Request the process in which this component is deployed, the name being
     * that defined in the sfProcessName attribute or the string ROOT if in
     * the root process compound.
     *
     * @return the name of the process
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public String sfDeployedProcessName() throws RemoteException;


    /**
     * Parentage changed in component hierachy.
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfParentageChanged() throws RemoteException;

    /**
     * Returns value of flag indicating if this component has been terminated.
     * @return true if the component is terminating
     * @throws RemoteException In case of Remote/network error
     */
    public boolean sfIsTerminated() throws RemoteException;

    /** Returns value of flag indicating if this component is terminating.
     * @return true if the component is terminating
     * @throws RemoteException In case of Remote/network error
     */
    public boolean sfIsTerminating() throws RemoteException;

    /** Returns value of flag indicating if this component has been deployed.
     * @return true if the component is deployed
     * @throws RemoteException In case of Remote/network error
     */
    public boolean sfIsDeployed() throws RemoteException;

    /** Returns value of flag indicating if this component has been started.
     * @return true if the component is started
     * @throws RemoteException In case of Remote/network error
     */
    public boolean sfIsStarted() throws RemoteException;

    /**
     * Validate all ASSERTs in the context of the Prim, returning true if OK, false if not.
     * @return true if the assertions are valid
     * @throws RemoteException In case of Remote/network error
     */
     public boolean sfValid() throws RemoteException;
}
