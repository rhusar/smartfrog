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

package org.smartfrog.sfcore.security;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;


/**
 * The SecureRemoteObject provides an export static method  equivalent to the
 * one in UnicastRemoteObject but, if SF security is active, it will
 * automatically ensure that remote access to the object exported will use
 * JSSE (i.e., SSL), and a system wide policy will be used for authentication
 * and authorization.
 *
 */
public class SecureRemoteObject {
    /**
     * A factory to create the JSSE connector in the server. If it is null, it
     * means that no security is required.
     */
    static private RMIServerSocketFactory ssf = null;

    /**
     * A factory to create the JSSE connector in the client. It is always empty
     * in the server and it should be constructed by the client while
     * unmarshalling.
     */
    static private RMIClientSocketFactory csf = null;

    /** A flag that indicates whether we have initialized the JSSE connector. */
    static private boolean alreadyInit = false;

    /**
     * Constructs SecureRemoteObject. Nobody should create this type of objects.
     */
    private SecureRemoteObject() {
    }

    /**
     * Gets whether initialization has already been done.
     *
     * @return Whether initialization has already been done.
     */
    static private boolean isAlreadyInit() {
        return alreadyInit;
    }

    /**
     * Sets a flag describing whether initialization has been done.
     *
     * @param flag A new value describing whether initialization has been done.
     */
    static private void setAlreadyInit(boolean flag) {
        alreadyInit = flag;
    }

    /**
     * Initializes the JSSE connector in the server.
     *
     * @throws SFGeneralSecurityException Error while initializing the
     *            connector.
     */
    static synchronized private void initialize()
        throws SFGeneralSecurityException {
        try {
            if (isAlreadyInit()) {
                return;
            }

            if (SFSecurity.isSecurityOn()) {
                SFSecurityEnvironment sfSecEnv;
                sfSecEnv = SFSecurity.getSecurityEnvironment();
                ssf = sfSecEnv.getRMIServerSocketFactory();

                // Empty client socket factory.
                csf = sfSecEnv.getEmptyRMIClientSocketFactory();
            }
        } catch (Exception e) {
            throw new SFGeneralSecurityException(e.getMessage());
        } finally {
            // Don't try again.
            setAlreadyInit(true);
        }
    }

    /**
     * Export the remote object to make it available to receive incoming calls,
     * using the particular supplied port. It adds a jsse connector if
     * security is enabled.
     *
     * @param obj the remote object to be exported
     * @param port the port to export the object on
     *
     * @return remote object stub
     *
     * @throws RemoteException if export fails
     * @throws SFGeneralSecurityException if we cannot initalize security
     *            mechanisms
     */
    public static Remote exportObject(Remote obj, int port)
        throws RemoteException, SFGeneralSecurityException {
        initialize();

        if (SFSecurity.isSecurityOn()) {
            return UnicastRemoteObject.exportObject(obj, port, csf, ssf);
        } else {
            return UnicastRemoteObject.exportObject(obj, port);
        }
    }

    /**
     * Exports the remote object to make it available to receive incoming calls,
     * using an anonymous port.It adds a jsse connector if security is
     * enabled.
     *
     * @param obj the remote object to be exported
     *
     * @return remote object stub
     *
     * @throws RemoteException if export fails
     * @throws SFGeneralSecurityException if we cannot initalize security
     *            mechanisms
     */
    public static RemoteStub exportObject(Remote obj)
        throws RemoteException, SFGeneralSecurityException {
        return (RemoteStub) exportObject(obj, 0);
    }

    /**
     * Removes the remote object, obj, from the RMI runtime. If successful, the
     * object can no longer accept incoming RMI calls. If the force parameter
     * is true, the object is forcibly unexported even if there are pending
     * calls to the remote object or the remote object still has calls in
     * progress.  If the force parameter is false, the object is only
     * unexported if there are no pending or in progress calls to the object.
     *
     * @param obj the remote object to be unexported
     * @param force if true, unexports the object even if there are pending or
     *        in-progress calls; if false, only unexports the object if there
     *        are no pending or in-progress calls
     *
     * @return true if operation is successful, false otherwise
     *
     * @throws java.rmi.NoSuchObjectException if the remote object is not
     *            currently exported
     */
    public static boolean unexportObject(Remote obj, boolean force)
        throws java.rmi.NoSuchObjectException {
        return UnicastRemoteObject.unexportObject(obj, force);
    }
}
