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
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.messages.SLPAttrReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPDAAdvMessage;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.messages.SLPSrvAckMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvDeregMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvRegMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvTypeReqMessage;
import org.smartfrog.services.comm.slp.network.SLPTcpServer;
import org.smartfrog.services.comm.slp.network.SlpMulticastClient;
import org.smartfrog.services.comm.slp.network.SlpUdpCallback;
import org.smartfrog.services.comm.slp.network.SlpUdpClient;
import org.smartfrog.services.comm.slp.network.SlpUnicastClient;
import org.smartfrog.services.comm.slp.util.SLPDefaults;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * This class implements ans SLP Directory Agent. A DA is used as a cache for service advertisements, and provides
 * better scalability to the service location protocol.
 */
public class DirectoryAgent implements SlpUdpCallback, SLPMessageCallbacks {
    /** The service type for the Directory Agent. */
    public static final String DA_TYPE = "service:directory-agent";

    private LogSF sflog = null;

    /** The database in which to store advertisements */
    private SLPDatabase database;

    /** Listens for incoming UDP packets on the unicast address. This is also used for sending replies to messages. */
    private SlpUdpClient unicastListener = null;
    /** Listens for incoming UDP packets on the multicast address. */
    private SlpUdpClient multicastListener = null;
    /** Listens for incoming TCP requests. */
    private SLPTcpServer tcpListener = null;

    // configuration
    private int CONFIG_SLP_PORT = SLPDefaults.DEF_CONFIG_SLP_PORT;
    private String CONFIG_SLP_MC_ADDR = SLPDefaults.DEF_CONFIG_MC_ADDR;
    private int CONFIG_SLP_MTU = SLPDefaults.DEF_CONFIG_MTU;
    private int CONFIG_DA_BEAT = SLPDefaults.DEF_CONFIG_DA_BEAT;
    private boolean CONFIG_DEBUG = SLPDefaults.DEF_CONFIG_DEBUG;
    private boolean CONFIG_LOG_ERRORS = SLPDefaults.DEF_CONFIG_LOG_ERRORS;
    private boolean CONFIG_LOG_MSG = SLPDefaults.DEF_CONFIG_LOG_MSG;
    private String CONFIG_LOGFILE = SLPDefaults.DEF_CONFIG_LOGFILE;
    private boolean CONFIG_SFLOG = SLPDefaults.DEF_CONFIG_SFLOG;

    /** A Vector containing all scopes supported by this DA. */
    private Vector supportedScopes = null;

    /** The IP address of this DA. */
    private InetAddress address;
    /** The multicast address to use for mc requests */
    private InetAddress mcAddr;

    /** A timestamp saying when the DA started. */
    private long bootTime = 0;

    // timer
    private Timer timer = new Timer();

    /** Creates a DirectoryAgent object. The default configuration is used. */
    public DirectoryAgent() throws ServiceLocationException {
        try {
            address = InetAddress.getLocalHost();
            mcAddr = InetAddress.getByName(CONFIG_SLP_MC_ADDR);
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "DA: Failed to resolve inet address", e);
        }

        createDA();
    }

    /**
     * Creates a DirectoryAgent object. Takes its configuration from the provided properties.
     *
     * @param properties The configuration of the DA.
     */
    public DirectoryAgent(Properties properties) throws ServiceLocationException {
        int intValue = 0;
        String stringVal = null;
        try {
            if ((stringVal = properties.getProperty("net.slp.interface")) != null) {
                address = InetAddress.getByName(stringVal);
            } else {
                address = InetAddress.getLocalHost();
            }
            mcAddr = InetAddress.getByName(CONFIG_SLP_MC_ADDR);
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "DA: Failed to resolve inet address", e);
        }
        if ((stringVal = properties.getProperty("net.slp.useScopes")) != null && stringVal.length() != 0) {
            supportedScopes = new Vector();
            String[] scopes = stringVal.split(",");
            for (int i = 0; i < scopes.length; i++) {
                supportedScopes.add(scopes[i]);
            }
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

        createDA();
    }

    /**
     * Does the actual work of creating the DA object from the given properties.
     *
     * @throws ServiceLocationException if creation doesnt work
     */
    private void createDA() throws ServiceLocationException {
        bootTime = (new Date()).getTime();
        database = new SLPDatabase();
        try {

            unicastListener = new SlpUnicastClient(address, CONFIG_SLP_PORT, true,
                    CONFIG_SLP_MTU, this);

            multicastListener = new SlpMulticastClient(CONFIG_SLP_MC_ADDR, address, CONFIG_SLP_PORT,
                    CONFIG_SLP_MTU, this);

            tcpListener = new SLPTcpServer(address, CONFIG_SLP_PORT, this);
            tcpListener.start();

        } catch (Exception e) {
            if (unicastListener != null) unicastListener.close();
            if (multicastListener != null) multicastListener.close();
            if (e instanceof ServiceLocationException) {
                throw (ServiceLocationException) e;
            }
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "DA: Could not create unicast/multicast sockets"
                    , e);
        }
        if (supportedScopes == null) {
            supportedScopes = new Vector();
            supportedScopes.add("default");
        }

        // create DA advert timer.
        timer.schedule(new TimerTask() {
            public void run() {
                sendDAAdvert();
            }
        }, 1000, CONFIG_DA_BEAT);
    }

    /*************************************/
    /******* Callback methods ************/
    /** ********************************* */
    public boolean udpReceived(DatagramPacket packet) {
        // create input stream, and check version.
        SLPInputStream sis = new SLPInputStream(new ByteArrayInputStream(packet.getData()));
        int version = 0;
        int function = 0;
        try {
            version = sis.readByte();
            function = sis.readByte();
        } catch (IOException ioe) {
            version = function = 0;
        }
        if (version != 2) {
            // unsupported version
            handleUnsupportedVersion(sis, packet);
        } else {
            try {
                SLPMessageHeader reply = handleNonReplyMessage(function, sis, true);
                if (reply != null) {
                    logMessage("Sending Message:", reply);
                    sendServiceReply(reply, packet.getAddress(), packet.getPort());
                }
            } catch (ServiceLocationException e) {
                logError("Error parsing message: ", e);
            }
        }

        return true;
    }

    public boolean udpError(Exception e) {
        logError("System Error: ", e);
        return false;
    }

    public boolean udpTimeout() {
        logError("Network timeout. Should never happen!", null);
        return false;
    }

    /************************************/
    /********** Request handlers ********/
    /************************************/

    /**
     * Called whenever a request with the wrong SLP version is received. Currently does nothing but write an entry in
     * the log if logging is switched on.
     */
    private void handleUnsupportedVersion(SLPInputStream sis, DatagramPacket packet) {
        logError("DA ERROR: Unsupported SLP Version", null);
    }

    public SLPMessageHeader handleNonReplyMessage(int function, SLPInputStream sis, boolean isUDP)
            throws ServiceLocationException {

        //handle requests here...
        switch (function) {
            case SLPMessageHeader.SLPMSG_SRVREQ:
                return handleServiceRequest(sis, isUDP);
            case SLPMessageHeader.SLPMSG_SRVTYPE:
                return handleServiceTypeRequest(sis, isUDP);
            case SLPMessageHeader.SLPMSG_ATTRREQ:
                return handleAttrRequest(sis, isUDP);
            case SLPMessageHeader.SLPMSG_SRVREG:
                return handleServiceRegistration(sis, isUDP);
            case SLPMessageHeader.SLPMSG_SRVDEREG:
                return handleServiceDeregistration(sis, isUDP);
        }

        logError("Received Unsupported request: " + function, null);
        return null;
    }

    public boolean handleReplyMessage(int function, SLPInputStream sis,
                                      ServiceLocationEnumeration results)
            throws ServiceLocationException {

        // handle replies here... (do nothing)
        return true;
    }

    /**
     * Called when a service request is received.
     *
     * @param sis   The SLPInputStream to read the message from.
     * @param isUDP Set to 'true' if the message was received by the UDP listener.
     * @return A reply to the service request if one is to be sent. null otherwise.
     */
    private SLPMessageHeader handleServiceRequest(SLPInputStream sis, boolean isUDP) {
        // create the message object
        SLPSrvReqMessage msg = new SLPSrvReqMessage();
        int error = 0;
        try {
            msg.fromInputStream(sis);
            if (msg.getPRList().indexOf(address.getHostAddress()) != -1) {
                logDebug("Has replied before...");
                return null;
            }
        } catch (ServiceLocationException sle) {
            // parse error.
            // if we received a unicast request, we should reply with an error
            error = sle.getErrorCode();
        }

        logMessage("Received Message:", msg);

        if (error == 0) {
            if (msg.getScopes().size() == 0 || SLPUtil.supportScopes(supportedScopes, msg.getScopes())) {
                // if the request is for the directory-agent service, we need to return
                // a DAAdvert. (If there was no error and we support the requested scope(s))
                if (msg.getServiceType().toString().equalsIgnoreCase(DA_TYPE)) {
                    return createDAAdvert(msg.getLanguage(), msg.getXID());
                }
            }//scopes
            else {
                logError("Scope NOT supported: " + msg.getScopes().toString(), null);
                if (msg.getScopes().isEmpty()) logDebug("No scope list provided");
                logDebug("num scopes: " + msg.getScopes().size());
                error = ServiceLocationException.SCOPE_NOT_SUPPORTED;
            }
        }//error

        // We have a normal service request, or there was an error.
        return SLPRequestHandlers.handleServiceRequest(database, msg, error,
                CONFIG_SLP_MTU,
                isUDP);
    }

    /**
     * Called when a service type request is received.
     *
     * @param sis   The SLPInputStream to read the message from.
     * @param isUDP Set to 'true' if the message was received on the UDP listener.
     * @return A reply to the request, or null if no reply is to be sent.
     */
    private SLPMessageHeader handleServiceTypeRequest(SLPInputStream sis, boolean isUDP) {
        int error = 0;
        SLPSrvTypeReqMessage msg = new SLPSrvTypeReqMessage();
        try {
            msg.fromInputStream(sis);
            // if we have replied to this message before: do nothing.
            if (msg.getPRList().indexOf(address.getHostAddress()) != -1) {
                return null; //have replied before...
            }
        } catch (ServiceLocationException e) {
            logError("Error parsing message", e);
            error = e.getErrorCode();
        }
        if (error == 0) {
            // check scopes...
            if (!SLPUtil.supportScopes(supportedScopes, msg.getScopes())) {
                error = ServiceLocationException.SCOPE_NOT_SUPPORTED;
            }
        }//error

        // handle request
        return SLPRequestHandlers.handleServiceTypeRequest(database,
                msg,
                error,
                CONFIG_SLP_MTU,
                isUDP);
    }

    /**
     * Called when an attribute request is received.
     *
     * @param sis   The stream to read the message from.
     * @param isUDP True if message was received using UDP.
     * @return A reply to the message, if any.
     */
    private SLPMessageHeader handleAttrRequest(SLPInputStream sis, boolean isUDP) {
        int error = 0;
        SLPAttrReqMessage msg = new SLPAttrReqMessage();
        try {
            msg.fromInputStream(sis);
            // if we have replied to this message before: do nothing.
            if (msg.getPRList().indexOf(address.getHostAddress()) != -1) {
                return null; //have replied before...
            }
        } catch (ServiceLocationException e) {
            logError("Error parsing message", e);
            error = e.getErrorCode();
        }
        if (error == 0) {
            // check scopes...
            if (!SLPUtil.supportScopes(supportedScopes, msg.getScopes())) {
                error = ServiceLocationException.SCOPE_NOT_SUPPORTED;
            }
        }//error

        // handle request
        return SLPRequestHandlers.handleAttributeRequest(database,
                msg,
                error,
                CONFIG_SLP_MTU,
                isUDP);
    }

    /**
     * Called when a service registration is received.
     *
     * @param sis   The SLPInputStream to read the message from.
     * @param isUDP Set to 'true' if the message was received on the UDP listener.
     * @return A SrvAck message saying if the registration went ok or not.
     */
    private SLPMessageHeader handleServiceRegistration(SLPInputStream sis, boolean isUDP) {
        SLPSrvRegMessage msg = new SLPSrvRegMessage();
        SLPMessageHeader reply = null;
        int error = 0;
        try {
            msg.fromInputStream(sis);
        } catch (ServiceLocationException sle) {
            error = sle.getErrorCode();
        }

        logMessage("Received Message:", msg);

        // handle registration
        if (error == 0) {
            Vector s = SLPUtil.findCommonScopes(supportedScopes, msg.getScopes());
            if (msg.getScopes().size() == s.size()) {
                // register message.
                if ((msg.getFlags() & SLPMessageHeader.FLAG_FRESH) != 0) {
                    SLPDatabaseEntry newReg = new SLPDatabaseEntry(msg.getURL(),
                            msg.getAttributes(),
                            msg.getScopes(),
                            msg.getLanguage());
                    database.removeEntry(newReg);
                    database.addEntry(newReg);
                } else {
                    // currently only handle fresh registrations...
                    error = ServiceLocationException.INVALID_REGISTRATION;
                }
            } else {
                error = ServiceLocationException.SCOPE_NOT_SUPPORTED;
            }
        }
        reply = new SLPSrvAckMessage(error, msg.getLanguage());
        reply.setXID(msg.getXID());

        return reply;
    }

    /**
     * Called when a service deregistration is received.
     *
     * @param sis   The SlpInputStream to read the message from.
     * @param isUDP Set to 'true' if the message was received on the UDP listener.
     * @return a SrvAck saying if the deregistration went ok or not.
     */
    private SLPMessageHeader handleServiceDeregistration(SLPInputStream sis, boolean isUDP) {
        SLPSrvDeregMessage msg = new SLPSrvDeregMessage();
        SLPMessageHeader reply = null;
        int error = 0;
        try {
            msg.fromInputStream(sis);
        } catch (ServiceLocationException sle) {
            error = sle.getErrorCode();
        }

        logMessage("Received Message:", msg);

        if (error == 0) {
            ServiceURL url = msg.getURL();
            boolean removed = database.removeEntry(url, msg.getScopes());
            if (!removed) error = ServiceLocationException.INVALID_REGISTRATION;
        }

        reply = new SLPSrvAckMessage(error, msg.getLanguage());
        reply.setXID(msg.getXID());

        return reply;
    }

    /**
     * Creates a DAAdvert message with information about this DA.
     *
     * @param lang The locale for the message
     * @param id   The XID to use for the message.
     * @return a new DAAdvMessage.
     */
    private SLPMessageHeader createDAAdvert(Locale lang, int id) {
        // create timestamp.
        int ts = 0;
        if (bootTime != -1) {
            long time = (new Date()).getTime();
            ts = (int) (time - bootTime);
        }
        // create DAAdvert message
        ServiceURL theUrl = new ServiceURL(DA_TYPE + "://" + address.getHostAddress());
        SLPDAAdvMessage m = new SLPDAAdvMessage(theUrl, supportedScopes, new Vector(), ts, lang);
        m.setXID(id);
        return m;
    }

    /**
     * Sends a service reply to the given address.
     *
     * @param m         The reply to send.
     * @param toAddress The address to send to.
     * @param toPort    The port to send to.
     */
    private void sendServiceReply(SLPMessageHeader m, InetAddress toAddress, int toPort) {
        try {
            logDebug("DA -> Sending to: " + toAddress.getHostAddress() + " - " + toPort);
            DatagramPacket toSend = SLPUtil.createDatagram(m, toAddress, toPort);
            unicastListener.send(toSend);
            logDebug("DA -> Reply sent!");
        } catch (Exception e) {
            logError("Failed to send reply", null);
        }
    }

    // write to log
    /*
    protected void writeLog(String message) {
        String toWrite;
        toWrite = "------ DirectoryAgent ------\n";
        toWrite += message + "\n";
        toWrite += "------ End ------";
        SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
    }
    */

    protected void logDebug(String message) {
        if (CONFIG_DEBUG) {
            String toWrite;
            toWrite = "------ SLP DirectoryAgent ------\n"
                    + message + "\n"
                    + "--------------------------------";
            if (sflog == null) {
                SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
            } else {
                sflog.info("\n" + toWrite);
            }
        }
    }

    protected void logMessage(String text, SLPMessageHeader message) {
        if (CONFIG_LOG_MSG) {
            String toWrite;
            toWrite = "------ SLP DirectoryAgent ------\n"
                    + text + "\n";
            if (message != null) toWrite += message.toString() + "\n";
            toWrite += "--------------------------------";

            if (sflog == null) {
                SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
            } else {
                sflog.info("\n" + toWrite);
            }
        }
    }

    protected void logError(String text, Exception error) {
        if (CONFIG_LOG_ERRORS) {
            String toWrite;
            toWrite = "------ SLP DirectoryAgent ------\n"
                    + text + "\n";
            if (error != null) toWrite += error.toString();
            toWrite += "--------------------------------";

            if (sflog == null) {
                SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
            } else {
                sflog.error("\n" + text, error);
            }
        }
    }

    /** Called when an unsolicited DAAdvert is to be sent. This is done every CONFIG_DA_BEAT seconds. */
    private void sendDAAdvert() {
        SLPMessageHeader m = createDAAdvert(new Locale(SLPDefaults.DEF_CONFIG_LOCALE), 0);
        DatagramPacket toSend = SLPUtil.createDatagram(m, mcAddr, CONFIG_SLP_PORT);
        try {
            unicastListener.send(toSend);
        } catch (ServiceLocationException e) {
            logError("Failed to send DA Advert.", null);
        }
    }

    /** Shuts down the DA. Sends a DAAdvert to notify other agents that it is going down, and closes all sockets. */
    public void killDA() {
        bootTime = -1;
        sendDAAdvert(); // notify agents that we are going down

        unicastListener.close();
        multicastListener.close();
        tcpListener.stopThread();
        unicastListener = null;
        multicastListener = null;
        tcpListener = null;
        timer.cancel();
        timer = null;
    }

    public void finalize() {
        logDebug("DA is dead !");
    }

    public synchronized void setSFLog(LogSF log) {
        if (CONFIG_SFLOG) {
            if (sflog != null) {
                sflog.info("Warning: Log Changed");
            }
            sflog = log;
        } else {
            logError("SmartFrog Logging not Enabled", null);
        }
    }

    /** Simple main method to start a DA. */
    public static void main(String[] args) {
        try {
            DirectoryAgent a = new DirectoryAgent();
            synchronized (a) {
                a.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
