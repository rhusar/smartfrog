/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp;

import java.rmi.Remote;

public interface SFSlpLocator extends Remote {

    String ATTR_RESULT = "result";
    String ATTR_SLP_CONFIG_INTERFACE = "slp_config_interface";
    String ATTR_SLP_CONFIG_MC_MAX = "slp_config_mc_max";
    String ATTR_SLP_CONFIG_RND_WAIT = "slp_config_rnd_wait";
    String ATTR_SLP_CONFIG_RETRY = "slp_config_retry";
    String ATTR_SLP_CONFIG_RETRY_MAX = "slp_config_retry_max";
    String ATTR_SLP_CONFIG_DA_FIND = "slp_config_da_find";
    String ATTR_SLP_CONFIG_DA_ADDRESSES = "slp_config_daAddresses";
    String ATTR_SLP_CONFIG_SCOPE_LIST = "slp_config_scope_list";
    String ATTR_SLP_CONFIG_MTU = "slp_config_mtu";
    String ATTR_SLP_CONFIG_PORT = "slp_config_port";
    String ATTR_SLP_CONFIG_LOCALE = "slp_config_locale";
    String ATTR_SLP_CONFIG_MC_ADDR = "slp_config_mc_addr";
    String ATTR_SLP_CONFIG_DEBUG = "slp_config_debug";
    String ATTR_SLP_CONFIG_LOG_ERRORS = "slp_config_log_errors";
    String ATTR_SLP_CONFIG_LOG_MSG = "slp_config_log_msg";
    String ATTR_SLP_CONFIG_LOGFILE = "slp_config_logfile";
    String ATTR_SLP_CONFIG_SFLOG = "slp_config_sflog";
    String ATTR_LOCATOR_DISCOVERY_DELAY = "locator_discovery_delay";
    String ATTR_LOCATOR_DISCOVERY_INTERVAL = "locator_discovery_interval";
    String ATTR_RETURN_ENUMERATION = "returnEnumeration";
    String ATTR_SERVICE_TYPE = "serviceType";
    String ATTR_SEARCH_FILTER = "searchFilter";
    String ATTR_SEARCH_SCOPES = "searchScopes";
}
