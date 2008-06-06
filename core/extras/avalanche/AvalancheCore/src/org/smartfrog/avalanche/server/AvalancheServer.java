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

    // xmpp credentials
    public static String ATTR_XMPP_ADMIN_USERNAME = "XmppAdminUsername";
    public static String ATTR_XMPP_ADMIN_PASSWORD = "XmppAdminPassword";

    /**
     * Gets the avalanche factory.
     * @return The avalanche factory.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public AvalancheFactory getAvalancheFactory()
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
}
