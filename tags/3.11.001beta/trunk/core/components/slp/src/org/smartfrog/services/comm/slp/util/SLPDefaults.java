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

package org.smartfrog.services.comm.slp.util;

/** This class holds the default values for the possible configuration parameters of the SLP library. */
public class SLPDefaults {
    // timing values
    public static final int DEF_CONFIG_MC_MAX = 15000; // 15 seconds
    public static final int DEF_CONFIG_RND_WAIT = 1000; // 1 second
    public static final int DEF_CONFIG_RETRY = 2000; // 2 seconds
    public static final int DEF_CONFIG_RETRY_MAX = 15000; // 15 seconds
    public static final int DEF_CONFIG_DA_BEAT = 10800000; // 3 hours
    public static final int DEF_CONFIG_DA_FIND = 900000; // 900 seconds
    public static final int DEF_CONFIG_CLOSE_CONN = 300000; // 5 minutes

    // behaviour
    public static final boolean DEF_CONFIG_BCONLY = false; // broadcast only
    public static final String DEF_CONFIG_USEDA = ""; // use predefined DA
    public static final boolean DEF_CONFIG_DA_DISCOVERY = true; // use active DA discovery ?
    public static final boolean DEF_CONFIG_PASSIVE_DA = true; // use passive DA discovery ?
    public static final String DEF_CONFIG_SCOPE_LIST = "default";
    public static final String DEF_CONFIG_SPI_STRING = ""; // currently not supported
    public static final String DEF_CONFIG_DA_SPI_STRING = ""; // currently not supported

    // misc values
    public static final int DEF_CONFIG_MTU = 1400; // mtu for datagram packets
    public static final int DEF_CONFIG_SLP_PORT = 427; // slp port
    public static final int DEF_CONFIG_UAPORT = 0; // ua unicast port (0 means any port)
    public static final int DEF_CONFIG_SAPORT = 0; // sa unicast port (0 means any port)
    public static final String DEF_CONFIG_LOCALE = "en"; // default locale
    public static final String DEF_CONFIG_MC_ADDR = "239.255.255.253";

    // debug
    public static final boolean DEF_CONFIG_DEBUG = false;
    public static final boolean DEF_CONFIG_LOG_ERRORS = false;
    public static final boolean DEF_CONFIG_LOG_MSG = false;
    public static final String DEF_CONFIG_LOGFILE = "";
    public static final boolean DEF_CONFIG_SFLOG = false;

    // properties object...
    private static java.util.Properties defProperties = null;

    // return the default properties...
    /** Returns a Properties object holding the default values for all supported properties. */
    public static java.util.Properties getDefaultProperties() {
        if (defProperties == null) {
            defProperties = new java.util.Properties();
            // set timing values
            defProperties.setProperty("net.slp.multicastMaximumWait", Integer.toString(DEF_CONFIG_MC_MAX));
            defProperties.setProperty("net.slp.randomWaitBound", Integer.toString(DEF_CONFIG_RND_WAIT));
            defProperties.setProperty("net.slp.initialTimeout", Integer.toString(DEF_CONFIG_RETRY));
            defProperties.setProperty("net.slp.unicastMaximumWait", Integer.toString(DEF_CONFIG_RETRY_MAX));
            defProperties.setProperty("net.slp.DAHeartBeat", Integer.toString(DEF_CONFIG_DA_BEAT));
            defProperties.setProperty("net.slp.DAActiveDiscoveryInterval", Integer.toString(DEF_CONFIG_DA_FIND));
            defProperties.setProperty("net.slp.closeConnectionWait", Integer.toString(DEF_CONFIG_CLOSE_CONN));
            // set behaviour
            defProperties.setProperty("net.slp.isBroadcastOnly", Boolean.toString(DEF_CONFIG_BCONLY));
            defProperties.setProperty("net.slp.useScopes", DEF_CONFIG_SCOPE_LIST);
            defProperties.setProperty("net.slp.DAAddresses", DEF_CONFIG_USEDA);
            defProperties.setProperty("net.slp.passiveDADetection", Boolean.toString(DEF_CONFIG_PASSIVE_DA));
            // misc values
            defProperties.setProperty("net.slp.MTU", Integer.toString(DEF_CONFIG_MTU));
            defProperties.setProperty("net.slp.port", Integer.toString(DEF_CONFIG_SLP_PORT));
            defProperties.setProperty("net.slp.uaport", Integer.toString(DEF_CONFIG_UAPORT));
            defProperties.setProperty("net.slp.saport", Integer.toString(DEF_CONFIG_SAPORT));
            defProperties.setProperty("net.slp.locale", DEF_CONFIG_LOCALE);
            defProperties.setProperty("net.slp.multicastAddress", DEF_CONFIG_MC_ADDR);
            // debug
            defProperties.setProperty("net.slp.debug", Boolean.toString(DEF_CONFIG_DEBUG));
            defProperties.setProperty("net.slp.logErrors", Boolean.toString(DEF_CONFIG_LOG_ERRORS));
            defProperties.setProperty("net.slp.logMsg", Boolean.toString(DEF_CONFIG_LOG_MSG));
            defProperties.setProperty("net.slp.logfile", DEF_CONFIG_LOGFILE);
            defProperties.setProperty("net.slp.sflog", Boolean.toString(DEF_CONFIG_SFLOG));
        }

        return (java.util.Properties) defProperties.clone();
    }
}
