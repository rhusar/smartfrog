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

import org.smartfrog.services.comm.slp.Advertiser;
import org.smartfrog.services.comm.slp.ServiceLocationAttribute;
import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.messages.SLPAttrReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.messages.SLPSAAdvMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvAckMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvDeregMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvRegMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvTypeReqMessage;
import org.smartfrog.services.comm.slp.network.SLPTcpClient;
import org.smartfrog.services.comm.slp.network.SLPTcpServer;
import org.smartfrog.services.comm.slp.network.SlpMulticastClient;
import org.smartfrog.services.comm.slp.util.ParseTree;
import org.smartfrog.services.comm.slp.util.SLPDefaults;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/** Implements an SLP Service Agent. */
public class ServiceAgent extends SLPAgent implements Advertiser {
    public static final String SA_TYPE = "service:service-agent";

    /** The database to use for registrations */
    private SLPDatabase database;

    /** The thread handling the registration of services with the known DAs. */
    private SARegistrationThread regThread;

    /** Listens for incoming TCP requiests. */
    SLPTcpServer tcpListener = null;

    /*******************************************/
    /************ Constructors *****************/
    /*******************************************/
    /** Creates a new ServiceAgent using only default values. */
    public ServiceAgent() throws ServiceLocationException {
        CONFIG_SLP_AGENTPORT = SLPDefaults.DEF_CONFIG_SAPORT;
        createAgent(null);
    }

    /**
     * Creates a new ServiceAgent with the specified properties.
     *
     * @param properties The properties to use for this SA.
     * @throws ServiceLocationException for trouble
     */
    public ServiceAgent(Properties properties) throws ServiceLocationException {
        super(properties);
        CONFIG_SLP_AGENTPORT = SLPDefaults.DEF_CONFIG_SAPORT;
        // set values from properties
        int intValue = 0;
        String stringVal = null;
        if ((stringVal = properties.getProperty("net.slp.saport")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_SLP_AGENTPORT = intValue;
            } catch (NumberFormatException nfe) {
            }
        }

        stringVal = properties.getProperty("net.slp.DAAddresses");
        // run standard construction code
        createAgent(stringVal);
    }

    /** Does the actual creation of the SA. Uses the values set in the constructors. */
    protected void createAgent(String DAsToUse) throws ServiceLocationException {
        // create addresses and listeners...
        try {
            // create multicast listener
            multicastCommunicator = new SlpMulticastClient(
                    CONFIG_SLP_MC_ADDR, address, CONFIG_SLP_PORT,
                    CONFIG_SLP_MTU, this);
        } catch (Exception e) {
            if (multicastCommunicator != null) multicastCommunicator.close();
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "SA: Could not create multicast socket");
        }

        database = new SLPDatabase();
        regThread = new SARegistrationThread(this, database);
        regThread.start();

        // let superclass handle the rest.
        super.createAgent(DAsToUse);

        // try to start TCP server...
        try {
            tcpListener = new SLPTcpServer(address, unicastCommunicator.getPort(), this);
            tcpListener.start();
        } catch (Exception e) {
            //e.printStackTrace();
            tcpListener = null;
        }
    }

    /*******************************************/
    /********** Advertiser methods *************/
    /** *************************************** */
    public void register(ServiceURL URL,
                         Vector serviceLocationAttributes)
            throws ServiceLocationException {

        SLPDatabaseEntry newEntry = new SLPDatabaseEntry(URL, serviceLocationAttributes, null, locale);
        database.removeEntry(URL, locale); // in case this is an update
        database.addEntry(newEntry);
        regThread.newRegistration(newEntry);
    }

    public void deregister(ServiceURL URL) throws ServiceLocationException {
        boolean removed = database.removeEntry(URL, (Vector) null);
        if (removed) {
            regThread.delRegistration(URL);
        }
    }

    public void addAttributes(ServiceURL URL,
                              Vector serviceLocationAttributes)
            throws ServiceLocationException {

        throw new ServiceLocationException(ServiceLocationException.NOT_IMPLEMENTED,
                "Method addAttributes is not implemented.");
    }

    public void deleteAttributes(ServiceURL URL,
                                 Vector attributeIds) throws ServiceLocationException {
        throw new ServiceLocationException(ServiceLocationException.NOT_IMPLEMENTED,
                "Method deleteAttributes is not implemented");
    }

    /******************************************/
    /********** Additional Methods ************/
    /******************************************/

    /**
     * Handles incoming replies. The message should be either DAAdv or SrvAck. If it is not one of those, the message is
     * simply ignored.
     *
     * @param function The type of message
     * @param sis      The input stream to read the message from
     * @param results  The enumaration holding the results.
     */
    public boolean handleReplyMessage(int function, SLPInputStream sis,
                                      ServiceLocationEnumeration results) throws ServiceLocationException {
        switch (function) {
            case SLPMessageHeader.SLPMSG_DAADV:
                recvDAAdvert(sis);
                break;
            case SLPMessageHeader.SLPMSG_SRVACK:
                recvSrvAck(sis);
                break;
            default:
                logError("handleReplyMessage: Unknown message type (" + function + ")", null);
                break;
        }
        return true; // Not going to get partial messages here...
    }

    /**
     * Handles incoming messages on the multicast or unicast listeners on the SLP port.
     *
     * @param function The function describing the message type.
     * @param sis      The input stream to read the message from.
     * @param isUDP    Set to 'true' if message was received on UDP listener.
     */
    public SLPMessageHeader handleNonReplyMessage(int function, SLPInputStream sis, boolean isUDP) throws ServiceLocationException {
        switch (function) {
            case SLPMessageHeader.SLPMSG_SRVREQ:
                return recvSrvReqMessage(sis, isUDP);
            case SLPMessageHeader.SLPMSG_SRVTYPE:
                return recvSrvTypeReqMessage(sis, isUDP);
            case SLPMessageHeader.SLPMSG_ATTRREQ:
                return recvAttrReqMessage(sis, isUDP);
            case SLPMessageHeader.SLPMSG_DAADV:
                if (CONFIG_PASSIVE_DA) {
                    recvDAAdvert(sis);
                    return null; // we never reply to a DAAdvert
                }
                break;
            default:
                logError("handleNonReplyMessage: Unknown message type (" + function + ")", null);
                break;
        }
        return null;
    }

    /**
     * Take the appropriate action after receiving a SrvReq message
     *
     * @param sis    The input stream to read the received message from
     * @param packet The datagram packet containing the recieved message.
     */
    private SLPMessageHeader recvSrvReqMessage(SLPInputStream sis, boolean isUDP) {
        int error = 0;
        SLPSrvReqMessage msg = new SLPSrvReqMessage();
        try {
            msg.fromInputStream(sis);
        } catch (ServiceLocationException e) {
            logError("Error parsing message", e);
            error = e.getErrorCode();
        }
        if (error == 0) {
            logMessage("Received Message:", msg);
            // if we have replied to this message before: do nothing.
            if (msg.getPRList().indexOf(address.getHostAddress()) != -1) {
                return null; //have replied before...
            }

            // check that at least one requested scope is supported.
            if (msg.getScopes().isEmpty() || SLPUtil.supportScopes(supportedScopes, msg.getScopes())) {
                // handle service:service-agent requests here...
                if (msg.getServiceType().toString().equalsIgnoreCase(SA_TYPE)) {
                    return handleSAAdvertReq(msg);
                }
            } else {
                error = ServiceLocationException.SCOPE_NOT_SUPPORTED;
            }
        }//error

        SLPMessageHeader reply = SLPRequestHandlers.handleServiceRequest(database,
                msg,
                error,
                CONFIG_SLP_MTU,
                isUDP);

        return reply;
    }

    /**
     * Called when a service type request is received.
     *
     * @param sis   The SLPInputStream to read the message from.
     * @param isUDP set to 'true' if the message was received on the UDP listener.
     * @return A reply to the request, or null if no reply is to be sent.
     */
    private SLPMessageHeader recvSrvTypeReqMessage(SLPInputStream sis, boolean isUDP) {
        int error = 0;
        SLPSrvTypeReqMessage msg = new SLPSrvTypeReqMessage();
        try {
            msg.fromInputStream(sis);
        } catch (ServiceLocationException e) {
            logError("Error parsing message", e);
            error = e.getErrorCode();
        }
        if (error == 0) {
            logMessage("Received Message: ", msg);
            // if we have replied to this message before: do nothing.
            if (msg.getPRList().indexOf(address.getHostAddress()) != -1) {
                return null; //have replied before...
            }

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
     * @param sis   The input stream to read the message from.
     * @param isUDP True if the  message was received by UDP.
     * @return A reply to the request.
     */
    private SLPMessageHeader recvAttrReqMessage(SLPInputStream sis, boolean isUDP) {
        int error = 0;
        SLPAttrReqMessage msg = new SLPAttrReqMessage();
        try {
            msg.fromInputStream(sis);
        } catch (ServiceLocationException e) {
            logError("Error parsing message", e);
            error = e.getErrorCode();
        }
        if (error == 0) {
            logMessage("Received Message: ", msg);
            // if we have replied to this message before: do nothing.
            if (msg.getPRList().indexOf(address.getHostAddress()) != -1) {
                return null; //have replied before...
            }
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
     * Handles incoming DAAdverts. The information for the DA will be put into a DAInfo object, which is then added to
     * the list of known DAs. The method also notifies the registration thread about the newly discovered DA.
     */
    protected DAInfo recvDAAdvert(SLPInputStream sis) throws ServiceLocationException {
        //System.out.println("ServiceAgent -> Received DAAdvert !");
        DAInfo newDA = super.recvDAAdvert(sis);
        if (newDA != null) {
            // set the scope list to be the scopes supported by both the DA and the SA.
            // This is the scopes in which we register our services with the DA.
            newDA.setScopes(SLPUtil.findCommonScopes(newDA.getScopes(), supportedScopes));
            // Tell the registration thread that we have a new DA to register with.
            regThread.newDA(newDA);
        }

        return null;
    }

    /**
     * Handles incoming SrvAck messages.
     *
     * @param sis incoming message
     * @return error code
     * @throws ServiceLocationException
     */
    private int recvSrvAck(SLPInputStream sis) throws ServiceLocationException {
        SLPSrvAckMessage msg = new SLPSrvAckMessage();
        msg.fromInputStream(sis);

        int error = msg.getErrorCode();

        logMessage("Received Message:", msg);

        return error;
    }

    /** Handles SAAdvert requests. */
    private SLPMessageHeader handleSAAdvertReq(SLPSrvReqMessage msg) {

        if (!msg.getSearchFilter().equals("")) {
            // build the service-types attribute
            Vector stypes = database.findServiceTypes(null);
            Vector values = new Vector();
            for (Iterator it = stypes.iterator(); it.hasNext();) {
                values.add(it.next().toString());
            }

            ServiceLocationAttribute sla = new ServiceLocationAttribute("service-types", values);
            stypes = new Vector();
            stypes.add(sla);

            // check that we have the requested attributes/values.
            ParseTree parseTree = new ParseTree();
            parseTree.buildTree(msg.getSearchFilter());
            if (!parseTree.evaluate(stypes)) {
                return null; // do not reply
            }
        }

        // create service-url
        ServiceURL url = new ServiceURL(SA_TYPE + "://" + address);

        // create message
        SLPSAAdvMessage reply = new SLPSAAdvMessage(url, supportedScopes, new Vector(), msg.getLanguage());
        reply.setXID(msg.getXID());

        return reply;
    }

    /************************************/
    /********** DA Registration *********/
    /************************************/
    /**
     * Registers the given service with the given da.
     *
     * @param url   The service URL to register
     * @param attrs The attributes for the service
     * @param da    The DA to register with.
     */
    protected boolean registerService(ServiceURL url, Vector attrs, DAInfo da) {

        Vector scopelist = da.getScopes();
        SLPSrvRegMessage msg = new SLPSrvRegMessage(url, scopelist, attrs, locale);
        msg.setFlags(SLPMessageHeader.FLAG_FRESH);
        if (msg.getLength() <= CONFIG_SLP_MTU) {
            logDebug("Service Reg to DA Using UDP");
            SLPMessageSender ms = new SLPMessageSender(this, CONFIG_SLP_MTU, CONFIG_RETRY, null);
            try {
                ms.sendSLPMessage(msg,
                        da.getHost(), da.getPort(), CONFIG_RETRY_MAX,
                        unicastCommunicator);
            } catch (ServiceLocationException se) {
                logError("sending message with UDP", se);
                if (se.getErrorCode() == ServiceLocationException.DA_NOT_AVAILABLE) {
                    // DA is most likely down...
                    DAs.remove(da.getHost());
                    return false;
                } else {
                    //some other error
                    //fall through
                }
            }
        } else {
            logDebug("Service Reg to DA Using TCP");
            SLPTcpClient tcp = new SLPTcpClient(this);
            try {
                tcp.sendSlpMessage(msg, da.getHost(), da.getPort(), null);
            } catch (IOException e) {
                // DA is most likely down...
                logError("sending message with TCP", e);
                return false;
            } catch (ServiceLocationException e) {
                logError("sending message with TCP", e);
                return false;
            }
        }
        return true;
    }

    /**
     * Deregisters a service from a DA
     *
     * @param url The Service URL o deregister.
     * @param da  The da to deregister with
     */
    protected boolean deregisterService(ServiceURL url, DAInfo da) {
        SLPSrvDeregMessage msg = new SLPSrvDeregMessage(url, da.getScopes());
        SLPMessageSender ms = new SLPMessageSender(this, CONFIG_SLP_MTU, CONFIG_RETRY, null);
        try {
            ms.sendSLPMessage(msg,
                    da.getHost(), da.getPort(), CONFIG_RETRY_MAX,
                    unicastCommunicator);
        } catch (ServiceLocationException se) {
            // DA down...
            DAs.remove(da.getHost());
            return false;
        }

        return true;
    }

    /** returns the list of known DAs */
    protected Vector getDAList() {
        return new Vector(DAs.values());
    }

    /*
     protected void writeLog(String message) {
         String toWrite;
         toWrite = "------ ServiceAgent ------\n";
         toWrite += message + "\n";
         toWrite += "------ End ------";
         SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
     }
     */
    protected void logDebug(String message) {
        if (CONFIG_DEBUG) {
            String toWrite;
            toWrite = "------ SLP ServiceAgent ------\n"
                    + message + "\n"
                    + "------------------------------";
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
            toWrite = "------ SLP ServiceAgent ------\n"
                    + text + "\n";
            if (message != null) toWrite += message.toString() + "\n";
            toWrite += "------------------------------";

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
            toWrite = "------ SLP ServiceAgent ------\n"
                    + text + "\n";
            if (error != null) toWrite += error.toString();
            toWrite += "------------------------------";

            if (sflog == null) {
                SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
            } else {
                sflog.error("\n" + text, error);
            }
        }
    }
}

