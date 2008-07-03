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

	/**
     * Constructor.
     * @throws RemoteException In case of network/rmi error
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

        sfLog().info("VMWareMessageListener going down.");

        // unregister this listener from the xmpp message listener
        refXmppListener.unregisterPacketHandler(this);
    }

    public void sfTerminate(TerminationRecord status) {
        super.sfTerminate(status);

        sfLog().info("VMWareMessageListener going down.");

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

    private boolean isManagerLive() {
        if(manager==null) {
            return false;
        }
        Prim managerPrim =  manager;
        try {
            return managerPrim.sfIsStarted();
        } catch (RemoteException e) {
            return false;
        }
    }
    /**
     * {@inheritDoc}
     * @param packet packet to process
     */
    public void processPacket(Packet packet) {
        sfLog().info("VMWare Message Listener: Received packet: " + packet.getFrom() + ": " + packet.toXML());

        // get the extension
        XMPPEventExtension ext = (XMPPEventExtension) packet.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
        if (ext != null)
        {
            // print the content of the property bag
            sfLog().info("Printing content of received property bag:");
            for (String key : ext.getPropertyBag().keySet()) {
                sfLog().info(String.format("%s = %s", key, ext.getPropertyBag().get(key)));
            }

            // use the property bag
            String command = ext.getPropertyBag().get(VMWareConstants.VMCMD);
            String strName = ext.getPropertyBag().get(VMWareConstants.VMNAME);

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
                response.getPropertyBag().put(VMWareConstants.VMCMD, command);
                response.getPropertyBag().put(VMWareConstants.VMNAME, strName);

                if (!isManagerLive()) {
                    response.getPropertyBag().put(VMWareConstants.VMRESPONSE, "No VMWareServerManager running");
                }
                else {
                    try {
                        if (command.equals(VMWareConstants.VM_CMD_START)) {
                            // attempt to start the machine
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.startVM(strName));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_STOP)) {
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.shutDownVM(strName));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_SUSPEND)) {
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.suspendVM(strName));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_RESET)) {
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.resetVM(strName));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_LIST)) {
                            // count the machines
                            int i = 0;
                            for (VMWareImageModule mod : manager.getControlledMachines()) {
                                // and the display name
                                try {
                                    response.getPropertyBag().put("list_" + i + "_vmname", mod.getAttribute("displayName"));
                                } catch (SmartFrogException e) {
                                    response.getPropertyBag().put("list_" + i + "_vmname", "Could not retreive displayName.");
                                }

                                // and the power state
                                try {
                                    response.getPropertyBag().put("list_" + i + "_vmstate", manager.convertPowerState(mod.getPowerState()));
                                } catch (SmartFrogException e) {
                                    response.getPropertyBag().put("list_" + i + "_vmstate", "Could not retreive power state.");
                                }

                                i++;
                            }
                            response.getPropertyBag().put(VMWareConstants.VM_LIST_COUNT, String.format("%d", i));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_POWERSTATE)) {
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.convertPowerState(manager.getPowerState(strName)));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_TOOLSSTATE)) {
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.convertToolsState(manager.getToolsState(strName)));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_CREATE)) {
                            // get additional attributes
                            String  name    = ext.getPropertyBag().get(VMWareConstants.VM_CREATE_NAME),
                                    master  = ext.getPropertyBag().get(VMWareConstants.VM_CREATE_MASTER),
                                    user    = ext.getPropertyBag().get(VMWareConstants.VM_CREATE_USER),
                                    pass    = ext.getPropertyBag().get(VMWareConstants.VM_CREATE_PASS);

                            // create a vmware from a master model
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.createCopyOfMaster(master, name, user, pass));
                            response.getPropertyBag().put(VMWareConstants.VMNAME, name);
						}
                        else if (command.equals(VMWareConstants.VM_CMD_DELETE)) {
                            // delete a vmware
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.deleteCopy(strName));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_GETMASTERS)) {
                            // list the master copies
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.getMasterImages());
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_RENAME)) {
                            // get the new name
                            String newName = ext.getPropertyBag().get(VMWareConstants.VM_RENAME_NAME);

                            // rename the vm
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.renameVM(strName, newName));

                            // correct response
                            response.getPropertyBag().put(VMWareConstants.VMNAME, newName);

                            response.getPropertyBag().put(VMWareConstants.VM_RENAME_OLD_NAME, strName);
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_GETATTRIBUTE)) {
                            // get the key
                            String key = ext.getPropertyBag().get(VMWareConstants.VM_GETATTRIBUTE_KEY);

                            // get the attribute
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.getVMAttribute(strName, key));
							response.getPropertyBag().put(VMWareConstants.VM_GETATTRIBUTE_KEY, key);
						}
                        else if (command.equals(VMWareConstants.VM_CMD_SETATTRIBUTE)) {
                            // get the key and the new value
                            String  key = ext.getPropertyBag().get(VMWareConstants.VM_SETATTRIBUTE_KEY),
                                    value = ext.getPropertyBag().get(VMWareConstants.VM_SETATTRIBUTE_VALUE);

                            if (key.equals("displayName")) {
                                response.getPropertyBag().put(VMWareConstants.VMRESPONSE, "Please use the rename command to rename a VM.");
                            }
                            else {
                                // set the attribute
                                response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.setVMAttribute(strName, key, value));
                            }
							response.getPropertyBag().put(VMWareConstants.VM_SETATTRIBUTE_KEY, key);
							response.getPropertyBag().put(VMWareConstants.VM_SETATTRIBUTE_VALUE, value);
						}
                        else if (command.equals(VMWareConstants.VM_CMD_EXECUTE)) {
                            String  strCommand = ext.getPropertyBag().get(VMWareConstants.VM_EXECUTE_CMD),
                                    strParameters = ext.getPropertyBag().get(VMWareConstants.VM_EXECUTE_PARAM);
                            boolean bNoWait = (ext.getPropertyBag().get(VMWareConstants.VM_EXECUTE_NOWAIT) != null);

                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.executeInGuestOS(strName, strCommand, strParameters, bNoWait));

							response.getPropertyBag().put(VMWareConstants.VM_EXECUTE_CMD, strCommand);
							response.getPropertyBag().put(VMWareConstants.VM_EXECUTE_PARAM, strParameters);
							response.getPropertyBag().put(VMWareConstants.VM_EXECUTE_NOWAIT, (bNoWait ? "true" : "false"));
						}
                        else if (command.equals(VMWareConstants.VM_CMD_WAIT_FOR_TOOLS)) {
                            int iTimeout = Integer.parseInt(ext.getPropertyBag().get(VMWareConstants.VM_WAIT_FOR_TOOLS_TIMEOUT));
                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.waitForTools(strName, iTimeout));
                        }
                        else if (command.equals(VMWareConstants.VM_CMD_TAKE_SNAPSHOT)) {
                            String  strSnapName = ext.getPropertyBag().get(VMWareConstants.VM_TAKE_SNAPSHOT_NAME),
                                    strSnapDesc = ext.getPropertyBag().get(VMWareConstants.VM_TAKE_SNAPSHOT_DESCRIPTION);
                            boolean bIncMem = Boolean.parseBoolean(ext.getPropertyBag().get(VMWareConstants.VM_TAKE_SNAPSHOT_INCLUDE_MEMORY));

                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.takeSnapshot(strName, strSnapName, strSnapDesc, bIncMem));

							response.getPropertyBag().put(VMWareConstants.VM_TAKE_SNAPSHOT_NAME, strSnapName);
							response.getPropertyBag().put(VMWareConstants.VM_TAKE_SNAPSHOT_DESCRIPTION, strSnapDesc);
						}
                        else if (command.equals(VMWareConstants.VM_CMD_REVERT)) {
                            String strSnapName = ext.getPropertyBag().get(VMWareConstants.VM_REVERT_NAME);
                            if (strSnapName != null)
                                response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.revertVMToSnapshot(strName, strSnapName));
                            else
                                response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.revertVMToSnapshot(strName));
							response.getPropertyBag().put(VMWareConstants.VM_REVERT_NAME, strSnapName);
						}
                        else if (command.equals(VMWareConstants.VM_CMD_SET_GUEST_CRED)) {
                            String  strUser = ext.getPropertyBag().get(VMWareConstants.VM_SET_GUEST_CRED_USER),
                                    strPass = ext.getPropertyBag().get(VMWareConstants.VM_SET_GUEST_CRED_PASS);

                            response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.setGuestOSCredentials(strName, strUser, strPass));

							response.getPropertyBag().put(VMWareConstants.VM_SET_GUEST_CRED_USER, strUser);
							response.getPropertyBag().put(VMWareConstants.VM_SET_GUEST_CRED_PASS, strPass);
						}
                        else if(command.equals(VMWareConstants.VM_CMD_DELETE_SNAPSHOT)) {
                            boolean bDelChildren = Boolean.parseBoolean(ext.getPropertyBag().get(VMWareConstants.VM_DELETE_SNAPSHOT_DEL_CHILD));
                            String strSnapName = ext.getPropertyBag().get(VMWareConstants.VM_DELETE_SNAPSHOT_NAME);
                            if (strSnapName != null)
                                response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.deleteVMSnapshot(strName, strSnapName, bDelChildren));
                            else
                                response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.deleteVMSnapshot(strName, bDelChildren));

							response.getPropertyBag().put(VMWareConstants.VM_DELETE_SNAPSHOT_NAME, strSnapName);
							response.getPropertyBag().put(VMWareConstants.VM_DELETE_SNAPSHOT_DEL_CHILD, (bDelChildren ? "true" : "false"));
						}
                        else if (command.equals(VMWareConstants.VM_CMD_COPY_HOST_TO_GUEST)) {
                            String strSrc = ext.getPropertyBag().get(VMWareConstants.VM_COPY_HTOG_SOURCE);
                            String strDest = ext.getPropertyBag().get(VMWareConstants.VM_COPY_HTOG_DEST);

							response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.copyFileFromHostToGuest(strName, strSrc, strDest));

							response.getPropertyBag().put(VMWareConstants.VM_COPY_HTOG_SOURCE, strSrc);
							response.getPropertyBag().put(VMWareConstants.VM_COPY_HTOG_DEST, strDest);
						}
//						else if (command.equals(VMWareConstants.VM_CMD_MKDIR)) {
//							String strPath = ext.getPropertyBag().get(VMWareConstants.VM_MKDIR_PATH);
//
//							response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.mkdirInGuest(strName, strPath));
//
//							response.getPropertyBag().put(VMWareConstants.VM_MKDIR_PATH, strPath);
//						}
//						else if (command.equals(VMWareConstants.VM_CMD_EXISTS_DIR)) {
//							String strPath = ext.getPropertyBag().get(VMWareConstants.VM_EXISTS_DIR_PATH);
//
//							response.getPropertyBag().put(VMWareConstants.VMRESPONSE, manager.existsDirInGuest(strName, strPath));
//
//							response.getPropertyBag().put(VMWareConstants.VM_EXISTS_DIR_PATH, strPath);
//						}
//                        else if (command.equals("writeguestenv")) {
//                            String  strVarName = ext.getPropertyBag().get("wenv_name"),
//                                    strVarValue = ext.getPropertyBag().get("wenv_value");
//
//                            response.getPropertyBag().put(VMRESPONSE, manager.writeGuestEnvVar(strName, strVarName, strVarValue));
//                        }
//                        else if (command.equals("readguestenv")) {
//                            String strVarName = ext.getPropertyBag().get("renv_name");
//
//                            response.getPropertyBag().put(VMRESPONSE, manager.readGuestEnvVar(strName, strVarName));
//                        }
                        // set the name of this module

                    } catch (Exception e) {
                        ProcessException(command, response, e);
                    }
                }

                // send the message
                sfLog().info("sending message: " + response);
                refXmppListener.sendMessage(packet.getFrom(), "", "AE", response);
            }
        }
    }

    private void ProcessException(String command, XMPPEventExtension newExt, Throwable thrown) {
        sfLog().error("Failing command "+ command, thrown);
        newExt.getPropertyBag().put(VMWareConstants.VMRESPONSE, thrown.toString());
    }
}
