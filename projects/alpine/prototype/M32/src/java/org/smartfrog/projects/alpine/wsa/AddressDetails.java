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
import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.om.base.ElementEx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @see <a href="http://www.w3.org/TR/2005/CR-ws-addr-core-20050817/">WS-A core </a>
 *  @see  <a href="http://www.w3.org/TR/2005/CR-ws-addr-soap-20050817/">WS-A SOAP binding</a>
 */

public class AddressDetails implements Validatable, AddressingConstants {

    private static Log log = LogFactory.getLog(MessageDocument.class);

    private AlpineEPR to;

    private String action;


    private AlpineEPR from;

    private AlpineEPR replyTo;

    private AlpineEPR faultTo;

    private String messageID;

    private String relatesTo;

    private Element referenceParameters;

    public AddressDetails() {
    }

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
        if(to!=null) {
            to.validate();
        }
        if(from!=null) {
            from.validate();
        }
        if(replyTo!=null) {
            replyTo.validate();
        }
        if(faultTo!=null) {
            faultTo.validate();
        }

        return true;
    }

    /**
     * validate everything, including that the To: address is there
     * @return
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
        * @param message
        */
    public void read(MessageDocument message, String namespace) {
        for (Element header : message.getEnvelope().getHeaders()) {
            if (!namespace.equals(header.getNamespaceURI())) {
                continue;
            }
            String localname = header.getLocalName();
            String text = header.getValue();
            if (WSA_TO.equals(localname)) {
                to = new AlpineEPR(header, namespace);
                //extract To:
            } else if (WSA_MESSAGEID.equals(localname)) {
                messageID = text;
            } else if (WSA_ACTION.equals(localname)) {
                action = text;
            } else if (WSA_FROM.equals(localname)) {
                from = new AlpineEPR(header, namespace);
            } else if (WSA_REPLYTO.equals(localname)) {
                replyTo = new AlpineEPR(header, namespace);
            } else if (WSA_FAULTTO.equals(localname)) {
                faultTo = new AlpineEPR(header, namespace);
            } else if (WSA_REFERENCE_PARAMETERS.equals(localname)){
                referenceParameters=(Element) header.copy();
            } else if (WSA_RELATES_TO.equals(localname)) {
                //TODO: RelatesTo, as and when needed
                log.warn("Not yet implemented "+header);
            }
        }
    }

    /**
     * Add the address to a SOAP message as the To: element. This will also replace any existing headers of the same name
     * This triggers a call to {@link #validate()} to validate the address
     *
     * @param message        message to add to
     * @param namespace      which xmlns to use
     * @param prefix         prefix for elements
     * @param markReferences whether to mark references or not as references (the later specs require this)
     * @param mustUnderstand should the address + actions headers be mustUnderstand=true?
     */
    public void addressMessage(MessageDocument message,
                               String namespace,
                               String prefix,
                               boolean markReferences,
                               boolean mustUnderstand) {
        validate();
        AlpineEPR dest = to;
        if(to==null) {
            dest=AlpineEPR.EPR_ANONYMOUS;
        }
        maybeAdd(message, dest, WSA_TO, namespace, prefix, markReferences, mustUnderstand);
        maybeAdd(message, from, WSA_FROM, namespace, prefix, markReferences, mustUnderstand);
        maybeAdd(message, replyTo, WSA_REPLYTO, namespace, prefix, markReferences, mustUnderstand);
        maybeAdd(message, faultTo, WSA_FAULTTO, namespace, prefix, markReferences, mustUnderstand);
        final String prefixColon = prefix + ":";
        Header header = message.getEnvelope().getHeader();
        Element actionElement = new ElementEx(prefixColon + WSA_ACTION, namespace,action);
        header.setHeaderElement(actionElement,mustUnderstand);
    }

    private void maybeAdd(MessageDocument message, AlpineEPR dest, String role, String namespace, String prefix,
                          boolean markReferences, boolean mustUnderstand) {
        if(dest!=null) {
            dest.addressMessage(message,role,namespace,prefix,markReferences,mustUnderstand);
        }
    }

    /**
     * Get the destination address
     * @return
     */
    public String getDestination() {
        if(to==null) {
            return null;
        } else {
            return to.getAddress();
        }
    }
}
