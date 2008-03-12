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
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        sfLog().info("VMWare Message Listener deployed.");
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
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
     * @return a filter that only accepts messages in the vmware namespace
     */
    public PacketFilter getFilter() {
        return new VMWareMessageFilter();
    }

    /**
     * {@inheritDoc}
     * @param packet packet to process
     */
    public void processPacket(Packet packet) {
        sfLog().info("VMWare Message Listener: Received packet: " + packet);
        // get the extension
        XMPPEventExtension ext = (XMPPEventExtension) packet.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
        if (ext != null)
        {
            // use the property bag
            String command = ext.getPropertyBag().get("vmcmd");
            String strPath = ext.getPropertyBag().get(VMPATH);

            if (command != null) {
                // extension for the response message
                XMPPEventExtension response = new XMPPEventExtension();

                response.setMessageType(MonitoringConstants.VM_MESSAGE);
                response.setTimestamp(String.format("%d", Calendar.getInstance().getTimeInMillis()));
                try {
                    response.setHost(InetAddress.getLocalHost().getHostName());
                } catch (UnknownHostException e) {
                    response.setHost(packet.getTo());
                }

                // fill the response bag
                response.getPropertyBag().put("vmcmd", command);
                response.getPropertyBag().put(VMPATH, strPath);

                try {
                    if (command.equals("start")) {
                        // attempt to start the machine
                        response.getPropertyBag().put(VMRESPONSE, manager.startVM(strPath));
                    }
                    else if (command.equals("stop")) {
                        response.getPropertyBag().put(VMRESPONSE, manager.shutDownVM(strPath));
                    }
                    else if (command.equals("suspend")) {
                        response.getPropertyBag().put(VMRESPONSE, manager.suspendVM(strPath));
                    }
                    else if (command.equals("reset")) {
                        response.getPropertyBag().put(VMRESPONSE, manager.resetVM(strPath));
                    }
                    else if (command.equals("list")) {
                        response.getPropertyBag().put(VMRESPONSE, manager.getControlledMachines());
                    }
                    else if (command.equals("powerstate")) {
                        int iState = manager.getPowerState(strPath);
                        String strResponse = "";

                        // the power state is a bitmask
                        int iTmp = iState & 0x000F;
                        switch (iTmp) {
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF:
                                strResponse += "Powered off. ";
                                break;
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_ON:
                                strResponse += "Powered on. ";
                                break;
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERING_OFF:
                                strResponse += "Powering off. ";
                                break;
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERING_ON:
                                strResponse += "Powering on. ";
                                break;
                            default:
                                break;
                        }

                        iTmp = iState & 0x00F0;
                        switch (iTmp) {
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_RESETTING:
                                strResponse += "Resetting. ";
                                break;
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDED:
                                strResponse += "Suspended. ";
                                break;
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDING:
                                strResponse += "Suspending. ";
                                break;
                            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_TOOLS_RUNNING:
                                strResponse += "Tools running. ";
                                break;
                            default:
                                break;
                        }

                        if ((iState & 0x0F00) == VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_BLOCKED_ON_MSG) {
                            strResponse += "Blocked on message. ";
                        }

                        if (strResponse.length() == 0)
                            strResponse = "Could not retrieve power state.";

                        response.getPropertyBag().put(VMRESPONSE, strResponse);
                    }
                    else if (command.equals("toolsstate")) {
                        int iState = manager.getToolsState(strPath);
                        switch (iState)
                        {
                            case VMWareVixLibrary.VixToolsState.VIX_TOOLSSTATE_NOT_INSTALLED:
                                response.getPropertyBag().put(VMRESPONSE, "Tools not installed.");
                                break;
                            case VMWareVixLibrary.VixToolsState.VIX_TOOLSSTATE_RUNNING:
                                response.getPropertyBag().put(VMRESPONSE, "Tools running.");
                                break;
                            case VMWareVixLibrary.VixToolsState.VIX_TOOLSSTATE_UNKNOWN:
                                response.getPropertyBag().put(VMRESPONSE, "Tools state unknown.");
                                break;
                            default:
                                response.getPropertyBag().put(VMRESPONSE, "Unknown tools state: " + iState);
                                break;
                        }
                    }
                    else if (command.equals("create")) {
                        // create a vmware from a master model
                        response.getPropertyBag().put(VMRESPONSE, manager.createCopyOfMaster(ext.getPropertyBag().get(VMMASTERPATH), strPath));
                        response.getPropertyBag().put(VMPATH, manager.getVmImagesFolder() + File.separator + strPath + File.separator + strPath + ".vmx");
                    }
                    else if (command.equals("delete")) {
                        // delete a vmware
                        response.getPropertyBag().put(VMRESPONSE, manager.deleteCopy(strPath));
                    }
                    else if (command.equals("getmasters")) {
                        // list the master copies
                        response.getPropertyBag().put(VMRESPONSE, manager.getMasterImages());
                    }
//                    else if (command.equals("copyhosttoguest")) {
//                        String strSrc = ext.getPropertyBag().get("source");
//                        String strDest = ext.getPropertyBag().get("dest");
//                        response.getPropertyBag().put(VMRESPONSE, manager.copyFileFromHostToGuestOS(strPath, strSrc, strDest));
//                    }
                } catch (Exception e) {
                    ProcessException(command, response, e);
                }

                // send the message
                refXmppListener.sendMessage(packet.getFrom(), "", "AE", response);
            }
        }
    }

    private void ProcessException(String command, XMPPEventExtension newExt, Throwable thrown) {
        sfLog().error("Failing command "+ command, thrown);
        newExt.getPropertyBag().put(VMRESPONSE, thrown.toString());
    }
}
