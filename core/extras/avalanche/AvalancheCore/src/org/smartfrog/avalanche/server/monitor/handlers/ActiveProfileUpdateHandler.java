/**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org
 */
package org.smartfrog.avalanche.server.monitor.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Roster;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.core.activeHostProfile.ModuleStateType;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.ServerSetup;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.shared.handlers.MessageHandler;

/**
 * This is a server side handler, this updates the active profile of a host
 * on receiving a message. The handler is indepenent of monitoring protocols
 * it uses an interface MonitoringEvent which can be implemented in different
 * ways for differnet monitoring protocols.
 *
 * @author sanjaydahiya
 */
public class ActiveProfileUpdateHandler implements MessageHandler {
    ActiveProfileManager profileManager;
    private static Log log = LogFactory.getLog(ActiveProfileUpdateHandler.class);
    ServerSetup setup = null;


    public ActiveProfileUpdateHandler(ServerSetup setup) {
        this.setup = setup;
        AvalancheFactory factory = setup.getFactory();
        try {
            profileManager = factory.getActiveProfileManager();
        } catch (ModuleCreationException e) {
            // TODO : Bad, bring down the whole system
            e.printStackTrace();
            log.fatal(e);
        }
    }

    /**
     * Callback method, this is invoked by event listener on the server when a
     * new Monitoring Event is received. This method updates the Active Profile on
     * a host with the host/module state received in the event.
     * @param e the to-be-handled event
     */
    public void handleEvent(MonitoringEvent e) {
        log.info("Event Received: " + e);
        if (null != e) {
            switch (e.getMessageType()) {
                case MonitoringConstants.MODULE_STATE_CHANGED:
                case MonitoringConstants.MODULE_OPERATION_FAILED: {
                    String hostName = e.getHost();
                    String moduleId = e.getModuleId();

                    try {
                        ActiveProfileType profile = profileManager.getProfile(hostName);
                        if (null == profile) {
                            // create a new profile for host
                            profile = profileManager.newProfile(hostName);
                        }
                        ModuleStateType[] moduleProfiles = profile.getModuleStateArray();
                        ModuleStateType moduleProfile = null;

                        for (int i = 0; i < moduleProfiles.length; i++) {
                            String id = moduleProfiles[i].getId();
                            if (moduleId.equals(id)) {
                                // need to update this module profile .
                                String instanceName = moduleProfiles[i].getInstanceName();
                                if (instanceName.equals(e.getInstanceName())) {
                                    moduleProfile = moduleProfiles[i];
                                    break;
                                }
                            }
                        }

                        if (null == moduleProfile) {
                            // profile should already exist, created while submitting.
                            // this should never execute.
                            // create a new entry for the host
                            moduleProfile = profile.addNewModuleState();
                            moduleProfile.setId(e.getModuleId());
                            moduleProfile.setInstanceName(e.getInstanceName());

                            log.error("Executing forbidden code !!! ");
                        }

                        moduleProfile.setState(e.getModuleState());
                        moduleProfile.setLastUpdated(e.getTimestamp());
                        moduleProfile.setMsg(e.getMsg());
                        moduleProfile.setLastAction(e.getLastAction());

                        profileManager.setProfile(profile);
                    } catch (DatabaseAccessException ex) {
                        log.error(ex);
                    } catch (DuplicateEntryException en) {
                        log.error(en);
                    }
                }
                break;
                case MonitoringConstants.HOST_VANISH:
                case MonitoringConstants.HOST_STARTED:
                    // update host state, instead of modules
                    String hostName = e.getHost();

                    // No use in adding a Roster entry again?!
                    //setup.getListenerAdapter().addUserToRoster(hostName);

                    try {
                        ActiveProfileType profile = profileManager.getProfile(hostName);
                        if (null == profile) {
                            // create a new profile for host
                            profile = profileManager.newProfile(hostName);
                        }

                        // As we received message from host - it is most likely that it is indeed "Available"
                        profile.setHostState("Available");

                        // TODO: Display message to the webinterface - sadly there is no such field in the ActiveProfileType

                    } catch (DatabaseAccessException ex) {
                        log.error(ex);
                    } catch (DuplicateEntryException en) {
                        // can never happen, we already checked for dups
                        log.error(en);
                    }
                    break;
                default:
                    break;

            }
		}
	}

}
