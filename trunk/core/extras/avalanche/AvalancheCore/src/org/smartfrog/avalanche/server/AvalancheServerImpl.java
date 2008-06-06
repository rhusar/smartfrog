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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.HashMap;

public class AvalancheServerImpl extends PrimImpl implements AvalancheServer {
    /**
     * Path to the home directory of the avalanche server.
     */
    private String strAvalancheHome;

    /**
     * IP address of the xmpp server.
     */
    private String strXmppServer;

    /**
     * Port to use on the xmpp server.
     */
    private int iXmppPort;

    /**
     * Using SSL for XMPP?
     */
    private boolean bUseSSLForXmpp;

    /**
     * Username for the XMPP admin account.
     */
    private String strXmppAdminUsername;

    /**
     * Password for the XMPP admin account.
     */
    private String strXmppAdminPassword;

    private ServerSetup avlServer = new ServerSetup();

    public AvalancheServerImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        // resolve the attributes
        strAvalancheHome = (String) sfResolve(ATTR_AVALANCHE_HOME, true);
        strXmppServer = (String) sfResolve(ATTR_XMPP_SERVER, true);
        iXmppPort = (Integer) sfResolve(ATTR_XMPP_PORT, true);
        bUseSSLForXmpp = (Boolean) sfResolve(ATTR_USE_SSL_FOR_XMPP, true);
        strXmppAdminUsername = (String) sfResolve(ATTR_XMPP_ADMIN_USERNAME, true);
        strXmppAdminPassword = (String) sfResolve(ATTR_XMPP_ADMIN_PASSWORD, true);
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // set up the avalanche server
        avlServer.setAvalancheHome(strAvalancheHome);
        avlServer.setUseSSLForXMPP(bUseSSLForXmpp);
        avlServer.setXmppServer(strXmppServer);
        avlServer.setXmppServerPort(iXmppPort);
        avlServer.setXmppServerAdminUser(strXmppAdminUsername);
        avlServer.setXmppServerAdminPassword(strXmppAdminPassword);

        // start the avalancher server
        try {
            avlServer.startup();
        } catch (Exception e) {
            sfLog().error("Error while starting up avalanche server", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void sfTerminate(TerminationRecord status) {
        super.sfTerminate(status);

        try {
            avlServer.shutdown();
        } catch (Exception e) {
            sfLog().error("Error while stopping avalanche server", e);
        }
    }

    public AvalancheFactory getAvalancheFactory() throws RemoteException, SmartFrogException {
        return avlServer.getFactory();
    }

    public void sendVMCommand(String inTargetMachine, String inVMPath, String inCmd) throws RemoteException, SmartFrogException {
        ServerSetup.sendVMCommand(inTargetMachine, inVMPath, inCmd);
    }

    public void sendVMCommand(String inTargetMachine, String inVMPath, String inCmd, HashMap<String, String> inAdditionalProperties) throws RemoteException, SmartFrogException {
        ServerSetup.sendVMCommand(inTargetMachine, inVMPath, inCmd, inAdditionalProperties);
    }
}
