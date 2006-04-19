/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

 For more information: www.smartfrog.org

 */
package org.smartfrog.projects.alpine.transport;

import nu.xom.Element;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;

/**
 * This represents an ongoing conversation with a single host/endpoint. Stuff like
 * auth data can go in there, and it has a default address
 */
public class Session {

    private AlpineEPR endpoint;
    private String role = DEFAULT_ROLE;
    private boolean validating = true;
    private AddressDetails address;

    /**
     * {@value}
     */
    public static final String DEFAULT_ROLE = "Client";
    private static Log log = LogFactory.getLog(Session.class);
    /**
     * a queue for transmissions
     */
    private TransmitQueue queue;

    private Credentials authenticationCredentials;
    private Credentials proxyCredentials;

    /**
     * Create a new session.
     *
     * @param endpoint   default address
     * @param role       role for this node. Defaults to {@link #DEFAULT_ROLE}
     * @param validating flag to set validating parser
     */
    public Session(AlpineEPR endpoint, String role, boolean validating) {
        bind(endpoint);
        if (role != null) {
            this.role = role;
        }
        this.validating = validating;
    }

    public AlpineEPR getEndpoint() {
        return endpoint;
    }


    public AddressDetails getAddress() {
        return address;
    }

    /**
     * Bind to an address; creates an AddresDetails instance that can be tweaked.
     *
     * @param endpoint
     */
    public void bind(AlpineEPR endpoint) {
        this.endpoint = endpoint;
        address = new AddressDetails(endpoint);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isValidating() {
        return validating;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public TransmitQueue getQueue() {
        return queue;
    }

    public void setQueue(TransmitQueue queue) {
        this.queue = queue;
    }


    /**
     * Create an outbound transmission
     *
     * @param action soap action
     * @return a tx bound to the default destination
     */
    public Transmission createTransmission(String action) {
        return createTransmission(address, action);
    }

    /**
     * Create an outbound transmission
     *
     * @param destination destination address
     * @param action      soap action
     * @return a tx bound to the default destination
     */
    public Transmission createTransmission(AlpineEPR destination,
                                           String action) {
        MessageContext messageContext = new MessageContext(role, validating);
        Transmission tx = new Transmission(messageContext);
        MessageDocument request = messageContext.createRequest();
        AddressDetails addressing = new AddressDetails();
        addressing.setTo(destination);
        addressing.setAction(action);
        request.setAddressDetails(addressing);
        return tx;
    }

    public Transmission createTransmission(AddressDetails destination,
                                           String action) {
        AddressDetails addressing = new AddressDetails(destination);
        addressing.setAction(action);
        return createTransmission(addressing);
    }

    public Transmission createTransmission(AddressDetails destination) {
        MessageContext messageContext = new MessageContext(role, validating);
        Transmission tx = new Transmission(messageContext);
        MessageDocument request = messageContext.createRequest();
        request.setAddressDetails(destination);
        return tx;
    }

    /**
     * Create an outbound transmission
     *
     * @param action, set to null to use the element local name
     * @param payload the body of the soap message
     * @return a tx bound to the default destination
     */
    public Transmission createTransmission(String action, Element payload) {
        AlpineEPR destination = endpoint;
        if (action == null) {
            action = payload.getLocalName();
        }
        Transmission tx = createTransmission(destination, action);
        tx.getRequest().getBody().appendChild(payload);
        return tx;
    }

    /**
     * Utility method to wrap up most of everything into one single operation. Create a message
     * addressed to the default destination, with the given payload.
     * <p/>
     * This only works if the queue has been set via {@link #setQueue(TransmitQueue)},
     * otherwise you get a null pointer exception.
     *
     * @param action  soap action; leave null for it to be taken from the payload
     * @param payload the contents of the SOAP Envelope
     * @return the transmission, which can be waited on
     */
    public Transmission queue(String action, Element payload) {
        Transmission tx = createTransmission(action, payload);
        queue.transmit(tx);
        return tx;
    }

    /**
     * Utility method to wrap up most of everything into one single operation. Create a message
     * addressed to the default destination, with the given payload.
     * <p/>
     * This only works if the queue has been set via {@link #setQueue(TransmitQueue)},
     * otherwise you get a null pointer exception.
     *
     * @param payload the contents of the SOAP Envelope
     * @return the transmission, which can be waited on
     */
    public Transmission queue(Element payload) {
        return queue(null, payload);
    }

}
