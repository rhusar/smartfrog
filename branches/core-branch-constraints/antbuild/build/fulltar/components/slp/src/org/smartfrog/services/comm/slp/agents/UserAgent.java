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

import org.smartfrog.services.comm.slp.Locator;
import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.ServiceType;
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.messages.SLPAttrReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPAttrRplyMessage;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.messages.SLPSAAdvMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvRplyMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvTypeReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvTypeRplyMessage;
import org.smartfrog.services.comm.slp.network.SlpMulticastClient;
import org.smartfrog.services.comm.slp.network.SlpUdpCallback;
import org.smartfrog.services.comm.slp.util.SLPDefaults;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;
import org.smartfrog.services.comm.slp.util.ServiceAttributeEnumeration;
import org.smartfrog.services.comm.slp.util.ServiceTypeEnumeration;
import org.smartfrog.services.comm.slp.util.ServiceURLEnumeration;

import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 * The User Agent is the SLP agent for locating services. It implements the Locator interface. Programs that want to use
 * the library for service discovery should use the Locator interface methods in order to do so. No other methods of
 * this class are intended to be used outside the library
 */
public class UserAgent extends SLPAgent implements Locator, SlpUdpCallback {

    private boolean userSelectableScopes = false;

    /**********************************************/
    /************* Constructors *******************/
    /**********************************************/

    /** Default constructor. This will create a new UserAgent object using only the default values. */
    public UserAgent() throws ServiceLocationException {
        CONFIG_SLP_AGENTPORT = SLPDefaults.DEF_CONFIG_UAPORT;
        createAgent(null);
    }

    public UserAgent(Properties properties) throws ServiceLocationException {
        super(properties);
        CONFIG_SLP_AGENTPORT = SLPDefaults.DEF_CONFIG_UAPORT;
        String stringVal = null;
        int intValue;
        if ((stringVal = properties.getProperty("net.slp.uaport")) != null) {
            try {
                intValue = Integer.parseInt(stringVal);
                CONFIG_SLP_AGENTPORT = intValue;
            } catch (NumberFormatException nfe) {
            }
        }

        stringVal = properties.getProperty("net.slp.DAAddresses");

        // if we are created with no scope list, set userSelectableScopes true.
        if (supportedScopes == null) {
            userSelectableScopes = true;
            supportedScopes = new Vector();
        }

        // run standard construction code
        createAgent(stringVal);
    }

    protected void createAgent(String DAsToUse) throws ServiceLocationException {
        try {
            // start multicast listener (if needed)
            if (CONFIG_PASSIVE_DA) {
                multicastCommunicator = new SlpMulticastClient(
                        CONFIG_SLP_MC_ADDR, address, CONFIG_SLP_PORT,
                        CONFIG_SLP_MTU, this);
            }
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "UA: Could not create unicast socket");
        }
        // call super class to do the rest...
        super.createAgent(DAsToUse);

        // if the UA is configured with no scope list: find scopes from SAs.
        if (userSelectableScopes) {
            sendSARequest();
        }
    }

    /****************************************************/
    /************** Locator Methods *********************/
    /****************************************************/

    /**
     * Find a service. This method is called whenever one wants to locate a particular service. The method will send a
     * request to a DA or to the multicast address and wait for the results. The method will block until the results are
     * available, an error occurs or the operation times out.
     *
     * @param type   - A ServiceType object specifying the type of service to find.
     * @param scopes - A list of scopes to search for the service in.
     * @param filter - An LDAPv3 filter to limit the number of results. May be an empty String.
     * @return A ServiceLocationEnumeration of the results, or null if no match could be found.
     */
    public ServiceLocationEnumeration findServices(
            ServiceType type,
            Vector scopes,
            String filter)
            throws ServiceLocationException {
        ServiceLocationEnumeration results = new ServiceURLEnumeration();

        // check that the UA supports the scopes given.
        Vector scopesToUse = getSupportedScopes(scopes);
        if (scopesToUse.isEmpty()) {
            throw new ServiceLocationException(ServiceLocationException.SCOPE_NOT_SUPPORTED,
                    "UA: Requested scope is NOT supported");
        }

        SLPSrvReqMessage req = new SLPSrvReqMessage(type, scopesToUse, filter, locale);

        sendRequest(req, scopesToUse, results);

        // return the discovered service URLs
        return results;
    }

    // find service types
    public ServiceLocationEnumeration findServiceTypes(
            String NA,
            Vector scopes)
            throws ServiceLocationException {
        ServiceLocationEnumeration results = new ServiceTypeEnumeration();

        // check that the UA supports the scopes given.
        Vector scopesToUse = getSupportedScopes(scopes);
        if (scopesToUse.isEmpty()) {
            throw new ServiceLocationException(ServiceLocationException.SCOPE_NOT_SUPPORTED,
                    "UA: Requested scope is NOT supported");
        }

        // create request...
        SLPSrvTypeReqMessage req = new SLPSrvTypeReqMessage(NA, scopesToUse, locale);

        sendRequest(req, scopesToUse, results);

        // return the discovered service types
        return results;
    }

    /**

     */
    public ServiceLocationEnumeration findAttributes(
            ServiceType type,
            Vector scopes,
            Vector attrIds)
            throws ServiceLocationException {

        return findAttributes(type.toString(), scopes, attrIds);
    }

    /**

     */
    public ServiceLocationEnumeration findAttributes(
            ServiceURL url,
            Vector scopes,
            Vector attrIds)
            throws ServiceLocationException {

        return findAttributes(url.toString(), scopes, attrIds);
    }

    /**********************************************/
    /*********** Helper Methods *******************/
    /**********************************************/

    /**
     * Received Service Reply. This method is called from the receive callback whenever a SrvRply message has arrived.
     * The method adds the received ServiceURLs to the ServiceURLEnumeration that is to be returned when service
     * discovery is completed.
     */
    private boolean recvSrvReply(SLPSrvRplyMessage msg,
                                 SLPInputStream sis,
                                 ServiceLocationEnumeration results) throws ServiceLocationException {
        msg.fromInputStream(sis);

        logMessage("Received Message:", msg);

        boolean complete = true;

        if (results == null) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }

        if (msg.getErrorCode() == 0) {
            ((ServiceURLEnumeration) results).addElements(msg.getURLs());

            if ((msg.getFlags() & SLPMessageHeader.FLAG_OVERFLOW) != 0x0) {
                complete = false;
            }
        }

        return complete;
    }

    /**
     * Received Service type reply. This method is called whenever a SrvTypeRply message has arrived. The discovered
     * service types are added to the result.
     */
    private boolean recvSrvTypeReply(SLPSrvTypeRplyMessage msg,
                                     SLPInputStream sis,
                                     ServiceLocationEnumeration results) throws ServiceLocationException {

        // read message...
        msg.fromInputStream(sis);
        logMessage("Received Message:", msg);

        boolean complete = true;
        if (results == null) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }

        if (msg.getErrorCode() == 0) {
            ((ServiceTypeEnumeration) results).addElements(msg.getServiceTypes());

            if ((msg.getFlags() & SLPMessageHeader.FLAG_OVERFLOW) != 0x0) {
                complete = false;
            }
        }

        return complete;
    }

    /** Reveiced attribute reply. */
    private boolean recvAttrReply(SLPAttrRplyMessage msg, SLPInputStream sis,
                                  ServiceLocationEnumeration results) throws ServiceLocationException {

        // read message
        msg.fromInputStream(sis);
        logMessage("Received Messaage: ", msg);

        boolean complete = true;
        if (results == null) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }

        if (msg.getErrorCode() == 0) {
            ((ServiceAttributeEnumeration) results).addElements(msg.getAttributes());

            if ((msg.getFlags() & SLPMessageHeader.FLAG_OVERFLOW) != 0x0) {
                complete = false;
            }
        }

        return complete;
    }

    protected DAInfo recvDAAdvert(SLPInputStream sis) throws ServiceLocationException {
        DAInfo newDA = super.recvDAAdvert(sis);
        if (newDA != null && userSelectableScopes) {
            // Add the scopes supported by the DA to our list of known scopes.
            for (Iterator it = newDA.getScopes().iterator(); it.hasNext();) {
                String s = (String) it.next();
                if (!supportedScopes.contains(s)) {
                    supportedScopes.add(s);
                }
            }
        }
        return null;
    }

    private void recvSAAdvert(SLPInputStream sis) throws ServiceLocationException {
        SLPSAAdvMessage msg = new SLPSAAdvMessage();
        msg.fromInputStream(sis);

        logMessage("Received Message:", msg);

        // add scopes...
        if (userSelectableScopes) {
            for (Iterator it = msg.getScopes().iterator(); it.hasNext();) {
                String s = (String) it.next();
                if (!supportedScopes.contains(s)) {
                    supportedScopes.add(s);
                }
            }
        }
    }

    /**
     * Finds the list of DAs we need to send the request to in order to cover all scopes.
     *
     * @param scopes The scopes we want to search in.
     * @return A vector of DAInfo objects for the DAs to send to.
     */
    private Vector findReceivers(Vector scopes) {
        Vector toReturn = new Vector();
        Iterator iter = DAs.values().iterator();
        DAInfo da;
        while (iter.hasNext() && !scopes.isEmpty()) {
            da = (DAInfo) iter.next();
            scopes.removeAll(da.getScopes());
            toReturn.add(da);
        }

        if (!scopes.isEmpty()) {
            // Can't find a DA for all scopes. Need to use multicast as well
            DAInfo mcast = new DAInfo(CONFIG_SLP_MC_ADDR, CONFIG_SLP_PORT);
            toReturn.add(mcast);
        }

        return toReturn;
    }

    /**
     * Handles incoming multicast messages. This is called by the receive callback when a message is received on the
     * SlpUdpClient litening to the multicast address.
     */
    public SLPMessageHeader handleNonReplyMessage(int function, SLPInputStream sis, boolean isUDP) throws ServiceLocationException {
        switch (function) {
            case SLPMessageHeader.SLPMSG_DAADV:
                recvDAAdvert(sis);
                break;
        }
        return null; // UA never sends a reply
    }

    /**
     * Handles incoming unicast messages. This is called by the receive callback when a message is received on the
     * fabricClient listening to the unicast address.
     *
     * @return true if the received message was complete. (false if overflow flag is set).
     */
    public boolean handleReplyMessage(int function, SLPInputStream sis,
                                      ServiceLocationEnumeration results) throws ServiceLocationException {
        switch (function) {
            case SLPMessageHeader.SLPMSG_SRVRPLY:
                return recvSrvReply(new SLPSrvRplyMessage(), sis, results);
            case SLPMessageHeader.SLPMSG_SRVTYPE:
                return recvSrvTypeReply(new SLPSrvTypeRplyMessage(), sis, results);
            case SLPMessageHeader.SLPMSG_ATTRRPLY:
                return recvAttrReply(new SLPAttrRplyMessage(), sis, results);
            case SLPMessageHeader.SLPMSG_DAADV:
                recvDAAdvert(sis);
                break;
            case SLPMessageHeader.SLPMSG_SAADV:
                recvSAAdvert(sis);
                break;
            default:
                //System.out.println("UserAgent -> handleUCM: function = " + function);
                //error = ServiceLocationException.PARSE_ERROR;
                logError("handleReplyMessage: Unknown message type (" + function + ")", null);
                break;
        }
        return true;
    }

    /**
     * Sends a service request for the service:service-agent service. This is only used when the UA is configured with
     * no scope list in order to discover available scopes. The discovery is currently only done once, on startup.
     */
    private void sendSARequest() {
        // create a request for the SA service type.
        try {
            ServiceType st = new ServiceType(ServiceAgent.SA_TYPE);
            SLPSrvReqMessage msg = new SLPSrvReqMessage(st, new Vector(), "", locale);
            msg.setFlags(SLPMessageHeader.FLAG_MCAST);
            SLPMessageSender ms = new SLPMessageSender(this, CONFIG_SLP_MTU, CONFIG_RETRY, null);
            ms.sendSLPMessage(msg,
                    CONFIG_SLP_MC_ADDR, CONFIG_SLP_PORT, CONFIG_MC_MAX,
                    unicastCommunicator);
        } catch (ServiceLocationException se) {
            // ignored
            logError("sendSARequest failed", se);
        }
    }

    /**
     * Returns a vector with a list of all scopes that exists in both the requested scope list and the scopes supported
     * by the agent.
     *
     * @param scopes The scopes we want to use for the request.
     * @return A Vector containing the scopes we can use.
     */
    private Vector getSupportedScopes(Vector scopes) {
        if (userSelectableScopes) {
            // use the scopes provided by user...
            return (Vector) scopes.clone();
        }

        return SLPUtil.findCommonScopes(supportedScopes, scopes);
    }

    private void sendRequest(SLPMessageHeader request,
                             Vector scopesToUse,
                             ServiceLocationEnumeration results) throws ServiceLocationException {

        Vector receivers = findReceivers((Vector) scopesToUse.clone());
        if (!receivers.isEmpty()) {
            DAInfo da;
            Iterator iter = receivers.iterator();
            SLPMessageSender ms = new SLPMessageSender(this, CONFIG_SLP_MTU, CONFIG_RETRY, results);
            Vector additional = null;
            while (iter.hasNext() || additional != null) {
                if (!iter.hasNext()) {
                    receivers = additional;
                    iter = receivers.iterator();
                    additional = null;
                }
                da = (DAInfo) iter.next();

                if (da.getHost().equals(CONFIG_SLP_MC_ADDR)) {
                    request.setFlags(SLPMessageHeader.FLAG_MCAST);
                    ms.sendSLPMessage(request,
                            da.getHost(), da.getPort(), CONFIG_MC_MAX,
                            unicastCommunicator);
                } else {
                    // If the DA is down, we should try to find another DA supporting
                    // the wanted scopes, or in the worst case use multicast...
                    try {
                        ms.sendSLPMessage(request,
                                da.getHost(), da.getPort(), CONFIG_RETRY_MAX,
                                unicastCommunicator);
                    } catch (ServiceLocationException se) {
                        if (se.getErrorCode() == ServiceLocationException.DA_NOT_AVAILABLE) {
                            DAs.remove(da.getHost());

                            // find another DA (or DAs)...
                            additional = findReceivers(da.getScopes());
                            for (Iterator it = additional.iterator(); it.hasNext();) {
                                if (receivers.contains(it.next())) {
                                    it.remove();
                                }
                            }
                        } else {
                            // some other exception.
                            throw se;
                        }
                    }
                }
            }
        } else {
            throw new ServiceLocationException(ServiceLocationException.SCOPE_NOT_SUPPORTED,
                    "UA: Requested scope is NOT supported");
        }
    }

    public ServiceLocationEnumeration findAttributes(
            String url,
            Vector scopes,
            Vector attrIds)
            throws ServiceLocationException {
        ServiceLocationEnumeration results = new ServiceAttributeEnumeration();

        // check that the UA supports the scopes given.
        Vector scopesToUse = getSupportedScopes(scopes);
        if (scopesToUse.isEmpty()) {
            throw new ServiceLocationException(ServiceLocationException.SCOPE_NOT_SUPPORTED,
                    "UA: Requested scope is NOT supported");
        }

        // create request...
        SLPAttrReqMessage req = new SLPAttrReqMessage(url, attrIds, scopesToUse, locale);

        sendRequest(req, scopesToUse, results);

        // return the discovered service types
        return results;
    }

    /*
     protected void writeLog(String message) {
         String toWrite;
         toWrite = "------ UserAgent ------\n";
         toWrite += message + "\n";
         toWrite += "------ End ------";
         SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
     }
     */
    protected void logDebug(String message) {
        if (CONFIG_DEBUG) {
            String toWrite;
            toWrite = "------ SLP UserAgent ------\n"
                    + message + "\n"
                    + "---------------------------";
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
            toWrite = "------ SLP UserAgent ------\n"
                    + text + "\n";
            if (message != null) toWrite += message.toString() + "\n";
            toWrite += "---------------------------";

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
            toWrite = "------ SLP UserAgent ------\n"
                    + text + "\n";
            if (error != null) toWrite += error.toString();
            toWrite += "---------------------------";

            if (sflog == null) {
                SLPUtil.writeLogFile(toWrite, CONFIG_LOGFILE);
            } else {
                sflog.error("\n" + text, error);
            }
        }
	}
}

