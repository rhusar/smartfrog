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

package org.smartfrog.sfcore.processcompound;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;


/**
 * Defines the process deployment interface. A process compound can deploy
 * component descriptions. It also manages other processes which contain
 * process compounds.
 *
 */
public interface ProcessCompound extends Compound {
    /**
     * Gets the process name for this process compound.
     *
     * @return process name for this compound
     * @throws RemoteException if there is any network or remote error
     *
     */
    public String sfProcessName() throws RemoteException;

    /**
     * Tests whether the Process Compound is the root process compound or not.
     *
     * @return true if it is the root
     * @throws RemoteException if there is any network or remote error
     */
    public boolean sfIsRoot() throws RemoteException;

    /**
     * Finds a process compound given a name. Creates the process if it does
     * not exist.
     *
     * @param name process name to use
     *
     * @return processcompound under name
     *
     * @throws Exception failed to deploy or locate process
     * @throws RemoteException if there is any network or remote error
     */
    public ProcessCompound sfResolveProcess(Object name)
        throws Exception, RemoteException;

    /**
     * Registers a deployed component inhibiting the generation of a new name
     * for the component. This is used, for example, for child process
     * compounds which HAVE to be named right. Exception might indicate that
     * the component cannot be registered since name is already in use.
     * Registering a component will cause termination to be called when the
     * processcompound is terminated (i.e. process is requested to exit. If
     * name is null, a name is made up for the component
     *
     * @param name name for component
     * @param comp component to register
     *
     * @return name of registered component
     *
     * @throws SmartFrogException if name already in use
     * @throws RemoteException if there is any network or remote error
     */
    public Object sfRegister(Object name, Prim comp)
        throws SmartFrogException, RemoteException;

    /**
     * Tries to find an attribute in the local context. If the attribute is not
     * found the thread will wait for a notification from sfAddAttribute or
     * until given timeout expires. Generally used to wait for a new process
     * compound to appear.
     *
     * @param name name of attribute to wait for
     * @param timeout max time to wait in millis
     *
     * @return The object found
     *
     * @throws Exception attribute not found after timeout
     * @throws RemoteException if there is any network or remote error
     */
    public Object sfResolveHereOrWait(Object name, long timeout)
        throws Exception, RemoteException;

    /**
     * Sets whether or not the ProcessCompound should terminate the JVM on
     * exit. This is, by default, set to true. It is used if the
     * ProcessCompound is created and managed by other code.
     *
     * @param exit whether or not to exit (true = exit)
     * @throws RemoteException if there is any network or remote error
     */
    public void systemExitOnTermination(boolean exit) throws RemoteException;
}
