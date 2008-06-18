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
import org.smartfrog.avalanche.server.engines.sf.BootStrap;
import org.smartfrog.avalanche.server.engines.HostIgnitionException;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.core.host.ArgumentType;
import org.smartfrog.avalanche.core.host.AccessModeType;
import org.smartfrog.avalanche.core.host.DataTransferModeType;
import org.smartfrog.avalanche.core.module.PlatformSelectorType;
import org.smartfrog.avalanche.shared.handlers.XMPPPacketHandler;
import org.jivesoftware.smack.XMPPException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.regex.Pattern;

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

    /**
     * Using security?
     */
    private String strSecurityOn;

    private ServerSetup avlServer = new ServerSetup();

    private BootStrap bootStrap;

    public AvalancheServerImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        sfLog().info("deploying");

        // resolve the attributes
        strAvalancheHome = (String) sfResolve(ATTR_AVALANCHE_HOME, true);
        strXmppServer = (String) sfResolve(ATTR_XMPP_SERVER, true);
        iXmppPort = (Integer) sfResolve(ATTR_XMPP_PORT, true);
        bUseSSLForXmpp = (Boolean) sfResolve(ATTR_USE_SSL_FOR_XMPP, true);
        strXmppAdminUsername = (String) sfResolve(ATTR_XMPP_ADMIN_USERNAME, true);
        strXmppAdminPassword = (String) sfResolve(ATTR_XMPP_ADMIN_PASSWORD, true);
        strSecurityOn = (String) sfResolve(ATTR_SECURITY_ON, true);

        avlServer.getFactory().setAttribute(AvalancheFactory.SECURITY_ON, strSecurityOn);
        avlServer.getFactory().setAttribute(AvalancheFactory.XMPP_SERVER_NAME, strXmppServer);

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

        bootStrap = new BootStrap(avlServer.getFactory());

        sfLog().info("deployed successfully");
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

    public void igniteHosts(String[] inHosts) throws RemoteException, SmartFrogException {
        try {
            bootStrap.ignite(inHosts);
        } catch (HostIgnitionException e) {
            sfLog().error("Error while igniting hosts", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void addAccessMode(String inName, String inType, String inUser, String inPassword, boolean inIsDefault) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inName);
            if (ht != null) {
                // add the access mode
                HostType.AccessModes am = ht.getAccessModes();
                AccessModeType amt = am.addNewMode();
                amt.setType(inType);
                amt.setUser(inUser);
                amt.setPassword(inPassword);
                amt.setIsDefault(inIsDefault);
            }

            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void addTransferMode(String inName, String inType, String inUser, String inPassword, boolean inIsDefault) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inName);
            if (ht != null) {
                // add the transfer mode
                HostType.TransferModes tm = ht.getTransferModes();
                DataTransferModeType tmt = tm.addNewMode();
                tmt.setType(inType);
                tmt.setUser(inUser);
                tmt.setPassword(inPassword);
                tmt.setIsDefault(inIsDefault);
            }

            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void clearAccessModes(String inName) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inName);
            if (ht != null) {
                // clear the old list
                HostType.AccessModes am = ht.getAccessModes();
                while (am.getModeArray().length > 0)
                    am.removeMode(0);
            }

            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void clearTransferModes(String inName) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inName);
            if (ht != null) {
                // clear the old list
                HostType.TransferModes tm = ht.getTransferModes();
                while (tm.getModeArray().length > 0)
                    tm.removeMode(0);
            }

            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void addArgument(String inHostName, String inArgName, String inArgValue) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inHostName);
            if (ht != null) {
                // add the new argument
                ArgumentType at = ht.getArguments();
                ArgumentType.Argument newArg = at.addNewArgument();
                newArg.setName(inArgName);
                newArg.setValue(inArgValue);
            }

            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void updateHost(String inName, String inArchitecture, String inPlatform, String inOS) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inName);
            if (ht == null) {
                // host not existing, create a new one
                try {
                    ht = hm.newHost(inName);
                } catch (Exception e) {
                    sfLog().error("Error while creating host type for: " + inName, e);
                    throw SmartFrogException.forward(e);
                }

                // platform settings
                PlatformSelectorType pst = ht.addNewPlatformSelector();
                pst.setPlatform(inPlatform);
                pst.setOs(inOS);
                pst.setArch(inArchitecture);

                // create the lists
                ht.addNewAccessModes();
                ht.addNewTransferModes();
                ht.addNewArguments();
            } else {
                // host existing, update

                // platform settings
                PlatformSelectorType pst = ht.getPlatformSelector();
                pst.setPlatform(inPlatform);
                pst.setOs(inOS);
                pst.setArch(inArchitecture);
            }

            // store the changes
            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void clearArguments(String inName) throws RemoteException, SmartFrogException {
        // get the host manager
        try {
            HostManager hm = getAvalancheFactory().getHostManager();

            HostType ht = hm.getHost(inName);
            if (ht != null) {
                // clear the old list
                ArgumentType at = ht.getArguments();
                while (at.getArgumentArray().length > 0)
                    at.removeArgument(0);
            }

            hm.setHost(ht);
        } catch (ModuleCreationException e) {
            sfLog().error("Error while trying to get the host manager", e);
            throw SmartFrogException.forward(e);
        } catch (DatabaseAccessException e) {
            sfLog().error("Error while accessing the database", e);
            throw SmartFrogException.forward(e);
        }
    }

    public void addXMPPHandler(XMPPPacketHandler inHandler) throws RemoteException, SmartFrogException {
        try {
            avlServer.addXmppPacketHandler(inHandler);
        } catch (XMPPException e) {
            sfLog().error("Error when trying to add a xmpp packet handler", e);
            throw SmartFrogException.forward(e);
        }
    }
}
