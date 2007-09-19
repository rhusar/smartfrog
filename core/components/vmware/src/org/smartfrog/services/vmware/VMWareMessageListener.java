/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */

package org.smartfrog.services.vmware;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.smartfrog.services.xmpp.LocalXmppPacketHandler;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.XmppListener;
import org.smartfrog.services.xmpp.XmppListenerImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Calendar;

/**
 * Component that listens for VMWARE messages
 */
public class VMWareMessageListener extends PrimImpl implements LocalXmppPacketHandler, Prim {

    /**
     * Reference to the VMWare Server manager module.
     */
    private VMWareServerManager manager;

    /**
     * Reference to the XMPP listener.
     */
    private XmppListenerImpl refXmppListener;
    public static final String ATTR_LISTENER = "listener";
    public static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    private static final String VMRESPONSE = "vmresponse";
    private static final String VMPATH = "vmpath";
    public static final String VMMASTERPATH = "vmmasterpath";

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
        manager = (VMWareServerManager)sfResolve("vmServerManager", manager, true);

        // get the reference to the xmpp message listener
        XmppListener xmppListener = (XmppListener) sfResolve(ATTR_LISTENER, refXmppListener, true);
        if(!(xmppListener instanceof XmppListenerImpl)) {
            throw new SmartFrogDeploymentException("The XmppListener referenced by "+ATTR_LISTENER+" must be in the same process");
        }
        refXmppListener = (XmppListenerImpl) xmppListener;


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

    private String  outcome(boolean value) {
        return value? SUCCESS : FAILURE;
    }

    public void processPacket(Packet packet) {
        sfLog().info("VMWare Message Listener: Received packet: " + packet);
        // get the extension
        XMPPEventExtension ext = (XMPPEventExtension) packet.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
        if (ext != null)
        {
            // use the property bag
            String strCommand = ext.getPropertyBag().get("vmcmd");
            String strPath = ext.getPropertyBag().get(VMPATH);

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
                newExt.getPropertyBag().put(VMPATH, strPath);

                try {
                    if (strCommand.equals("start"))
                    {
                        // attempt to start the machine
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.startVM(strPath)));
                    }
                    else if (strCommand.equals("stop"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.stopVM(strPath)));
                    }
                    else if (strCommand.equals("suspend"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.suspendVM(strPath)));
                    }
                    else if (strCommand.equals("reset"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.resetVM(strPath)));
                    }
                    else if (strCommand.equals("register"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.registerVM(strPath)));
                    }
                    else if (strCommand.equals("unregister"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.unregisterVM(strPath)));
                    }
                    else if (strCommand.equals("list"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, manager.getControlledMachines());
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
                        int iState = manager.getPowerState(strPath);
                        switch (iState)
                        {
                            case VMWareImageModule.POWER_STATUS_BLOCKED_ON_MSG:
                                newExt.getPropertyBag().put(VMRESPONSE, "Blocked on message.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERED_OFF:
                                newExt.getPropertyBag().put(VMRESPONSE, "Powered off.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERED_ON:
                                newExt.getPropertyBag().put(VMRESPONSE, "Powered on.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERING_OFF:
                                newExt.getPropertyBag().put(VMRESPONSE, "Powering off.");
                                break;
                            case VMWareImageModule.POWER_STATUS_POWERING_ON:
                                newExt.getPropertyBag().put(VMRESPONSE, "Powering on.");
                                break;
                            case VMWareImageModule.POWER_STATUS_RESETTING:
                                newExt.getPropertyBag().put(VMRESPONSE, "Resetting.");
                                break;
                            case VMWareImageModule.POWER_STATUS_SUSPENDED:
                                newExt.getPropertyBag().put(VMRESPONSE, "Suspended.");
                                break;
                            case VMWareImageModule.POWER_STATUS_SUSPENDING:
                                newExt.getPropertyBag().put(VMRESPONSE, "Suspending.");
                                break;
                            case VMWareImageModule.POWER_STATUS_TOOLS_RUNNING:
                                newExt.getPropertyBag().put(VMRESPONSE, "Tools running.");
                                break;
                            default:
                                newExt.getPropertyBag().put(VMRESPONSE, FAILURE);
                                break;
                        }
                    }
                    else if (strCommand.equals("stopvmwareservice"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.shutdownVMWareServerService()));
                    }
                    else if (strCommand.equals("startvmwareservice"))
                    {
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.startVMWareServerService()));
                    }
                    else if (strCommand.equals("create"))
                    {
                        // create a vmware from a master model
                        newExt.getPropertyBag().put(VMRESPONSE,
                                outcome(manager.createCopyOfMaster(ext.getPropertyBag().get(VMMASTERPATH), strPath)));
                        newExt.getPropertyBag().put(VMPATH, manager.getVmImagesFolder() + File.separator + strPath);
                    }
                    else if (strCommand.equals("delete")) 
                    {
                        // delete a vmware
                        newExt.getPropertyBag().put(VMRESPONSE, outcome(manager.deleteCopy(strPath)));
                    }
                    else if (strCommand.equals("getmasters"))
                    {
                        // list the master copies
                        newExt.getPropertyBag().put(VMRESPONSE, manager.getMasterImages());
                    }
                } catch (RemoteException e) {
                    newExt.getPropertyBag().put(VMRESPONSE, FAILURE);
                }

                // send the message
                refXmppListener.sendMessage(packet.getFrom(), "", "AE", newExt);
            }
        }
    }
}
