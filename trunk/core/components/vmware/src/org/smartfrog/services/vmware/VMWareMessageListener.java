/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.services.xmpp.LocalXmppPacketHandler;
import org.smartfrog.services.xmpp.XmppListenerImpl;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;

import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

public class VMWareMessageListener extends PrimImpl implements LocalXmppPacketHandler, Prim {

    /**
     * Reference to the VMWare Server manager module.
     */
    private VMWareServerManager refServerManager;

    /**
     * Reference to the XMPP listener.
     */
    private XmppListenerImpl refXmppListener;

    /**
     * Constructor.
     * @throws RemoteException
     */
    public VMWareMessageListener() throws RemoteException {

    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        sfLog().info("VMWare Message Listener deployed.");
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
     public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // get the reference to the vmware server manager
        refServerManager = (VMWareServerManager)sfResolve("vmServerManager", refServerManager, true);
        if (refServerManager == null)
        {
            throw new SmartFrogDeploymentException("sfStart failed: VMWareServerManager reference not found.");
        }

        // get the reference to the xmpp message listener
        refXmppListener = (XmppListenerImpl)sfResolve("listener", refXmppListener, true);
        if (refXmppListener == null)
        {
            throw new SmartFrogDeploymentException("sfStart failed: listener reference not found.");
        }

        // register this listener to the xmpp message listener
        refXmppListener.registerPacketHandler(this);

        sfLog().info("VMWare Message Listener started.");
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        // unregister this listener from the xmpp message listener
        refXmppListener.unregisterPacketHandler(this);
    }

    /**
     * get a message filter object
     * @return
     */
    public PacketFilter getFilter() {
        return new VMWareMessageFilter();
    }

    public void processPacket(Packet packet) {
        sfLog().info("VMWare Message Listener: Received packet: " + packet);
        // get the extension
        XMPPEventExtension ext = (XMPPEventExtension) packet.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
        if (ext != null)
        {
            // use the property bag
            String strCommand = ext.getPropertyBag().get("vmcmd");
            String strPath = ext.getPropertyBag().get("vmpath");

            if (strCommand != null)
            {
                // extension for the response message
                XMPPEventExtension newExt = new XMPPEventExtension();

                newExt.setMessageType(MonitoringConstants.VM_MESSAGE);
                newExt.setTimestamp(String.format("%d", Calendar.getInstance().getTimeInMillis()));
                try {
                    newExt.setHost(InetAddress.getLocalHost().getHostName());
                } catch (UnknownHostException e) {
                    newExt.setHost(packet.getTo());
                }

                // fill the response bag
                newExt.getPropertyBag().put("vmcmd", strCommand);
                newExt.getPropertyBag().put("vmpath", strPath);

                try {
                    if (strCommand.equals("start"))
                    {
                        // attempt to start the machine
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.startVM(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("stop"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.stopVM(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("suspend"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.suspendVM(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("reset"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.resetVM(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("register"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.registerVM(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("unregister"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.unregisterVM(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("list"))
                    {
                        newExt.getPropertyBag().put("vmresponse", refServerManager.getControlledMachines());
                    }
//      VMFox code
//                    else if (strCommand.equals("toolsstate"))
//                    {
//                        int iState = refServerManager.getToolsState(strPath);
//                        switch (iState)
//                        {
//                            case VMWareImageModule.TOOLS_STATUS_NOT_INSTALLED:
//                                newExt.getPropertyBag().put("vmresponse", "Tools not installed.");
//                                break;
//                            case VMWareImageModule.TOOLS_STATUS_RUNNING:
//                                newExt.getPropertyBag().put("vmresponse", "Tools running.");
//                                break;
//                            case VMWareImageModule.TOOLS_STATUS_UNKNOWN:
//                                newExt.getPropertyBag().put("vmresponse", "Tools state unknown.");
//                                break;
//                            default:
//                                newExt.getPropertyBag().put("vmresponse", "failure");
//                                break;
//                        }
//                    }
                    else if (strCommand.equals("powerstate"))
                    {
                        int iState = refServerManager.getPowerState(strPath);
                        switch (iState)
                        {
                            case VMWareImageModule.POWER_STATUS_BLOCKED_ON_MSG:
                                newExt.getPropertyBag().put("vmresponse", "Blocked on message.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERED_OFF:
                                newExt.getPropertyBag().put("vmresponse", "Powered off.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERED_ON:
                                newExt.getPropertyBag().put("vmresponse", "Powered on.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERING_OFF:
                                newExt.getPropertyBag().put("vmresponse", "Powering off.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERING_ON:
                                newExt.getPropertyBag().put("vmresponse", "Powering on.");
                                break;
                            case VMWareImageModule.POWER_STATUS_RESETTING:
                                newExt.getPropertyBag().put("vmresponse", "Resetting.");
                                break;
                            case VMWareImageModule.POWER_STATUS_SUSPENDED:
                                newExt.getPropertyBag().put("vmresponse", "Suspended.");
                                break;
                            case VMWareImageModule.POWER_STATUS_SUSPENDING:
                                newExt.getPropertyBag().put("vmresponse", "Suspending.");
                                break;
                            case VMWareImageModule.POWER_STATUS_TOOLS_RUNNING:
                                newExt.getPropertyBag().put("vmresponse", "Tools running.");
                                break;
                            default:
                                newExt.getPropertyBag().put("vmresponse", "failure");
                                break;
                        }
                    }
                    else if (strCommand.equals("stopvmwareservice"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.shutdownVMWareServerService() ? "success" : "failure"));
                    }
                    else if (strCommand.equals("startvmwareservice"))
                    {
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.startVMWareServerService() ? "success" : "failure"));
                    }
                    else if (strCommand.equals("create"))
                    {
                        // create a vmware from a master model
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.createCopyOfMaster(ext.getPropertyBag().get("vmmasterpath"), strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("delete")) 
                    {
                        // delete a vmware
                        newExt.getPropertyBag().put("vmresponse", (refServerManager.deleteCopy(strPath) ? "success" : "failure"));
                    }
                    else if (strCommand.equals("getmasters"))
                    {
                        // list the master copies
                        newExt.getPropertyBag().put("vmresponse", refServerManager.getMasterImages());
                    }
                } catch (RemoteException e) {
                    newExt.getPropertyBag().put("vmresponse", "failure");
                }

                // send the message
                refXmppListener.sendMessage(packet.getFrom(), "", "AE", newExt);
            }
        }
    }
}
