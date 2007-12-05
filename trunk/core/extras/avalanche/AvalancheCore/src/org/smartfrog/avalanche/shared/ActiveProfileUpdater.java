package org.smartfrog.avalanche.shared;

/**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org
 */

import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.core.activeHostProfile.*;
import org.smartfrog.services.xmpp.MonitoringEvent;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlString;

/**
 * Retrieves, saves and modifies the ActiveProfile of a given host
 */
public class ActiveProfileUpdater {
    private static Log log = LogFactory.getLog(ActiveProfileUpdater.class);
    private ActiveProfileManager profileManager = null;
    private static final int XMPP_HISTORY_LIMIT = 25;

    public ActiveProfileUpdater() {
        try {
            profileManager = AvalancheFactory.getFactory(AvalancheFactory.BDB).getActiveProfileManager();
        } catch (ModuleCreationException e) {
            log.fatal(e);
        }
    }

    /**
     * Get ActiveProfile out of the database
     *
     * @param hostId of host whose ActiveProfile should be retrieved.
     * @return ActiveProfile of the given host
     */
    public ActiveProfileType getActiveProfile(String hostId) {
        try {
            return profileManager.getProfile(hostId);
        } catch (DatabaseAccessException ex) {
            log.error("Error while getting ActiveProfile of " + hostId);
            return null;
        }
    }

    /**
     * Creates a new ActiveProfile for a specific host
     *
     * @param hostId of the new host
     */
    public void createActiveProfile(String hostId) {
        try {
            profileManager.newProfile(hostId);
        } catch (Exception ex) {
            log.error("Error while creating ActiveProfile for " + hostId);
        }
    }

    /**
     * Stores an ActiveProfile in the database
     *
     * @param profile of the host
     */
    public void storeActiveProfile(ActiveProfileType profile) {
        try {
            profileManager.setProfile(profile);
        } catch (Exception ex) {
            log.error("Error while storing ActiveProfile of " + profile.getHostId());
        }
    }

    /**
     * Removes an ActiveProfile from the database
     *
     * @param hostId of the host
     */
    public void removeActiveProfile(String hostId) {
        try {
            profileManager.removeProfile(hostId);
        } catch (Exception ex) {
            log.error("Error while storing ActiveProfile of " + hostId);
        }
    }

    /**
     * Updates the availability record of a given machine
     * This needs to be performed e.g. after a XMPP presence change is detected.
     *
     * @param hostId       is the name of the machine
     * @param availability true if the machine is available; false if it is not.
     */
    public void setMachineAvailability(String hostId, boolean availability) {
        ActiveProfileType type = getActiveProfile(hostId);
        if (type != null) {
            type.setHostState(availability ? "Available" : "Not Available");
            storeActiveProfile(type);
        }
    }


    /**
     * Saves a MessageType in the ActiveProfile
     *
     * @param e is a MonitoringEvent
     */
    public void addNewMessage(MonitoringEvent e) {
        ActiveProfileType type = getActiveProfile(e.getHost());
        if (type != null) {
            while (type.getMessagesHistoryArray().length > XMPP_HISTORY_LIMIT) {
                type.removeMessagesHistory(0);
            }
            MessageType newMsg = type.addNewMessagesHistory();
            newMsg.setTime(e.getTimestamp());
            newMsg.setMsg(e.getMsg());
            storeActiveProfile(type);
        }
    }

    /**
     * Saves a vmMessageType in the ActiveProfile
     * @param ext
     */
    public void processVMMessage(XMPPEventExtension ext)
    {
        ActiveProfileType type = getActiveProfile(ext.getHost());
        if (type != null) {
            // clip the history
            while (type.getMessagesHistoryArray().length > XMPP_HISTORY_LIMIT)
                type.removeMessagesHistory(0);

            String strCommand = ext.getPropertyBag().get("vmcmd");
            String strResponse = ext.getPropertyBag().get("vmresponse");
            String strVMPath = ext.getPropertyBag().get("vmpath");

            if (strCommand != null) {
                if (strCommand.equals("create")) {
                    if (strResponse.equals("success")) {
                        // create a new type
                        VmStateType vst = type.addNewVmState();
                        vst.setVmLastCmd(strCommand);
                        vst.setVmPath(strVMPath);
                        vst.setVmResponse(strResponse);
                    }
                } else if (strCommand.equals("getmasters")) {
                    // delete the old masters list
                    while (type.getVmMasterCopyArray().length > 0)
                        type.removeVmMasterCopy(0);

                    // add the new ones
                    String[] strMasters = strResponse.split("\n");
                    for (String s : strMasters)
                    {
                        XmlString str = type.insertNewVmMasterCopy(0);
                        str.setStringValue(s);
                    }
                } else if (strCommand.equals("list")) {
                    // a list command has been sent and responded to
                    // the response contains the list of running
                    // machines divided by '\n'

                    // clear the old entries
                    while (type.getVmStateArray().length > 0)
                        type.removeVmState(0);

                    // add the new data
                    String[] strMachines = strResponse.split("\n");
                    for (String s : strMachines) {
                        if (s.equals(""))
                                continue;

                        VmStateType newType = type.addNewVmState();
                        newType.setVmPath(s);
                        newType.setVmLastCmd("list");
                        newType.setVmResponse("success");
                    }
                } else if (strCommand.equals("delete")) {
                    // find the entry
                    for (int i = 0; i < type.getVmStateArray().length; i++) {
                        VmStateType t = type.getVmStateArray()[i];
                        if (t.getVmPath().equals(strVMPath)) {
                            type.removeVmState(i);
                            break;
                        }
                    }
                } else if (strVMPath != null) {
                    // find the appropriate type
                    boolean bFound = false;
                    for (VmStateType t : type.getVmStateArray()) {
                        if (t.getVmPath().equals(strVMPath))
                        {
                            bFound = true;
                            t.setVmLastCmd(strCommand);
                            t.setVmResponse(strResponse);
                        }
                    }
                    if (!bFound)
                    {
                        // type not found, create a new one
                        VmStateType newType = type.addNewVmState();
                        newType.setVmLastCmd(strCommand);
                        newType.setVmResponse(strResponse);
                        newType.setVmPath(strVMPath);
                    }
                }

                // add a new message
                MessageType newMsg = type.addNewMessagesHistory();
                newMsg.setTime(ext.getTimestamp());
                newMsg.setMsg("VM Path: " + strVMPath +
                                ", Command: " + strCommand +
                                ", Response: " + strResponse);
            }
            
            // store the profile
            storeActiveProfile(type);
        }
    }

    /**
     * Saves a ModuleStateType in the ActiveProfile
     *
     * @param e is a MonitoringEvent
     */
    public void setModuleState(MonitoringEvent e) {
        ActiveProfileType type = getActiveProfile(e.getHost());

        ModuleStateType[] moduleProfiles = type.getModuleStateArray();
        ModuleStateType moduleProfile = null;

        for (ModuleStateType currentModuleProfile : moduleProfiles) {
            // If moduleId is found
            String id = currentModuleProfile.getId();
            if (e.getModuleId().equals(id)) {
                // g
                String instanceName = currentModuleProfile.getInstanceName();
                if (instanceName.equals(e.getInstanceName())) {
                    moduleProfile = currentModuleProfile;
                    break;
                }
            }
        }

        if (moduleProfile == null) {
            moduleProfile = type.addNewModuleState();
            moduleProfile.setId(e.getModuleId());
            moduleProfile.setInstanceName(e.getInstanceName());

            log.error("Module does not exist - creating module information");
        }

        moduleProfile.setState(e.getModuleState());
        moduleProfile.setLastUpdated(e.getTimestamp());
        moduleProfile.setMsg(e.getMsg());
        moduleProfile.setLastAction(e.getLastAction());
    }
}
