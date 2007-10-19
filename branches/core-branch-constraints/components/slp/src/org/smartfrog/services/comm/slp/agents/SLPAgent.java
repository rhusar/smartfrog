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

package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.ServiceType;
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.messages.SLPDAAdvMessage;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.messages.SLPSrvReqMessage;
import org.smartfrog.services.comm.slp.network.SlpSharedUnicastClient;
import org.smartfrog.services.comm.slp.network.SlpUdpCallback;
import org.smartfrog.services.comm.slp.network.SlpUdpClient;
import org.smartfrog.services.comm.slp.util.SLPDefaults;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;

/** Base class for Service- and User Agents. */
abstract class SLPAgent implements SlpUdpCallback, SLPMessageCallbacks {
    // smartfrog logging.
    protected LogSF sflog = null;

    // properties
    protected Properties properties = null;

    // fabricClients...
    protected SlpUdpClient multicastCommunicator = null;
    protected SlpUdpClient unicastCommunicator = null;

    // DA locator thread
    protected Timer daLocator = null;

    // Supported scopes
    protected Vector supportedScopes;

    // SLP configuration
    protected int CONFIG_MC_MAX = SLPDefaults.DEF_CONFIG_MC_MAX;
    protected int CONFIG_START_WAIT = SLPDefaults.DEF_CONFIG_RND_WAIT;
    protected int CONFIG_RETRY = SLPDefaults.DEF_CONFIG_RETRY;
    protected int CONFIG_RETRY_MAX = SLPDefaults.DEF_CONFIG_RETRY_MAX;
    protected int CONFIG_DA_FIND = SLPDefaults.DEF_CONFIG_DA_FIND;
    protected int CONFIG_SLP_PORT = SLPDefaults.DEF_CONFIG_SLP_PORT;
    protected String CONFIG_SLP_MC_ADDR = SLPDefaults.DEF_CONFIG_MC_ADDR;
    protected int CONFIG_SLP_AGENTPORT = 0; // this is set in the constructor !
    protected int CONFIG_SLP_MTU = SLPDefaults.DEF_CONFIG_MTU;
    protected boolean CONFIG_DA_DISCOVERY = SLPDefaults.DEF_CONFIG_DA_DISCOVERY;
    protected boolean CONFIG_PASSIVE_DA = SLPDefaults.DEF_CONFIG_PASSIVE_DA;
    protected boolean CONFIG_DEBUG = SLPDefaults.DEF_CONFIG_DEBUG;
    protected boolean CONFIG_LOG_ERRORS = SLPDefaults.DEF_CONFIG_LOG_ERRORS;
    protected boolean CONFIG_LOG_MSG = SLPDefaults.DEF_CONFIG_LOG_MSG;
    protected String CONFIG_LOGFILE = SLPDefaults.DEF_CONFIG_LOGFILE;
    protected boolean CONFIG_SFLOG = SLPDefaults.DEF_CONFIG_SFLOG;

    // language
    protected Locale locale = new Locale(SLPDefaults.DEF_CONFIG_LOCALE);

    // address
    InetAddress address = null;

    // known DAs.
    Map DAs = Collections.synchronizedMap(new TreeMap());

    // synchronization
    private Object wtSync = new Object();

    /********************************************/
    /************* Constructors *****************/
    /********************************************/

    /** Creates a new SLPAgent with the default properties. */
    SLPAgent() throws ServiceLocationException {
        try {
            address = InetAddress.getLocalHost();
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "SLP: Could not resolve inet address");
        }
    }

    /**
     * Creates a new SLPAgent with the given properties.
     *
     * @param properties The proterties for the agent.
     */
    SLPAgent(Properties properties) throws ServiceLocationException {
        this.properties = properties;
        int intValue = 0;
        String stringVal = null;
        try {
            if ((stringVal = properties.getProperty("net.slp.interface")) != null) {
                address = InetAddress.getByName(stringVal);
            } else {
                address = InetAddress.getLocalHost();
            }
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "SLP: Could not resolve inet address");
        }
        if ((stringVal = properties.getProperty("net.slp.multicastMaximumWait")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_MC_MAX = intValue;
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.randomWaitBound")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_START_WAIT = intValue;
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.initialTimeout")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_RETRY = intValue;
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.unicastMaximumWait")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_RETRY_MAX = intValue;
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.DAActiveDiscoveryInterval")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_DA_FIND = intValue;
                if (intValue == 0) {
                    CONFIG_DA_DISCOVERY = false;
                } else {
                    CONFIG_DA_DISCOVERY = true;
                }
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.useScopes")) != null && stringVal.length() != 0) {
            supportedScopes = new Vector();
            String[] scopes = stringVal.split(",");
            for (int i = 0; i < scopes.length; i++) {
                supportedScopes.add(scopes[i]);
            }
        }
        if ((stringVal = properties.getProperty("net.slp.passiveDADetection")) != null) {
            if (stringVal.equalsIgnoreCase("true")) {
                CONFIG_PASSIVE_DA = true;
            } else if (stringVal.equalsIgnoreCase("false")) CONFIG_PASSIVE_DA = false;
        }
        if ((stringVal = properties.getProperty("net.slp.mtu")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_SLP_MTU = intValue;
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.port")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_SLP_PORT = intValue;
            } catch (NumberFormatException nfe) {
            }
        }
        if ((stringVal = properties.getProperty("net.slp.locale")) != null && stringVal.length() != 0) {
            locale = new Locale(stringVal);
        }
        if ((stringVal = properties.getProperty("net.slp.multicastAddress")) != null) {
            if (stringVal.length() != 0) CONFIG_SLP_MC_ADDR = stringVal;
        }
        if ((stringVal = properties.getProperty("net.slp.debug")) != null) {
            if (stringVal.equalsIgnoreCase("true")) {
                CONFIG_DEBUG = true;
            } else if (stringVal.equalsIgnoreCase("false")) CONFIG_DEBUG = false;
        }
        if ((stringVal = properties.getProperty("net.slp.logErrors")) != null) {
            if (stringVal.equalsIgnoreCase("true")) {
                CONFIG_LOG_ERRORS = true;
            } else if (stringVal.equalsIgnoreCase("false")) CONFIG_LOG_ERRORS = false;
        }
        if ((stringVal = properties.getProperty("net.slp.logMsg")) != null) {
            if (stringVal.equalsIgnoreCase("true")) {
                CONFIG_LOG_MSG = true;
            } else if (stringVal.equalsIgnoreCase("false")) CONFIG_LOG_MSG = false;
        }
        if ((stringVal = properties.getProperty("net.slp.logfile")) != null) {
            CONFIG_LOGFILE = stringVal;
        }
        if ((stringVal = properties.getProperty("net.slp.sflog")) != null) {
            if (stringVal.equalsIgnoreCase("true")) {
                CONFIG_SFLOG = true;
            } else if (stringVal.equalsIgnoreCase("false")) CONFIG_SFLOG = false;
        }
    }

    /**
     * Does the actual work of creating the agent. If a list of DAs have been defined, it will contact those DAs here in
     * order to get information about them. This also ensures that only DAs that are actually found will be added to the
     * list of known DAs.
     */
    protected void createAgent(String DAsToUse) throws ServiceLocationException {
        // create scope list...
        if (supportedScopes == null) {
            supportedScopes = new Vector();
            supportedScopes.add(SLPDefaults.DEF_CONFIG_SCOPE_LIST);
        }

        // create shared SlpFabricClient to use for messages...
        boolean sendOnly = (CONFIG_SLP_AGENTPORT == 0);
        try {
            unicastCommunicator = new SlpSharedUnicastClient(address,
                    CONFIG_SLP_AGENTPORT,
                    CONFIG_SLP_MTU);
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "SLP: Could not create unicast socket");
        }

        // try to contact predefined DAs
        if (DAsToUse != null && DAsToUse.length() != 0) {
            String das[] = DAsToUse.split(",");
            SLPMessageSender ms = new SLPMessageSender(this, CONFIG_SLP_MTU, CONFIG_RETRY, null);

            for (int i = 0; i < das.length; i++) {
                try {
                    ms.sendSLPMessage(new SLPSrvReqMessage(
                            new ServiceType(DirectoryAgent.DA_TYPE),
                            supportedScopes,
                            "",
                            locale),
                            das[i], CONFIG_SLP_PORT, CONFIG_RETRY_MAX,
                            unicastCommunicator);
                } catch (ServiceLocationException se) {
                    logDebug("Failed to contact DA at " + das[i]);
                    // could throw exception, but do we really
                    // want to do that ? The system should be
                    // able to run without the DA. Perhaps this
                    // should be configurable ?
                }
            }
        }
        // start DA locator thread if required.
        if (CONFIG_DA_DISCOVERY) {
            Random rnd = new Random();
            int start_wait = Math.abs(rnd.nextInt() % CONFIG_START_WAIT);
            daLocator = new Timer();
            daLocator.schedule(new TimerTask() {
                public void run() {
                    daDiscovery();
                }
            }, start_wait, CONFIG_DA_FIND);
        }
    }

    /*******************************************/
    /***** FabricClient Callbacks **************/
    /** ************************************** */

    // timeout
    public boolean udpTimeout() {
        // should never get a timeout. But in any case, we just continue to listen.
        logError("UDP Timeout", null);
        return true;
    }

    // error
    public boolean udpError(Exception e) {
        logError("Internal System Error: ", e);
        return false;
    }

    // received
    public boolean udpReceived(DatagramPacket packet) {
        // create input stream. Check version.
        int version = 0;
        int function = 0;
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
        SLPInputStream sis = new SLPInputStream(bais);
        try {
            version = sis.readByte();
            function = sis.readByte();
        } catch (IOException ioe) {
            function = version = 0; // this packet will not be handled.
            logError("Received invalid message - packet ignored", ioe);
        }

        // check that version == 2. We only support version 2 of the SLP.
        if (version != 2) {
            function = 0; // packet will be ignored
            logError("Wrong SLP Version number: " + version, null);
        }

        SLPMessageHeader msgReply = null;
        try {
            msgReply = handleNonReplyMessage(function, sis, true);
        } catch (ServiceLocationException e) {
            logError("Error during message handling", e);
        }

        if (msgReply != null) {
            try {
                DatagramPacket toSend = SLPUtil.createDatagram(msgReply, packet.getAddress(), packet.getPort());
                unicastCommunicator.send(toSend);
                logMessage("Sending Message:", msgReply);
            } catch (Exception e) {
                logError("Failed to send message!", e);
            }
        }

        return true;
    }

    /**
     * This method is called when the agent is to perform active DA discovery. The agent should then send a request for
     * the service type "directory-agent" and wait for replies.
     */
    public void daDiscovery() {
        ServiceType type = new ServiceType(DirectoryAgent.DA_TYPE);
        SLPSrvReqMessage daRequest = new SLPSrvReqMessage(type,
                supportedScopes,
                "",
                locale);
        daRequest.setFlags(SLPMessageHeader.FLAG_MCAST);
        SLPMessageSender ms = new SLPMessageSender(this, CONFIG_SLP_MTU, CONFIG_RETRY, null);
        try {
            ms.sendSLPMessage(daRequest,
                    CONFIG_SLP_MC_ADDR, CONFIG_SLP_PORT, CONFIG_MC_MAX,
                    unicastCommunicator);
        } catch (ServiceLocationException se) {
            // write log if requested. Otherwise, ignore.
            logError("DA Discovery failed", se);
        }
    }

    /**
     * Called when a DAAdvert has been received. If the DA is new, it is added to the list of known DAs. If the DA is
     * known, and signals that it is going down (timestamp == 0), it is removed from the list of DAs.
     *
     * @param sis The stream to read the mesage from.
     * @return a DAInfo for the new DA, or a DA that has been down. null otherwise.
     */
    protected DAInfo recvDAAdvert(SLPInputStream sis) throws ServiceLocationException {
        DAInfo added = null;
        SLPDAAdvMessage msg = new SLPDAAdvMessage();
        msg.fromInputStream(sis);

        logMessage("Received Message:", msg);

        if (msg.getErrorCode() == 0) {
            ServiceURL daUrl = msg.getURL();
            int daPort = daUrl.getPort();
            if (daPort == ServiceURL.NO_PORT) daPort = CONFIG_SLP_PORT;
            // if the DA is not allready registered: Add it to the DA list.
            synchronized (DAs) {
                if (!DAs.containsKey(daUrl.getHost())) {
                    logDebug("Found a new DA - timestamp: " + msg.getTimestamp());
                    // we have a new DA. Add it to the list of DAs (if the DA is not going down)
                    if (msg.getTimestamp() != 0) { // 0 means the DA is going down
                        logDebug("Adding DA to list");
                        DAInfo newDA = new DAInfo(daUrl.getHost(), daPort, msg);
                        DAs.put(daUrl.getHost(), newDA);
                        added = newDA;
                    }
                } else {
                    // We allready have the DA in our list.
                    // Need to check that it has been running continously since
                    // the last received advert. If not, we need to refresh the
                    // service registrations with this DA.
                    // if timestamp == 0, the DA is removed, since a ts of 0 is an
                    // indication of the server going down.
                    logDebug("DA Allready known");
                    DAInfo theDA = (DAInfo) DAs.get(daUrl.getHost());
                    if (msg.getTimestamp() == 0) {
                        // DA going down...
                        logDebug("DA going down - Removing DA");
                        DAs.remove(daUrl.getHost());
                    } else {
                        DAInfo newDA = new DAInfo(daUrl.getHost(), daPort, msg);
                        if (msg.getTimestamp() < theDA.getTimestamp()) {
                            // The DA has been down. Need to reregister...
                            logDebug("Existing DA has been down. Reregistering");
                            added = newDA;
                        }
                        // update DAInfo (The attributes may have changed)
                        DAs.remove(daUrl.getHost());
                        DAs.put(daUrl.getHost(), newDA);
                    }
                }
            }
        }

        return added;
    }

    // handle incoming reply. This must be done in the subclasses
    public abstract boolean handleReplyMessage(int function, SLPInputStream sis,
                                               ServiceLocationEnumeration results)
            throws ServiceLocationException;

    // handle incoming non-reply message. To be done in subclasses.
    public abstract SLPMessageHeader handleNonReplyMessage(int function, SLPInputStream sis, boolean isUDP)
            throws ServiceLocationException;

    // logging methods
    protected abstract void logDebug(String message);

    protected abstract void logMessage(String text, SLPMessageHeader message);

    protected abstract void logError(String text, Exception error);

    /** Returns the locale for this agent */
    public Locale getLocale() {
        return locale;
    }

    /** Returns the properties for this agent. */
    public Properties getProperties() {
        return properties;
    }

    /** Returns the scopes supported by this agent. */
    public Vector getScopes() {
        return (Vector) supportedScopes.clone();
    }

    public synchronized void setSFLog(LogSF log) {
        if (CONFIG_SFLOG) {
            if (sflog != null && sflog != log) {
                sflog.info("Warning: Log changed");
            }

            sflog = log;
        } else {
            logError("SmartFrog logging not enabled.", null);
        }
    }
}
