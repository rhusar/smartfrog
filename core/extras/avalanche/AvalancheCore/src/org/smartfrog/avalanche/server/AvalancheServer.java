/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.avalanche.server;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface AvalancheServer extends Remote {
    // path to the home directory of the avalanche server
    public static String ATTR_AVALANCHE_HOME = "AvalancheHome";

    // IP address of the xmpp server
    public static String ATTR_XMPP_SERVER = "XmppServer";

    // port to use on the xmpp server
    public static String ATTR_XMPP_PORT = "XmppPort";

    public static String ATTR_USE_SSL_FOR_XMPP = "UseSSLForXmpp";

    public static String ATTR_SECURITY_ON = "SecurityOn";

    // xmpp credentials
    public static String ATTR_XMPP_ADMIN_USERNAME = "XmppAdminUsername";
    public static String ATTR_XMPP_ADMIN_PASSWORD = "XmppAdminPassword";

    /**
     * Updates a host. That is updating the values if existing or creating a new one.
     * @param inName Hostname or IP address.
     * @param inArchitecture Architecture of the host.
     * @param inPlatform Platform of the host.
     * @param inOS Operating System of the host.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void updateHost(String inName, String inArchitecture, String inPlatform, String inOS)
            throws RemoteException, SmartFrogException;

    /**
     * Clears the list of access modes of a host.
     * @param inName The name of the host.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void clearAccessModes(String inName)
            throws RemoteException, SmartFrogException;

    /**
     * Clears the list of transfer modes of a host.
     * @param inName The name of the host.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void clearTransferModes(String inName)
            throws RemoteException, SmartFrogException;

    /**
     * Clears the list of arguments of a host.
     * @param inName The name of the host.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void clearArguments(String inName)
            throws RemoteException, SmartFrogException;

    /**
     * Adds an access mode to a host profile.
     * @param inName Name of the host.
     * @param inType Access type. (e.g. SSH)
     * @param inUser Username of the access mode.
     * @param inPassword Password of the access mode.
     * @param inIsDefault Is this the default access mode?
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void addAccessMode(String inName, String inType, String inUser, String inPassword, boolean inIsDefault)
            throws RemoteException, SmartFrogException;

    /**
     * Adds a transfer mode to a host profile.
     * @param inName The name of the host.
     * @param inType The transfer mode. (e.g. SCP)
     * @param inUser The username of the transfer mode.
     * @param inPassword The password of the transfer mode.
     * @param inIsDefault Is this the default transfer mode?
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void addTransferMode(String inName, String inType, String inUser, String inPassword, boolean inIsDefault)
            throws RemoteException, SmartFrogException;

    /**
     * Updates an argument of a host profile. Will be created if not existing.
     * @param inHostName The name of the host.
     * @param inArgName The name of the argument.
     * @param inArgValue The value of the argument.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void addArgument(String inHostName, String inArgName, String inArgValue)
            throws RemoteException, SmartFrogException;

    /**
     * Send a vm command to a host.
     * @param inTargetMachine The host of the virtual machine.
     * @param inVMPath The path to the .vmx file.
     * @param inCmd The command to execute.
     */
    public void sendVMCommand(String inTargetMachine, String inVMPath, String inCmd)
            throws RemoteException, SmartFrogException;

    /**
     * Send a vm command to a host.
     * @param inTargetMachine The host of the virtual machine.
     * @param inVMPath The path to the .vmx file.
     * @param inCmd The command to execute.
     * @param inAdditionalProperties Additional attributes required for the command.
     */
    public void sendVMCommand(String inTargetMachine, String inVMPath, String inCmd, HashMap<String, String> inAdditionalProperties)
            throws RemoteException, SmartFrogException;

    /**
     * Ignites the given hosts if they are present in the database.
     * @param inHosts The list of hostnames.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void igniteHosts(String[] inHosts)
            throws RemoteException, SmartFrogException;
}
