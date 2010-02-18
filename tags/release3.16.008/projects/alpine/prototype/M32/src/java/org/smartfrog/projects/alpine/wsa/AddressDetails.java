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
package org.smartfrog.projects.alpine.wsa;

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Envelope;

import java.net.URI;
import java.net.URISyntaxException;

/*
* <wsa:To>xs:anyURI</wsa:To> ?
 * <wsa:From>wsa:EndpointReferenceType</wsa:From> ?
 * <wsa:ReplyTo>wsa:EndpointReferenceType</wsa:ReplyTo> ?
 * <wsa:FaultTo>wsa:EndpointReferenceType</wsa:FaultTo> ?
 * <wsa:Action>xs:anyURI</wsa:Action>
 * <wsa:MessageID>xs:anyURI</wsa:MessageID> ?
 * <wsa:RelatesTo RelationshipType="xs:anyURI"?>xs:anyURI</wsa:RelatesTo> *
 * <wsa:ReferenceParameters>xs:any*</wsa:ReferenceParameters> ?
*/

/**
 * This is all the extra stuff for WS-A addressing of SOAP
 * created 23-Mar-2006 13:39:59
 * <p/>
 *
 * @see <a href="http://www.w3.org/TR/2005/CR-ws-addr-core-20050817/">WS-A core </a>
 * @see <a href="http://www.w3.org/TR/2005/CR-ws-addr-soap-20050817/">WS-A SOAP binding</a>
 */

public class AddressDetails implements Validatable, AddressingConstants {

    private static final Log log = LogFactory.getLog(MessageDocument.class);

    private AlpineEPR to;

    private String action;


    private AlpineEPR from;

    private AlpineEPR replyTo;

    private AlpineEPR faultTo;

    private String messageID;

    private String relatesTo;

    private Element referenceParameters;

    /**
     * namespace of the message.
     * Default value {@link AddressingConstants.XMLNS_WSA_2005}
     */
    private String namespace = AddressingConstants.XMLNS_WSA_2005;

    /**
     * Should references be marked as coming from an address? This
     * is a requirement in later versions of WS-A, so is marked as true
     */
    private boolean markReferences = true;

    /**
     * Should address headers be marked mustUnderstand
     */
    private boolean mustUnderstand = false;

    /**
     * Create an empty address details
     */
    public AddressDetails() {
    }

    public AddressDetails(AlpineEPR epr) {
        setTo(epr);
    }

    /**
     * Parse a set of address details from a message; supply the xmlns of the address expected
     *
     * @param message   message to parse
     * @param namespace namespace of addresses
     */
    public AddressDetails(MessageDocument message, String namespace) {
        read(message, namespace);
    }

    /**
     * Deep copy constructor
     *
     * @param that
     */
    public AddressDetails(AddressDetails that) {

        action = that.action;
        messageID = that.messageID;
        relatesTo = that.relatesTo;
        namespace = that.namespace;
        markReferences = that.markReferences;
        mustUnderstand = that.mustUnderstand;

        if (that.to != null) {
            to = that.to.clone();
        }
        if (that.from != null) {
            from = that.from.clone();
        }
        if (that.replyTo != null) {
            replyTo = that.replyTo.clone();
        }
        if (that.faultTo != null) {
            faultTo = that.faultTo.clone();
        }
        if (referenceParameters != null) {
            referenceParameters = (Element) that.referenceParameters.copy();
        }


    }

    public AlpineEPR getTo() {
        return to;
    }

    public void setTo(AlpineEPR to) {
        this.to = to;
    }

    public AlpineEPR getFrom() {
        return from;
    }

    public void setFrom(AlpineEPR from) {
        this.from = from;
    }

    public AlpineEPR getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(AlpineEPR replyTo) {
        this.replyTo = replyTo;
    }

    public AlpineEPR getFaultTo() {
        return faultTo;
    }

    public void setFaultTo(AlpineEPR faultTo) {
        this.faultTo = faultTo;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getRelatesTo() {
        return relatesTo;
    }

    public void setRelatesTo(String relatesTo) {
        this.relatesTo = relatesTo;
    }

    public Element getReferenceParameters() {
        return referenceParameters;
    }

    public void setReferenceParameters(Element referenceParameters) {
        this.referenceParameters = referenceParameters;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isMarkReferences() {
        return markReferences;
    }

    public void setMarkReferences(boolean markReferences) {
        this.markReferences = markReferences;
    }

    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        this.mustUnderstand = mustUnderstand;
    }

    /**
     * validate an instance.
     * Return if the object is valid, thrown an exception if not.
     * It is imperative that this call has <i>No side effects</i>.
     * <p/>
     * Why is this a boolean? For insertion into assert ; statements.
     *
     * @return true unless an exception is thrown
     * @throws org.smartfrog.projects.alpine.faults.ValidationException
     *          with text if not valid
     */
    public boolean validate() throws ValidationException {
        if (getAction() == null || getAction().length() == 0) {
            throw new ValidationException("Missing or empty " + WSA_ACTION + " attribute");
        }
        if (to != null) {
            to.validate();
        }
        if (from != null) {
            from.validate();
        }
        if (replyTo != null) {
            replyTo.validate();
        }
        if (faultTo != null) {
            faultTo.validate();
        }

        return true;
    }

    /**
     * validate everything, including that the To: address is there
     *
     * @throws ValidationException
     */
    public void checkToIsValid() throws ValidationException {
        validate();
        if (to == null) {
            throw new ValidationException("Missing " + WSA_TO + " attribute");
        }
    }

    /**
     * read everything from the document. After this is done, the extracted elements are
     * copied; they are not live. Changes in the values are not reflected in the message contents
     *
     * @param message message to read
     * @param namespace namespace to expect
     * @return true if a wsa:To element in that xmlns was found.
     */
    public boolean read(MessageDocument message, String namespace) {
        this.namespace = namespace;
        boolean found = false;
        Header headers = message.getEnvelope().getHeader();
        String soapNS = headers.getNamespaceURI();
        AlpineEPR epr = new AlpineEPR();
        found = epr.readFromHeaders(headers, namespace, false);
        if (found) {
            to = epr;
        }
        for (Element header : headers.elements(namespace)) {
            String localname = header.getLocalName();
            String text = header.getValue();
            boolean understood=false;
            if (WSA_MESSAGEID.equals(localname)) {
                checkNotEmpty(header, text);
                checkValidURI(WSA_MESSAGEID, text);
                messageID = text;
                understood=true;
            } else if (WSA_ACTION.equals(localname)) {
                checkNotEmpty(header, text);
                action = text;
                checkValidURI(WSA_ACTION, text);
                understood = true;
            } else if (WSA_FROM.equals(localname)) {
                from = new AlpineEPR(header, namespace);
                understood = true;
            } else if (WSA_REPLYTO.equals(localname)) {
                replyTo = new AlpineEPR(header, namespace);
                understood = true;
            } else if (WSA_FAULTTO.equals(localname)) {
                faultTo = new AlpineEPR(header, namespace);
                understood = true;
            } else if (WSA_REFERENCE_PARAMETERS.equals(localname)) {
                referenceParameters = (Element) header.copy();
                understood = true;
            } else if (WSA_RELATES_TO.equals(localname)) {
                checkNotEmpty(header, text);
                relatesTo = text;
                understood = true;
            } else if (WSA_TO.equals(localname)) {
                //this is nt grabbed because it was pulled earlier.
                understood = true;
            }
            //mark headers that we understood as so, for the MustUnderstand checker.
            if(Header.isMustUnderstand(header, soapNS) && understood) {
                Header.setMustUnderstand(header, soapNS, false);
            }
        }
        return found;
    }

    private void checkValidURI(String headerName, String text) {
        try {
            new URI(text);
        } catch (URISyntaxException e) {
            throw new AlpineRuntimeException("Invalid wsa:"+headerName +" header -it must be a URI :"+text);
        }
    }

    private void checkNotEmpty(Element header, String text) {
        if(text==null || text.length()==0) {
            throw new AlpineRuntimeException("Missing content from header "+header.getQualifiedName());
        }
    }

    /**
     * Add the address to a SOAP message as the To: element. This will also replace any existing headers of the same name
     * This triggers a call to {@link #validate()} to validate the address
     *
     * @param message message to add to
     */
    public void addressMessage(MessageDocument message) {
        addressMessage(message, namespace, "wsa", markReferences, mustUnderstand);
    }

    /**
     * Add the address to a SOAP message as the To: element. This will also replace any existing headers of the same name
     * This triggers a call to {@link #validate()} to validate the address
     *
     * @param message        message to add to
     * @param wsaNamespace      which xmlns to use
     * @param prefix         prefix for elements
     * @param markWsaReferences whether to mark references or not as references (the later specs require this)
     * @param markMustUnderstand should the address + actions headers be mustUnderstand=true?
     */
    public void addressMessage(MessageDocument message,
                               String wsaNamespace,
                               String prefix,
                               boolean markWsaReferences,
                               boolean markMustUnderstand) {
        validate();
        AlpineEPR dest = to;
        if (to == null) {
            dest = AlpineEPR.EPR_ANONYMOUS;
        }
        //patch in WSA prefix to the message header.
        Envelope envelope = message.getEnvelope();
        envelope.getHeader().addNewNamespace(prefix,wsaNamespace);
        //and the soap prefix
        envelope.addSoapPrefix();
        maybeAdd(message, dest, WSA_TO, wsaNamespace, prefix, markWsaReferences, markMustUnderstand);
        maybeAdd(message, from, WSA_FROM, wsaNamespace, prefix, markWsaReferences, markMustUnderstand);
        maybeAdd(message, replyTo, WSA_REPLYTO, wsaNamespace, prefix, markWsaReferences, markMustUnderstand);
        maybeAdd(message, faultTo, WSA_FAULTTO, wsaNamespace, prefix, markWsaReferences, markMustUnderstand);
        //text values iain
        maybeAdd(message, messageID, WSA_MESSAGEID, wsaNamespace, prefix, markMustUnderstand);
        maybeAdd(message, action, WSA_ACTION, wsaNamespace, prefix, markMustUnderstand);
        maybeAdd(message, relatesTo, WSA_RELATES_TO, wsaNamespace, prefix, markMustUnderstand);
    }

    /**
     * Add a value if it is not null
     *
     * @param message
     * @param value
     * @param localname
     * @param namespace
     * @param prefix
     * @param mustUnderstand
     */
    private void maybeAdd(MessageDocument message, String value, String localname, String namespace,
                          String prefix,
                          boolean mustUnderstand) {
        if (value != null) {
            final String prefixColon = prefix + ":";
            Header header = message.getEnvelope().getHeader();
            Element actionElement = new SoapElement(prefixColon + localname, namespace, value);
            header.setHeaderElement(actionElement, mustUnderstand);
        }
    }

    /**
     * add an address if it is not null
     *
     * @param message
     * @param dest
     * @param localname
     * @param namespace
     * @param prefix
     * @param markReferences
     * @param mustUnderstand
     */
    private void maybeAdd(MessageDocument message, AlpineEPR dest, String localname, String namespace, String prefix,
                          boolean markReferences, boolean mustUnderstand) {
        if (dest != null) {
            dest.addressMessage(message, localname, namespace, prefix, markReferences, mustUnderstand);
        }
    }


    /**
     * Get the destination address
     *
     * @return the destination
     */
    public String getDestination() {
        if (to == null) {
            return null;
        } else {
            return to.getAddress();
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        if (to == null) {
            return "Address to unknown destination; action=" + action;
        } else {
            return "AddressDetails to " + to + " action=" + action;
        }
    }

    /**
     * Equality test test everything. It is not clever enough to know that if reference parameters
     * are in a different order they are probably equivalent; it is not a full XML comparison
     *
     * @param o object to compare
     * @return true iff the two objects are equal
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AddressDetails that = (AddressDetails) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (faultTo != null ? !faultTo.equals(that.faultTo) : that.faultTo != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (messageID != null ? !messageID.equals(that.messageID) : that.messageID != null) return false;
        if (referenceParameters != null ? !referenceParameters.equals(that.referenceParameters)
                : that.referenceParameters != null) return false;
        if (relatesTo != null ? !relatesTo.equals(that.relatesTo) : that.relatesTo != null) return false;
        if (replyTo != null ? !replyTo.equals(that.replyTo) : that.replyTo != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (to != null ? to.hashCode() : 0);
        result = 29 * result + (action != null ? action.hashCode() : 0);
        result = 29 * result + (from != null ? from.hashCode() : 0);
        result = 29 * result + (replyTo != null ? replyTo.hashCode() : 0);
        result = 29 * result + (faultTo != null ? faultTo.hashCode() : 0);
        result = 29 * result + (messageID != null ? messageID.hashCode() : 0);
        result = 29 * result + (relatesTo != null ? relatesTo.hashCode() : 0);
        result = 29 * result + (referenceParameters != null ? referenceParameters.hashCode() : 0);
        return result;
    }

    /**
     * Clone operation does a deep copy.
     *
     * @return a cloned object
     */
    public Object clone() {
        return new AddressDetails(this);
    }
}
